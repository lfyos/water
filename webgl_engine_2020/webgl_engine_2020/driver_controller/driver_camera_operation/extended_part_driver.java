package driver_camera_operation;

import kernel_common_class.const_value;
import kernel_component.component;
import kernel_driver.component_driver;

import kernel_driver.part_driver;
import kernel_engine.client_information;
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
	private String init_file_name,init_file_charset;
	private double x0,y0,scale;
	private int camera_modifier_id;
	
	public extended_part_driver(String my_init_file_name,String my_init_file_charset,
			double my_x0,double my_y0,double my_scale,int my_camera_modifier_id)
	{
		super();
		
		init_file_name=my_init_file_name;
		init_file_charset=my_init_file_charset;
		x0=my_x0;
		y0=my_y0;
		scale=my_scale;
		camera_modifier_id=my_camera_modifier_id;
	}
	public void destroy()
	{	
		super.destroy();
		init_file_name=null;
		init_file_charset=null;
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
		return new extended_part_driver(init_file_name,init_file_charset,x0,y0,scale,camera_modifier_id);
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
		part_rude ret_val=super.create_part_mesh_and_buffer_object_head(p,buffer_object_file_writer,pcps,system_par,scene_par);
		
		if(ret_val==null)
			return ret_val;
		
		if(ret_val.part_box==null)
			return ret_val;
		
		double box_distance=ret_val.part_box.distance();
		if(box_distance<const_value.min_value)
			return ret_val;

		if(buffer_object_file_writer!=null) {
			buffer_object_file_writer.print  ("		",	x0);
			buffer_object_file_writer.print  (",",		y0);
			buffer_object_file_writer.print  (",",		scale);
			buffer_object_file_writer.println(",",		box_distance);
		}
		return ret_val;
	}
	public component_driver create_component_driver(file_reader fr,boolean rollback_flag,
			part my_component_part,engine_kernel ek,client_request_response request_response)
	{
		return new extended_component_driver(my_component_part,camera_modifier_id,init_file_name,init_file_charset);
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
	public String[] response_event(part p,engine_kernel ek,client_information ci)
	{
		return super.response_event(p,ek,ci);
	}
}
