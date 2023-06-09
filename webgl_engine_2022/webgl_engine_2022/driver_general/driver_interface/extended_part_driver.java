package driver_interface;


import kernel_part.part;
import kernel_driver.part_driver;
import kernel_transformation.box;
import kernel_component.component;
import kernel_transformation.point;
import kernel_engine.engine_kernel;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_driver.component_driver;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_driver.part_instance_driver;
import kernel_network.client_request_response;
import kernel_component.component_load_source_container;

public class extended_part_driver extends part_driver
{
	private boolean always_show_flag;
	private double depth_step;
	public extended_part_driver(boolean my_always_show_flag,double my_depth_step)
	{
		super();
		always_show_flag=my_always_show_flag;
		depth_step=my_depth_step;
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
		return new extended_part_driver(always_show_flag,depth_step);
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
		file_reader.get_text(fw,p.directory_name+file_reader.separator(p.material_file_name),p.file_charset);
	}
	public box caculate_part_box(part p,component comp,int driver_id,
			int body_id,int face_id,int primitive_id,int vertex_id,int loop_id,int edge_id,
			point p0,point p1)
	{
		return null;
//		return super.caculate_part_box(p,comp,driver_id,body_id,face_id,primitive_id,vertex_id,loop_id,edge_id,p0,p1);
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
		return new extended_component_driver(my_component_part,	fr.get_boolean(),
				fr.get_double()*depth_step,fr.get_double(),fr.get_double(),
				fr.directory_name+file_reader.separator(fr.get_string()),
				fr.get_charset(),always_show_flag);
	}
	public part_instance_driver create_part_instance_driver(part p,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_part_instance_driver();
	}
}
