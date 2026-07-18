package kernel_servlet;

import java.io.File;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;
import kernel_network.network_implementation;
import kernel_scene.system_scene;

public class engine_servlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private system_scene scene;
	
	public engine_servlet() 
	{
		scene=null;
	}
	public void destroy() 
    {
		super.destroy();
		
		if(scene!=null) {
			scene.destroy();
			scene=null;
		}
	}
	public void create_scene(
	    	String scene_data_path_name,
	    	String scene_temparatory_path_name,
	    	String scene_environment_path_name)
	{
		scene_data_path_name		=file_reader.separator(scene_data_path_name);
    	scene_temparatory_path_name	=file_reader.separator(scene_temparatory_path_name);
    	scene_environment_path_name	=file_reader.separator(scene_environment_path_name);
    	
    	debug_information.println("scene_data_path_name:	",scene_data_path_name);
    	debug_information.println("temparatory_path_name:	",scene_temparatory_path_name);
    	debug_information.println("environment_path_name:	",scene_environment_path_name);
    	debug_information.println();
    	
    	if(scene!=null)
			scene.destroy();
    	
    	if(new File(scene_data_path_name).exists())
    		scene=new system_scene(
        			scene_data_path_name,
        			scene_temparatory_path_name,
        			scene_environment_path_name);
    	else {
    		scene=null;
			debug_information.println("scene_data_path_name is NOT exist: ",scene_data_path_name);
			System.exit(0);
		}
    }
	protected void doGet(HttpServletRequest request,HttpServletResponse response)
		throws ServletException,IOException 
	{
		if(scene!=null)
			scene.process_system_call(new network_implementation(request,response));
	}
	protected void doPost(HttpServletRequest request,HttpServletResponse response)
		throws ServletException,IOException 
	{
		if(scene!=null)
			scene.process_system_call(new network_implementation(request,response));
	}
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		if(scene!=null)
			scene.process_option(new network_implementation(request,response));
	}
}
