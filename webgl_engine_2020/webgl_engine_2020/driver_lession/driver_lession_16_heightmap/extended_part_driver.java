package driver_lession_16_heightmap;

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
import kernel_part.create_grid;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_part.part_container_for_part_search;
import kernel_transformation.box;
import kernel_transformation.point;

final class my_create_grid extends create_grid
{
	public my_create_grid(String file_name,int width,int height,String file_system_charset)
	{
		do_create(file_name,true,width,height,
				new String[] {"vertex","normal","texture"},
				file_system_charset);
	}
}
public class extended_part_driver extends part_driver
{
	private int grid_width,grid_height;
	private double model_scale[],texture_scale[];
	
	public extended_part_driver(part p,system_parameter system_par,client_request_response request_response)
	{
		super();
		file_reader f=new file_reader(p.directory_name+p.material_file_name,p.file_charset);
		
		grid_width		=f.get_int();
		grid_height		=f.get_int();
		
		model_scale		=new double[] {f.get_double(),f.get_double(),f.get_double()};
		texture_scale	=new double[] {f.get_double(),f.get_double()};
		
		f.close();
	}
	public void destroy()
	{	
		super.destroy();
		model_scale=null;
		texture_scale=null;
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
	public part_rude create_part_mesh_and_buffer_object_head(
			part p,file_writer buffer_object_file_writer,
			part_container_for_part_search pcps,system_parameter system_par)
	{
		String file_name=p.directory_name+p.mesh_file_name;
		long t=(new File(file_name)).lastModified();

		new my_create_grid(file_name,grid_width,grid_height,p.file_charset);
		
		(new File(file_name)).setLastModified(t);
		
		
		buffer_object_file_writer.println("\t\t{");
		
		buffer_object_file_writer.print  ("\t\t\t\"model_scale\"\t:\t");
		buffer_object_file_writer.print  ("[\t",model_scale[0]);
		buffer_object_file_writer.print  (",",model_scale[1]);
		buffer_object_file_writer.print  (",",model_scale[2]);
		buffer_object_file_writer.println(",1\t],");
		
		buffer_object_file_writer.print  ("\t\t\t\"texture_scale\"\t:\t");
		buffer_object_file_writer.print  ("[\t",texture_scale[0]);
		buffer_object_file_writer.print  (",",texture_scale[1]);
		buffer_object_file_writer.println(",0,1\t]");
		
		buffer_object_file_writer.println("\t\t}");

		file_writer.file_copy_with_brother(
				p.directory_name+p.mesh_file_name,buffer_object_file_writer.directory_name);
		
		return super.create_part_mesh_and_buffer_object_head(p,buffer_object_file_writer,pcps,system_par);
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
		box b=super.caculate_part_box(p,comp,driver_id,
				body_id,face_id,loop_id,edge_id,point_id,p0,p1);
		if(b==null)
			return null;
		else
			return new box(
				new point(b.p[0].y*model_scale[0],	0,				b.p[0].x*model_scale[2]),
				new point(b.p[1].y*model_scale[0],	model_scale[1],	b.p[1].x*model_scale[2]));
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
