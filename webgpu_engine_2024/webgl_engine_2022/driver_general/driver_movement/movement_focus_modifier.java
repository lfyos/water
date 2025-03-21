package driver_movement;

import kernel_driver.modifier_driver;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_transformation.location;

public class movement_focus_modifier extends modifier_driver
{
	private movement_parameter parameter;
	private long current_movement_id;
	
	private movement_suspend suspend;
	private movement_match_container match;
	
	private movement_switch_camera_modifier swcm;
	
	private double scale_value;
	private location direction,start_location,terminate_location;
	private int component_id;
	private int follow_component_id[];
	private location follow_component_location[];
	
	private String node_name,description,sound_file_name;

	public void destroy()
	{
		super.destroy();

		parameter=null;
		suspend=null;
		match=null;
		
		swcm=null;
		direction=null;
		start_location=null;
		terminate_location=null;
		follow_component_id=null;
		follow_component_location=null;
	}
	
	public movement_focus_modifier(movement_parameter my_parameter,long my_current_movement_id,
			movement_suspend my_suspend,movement_match_container my_match,
			int my_component_id,int my_follow_component_id[],location my_follow_component_location[],
			long start_time,movement_switch_camera_modifier my_swcm,
			double my_scale_value,location my_direction,
			location my_start_location, location my_terminate_location,
			String my_node_name,String my_description,String my_sound_file_name)
	{
		super(start_time,start_time);
		
		parameter=my_parameter;
		current_movement_id=my_current_movement_id;
		suspend=my_suspend;
		match=my_match;
		
		component_id=my_component_id;
		follow_component_id=my_follow_component_id;
		follow_component_location=my_follow_component_location;
		swcm=my_swcm;
		scale_value=my_scale_value;
		direction=my_direction;
		start_location=my_start_location;
		terminate_location=my_terminate_location;
		
		node_name		=my_node_name;
		description		=my_description;
		sound_file_name	=my_sound_file_name;
	}
	public void modify(long my_current_time,scene_kernel sk,client_information ci)
	{
		super.modify(my_current_time,sk,ci);
	}
	public void last_modify(long my_current_time,scene_kernel sk,client_information ci,boolean terminated_flag)
	{
		super.last_modify(my_current_time,sk,ci,terminated_flag);
		
		if(!terminated_flag)
			return;
		parameter.current_movement_id=current_movement_id;
		if(follow_component_id!=null)
			for(int i=0,ni=follow_component_id.length;i<ni;i++){
				swcm.register_move_component(
						component_id,follow_component_id[i],scale_value,direction,
						start_location.multiply(follow_component_location[i]),
						terminate_location.multiply(follow_component_location[i]),
						node_name,description,null);
			}
		suspend.register_match_and_component(match,component_id,follow_component_id,sk.component_cont);
		swcm.register_move_component(component_id,component_id,scale_value,direction,
				start_location,terminate_location,node_name,description,sound_file_name);
	}
	public boolean can_start(long my_current_time,scene_kernel sk,client_information ci)
	{
		return super.can_start(my_current_time,sk,ci);
	}
}
