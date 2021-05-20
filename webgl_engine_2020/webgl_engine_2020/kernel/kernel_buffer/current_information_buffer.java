package kernel_buffer;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class current_information_buffer
{
	private int render_buffer_id,target_id,target_viewport_id,camera_id,camera_component_id;
	private int high_or_low_precision_flag;
	
	public current_information_buffer()
	{
		render_buffer_id=-1;
		target_id=-1;
		target_viewport_id=-1;
		camera_id=-1;
		camera_component_id=-1;
		high_or_low_precision_flag=-1;
	}
	public void response_current(engine_kernel ek,client_information ci,int my_target_viewport_id)
	{
		String str="";
		ci.request_response.print(",[");
		
		int new_render_buffer_id=ci.display_camera_result.get_render_buffer_id(ci);
		if(new_render_buffer_id!=render_buffer_id) {
			render_buffer_id=new_render_buffer_id;
			ci.request_response.print(str+"[0,",render_buffer_id);
			ci.request_response.print("]");
			str=",";
		}
		if(ci.display_camera_result.target.target_id!=target_id){
			target_id=ci.display_camera_result.target.target_id;
			ci.request_response.print(str+"[1,",target_id);
			ci.request_response.print("]");
			str=",";
		}
		if(my_target_viewport_id!=target_viewport_id){
			target_viewport_id=my_target_viewport_id;
			ci.request_response.print(str+"[2,",target_viewport_id);
			ci.request_response.print("]");
			str=",";
		}
		if(ci.display_camera_result.target.camera_id!=camera_id){
			camera_id=ci.display_camera_result.target.camera_id;
			ci.request_response.print(str+"[3,",camera_id);
			ci.request_response.print("]");
			str=",";
		}
		if(ci.display_camera_result.cam.eye_component!=null)
			if(ci.display_camera_result.cam.eye_component.component_id!=camera_component_id){
				camera_component_id=ci.display_camera_result.cam.eye_component.component_id;
				ci.request_response.print(str+"[4,",camera_component_id);
				ci.request_response.print("]");
				str=",";
			}
		int new_high_or_low_precision_flag=ci.parameter.high_or_low_precision_flag?1:0;
		if(new_high_or_low_precision_flag!=high_or_low_precision_flag) {
			high_or_low_precision_flag=new_high_or_low_precision_flag;
			ci.request_response.print(str+"[6,",high_or_low_precision_flag);
			ci.request_response.print("]");
			str=",";
		}
		
		ci.request_response.print("]");
	}
}
