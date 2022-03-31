package kernel_render;

import java.io.File;

import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_driver.render_driver;
import kernel_engine.system_parameter;
import kernel_engine.scene_parameter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_container_for_part_search;
import kernel_part.part_parameter;

public class render
{
	public String render_name;
	public render_driver driver;
	public part parts[];
	private int part_number;
	
	public void destroy()
	{
		if(render_name!=null)
			render_name=null;
		
		if(parts!=null) {
			for(int i=0,ni=parts.length;i<ni;i++) 
				if(parts[i]!=null){
					parts[i].destroy();
					parts[i]=null;
				}
			parts=null;
		}
		if(driver!=null){
			driver.destroy();
			driver=null;
		}
		part_number=0;
	}
	public render(render r,client_request_response request_response,
				system_parameter system_par,scene_parameter scene_par)
	{	
		render_name=r.render_name;
		part_number=r.part_number;
		driver=r.driver.clone(r,request_response,system_par,scene_par);
		
		if(r.parts==null)
			parts=null;
		else if(r.parts.length<=0)
			parts=null;
		else{
			parts=new part[r.parts.length];
			for(int i=0,ni=r.parts.length;i<ni;i++)
				if(i<part_number)
					parts[i]=new part(r.parts[i],request_response,system_par,scene_par);
				else
					parts[i]=null;
		}
	}
	public render(String my_render_name,String my_driver_name,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		render_name=my_render_name;
		part_number=0;
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
		if(part_number<=0) {
			parts=null;
			return;
		}
		parts[part_number-1].destroy();
		parts[part_number-1]=null;
		part_number--;
		
		if(part_number<=0)
			parts=null;
		return;
	}
	public void compress_part()
	{
		if((parts==null)||(part_number<=0)){
			part_number=0;
			parts=null;
		}else if(parts.length!=part_number){
			part bak[]=parts;
			parts=new part[part_number];
			for(int i=0;i<part_number;i++)
				parts[i]=bak[i];
		}
	}
	public void add_part(int add_render_id,part p)
	{
		if(p==null)
			return;
		
		if(parts==null) {
			parts=new part[10];
			part_number=0;
		}else if(part_number>=parts.length){
			part bak[]=parts;
			parts=new part[parts.length*2];
			for(int i=0,ni=bak.length;i<ni;i++)
				parts[i]=bak[i];
		}
		parts[part_number]=p;

		p.render_id				=add_render_id;
		p.part_id				=part_number;
		p.part_from_id			=-1;
		
		p.permanent_render_id	=p.render_id;
		p.permanent_part_id		=p.part_id;
		p.permanent_part_from_id=-1;
		
		part_number++;
		
		return;
	}
	public void add_part(boolean not_real_scene_fast_load_flag,
			part_container_for_part_search pcps,render_driver r_driver,int part_type_id,
			int add_render_id,part_parameter part_par,system_parameter system_par,
			String file_name,String file_charset,String pre_buffer_object_file_name,
			change_name mount_component_name_and_assemble_file_name,client_request_response request_response)
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
				
				add_part(add_render_id,my_part);
				
				try{
					my_part.driver=r_driver.create_part_driver(f,my_part,system_par,
							mount_component_name_and_assemble_file_name,request_response);
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
		compress_part();
		
		return;
	}
}