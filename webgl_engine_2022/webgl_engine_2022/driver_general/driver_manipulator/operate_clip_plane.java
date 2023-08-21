package driver_manipulator;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;

import kernel_transformation.plane;
import kernel_common_class.const_value;
import kernel_driver.modifier_driver;
import kernel_transformation.box;

class clip_plane_modifier extends modifier_driver
{
	private boolean destroy_flag;
	private plane source,target;
	public void destroy()
	{
	}
	public void modify(long my_current_time,engine_kernel ek,client_information ci)
	{
		double p=((double)(my_current_time-start_time))/((double)(terminate_time-start_time));
		p=(p<0.0)?0.0:(p>1.0)?1.0:p;
		
		double A=source.A*(1.0-p)+target.A*p;
		double B=source.B*(1.0-p)+target.B*p;
		double C=source.C*(1.0-p)+target.C*p;
		double D=source.D*(1.0-p)+target.D*p;
		
		if((A*A+B*B+C*C)>const_value.min_value2)
			(ci.clip_plane=new plane(A,B,C,D)).close_clip_plane_flag=true;
		else if(destroy_flag)
			ci.clip_plane=null;
		else
			(ci.clip_plane=target).close_clip_plane_flag=true;
	}
	public void last_modify(long my_current_time,engine_kernel ek,client_information ci,boolean terminated_flag)
	{
		if(destroy_flag)
			ci.clip_plane=null;
		else
			(ci.clip_plane=target).close_clip_plane_flag=true;
	}
	public boolean can_start(long my_current_time,engine_kernel ek,client_information ci)
	{
		return true;
	}
	public clip_plane_modifier(boolean my_destroy_flag,
			plane my_source,plane my_target,long my_start_time,long my_terminate_time)
	{
		super(my_start_time,my_terminate_time);
		
		destroy_flag=my_destroy_flag;
		source=new plane(my_source);
		target=new plane(my_target);
	}
}
public class operate_clip_plane
{
	public static void clip_plane_request(int modifier_container_id,engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("event_operation"))==null)
			return;
		long my_start_time=ek.modifier_cont[modifier_container_id].get_timer().get_current_time();
		long my_terminate_time=ci.display_camera_result.cam.parameter.switch_time_length+my_start_time;
		
		box my_box;
		plane source_pl,target_pl;
		
		switch(str.toLowerCase()) {
		case "off":
		case "no":
		case "false":
			if((source_pl=ci.clip_plane)==null)
				break;
			target_pl=null;
			if((my_box=ci.display_component_collector.caculate_box(false))==null)
				if((my_box=ci.display_component_collector.caculate_box(true))==null)
					target_pl=ci.display_camera_result.near_plane;
			if(target_pl==null) {
				target_pl=new plane(source_pl);
				for(int i=0;i<2;i++)
					for(int j=0;j<2;j++)
						for(int k=0;k<2;k++){
							double diff=target_pl.test(
									my_box.p[i].x,my_box.p[j].y,my_box.p[k].z);
							if(diff>0)
								target_pl.D-=diff;
						}
			}
			ek.modifier_cont[modifier_container_id].add_modifier(
				new clip_plane_modifier(true,source_pl,target_pl,my_start_time,my_terminate_time));
			break;
		case "on":
		case "yes":
		case "true":
			source_pl=ci.clip_plane;
			target_pl=new plane(ci.display_camera_result.center_point,ci.display_camera_result.eye_point);
			
			if(source_pl==null){
				if((my_box=ci.display_component_collector.caculate_box(false))==null)
					if((my_box=ci.display_component_collector.caculate_box(true))==null)
						source_pl=ci.display_camera_result.near_plane;
				if(source_pl==null){	
					source_pl=new plane(target_pl);
					for(int i=0;i<2;i++)
						for(int j=0;j<2;j++)
							for(int k=0;k<2;k++) {
								double diff_this;
								if((diff_this=source_pl.test(my_box.p[i].x,my_box.p[j].y,my_box.p[k].z))>0)
									source_pl.D-=diff_this;
							}
				}
			}
			ek.modifier_cont[modifier_container_id].add_modifier(
				new clip_plane_modifier(false,source_pl,target_pl,my_start_time,my_terminate_time));
			break;
		}
	}
}
