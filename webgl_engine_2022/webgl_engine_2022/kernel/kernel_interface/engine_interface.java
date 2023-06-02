package kernel_interface;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.balance_tree;
import kernel_common_class.balance_tree_item;
import kernel_common_class.debug_information;
import kernel_component.component_load_source_container;
import kernel_engine.part_package;
import kernel_engine.system_parameter;
import kernel_engine.engine_statistics;
import kernel_engine.engine_kernel_container;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_loader_container;
import kernel_part.part_container_for_part_search;
import kernel_engine.delete_part_files;
import kernel_render.render_container;

public class engine_interface
{
	class engine_kernel_balance_tree_item extends balance_tree_item
	{
		public String scene_name,link_name;
		public engine_kernel_container engine_kernel_cont;
		public int compare(balance_tree_item t)
		{
			int ret_val=0;
			if(t instanceof engine_kernel_balance_tree_item) {
				engine_kernel_balance_tree_item p=(engine_kernel_balance_tree_item)t;
				if((ret_val=scene_name.compareTo(p.scene_name))==0)
					ret_val=link_name.compareTo(p.link_name);
			}
			return ret_val;
		}
		public void destroy()
		{
			scene_name=null;
			link_name=null;
			if(engine_kernel_cont!=null){
				engine_kernel_cont.destroy();
				engine_kernel_cont=null;
			};
		}
		public engine_kernel_balance_tree_item(String my_scene_name,String my_link_name)
		{
			scene_name=my_scene_name;
			link_name=my_link_name;
			engine_kernel_cont=null;
		}
	}
	
	private balance_tree bt;
	public component_load_source_container component_load_source_cont;
	private render_container original_render;
	private part_loader_container part_loader_cont;
	private ReentrantLock client_interface_lock;
	
	private void load_render_container(client_request_response request_response,system_parameter system_par)
	{
		int part_type_id=0;
		ArrayList<part> part_list_for_delete_file=new ArrayList<part>();//part_container_for_delete_part_file();
		original_render=new render_container();
		part_container_for_part_search pcps=new part_container_for_part_search(new ArrayList<part>());
		original_render.load_shader(true,
			component_load_source_cont,pcps,system_par.last_modified_time,
			system_par.data_root_directory_name+system_par.shader_file_name,
			system_par.local_data_charset,"",part_type_id,system_par,null,request_response);
		pcps.execute_append();
		original_render.load_part(true,1<<part_type_id,1,part_loader_cont,
				system_par,null,pcps,null,null,null,part_list_for_delete_file);
		
		original_render.create_bottom_box_part(pcps,request_response,system_par,null);
		pcps.execute_append();
		original_render.load_part(true,1<<part_type_id,2,part_loader_cont,
				system_par,null,pcps,null,null,null,part_list_for_delete_file);
		
		debug_information.println("Begin create system_part_package");
		original_render.system_part_package=new part_package(
				true,null,null,original_render,part_type_id,system_par,null);
		debug_information.println("End create system_part_package");
		
		delete_part_files.do_delete(part_list_for_delete_file,null,system_par,null);
	}
	private engine_kernel_container get_kernel_container_routine(
			client_request_response request_response,
			String client_scene_file_name,String client_scene_file_charset,
			engine_statistics statistics_engine,system_parameter system_par)
	{
		String scene_name,link_name,request_charset=request_response.implementor.get_request_charset();
		if((scene_name=request_response.get_parameter("scene_name"))==null)
			scene_name="";
		else try {
			scene_name=java.net.URLDecoder.decode(scene_name,request_charset);
			scene_name=java.net.URLDecoder.decode(scene_name,request_charset);
		}catch(Exception e) {
			;
		}
		if((link_name=request_response.get_parameter("link_name"))==null)
			link_name="";
		else try {
			link_name=java.net.URLDecoder.decode(link_name,request_charset);
			link_name=java.net.URLDecoder.decode(link_name,request_charset);
		}catch(Exception e) {
			;
		}
		
		if(link_name.compareTo("")==0)
			link_name=Double.toString(Math.random());

		debug_information.println(request_response.implementor.get_client_id(),"	Create scene");
		debug_information.print  ("scene_name:	",scene_name);
		debug_information.println(",link_name:	",link_name);
		debug_information.print  ("engine_interface engine_kernel_number:	",		statistics_engine.engine_kernel_number);
		debug_information.println("/",system_par.max_engine_kernel_number);
		debug_information.print  ("engine_interface engine_component_number:	",	statistics_engine.engine_component_number);
		debug_information.println("/",system_par.max_engine_component_number);
		
		balance_tree_item bti;
		engine_kernel_balance_tree_item ekbti=new engine_kernel_balance_tree_item(scene_name,link_name);
		
		if(bt==null)
			bt=new balance_tree(ekbti);
		else if((bti=bt.search(ekbti,true,false))!=null) 
			ekbti=(engine_kernel_balance_tree_item)bti;
		
		if(ekbti.engine_kernel_cont!=null) {
			debug_information.println("Found created engine");
			
			ekbti.engine_kernel_cont.link_number++;
			return ekbti.engine_kernel_cont;
		}
		if(   (statistics_engine.engine_kernel_number   >=system_par.max_engine_kernel_number)
			||(statistics_engine.engine_component_number>=system_par.max_engine_component_number))
		{
			debug_information.print  ("Create too many scenes or components:	",scene_name+"	"+link_name);
			return null;
		}
		if((ekbti.engine_kernel_cont=new engine_kernel_container(scene_name,link_name,
			request_response,system_par,client_scene_file_name,client_scene_file_charset,
			original_render,part_loader_cont,statistics_engine)).ek==null)
		{
			debug_information.print  ("Create scene fail:	",scene_name+"	"+link_name);
			return null;
		}
		debug_information.print  ("Create scene success:	",scene_name+"	"+link_name);
		ekbti.engine_kernel_cont.link_number++;
		
		return ekbti.engine_kernel_cont;
	}
	public engine_kernel_container get_kernel_container(client_request_response request_response,
			String client_scene_file_name,String client_scene_file_charset,
			engine_statistics statistics_engine,system_parameter system_par)
	{
		engine_kernel_container ret_val=null;
		client_interface_lock.lock();
		
		if(original_render==null)
			load_render_container(request_response,system_par);
		
		try {
			ret_val=get_kernel_container_routine(request_response,
				client_scene_file_name,client_scene_file_charset,statistics_engine,system_par);
		}catch(Exception e) {
			debug_information.println("get_kernel_container of engine_interface fail");
			debug_information.println(e.toString());
			e.printStackTrace();
			ret_val=null;
		}
		client_interface_lock.unlock();
		return ret_val;
	}
	public void destroy_scene(String my_scene_name,String my_link_name,engine_statistics statistics_engine)
	{
		client_interface_lock.lock();
		while(bt!=null) {
			balance_tree_item original_bti=new engine_kernel_balance_tree_item(my_scene_name,my_link_name);
			balance_tree_item search_bti=bt.search(original_bti,false,false);
			if(search_bti==null)
				break;
			engine_kernel_balance_tree_item ekbtl=(engine_kernel_balance_tree_item)search_bti;
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
							statistics_engine.update_kernel_component_number(-1,
									-1-ekbtl.engine_kernel_cont.ek.component_cont.root_component.component_id);
			ekbtl.destroy();
			debug_information.println("engine_interface deletes scene,scene_name:	",	ekbtl.scene_name+"	,link_name:	"+ekbtl.link_name);
			debug_information.println("engine_interface	engine_kernel_number:	",		statistics_engine.engine_kernel_number);
			debug_information.println("engine_interface	engine_component_number:	",	statistics_engine.engine_component_number);
			break;
		}
		client_interface_lock.unlock();
	}
	private void destroy_engine_kernel_balance_tree(balance_tree ek_bt)
	{
		if(ek_bt!=null){
			destroy_engine_kernel_balance_tree(ek_bt.get_left_child());
			destroy_engine_kernel_balance_tree(ek_bt.get_right_child());
			balance_tree_item bti=ek_bt.get_item();
			if(bti!=null)
				if(bti instanceof engine_kernel_balance_tree_item)
					((engine_kernel_balance_tree_item)bti).destroy();
		}
	}
	public void destroy(engine_statistics statistics_engine)
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
		statistics_engine.engine_kernel_number=0;
		statistics_engine.engine_component_number=0;
		my_client_interface_lock.unlock();
	}
	public engine_interface()
	{
		component_load_source_cont	=new component_load_source_container();
		bt							=null;
		original_render				=null;
		part_loader_cont			=new part_loader_container();
		client_interface_lock		=new ReentrantLock();
	}
}