package driver_camera_operation;

import kernel_part.part;
import kernel_camera.camera;
import kernel_scene.scene_kernel;
import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.locate_camera;
import kernel_transformation.point;
import kernel_transformation.location;
import kernel_driver.component_driver;
import kernel_common_class.const_value;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_driver.component_instance_driver;

public class extended_component_driver  extends component_driver
{
	private int modifier_container_id;
	private change_name title_change_name;
	
	public void destroy()
	{
		super.destroy();
		if(title_change_name!=null) {
			title_change_name.destroy();
			title_change_name=null;
		}
	}
	public extended_component_driver(part my_component_part,
			int my_modifier_container_id,change_name my_title_change_name)
	{
		super(my_component_part);
		modifier_container_id=my_modifier_container_id;
		title_change_name=my_title_change_name;
	}
	public void initialize_component_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
//		String component_directory_name			=comp.component_directory_name;
//		String scene_directory_name				=sk.create_parameter.scene_directory_name;
//		String parameter_directory_name			=sk.scene_par.directory_name;
//		String extra_parameter_directory_name	=sk.scene_par.extra_directory_name;
		
		box my_box;
		int box_parameter_channel_id;
		
		comp.uniparameter.caculate_location_flag=true;
		
		if(sk.camera_cont==null) {
			debug_information.println("(sk.camera_cont==null)");
			return;
		}
		if(sk.camera_cont.size()<=0){
			debug_information.println("(cam_array.length<=0)");
			return;
		}
		String file_name=component_part.directory_name+component_part.material_file_name;
		file_reader fr=new file_reader(file_name,component_part.file_charset);
		if(fr.error_flag()){
			fr.close();
			debug_information.println("camera material file error:	",file_name);
			return;
		}
		fr.get_string();
		file_name=fr.directory_name+fr.get_string();
		fr.close();
		
		fr=new file_reader(file_name,component_part.file_charset);

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
		if(box_parameter_channel_id>=sk.scene_par.multiparameter_number){
			fr.close();
			debug_information.println("(sk.scene_par.multiparameter_number<=box_parameter_channel_id)	",
					sk.scene_par.multiparameter_number+"/"+box_parameter_channel_id);
			return;
		}
		
		if((my_box=sk.component_cont.get_effective_box(box_parameter_channel_id))==null) 
			my_box=new box(-0.5,-0.5,-0.5,0.5,0.5,0.5);
		else if(my_box.distance2()<const_value.min_value2) 
			my_box=new box(-0.5,-0.5,-0.5,0.5,0.5,0.5);
		
		do{
			int cam_id=fr.get_int();
			if(fr.eof())
				break;
			point dz=new point(fr),dy=new point(fr),dx=dy.cross(dz);
		
			if(cam_id<0)
				continue;
			if(cam_id>=sk.camera_cont.size())
				continue;
			if(dx.distance2()<const_value.min_value2)
				continue;

			dx=dx.expand(1.0);
			dy=dz.cross(dx).expand(1.0);
			dz=dz.expand(1.0);

			location loca=new location(new point(),dx,dy,dz).multiply(location.standard_negative);
			camera cam=sk.camera_cont.get(cam_id);
			locate_camera loca_cam=new locate_camera(cam);
			
			loca=loca_cam.locate(my_box,loca);
			cam.eye_component.set_component_move_location(loca,sk.component_cont);
			
			loca_cam.scale(Math.abs(cam.parameter.scale_value));
			
			cam.parameter.distance		=loca_cam.distance;
			cam.parameter.bak_distance	=loca_cam.distance;
		}while(true);
		
		fr.close();

		return;
	}
	public void create_component_driver_initialization_data(
			file_writer fw,component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
		String camera_body_title=title_change_name.search_change_name(
				"camera_body_title+"+request_response.language_str,"body");
		String camera_face_title=title_change_name.search_change_name(
				"camera_face_title+"+request_response.language_str,"face");
		return new extended_component_instance_driver(comp,driver_id,
					modifier_container_id,camera_body_title,camera_face_title);
	}
}