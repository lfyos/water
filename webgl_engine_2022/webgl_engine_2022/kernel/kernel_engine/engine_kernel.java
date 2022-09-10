package kernel_engine;

import java.io.File;
import java.util.Date;

import kernel_camera.camera_container;
import kernel_common_class.change_name;
import kernel_common_class.nanosecond_timer;
import kernel_component.component;
import kernel_component.component_collector_stack;
import kernel_component.component_container;
import kernel_component.component_load_source_container;
import kernel_create_top_assemble_part.create_assemble_part;
import kernel_driver.modifier_container;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_render.render_container;
import kernel_part.buffer_object_file_modify_time_and_length_container;
import kernel_part.part;
import kernel_part.part_container_for_part_search;
import kernel_part.part_container;
import kernel_part.part_container_for_delete_part_file;
import kernel_driver.component_driver;
import kernel_part.part_loader_container;
import kernel_common_class.debug_information;
import kernel_common_class.exclusive_file_mutex;
import kernel_interface.client_process_bar;

public class engine_kernel
{
	public engine_kernel_create_parameter 	create_parameter;

	public system_parameter		 			system_par;
	public scene_parameter					scene_par;
	
	public render_container 				render_cont;
	public part_container_for_part_search	part_cont;
	public component_container			 	component_cont;
	public camera_container 				camera_cont;
	public component_collector_stack		collector_stack;
	
	public part_process_sequence			process_part_sequence;
	
	public nanosecond_timer					current_time;
	
	public modifier_container				modifier_cont[];
	
	public part_lru_manager					part_lru;

	public part_loader_container 			part_loader_cont;
	
	public long 							do_selection_version,program_last_time;

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
			camera_cont.destroy();
			camera_cont=null;
		}
		if(part_cont!=null) {
			part_cont.destroy();
			part_cont=null;
		}
		if(process_part_sequence!=null) {
			process_part_sequence.destroy();
			process_part_sequence=null;
		}
		
		create_parameter=null;
		system_par=null;
		scene_par=null;
		current_time=null;
		
		part_loader_cont=null;
	}
	public engine_kernel(engine_kernel_create_parameter my_create_parameter,
			client_request_response request_response,system_parameter my_system_parameter,
			render_container my_original_render,part_loader_container my_part_loader_cont)
	{
		create_parameter		=my_create_parameter;
		system_par				=my_system_parameter;
		scene_par				=new scene_parameter(request_response,system_par,create_parameter);
		
		component_cont			=null;
		camera_cont				=null;
		collector_stack			=null;

		process_part_sequence	=null;
		
		current_time			=new nanosecond_timer();

		modifier_cont			=new modifier_container[scene_par.max_modifier_container_number];
		for(int i=0;i<scene_par.max_modifier_container_number;i++)
			modifier_cont[i]=new modifier_container(current_time.nanoseconds());
		
		part_lru				=null;	

		part_loader_cont		=my_part_loader_cont;
		
		reset_flag				=false;
		
		render_cont				=my_original_render;
		part_cont				=null;	
		
		do_selection_version	=1;
		program_last_time		=0;
	}
	public long get_file_last_modified_time()
	{
		long ret_val=0;
		
		if(ret_val<program_last_time)
			ret_val=program_last_time;
		
		if(ret_val<system_par.last_modified_time)
			ret_val=system_par.last_modified_time;
		if(ret_val<scene_par.parameter_last_modified_time)
			ret_val=scene_par.parameter_last_modified_time;
		if(ret_val<scene_par.scene_last_modified_time)
			ret_val=scene_par.scene_last_modified_time;
		
		if(component_cont!=null)
			if(component_cont.root_component!=null)
				if(ret_val<component_cont.root_component.uniparameter.file_last_modified_time)
					ret_val=component_cont.root_component.uniparameter.file_last_modified_time;
		
		if(process_part_sequence!=null)
			if(ret_val<process_part_sequence.all_buffer_object_head_package_last_modify_time)
				ret_val=process_part_sequence.all_buffer_object_head_package_last_modify_time;
		
		return ret_val;
	}
	private void load_camera()
	{
		String camera_file_name;
		if(!(new File(camera_file_name=scene_par.directory_name+scene_par.camera_file_name).exists()))
			if(!(new File(camera_file_name=scene_par.extra_directory_name+scene_par.camera_file_name).exists()))
				camera_file_name=system_par.default_parameter_directory+"camera_parameter"+File.separator+scene_par.camera_file_name;
		
		file_reader f_camera=new file_reader(camera_file_name,scene_par.parameter_charset);
		if(f_camera.error_flag()){
			camera_cont=null;
			debug_information.println("Opening camera file fail:"+scene_par.directory_name+scene_par.camera_file_name);
			debug_information.println("Opening camera file fail:"+camera_file_name);	
		}else
			camera_cont=new camera_container(f_camera,component_cont,scene_par.max_camera_return_stack_number);
		f_camera.close();
	}
	private void mount_top_box_part(component comp,
			component_load_source_container component_load_source_cont,
			part_container_for_part_search part_search,client_request_response request_response)
	{
		part p[];
		int child_number;
		
		if((comp.driver_number()>0)||((child_number=comp.children_number())<=0))
			return;

		if((p=part_search.search_part(comp.part_name))==null)
			for(int i=0;i<child_number;i++)
				mount_top_box_part(comp.children[i],component_load_source_cont,part_search,request_response);
		else{
			component_driver cd;
			try{
				cd=p[0].driver.create_component_driver(null,false,p[0],component_load_source_cont,this,request_response);
			}catch(Exception e){
				cd=null;
				debug_information.println("create_component_driver fail int mount_top_box_part():	",e.toString());
				debug_information.println("Part user name:",	p[0].user_name);
				debug_information.println("Part system name:",	p[0].system_name);
				debug_information.println("Mesh_file_name:",	p[0].directory_name+p[0].mesh_file_name);
				debug_information.println("Material_file_name:",p[0].directory_name+p[0].material_file_name);
				e.printStackTrace();
			}
			comp.driver_array=(cd==null)?null:new component_driver[]{cd};
		}	
	}
	
	private void load_create_assemble_part(component_load_source_container component_load_source_cont,
			client_request_response request_response,boolean not_real_scene_fast_load_flag,
			part_container part_cont_for_delete_file,part_container_for_part_search all_part_part_cont,
			buffer_object_file_modify_time_and_length_container boftal_container)
	{	
		if(create_parameter.create_top_part_expand_ratio>=1.0)
			if(create_parameter.create_top_part_left_ratio>=1.0)
				if(component_cont.root_component!=null){
					part top_box_part[]=(new create_assemble_part(
							not_real_scene_fast_load_flag,part_cont_for_delete_file,
							request_response,component_cont.root_component,
							create_parameter.create_top_part_expand_ratio,
							create_parameter.create_top_part_left_ratio,
							scene_par.create_top_part_assembly_precision2,
							scene_par.create_top_part_discard_precision2,
							scene_par.discard_top_part_component_precision2,
							render_cont,part_loader_cont,
							system_par,scene_par,all_part_part_cont,boftal_container,
							get_file_last_modified_time())).top_box_part;
					if(top_box_part!=null)
						if(top_box_part.length>0)
							mount_top_box_part(component_cont.root_component,component_load_source_cont,
								new part_container_for_part_search(top_box_part),request_response);
				}
	}
	private void load_routine(component_load_source_container component_load_source_cont,
			client_request_response request_response,client_process_bar process_bar)
	{
		long start_time=new Date().getTime(),current_time;
		String boftal_file_name=scene_par.scene_proxy_directory_name+"engine.boftal";
		buffer_object_file_modify_time_and_length_container boftal_container=
			new buffer_object_file_modify_time_and_length_container(
					process_bar,boftal_file_name,system_par.local_data_charset);
		debug_information.println("Load engine.boftal time length	:	",new Date().getTime()-start_time);
		debug_information.println();
		
		boolean not_real_scene_fast_load_flag=scene_par.fast_load_flag?false:true;
		if(boftal_container.get_boftal_number()<=0)
			not_real_scene_fast_load_flag=true;
		
		file_reader scene_f=new file_reader(
				create_parameter.scene_directory_name+create_parameter.scene_file_name,
				create_parameter.scene_charset);
		if(!(scene_f.error_flag())){
			create_parameter.scene_directory_name	=scene_f.directory_name;
			create_parameter.scene_file_name		=scene_f.file_name;
			if(scene_par.scene_last_modified_time<scene_f.lastModified_time)
				scene_par.scene_last_modified_time=scene_f.lastModified_time;
		}
		
		part_container_for_delete_part_file part_cont_for_delete_file;
		part_cont_for_delete_file=new part_container_for_delete_part_file();

		render_cont=new render_container(render_cont,request_response,system_par,scene_par);
		part_cont=new part_container_for_part_search(render_cont.part_array(true,-1));

		start_time=new Date().getTime();
		render_cont.load_shader(not_real_scene_fast_load_flag,component_load_source_cont,
				part_cont,scene_par.parameter_last_modified_time,
				scene_par.directory_name+scene_par.type_shader_file_name,
				scene_par.parameter_charset,"",1,system_par,scene_par,request_response);
		render_cont.load_shader(not_real_scene_fast_load_flag,component_load_source_cont,
				part_cont,scene_par.scene_last_modified_time,
				create_parameter.scene_directory_name+scene_par.scene_shader_file_name,
				create_parameter.scene_charset,
				scene_par.scene_sub_directory,2,system_par,scene_par,request_response);
		part_cont.execute_append();
		debug_information.println("Load shaders time length:	",(current_time=new Date().getTime())-start_time);
		debug_information.println();
		
		start_time=current_time;
		render_cont.load_part(not_real_scene_fast_load_flag,(1<<1)+(1<<2),1,part_loader_cont,
			system_par,scene_par,part_cont,boftal_container,"load_first_class_part",process_bar,part_cont_for_delete_file);
		debug_information.println("Load first class part time length:	",(current_time=new Date().getTime())-start_time);
		debug_information.println();
		
		start_time=current_time;
		render_cont.create_bottom_box_part(part_cont,request_response,system_par,scene_par);
		part_cont.execute_append();
		render_cont.load_part(not_real_scene_fast_load_flag,(1<<1)+(1<<2),2,part_loader_cont,
			system_par,scene_par,part_cont,boftal_container,"load_second_class_part",process_bar,part_cont_for_delete_file);
		debug_information.println("Load second class part time length:	",
				(current_time=new Date().getTime())-start_time);
		debug_information.println();

		start_time=current_time;
		render_cont.type_part_package=new part_package(not_real_scene_fast_load_flag,process_bar,
				"create_first_class_package",render_cont,1,system_par,scene_par);
		debug_information.println("Create first part package time length:	",
				(current_time=new Date().getTime())-start_time);
		debug_information.println();
		
		start_time=current_time;
		process_bar.set_process_bar(true,"load_component", "",1, 2);
		component_cont=new component_container(
				scene_f,this,component_load_source_cont,
				scene_par.default_display_bitmap,request_response,
				new change_name(
						new String[]{
								scene_par.extra_directory_name+scene_par.change_part_file_name,
								scene_par.directory_name+scene_par.change_part_file_name,
								scene_f.directory_name	+scene_par.change_part_file_name
						},scene_par.change_part_string,scene_par.parameter_charset),
				new change_name(
						new String[]{
								scene_par.extra_directory_name+scene_par.change_component_file_name,
								scene_par.directory_name+scene_par.change_component_file_name,
								scene_f.directory_name	+scene_par.change_component_file_name
						},scene_par.change_component_string,scene_par.parameter_charset),
				new part_type_string_sorter(
						new String[]{
								scene_par.extra_directory_name+scene_par.type_string_file_name,
								scene_par.directory_name+scene_par.type_string_file_name,
								create_parameter.scene_directory_name+scene_par.type_string_file_name
						},scene_par.part_type_string,scene_par.parameter_charset));
		
		scene_f.close();
		process_bar.set_process_bar(false,"load_component","", 2, 2);
		debug_information.println("Load components time length:	",new Date().getTime()-start_time);
		debug_information.println();

		component_cont.do_component_caculator(false,process_bar,"first_do_component_caculator");
		component_cont.root_component.reset_component(component_cont);
		
		start_time=new Date().getTime();
		load_create_assemble_part(component_load_source_cont,request_response,
				not_real_scene_fast_load_flag,part_cont_for_delete_file,part_cont,boftal_container);	
		part_cont.execute_append();
		render_cont.load_part(not_real_scene_fast_load_flag,
				(1<<2),4,part_loader_cont,system_par,scene_par,part_cont,
				boftal_container,"load_third_class_part",process_bar,part_cont_for_delete_file);
		debug_information.println("Create top assemble time length:	",(current_time=new Date().getTime())-start_time);
		debug_information.println();
		start_time=current_time;
		
		boftal_container.destroy();
		
		render_cont.scene_part_package=new part_package(not_real_scene_fast_load_flag,process_bar,
				"create_second_class_package",render_cont,2,system_par,scene_par);
		
		debug_information.println("Create second part package time length:	",(current_time=new Date().getTime())-start_time);
		debug_information.println();
		start_time=current_time;
		
		component_cont.original_part_number	=new compress_render_container(
				render_cont,part_cont,component_cont.root_component).original_part_number;
		
		part_cont.destroy();
		part_cont=new part_container_for_part_search(render_cont.part_array(true,-1));

		component_cont.do_component_caculator(true,process_bar,"second_do_component_caculator");
		
		process_part_sequence=new part_process_sequence(render_cont,component_cont.root_component);

		collector_stack=new component_collector_stack(component_cont,system_par,scene_par,render_cont.renders);
		load_camera();

		start_time=new Date().getTime();
		if(not_real_scene_fast_load_flag){
			process_bar.set_process_bar(true,"create_shader","",1,2);
			program_last_time=copy_program.copy_shader_programs(render_cont,system_par,scene_par);
			new engine_initialization(true,program_last_time,this,request_response,process_bar);
			new engine_boftal_creator(boftal_file_name,system_par.local_data_charset,
					part_cont.data_array,system_par,scene_par,process_bar);
			debug_information.println("Create engine temp data time length:	",		new Date().getTime()-start_time);
		}else{
			new engine_initialization(false,program_last_time,this,request_response,process_bar);
			debug_information.println("Doing engine_initialization time length:	",	new Date().getTime()-start_time);
		}
		debug_information.println();
		
		part_lru=new part_lru_manager(render_cont.renders,scene_par.part_lru_in_list_number);
		
		part_cont_for_delete_file.delete_part_file(process_bar,system_par, scene_par);
		
		process_bar.set_process_bar(true,"load_termination", "",1, 1);

		return;
	}
	public void load(component_load_source_container component_load_source_cont,
			client_request_response request_response,client_process_bar process_bar)
	{
		String my_lock_name=scene_par.scene_proxy_directory_name+"engine.lock";
		exclusive_file_mutex efm=exclusive_file_mutex.lock(my_lock_name,
				"wait for load engine kernel:	"+scene_par.scene_proxy_directory_name);
		
		debug_information.println();
		debug_information.println("type_shader_file_name 		:	",	scene_par.directory_name+scene_par.type_shader_file_name);
		debug_information.println("scene_shader_file_name		:	",	
				create_parameter.scene_directory_name+scene_par.scene_shader_file_name);		
		debug_information.println("camera_file_name		:	"		 ,	scene_par.directory_name+scene_par.camera_file_name);
		debug_information.println("change_part_file_name		:	",	scene_par.directory_name+scene_par.change_part_file_name);
		debug_information.println("change_component_file_name	:	",	scene_par.directory_name+scene_par.change_component_file_name);
		debug_information.println("type_proxy_directory_name	:	",	scene_par.type_proxy_directory_name);
		debug_information.println("scene_proxy_directory_name	:	",	scene_par.scene_proxy_directory_name);
		debug_information.println("change_part_string		:	"	 ,	scene_par.change_part_string);
		debug_information.println("change_component_string		:	",	scene_par.change_component_string);
		debug_information.println("part_type_string		:	"		 ,	scene_par.part_type_string);
		debug_information.println("scene_sub_directory		:	"	 ,	scene_par.scene_sub_directory);
		
		try {
			load_routine(component_load_source_cont,request_response,process_bar);
		}catch(Exception e) {
			debug_information.println("Engine load exception:	",e.toString());
			e.printStackTrace();
		}finally {
			efm.unlock();
		}
	}
	private boolean reset_flag;
	public void mark_reset_flag()
	{
		reset_flag=true;
	}
	public void process_reset()
	{
		if(reset_flag){
			reset_flag=false;
			component_cont.root_component.reset_component(component_cont);
		}
	}
}
