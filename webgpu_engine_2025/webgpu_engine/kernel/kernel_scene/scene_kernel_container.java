package kernel_scene;

import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_part.part_loader_container;
import kernel_render.render_container;

public class scene_kernel_container
{
	private volatile int link_number;
	
	public scene_kernel sk;
	public boolean initilization_flag;
	
	public volatile ReentrantLock scene_kernel_container_lock;
	
	public int get_link_number()
	{
		return link_number;
	}
	public int update_link_number(int modify_number)
	{
		int ret_val;
		
		ReentrantLock my_lock;
		if((my_lock=scene_kernel_container_lock)==null)
			return 0;
		my_lock.lock();
		link_number+=modify_number;
		ret_val=link_number;
		my_lock.unlock();
		return ret_val;
	}
	public void destroy()
	{
		ReentrantLock my_lock;
		if((my_lock=scene_kernel_container_lock)!=null){
			my_lock.lock();
			if(sk!=null) {
				sk.destroy();
				sk=null;
			}
			if(scene_kernel_container_lock!=null)
				scene_kernel_container_lock=null;
			
			link_number=0;
			
			my_lock.unlock();
		}
	}
	public scene_kernel_container(String my_scene_name,String my_link_name,
			client_request_response request_response,system_parameter system_par,
			String client_scene_file_name,String client_scene_file_charset,
			render_container original_render,part_loader_container my_part_loader_cont)
	{
		scene_kernel_create_parameter create_parameter=new scene_kernel_create_parameter(
				my_scene_name,client_scene_file_name,client_scene_file_charset,system_par);
		if(!(create_parameter.success_load_parameter_flag))
			create_parameter=new scene_kernel_create_parameter(
					null,client_scene_file_name,client_scene_file_charset,system_par);
		
		sk=null;
		if(create_parameter.success_load_parameter_flag)
			sk=new scene_kernel(my_scene_name,my_link_name,create_parameter,
					request_response,system_par,original_render,my_part_loader_cont);
		else	
			debug_information.println("Cann't Create scene:	",my_scene_name+"	"+my_link_name);

		link_number=0;
		initilization_flag=true;
		scene_kernel_container_lock=new ReentrantLock();
	}
}
