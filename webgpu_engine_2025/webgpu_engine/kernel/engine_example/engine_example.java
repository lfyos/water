package engine_example;

import java.io.File;
import jakarta.servlet.ServletConfig;

import kernel_scene.engine_servlet;
import kernel_file_manager.file_reader;
import kernel_common_class.common_reader;
import kernel_common_class.debug_information;
import kernel_common_class.class_file_reader;

@jakarta.servlet.annotation.WebServlet(	

	initParams= {
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_scene_servlet_type",
			value	=	"class_configure_file"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_data_configure_file",
			value	=	"G:/water_all/data/configure.txt"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_temparatory_configure_file",
			value	=	"G:/temp/configure.txt"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_environment_configure_file",
			value	=	"G:/water_all/environment.txt"
		)
	},
	
	asyncSupported = true,
	urlPatterns = { 
		"/water" 
	}
)
public class engine_example extends engine_servlet
{
	private static final long serialVersionUID = 1L;
	
	protected String[]get_engine_configure_files(ServletConfig config)
	{
		String scene_data_path_name;
		String scene_temparatory_path_name;
		String scene_environment_path_name;
		
		String scene_servlet_type=config.getInitParameter("lfy_scene_servlet_type");
		
		switch((scene_servlet_type==null)?"":scene_servlet_type){
		default:
    		scene_data_path_name		="G:/water_all/data/configure.txt";
    		scene_temparatory_path_name	="G:/temp/configure.txt";
    		scene_environment_path_name	="G:/water_all/environment.txt";
    		break;
		case "servlet_initialization_parameter":
    		scene_data_path_name		=config.getInitParameter("lfy_data_configure_file");
    		scene_temparatory_path_name	=config.getInitParameter("lfy_temparatory_configure_file");
    		scene_environment_path_name	=config.getInitParameter("lfy_environment_configure_file");
    		break;
		case "system_environment_variable":
    		scene_data_path_name		=System.getenv("lfy_data_configure_file");
    		scene_temparatory_path_name	=System.getenv("lfy_temparatory_configure_file");
    		scene_environment_path_name	=System.getenv("lfy_environment_configure_file");
    		break;
    	case "webserver_configure_file":
	    	{
	    		String configure_file_name;
	    		if((configure_file_name=config.getServletContext().getRealPath("configure.txt"))==null){
	        		debug_information.println("webserver_configure_file name is null");
	        		return null;
	        	}
	    		if(!(new File(configure_file_name).exists())) {
	    			debug_information.println("webserver_configure_file is NOT exist: ",configure_file_name);
	    			return null;
	    		}
	    		file_reader fr=new file_reader(configure_file_name,"UTF-8");
	    		scene_data_path_name		=fr.get_string();
	    		scene_temparatory_path_name	=fr.get_string();
	    		scene_environment_path_name	=fr.get_string();
	    		fr.close();
	    		break;
	    	}	
    	case "class_configure_file":
	    	{
	    		common_reader reader=class_file_reader.get_reader("configure.txt",getClass(),"UTF-8","UTF-8");
	    		scene_data_path_name		=reader.get_string();
	    		scene_temparatory_path_name	=reader.get_string();
	    		scene_environment_path_name	=reader.get_string();
	    		reader.close();
	    		break;
	    	}
    	case "jar_configure_file":
			{
	    		String path_name=class_file_reader.get_file_path("configure.txt",getClass(),"UTF-8");
	    		file_reader fr=new file_reader(path_name,"UTF-8");
	    		scene_data_path_name		=fr.get_string();
	    		scene_temparatory_path_name	=fr.get_string();
	    		scene_environment_path_name	=fr.get_string();
	    		fr.close();
	    		break;
	    	}
		}
		return new String[] {scene_data_path_name,scene_temparatory_path_name,scene_environment_path_name};
	}
}
