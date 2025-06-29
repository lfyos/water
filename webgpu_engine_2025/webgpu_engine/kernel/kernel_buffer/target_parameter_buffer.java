package kernel_buffer;

import java.util.ArrayList;

import kernel_render.render_target;
import kernel_common_class.const_value;
import kernel_common_class.jason_string;
import kernel_network.client_request_response;

public class target_parameter_buffer 
{
	private ArrayList<render_target> buffer;
	
	public void destroy()
	{
		if(buffer!=null) { 
			buffer.clear();
			buffer=null;
		}
	}
	public target_parameter_buffer()
	{
		buffer=new ArrayList<render_target>();
	}
	public void response_parameter(render_target rt,client_request_response client_interface)
	{
		for(int last_id;(last_id=buffer.size())<=rt.target_id;)
			buffer.add(last_id,null);
		
		render_target old_rt=buffer.get(rt.target_id);
		buffer.set(rt.target_id,rt);
		
		int print_number=0;
		client_interface.print(rt.target_id+",[");
		
		do{
			if(old_rt!=null)
				if(old_rt.target_id_from==rt.target_id_from)
					break;
			client_interface.	print(((print_number++)<=0)?"0,":",0,",rt.target_id_from);
		}while(false);

		do{
			if(old_rt!=null)
				if(!(old_rt.target_or_bundle_flag^rt.target_or_bundle_flag))
					break;
			if(rt.target_or_bundle_flag)
				client_interface.print(((print_number++)<=0)?"1":",1");
			else
				client_interface.print(((print_number++)<=0)?"2":",2");
		}while(false);
		
		do{
			if(old_rt!=null)
				if(old_rt.target_comonent_id==rt.target_comonent_id)
					if(old_rt.target_driver_id==rt.target_driver_id)
						if(old_rt.target_texture_id==rt.target_texture_id)
							if(old_rt.target_name.compareTo(rt.target_name)==0)
								break;
			
			client_interface.	print(((print_number++)<=0)?"3":",3").
								print(",",rt.target_comonent_id).
								print(",",rt.target_driver_id).
								print(",",rt.target_texture_id).
								print(",",jason_string.change_string(rt.target_name.trim()));
		}while(false);

		do{
			if(old_rt!=null)
				if(old_rt.camera_id==rt.camera_id)
					break;
			client_interface.	print(((print_number++)<=0)?"4,":",4,",rt.camera_id);
		}while(false);

		do{
			if(old_rt!=null)
				if(old_rt.view_volume_box.p[0].sub(rt.view_volume_box.p[0]).distance2()<const_value.min_value2)
					if(old_rt.view_volume_box.p[1].sub(rt.view_volume_box.p[1]).distance2()<const_value.min_value2)
						break;
			client_interface.	print(((print_number++)<=0)?"5":",5").
								print(",",	rt.view_volume_box.p[0].x).
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
				client_interface.	print(((print_number++)<=0)?"6":",6");
			else 
				client_interface.	print(((print_number++)<=0)?"7":",7").
									print(",",	rt.clip_plane.A).
									print(",",	rt.clip_plane.B).
									print(",",	rt.clip_plane.C).
									print(",",	rt.clip_plane.D);
		}while(false);
		
		do{
			if(old_rt!=null) {
				if(!((old_rt.camera_transformation_matrix!=null)^(rt.camera_transformation_matrix!=null))){
					if(rt.camera_transformation_matrix==null)
						break;
					double old_data[]=old_rt.camera_transformation_matrix.get_location_data();
					double new_data[]=    rt.camera_transformation_matrix.get_location_data();
					double sum=0,diff;
					for(int i=0,ni=old_data.length;i<ni;i++) {
						diff=new_data[i]-old_data[i];
						sum+=diff*diff;
					}
					if(sum<const_value.min_value2)
						break;
				}
			}
			if(rt.camera_transformation_matrix==null)
				client_interface.print(((print_number++)<=0)?"8":",8");
			else{
				client_interface.print(((print_number++)<=0)?"9":",9");
				double new_data[]=rt.camera_transformation_matrix.get_location_data();
				for(int i=0,ni=new_data.length;i<ni;i++)
					client_interface.print(",",new_data[i]);
			}
		}while(false);

		do{
			if(old_rt!=null)
				if(!((old_rt.main_display_target_flag)^(rt.main_display_target_flag)))
					break;
			if(rt.main_display_target_flag)
				client_interface.	print(((print_number++)<=0)?"10":",10");
			else
				client_interface.	print(((print_number++)<=0)?"11":",11");
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
			client_interface.	print(((print_number++)<=0)?"12":",12").
								print(",",	rt.target_view.view_x0).
								print(",",	rt.target_view.view_y0).
								print(",",	rt.target_view.view_width).
								print(",",	rt.target_view.view_height).
								print(",",	rt.target_view.whole_view_width).
								print(",",	rt.target_view.whole_view_height);
		}while(false);

		client_interface.print("]");
	}
}