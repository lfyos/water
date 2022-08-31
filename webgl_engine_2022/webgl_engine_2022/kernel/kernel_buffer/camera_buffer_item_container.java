package kernel_buffer;

import kernel_network.client_request_response;
import kernel_transformation.plane;
import kernel_transformation.box;

public class camera_buffer_item_container
{
	private camera_buffer_item current_camera_id,x0y0z0x1y1z1;
	private plane mirror_plane;
	private boolean first_response_mirror_plane;
	
	public void destroy()
	{
		current_camera_id=null;
		x0y0z0x1y1z1=null;
		mirror_plane=null;
	}
	public camera_buffer_item_container()
	{
		current_camera_id	=new camera_buffer_item(0);
		x0y0z0x1y1z1		=new camera_buffer_item(1);
		mirror_plane		=null;
		first_response_mirror_plane=true;
	}
	public void response(client_request_response out,
			plane my_mirror_plane,int my_current_camera_id,box view_volume_box)
	{
		String pre_string="";
		
		double my_x0=view_volume_box.p[0].x,my_y0=view_volume_box.p[0].y,my_z0=view_volume_box.p[0].z;
		double my_x1=view_volume_box.p[1].x,my_y1=view_volume_box.p[1].y,my_z1=view_volume_box.p[1].z;
		
		pre_string=current_camera_id.response(out,new double[]{my_current_camera_id},				pre_string);
		pre_string=x0y0z0x1y1z1.	 response(out,new double[]{my_x0,my_y0,my_z0,my_x1,my_y1,my_z1},pre_string);
		
		boolean response_mirror_flag;
		if(first_response_mirror_plane){
			response_mirror_flag=true;
			first_response_mirror_plane=false;
			mirror_plane=(my_mirror_plane==null)?null:new plane(my_mirror_plane);
		}else if(mirror_plane==null){
			if(my_mirror_plane==null)
				response_mirror_flag=false;
			else{
				response_mirror_flag=true;
				mirror_plane=new plane(my_mirror_plane);
			}
		}else	if(my_mirror_plane==null){
			response_mirror_flag=true;
			mirror_plane=null;
		}else if(my_mirror_plane.test_same_plane(mirror_plane))
			response_mirror_flag=false;
		else{
			response_mirror_flag=true;
			mirror_plane=new plane(my_mirror_plane);
		}
		if(response_mirror_flag){
			if(mirror_plane==null)
				out.print(pre_string,"[2]");
			else{
				out.print(pre_string,"[2");
				out.print(",",mirror_plane.A);	
				out.print(",",mirror_plane.B);	
				out.print(",",mirror_plane.C);	
				out.print(",",mirror_plane.D);	
				out.print("]");
			}
			pre_string=",";
		}
	}
}
