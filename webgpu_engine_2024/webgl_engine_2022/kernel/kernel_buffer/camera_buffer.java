package kernel_buffer;

import java.util.ArrayList;

import kernel_camera.camera;
import kernel_common_class.const_value;
import kernel_scene.client_information;

public class camera_buffer
{
	private int camera_component_id[];
	
	private double distance[],half_fovy_tanl[],near_ratio[],far_ratio[];
	private int projection_type_flag[],light_camera_flag[];
	
	public void destroy()
	{
		camera_component_id=null;
		
		distance=null;
		half_fovy_tanl=null;
		near_ratio=null;
		far_ratio=null;
		projection_type_flag=null;
		light_camera_flag=null;
	}
	
	public camera_buffer(camera my_camera_array[])
	{
		camera_component_id=new int[my_camera_array.length];
		
		projection_type_flag=new int[my_camera_array.length];
		light_camera_flag=new int[my_camera_array.length];
		distance=new double[my_camera_array.length];
		half_fovy_tanl=new double[my_camera_array.length];
		near_ratio=new double[my_camera_array.length];
		far_ratio=new double[my_camera_array.length];
		
		for(int i=0;i<my_camera_array.length;i++){
			camera_component_id[i]	=-1;
			
			projection_type_flag[i]	=-1;
			light_camera_flag[i]	=-1;
			distance[i]				=-1;
			half_fovy_tanl[i]		=-1;
			near_ratio[i]			=-1;
			far_ratio[i]			=-1;
		}
	}
	public void synchronize_camera_buffer(ArrayList<camera> my_camera_array,int camera_id)
	{
		if(my_camera_array==null)
			return;
		if((camera_id<0)||(camera_id>=my_camera_array.size()))
			return;
		camera cam=my_camera_array.get(camera_id);
		
		camera_component_id	[camera_id]=(cam.eye_component==null)?-1:(cam.eye_component.component_id);
		
		distance			[camera_id]=cam.parameter.distance;
		half_fovy_tanl		[camera_id]=cam.parameter.half_fovy_tanl;
		near_ratio			[camera_id]=cam.parameter.near_ratio;
		far_ratio			[camera_id]=cam.parameter.far_ratio;
		projection_type_flag[camera_id]=cam.parameter.projection_type_flag?1:0;
		
		return;
	}
	private void response_one_camera_data(
			response_flag create_flag,client_information ci,
			camera cam,int current_camera_id)
	{
		if(cam==null)
			return;
		if(cam.eye_component==null)
			return;
		
		if(camera_component_id	[current_camera_id]!=cam.eye_component.component_id) {
			if(create_flag.first_item_flag) {
				ci.request_response.print(current_camera_id);
				create_flag.first_item_flag=false;
			}else
				ci.request_response.print(",",current_camera_id);
			
			ci.request_response.print(",0,",camera_component_id[current_camera_id]=cam.eye_component.component_id);
		}
		if(Math.abs(cam.parameter.distance-distance[current_camera_id])>const_value.min_value){
			if(create_flag.first_item_flag) {
				ci.request_response.print(current_camera_id);
				create_flag.first_item_flag=false;
			}else
				ci.request_response.print(",",current_camera_id);
			ci.request_response.print(",1,",distance[current_camera_id]=cam.parameter.distance);
		}
		if(Math.abs(cam.parameter.half_fovy_tanl-half_fovy_tanl[current_camera_id])>const_value.min_value){
			if(create_flag.first_item_flag) {
				ci.request_response.print(current_camera_id);
				create_flag.first_item_flag=false;
			}else
				ci.request_response.print(",",current_camera_id);
			ci.request_response.print(",2,",half_fovy_tanl[current_camera_id]=cam.parameter.half_fovy_tanl);
		}
		if(Math.abs(cam.parameter.near_ratio-near_ratio[current_camera_id])>const_value.min_value){
			if(create_flag.first_item_flag) {
				ci.request_response.print(current_camera_id);
				create_flag.first_item_flag=false;
			}else
				ci.request_response.print(",",current_camera_id);
			ci.request_response.print(",3,",near_ratio[current_camera_id]=cam.parameter.near_ratio);
		}
		if(Math.abs(cam.parameter.far_ratio-far_ratio[current_camera_id])>const_value.min_value){
			if(create_flag.first_item_flag) {
				ci.request_response.print(current_camera_id);
				create_flag.first_item_flag=false;
			}else
				ci.request_response.print(",",current_camera_id);
			ci.request_response.print(",4,",far_ratio[current_camera_id]=cam.parameter.far_ratio);
		}
		int value=cam.parameter.projection_type_flag?1:0;
		if(projection_type_flag[current_camera_id]!=value){
			if(create_flag.first_item_flag) {
				ci.request_response.print(current_camera_id);
				create_flag.first_item_flag=false;
			}else
				ci.request_response.print(",",current_camera_id);
			ci.request_response.print(",5,",projection_type_flag[current_camera_id]=value);
		}
		value=cam.parameter.light_camera_flag?1:0;
		if(light_camera_flag[current_camera_id]!=value){
			if(create_flag.first_item_flag) {
				ci.request_response.print(current_camera_id);
				create_flag.first_item_flag=false;
			}else
				ci.request_response.print(",",current_camera_id);
			
			ci.request_response.print(",6,",light_camera_flag[current_camera_id]=value);
		}
	}
	public void response_camera_buffer_data(
			client_information ci,ArrayList<camera> camera_con)
	{
		ci.request_response.print(",[");
		response_flag create_flag=new response_flag();
		for(int i=0,ni=camera_con.size();i<ni;i++)
			response_one_camera_data(create_flag,ci,camera_con.get(i),i);
		ci.request_response.print("]");
	}
}
