package driver_java_package_proxy;

import java.net.URL;
import java.io.File;
import java.net.URLClassLoader;

import kernel_render.render;
import kernel_driver.render_driver;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;

public class extended_render_driver extends render_driver
{
	private render_driver real_render_driver;
	
	private render_driver creat_render_driver(String jar_file_name,String jar_class_name,
				file_reader shader_fr,render ren,client_request_response request_response,
				system_parameter system_par,scene_parameter scene_par)
	{
		File jar_f=new File(jar_file_name);

		if(!(jar_f.exists()))
			return null;
		URLClassLoader jar_loader=null;
		try {
			URL jar_url[]=new URL[] {jar_f.toURI().toURL()};
			jar_loader=new URLClassLoader(jar_url,getClass().getSuperclass().getClassLoader());
		}catch(Exception e){
			jar_loader=null;
		}
		if(jar_loader==null) 
			return null;
		
		Object render_driver_object=null;
		try {
			render_driver_object=jar_loader.loadClass(jar_class_name).
				getConstructor(
					file_reader.class,render.class,client_request_response.class,
					system_parameter.class,scene_parameter.class).
				newInstance(shader_fr,ren,request_response,system_par,scene_par);
		}catch(Exception e) {
			render_driver_object=null;
		}		
		if(render_driver_object!=null) 
			if(render_driver_object instanceof render_driver)
				return (render_driver)render_driver_object;	
		return null;
	}
	public extended_render_driver(file_reader shader_fr,render ren,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		super(shader_fr,ren,request_response,system_par,scene_par);
		String jar_file_directory[],sub_jar_file_directory="jar_package_directory"+File.separatorChar;
		
		if(scene_par==null)
			jar_file_directory=new String[] 
			{
				shader_fr.directory_name,
				system_par.parameter_directory	+sub_jar_file_directory
			};
		else
			jar_file_directory=new String[] 
			{
				shader_fr.directory_name,
				scene_par.directory_name		+sub_jar_file_directory,
				scene_par.extra_directory_name	+sub_jar_file_directory,
				system_par.parameter_directory	+sub_jar_file_directory
			};
		String jar_file_name,jar_class_name;
		jar_file_name	=file_directory.replace_directory_special_char(shader_fr.get_string());
		jar_class_name	=shader_fr.get_string();

		for(int i=0,ni=jar_file_directory.length;i<ni;i++)
			if((real_render_driver=creat_render_driver(jar_file_directory[i]+jar_file_name,
				jar_class_name,shader_fr,ren,request_response,system_par,scene_par))!=null) 
					return;
		real_render_driver=null;
		debug_information.println("create jar render driver fail");
		debug_information.println("shader file is	",			
				shader_fr.directory_name+shader_fr.file_name);
		debug_information.println("proxy jar_class_name is	",	jar_class_name);
		debug_information.println("proxy jar_file_name is	",	jar_file_name);
	
		return;
	}	
	public void destroy()
	{
		super.destroy();
		
		if(real_render_driver!=null) {
			real_render_driver.destroy();
			real_render_driver=null;
		}
	}
	public render_driver clone(render ren,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		if(real_render_driver==null)
			return null;
		else
			return real_render_driver.clone(ren,request_response,system_par,scene_par);
	}
}
