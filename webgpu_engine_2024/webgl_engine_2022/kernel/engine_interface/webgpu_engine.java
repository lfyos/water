package engine_interface;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kernel_common_class.debug_information;
import kernel_engine.system_engine;
import kernel_file_manager.file_reader;
import kernel_network.network;

public class webgpu_engine 
{
	private system_engine engine;
	
	public webgpu_engine()
	{
		engine=null;
	}
	synchronized void create(HttpServletRequest request)
	{
		if(engine!=null)
			return;
		
		String configure_file_name=request.getSession().getServletContext().getRealPath("configure.txt");
		if(!(new File(configure_file_name).exists())) {
			debug_information.println("webserver_configure_file is NOT exist,its file_name is ",configure_file_name);
			System.exit(0);
			return;
		}
		file_reader f=new file_reader(configure_file_name,null);
		String data_environment_variable_name=f.get_string();
		String temp_environment_variable_name=f.get_string();
		f.close();
		
		if(data_environment_variable_name==null) {
			debug_information.println("data_environment_variable_name is null");
			System.exit(0);
			return;
		}
		if(temp_environment_variable_name==null) {
			debug_information.println("temp_environment_variable_name is null");
			System.exit(0);
			return;
		}
        String data_dir_name;
		if((data_dir_name=System.getenv(data_environment_variable_name))==null) {
			debug_information.println(
					"data_file_configure_directory_name is null,its environment is ",
					data_environment_variable_name);
			System.exit(0);
			return;
		}
		if((data_dir_name=file_reader.separator(data_dir_name.trim())).length()<=0) {
			debug_information.println(
					"data_file_configure_directory_name is empty,its environment is ",
					data_environment_variable_name);
			System.exit(0);
			return;
		}
		if(data_dir_name.charAt(data_dir_name.length()-1)!=File.separatorChar)
			data_dir_name+=File.separatorChar;
		
		String temp_dir_name;
		if((temp_dir_name=System.getenv(temp_environment_variable_name))==null) {
			debug_information.println(
					"temp_file_configure_directory_name is null,its environment is ",
					temp_environment_variable_name);
			System.exit(0);
			return;
		}
		if((temp_dir_name=file_reader.separator(temp_dir_name.trim())).length()<=0) {
			debug_information.println(
					"temp_file_configure_directory_name is empty,its environment is ",
					temp_environment_variable_name);
			System.exit(0);
			return;
		}
		if(temp_dir_name.charAt(temp_dir_name.length()-1)!=File.separatorChar)
			temp_dir_name+=File.separatorChar;
		
		String data_file_name=data_dir_name+"configure.txt";
		String temp_file_name=temp_dir_name+"configure.txt";
		
		if(!(new File(data_file_name).exists())){
			debug_information.println(
					"data_configure_file is NOT exist,its file_name is ",data_file_name);
			debug_information.println(data_file_name);
			System.exit(0);
			return;
		}
		engine=new system_engine(data_file_name,temp_file_name);
	}
	public void destroy()
	{
		if(engine!=null) {
			engine.destroy();
			engine=null;
		}
	}
    public void process_system_call(HttpServletRequest request,HttpServletResponse response)
	{
    	if(engine==null)
    		create(request);
    	engine.process_system_call(new network(request,response));
	}
}
