package driver_lession_07_texture_with_discard_and_movement;

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
import kernel_part.part_container_for_part_search;
import kernel_part.part_rude;
import kernel_transformation.box;
import kernel_transformation.point;

public class extended_part_driver extends part_driver
{
	public extended_part_driver(part p,system_parameter system_par,client_request_response request_response)
	{
		super();
	}
	public void destroy()
	{	
		super.destroy();
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
		return new extended_part_driver(p,system_par,request_response);
	}
	public int caculate_material_id(
			part p,String type_str,int body_id,int face_id,int loop_id,int edge_id,
			String material_x,String material_y,String material_z,String material_w)
	{
		return 0;
	}
	public part_rude create_mesh_and_material(part p,file_writer buffer_object_file_writer,
			part_container_for_part_search pcps,system_parameter system_par)
	{
		file_writer.file_copy_with_brother(
				p.directory_name+p.mesh_file_name,buffer_object_file_writer.directory_name);
		return super.create_mesh_and_material(p,buffer_object_file_writer,pcps,system_par);
	}
	public component_driver create_component_driver(file_reader fr,boolean rollback_flag,
			part my_component_part,engine_kernel ek,client_request_response request_response)
	{
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
