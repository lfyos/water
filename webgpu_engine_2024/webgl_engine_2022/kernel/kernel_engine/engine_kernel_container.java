package kernel_engine;

import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_part.part_loader_container;
import kernel_render.render_container;

public class engine_kernel_container
{
	private volatile int link_number;
	
	public engine_kernel ek;
	public boolean initilization_flag;
	
	public volatile ReentrantLock engine_kernel_container_lock;
	
	public int get_link_number()
	{
		return link_number;
	}
	public int update_link_number(int modify_number)
	{
		int ret_val;
		
		ReentrantLock my_engine_kernel_container_lock;
		if((my_engine_kernel_container_lock=engine_kernel_container_lock)==null)
			return 0;
		
		my_engine_kernel_container_lock.lock();
		link_number+=modify_number;
		ret_val=link_number;
		my_engine_kernel_container_lock.unlock();
		return ret_val;
	}
	public void destroy()
	{
		ReentrantLock my_engine_kernel_container_lock;
		if((my_engine_kernel_container_lock=engine_kernel_container_lock)!=null){
			my_engine_kernel_container_lock.lock();
			if(ek!=null) {
				ek.destroy();
				ek=null;
			}
			if(engine_kernel_container_lock!=null)
				engine_kernel_container_lock=null;
			
			link_number=0;
			
			my_engine_kernel_container_lock.unlock();
		}
	}
	public engine_kernel_container(String my_scene_name,String my_link_name,
			client_request_response request_response,system_parameter system_par,
			String client_scene_file_name,String client_scene_file_charset,
			render_container original_render,part_loader_container my_part_loader_cont)
	{
		engine_kernel_create_parameter create_parameter=new engine_kernel_create_parameter(
				my_scene_name,my_link_name,client_scene_file_name,client_scene_file_charset,system_par);
		if(!(create_parameter.success_load_parameter_flag))
			create_parameter=new engine_kernel_create_parameter(
					null,my_link_name,client_scene_file_name,client_scene_file_charset,system_par);
		
		ek=null;
		if(create_parameter.success_load_parameter_flag)
			ek=new engine_kernel(my_scene_name,my_link_name,create_parameter,
					request_response,system_par,original_render,my_part_loader_cont);
		else	
			debug_information.println("Cann't Create scene:	",my_scene_name+"	"+my_link_name);

		link_number=0;
		initilization_flag=true;
		engine_kernel_container_lock=new ReentrantLock();
	}
}
