package kernel_engine;

import java.io.File;
import java.util.Date;

import kernel_camera.camera_container;
import kernel_common_class.change_name;
import kernel_common_class.nanosecond_timer;
import kernel_component.component;
import kernel_component.component_collector_stack;
import kernel_driver.modifier_container;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_render.render_container;

import kernel_part.part;
import kernel_part.part_container_for_part_search;
import kernel_render.create_assemble_part;
import kernel_driver.component_driver;
import kernel_part.part_loader_container;
import kernel_common_class.debug_information;

public class engine_kernel
{
	public String 							title,scene_name,link_name;
	public String							scene_directory_name,scene_file_name,scene_charset;

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

	public long 							do_selection_version;
	
	private part_loader_container 			part_loader_cont;

	private double create_top_part_expand_ratio,create_top_part_left_ratio;
	private long							program_last_time;

	public void destroy()
	{
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
		
		system_par=null;
		scene_par=null;
		current_time=null;
		
		part_loader_cont=null;
	}
	public engine_kernel(client_request_response request_response,
			double my_create_top_part_expand_ratio,double my_create_top_part_left_ratio,
			String my_scene_name,String my_scene_title,String my_link_name,
			String my_scene_directory_name,String my_scene_file_name,String my_scene_charset,
			long my_scene_list_file_last_modified_time,
			String my_parameter_file_name,String my_parameter_charset,
			String my_extra_parameter_file_name,String my_extra_parameter_charset,
			system_parameter my_system_parameter,render_container my_render_cont,
			part_loader_container my_part_loader_cont)
	{
		create_top_part_expand_ratio	=my_create_top_part_expand_ratio;
		create_top_part_left_ratio		=my_create_top_part_left_ratio;
		
		scene_name				=new String(my_scene_name);
		title					=new String(my_scene_title);
		link_name				=new String(my_link_name);
				
		system_par				=my_system_parameter;
		scene_par				=new scene_parameter(request_response,
			scene_name,system_par,request_response.get_parameter("sub_directory"),
			my_parameter_file_name,my_parameter_charset,my_extra_parameter_file_name,
			my_extra_parameter_charset,my_scene_list_file_last_modified_time);
		
		component_cont			=null;
		camera_cont				=null;
		collector_stack			=null;

		process_part_sequence	=null;
		
		current_time			=new nanosecond_timer();

		modifier_cont			=new modifier_container[scene_par.max_modifier_container_number];
		for(int i=0;i<modifier_cont.length;i++)
			modifier_cont[i]	=new modifier_container(current_time.nanoseconds());
		
		do_selection_version	=1;
		
		part_loader_cont		=my_part_loader_cont;
		
		scene_directory_name	=my_scene_directory_name;
		scene_file_name			=my_scene_file_name;
		scene_charset			=my_scene_charset;
		
		reset_flag				=false;
		render_cont				=new render_container(my_render_cont,system_par,request_response);
		
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
		String camera_file_name=scene_par.directory_name+scene_par.camera_file_name;
		if(!(new File(camera_file_name).exists()))
			camera_file_name=system_par.default_parameter_directory
				+"camera_parameter"+File.separator+scene_par.camera_file_name;
		file_reader f_camera=new file_reader(camera_file_name,scene_par.parameter_charset);
		if(f_camera.error_flag()){
			camera_cont=null;
			debug_information.println("Opening camera file fail:"+scene_par.directory_name+scene_par.camera_file_name);
			debug_information.println("Opening camera file fail:"+camera_file_name);	
		}else
			camera_cont=new camera_container(f_camera,component_cont,scene_par.max_camera_return_stack_number);
		f_camera.close();
	}
	private void mount_top_box_part(
			component comp,part_container_for_part_search part_search,
			client_request_response request_response)
	{
		part p[];
		int child_number;
		
		if((comp.driver_number()>0)||((child_number=comp.children_number())<=0))
			return;

		if((p=part_search.search_part(comp.part_name))==null)
			for(int i=0;i<child_number;i++)
				mount_top_box_part(comp.children[i],part_search,request_response);
		else{
			component_driver cd;
			try{
				cd=p[0].driver.create_component_driver(null,false,p[0],this,request_response);
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
	
	private void load_create_assemble_part(
			client_request_response request_response,part_container_for_part_search all_part_part_cont)
	{	
		if((create_top_part_expand_ratio>=1.0)&&(create_top_part_left_ratio>=1.0)){
			if(component_cont.root_component!=null){
				part top_box_part[]=(new create_assemble_part(
						request_response,component_cont.root_component,
						create_top_part_expand_ratio,create_top_part_left_ratio,
						scene_par.create_top_part_assembly_precision2,
						scene_par.create_top_part_discard_precision2,
						scene_par.discard_top_part_component_precision2,
						render_cont,part_loader_cont,
						system_par,scene_par,all_part_part_cont,
						get_file_last_modified_time())).top_box_part;
				if(top_box_part!=null)
					if(top_box_part.length>0)
						mount_top_box_part(component_cont.root_component,
							new part_container_for_part_search(top_box_part),request_response);
			}
		}
	}
	private void load_routine(client_request_response request_response)
	{
		debug_information.println();
		debug_information.println("type_shader_file_name 		:	",	scene_par.directory_name+scene_par.type_shader_file_name);
		debug_information.println("scene_shader_file_name		:	",	scene_directory_name	+scene_par.scene_shader_file_name);		
		debug_information.println("camera_file_name		:	"		 ,	scene_par.directory_name+scene_par.camera_file_name);
		debug_information.println("change_part_file_name		:	",	scene_par.directory_name+scene_par.change_part_file_name);
		debug_information.println("change_component_file_name	:	",	scene_par.directory_name+scene_par.change_component_file_name);
		debug_information.println("type_proxy_directory_name	:	",	scene_par.type_proxy_directory_name);
		debug_information.println("scene_proxy_directory_name	:	",	scene_par.scene_proxy_directory_name);
		debug_information.println("change_part_string		:	"	 ,	scene_par.change_part_string);
		debug_information.println("change_component_string		:	",	scene_par.change_component_string);
		debug_information.println("part_type_string		:	"		 ,	scene_par.part_type_string);
		debug_information.println("scene_sub_directory		:	"	 ,	scene_par.scene_sub_directory);

		file_reader scene_f=new file_reader(scene_directory_name+scene_file_name,scene_charset);
		if(!(scene_f.error_flag())){
			scene_directory_name				=scene_f.directory_name;
			scene_file_name						=scene_f.file_name;
			if(scene_par.scene_last_modified_time<scene_f.lastModified_time)
				scene_par.scene_last_modified_time=scene_f.lastModified_time;
		}

		part_type_string_sorter my_part_type_string_sorter=new part_type_string_sorter(
				new String[]{
						scene_par.directory_name+scene_par.type_string_file_name,
						scene_directory_name	+scene_par.type_string_file_name
				},scene_par.part_type_string,scene_par.parameter_charset);
		render_cont.load_shader(scene_par.parameter_last_modified_time,
				scene_par.directory_name+scene_par.type_shader_file_name,
				scene_par.parameter_charset,"",1,null,system_par,scene_par,request_response);
		render_cont.load_shader(scene_par.scene_last_modified_time,
				scene_directory_name+scene_par.scene_shader_file_name,scene_charset,
				scene_par.scene_sub_directory, 2,my_part_type_string_sorter,
				system_par,scene_par,request_response);
		render_cont.create_bottom_box_part(request_response,system_par);
		part_container_for_part_search all_part_part_cont=new part_container_for_part_search(render_cont.part_array(true,-1));
		render_cont.load_part(part_loader_cont,system_par,scene_par,all_part_part_cont);
		render_cont.type_part_package=new part_package(render_cont,1,system_par,scene_par);
		
		part_cont=new part_container_for_part_search(render_cont.part_array(false,-1));
		
		component_cont=new component_container(scene_f,
				this,scene_par.default_display_bitmap,request_response,
				new change_name(
						new String[]{
								scene_par.directory_name+scene_par.change_part_file_name,
								scene_f.directory_name	+scene_par.change_part_file_name
						},scene_par.change_part_string,scene_par.parameter_charset),
				new change_name(
						new String[]{
								scene_par.directory_name+scene_par.change_component_file_name,
								scene_f.directory_name	+scene_par.change_component_file_name
						},scene_par.change_component_string,scene_par.parameter_charset),
				new change_name(
						new String[]{
								scene_par.directory_name+scene_par.mount_component_file_name,
								scene_f.directory_name	+scene_par.mount_component_file_name
						},scene_par.mount_component_string,scene_par.parameter_charset),
				my_part_type_string_sorter);
		
		scene_f.close();
		
		component_cont.do_component_caculator(false);
		component_cont.root_component.reset_component(component_cont);
		load_create_assemble_part(request_response,all_part_part_cont);
		component_cont.original_part_number=new compress_render_container(
			render_cont,part_cont,component_cont.root_component).original_part_number;
		part_cont=new part_container_for_part_search(render_cont.part_array(false,-1));
		
		render_cont.scene_part_package=new part_package(render_cont,2,system_par,scene_par);
		
		component_cont.do_component_caculator(true);
		process_part_sequence=new part_process_sequence(render_cont,component_cont.root_component);

		collector_stack=new component_collector_stack(scene_directory_name,
			scene_f.get_charset(),component_cont,system_par,scene_par,render_cont.renders);
		load_camera();

		program_last_time=copy_program.copy_shader_programs(render_cont,system_par,scene_par);
		
		new engine_initialization(this,request_response);
		
		{
			long current_time=(new Date()).getTime();
			(new File(scene_par.type_proxy_directory_name)).setLastModified(current_time);
			(new File(scene_par.scene_proxy_directory_name)).setLastModified(current_time);
		}
	}
	public void load(client_request_response request_response)
	{
		system_par.system_exclusive_name_mutex.lock(
				scene_par.scene_proxy_directory_name+"engine.lock");
		load_routine(request_response);
		system_par.system_exclusive_name_mutex.unlock(
				scene_par.scene_proxy_directory_name+"engine.lock");
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
