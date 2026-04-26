package engine_interface;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kernel_scene.system_scene;
import kernel_file_manager.file_reader;
import kernel_common_class.common_reader;
import kernel_common_class.debug_information;
import kernel_network.network_implementation;
import kernel_common_class.class_file_reader;

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
		
    	debug_information.println("scene_servlet_type:		",	scene_servlet_type);
    	debug_information.println("scene_data_path_name:	",	scene_data_path_name);
    	debug_information.println("temparatory_path_name:	",	scene_temparatory_path_name);
    	debug_information.println("environment_path_name:	",	scene_environment_path_name);
    	debug_information.println();
    	
    	switch(scene_servlet_type) {
    	case "servlet_initialization_parameter":
    		my_scene_data_path_name			=config.getInitParameter(scene_data_path_name);
    		my_scene_temparatory_path_name	=config.getInitParameter(scene_temparatory_path_name);
    		my_scene_environment_path_name	=config.getInitParameter(scene_environment_path_name);
    		
    		break;
    	case "system_environment_variable":
    		my_scene_data_path_name			=System.getenv(scene_data_path_name);
    		my_scene_temparatory_path_name	=System.getenv(scene_temparatory_path_name);
    		my_scene_environment_path_name	=System.getenv(scene_environment_path_name);
 
    		break;
    	case "webserver_configure_file":
	    	{
	    		String configure_file_name;
	    		String configure_file_charset=(scene_temparatory_path_name!=null)
		    			?scene_temparatory_path_name:Charset.defaultCharset().name();
	    		if((configure_file_name=config.getServletContext().getRealPath(scene_data_path_name))==null){
	        		debug_information.println("webserver_configure_file name is null");
	        		System.exit(0);
	        		return;
	        	}
	    		if(!(new File(configure_file_name).exists())) {
	    			debug_information.println("webserver_configure_file is NOT exist,its file_name is ",
	    					configure_file_name);
	    			System.exit(0);
	    			return;
	    		}
	    		
	    		debug_information.println("webserver_configure_file is	",configure_file_name);
	    		debug_information.println("configure_file_charset is	",configure_file_charset);
	    		
	    		file_reader fr=new file_reader(configure_file_name,configure_file_charset);
	    		my_scene_data_path_name			=fr.get_string();
	    		my_scene_temparatory_path_name	=fr.get_string();
	    		my_scene_environment_path_name	=fr.get_string();
	    		fr.close();
	    	}
	    	break;
    	case "class_configure_file":
	    	{
	    		common_reader reader=class_file_reader.get_reader(
	    			scene_data_path_name,getClass(),
	    			(scene_temparatory_path_name!=null)
	    				?scene_temparatory_path_name:Charset.defaultCharset().name(),
	    			(scene_environment_path_name!=null)
	    				?scene_environment_path_name:Charset.defaultCharset().name());
	    		my_scene_data_path_name			=reader.get_string();
	    		my_scene_temparatory_path_name	=reader.get_string();
	    		my_scene_environment_path_name	=reader.get_string();
	    		reader.close();
	    	}
	    	break;
    	case "jar_configure_file":
	    	{
	    		String path_name=class_file_reader.get_file_path(
	    			scene_data_path_name,getClass(),
	    			(scene_temparatory_path_name!=null)
    					?scene_temparatory_path_name
    					:Charset.defaultCharset().name());
	    		file_reader fr=new file_reader(path_name,
	    			(scene_environment_path_name!=null)
	    				?scene_environment_path_name
	    				:Charset.defaultCharset().name());
	    		my_scene_data_path_name			=fr.get_string();
	    		my_scene_temparatory_path_name	=fr.get_string();
	    		my_scene_environment_path_name	=fr.get_string();
	    		fr.close();
	    	}
	    	break;
    	default:
    		my_scene_data_path_name			=scene_data_path_name;
    		my_scene_temparatory_path_name	=scene_temparatory_path_name;
    		my_scene_environment_path_name	=scene_environment_path_name;
    		break;
    	}
    	
    	my_scene_data_path_name			=file_reader.separator(my_scene_data_path_name);
    	my_scene_temparatory_path_name	=file_reader.separator(my_scene_temparatory_path_name);
    	my_scene_environment_path_name	=file_reader.separator(my_scene_environment_path_name);
    	
    	debug_information.println("scene_servlet_type:		",scene_servlet_type);
    	debug_information.println("scene_data_path_name:	",my_scene_data_path_name);
    	debug_information.println("temparatory_path_name:	",my_scene_temparatory_path_name);
    	debug_information.println("environment_path_name:	",my_scene_environment_path_name);
    	debug_information.println();
    	
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
