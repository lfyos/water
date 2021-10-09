package driver_territory;

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
	private int user_parameter_channel_id[];
	private double geometry_scale_x,geometry_scale_y,geometry_scale_z;
	private double min_height,max_height;											
	private double grass_scale,earth_scale,bottom_scale,stone_scale,cobble_scale,sandstone_scale;
	private int material_map[];
	
	public extended_part_driver(int my_user_parameter_channel_id[],
			double my_geometry_scale_x,double my_geometry_scale_y,double my_geometry_scale_z,
			double my_min_height,double my_max_height,											
			double my_grass_scale,double my_earth_scale,double my_bottom_scale,
			double my_stone_scale,double my_cobble_scale,double my_sandstone_scale,
			int my_material_map[])
	{
		super();
		user_parameter_channel_id=my_user_parameter_channel_id;
		
		geometry_scale_x=my_geometry_scale_x;
		geometry_scale_y=my_geometry_scale_y;
		geometry_scale_z=my_geometry_scale_z;
		
		min_height=my_min_height;
		max_height=my_max_height;
		
		grass_scale=my_grass_scale;
		earth_scale=my_earth_scale;
		bottom_scale=my_bottom_scale;
		stone_scale=my_stone_scale;
		cobble_scale=my_cobble_scale;
		sandstone_scale=my_sandstone_scale;
		
		material_map=new int[] 
		{
				my_material_map[ 0],my_material_map[ 1],my_material_map[ 2],my_material_map[ 3],
				my_material_map[ 4],my_material_map[ 5],my_material_map[ 6],my_material_map[ 7],
				my_material_map[ 8],my_material_map[ 9],my_material_map[10],my_material_map[11],
				my_material_map[12],my_material_map[13],my_material_map[14],my_material_map[15]
		};
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
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		return new extended_part_driver(user_parameter_channel_id,
				geometry_scale_x,geometry_scale_y,geometry_scale_z,min_height,max_height,										
				grass_scale,earth_scale,bottom_scale,stone_scale,cobble_scale,sandstone_scale,material_map);
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
		if(buffer_object_file_writer!=null) {
			file_writer.file_copy_with_brother(
				p.directory_name+p.mesh_file_name,buffer_object_file_writer.directory_name);
	
			for(int i=0;i<16;i++)
				buffer_object_file_writer.print  ((i<=0)?"			":",",material_map[i]);
			buffer_object_file_writer.println(",");
			
			
			buffer_object_file_writer.print  ("			",	geometry_scale_x);
			buffer_object_file_writer.print  (",",			geometry_scale_y);
			buffer_object_file_writer.print  (",",			geometry_scale_z);
			buffer_object_file_writer.println(",");
			
			buffer_object_file_writer.print  ("			",	min_height);
			buffer_object_file_writer.print  (",",			max_height);
			buffer_object_file_writer.println(",");
	
			buffer_object_file_writer.print  ("			",	grass_scale);
			buffer_object_file_writer.print  (",",			earth_scale);
			buffer_object_file_writer.print  (",",			bottom_scale);
			buffer_object_file_writer.print  (",",			stone_scale);
			buffer_object_file_writer.print  (",",			cobble_scale);
			buffer_object_file_writer.println(",",			sandstone_scale);
		}
		return super.create_part_mesh_and_buffer_object_head(p,buffer_object_file_writer,pcps,system_par,scene_par);
	}
	public component_driver create_component_driver(file_reader fr,boolean rollback_flag,
			part my_component_part,engine_kernel ek,client_request_response request_response)
	{
		return new extended_component_driver(my_component_part,user_parameter_channel_id);
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
