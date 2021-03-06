package driver_camera_operation;

import kernel_camera.camera;
import kernel_camera.locate_camera;
import kernel_common_class.const_value;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;

import kernel_transformation.box;
import kernel_transformation.point;

public class extended_component_driver  extends component_driver
{
	private int camera_modifier_id;
	private String init_file_name,init_file_charset;
	
	public void destroy()
	{
		super.destroy();
		init_file_name=null;
	}
	public extended_component_driver(
			part my_component_part,int my_camera_modifier_id,
			String my_init_file_name,String my_init_file_charset)
	{
		super(my_component_part);
		camera_modifier_id=my_camera_modifier_id;
		init_file_name=my_init_file_name;
		init_file_charset=my_init_file_charset;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;
		
		box my_box;
		camera cam_array[];
		int box_parameter_channel_id;
		
		if((cam_array=ek.camera_cont.camera_array)==null)
			return;
		if(cam_array.length<=0)
			return;
		file_reader fr=new file_reader(
			component_part.directory_name+init_file_name,init_file_charset);
		if(fr.error_flag()){
			fr.close();
			return;
		}
		if((box_parameter_channel_id=fr.get_int())<0){
			fr.close();
			return;
		}
		if(ek.scene_par.multiparameter_number<=box_parameter_channel_id){
			fr.close();
			return;
		}
		if((my_box=ek.component_cont.get_effective_box(box_parameter_channel_id))==null) {
			fr.close();
			return;
		}
		if(my_box.distance2()<const_value.min_value2) {
			fr.close();
			return;
		}
		
		do{
			int cam_id=fr.get_int();
			if(fr.eof())
				break;
			double x=fr.get_double(),y=fr.get_double(),z=fr.get_double();
			if((cam_id>=0)&&(cam_id<cam_array.length)){
				locate_camera loca_cam=new locate_camera(cam_array[cam_id]);
				cam_array[cam_id].eye_component.modify_location(
						loca_cam.locate(my_box,null),ek.component_cont);
				cam_array[cam_id].eye_component.modify_location(
						loca_cam.direction_locate(new point(x,y,z),null,false),ek.component_cont);
				loca_cam.scale(Math.abs(cam_array[cam_id].parameter.scale_value),1.0);
				cam_array[cam_id].parameter.distance=loca_cam.distance;
			}
		}while(true);
		
		fr.close();
		return;
	}
	public String [][]assemble_file_name_and_file_charset(file_reader fr,
			engine_kernel ek,client_request_response request_response)
	{
		return null;
	}
	public instance_driver create_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_instance_driver(comp,driver_id,camera_modifier_id);
	}
}