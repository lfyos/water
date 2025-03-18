package driver_camera_operation;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_driver.part_driver;
import kernel_transformation.box;
import kernel_component.component;
import kernel_transformation.point;
import kernel_driver.component_driver;
import kernel_common_class.const_value;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_driver.part_instance_driver;
import kernel_network.client_request_response;
import kernel_component.component_load_source_container;

public class extended_part_driver extends part_driver
{
	private double x0,y0,scale;
	private int modifier_container_id;
	
	public extended_part_driver(double my_x0,double my_y0,
			double my_scale,int my_modifier_container_id)
	{
		super();
		
		x0=my_x0;
		y0=my_y0;
		scale=my_scale;
		modifier_container_id=my_modifier_container_id;
	}
	public void destroy()
	{	
		super.destroy();
	}
	public void initialize_part_driver(part p,scene_kernel sk,client_request_response request_response)
	{
	}
	public part_driver clone(part parent,part p,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		return new extended_part_driver(x0,y0,scale,modifier_container_id);
	}
	public int caculate_material_id(
			part p,String type_str,int body_id,int face_id,int loop_id,int edge_id,
			String material_x,String material_y,String material_z,String material_w)
	{
		return 0;
	}
	public void create_part_material_in_head(file_writer fw,
			part p,system_parameter system_par,scene_parameter scene_par)
	{
		double box_distance=1.0,my_box_distance;
		if(p.part_mesh.part_box!=null)
			if((my_box_distance=p.part_mesh.part_box.distance())>const_value.min_value)
				box_distance=my_box_distance;
		fw.print  ("		",	x0);
		fw.print  (",",			y0);
		fw.print  (",",			scale);
		fw.println(",",			box_distance);
		fw.print  (",1");
	}
	public box caculate_part_box(part p,component comp,int driver_id,
			int body_id,int face_id,int primitive_id,int vertex_id,int loop_id,int edge_id,
			point p0,point p1)
	{
		return null;
//		return super.caculate_part_box(p,comp,driver_id,body_id,face_id,primitive_id,vertex_id,loop_id,edge_id,p0,p1);
	}
	public String [][]assemble_file_name_and_file_charset(file_reader fr,part p,
			scene_kernel sk,client_request_response request_response)
	{
		return null;
	}
	public component_driver create_component_driver(
			file_reader fr,boolean rollback_flag,part my_component_part,
			component_load_source_container component_load_source_cont,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_component_driver(my_component_part,modifier_container_id);
	}
	public part_instance_driver create_part_instance_driver(part p,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_part_instance_driver();
	}
}
