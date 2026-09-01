package kernel_scene;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import kernel_part.part;
import kernel_camera.camera;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_render.render_container;
import kernel_file_manager.file_reader;
import kernel_driver.modifier_container;
import kernel_part.part_loader_container;
import kernel_file_manager.file_directory;
import kernel_interface.client_process_bar;
import kernel_component.component_container;
import kernel_common_class.nanosecond_timer;
import kernel_part.permanent_part_id_encoder;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_camera.camera_container_creator;
import kernel_component.component_collector_stack;
import kernel_part.part_container_for_part_search;
import kernel_common_class.tree_string_locker_container;
import kernel_component.component_load_source_container;
import kernel_create_top_assemble_part.create_assemble_part;
import kernel_part.buffer_object_file_modify_time_and_length_container;

public class scene_kernel
{
	public String 							scene_name,link_name;
	
	public scene_kernel_create_parameter 	create_parameter;

	public system_parameter		 			system_par;
	public scene_parameter					scene_par;
	
	public render_container 				render_cont;
	public part_container_for_part_search	part_search_cont;
	public component_container			 	component_cont;
	public ArrayList<camera> 				camera_cont;
	public component_collector_stack		collector_stack;
	
	public part_process_sequence			process_part_sequence;
	
	public nanosecond_timer					current_time;
	
	public modifier_container				modifier_cont[];
	
	public part_lru_manager					part_lru;

	public part_loader_container 			part_loader_cont;

	public void destroy()
	{
		if(part_lru!=null) {
			part_lru.destroy();
			part_lru=null;
		}
		if(modifier_cont!=null) {
			for(int i=0,ni=modifier_cont.length;i<ni;i++)
				if(modifier_cont[i]!=null){
					modifier_cont[i].destroy();
					modifier_cont[i]=null;
				}
			modifier_cont=null;
		}
		if(component_cont!=null){
			component_cont.destroy();
			component_cont=null;
		}
		if(render_cont!=null) {
			render_cont.destroy();
			render_cont=null;
		}
		if(collector_stack!=null) {
			collector_stack.destroy();
			collector_stack=null;
		}
		if(camera_cont!=null) {
			for(int i=0,ni=camera_cont.size();i<ni;i++)
				camera_cont.get(i).destroy();
			camera_cont.clear();
			camera_cont=null;
		}
		if(part_search_cont!=null) {
			part_search_cont.destroy();
			part_search_cont=null;
		}
		if(process_part_sequence!=null) {
			process_part_sequence.destroy();
			process_part_sequence=null;
		}
		
		scene_name		=null;
		link_name		=null;
		create_parameter=null;
		system_par		=null;
		scene_par		=null;
		current_time	=null;

		part_loader_cont=null;
	}
	public scene_kernel(
		String my_scene_name,String my_link_name,scene_kernel_create_parameter my_create_parameter,
		client_request_response request_response,system_parameter my_system_parameter,
		render_container my_original_render,part_loader_container my_part_loader_cont)
	{
		scene_name				=my_scene_name;
		link_name				=my_link_name;
		
		create_parameter		=my_create_parameter;
		system_par				=my_system_parameter;
		scene_par				=new scene_parameter(my_scene_name,
										request_response,system_par,create_parameter);
		
		component_cont			=null;
		camera_cont				=null;
		collector_stack			=null;

		process_part_sequence	=null;
		
		current_time			=new nanosecond_timer();

		modifier_cont			=new modifier_container[scene_par.max_modifier_container_number];
		for(int i=0,ni=scene_par.max_modifier_container_number;i<ni;i++)
			modifier_cont[i]=new modifier_container(current_time.nanoseconds());
		
		part_lru				=null;	

		part_loader_cont		=my_part_loader_cont;
		
		caculate_component_flag	=false;
		
		render_cont				=my_original_render;
		part_search_cont		=null;	
	}
	private void load_camera()
	{
		String camera_file_name;
		
		if(!(new File(camera_file_name=scene_par.directory_name+scene_par.camera_file_name).exists()))
			if(!(new File(camera_file_name=scene_par.extra_directory_name+scene_par.camera_file_name).exists()))
				camera_file_name=system_par.parameter_directory
						+"camera_parameter"+File.separatorChar+scene_par.camera_file_name;

		file_reader f_camera=new file_reader(camera_file_name,scene_par.parameter_charset);
		if(f_camera.error_flag()){
			camera_cont=null;
			debug_information.println("Opening camera file fail:"
						+scene_par.directory_name+scene_par.camera_file_name);
			debug_information.println("Opening camera file fail:"+camera_file_name);	
		}else
			camera_cont=camera_container_creator.load_camera_container(
					f_camera,component_cont,scene_par.max_camera_return_stack_number);
		f_camera.close();
	}
	private void mount_top_box_part(
			component comp,component_load_source_container scene_component_load_source_cont,
			part_container_for_part_search part_search,client_request_response request_response)
	{
		int child_number;
		if((child_number=comp.children.size())<=0)
			return;
		if(comp.driver_array.size()>0)
			return;
		do{
			ArrayList<part> my_part_list;
			if((my_part_list=part_search.search_value_list(comp.part_name))==null)
				break;
			if(my_part_list.size()<=0)
				break;
			part my_part=my_part_list.get(0);
			try{
				component_driver cd=my_part.driver.create_component_driver(null,false,my_part,
					scene_component_load_source_cont,this,request_response);
				if(cd==null)
					break;
				comp.driver_array=new ArrayList<component_driver>();
				comp.driver_array.add(cd);
				return;
			}catch(Exception e){
				e.printStackTrace();
				debug_information.println(
						"create_component_driver fail in mount_top_box_part():	",e.toString());
				debug_information.println("Part user name:",	my_part.user_name);
				debug_information.println("Part system name:",	my_part.system_name);
				debug_information.println("Mesh_file_name:",	my_part.directory_name+my_part.mesh_file_name);
				debug_information.println("Material_file_name:",my_part.directory_name+my_part.material_file_name);
			}
		}while(false);
		
		for(int i=0;i<child_number;i++)
			mount_top_box_part(comp.children.get(i),
				scene_component_load_source_cont,part_search,request_response);
	}
	public long caculate_scene_last_modified_time()
	{
		long last_modified_time=0;
		
		if(last_modified_time<system_par.last_modified_time)
			last_modified_time=system_par.last_modified_time;
		if(last_modified_time<scene_par.parameter_last_modified_time)
			last_modified_time=scene_par.parameter_last_modified_time;
		if(last_modified_time<scene_par.scene_last_modified_time)
			last_modified_time=scene_par.scene_last_modified_time;
		if(last_modified_time<component_cont.root_component.uniparameter.file_last_modified_time)
			last_modified_time=component_cont.root_component.uniparameter.file_last_modified_time;
		
		return last_modified_time;
	}
	private void load_create_assemble_part(
			String fast_load_type,client_request_response request_response,
			component_load_source_container scene_component_load_source_cont,
			part_container_for_part_search all_part_part_cont,permanent_part_id_encoder part_id_encoder,
			ArrayList<buffer_object_file_modify_time_and_length_container> boftal_container,
			tree_string_locker_container string_locker_container)
	{		
		if(create_parameter.create_top_part_expand_ratio<1.0)
			return;
		if(create_parameter.create_top_part_left_ratio<1.0)
			return;
		if(component_cont.root_component==null)
			return;

		ArrayList<part>top_box_part_list=new create_assemble_part(
			fast_load_type,component_cont,render_cont,request_response,part_id_encoder,
			part_loader_cont,all_part_part_cont,boftal_container,
			caculate_scene_last_modified_time(),string_locker_container,
			create_parameter,system_par,scene_par).top_box_part;
		
		if(top_box_part_list==null)
			return;
		if(top_box_part_list.size()<=0)
			return;
		mount_top_box_part(component_cont.root_component,scene_component_load_source_cont,
				new part_container_for_part_search(top_box_part_list),request_response);
		return;
	}
	private void add_boftal_container(String fast_load_type,scene_load_call_parameter load_par)
	{
		switch(fast_load_type){
		case "fast":
			break;
		default:
			return;
		}

		var bofmtlc=new buffer_object_file_modify_time_and_length_container();
		
		String package_directory_name,boftal_data_file_name,my_lock_key;
		
		package_directory_name	=file_directory.package_file_directory(1,system_par,scene_par);
		boftal_data_file_name 	=package_directory_name+"boftal_data.txt";
		my_lock_key				=package_directory_name+"package.lock";

		load_par.string_locker_cont.read_lock(my_lock_key);
		
		File f;
		if((f=new File(boftal_data_file_name)).exists())
			if(f.length()>0)
				bofmtlc.load(load_par.process_bar,"load_scene_buffer_object_file_information",
						boftal_data_file_name,system_par.local_data_charset);
		load_par.string_locker_cont.read_unlock(my_lock_key);
		
		for(int i=0,ni=scene_par.type_sub_directory.length;i<ni;i++) {
			package_directory_name	=file_directory.package_file_directory(i+2,system_par,scene_par);
			boftal_data_file_name 	=package_directory_name+"boftal_data.txt";
			my_lock_key				=package_directory_name+"package.lock";
			
			load_par.string_locker_cont.read_lock(my_lock_key);
			if((f=new File(boftal_data_file_name)).exists())
				if(f.length()>0)
					bofmtlc.load(load_par.process_bar,"load_type_buffer_object_file_information",
							boftal_data_file_name,system_par.local_data_charset);
			load_par.string_locker_cont.read_unlock(my_lock_key);
		}
		
		var old_boftal_container=load_par.boftal_cont;
		load_par.boftal_cont=new ArrayList<buffer_object_file_modify_time_and_length_container>();
		for(var my_boftal:old_boftal_container)
			load_par.boftal_cont.add(my_boftal);
		if(bofmtlc.size()>0)
			load_par.boftal_cont.add(bofmtlc);
	}
	private void scene_kernel_load_part(String fast_load_type,long part_type_code,
			permanent_part_id_encoder part_id_encoder,scene_load_call_parameter load_par)
	{
		long start_time=new Date().getTime(),current_time;

		render_cont	=new render_container(render_cont,load_par.request_response,system_par,scene_par);
		part_search_cont=new part_container_for_part_search(render_cont.part_array_list(-1));
		
		for(int i=0,ni=scene_par.type_sub_directory.length;i<ni;i++) {
			String path_name=scene_par.type_shader_directory_name
				+scene_par.type_sub_directory[i]+scene_par.type_shader_file_name;
			render_cont.load_shader(load_par.scene_component_load_source_cont,
				part_search_cont,scene_par.parameter_last_modified_time,path_name,
				scene_par.parameter_charset,i+2,system_par,scene_par,
				part_id_encoder,load_par.request_response);
		}
		{
			String path_name=scene_par.scene_shader_directory_name
					+scene_par.scene_shader_file_name;
			render_cont.load_shader(load_par.scene_component_load_source_cont,part_search_cont,
				scene_par.scene_last_modified_time,path_name,create_parameter.scene_charset,
				1,system_par,scene_par,part_id_encoder,load_par.request_response);
		}
		debug_information.println("Load shaders time length:	",
				(current_time=new Date().getTime())-start_time);
		debug_information.println();
		
		
		add_boftal_container(fast_load_type,load_par);

		start_time=current_time;
		render_cont.load_part(part_type_code,1,part_loader_cont,system_par,scene_par,load_par.boftal_cont,
			load_par.string_locker_cont,load_par.process_bar,"load_first_class_part",fast_load_type);
		debug_information.println("Load first class part time length:	",
				(current_time=new Date().getTime())-start_time);
		debug_information.println();

		start_time=current_time;
		render_cont.create_bottom_box_part(part_search_cont,
				load_par.request_response,part_id_encoder,system_par,scene_par);
		
		render_cont.load_part(part_type_code,2,part_loader_cont,system_par,scene_par,load_par.boftal_cont,
				load_par.string_locker_cont,load_par.process_bar,"load_second_class_part",fast_load_type);
		debug_information.println("Load second class part time length:	",
				(current_time=new Date().getTime())-start_time);
		debug_information.println();

		start_time=current_time;
		render_cont.type_part_package=new part_package[scene_par.type_sub_directory.length];
		for(int i=0,ni=render_cont.type_part_package.length;i<ni;i++)
			render_cont.type_part_package[i]=new part_package(
					fast_load_type,load_par.process_bar,load_par.string_locker_cont,
					"create_first_class_package","create_first_boftal_file",
					render_cont,i+2,system_par,scene_par);
		
		debug_information.println();
		debug_information.println("Create first part package time length:	",
				(current_time=new Date().getTime())-start_time);
		debug_information.println();
	}
	private boolean scene_kernel_load_component(scene_load_call_parameter load_par)
	{
		file_reader scene_f=new file_reader(
				create_parameter.scene_directory_name+create_parameter.scene_file_name,
				create_parameter.scene_charset);
		if(scene_f.error_flag()){
			debug_information.println("Open scene file fail	:	",
				create_parameter.scene_directory_name+create_parameter.scene_file_name);
			return true;
		}

		create_parameter.scene_directory_name		=scene_f.directory_name;
		create_parameter.scene_file_name			=scene_f.file_name;
		if(scene_par.scene_last_modified_time<scene_f.lastModified_time)
			scene_par.scene_last_modified_time		=scene_f.lastModified_time;
		if(scene_par.scene_shader_directory_name==null)
			scene_par.scene_shader_directory_name	=scene_f.directory_name;
		
		long start_time=new Date().getTime();
		
		load_par.process_bar.set_process_bar(true,"load_component", "",1, 2);
		component_cont=new component_container(scene_f,this,
				load_par.scene_component_load_source_cont,load_par.request_response);
		
		scene_f.close();
		load_par.process_bar.set_process_bar(false,"load_component","", 2, 2);
		debug_information.println("Load components time length:	",new Date().getTime()-start_time);
		debug_information.println();
		
		component_cont.do_component_caculator(false,load_par.process_bar,"first_do_component_caculator");
		component_cont.root_component.recurse_caculate_component_flag(component_cont,null);
		
		return false;
	}
	private void scene_kernel_create_component_assemble(String fast_load_type,
			permanent_part_id_encoder part_id_encoder,long part_type_code,scene_load_call_parameter load_par)
	{
		long start_time=new Date().getTime(),current_time;
		load_create_assemble_part(fast_load_type,
				load_par.request_response,load_par.scene_component_load_source_cont,
				part_search_cont,part_id_encoder,load_par.boftal_cont,load_par.string_locker_cont);	
	
		render_cont.load_part(part_type_code,4,part_loader_cont,system_par,scene_par,load_par.boftal_cont,
				load_par.string_locker_cont,load_par.process_bar,"load_third_class_part",fast_load_type);

		debug_information.println("Create top assemble time length:	",
				(current_time=new Date().getTime())-start_time);

		start_time=current_time;
		
		render_cont.scene_part_package=new part_package(fast_load_type,
			load_par.process_bar,load_par.string_locker_cont,
			"create_second_class_package","create_second_boftal_file",
			render_cont,1,system_par,scene_par);
		
		debug_information.println();
		debug_information.println("Create second part package time length:	",
				(current_time=new Date().getTime())-start_time);
		debug_information.println();
	}
	private String get_fast_load_type(
			client_request_response request_response,
			client_process_bar process_bar)
	{
		String fast_load_type=request_response.get_fast_load_type();
		switch(fast_load_type){
		case "clear":
			process_bar_delete_file.do_delete(
				scene_par.scene_temporary_directory_name,process_bar);
			break;
		default:
			break;
		}
		return fast_load_type;
	}
	private long create_part_type_code()
	{
		long part_type_code=0;
		for(int i=0,ni=scene_par.type_sub_directory.length;i<=ni;i++)
			part_type_code|=((long)1)<<(1+i);
		return part_type_code;
	}
	private void scene_kernel_load_last_process(
			client_request_response request_response,client_process_bar process_bar)
	{
		component_cont.original_part_number=new compress_render_container(
				render_cont,part_search_cont,component_cont.root_component).original_part_number;
			
		part_search_cont.destroy();
		part_search_cont=new part_container_for_part_search(render_cont.part_array_list(-1));
		part_search_cont.reset_assembly_precision();

		component_cont.do_component_caculator(true,process_bar,"second_do_component_caculator");
		component_cont.scene_component=component_cont.search_component(scene_par.scene_component_name);
			
		process_part_sequence=new part_process_sequence(render_cont,
				system_par.box_distance_difference_scale,system_par.buffer_data_length_difference_scale);

		collector_stack=new component_collector_stack(
			component_cont,system_par,scene_par,render_cont.renders);
			
		load_camera();

		long start_time=new Date().getTime();
		new scene_initialization(this,request_response,process_bar);
		debug_information.println("Create scene temp data time length:	",new Date().getTime()-start_time);
		debug_information.println();
			
		part_lru=new part_lru_manager(render_cont.renders,scene_par.part_lru_in_list_number);
		
		process_bar.set_process_bar(true,"load_termination","",1,1);
		
		return;
	}
	public boolean load(scene_load_call_parameter load_par)
	{
		debug_information.println();
		debug_information.println("scene_par.directory_name                 :	",	scene_par.directory_name);
		debug_information.println("scene_par.scene_temporary_directory_name :	",	scene_par.scene_temporary_directory_name);
		debug_information.println("scene_par.camera_file_name               :	",	scene_par.camera_file_name);
		debug_information.println("scene_par.change_part_string             :	",	scene_par.change_part_string);
		debug_information.println("scene_par.part_type_string               :	",	scene_par.part_type_string);
		
		debug_information.println("scene_par.type_shader_directory_name     :	",	scene_par.type_shader_directory_name);
		debug_information.println("scene_par.type_shader_file_name          :	",	scene_par.type_shader_file_name);
		debug_information.println("scene_par.scene_shader_directory_name    :	",	scene_par.scene_shader_directory_name);	
		debug_information.println("scene_par.scene_shader_file_name         :	",	scene_par.scene_shader_file_name);	
		
		try{
			long part_type_code=create_part_type_code();
			String fast_load_type=get_fast_load_type(load_par.request_response,load_par.process_bar);		
			permanent_part_id_encoder part_id_encoder=new permanent_part_id_encoder();
			
			scene_kernel_load_part(fast_load_type,part_type_code,part_id_encoder,load_par);
			
			if(scene_kernel_load_component(load_par))
				return true;
			
			scene_kernel_create_component_assemble(fast_load_type,part_id_encoder,part_type_code,load_par);
			
			scene_kernel_load_last_process(load_par.request_response,load_par.process_bar);

			return false;
		}catch(Exception e){
			e.printStackTrace();
			debug_information.println("Scene load exception:	",e.toString());
			return true;
		}
	}
	
	private boolean caculate_component_flag;
	public void mark_caculate_component_flag()
	{
		caculate_component_flag=true;
	}
	public void caculate_scene_component_flag()
	{
		if(caculate_component_flag){
			caculate_component_flag=false;
			component_cont.root_component.recurse_caculate_component_flag(component_cont,null);
		}
	}
	public String get_scene_environment_parameter(String parameter_name)
	{
		return scene_par.scene_environment.search_change_name(parameter_name,null);
	}
}
