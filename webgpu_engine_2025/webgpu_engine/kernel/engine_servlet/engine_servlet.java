package engine_servlet;

import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
	
	public engine_configure_files get_engine_configure_files(ServletConfig config)
	{
		return null;
	};
	
	public void init(ServletConfig config) throws ServletException 
	{
		engine_configure_files configure_file;
		
		if(scene!=null) {
			scene.destroy();
			scene=null;
		}
		if((configure_file=get_engine_configure_files(config))==null){
			debug_information.println("scene configure file is NULL");
			System.exit(0);
		}else if(configure_file.configure_files_exist_flag)
			scene=new system_scene(
				configure_file.scene_data_path_name,
				configure_file.scene_temparatory_path_name,
				configure_file.scene_environment_path_name);
		else{
			debug_information.println("scene configure file NOT exists");
			System.exit(0);
		}
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
