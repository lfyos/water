package driver_coordinate;

import kernel_component.component;
import kernel_component.component_load_source_container;
import kernel_driver.component_driver;
import kernel_driver.part_driver;
import kernel_driver.part_instance_driver;
import kernel_engine.engine_kernel;
import kernel_engine.scene_parameter;
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
	public double camera_length_scale,selection_length_scale;
	
	public extended_part_driver(double my_camera_length_scale,double my_selection_length_scale)
	{
		super();
		camera_length_scale=my_camera_length_scale;
		selection_length_scale=my_selection_length_scale;
	}
	public void destroy()
	{	
		super.destroy();
	}
	public void initialize_part_driver(part p,engine_kernel ek,client_request_response request_response)
	{
	}
	public part_driver clone(part parent,part p,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		return new extended_part_driver(camera_length_scale,selection_length_scale);
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
		return super.create_part_mesh_and_buffer_object_head(p,buffer_object_file_writer,pcps,system_par,scene_par);
	}
	public box caculate_part_box(part p,component comp,int driver_id,
			int body_id,int face_id,int loop_id,int edge_id,int point_id,
			point p0,point p1)
	{
//		super.caculate_part_box(p,comp,driver_id,body_id,face_id,loop_id,edge_id,point_id,p0,p1);
		return null;
	}
	public String [][]assemble_file_name_and_file_charset(file_reader fr,part p,
			engine_kernel ek,client_request_response request_response)
	{
		return null;
	}
	
	public component_driver create_component_driver(
			file_reader fr,boolean rollback_flag,part my_component_part,
			component_load_source_container component_load_source_cont,
			engine_kernel ek,client_request_response request_response)
	{
		boolean my_abandon_camera_display_flag	=fr.get_boolean();
		boolean my_abandon_selected_display_flag=fr.get_boolean();
		return new extended_component_driver(my_component_part,
				my_abandon_camera_display_flag,my_abandon_selected_display_flag);
	}
	public part_instance_driver create_part_instance_driver(part p,
				engine_kernel ek,client_request_response request_response)
	{
		return new extended_part_instance_driver();
	}
}
