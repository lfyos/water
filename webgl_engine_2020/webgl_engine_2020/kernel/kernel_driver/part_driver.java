package kernel_driver;

import java.io.File;

import kernel_common_class.const_value;
import kernel_component.component;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
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
	public void response_init_data(part p,engine_kernel ek,client_information ci)
	{
	}
	public part_driver()
	{
	}
	public part_driver clone(part parent,part p,
			system_parameter system_par,client_request_response request_response)
	{
		return new part_driver();
	}
	public int caculate_material_id(
			part p,String type_str,int body_id,int face_id,int loop_id,int edge_id,
			String material_x,String material_y,String material_z,String material_w)
	{
		return 0;
	}
	public part_rude create_part_mesh_and_buffer_object_head(
			part p,file_writer buffer_object_file_writer,
			part_container_for_part_search pcps,system_parameter system_par)
	{
		if(p.part_mesh!=null)
			return p.part_mesh;
		if(p.mesh_file_name==null)
			return null;
		
		String my_file_path=file_reader.separator(p.directory_name+p.mesh_file_name);
		if(!(new File(my_file_path).exists()))
			return null;
		
		system_par.system_exclusive_name_mutex.lock(my_file_path+".lock");
	
		file_reader fr=new file_reader(my_file_path,p.file_charset);
		fr.mark_start();
		String version_str=fr.get_string();
		if(version_str!=null)
			if(version_str.compareTo("2021.07.01")==0) {
				system_par.system_exclusive_name_mutex.unlock(my_file_path+".lock");
				
				fr.mark_terminate(true);
				part_rude ret_val=new part_rude(fr,p.part_par.scale_value);
				fr.close();
				
				return ret_val;
			}
		fr.close();
		
		String targety_file_path=buffer_object_file_writer.directory_name+"part.tmp.mesh";
		
		file_writer.file_rename(my_file_path,		 targety_file_path);
		file_writer.file_rename(my_file_path+".face",targety_file_path+".face");
		file_writer.file_rename(my_file_path+".edge",targety_file_path+".edge");
		
		convert_old_part_rude_2021_07_01.part_rude.convert(targety_file_path,my_file_path,p.file_charset);
		
		file_writer.file_delete(targety_file_path);
		file_writer.file_delete(targety_file_path+".face");
		file_writer.file_delete(targety_file_path+".edge");
		
		system_par.system_exclusive_name_mutex.unlock(my_file_path+".lock");
		
		fr=new file_reader(my_file_path,p.file_charset);
		part_rude ret_val=new part_rude(fr,p.part_par.scale_value);
		fr.close();
		
		return ret_val;
	}
	public component_driver create_component_driver(file_reader fr,boolean rollback_flag,
			part my_component_part,engine_kernel ek,client_request_response request_response)
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
		String directory_name=file_directory.part_file_directory(p,ek.system_par,ek.scene_par);
		String file_name=ci.request_response.get_parameter("file");
		file_name=(file_name==null)?directory_name:(directory_name+file_reader.separator(file_name));
		
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
