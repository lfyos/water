package driver_camera_operation;

import kernel_part.part;
import kernel_camera.camera;
import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.locate_camera;
import kernel_engine.engine_kernel;
import kernel_transformation.point;
import kernel_transformation.location;
import kernel_driver.component_driver;
import kernel_common_class.const_value;
import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_driver.component_instance_driver;

public class extended_component_driver  extends component_driver
{
	private int modifier_container_id;
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_driver(part my_component_part,int my_modifier_container_id)
	{
		super(my_component_part);
		modifier_container_id=my_modifier_container_id;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;
		
		box my_box;
		int box_parameter_channel_id;
		
		comp.uniparameter.cacaulate_location_flag=true;
		
		if(ek.camera_cont==null) {
			debug_information.println("(ek.camera_cont==null)");
			return;
		}
		if(ek.camera_cont.size()<=0) {
			debug_information.println("(cam_array.length<=0)");
			return;
		}
		String file_name=component_part.directory_name+component_part.material_file_name;
		file_reader fr=new file_reader(file_name,component_part.file_charset);
		if(fr.error_flag()){
			fr.close();
			debug_information.println("camera init file NOT exist!	",file_name);
			return;
		}
		if((box_parameter_channel_id=fr.get_int())<0){
			fr.close();
			debug_information.println("Find box_parameter_channel_id less than zero	",box_parameter_channel_id);
			return;
		}
		if(ek.scene_par.multiparameter_number<=box_parameter_channel_id){
			fr.close();
			debug_information.println("(ek.scene_par.multiparameter_number<=box_parameter_channel_id)	",
					ek.scene_par.multiparameter_number+"/"+box_parameter_channel_id);
			return;
		}
		if((my_box=ek.component_cont.get_effective_box(box_parameter_channel_id))==null) {
			fr.close();
			debug_information.println("((my_box=ek.component_cont.get_effective_box(box_parameter_channel_id))==null)");
			return;
		}
		if(my_box.distance2()<const_value.min_value2) {
			fr.close();
			debug_information.println("(my_box.distance2()<const_value.min_value2)");
			return;
		}
		do{
			int cam_id=fr.get_int();
			if(fr.eof())
				break;
			point dz=new point(fr),dy=new point(fr),dx=dy.cross(dz);
			if((cam_id>=0)&&(cam_id<ek.camera_cont.size())&&(dx.distance2()>const_value.min_value2)){
				dx=dx.expand(1.0);
				dy=dz.cross(dx).expand(1.0);
				dz=dz.expand(1.0);
				location loca=new location(new point(),dx,dy,dz).multiply(location.standard_negative);
				camera cam=ek.camera_cont.get(cam_id);
				locate_camera loca_cam=new locate_camera(cam);
				cam.eye_component.modify_location(loca_cam.locate(my_box,loca),ek.component_cont);
				loca_cam.scale(Math.abs(cam.parameter.scale_value));
				cam.parameter.distance=loca_cam.distance;
			}
		}while(true);
		
		fr.close();

		return;
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_component_instance_driver(comp,driver_id,modifier_container_id);
	}
}