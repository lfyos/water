package driver_opengl_fixed_pipeline;

import java.io.File;

import kernel_part.part;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_engine.client_information;
import kernel_driver.part_instance_driver;
import kernel_common_class.debug_information;

public class extended_part_instance_driver extends part_instance_driver
{
	public extended_part_instance_driver()
	{
		super();
	}
	public void destroy()
	{
		super.destroy();
	}
	public void response_init_part_data(part p,engine_kernel ek,client_information ci)
	{
	}
	public String[] response_part_event(part p,engine_kernel ek,client_information ci)
	{			
		String directory_name,file_name,path_name_1;
		if((file_name=ci.request_response.get_parameter("file"))==null)
			return null;
		String request_charset=ci.request_response.implementor.get_request_charset();
		try{
			file_name=java.net.URLDecoder.decode(file_name,request_charset);
			file_name=java.net.URLDecoder.decode(file_name,request_charset);
		}catch(Exception e){
			return null;
		}
		file_name=file_reader.separator(file_name);
		directory_name=new File(p.directory_name+p.material_file_name).getParent();
		path_name_1=file_reader.separator(directory_name)+File.separator+file_name;
		if(new File(path_name_1).exists())
			return new String[]{path_name_1,p.file_charset};
		
		String path_name_2=ek.system_par.proxy_par.proxy_data_root_directory_name+file_name;
		if(new File(path_name_2).exists())
			return new String[]{path_name_2,p.file_charset};
		
		debug_information.println("File 1 does NOT exist :	",path_name_1);
		debug_information.println("File 2 does NOT exist :	",path_name_2);
		
		return null;
	}
}