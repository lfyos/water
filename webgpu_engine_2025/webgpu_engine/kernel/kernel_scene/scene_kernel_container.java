package kernel_scene;

import java.util.concurrent.locks.ReentrantLock;

import kernel_common_class.debug_information;
import kernel_network.client_request_response;

public class scene_kernel_container
{
	private volatile int scene_kernel_link_number;
	
	public scene_kernel sk;
	
	public ReentrantLock scene_kernel_container_lock;
	
	public int get_scene_kernel_link_number()
	{
		return scene_kernel_link_number;
	}
	public int modify_scene_kernel_link_number(int modify_number)
	{
		int ret_val;
		
		ReentrantLock my_lock;
		if((my_lock=scene_kernel_container_lock)==null)
			return 0;
		my_lock.lock();
		scene_kernel_link_number+=modify_number;
		ret_val=scene_kernel_link_number;
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
			
			scene_kernel_link_number=0;
			
			my_lock.unlock();
		}
	}
	public scene_kernel_container(String my_scene_name,String my_link_name,
			client_request_response request_response,system_parameter system_par,
			String client_scene_file_name,String client_scene_file_charset)
	{
		scene_kernel_create_parameter create_parameter=new scene_kernel_create_parameter(
				my_scene_name,client_scene_file_name,client_scene_file_charset,system_par);
		if(!(create_parameter.success_load_parameter_flag))
			create_parameter=new scene_kernel_create_parameter(
					null,client_scene_file_name,client_scene_file_charset,system_par);
		
		if(create_parameter.success_load_parameter_flag)
			sk=new scene_kernel(my_scene_name,my_link_name,
					create_parameter,request_response,system_par);
		else {	
			sk=null;
			debug_information.println("Cann't Create scene:	",my_scene_name+"	"+my_link_name);
		}
		scene_kernel_link_number	=0;
		scene_kernel_container_lock	=new ReentrantLock();
	}
}
