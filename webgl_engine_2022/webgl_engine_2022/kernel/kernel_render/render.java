package kernel_render;

import java.io.File;
import java.util.ArrayList;

import kernel_part.part;
import kernel_part.part_parameter;
import kernel_part.part_container_for_part_search;

import kernel_common_class.debug_information;
import kernel_component.component_load_source_container;
import kernel_driver.render_driver;
import kernel_engine.system_parameter;
import kernel_engine.scene_parameter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;

public class render
{
	public int render_id;
	
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
	public render(int my_render_id,
			render r,client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{	
		render_id=my_render_id;
		render_name=r.render_name;
		render_id=r.render_id;
		driver=r.driver.clone(r,request_response,system_par,scene_par);
		
		parts=new ArrayList<part>();
		if(r.parts!=null)
			for(int i=0,ni=r.parts.size();i<ni;i++){
				part p;
				if((p=r.parts.get(i))!=null)
					p=new part(p,request_response,system_par,scene_par);
				parts.add(i,p);
			}
	}
	public render(int my_render_id,
			String my_render_name,String my_driver_name,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		render_id=my_render_id;
		render_name=my_render_name;
		driver=null;
		parts=null;
		
		Object render_driver_object;
		try{
			render_driver_object=Class.forName(my_driver_name).getConstructor().newInstance();
		}catch(Exception e){
			debug_information.println("Create render driver fail:		",e.toString());
			debug_information.println("Driver name is ",my_driver_name);
			e.printStackTrace();
			return;
		}
		if(!(render_driver_object instanceof render_driver)){
			debug_information.println("render driver class name error:		",my_driver_name);
			return;
	    }
		render_driver original_driver=(render_driver)render_driver_object;
		driver=original_driver.clone(null,request_response,system_par,scene_par);
		original_driver.destroy();

		return;
	}
	public void delete_last_part()
	{
		int part_number=parts.size();
		if(part_number<=0) 
			return;
		part p=parts.get(part_number-1);
		p.destroy();
		parts.remove(part_number-1);
		return;
	}
	
	public void add_part(part p)
	{
		if(p==null)
			return;
		
		if(parts==null) 
			parts=new ArrayList<part>();
		
		int part_number=parts.size();
		parts.add(part_number, p);

		p.render_id				=render_id;
		p.part_id				=part_number;
		p.part_from_id			=-1;
		
		p.permanent_render_id	=p.render_id;
		p.permanent_part_id		=p.part_id;
		p.permanent_part_from_id=-1;
		
		return;
	}
	public void add_part(boolean not_real_scene_fast_load_flag,
			component_load_source_container component_load_source_cont,
			part_container_for_part_search pcps,render_driver r_driver,int part_type_id,
			part_parameter part_par,system_parameter system_par,
			String file_name,String file_charset,String pre_buffer_object_file_name,
			client_request_response request_response)
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
				
				if(not_real_scene_fast_load_flag) {
					File mesh_f=new File(f.directory_name+mesh_file_name);
					if(mesh_f.exists())
						if(mesh_f.lastModified()<f.lastModified_time)
							mesh_f.setLastModified(f.lastModified_time);
					
					File material_f=new File(f.directory_name+material_file_name);
					if(material_f.exists())
						if(material_f.lastModified()<f.lastModified_time)
							material_f.setLastModified(f.lastModified_time);
				}
				part my_part=new part(part_type_id,false,
						part_par.clone(),f.directory_name,f.get_charset(),
						(user_name==null)				?"":user_name,
						(system_name==null)				?"":system_name,
						(mesh_file_name==null)			?"":mesh_file_name,
						(material_file_name==null)		?"":material_file_name,
						(description_file_name==null)	?"":description_file_name,		
						(audio_file_name==null)			?"":audio_file_name);
				
				add_part(my_part);
				
				try{
					my_part.driver=r_driver.create_part_driver(f,my_part,
							component_load_source_cont,system_par,request_response);
				}catch(Exception e){
					my_part.driver=null;
					debug_information.println("Create part driver fail:",e.toString());
					debug_information.println("Part user name:	",		my_part.user_name);
					debug_information.println("Part system name:	",	my_part.system_name);
					debug_information.println("Directory name:	",		my_part.directory_name);
					debug_information.println("Mesh file name:	",		my_part.mesh_file_name);
					e.printStackTrace();
				}
				if(my_part.driver!=null)
					pcps.append_one_part(my_part);
				else {
					delete_last_part();
					my_part.destroy();
				}
		}
		f.close();
		return;
	}
}