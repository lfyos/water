package engine_servlet;

import java.io.File;

import jakarta.servlet.ServletConfig;
import kernel_common_class.class_file_reader;
import kernel_common_class.common_reader;
import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;

public class engine_parameter_servlet extends engine_servlet
{
	private static final long serialVersionUID = 1L;
	
	public Class<?> get_engine_configure_class()
	{
		return null;
	};
	public engine_configure_files get_engine_configure_files(ServletConfig config) 
	{
		String scene_servlet_type;
		if((scene_servlet_type=config.getInitParameter("lfy_scene_servlet_type"))==null)
			scene_servlet_type="";
		String configure_file_name;
		if((configure_file_name=config.getInitParameter("lfy_scene_configure_file"))==null)
			configure_file_name="";
		String configure_charset_name;		
		if((configure_charset_name=config.getInitParameter("lfy_scene_configure_charset"))==null)
			configure_charset_name="UTF-8";
		
		String scene_data_path_name			="lfy_data_configure_file";
		String scene_temparatory_path_name	="lfy_temparatory_configure_file";
		String scene_environment_path_name	="lfy_environment_configure_file";

		String configure_path_name;
		engine_configure_files ret_val=null;
		
		switch((scene_servlet_type==null)?"":scene_servlet_type){
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
	    	if(configure_path_name==null)
	    		debug_information.println("webserver_configure_file is NOT exist: ",configure_path_name);
	    	else if(!(new File(configure_path_name).exists()))
	    		debug_information.println("webserver_configure_file is NOT exist: ",configure_path_name);
	    	else{
		    	file_reader fr=new file_reader(configure_path_name,configure_charset_name);
			    ret_val=new engine_configure_files(fr.get_string(),fr.get_string(),fr.get_string());
			    fr.close();
			}
		    break;
		case "java_configure_parameter":
			Class<?>engine_class;
			if((engine_class=get_engine_configure_class())==null) {
				debug_information.println("get_engine_configure_class()==null\t"+configure_file_name);
				break;
			}
    		try{
				configure_path_name=engine_class.getProtectionDomain().getCodeSource().getLocation().getPath();
				configure_path_name=java.net.URLDecoder.decode(configure_path_name,configure_charset_name);
			}catch(Exception e){
				debug_information.println("jar_configure_parameter get file_path_name fail: ",
	    				engine_class.getName()+"\t"+configure_file_name);
				debug_information.println("Exception:	"+e.toString());
				configure_path_name=null;
			}
    		if(configure_path_name!=null) {
				configure_path_name=file_reader.separator(configure_path_name);
				configure_path_name=configure_path_name.substring(
						0,configure_path_name.lastIndexOf(File.separatorChar)+1);
				configure_path_name+=file_reader.separator(configure_file_name);
				
				if(new File(configure_path_name).exists()){
					file_reader fr=new file_reader(configure_path_name,configure_charset_name);
					ret_val=new engine_configure_files(fr.get_string(),fr.get_string(),fr.get_string());
			    	fr.close();
			    	break;
				}
    		}
			debug_information.println("jar_configure_parameter is NOT exist: ",
	    			engine_class.getName()+"\t"+configure_file_name);
			
			common_reader reader=class_file_reader.get_reader(
		    		configure_file_name,engine_class,configure_charset_name);
			if(reader!=null) {
				ret_val=new engine_configure_files(
						reader.get_string(),reader.get_string(),reader.get_string());
		    	reader.close();
		    	break;
			}	
			debug_information.println("class_configure_parameter is NOT exist: ",
			    	engine_class.getName()+"\t"+configure_file_name);
			break;
    	default:
			debug_information.println("scene_servlet_type error: ",scene_servlet_type);
			break;
		}
		return ret_val;
	}
}
