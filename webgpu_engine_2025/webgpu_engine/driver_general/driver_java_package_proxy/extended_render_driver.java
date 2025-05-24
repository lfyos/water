package driver_java_package_proxy;

import java.net.URL;
import java.io.File;
import java.net.URLClassLoader;

import kernel_render.render;
import kernel_driver.render_driver;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;

public class extended_render_driver extends render_driver
{
	private render_driver real_render_driver;
	
	public extended_render_driver(file_reader shader_fr,render parent_render,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		super(shader_fr,parent_render,request_response,system_par,scene_par);
		
		real_render_driver=null;
		
		String jar_file_name,jar_class_name;
		jar_file_name	=shader_fr.directory_name+file_reader.separator(shader_fr.get_string());
		jar_class_name	=shader_fr.get_string();
	
		URLClassLoader jar_loader=null;
		try {
			URL jar_url[]=new URL[] {new File(jar_file_name).toURI().toURL()};
			jar_loader=new URLClassLoader(jar_url,getClass().getSuperclass().getClassLoader());
		}catch(Exception e){
			jar_loader=null;
			e.printStackTrace();
			debug_information.println("create jar render driver error,URLClassLoader fail:",e.toString());
		}
		if(jar_loader==null)  {
			debug_information.println("create jar render driver error,jar_loader==null");
			debug_information.println("proxy jar_class_name is ",	jar_class_name);
			debug_information.println("proxy jar_file_name is ",	jar_file_name);
			return;
		}	
		
		Object render_driver_object=null;
		try {
			render_driver_object=jar_loader.loadClass(jar_class_name).
				getConstructor(
					file_reader.class,render.class,client_request_response.class,
					system_parameter.class,scene_parameter.class).
				newInstance(
					shader_fr,parent_render,request_response,system_par,scene_par);
		}catch(Exception e){
			render_driver_object=null;
			e.printStackTrace();
			debug_information.println("create jar render driver error,getConstructor fail:",e.toString());
		}
		if(render_driver_object==null) {
			debug_information.println("create jar render driver error,render_driver_object==null");
			debug_information.println("proxy jar_class_name is ",	jar_class_name);
			debug_information.println("proxy jar_file_name is ",	jar_file_name);
			return;
		}
		if(!(render_driver_object instanceof render_driver)){
			real_render_driver=null;
			debug_information.println("create jar render driver error,type error");
			debug_information.println("proxy jar_class_name is ",	jar_class_name);
			debug_information.println("proxy jar_file_name is ",	jar_file_name);
			return;
		}
		real_render_driver=(render_driver)render_driver_object;	
	}
	public void destroy()
	{
		super.destroy();
		
		if(real_render_driver!=null) {
			real_render_driver.destroy();
			real_render_driver=null;
		}
	}
	public render_driver clone(render parent_render,
			client_request_response request_response,system_parameter system_par,scene_parameter scene_par)
	{
		if(real_render_driver==null)
			return null;
		else
			return real_render_driver.clone(parent_render, request_response, system_par, scene_par);
	}
}
