package kernel_buffer;

import kernel_network.client_request_response;
import kernel_transformation.plane;

public class clip_plane_buffer {
	private plane last_clip_plane[];
	
	
	public void destroy()
	{
		if(last_clip_plane!=null)
			for(int i=0,ni=last_clip_plane.length;i<ni;i++)
				last_clip_plane[i]=null;
		last_clip_plane=null;
	}
	
	public clip_plane_buffer()
	{
		last_clip_plane=null;
	}
	public void response(int render_buffer_id,plane clip_plane,client_request_response client_interface)
	{
		if(last_clip_plane==null){
			last_clip_plane=new plane[render_buffer_id+1];
			for(int i=0;i<last_clip_plane.length;i++)
				last_clip_plane[i]=null;
		}else{
			if(last_clip_plane.length<=render_buffer_id){
				plane bak[]=last_clip_plane;
				last_clip_plane=new plane[render_buffer_id+1];
				for(int i=0;i<bak.length;i++)
					last_clip_plane[i]=bak[i];
				for(int i=bak.length;i<last_clip_plane.length;i++)
					last_clip_plane[i]=null;
			}
			if(last_clip_plane[render_buffer_id]==null)
				if(clip_plane==null)
					return;
			if(last_clip_plane[render_buffer_id]!=null)
				if(clip_plane!=null){
					double d2=0,d;
					d=clip_plane.A-last_clip_plane[render_buffer_id].A;	d2+=d*d;
					d=clip_plane.B-last_clip_plane[render_buffer_id].B;	d2+=d*d;
					d=clip_plane.C-last_clip_plane[render_buffer_id].C;	d2+=d*d;
					d=clip_plane.D-last_clip_plane[render_buffer_id].D;	d2+=d*d;
					if(d2<kernel_common_class.const_value.min_value2){
						if(clip_plane.close_clip_plane_flag)
							if(last_clip_plane[render_buffer_id].close_clip_plane_flag)
								return;
						if(!(clip_plane.close_clip_plane_flag))
							if(!(last_clip_plane[render_buffer_id].close_clip_plane_flag))
								return;
					}
				}
		}
		if(clip_plane==null){
			client_interface.print(",[]");
			last_clip_plane[render_buffer_id]=null;
			return;
		}
		client_interface.print(",[");
		client_interface.print(    clip_plane.A);	
		client_interface.print(",",clip_plane.B);	
		client_interface.print(",",clip_plane.C);
		client_interface.print(",",clip_plane.D);
		
		client_interface.print(clip_plane.close_clip_plane_flag?",0]":"]");
		
		last_clip_plane[render_buffer_id]=new plane(clip_plane);
		
		return;
	}
}
