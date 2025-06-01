package engine_interface;

import java.io.IOException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kernel_common_class.debug_information;
import kernel_scene.system_scene;
import kernel_network.network_implementation;

public class scene_servlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private String scene_servlet_type;
	private String scene_data_path_name,scene_temparatory_path_name,scene_environment_path_name;
	private system_scene scene;
	
    public scene_servlet(	
    			String my_scene_servlet_type,			String my_scene_data_path_name,
    			String my_scene_temparatory_path_name,	String my_scene_environment_path_name) 
    {
    	scene_servlet_type			=my_scene_servlet_type;
    	scene_data_path_name		=my_scene_data_path_name;
    	scene_temparatory_path_name	=my_scene_temparatory_path_name;
    	scene_environment_path_name	=my_scene_environment_path_name;
    	
    	debug_information.println("scene_servlet_type:	",			scene_servlet_type);
    	debug_information.println("scene_data_path_name:	",		scene_data_path_name);
    	debug_information.println("scene_temparatory_path_name:	",	scene_temparatory_path_name);
    	debug_information.println("scene_environment_path_name:	",	scene_environment_path_name);
    	
    	scene=null;
    }
    public void destroy() 
    {
		super.destroy();
		
		scene_servlet_type			=null;
		scene_data_path_name		=null;
    	scene_temparatory_path_name	=null;
    	scene_environment_path_name	=null;

		if(scene!=null) {
			scene.destroy();
			scene=null;
		}
	}
    public void init(ServletConfig config) throws ServletException 
    {
    	String my_scene_data_path_name,my_scene_temparatory_path_name,my_scene_environment_path_name;
		
    	switch(scene_servlet_type) {
    	default:
    		my_scene_data_path_name			=scene_data_path_name;
    		my_scene_temparatory_path_name	=scene_temparatory_path_name;
    		my_scene_environment_path_name	=scene_environment_path_name;
    		break;
    	case "servlet_initialization_parameter":
    		my_scene_data_path_name			=config.getInitParameter(scene_data_path_name);
    		my_scene_temparatory_path_name	=config.getInitParameter(scene_temparatory_path_name);
    		my_scene_environment_path_name	=config.getInitParameter(scene_environment_path_name);
    		
    		debug_information.println("servlet:scene_servlet_type:	",			scene_servlet_type);
        	debug_information.println("servlet:scene_data_path_name:	",		my_scene_data_path_name);
        	debug_information.println("servlet:scene_temparatory_path_name:	",	my_scene_temparatory_path_name);
        	debug_information.println("servlet:scene_environment_path_name:	",	my_scene_environment_path_name);

    		break;
    	case "system_environment_variable":
    		my_scene_data_path_name			=System.getenv(scene_data_path_name);
    		my_scene_temparatory_path_name	=System.getenv(scene_temparatory_path_name);
    		my_scene_environment_path_name	=System.getenv(scene_environment_path_name);
 
    		debug_information.println("environment:scene_servlet_type:	",			scene_servlet_type);
        	debug_information.println("environment:scene_data_path_name:	",		my_scene_data_path_name);
        	debug_information.println("environment:scene_temparatory_path_name:	",	my_scene_temparatory_path_name);
        	debug_information.println("environment:scene_environment_path_name:	",	my_scene_environment_path_name);
  
    		break;
    	}
    	scene=new system_scene(my_scene_data_path_name,
				my_scene_temparatory_path_name,my_scene_environment_path_name);
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
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		scene.process_option(new network_implementation(request,response));
	}
}
