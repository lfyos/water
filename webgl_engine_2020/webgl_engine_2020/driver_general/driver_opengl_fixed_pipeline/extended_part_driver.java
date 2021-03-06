package driver_opengl_fixed_pipeline;

import java.io.File;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.part_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_part.part_container_for_part_search;
import kernel_transformation.box;
import kernel_transformation.point;

public class extended_part_driver extends part_driver
{
	private light_parameter light_par;
	private render_material_parameter render_material_par;
	private String scene_directory_name,scene_parameter_directory_name;
	private int top_box_part_material_id;
	
	public extended_part_driver(
			light_parameter my_light_par,render_material_parameter my_render_material_par,
			String my_scene_directory_name,String my_scene_parameter_directory_name)
	{
		super();
		
		light_par									=my_light_par;
		render_material_par							=my_render_material_par;
		
		scene_directory_name						=my_scene_directory_name;
		scene_parameter_directory_name				=my_scene_parameter_directory_name;
		
		top_box_part_material_id					=0;
	}
	public void destroy()
	{	
		super.destroy();
		
		light_par						=null;
		render_material_par				=null;
		scene_directory_name			=null;
		scene_parameter_directory_name	=null;
	}
	public void initialize_part_driver(part p,engine_kernel ek,client_request_response request_response)
	{
	}
	public void response_init_data(part p,engine_kernel ek,client_information ci)
	{
	}
	public part_driver clone(part parent,part p,
			system_parameter system_par,client_request_response request_response)
	{
		extended_part_driver ret_val=new extended_part_driver(
				light_par,render_material_par,scene_directory_name,scene_parameter_directory_name);
		if(p.top_box_part_flag)
			if(parent.part_mesh!=null)
				ret_val.top_box_part_material_id=Integer.decode(parent.part_mesh.default_material[3]);
		return ret_val;
	}
	public int caculate_material_id(
			part p,String type_str,int body_id,int face_id,int loop_id,int edge_id,
			String material_x,String material_y,String material_z,String material_w)
	{
		if(type_str.compareTo("face")!=0)
			return 0;
		if(p.top_box_part_flag)
			return top_box_part_material_id;
		if(material_w==null)
			return 0;
		return Integer.decode(material_w);
	}
	public part_rude create_part_mesh_and_buffer_object_head(
			part p,file_writer buffer_object_file_writer,
			part_container_for_part_search pcps,system_parameter system_par)
	{
		String material_file_name=p.directory_name+p.material_file_name;
		String material_file_charset=p.file_charset;
		
		if(p.top_box_part_flag) {
			String test_file_name=render_material_par.render_directory_name+render_material_par.top_box_part_material_file_name;
			
			if(new File(test_file_name).exists()) {
				material_file_name=test_file_name;
				material_file_charset=render_material_par.render_material_charset;
			}
			if(scene_parameter_directory_name!=null) {
				test_file_name=scene_parameter_directory_name+render_material_par.top_box_part_material_file_name;
				if(new File(test_file_name).exists()) {
					material_file_name=test_file_name;
				}
			}
			if(scene_directory_name!=null) {
				test_file_name=scene_directory_name+render_material_par.top_box_part_material_file_name;
				if(new File(test_file_name).exists()) {
					material_file_name=test_file_name;
				}
			}
		}
		part_material_parameter material[]=part_material_parameter.load_material_parameter(
				material_file_name,render_material_par.texture_directory_name,material_file_charset);
		for(int i=0,ni=material.length;i<ni;i++) {
			if(material[i].texture_file_name!=null) {
				String d_file_name=buffer_object_file_writer.directory_name+material[i].texture_file_name;
				String s_file_name_1=material[i].directory_name+material[i].texture_file_name;
				if(new File(s_file_name_1).exists())
					file_writer.file_copy(s_file_name_1,d_file_name);
				else{
					String s_file_name_2=render_material_par.texture_directory_name+material[i].texture_file_name;
					if(new File(s_file_name_2).exists())
						file_writer.file_copy(s_file_name_2,d_file_name);
				}
			}
		}
		buffer_object_file_writer.println("\t\t{");
		light_par.create_light_in_part_head(buffer_object_file_writer);		
		part_material_parameter.create_material_in_part_head(buffer_object_file_writer,material,render_material_par);
		buffer_object_file_writer.println("\t\t}");
		
		return super.create_part_mesh_and_buffer_object_head(p,buffer_object_file_writer,pcps,system_par);
	}
	public component_driver create_component_driver(file_reader fr,boolean rollback_flag,
			part my_component_part,engine_kernel ek,client_request_response request_response)
	{
		scene_directory_name			=new String(ek.scene_directory_name);
		scene_parameter_directory_name	=new String(ek.scene_par.directory_name);
		
		return new extended_component_driver(my_component_part);
	}
	public box caculate_part_box(part p,component comp,int driver_id,
			int body_id,int face_id,int loop_id,int edge_id,int point_id,
			point p0,point p1)
	{
		return super.caculate_part_box(p,comp,driver_id,body_id,face_id,loop_id,edge_id,point_id,p0,p1);
	}
	public String [][]assemble_file_name_and_file_charset(file_reader fr,part p,
			engine_kernel ek,client_request_response request_response)
	{
		return null;
	}
	public String[] response_event(part p,engine_kernel ek,client_information ci)
	{
		return super.response_event(p,ek,ci);
	}
}
