package kernel_engine;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.debug_information;
import kernel_common_class.tree_string_search_container;
import kernel_component.component_load_source_container;
import kernel_file_manager.file_directory;
import kernel_network.client_request_response;
import kernel_part.buffer_object_file_modify_time_and_length_container;
import kernel_part.part;
import kernel_part.part_container_for_part_search;
import kernel_part.part_loader_container;
import kernel_part.permanent_part_id_encoder;
import kernel_render.render_container;

public class engine_kernel_container_search_tree 
{
	private tree_string_search_container<engine_kernel_container> tree;
	
	private render_container original_render;
	public component_load_source_container component_load_source_cont;
	public buffer_object_file_modify_time_and_length_container system_boftal_container;
	private part_loader_container part_loader_cont;
	
	private volatile ReentrantLock engine_interface_container_lock;
	
	private void load_render_container(
		client_request_response request_response,system_parameter system_par)
	{
		int part_type_id=0;
		buffer_object_file_modify_time_and_length_container boftal_container[];
		boftal_container=new buffer_object_file_modify_time_and_length_container[] {};
		
		permanent_part_id_encoder encoder[]=new permanent_part_id_encoder[] {new permanent_part_id_encoder()};
		ArrayList<part> part_list_for_delete_file=new ArrayList<part>();
		original_render=new render_container();
		part_container_for_part_search pcps=new part_container_for_part_search(new ArrayList<part>());
		original_render.load_shader(
			component_load_source_cont,pcps,system_par.last_modified_time,
			system_par.data_root_directory_name+system_par.shader_file_name,
			system_par.local_data_charset,part_type_id,system_par,null,encoder,request_response);
		pcps.execute_append();
		original_render.load_part(((long)1)<<part_type_id,1,part_loader_cont,
				system_par,null,boftal_container,part_list_for_delete_file,null,null,null);
		
		original_render.create_bottom_box_part(pcps,request_response,encoder,system_par,null);
		pcps.execute_append();
		original_render.load_part(((long)1)<<part_type_id,2,part_loader_cont,
				system_par,null,boftal_container,part_list_for_delete_file,null,null,null);
		
		debug_information.println("Begin create system_part_package");
		original_render.system_part_package=new part_package(
				null,null,null,null,original_render,part_type_id,system_par,null);
		
		system_boftal_container=new buffer_object_file_modify_time_and_length_container(null,
				file_directory.package_file_directory(0,system_par,null)+"boftal_data.txt",
				system_par.local_data_charset);
		
		debug_information.println("End create system_part_package");
		
		delete_part_files.do_delete(part_list_for_delete_file,null,system_par,null);
	}
	private engine_kernel_container create_engine_kernel_container_routine(
			client_request_response request_response,
			String client_scene_file_name,String client_scene_file_charset,
			create_engine_counter engine_counter,system_parameter system_par)
	{
		String scene_name,link_name;
		if((scene_name=request_response.get_parameter("scene_name"))==null)
			scene_name="";
		
		if((link_name=request_response.get_parameter("link_name"))==null)
			link_name="";
		
		if(link_name.compareTo("")==0)
			link_name=Double.toString(Math.random());

		debug_information.println(request_response.client_id,"	Create scene");
		debug_information.print  ("scene_name:	",scene_name);
		debug_information.println(",link_name:	",link_name);
		debug_information.print  ("engine_interface engine_kernel_number:	",		engine_counter.engine_kernel_number);
		debug_information.println("/",system_par.max_engine_kernel_number);
		debug_information.print  ("engine_interface engine_component_number:	",	engine_counter.engine_component_number);
		debug_information.println("/",system_par.max_engine_component_number);
		
		
		if(tree.search(new String[]{scene_name,link_name})!=null) {
			tree.search_value.update_link_number(1);
			return tree.search_value;
		}
		if(   (engine_counter.engine_kernel_number   >=system_par.max_engine_kernel_number)
				||(engine_counter.engine_component_number>=system_par.max_engine_component_number))
		{
			debug_information.println("Create too many scenes or components:	",scene_name+"	"+link_name);
			destroy_engine_kernel_container_routine(scene_name,link_name,engine_counter);
			return null;
		}
		
		engine_kernel_container engine_kernel_cont=new engine_kernel_container(
			scene_name,link_name,request_response,system_par,
			client_scene_file_name,client_scene_file_charset,
			original_render,part_loader_cont);
		
		if(engine_kernel_cont.ek==null){
			debug_information.println("Create scene fail:	",scene_name+"	"+link_name);
			destroy_engine_kernel_container_routine(scene_name,link_name,engine_counter);
			engine_kernel_cont.destroy();
			return null;
		}
		debug_information.println("Create scene success:	",scene_name+"	"+link_name);
		engine_kernel_cont.update_link_number(1);
		
		tree.add(new String[]{scene_name,link_name},engine_kernel_cont);
	
		return engine_kernel_cont;
	}
	private void destroy_engine_kernel_container_routine(
			String my_scene_name,String my_link_name,create_engine_counter engine_counter)
	{
		for(String key[]={my_scene_name,my_link_name};tree.search(key)!=null;) {
			if(tree.search_value.update_link_number(-1)>0)
				return;
			tree.remove(key);

			if(tree.search_value.ek!=null)
				if(tree.search_value.ek.component_cont!=null)
					if(tree.search_value.ek.component_cont.root_component!=null)
							engine_counter.update_kernel_component_number(-1,
									-1-tree.search_value.ek.component_cont.root_component.component_id);
			
			tree.search_value.destroy();
			
			debug_information.println(
					"engine_interface deletes scene,scene_name: ",
					my_scene_name+",link_name: "+my_link_name);
			debug_information.println(
					"engine_interface engine_kernel_number: ",
					engine_counter.engine_kernel_number);
			debug_information.println(
					"engine_interface engine_component_number: ",
					engine_counter.engine_component_number);
		}
	}
	
	public engine_kernel_container create_engine_kernel_container(
			client_request_response request_response,
			String client_scene_file_name,String client_scene_file_charset,
			create_engine_counter engine_counter,system_parameter system_par)
	{
		ReentrantLock my_lock;
		if((my_lock=engine_interface_container_lock)==null)
			return null;
		my_lock.lock();
		
		engine_kernel_container ret_val=null;
		if(original_render==null)
			load_render_container(request_response,system_par);
		
		try {
			ret_val=create_engine_kernel_container_routine(request_response,
				client_scene_file_name,client_scene_file_charset,engine_counter,system_par);
		}catch(Exception e) {
			e.printStackTrace();
			
			debug_information.println("get_kernel_container of engine_interface fail");
			debug_information.println(e.toString());
			ret_val=null;
		}
		my_lock.unlock();
		return ret_val;
	}
	public void destroy_engine_kernel_container(
			String my_scene_name,String my_link_name,
			create_engine_counter engine_counter)
	{
		ReentrantLock my_lock;
		if((my_lock=engine_interface_container_lock)==null)
			return;
		my_lock.lock();
		destroy_engine_kernel_container_routine(
				my_scene_name,my_link_name,engine_counter);
		my_lock.unlock();
	}
	public void destroy()
	{
		ReentrantLock my_lock;
		if((my_lock=engine_interface_container_lock)==null)
			return;
		my_lock.lock();
		
		while(tree.first_touch_time()>=0) {
			tree.search_value.destroy();
			tree.remove(tree.search_key);
		}
		if(component_load_source_cont!=null) {
			component_load_source_cont.destroy();
			component_load_source_cont=null;
		}
		if(original_render!=null){
			original_render.destroy();
			original_render=null;
		}
		if(part_loader_cont!=null){
			part_loader_cont.destroy();
			part_loader_cont=null;
		}
		
		engine_interface_container_lock=null;
		
		my_lock.unlock();
	}
	public engine_kernel_container_search_tree()
	{
		tree=new tree_string_search_container<engine_kernel_container>();
		
		component_load_source_cont		=new component_load_source_container();
		
		original_render					=null;
		system_boftal_container			=null;
		part_loader_cont				=new part_loader_container();
		engine_interface_container_lock	=new ReentrantLock();
	}
}
