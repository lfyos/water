package kernel_scene;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import kernel_render.render_container;
import kernel_part.part_loader_container;
import kernel_file_manager.file_directory;
import kernel_part.permanent_part_id_encoder;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_common_class.tree_string_locker_container;
import kernel_component.component_load_source_container;
import kernel_common_class.tree_search_container_tree_node;
import kernel_common_class.tree_string_array_search_container;
import kernel_part.buffer_object_file_modify_time_and_length_container;

public class scene_kernel_container_search_tree 
{
	private tree_string_array_search_container<scene_kernel_container> tree;
	
	private render_container original_render;
	public component_load_source_container system_component_load_source_cont;
	public buffer_object_file_modify_time_and_length_container system_boftal_container;
	private part_loader_container part_loader_cont;
	
	private volatile ReentrantLock scene_kernel_container_search_tree_lock;
	
	private void load_render_container(client_request_response request_response,
			system_parameter system_par,tree_string_locker_container string_locker_container)
	{
		int part_type_id=0;
		
		permanent_part_id_encoder encoder[]=new permanent_part_id_encoder[] {new permanent_part_id_encoder()};
		part_container_for_part_search pcps=new part_container_for_part_search(null);
		
		original_render=new render_container();
		original_render.load_shader(
				system_component_load_source_cont,pcps,system_par.last_modified_time,
			system_par.data_root_directory_name+system_par.shader_file_name,
			system_par.local_data_charset,part_type_id,system_par,null,encoder,request_response);
		
		String fast_load_type=request_response.get_fast_load_type();
		
		original_render.load_part(((long)1)<<part_type_id,1,part_loader_cont,system_par,null,
				new ArrayList<buffer_object_file_modify_time_and_length_container>(),
				string_locker_container,null,null,fast_load_type);
		
		original_render.create_bottom_box_part(pcps,request_response,encoder,system_par,null);
		original_render.load_part(((long)1)<<part_type_id,2,part_loader_cont,system_par,null,
				new ArrayList<buffer_object_file_modify_time_and_length_container>(),
				string_locker_container,null,null,fast_load_type);
		
		debug_information.println();
		debug_information.println("Begin create system_part_package");
		
		original_render.system_part_package=new part_package(
			fast_load_type,null,string_locker_container,
			null,null,original_render,part_type_id,system_par,null);
		
		system_boftal_container=new buffer_object_file_modify_time_and_length_container();
		try {
			system_boftal_container.load(null,"",
					file_directory.package_file_directory(0,system_par,null)+"boftal_data.txt",
					system_par.local_data_charset);
		}catch(Exception e) {
			system_boftal_container=new buffer_object_file_modify_time_and_length_container();
		}
		
		debug_information.println();
		debug_information.println("End create system_part_package");
		debug_information.println();
	}
	private scene_kernel_container create_scene_kernel_container_routine(
			client_request_response request_response,
			String client_scene_file_name,String client_scene_file_charset,
			create_scene_counter scene_counter,system_parameter system_par)
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
		debug_information.print  ("scene_interface scene_kernel_number:	",		scene_counter.scene_kernel_number);
		debug_information.println("/",system_par.max_scene_kernel_number);
		debug_information.print  ("scene_interface scene_component_number:	",	scene_counter.scene_component_number);
		debug_information.println("/",system_par.max_scene_component_number);
		
		ArrayList<scene_kernel_container>search_list;
		if((search_list=tree.search_value_list(new String[]{scene_name,link_name}))!=null){
			scene_kernel_container skc=search_list.get(0);
			skc.modify_scene_kernel_link_number(1);
			return skc;
		}
		if(   (scene_counter.scene_kernel_number   >=system_par.max_scene_kernel_number)
				||(scene_counter.scene_component_number>=system_par.max_scene_component_number))
		{
			debug_information.println("Create too many scenes or components:	",scene_name+"	"+link_name);
			destroy_scene_kernel_container_routine(scene_name,link_name,scene_counter);
			return null;
		}
		
		scene_kernel_container scene_kernel_cont=new scene_kernel_container(
				scene_name,link_name,request_response,system_par,
				client_scene_file_name,client_scene_file_charset,
				original_render,part_loader_cont);
		
		if(scene_kernel_cont.sk==null){
			debug_information.println("Create scene fail:	",scene_name+"	"+link_name);
			destroy_scene_kernel_container_routine(scene_name,link_name,scene_counter);
			scene_kernel_cont.destroy();
			return null;
		}
		
		scene_kernel_cont.modify_scene_kernel_link_number(1);
		
		tree.add(new String[]{scene_name,link_name},scene_kernel_cont);
	
		return scene_kernel_cont;
	}
	public scene_kernel_container create_scene_kernel_container(
			client_request_response request_response,
			tree_string_locker_container string_locker_container,
			String client_scene_file_name,String client_scene_file_charset,
			create_scene_counter scene_counter,system_parameter system_par)
	{
		ReentrantLock my_lock;
		if((my_lock=scene_kernel_container_search_tree_lock)==null)
			return null;
		my_lock.lock();

		if(original_render==null)
			load_render_container(request_response,system_par,string_locker_container);
		
		scene_kernel_container my_scene_kernel_container=null;
		try {
			my_scene_kernel_container=create_scene_kernel_container_routine(request_response,
				client_scene_file_name,client_scene_file_charset,scene_counter,system_par);
		}catch(Exception e) {
			e.printStackTrace();
			
			debug_information.println("get_kernel_container of scene_interface fail");
			debug_information.println(e.toString());
			my_scene_kernel_container=null;
		}
		my_lock.unlock();
		
		return my_scene_kernel_container;
	}
	private void destroy_scene_kernel_container_routine(
			String my_scene_name,String my_link_name,create_scene_counter scene_counter)
	{
		String key[]={my_scene_name,my_link_name};
		ArrayList<scene_kernel_container>search_list;
		
		if((search_list=tree.search_value_list(key))!=null)
			while(search_list.size()>0){
				scene_kernel_container skc=search_list.get(0);
				if(skc.modify_scene_kernel_link_number(-1)>0)
					return;
				search_list.remove(0);
				
				if(skc.sk!=null)
					if(skc.sk.component_cont!=null)
						if(skc.sk.component_cont.root_component!=null)
							scene_counter.update_kernel_component_number(-1,
								-1-skc.sk.component_cont.root_component.component_id);
				skc.destroy();
				
				debug_information.println(
						"scene_interface deletes scene,scene_name: ",
						my_scene_name+",link_name: "+my_link_name);
				debug_information.println(
						"scene_interface scene_kernel_number: ",
						scene_counter.scene_kernel_number);
				debug_information.println(
						"scene_interface scene_component_number: ",
						scene_counter.scene_component_number);
			}	
		tree.remove(key);
	}
	public void destroy_scene_kernel_container(
			String my_scene_name,String my_link_name,
			create_scene_counter scene_counter)
	{
		ReentrantLock my_lock;
		if((my_lock=scene_kernel_container_search_tree_lock)==null)
			return;
		my_lock.lock();
		destroy_scene_kernel_container_routine(my_scene_name,my_link_name,scene_counter);
		my_lock.unlock();
	}
	public void destroy()
	{
		ReentrantLock my_lock;
		if((my_lock=scene_kernel_container_search_tree_lock)==null)
			return;
		my_lock.lock();
		
		tree_search_container_tree_node <String[],scene_kernel_container> first_tree_node;
		
		while((first_tree_node=tree.get_first_tree_node())!=null){
			String my_key[]								=first_tree_node.key;
			ArrayList<scene_kernel_container> my_list	=first_tree_node.list;
			for(int i=0,ni=my_list.size();i<ni;i++)
				my_list.remove(i).destroy();
			tree.remove(my_key);
		}
		if(system_component_load_source_cont!=null) {
			system_component_load_source_cont.destroy();
			system_component_load_source_cont=null;
		}
		if(original_render!=null){
			original_render.destroy();
			original_render=null;
		}
		if(part_loader_cont!=null){
			part_loader_cont.destroy();
			part_loader_cont=null;
		}
		
		scene_kernel_container_search_tree_lock=null;
		
		my_lock.unlock();
	}
	public scene_kernel_container_search_tree()
	{
		tree=new tree_string_array_search_container<scene_kernel_container>(null);
		
		system_component_load_source_cont		=new component_load_source_container();
		
		original_render							=null;
		system_boftal_container					=null;
		part_loader_cont						=new part_loader_container();
		scene_kernel_container_search_tree_lock	=new ReentrantLock();
	}
}
