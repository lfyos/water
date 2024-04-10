package kernel_interface;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.balance_tree;
import kernel_common_class.balance_tree_item;
import kernel_common_class.debug_information;
import kernel_component.component_load_source_container;
import kernel_engine.part_package;
import kernel_engine.system_parameter;
import kernel_file_manager.file_directory;
import kernel_engine.create_engine_counter;
import kernel_engine.engine_kernel_container;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.buffer_object_file_modify_time_and_length_container;
import kernel_part.part_loader_container;
import kernel_part.permanent_part_id_encoder;
import kernel_part.part_container_for_part_search;
import kernel_engine.delete_part_files;
import kernel_render.render_container;

public class engine_interface_container
{
	class engine_kernel_balance_tree_item extends balance_tree_item<String[]>
	{
		public engine_kernel_container engine_kernel_cont;
		
		public int compare(String[] t)
		{
			int ret_val=compare_data[0].compareTo(t[0]);
			return (ret_val!=0)?ret_val:compare_data[1].compareTo(t[1]);
		}
		public void destroy()
		{
			super.destroy();
			
			if(engine_kernel_cont!=null){
				engine_kernel_cont.destroy();
				engine_kernel_cont=null;
			}
		}
		public engine_kernel_balance_tree_item(String my_scene_name,String my_link_name)
		{
			super(new String[] {my_scene_name,my_link_name});
			engine_kernel_cont=null;
		}
	}
	
	private balance_tree<String[],engine_kernel_balance_tree_item> bt;
	private render_container original_render;
	public component_load_source_container component_load_source_cont;
	public buffer_object_file_modify_time_and_length_container system_boftal_container;
	private part_loader_container part_loader_cont;
	private ReentrantLock client_interface_lock;
	
	private void load_render_container(client_request_response request_response,system_parameter system_par)
	{
		int part_type_id=0;
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
				system_par,null,null,part_list_for_delete_file,null,null);
		
		original_render.create_bottom_box_part(pcps,request_response,encoder,system_par,null);
		pcps.execute_append();
		original_render.load_part(((long)1)<<part_type_id,2,part_loader_cont,
				system_par,null,null,part_list_for_delete_file,null,null);
		
		debug_information.println("Begin create system_part_package");
		original_render.system_part_package=new part_package(null,null,null,
				original_render,part_type_id,system_par,null);
		
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

		debug_information.println(request_response.implementor.get_client_id(),"	Create scene");
		debug_information.print  ("scene_name:	",scene_name);
		debug_information.println(",link_name:	",link_name);
		debug_information.print  ("engine_interface engine_kernel_number:	",		engine_counter.engine_kernel_number);
		debug_information.println("/",system_par.max_engine_kernel_number);
		debug_information.print  ("engine_interface engine_component_number:	",	engine_counter.engine_component_number);
		debug_information.println("/",system_par.max_engine_component_number);
		
		engine_kernel_balance_tree_item bti;
		engine_kernel_balance_tree_item ekbti=new engine_kernel_balance_tree_item(scene_name,link_name);
		
		if(bt==null)
			bt=new balance_tree<String[],engine_kernel_balance_tree_item>(ekbti);
		else if((bti=bt.search(ekbti,true,false))!=null)
			ekbti=bti;
		
		if(ekbti.engine_kernel_cont!=null) {
			debug_information.println("Found created engine");
			
			ekbti.engine_kernel_cont.link_number++;
			return ekbti.engine_kernel_cont;
		}
		if(   (engine_counter.engine_kernel_number   >=system_par.max_engine_kernel_number)
			||(engine_counter.engine_component_number>=system_par.max_engine_component_number))
		{
			debug_information.println("Create too many scenes or components:	",scene_name+"	"+link_name);
			destroy_scene_routine(scene_name,link_name,engine_counter);
			return null;
		}
		ekbti.engine_kernel_cont=new engine_kernel_container(scene_name,link_name,request_response,system_par,
			client_scene_file_name,client_scene_file_charset,original_render,part_loader_cont);
		
		if(ekbti.engine_kernel_cont.ek==null){
			debug_information.println("Create scene fail:	",scene_name+"	"+link_name);
			destroy_scene_routine(scene_name,link_name,engine_counter);
			return null;
		}
		debug_information.println("Create scene success:	",scene_name+"	"+link_name);
		ekbti.engine_kernel_cont.link_number++;
		
		return ekbti.engine_kernel_cont;
	}
	public engine_kernel_container create_engine_kernel_container(
			client_request_response request_response,
			String client_scene_file_name,String client_scene_file_charset,
			create_engine_counter engine_counter,system_parameter system_par)
	{
		engine_kernel_container ret_val=null;
		client_interface_lock.lock();
		
		if(original_render==null)
			load_render_container(request_response,system_par);
		
		try {
			ret_val=create_engine_kernel_container_routine(request_response,
				client_scene_file_name,client_scene_file_charset,engine_counter,system_par);
		}catch(Exception e) {
			debug_information.println("get_kernel_container of engine_interface fail");
			debug_information.println(e.toString());
			e.printStackTrace();
			ret_val=null;
		}
		client_interface_lock.unlock();
		return ret_val;
	}
	private void destroy_scene_routine(String my_scene_name,String my_link_name,create_engine_counter engine_counter)
	{
		for(engine_kernel_balance_tree_item ekbtl,original_bti;bt!=null;) {
			original_bti=new engine_kernel_balance_tree_item(my_scene_name,my_link_name);
			if((ekbtl=bt.search(original_bti,false,false))==null)
				break;
			if(ekbtl.engine_kernel_cont!=null)
				if((--(ekbtl.engine_kernel_cont.link_number))>0)
					break;
			if((bt.get_left_child()==null)&&(bt.get_right_child()==null))	
				bt=null;
			else
				bt.search(original_bti,false,true);
			
			if(ekbtl.engine_kernel_cont!=null)
				if(ekbtl.engine_kernel_cont.ek!=null)
					if(ekbtl.engine_kernel_cont.ek.component_cont!=null)
						if(ekbtl.engine_kernel_cont.ek.component_cont.root_component!=null)
							engine_counter.update_kernel_component_number(-1,
									-1-ekbtl.engine_kernel_cont.ek.component_cont.root_component.component_id);
			ekbtl.destroy();
			
			debug_information.println("engine_interface deletes scene,scene_name: ",my_scene_name+",link_name: "+my_link_name);
			debug_information.println("engine_interface engine_kernel_number: ",	engine_counter.engine_kernel_number);
			debug_information.println("engine_interface engine_component_number: ",	engine_counter.engine_component_number);
		}
	}
	public void destroy_scene(String my_scene_name,String my_link_name,create_engine_counter engine_counter)
	{
		client_interface_lock.lock();
		destroy_scene_routine(my_scene_name,my_link_name,engine_counter);
		client_interface_lock.unlock();
	}
	private void destroy_engine_kernel_balance_tree(
			balance_tree<String[],engine_kernel_balance_tree_item> ek_bt)
	{
		if(ek_bt!=null){
			destroy_engine_kernel_balance_tree(ek_bt.get_left_child());
			destroy_engine_kernel_balance_tree(ek_bt.get_right_child());
			engine_kernel_balance_tree_item bti;
			if((bti=ek_bt.get_item())!=null)
				bti.destroy();
		}
	}
	public void destroy()
	{
		ReentrantLock my_client_interface_lock=client_interface_lock;
		client_interface_lock=null;
		my_client_interface_lock.lock();
		if(bt!=null) {
			destroy_engine_kernel_balance_tree(bt);
			bt.destroy();
			bt=null;
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
		my_client_interface_lock.unlock();
	}
	public engine_interface_container()
	{
		component_load_source_cont	=new component_load_source_container();
		bt							=null;
		original_render				=null;
		system_boftal_container		=null;
		part_loader_cont			=new part_loader_container();
		client_interface_lock		=new ReentrantLock();
	}
}