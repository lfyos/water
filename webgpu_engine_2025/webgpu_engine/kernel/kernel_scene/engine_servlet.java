package kernel_scene;

import java.io.File;
import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;
import kernel_network.network_implementation;

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
	protected String[]get_engine_configure_files(ServletConfig config)
	{
		return null;
	}
	public void init(ServletConfig config) throws ServletException 
	{
		if(scene!=null) {
			scene.destroy();
			scene=null;
		}
		
		String configure_file_array[];
		if((configure_file_array=get_engine_configure_files(config))==null) {
			debug_information.println("configure_file_array is NULL");
			debug_information.println();
			System.exit(0);
			return;
		}
		if(configure_file_array.length<3){
			debug_information.println("configure_file_array.length is ",configure_file_array.length);
			debug_information.println();
			System.exit(0);
			return;
		}
		for(int i=0,str_length;i<3;i++) {
			if(configure_file_array[i]==null) {
				debug_information.println("configure_file_array["+i+"] is NULL");
				debug_information.println();
				System.exit(0);
    			return;
			}
			configure_file_array[i]=configure_file_array[i].trim();
			str_length=configure_file_array[i].length();
			if(str_length<=0) {
				debug_information.println("configure_file_array["+i+"] length is "+str_length);
				debug_information.println();
				System.exit(0);
    			return;
			}
		}
		
		String scene_data_path_name			=file_reader.separator(configure_file_array[0]);
		String scene_temparatory_path_name	=file_reader.separator(configure_file_array[1]);
		String scene_environment_path_name	=file_reader.separator(configure_file_array[2]);
    	
    	debug_information.println("scene_data_path_name:	",scene_data_path_name);
    	debug_information.println("temparatory_path_name:	",scene_temparatory_path_name);
    	debug_information.println("environment_path_name:	",scene_environment_path_name);
    	debug_information.println();
    	
    	if(!(new File(scene_data_path_name).exists())){
			debug_information.println("scene_data_path_name is NOT exist: ",scene_data_path_name);
			System.exit(0);
			return;
		}
    	scene=new system_scene(scene_data_path_name,scene_temparatory_path_name,scene_environment_path_name);
    	return;
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
