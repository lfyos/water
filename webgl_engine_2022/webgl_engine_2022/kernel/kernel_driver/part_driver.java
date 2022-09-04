package kernel_driver;

import java.io.File;

import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_load_source_container;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_part.caculate_part_items;
import kernel_part.part;
import kernel_part.part_container_for_part_search;
import kernel_transformation.box;
import kernel_transformation.point;
import kernel_network.client_request_response;
import kernel_common_class.debug_information;
import kernel_part.part_rude;

public class part_driver
{
	public void destroy()
	{
	}
	public void initialize_part_driver(part p,engine_kernel ek,client_request_response request_response)
	{
	}
	public void response_init_part_data(part p,engine_kernel ek,client_information ci)
	{
	}
	public part_driver()
	{
	}
	public part_driver clone(part parent,part p,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		return new part_driver();
	}
	public int caculate_material_id(
			part p,String type_str,int body_id,int face_id,int loop_id,int edge_id,
			String material_x,String material_y,String material_z,String material_w)
	{
		return 0;
	}
	public part_rude create_part_mesh_and_buffer_object_head(part p,
			file_writer buffer_object_file_writer,part_container_for_part_search pcps,
			system_parameter system_par,scene_parameter scene_par)
	{
		if(!(p.is_normal_part()))
			return p.part_mesh;
		
		String my_file_path=file_reader.separator(p.directory_name+p.mesh_file_name);
		if(!(new File(my_file_path).exists()))
			return null;

		file_reader fr=null;
		try{
			fr=new file_reader(my_file_path,p.file_charset);
			fr.mark_start();
			for(String version_str;;) {
				if((version_str=fr.get_string())!=null)
					if(version_str.compareTo("2021.07.15")==0) {
						fr.mark_terminate(true);
						break;
					}
				fr.close();	
				convert_old_part_rude_2021_07_01.part_rude.convert(my_file_path,p.file_charset);
				fr=new file_reader(my_file_path,p.file_charset);
				break;
			}
		}catch(Exception e) {
			debug_information.println("create_part_mesh_and_buffer_object_head(format convertion) exception:	",e.toString());
			e.printStackTrace();
			if(fr!=null) {
				fr.close();
				fr=null;
			}
		}
		
		if(fr==null)
			return null;
		
		part_rude ret_val=new part_rude(fr);
		fr.close();
		
		return ret_val;
	}
	public component_driver create_component_driver(
			file_reader fr,boolean rollback_flag,part my_component_part,
			component_load_source_container component_load_source_cont,
			engine_kernel ek,client_request_response request_response)
	{
		return new component_driver(my_component_part);
	}
	public box caculate_part_box(part p,component comp,int driver_id,
			int body_id,int face_id,int loop_id,int edge_id,int point_id,
			point p0,point p1)
	{
		if(p.part_mesh==null)
			return null;
		caculate_part_items cpi=new caculate_part_items(p,body_id,face_id,loop_id,edge_id,point_id);
		if((p0==null)||(p1==null))
			return (cpi.my_box==null)?null:new box(cpi.my_box);
		if(p1.sub(p0).distance2()<const_value.min_value2)
			return (cpi.my_box==null)?null:new box(cpi.my_box);
		
		point my_point=cpi.caculate_focus_point(p0,p1);
		return (my_point==null)?null:new box(my_point);
	}
	public String [][]assemble_file_name_and_file_charset(file_reader fr,part p,
			engine_kernel ek,client_request_response request_response)
	{
		return null;
	}
	public String[] response_event(part p,engine_kernel ek,client_information ci)
	{	
		String file_name;
		if((file_name=ci.request_response.get_parameter("file"))==null)
			file_name="";
		else{
			String request_charset=ci.request_response.implementor.get_request_charset();
			try {
				file_name=java.net.URLDecoder.decode(file_name,request_charset);
				file_name=java.net.URLDecoder.decode(file_name,request_charset);
			}catch(Exception e) {
				;
			}
			file_name=file_reader.separator(file_name);
		};
		file_name=file_directory.part_file_directory(p,ek.system_par,ek.scene_par)+file_name;
		
		if(file_reader.is_exist(file_name))
			return new String[] {file_name,ek.system_par.local_data_charset};
		
		if(p.part_from_id>=0){
			p=ek.render_cont.renders[p.render_id].parts[p.part_from_id];
			try{
				return p.driver.response_event(p,ek,ci);
			}catch(Exception e){
				debug_information.println("Execute part response_event fail:",e.toString());
				debug_information.println("Part user name:",	p.user_name);
				debug_information.println("Part system name:",	p.system_name);
				debug_information.println("Mesh_file_name:",	p.directory_name+p.mesh_file_name);
				debug_information.println("Material_file_name:",p.directory_name+p.material_file_name);
				
				e.printStackTrace();
			}
		}
		return null;
	}
}
