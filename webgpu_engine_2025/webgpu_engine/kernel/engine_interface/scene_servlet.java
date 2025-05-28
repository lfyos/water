package engine_interface;

import java.io.IOException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kernel_network.network_implementation;
import kernel_scene.system_scene;

public class scene_servlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private String configure_name;
	private system_scene scene;
	
    public scene_servlet(String my_configure_name) 
    {
    	scene=null;
    	configure_name=my_configure_name;
    }
    public void destroy() 
    {
		super.destroy();
		if(scene!=null) {
			scene.destroy();
			scene=null;
		}
	}
    public void init(ServletConfig config) throws ServletException 
    {
    	scene=new system_scene(config.getInitParameter(configure_name));
	}
	protected void doGet(HttpServletRequest request,HttpServletResponse response)
		throws ServletException,IOException 
	{
		scene.process_system_call(new network_implementation(request,response));
	}
	protected void doPost(HttpServletRequest request,HttpServletResponse response)
		throws ServletException,IOException 
	{
		scene.process_system_call(new network_implementation(request,response));
	}
}
