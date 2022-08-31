package kernel_camera;

import kernel_component.component;
import kernel_driver.modifier_container;
import kernel_transformation.location;

public class camera {
	public camera_parameter parameter;	
	public component eye_component;
	
	public void destroy()
	{
		parameter=null;
		eye_component=null;
		
		mark_start_location=null;
		mark_start_parameter=null;		
			
		if(start_location!=null)
			for(int i=0,ni=start_location.length;i<ni;i++)
				start_location[i]=null;
		start_location=null;
		start_parameter=null;;
	}

	public camera(component my_eye_component,camera_parameter my_parameter,int max_camera_stack_number)
	{
		eye_component=my_eye_component;	
		parameter=my_parameter;
		
		start_location=new location[max_camera_stack_number];
		start_parameter=new camera_parameter[max_camera_stack_number];
		for(int i=0;i<(start_location.length);i++){
			start_location[i]=null;
			start_parameter[i]=null;
		}
		restore_stack_pointer=0;
	
		mark_start_location=null;
		mark_start_parameter=null;
	}
	private location mark_start_location;
	private camera_parameter mark_start_parameter;		
		
	private location start_location[];
	private camera_parameter start_parameter[];
	private int restore_stack_pointer;
	
	public void mark_restore_stack()
	{
		mark_start_location		=new location(eye_component.move_location);
		mark_start_parameter	=new camera_parameter(parameter);
	}
	public boolean push_restore_stack(
			modifier_container modifier_cont,boolean switch_camera_flag,
			location terminate_location,camera_parameter terminate_parameter)
	{
		if((mark_start_location==null)||(mark_start_parameter==null))
			return false;
		if((terminate_location==null)||(terminate_parameter==null))
			return false;
		if(	  (location.is_not_same_location(mark_start_location,terminate_location))
			||(camera_parameter.is_not_same_parameter(mark_start_parameter, terminate_parameter)))
		{
			int max_camera_return_stack_number=start_location.length;

			start_location[restore_stack_pointer]	=new location(mark_start_location);
			start_parameter[restore_stack_pointer]	=new camera_parameter(mark_start_parameter);
			restore_stack_pointer++;
			restore_stack_pointer%=max_camera_return_stack_number;
			if(switch_camera_flag){
				camera_modifier cm=new camera_modifier(this,
						terminate_location,terminate_parameter,
						modifier_cont.get_timer().get_current_time());
				modifier_cont.add_modifier(cm);
			}
			return true;
		}
		return false;
	}
	public void pop_restore_stack(modifier_container modifier_cont)
	{
		int max_camera_return_stack_number=start_location.length;
		
		restore_stack_pointer--;
		restore_stack_pointer+=max_camera_return_stack_number;
		restore_stack_pointer%=max_camera_return_stack_number;
		
		location my_location=start_location[restore_stack_pointer];
		start_location[restore_stack_pointer]=null;
		
		camera_parameter my_parameter=start_parameter[restore_stack_pointer];
		start_parameter[restore_stack_pointer]=null;
		
		if(my_location!=null){
			camera_modifier cm=new camera_modifier(this,my_location,my_parameter,
					modifier_cont.get_timer().get_current_time());
			modifier_cont.add_modifier(cm);
			return;
		}
	}
}
