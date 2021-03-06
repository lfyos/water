package kernel_interface;

import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.debug_information;
import kernel_engine.engine_kernel_link_list;
import kernel_engine.system_parameter;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_part.part_loader_container;
import kernel_render.render_container;
import kernel_security.client_session;
import kernel_engine.part_package;

public class engine_interface
{
	public int engine_current_number[];
	
	private engine_kernel_link_list engine_kernel_link_list_first;
	
	private render_container original_render;
	private part_loader_container part_loader_cont;
	
	private ReentrantLock engine_interface_lock;
	
	public void destroy()
	{
		if(engine_current_number!=null)
			engine_current_number=null;
		
		if(engine_kernel_link_list_first!=null){
			engine_kernel_link_list_first.forced_destroy();
			engine_kernel_link_list_first=null;
		}
		if(original_render!=null){
			original_render.destroy();
			original_render=null;
		}
		if(part_loader_cont!=null){
			part_loader_cont.destroy();
			part_loader_cont=null;
		}
		if(engine_interface_lock!=null)
			engine_interface_lock=null;
	}
	
	private boolean clear_expire_engine_kernel_routine(system_parameter system_par)
	{
		int my_current_component_number=0,my_current_engine_kernel_number=0;
		int my_reset_engine_kernel_number=0,my_reset_component_number=0;
		
		engine_kernel_link_list p=engine_kernel_link_list_first,q;
		for(engine_kernel_link_list_first=null;p!=null;){
			q=p;
			p=p.next_link_list;
			q.next_link_list=null;
			if(q.get_link_number()<=0){
				my_reset_engine_kernel_number++;
				debug_information.print("Delete unused scene");
				if(q.ek!=null){
					debug_information.print(", scene name is ",	q.ek.scene_name);
					debug_information.print(", link name is ",	q.ek.link_name);
					debug_information.print(", scene title is ",q.ek.title);
					
					if(q.ek.component_cont!=null)
						if(q.ek.component_cont.root_component!=null)
							my_reset_component_number+=q.ek.component_cont.root_component.component_id+1;
				}
				debug_information.println();
				q.destroy();
			}else{
				q.next_link_list=engine_kernel_link_list_first;
				engine_kernel_link_list_first=q;
				if(q.ek!=null)
					if(q.ek.component_cont!=null)
						if(q.ek.component_cont.root_component!=null) {
							my_current_engine_kernel_number++;
							my_current_component_number+=q.ek.component_cont.root_component.component_id+1;
						}
			}
		}
		debug_information.println();
		debug_information.print  ("Engine number			:	",my_current_engine_kernel_number);
		debug_information.print  ("/",my_reset_engine_kernel_number);	
		debug_information.println("/",system_par.max_engine_kernel_number);	
		debug_information.print  ("Component number		:	",my_current_component_number);
		debug_information.print  ("/",my_reset_component_number);
		debug_information.println("/",system_par.max_component_number);
		debug_information.println();

		engine_current_number=new int[] {my_current_engine_kernel_number,my_current_component_number};
		if(my_current_component_number<system_par.max_component_number)
			if(my_current_engine_kernel_number<system_par.max_engine_kernel_number)
				return false;
		
		debug_information.println(
				"create engine fail,has created too many engines or too many conponents");
		return true;
	}
	
	private engine_kernel_link_list get_kernel_container_routine(client_session session,
			client_request_response request_response,system_parameter system_par)
	{
		if(original_render==null){
			int part_type_id=0;
			original_render=new render_container();
			original_render.load_shader(system_par.last_modified_time,
					system_par.data_root_directory_name+system_par.shader_file_name,
					system_par.local_data_charset,"",part_type_id,
					null,system_par,null,request_response);
			original_render.create_bottom_box_part(request_response,system_par);
			original_render.load_part(part_loader_cont,system_par,null,
					new part_container_for_part_search(original_render.part_array(true,-1)));
			
			debug_information.println("Begin create system_part_package");
			original_render.system_part_package=new part_package(original_render,part_type_id,system_par,null);
			debug_information.println("End create system_part_package");
		}
		
		if(clear_expire_engine_kernel_routine(system_par))
			return null;
		String scene_name,link_name;
		if((scene_name=request_response.get_parameter("scene_name"))==null)
			scene_name="";
		if((link_name=request_response.get_parameter("link_name"))==null)
			link_name="";
		if(link_name.compareTo("")==0)
			link_name=Double.toString(Math.random());

		debug_information.println("scene_name			:	",scene_name);
		debug_information.println("link_name			:	",link_name);

		for(engine_kernel_link_list p=engine_kernel_link_list_first;p!=null;p=p.next_link_list){	
			if(p.ek==null)
				continue;
			if(p.ek.scene_name.compareTo(scene_name)!=0)
				continue;
			if(p.ek.link_name.compareTo(link_name)!=0)
				continue;
			debug_information.print  ("Found created engine			:	");
			debug_information.print  ("	",scene_name);
			debug_information.println("	",link_name);
			
			p.increase_link_number();
			
			return p;
		}

		debug_information.println();
		debug_information.println(
			request_response.implementor.get_client_id(),"	No created engine found, create it!");
		debug_information.println();
		
		engine_kernel_link_list_first=new engine_kernel_link_list(system_par,
					session,request_response,original_render,part_loader_cont,
					scene_name,link_name,engine_kernel_link_list_first);

		if(engine_kernel_link_list_first.ek!=null){
			engine_kernel_link_list_first.increase_link_number();
			return engine_kernel_link_list_first;
		}
		engine_kernel_link_list_first.destroy();
		return null;
	}
	
	public engine_kernel_link_list get_kernel_container(client_session session,
			client_request_response request_response,system_parameter system_par)
	{
		engine_kernel_link_list ret_val=null;
		engine_interface_lock.lock();
		try {
			ret_val=get_kernel_container_routine(session,request_response,system_par);
		}catch(Exception e) {
			debug_information.println(
					"get_kernel_container of engine_interface fail");
			debug_information.println(e.toString());
			e.printStackTrace();
			ret_val=null;
		}
		engine_interface_lock.unlock();
		
		return ret_val;
	}
	public engine_interface()
	{
		engine_current_number				=new int[]{0,0};
		engine_kernel_link_list_first		=null;

		original_render						=null;
		part_loader_cont					=new part_loader_container();
		
		engine_interface_lock				=new ReentrantLock();
	}
}