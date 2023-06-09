package kernel_buffer;

import java.util.ArrayList;

import kernel_render.render_target;
import kernel_common_class.const_value;
import kernel_network.client_request_response;

public class target_parameter_buffer 
{
	private ArrayList<render_target> render_target_buffer;
	
	public void destroy()
	{
		if(render_target_buffer!=null) {
			render_target_buffer.clear();
			render_target_buffer=null;
		}
	}
	public target_parameter_buffer()
	{
		render_target_buffer=new ArrayList<render_target>();
	}
	public void response_parameter(int render_buffer_id,
			render_target rt,client_request_response client_interface)
	{
		for(int last_id;(last_id=render_target_buffer.size())<=render_buffer_id;)
			render_target_buffer.add(last_id,null);
		
		render_target old_rt=render_target_buffer.get(render_buffer_id);
		render_target_buffer.set(render_buffer_id,rt);
		
		int print_number=0;
		client_interface.print(",[");

		do{
			if(old_rt!=null)
				if(old_rt.target_comonent_id==rt.target_comonent_id)
					if(old_rt.target_driver_id==rt.target_driver_id)
						break;
			client_interface.	print(((print_number++)<=0)?"0,":",0,",rt.target_comonent_id).print(",",rt.target_driver_id);
		}while(false);

		do{
			if(old_rt!=null)
				if(old_rt.target_texture_id==rt.target_texture_id)
					break;
			client_interface.	print(((print_number++)<=0)?"1,":",1,",rt.target_texture_id);
		}while(false);

		do{
			if(old_rt!=null)
				if(old_rt.camera_id==rt.camera_id)
					break;
			client_interface.	print(((print_number++)<=0)?"2,":",2,",rt.camera_id);
		}while(false);

		do{
			if(old_rt!=null)
				if(old_rt.view_volume_box.p[0].sub(rt.view_volume_box.p[0]).distance2()<const_value.min_value2)
					if(old_rt.view_volume_box.p[1].sub(rt.view_volume_box.p[1]).distance2()<const_value.min_value2)
						break;
			client_interface.	print(((print_number++)<=0)?"3,":",3,").
								print(		rt.view_volume_box.p[0].x).
								print(",",	rt.view_volume_box.p[0].y).
								print(",",	rt.view_volume_box.p[0].z).
								print(",",	rt.view_volume_box.p[1].x).
								print(",",	rt.view_volume_box.p[1].y).
								print(",",	rt.view_volume_box.p[1].z);
		}while(false);
		
		do{
			if(old_rt!=null)
				if(!((old_rt.clip_plane!=null)^(rt.clip_plane!=null))) {
					if(rt.clip_plane==null)
						break;
					double diff,sum=0;
					diff=old_rt.clip_plane.A-rt.clip_plane.A;	sum+=diff*diff;
					diff=old_rt.clip_plane.B-rt.clip_plane.B;	sum+=diff*diff;
					diff=old_rt.clip_plane.C-rt.clip_plane.C;	sum+=diff*diff;
					diff=old_rt.clip_plane.D-rt.clip_plane.D;	sum+=diff*diff;
					if(sum<const_value.min_value2)
						break;
				}
			if(rt.clip_plane==null)
				client_interface.	print(((print_number++)<=0)?"4":",4");
			else 
				client_interface.	print(((print_number++)<=0)?"5,":",5,").
									print(		rt.clip_plane.A).
									print(",",	rt.clip_plane.B).
									print(",",	rt.clip_plane.C).
									print(",",	rt.clip_plane.D);
		}while(false);
		
		do{
			if(old_rt!=null)
				if(!((old_rt.mirror_plane!=null)^(rt.mirror_plane!=null))){
					if(rt.mirror_plane==null)
						break;
					double diff,sum=0;
					diff=old_rt.mirror_plane.A-rt.mirror_plane.A;	sum+=diff*diff;
					diff=old_rt.mirror_plane.B-rt.mirror_plane.B;	sum+=diff*diff;
					diff=old_rt.mirror_plane.C-rt.mirror_plane.C;	sum+=diff*diff;
					diff=old_rt.mirror_plane.D-rt.mirror_plane.D;	sum+=diff*diff;
					if(sum<const_value.min_value2)
						break;
				}
			if(rt.mirror_plane==null)
				client_interface.	print(((print_number++)<=0)?"6":",6");
			else 
				client_interface.	print(((print_number++)<=0)?"7,":",7,").
									print(		rt.mirror_plane.A).
									print(",",	rt.mirror_plane.B).
									print(",",	rt.mirror_plane.C).
									print(",",	rt.mirror_plane.D);
		}while(false);

		do{
			if(old_rt!=null)
				if(!((old_rt.main_display_target_flag)^(rt.main_display_target_flag)))
					break;
			if(rt.main_display_target_flag)
				client_interface.	print(((print_number++)<=0)?"8":",8");
			else
				client_interface.	print(((print_number++)<=0)?"9":",9");
		}while(false);
		
		do{
			if(old_rt!=null)
				if(old_rt.target_view.view_x0==rt.target_view.view_x0)
					if(old_rt.target_view.view_y0==rt.target_view.view_y0)
						if(old_rt.target_view.view_width==rt.target_view.view_width)
							if(old_rt.target_view.view_height==rt.target_view.view_height)
								if(old_rt.target_view.whole_view_width==rt.target_view.whole_view_width)
									if(old_rt.target_view.whole_view_height==rt.target_view.whole_view_height)
										break;
			client_interface.	print(((print_number++)<=0)?"10,":",10,").
								print(		rt.target_view.view_x0).
								print(",",	rt.target_view.view_y0).
								print(",",	rt.target_view.view_width).
								print(",",	rt.target_view.view_height).
								print(",",	rt.target_view.whole_view_width).
								print(",",	rt.target_view.whole_view_height);
		}while(false);
		
		client_interface.print("]");

		return;
	}
}