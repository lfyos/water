package driver_opengl_fixed_pipeline;

import java.io.File;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_file_manager.file_writer;
import kernel_scene.client_information;
import kernel_file_manager.file_directory;
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
	public void response_init_part_data(part p,scene_kernel sk,client_information ci)
	{
	}
	public String[] response_part_event(part p,scene_kernel sk,client_information ci)
	{			
		String file_name;
		if((file_name=ci.request_response.get_parameter("file"))==null)
			return null;
		String request_charset=ci.request_response.implementor.get_request_charset();
		try{
			file_name=java.net.URLDecoder.decode(file_name,request_charset);
			file_name=java.net.URLDecoder.decode(file_name,request_charset);
		}catch(Exception e){
			return null;
		}
		file_name=file_directory.replace_directory_special_char(file_name);
		String temp_path_name=file_directory.part_file_directory(p,sk.system_par, sk.scene_par)+file_name;
		
		File f=new File(p.directory_name+p.material_file_name);
		String path_name_0=f.getParent()+File.separator+file_name;
		if((f=new File(path_name_0)).exists()){
			if(new File(temp_path_name).lastModified()<f.lastModified())
				file_writer.file_copy(path_name_0,temp_path_name);
			return new String[]{temp_path_name,p.file_charset};
		}
		
		String path_name_1=p.directory_name+file_name;
		if((f=new File(path_name_1)).exists()){
			if(new File(temp_path_name).lastModified()<f.lastModified())
				file_writer.file_copy(path_name_1,temp_path_name);
			return new String[]{temp_path_name,p.file_charset};
		}
		
		String path_name_2=sk.system_par.temporary_file_par.root_directory_name+file_name;
		if(new File(path_name_2).exists())
			return new String[]{path_name_2,p.file_charset};

		debug_information.println("File 0 does NOT exist :	",path_name_0);
		debug_information.println("File 1 does NOT exist :	",path_name_1);
		debug_information.println("File 2 does NOT exist :	",path_name_2);
		
		return null;
	}
}