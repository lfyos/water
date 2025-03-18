package kernel_driver;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_transformation.box;
import kernel_component.component;
import kernel_transformation.point;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_part.caculate_part_items;
import kernel_common_class.const_value;
import kernel_network.client_request_response;
import kernel_component.component_load_source_container;

public class part_driver
{
	public void destroy()
	{
	}
	public void initialize_part_driver(part p,scene_kernel sk,client_request_response request_response)
	{	
	}
	public part_driver()
	{
	}
	public part_driver clone(part parent,part p,
			client_request_response request_response,
			system_parameter system_par,scene_parameter scene_par)
	{
		return new part_driver();
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
		if(p.part_mesh==null)
			return null;
		caculate_part_items cpi=new caculate_part_items(p,
				body_id,face_id,primitive_id,vertex_id,loop_id,edge_id);
		if((p0==null)||(p1==null))
			return (cpi.my_box==null)?null:new box(cpi.my_box);
		if(p1.sub(p0).distance2()<const_value.min_value2)
			return (cpi.my_box==null)?null:new box(cpi.my_box);
		
		point my_point=cpi.caculate_focus_point(p0,p1);
		return (my_point==null)?null:new box(my_point);
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
		return new component_driver(my_component_part);
	}
	public part_instance_driver create_part_instance_driver(part p,
			scene_kernel sk,client_request_response request_response)
	{
		return new part_instance_driver();
	}
}
