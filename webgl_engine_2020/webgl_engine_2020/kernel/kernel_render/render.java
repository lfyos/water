package kernel_render;

import java.io.File;
import kernel_common_class.debug_information;
import kernel_driver.render_driver;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_container_for_part_search;
import kernel_part.part_parameter;


public class render
{
	public Class<?>driver_class;
	
	public String javascript_decode,javascript_draw;
	public String vertex_shader,fragment_shader,geometry_shader,tess_control_shader,tess_evalue_shader;
	
	public part parts[];
	
	public void destroy()
	{
		driver_class		=null;
		
		javascript_decode	=null;
		javascript_draw		=null;
		vertex_shader		=null;
		fragment_shader		=null;
		geometry_shader		=null;
		tess_control_shader	=null;
		tess_evalue_shader	=null;
		
		if(parts!=null) {
			for(int i=0,ni=parts.length;i<ni;i++) 
				if(parts[i]!=null){
					parts[i].destroy();
					parts[i]=null;
				}
			parts=null;
		}
	}
	public render(render r,system_parameter system_par,client_request_response request_response)
	{	
		driver_class		=r.driver_class;
		
		javascript_decode	=new String(r.javascript_decode);
		javascript_draw		=new String(r.javascript_draw);
		
		vertex_shader		=new String(r.vertex_shader);
		fragment_shader		=new String(r.fragment_shader);
		
		geometry_shader		=new String(r.geometry_shader);
		
		tess_control_shader	=new String(r.tess_control_shader);
		tess_evalue_shader	=new String(r.tess_evalue_shader);

		if(r.parts==null)
			parts=null;
		else if(r.parts.length<=0)
			parts=null;
		else{
			parts=new part[r.parts.length];
			for(int i=0,ni=r.parts.length;i<ni;i++)
				parts[i]=new part(r.parts[i],system_par,request_response);
		}
	}
	
	public render(render_driver my_driver)
	{
		driver_class=my_driver.getClass();
		
		javascript_decode	=new String(my_driver.javascript_decode);
		javascript_draw		=new String(my_driver.javascript_draw);
		
		vertex_shader		=new String(my_driver.vertex_shader);
		fragment_shader		=new String(my_driver.fragment_shader);
		
		geometry_shader		=new String(my_driver.geometry_shader);
		
		tess_control_shader	=new String(my_driver.tess_control_shader);
		tess_evalue_shader	=new String(my_driver.tess_evalue_shader);
		
		parts=null;
	}
	public void delete_last_part()
	{
		if(parts==null)
			return;
		if(parts.length<=1) {
			parts=null;
			return;
		}
		part bak[]=parts;
		parts=new part[parts.length-1];
		for(int i=0,ni=parts.length;i<ni;i++)
			parts[i]=bak[i];
		bak[bak.length-1].destroy();
		return;
	}
	public void add_part(int add_render_id,part p)
	{
		if(p==null)
			return;

		if(parts==null)
			parts=new part[]{p};
		else{
			part bak[]=parts;
			parts=new part[parts.length+1];
			for(int i=0,ni=parts.length-1;i<ni;i++)
				parts[i]=bak[i];
			parts[parts.length-1]=p;
		}

		p.render_id				=add_render_id;
		p.part_id				=parts.length-1;
		p.part_from_id			=-1;
		
		p.permanent_render_id	=p.render_id;
		p.permanent_part_id		=p.part_id;
		p.permanent_part_from_id=-1;
		
		return;
	}
	public void add_part(part_container_for_part_search pcps,
			render_driver r_driver,int part_type_id,int add_render_id,
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
				
				{
					File mesh_f=new File(f.directory_name+mesh_file_name);
					if(!(mesh_f.exists())){
						debug_information.println("mesh file not exist ",f.directory_name+mesh_file_name);
						continue;
					}
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
				try{
					my_part.driver=r_driver.create_part_driver(f,my_part,system_par,request_response);
				}catch(Exception e){
					my_part.driver=null;
					debug_information.println("Create part driver fail:",e.toString());
					debug_information.println("Part user name:	",		my_part.user_name);
					debug_information.println("Part system name:	",	my_part.system_name);
					debug_information.println("Directory name:	",		my_part.directory_name);
					debug_information.println("Mesh file name:	",		my_part.mesh_file_name);
					e.printStackTrace();
				}
				if(my_part.driver==null)
					my_part.destroy();
				else {
					add_part(add_render_id,my_part);
					pcps.append_one_part(my_part);
				}
		}
		f.close();
		return;
	}
}