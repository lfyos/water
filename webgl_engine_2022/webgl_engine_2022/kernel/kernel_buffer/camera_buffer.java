package kernel_buffer;

import java.util.ArrayList;

import kernel_camera.camera;
import kernel_camera.camera_parameter;
import kernel_camera.camera_result;
import kernel_common_class.const_value;
import kernel_engine.client_information;
import kernel_transformation.plane;

public class camera_buffer
{
	private double distance[],half_fovy_tanl[],near_ratio[],far_ratio[];
	private int projection_type_flag[],light_camera_flag[];
	private camera_buffer_item_container buffer[];
	
	public void destroy()
	{
		distance=null;
		half_fovy_tanl=null;
		near_ratio=null;
		far_ratio=null;
		projection_type_flag=null;
		light_camera_flag=null;
		if(buffer!=null)
			for(int i=0,ni=buffer.length;i<ni;i++)
				if(buffer[i]!=null) {
					buffer[i].destroy();
					buffer[i]=null;
				}
		buffer=null;
	}
	
	public camera_buffer(camera my_camera_array[])
	{
		projection_type_flag=new int[my_camera_array.length];
		light_camera_flag=new int[my_camera_array.length];
		distance=new double[my_camera_array.length];
		half_fovy_tanl=new double[my_camera_array.length];
		near_ratio=new double[my_camera_array.length];
		far_ratio=new double[my_camera_array.length];
		for(int i=0;i<my_camera_array.length;i++){
			projection_type_flag[i]	=-1;
			light_camera_flag[i]	=-1;
			distance[i]				=-1;
			half_fovy_tanl[i]		=-1;
			near_ratio[i]			=-1;
			far_ratio[i]			=-1;
		}
		buffer=new camera_buffer_item_container[1];
		buffer[0]=null;
	}
	public void synchronize_camera_buffer(ArrayList<camera> my_camera_array,int camera_id)
	{
		if(my_camera_array==null)
			return;
		if((camera_id<0)||(camera_id>=my_camera_array.size()))
			return;
		camera cam=my_camera_array.get(camera_id);
		distance			[camera_id]=cam.parameter.distance;
		half_fovy_tanl		[camera_id]=cam.parameter.half_fovy_tanl;
		near_ratio			[camera_id]=cam.parameter.near_ratio;
		far_ratio			[camera_id]=cam.parameter.far_ratio;
		projection_type_flag[camera_id]=cam.parameter.projection_type_flag?1:0;
		
		return;
	}
	
	private int response_one_camera_data(client_information ci,ArrayList<camera> camera_con,int current_camera_id,int n)
	{
		camera_parameter par=camera_con.get(current_camera_id).parameter;
		
		if(Math.abs(par.distance-distance[current_camera_id])>const_value.min_value){
			ci.request_response.print(((n++)>0)?",[":"[",current_camera_id);
			ci.request_response.print(",0,",distance[current_camera_id]=par.distance);
			ci.request_response.print("]");
		}
		if(Math.abs(par.half_fovy_tanl-half_fovy_tanl[current_camera_id])>const_value.min_value){
			ci.request_response.print(((n++)>0)?",[":"[",current_camera_id);
			ci.request_response.print(",1,",half_fovy_tanl[current_camera_id]=par.half_fovy_tanl);
			ci.request_response.print("]");
		}
		if(Math.abs(par.near_ratio-near_ratio[current_camera_id])>const_value.min_value){
			ci.request_response.print(((n++)>0)?",[":"[",current_camera_id);
			ci.request_response.print(",2,",near_ratio[current_camera_id]=par.near_ratio);
			ci.request_response.print("]");
		}
		if(Math.abs(par.far_ratio-far_ratio[current_camera_id])>const_value.min_value){
			ci.request_response.print(((n++)>0)?",[":"[",current_camera_id);
			ci.request_response.print(",3,",far_ratio[current_camera_id]=par.far_ratio);
			ci.request_response.print("]");
		}
		
		int value=par.projection_type_flag?1:0;
		if(projection_type_flag[current_camera_id]!=value){
			ci.request_response.print(((n++)>0)?",[":"[",current_camera_id);
			ci.request_response.print(",4,",projection_type_flag[current_camera_id]=value);
			ci.request_response.print("]");
		}
		
		value=par.light_camera_flag?1:0;
		if(light_camera_flag[current_camera_id]!=value){
			ci.request_response.print(((n++)>0)?",[":"[",current_camera_id);
			ci.request_response.print(",5,",light_camera_flag[current_camera_id]=value);
			ci.request_response.print("]");
		}
		return n;
	}
	
	public void response_camera_data(plane mirror_plane,camera_result cam_result,
			client_information ci,ArrayList<camera> camera_con,int current_camera_id)
	{
		ci.request_response.print(",[");
		ci.request_response.print("[");
		
		for(int i=0,number=0,ni=camera_con.size();i<ni;i++)
			number=response_one_camera_data(ci,camera_con,i,number);
		
		ci.request_response.print("],[");
		int render_buffer_id=cam_result.get_render_buffer_id(ci);
		if(render_buffer_id>=buffer.length){
			camera_buffer_item_container bak[]=buffer;
			buffer=new camera_buffer_item_container[render_buffer_id+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				buffer[i]=bak[i];
			for(int i=bak.length,ni=buffer.length;i<ni;i++)
				buffer[i]=null;	
		}
		if(buffer[render_buffer_id]==null)
			buffer[render_buffer_id]=new camera_buffer_item_container();

		buffer[render_buffer_id].response(ci.request_response,
				mirror_plane,current_camera_id,cam_result.target.view_volume_box);
		ci.request_response.print("]");
		ci.request_response.print("]");
	}
}
