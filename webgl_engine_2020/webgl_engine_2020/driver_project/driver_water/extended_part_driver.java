package driver_water;

import java.io.File;

import kernel_component.component;
import kernel_driver.component_driver;

import kernel_driver.part_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_part.create_grid;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_part.part_container_for_part_search;
import kernel_transformation.box;
import kernel_transformation.point;
import kernel_network.client_request_response;

public class extended_part_driver extends part_driver{
	private int user_parameter_channel_id[];
	
	public extended_part_driver(int my_user_parameter_channel_id[])
	{
		super();
		user_parameter_channel_id=my_user_parameter_channel_id;
	}
	public void destroy()
	{	
		super.destroy();
		user_parameter_channel_id=null;
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
		return new extended_part_driver(user_parameter_channel_id);
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
		String file_name=p.directory_name+p.mesh_file_name;
		long t=(new File(file_name)).lastModified();
		
		(new create_grid()).do_create(file_name,true,64,64,new String[]{"vertex","normal"},p.file_charset);
		
		(new File(file_name)).setLastModified(t);
		
		file_writer.file_copy_with_brother(file_name,buffer_object_file_writer.directory_name);
		
		return super.create_part_mesh_and_buffer_object_head(p,buffer_object_file_writer,pcps,system_par);
	}
	public component_driver create_component_driver(file_reader fr,boolean rollback_flag,
			part my_component_part,engine_kernel ek,client_request_response request_response)
	{
		return new extended_component_driver(my_component_part,fr,user_parameter_channel_id);			
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
