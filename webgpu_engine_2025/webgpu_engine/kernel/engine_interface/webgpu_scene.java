package engine_interface;

import java.io.File;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kernel_scene.system_scene;
import kernel_network.network_implementation;
import kernel_common_class.debug_information;

public class webgpu_scene 
{
	private system_scene scene;
	
	public webgpu_scene()
	{
		scene=null;
	}
	synchronized void create(HttpServletRequest request)
	{
		if(scene!=null)
			return;
		String configure_file_name=request.getSession().getServletContext().getRealPath("environment.txt");
		if(new File(configure_file_name).exists())
			scene=new system_scene(configure_file_name);
		else {
			debug_information.println(
				"webserver_configure_file is NOT exist,its file_name is ",configure_file_name);
			System.exit(0);
		}
		return;
	}
	public void destroy()
	{
		if(scene!=null) {
			scene.destroy();
			scene=null;
		}
	}
    public void process_system_call(HttpServletRequest request,HttpServletResponse response)
	{
    	if(scene==null)
    		create(request);
    	scene.process_system_call(new network_implementation(request,response));
	}
}
