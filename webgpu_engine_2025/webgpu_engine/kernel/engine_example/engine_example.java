package engine_example;

import java.io.File;

import engine_servlet.engine_configure_files;
import engine_servlet.engine_servlet;
import jakarta.servlet.ServletConfig;
import kernel_file_manager.file_reader;
import kernel_common_class.common_reader;
import kernel_common_class.debug_information;
import kernel_common_class.class_file_reader;

@jakarta.servlet.annotation.WebServlet(	

	initParams= {
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_scene_servlet_type",
			value	=	"java_configure_parameter"
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
	
	private static final String configure_file_name			="configure.txt";
	private static final String configure_charset_name		="UTF-8";
	
	private static final String scene_data_path_name		="lfy_data_configure_file";
	private static final String scene_temparatory_path_name	="lfy_temparatory_configure_file";
	private static final String scene_environment_path_name	="lfy_environment_configure_file";
	
	private engine_configure_files class_engine_configure_files()
	{
		engine_configure_files ret_val=null;
		common_reader reader=class_file_reader.get_reader(
	    		configure_file_name,getClass(),configure_charset_name);
		if(reader!=null) {
			ret_val=new engine_configure_files(
	    		reader.get_string(),reader.get_string(),reader.get_string());
	    	reader.close();
		}else{	
			debug_information.println(
				"class_configure_parameter is NOT exist: ",
		    	getClass().getName()+"\t"+configure_file_name);
			System.exit(0);
		}
		return ret_val;
	}
	public engine_configure_files get_engine_configure_files(ServletConfig config)
	{
		common_reader reader;
		engine_configure_files ret_val=null;
		String configure_path_name,scene_servlet_type;
		
		scene_servlet_type=config.getInitParameter("lfy_scene_servlet_type");
		scene_servlet_type=(scene_servlet_type==null)?"":scene_servlet_type;
		switch(scene_servlet_type){
		case "servlet_initialization_parameter":
			ret_val=new engine_configure_files(
							config.getInitParameter(scene_data_path_name),
				    		config.getInitParameter(scene_temparatory_path_name),
				    		config.getInitParameter(scene_environment_path_name));
			break;
		case "environment_variable_parameter":
			ret_val=new engine_configure_files(
							System.getenv(scene_data_path_name),
							System.getenv(scene_temparatory_path_name),
							System.getenv(scene_environment_path_name));
			break;
		case "webserver_configure_parameter":
			configure_path_name=config.getServletContext().getRealPath(configure_file_name);
	    	if(configure_path_name==null){
	       		debug_information.println("webserver_configure_file name is null");
	       		System.exit(0);
	       	}else if(new File(configure_path_name).exists()){
	    		reader=new file_reader(configure_path_name,configure_charset_name);
		    	ret_val=new engine_configure_files(
		    			reader.get_string(),reader.get_string(),reader.get_string());
		    	reader.close();
	    	}else {
		    	debug_information.println(
		    		"webserver_configure_file is NOT exist: ",configure_path_name);
		    	System.exit(0);
	    	}
	    	break;
		case "java_configure_parameter":
    		try{
				configure_path_name=getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
				configure_path_name=java.net.URLDecoder.decode(configure_path_name,configure_charset_name);
			}catch(Exception e){
				debug_information.println("jar_configure_parameter get file_path_name fail: ",
	    				getClass().getName()+"\t"+configure_file_name);
				debug_information.println("Exception:	"+e.toString());
				
	       		ret_val=class_engine_configure_files();
	       		break;
			}
			configure_path_name=file_reader.separator(configure_path_name);
			configure_path_name=configure_path_name.substring(
					0,configure_path_name.lastIndexOf(File.separatorChar)+1);
			configure_path_name+=file_reader.separator(configure_file_name);
			
			if(new File(configure_path_name).exists()){
				reader=new file_reader(configure_path_name,configure_charset_name);
				ret_val=new engine_configure_files(
		    			reader.get_string(),reader.get_string(),reader.get_string());
		    	reader.close();
		    	break;
			}
			debug_information.println("jar_configure_parameter is NOT exist: ",
	    			getClass().getName()+"\t"+configure_file_name);
			ret_val=class_engine_configure_files();
       		break;
    	default:
			debug_information.println("scene_servlet_type error: ",scene_servlet_type);
			System.exit(0);
    		break;
		}
		return ret_val;
	}
}
