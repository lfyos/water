package driver_lession_06_create_grid_with_movement;

import java.io.File;

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
import kernel_part.create_grid;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_part.part_container_for_part_search;
import kernel_transformation.box;
import kernel_transformation.point;

final class my_create_grid extends create_grid
{
	public double[][]vertex_data_modifier(double old_vertex_data[][])
	{
		double new_vertex_data[][]=new double[old_vertex_data.length][];
		for(int i=0,ni=old_vertex_data.length;i<ni;i++){
			new_vertex_data[i]=new double[old_vertex_data[i].length];
			for(int j=0,nj=old_vertex_data[i].length;j<nj;j++)
				new_vertex_data[i][j]=old_vertex_data[i][j];
			(new_vertex_data[i][0])*=width;
			(new_vertex_data[i][1])*=height;
		}
		return new_vertex_data;
	}
	public my_create_grid(String file_name,int my_width,int my_height,String file_charset)
	{
		super(file_name,file_charset,my_width,my_height,new String[] {"vertex","normal","texture"});
	}
}
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
	public void response_init_part_data(part p,engine_kernel ek,client_information ci)
	{
	}
	public part_driver clone(part parent,part p,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		return new extended_part_driver(p,system_par,request_response);
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
			String file_name=p.directory_name+p.mesh_file_name;
			long t=(new File(file_name)).lastModified();
			int width=128,height=64;
			new my_create_grid(file_name,width,height,p.file_charset);
			(new File(file_name)).setLastModified(t);
			file_writer.file_copy(new File(file_name).getParent()+File.separatorChar+"texture.png", 
					buffer_object_file_writer.directory_name+"texture.png");
		}
		return super.create_part_mesh_and_buffer_object_head(p,buffer_object_file_writer,pcps,system_par,scene_par);
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
	public String[] response_event(part p,engine_kernel ek,client_information ci)
	{
		return super.response_event(p,ek,ci);
	}
}
