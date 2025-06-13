package kernel_render;

import java.io.File;
import java.util.ArrayList;

import kernel_part.part;
import kernel_part.part_parameter;
import kernel_driver.render_driver;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_file_manager.file_reader;
import kernel_part.permanent_part_id_encoder;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_component.component_load_source_container;

public class render
{
	public int render_id,part_type_id;
	public String render_name;
	public render_driver driver;
	public ArrayList<part> parts;

	public void destroy()
	{
		if(render_name!=null)
			render_name=null;
		if(parts!=null){
			for(int i=0,ni=parts.size();i<ni;i++){
				part p;
				if((p=parts.get(i))!=null){
					p.destroy();
					parts.set(i,null);
				}
			}
			parts=null;
		}
		if(driver!=null){
			driver.destroy();
			driver=null;
		}
	}
	public render(
			render r,client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{	
		part p;
		
		render_name		=r.render_name;
		render_id		=r.render_id;
		part_type_id	=r.part_type_id;
		driver			=r.driver.clone(r,request_response,system_par,scene_par);
		
		parts=new ArrayList<part>();
		if(r.parts!=null)
			for(int i=0,ni=r.parts.size();i<ni;i++)
				if((p=r.parts.get(i))!=null)
					parts.add(i,new part(p,request_response,system_par,scene_par));
	}
	public render(int my_render_id,int my_part_type_id,
			String my_render_name,String my_driver_name,
			file_reader f_shader,client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		render_id	=my_render_id;
		part_type_id=my_part_type_id;
		render_name	=my_render_name;
		driver		=null;
		parts		=new ArrayList<part>();
	
		String my_file_name=f_shader.directory_name+f_shader.file_name;
		
		Object my_render_driver;
		try{
			var my_class=Class.forName(my_driver_name);
			var my_constructor=my_class.getConstructor(file_reader.class,render.class,
					client_request_response.class,system_parameter.class,scene_parameter.class);
			my_render_driver=my_constructor.newInstance(
					f_shader,this,request_response,system_par,scene_par);
		}catch(Exception e){
			debug_information.println("Create render driver exception,class name:	",	my_driver_name);
			debug_information.println("Create render driver exception,file_name:	",	my_file_name);
			debug_information.println("Create render driver exception,Exception:	",	e.toString());
			e.printStackTrace();
			return;
		}
		if(my_render_driver==null) {
			debug_information.println("Create render driver (my_render_driver==null),class name:	",	my_driver_name);
			debug_information.println("Create render driver (my_render_driver==null),file_name:	",		my_file_name);
			return;
		}
		if(!(my_render_driver instanceof render_driver)){
			debug_information.println("Create render driver (NOT instanceof),class name:	",	my_driver_name);
			debug_information.println("Create render driver (NOT instanceof),file_name:	",		my_file_name);
			return;
		}
		try {
			render_driver old_driver=(render_driver)my_render_driver;
			driver=old_driver.clone(this,request_response,system_par,scene_par);
			old_driver.destroy();
		}catch(Exception e){
			driver=null;
			debug_information.println("clone render driver exception,class name:	",	my_driver_name);
			debug_information.println("clone render driver exception,file_name:	",		my_file_name);
			debug_information.println("clone render driver exception,Exception:	",		e.toString());
			e.printStackTrace();
			return;
		}
		if(driver==null) {
			debug_information.println("Create render driver (driver==null),class name:	",	my_driver_name);
			debug_information.println("Create render driver (driver==null),file_name:	",	my_file_name);
		};
	}
	public void delete_last_part()
	{
		int part_number;
		if((part_number=parts.size())>0)
			parts.remove(part_number-1).destroy();
	}
	public void add_part(part p,permanent_part_id_encoder encoder[])
	{
		if(p==null)
			return;
		int part_number=parts.size();
		parts.add(part_number,p);
	
		p.render_id				=render_id;
		p.part_id				=part_number;
		p.part_from_id			=-1;
		
		p.permanent_part_id		=encoder[p.part_type_id].encoder(p.part_par.part_type_string);
		p.permanent_part_from_id=-1;
	}
	
	public void add_part(part_container_for_part_search pcps,render ren,
			component_load_source_container component_load_source_cont,
			part_parameter part_par,system_parameter system_par,scene_parameter scene_par,
			String file_name,String file_charset,String pre_buffer_object_file_name,
			permanent_part_id_encoder encoder[],client_request_response request_response)
	{
		file_reader f=new file_reader(file_name,file_charset);
		if(f.error_flag()) {
			f.close();
			debug_information.println("Execute add_part fail,part list file NOT exits:	",file_name);
			return;
		}
		while(!(f.eof())){
			String user_name			=f.get_string();
			String system_name			=f.get_string();
			String mesh_file_name		=f.get_string();
			String material_file_name	=f.get_string();
			String description_file_name=f.get_string();
			String audio_file_name		=f.get_string();
								
			if(audio_file_name==null)
				continue;
			if(audio_file_name.compareTo("")==0)
				continue;
				
			mesh_file_name			=file_reader.separator(mesh_file_name);
			material_file_name		=file_reader.separator(material_file_name);
			description_file_name	=file_reader.separator(description_file_name);
			audio_file_name			=file_reader.separator(audio_file_name);
			
			long my_last_time;
			part_parameter my_part_par=part_par.clone();
			
			if(my_part_par.last_modified_time<f.lastModified_time)
				my_part_par.last_modified_time=f.lastModified_time;

			File mesh_f=new File(f.directory_name+mesh_file_name);
			if(mesh_f.exists()) {
				if((my_last_time=mesh_f.lastModified())<f.lastModified_time)
					mesh_f.setLastModified(f.lastModified_time);
				if(my_part_par.last_modified_time<my_last_time)
					my_part_par.last_modified_time=my_last_time;
			}
			File material_f=new File(f.directory_name+material_file_name);
			if(material_f.exists()) {
				if((my_last_time=material_f.lastModified())<f.lastModified_time)
					material_f.setLastModified(f.lastModified_time);
				if(my_part_par.last_modified_time<my_last_time)
					my_part_par.last_modified_time=my_last_time;
			}

			part my_part=new part(part_type_id,false,my_part_par,f.directory_name,f.get_charset(),
					
					(user_name==null)				?"":user_name,
					(system_name==null)				?"":system_name,
					(mesh_file_name==null)			?"":mesh_file_name,
					(material_file_name==null)		?"":material_file_name,
					(description_file_name==null)	?"":description_file_name,		
					(audio_file_name==null)			?"":audio_file_name);
			
			add_part(my_part,encoder);
				
			try{
				my_part.driver=ren.driver.create_part_driver(f,my_part,ren,
						component_load_source_cont,request_response,system_par,scene_par);
			}catch(Exception e){
				e.printStackTrace();
				
				my_part.driver=null;
				debug_information.println("Create part driver fail:",e.toString());
				debug_information.println("Part user name:	",		my_part.user_name);
				debug_information.println("Part system name:	",	my_part.system_name);
				debug_information.println("Directory name:	",		my_part.directory_name);
				debug_information.println("Mesh file name:	",		my_part.mesh_file_name);
			}
			if(my_part.driver!=null)
				pcps.append(my_part);
			else{
				delete_last_part();
				my_part.destroy();
			}
			component_load_source_cont.register_component(
					f,my_part.part_par.load_assemble_type,
					system_par.default_system_mount_component_name);
		}
		f.close();
		return;
	}
}