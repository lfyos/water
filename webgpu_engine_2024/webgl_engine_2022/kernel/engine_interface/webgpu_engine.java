package engine_interface;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_interface.interface_engine;
import kernel_network.network;

public class webgpu_engine 
{
	private interface_engine engine;
    
    public webgpu_engine() 
    {
        String data_dir_name,temp_dir_name;
        
		if((data_dir_name=System.getenv("lfy_data_dir"))==null) {
			debug_information.println("data_file_configure_directory_name is null,its environment is lfy_data_dir");
			System.exit(0);
			return;
		}
		if((data_dir_name=file_reader.separator(data_dir_name.trim())).length()<=0) {
			debug_information.println("data_file_configure_directory_name is empty,its environment is lfy_data_dir");
			System.exit(0);
			return;
		}
		if(data_dir_name.charAt(data_dir_name.length()-1)!=File.separatorChar)
			data_dir_name+=File.separatorChar;
		
		if((temp_dir_name=System.getenv("lfy_temp_dir"))==null) {
			debug_information.println("temp_file_configure_directory_name is null,its environment is temp_dir");
			System.exit(0);
			return;
		}
		if((temp_dir_name=file_reader.separator(temp_dir_name.trim())).length()<=0) {
			debug_information.println("temp_file_configure_directory_name is empty,its environment is temp_dir");
			System.exit(0);
			return;
		}
		if(temp_dir_name.charAt(temp_dir_name.length()-1)!=File.separatorChar)
			temp_dir_name+=File.separatorChar;
		
		String data_file_name=data_dir_name+"configure.txt";
		String temp_file_name=temp_dir_name+"configure.txt";
		
		if(!(new File(data_file_name).exists())) {
			debug_information.println("data_file_configure_file NOT exist,its environment is lfy_data_dir");
			debug_information.println(data_file_name);
			System.exit(0);
			return;
		}
		engine=new interface_engine(data_file_name,temp_file_name);
    }
    public void process_call(HttpServletRequest request,HttpServletResponse response)
	{
    	engine.process_system_call(new network(request,response));	
	}
}
