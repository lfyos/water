package driver_mouse_modify_location;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_driver.part_driver;
import kernel_transformation.box;
import kernel_component.component;
import kernel_transformation.point;
import kernel_driver.component_driver;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_driver.part_instance_driver;
import kernel_network.client_request_response;
import kernel_component.component_load_source_container;

public class extended_part_driver extends part_driver
{
	private String material_file_name;
	private String mark_component_name,movement_component_name;
	private String movement_abstract_menu_component_name;
	
	public extended_part_driver(String my_material_file_name,
			String my_mark_component_name,String my_movement_component_name,
			String my_movement_abstract_menu_component_name)
	{
		super();
		material_file_name				=new String(my_material_file_name);
		mark_component_name				=new String(my_mark_component_name);
		movement_component_name			=new String(my_movement_component_name);
		movement_abstract_menu_component_name=new String(my_movement_abstract_menu_component_name);
	}
	public void destroy()
	{	
		super.destroy();
		
		material_file_name=null;
		mark_component_name=null;
		movement_component_name=null;
		movement_abstract_menu_component_name=null;
	}
	public void initialize_part_driver(part p,scene_kernel sk,client_request_response request_response)
	{
	}
	public part_driver clone(part parent,part p,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		return new extended_part_driver(material_file_name,
				mark_component_name,movement_component_name,
				movement_abstract_menu_component_name);
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
		file_reader material_fr=new file_reader(
			my_component_part.directory_name+my_component_part.material_file_name,my_component_part.file_charset);
		component_driver ret_val=new extended_component_driver(my_component_part,
				new double[]
				{
					material_fr.get_double(),material_fr.get_double(),
					material_fr.get_double(),material_fr.get_double()
				},
				material_fr.get_double(),	material_fr.get_double(),	material_fr.get_boolean(),
				material_fr.get_boolean(),	material_fr.get_boolean(),	material_fr.get_int());
		material_fr.close();
		return ret_val;
	}
	public part_instance_driver create_part_instance_driver(part p,
			scene_kernel sk,client_request_response request_response)
	{
		return new extended_part_instance_driver(
				mark_component_name,movement_component_name,movement_abstract_menu_component_name);
	}
}
