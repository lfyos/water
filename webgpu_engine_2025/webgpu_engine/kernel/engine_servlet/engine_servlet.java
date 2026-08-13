package engine_servlet;

import java.io.File;
import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kernel_scene.system_scene;
import kernel_common_class.class_file_reader;
import kernel_common_class.common_reader;
import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_network.network_implementation;

public class engine_servlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private system_scene scene;
	private String init_scene_configure_class_name;
	private String init_scene_configure_file_name;
	private String init_scene_configure_charset_name;
	
	public engine_servlet(String my_init_scene_configure_class_name,
			String my_init_scene_configure_file_name,String my_init_scene_configure_charset_name) 
	{
		scene=null;
		
		init_scene_configure_class_name		=my_init_scene_configure_class_name;
		init_scene_configure_file_name		=my_init_scene_configure_file_name;
		init_scene_configure_charset_name	=my_init_scene_configure_charset_name;
	}
	public void destroy() 
    {
		if(scene!=null) {
			scene.destroy();
			scene=null;
		}
		super.destroy();
	}
	private String[] read_configure_files(common_reader reader,ServletConfig config)
	{
		String str,scene_data_path_name=null,scene_temparatory_path_name=null,scene_environment_path_name=null;
		
		switch(((str=reader.get_string())==null)?"":str.trim().toLowerCase()){
		case "java_configure_parameter":
			scene_data_path_name		=((str=reader.get_string())==null)?"":str;
			scene_temparatory_path_name	=((str=reader.get_string())==null)?"":str;
			scene_environment_path_name	=((str=reader.get_string())==null)?"":str;
			break;
		case "servlet_initialization_parameter":
			scene_data_path_name		=config.getInitParameter(((str=reader.get_string())==null)?"":str);
			scene_temparatory_path_name	=config.getInitParameter(((str=reader.get_string())==null)?"":str);
			scene_environment_path_name	=config.getInitParameter(((str=reader.get_string())==null)?"":str);
			break;
		case "environment_variable_parameter":
			scene_data_path_name		=System.getenv(((str=reader.get_string())==null)?"":str);
			scene_temparatory_path_name	=System.getenv(((str=reader.get_string())==null)?"":str);
			scene_environment_path_name	=System.getenv(((str=reader.get_string())==null)?"":str);
			break;
		case "webserver_configure_parameter":
			String configure_path_name=config.getServletContext().getRealPath(((str=reader.get_string())==null)?"":str);
	    	if(configure_path_name==null)
	    		debug_information.println("webserver_configure_file is NOT exist: ",str);
	    	else if(new File(configure_path_name).exists()){
	    		str=((str=reader.get_string())==null)?"":str;
		    	file_reader fr=new file_reader(configure_path_name,str);
		    	scene_data_path_name		=fr.get_string();
				scene_temparatory_path_name	=fr.get_string();
				scene_environment_path_name	=fr.get_string();
			    fr.close();
			    break;
			}else
	    		debug_information.println("webserver_configure_file is NOT exist: ",configure_path_name);
	    	break;
		default:
			break;
		}

		scene_data_path_name		=(scene_data_path_name==null)		?"":scene_data_path_name;
		scene_temparatory_path_name	=(scene_temparatory_path_name==null)?"":scene_temparatory_path_name;
		scene_environment_path_name	=(scene_environment_path_name==null)?"":scene_environment_path_name;
		
		scene_data_path_name		=file_reader.separator(scene_data_path_name);
		scene_temparatory_path_name	=file_reader.separator(scene_temparatory_path_name);
		scene_environment_path_name	=file_reader.separator(scene_environment_path_name);
 
		debug_information.println();
    	debug_information.println("scene_data_path_name:	",scene_data_path_name);
        debug_information.println("temparatory_path_name:	",scene_temparatory_path_name);
        debug_information.println("environment_path_name:	",scene_environment_path_name);
        debug_information.println();
        
		return new String[] {scene_data_path_name,scene_temparatory_path_name,scene_environment_path_name};
	}
	private String[] get_engine_configure_files(ServletConfig config)
	{
		String jar_configure_files[]=null,class_configure_files[]=null;
		
		String scene_configure_class_name	=init_scene_configure_class_name;
		String scene_configure_file_name	=init_scene_configure_file_name;
		String scene_configure_charset_name	=init_scene_configure_charset_name;
		
		debug_information.println();
    	debug_information.println("scene_configure_class_name:		",	scene_configure_class_name);
        debug_information.println("scene_configure_file_name:		",	scene_configure_file_name);
        debug_information.println("scene_configure_charset_name:	",	scene_configure_charset_name);
        debug_information.println();
		
		Class<?>engine_class;
		try {
			engine_class=Class.forName(scene_configure_class_name);
		}catch(Exception e){
			debug_information.println("get_engine_configure_files() get class exception\t"+scene_configure_class_name);
			return null;
		}
		if(engine_class==null) {
			debug_information.println("get_engine_configure_files() get class return null\t"+scene_configure_class_name);
			return null;
		}
		String configure_path_name;
		try{
			configure_path_name=engine_class.getProtectionDomain().getCodeSource().getLocation().getPath();
			configure_path_name=java.net.URLDecoder.decode(configure_path_name,scene_configure_charset_name);
		}catch(Exception e){
			debug_information.println(
				"get_engine_configure_files() get jar path name exception: ",scene_configure_class_name);
			debug_information.println("Exception:	"+e.toString());
			configure_path_name=null;
		}
		if(configure_path_name==null) 
			debug_information.println(
				"get_engine_configure_files() get jar path name is null: ",scene_configure_class_name);
		else{
			configure_path_name=file_reader.separator(configure_path_name);
			int index_id=configure_path_name.lastIndexOf(File.separatorChar);
			if(index_id>=0)
				configure_path_name=configure_path_name.substring(0,index_id+1);
			else
				configure_path_name="";
			configure_path_name+=file_reader.separator(scene_configure_file_name);
			
			if(!(new File(configure_path_name).exists()))
				debug_information.println(
					"get_engine_configure_files() configure_path_name NOT exist: ",configure_path_name);
			else{
				file_reader fr=new file_reader(configure_path_name,scene_configure_charset_name);
				jar_configure_files=read_configure_files(fr,config);
		    	fr.close();
		    	if(jar_configure_files==null)
		    		debug_information.println(
							"get_engine_configure_files() jar_configure_files==null: ",configure_path_name);
		    	else{
		    		String scene_data_path_name=jar_configure_files[0];
		    		if(!(new File(scene_data_path_name).exists())) {
		    			jar_configure_files=null;
		    			debug_information.println(
							"get_engine_configure_files() scene_data_path_name NOT exist: ",scene_data_path_name);
		    		}
		    	}
			}
		}

		common_reader reader=class_file_reader.get_reader(
				scene_configure_file_name,engine_class,scene_configure_charset_name);
		
		if(reader==null)
			debug_information.println("get_engine_configure_files() class configure file is NOT exist: ",
	    		scene_configure_class_name+"\t"+scene_configure_file_name);
		else{
			class_configure_files=read_configure_files(reader,config);
		    reader.close();
		    if(class_configure_files==null)
			    debug_information.println("get_engine_configure_files() return value is null: ",
			    	scene_configure_class_name+"\t"+scene_configure_file_name);
		    else{
		    	String scene_data_path_name=class_configure_files[0];
				if(!(new File(scene_data_path_name).exists())) {
					class_configure_files=null;
					debug_information.println("get_engine_configure_files() file NOT exist: ",
					    	scene_configure_class_name+"\t"+scene_configure_file_name+"\t"+scene_data_path_name);
				}
		    }
		}
		
		return 	(jar_configure_files!=null)?jar_configure_files:
				(class_configure_files!=null)?class_configure_files:null;
	}
	
	public void init(ServletConfig config)
		throws ServletException 
	{	
		if(scene!=null) {
			scene.destroy();
			scene=null;
		}
		String configure_files[];
		if((configure_files=get_engine_configure_files(config))!=null)
			scene=new system_scene(configure_files[0],configure_files[1],configure_files[2]);
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
