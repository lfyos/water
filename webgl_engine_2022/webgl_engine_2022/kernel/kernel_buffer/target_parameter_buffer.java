package kernel_buffer;

import kernel_network.client_request_response;
import kernel_render.target_viewport;


public class target_parameter_buffer 
{
	private long target_parameter[][];
	private target_viewport viewport[][];
	
	public void destroy()
	{
		target_parameter=null;
		viewport=null;
	}
	public target_parameter_buffer()
	{
		target_parameter=null;
		viewport=null;
	}
	public void clear_buffer(int target_id)
	{
		if(target_parameter==null)
			return;
		if(target_id<0)
			return;
		if(target_id>=target_parameter.length)
			return;
		target_parameter[target_id]=null;
	}
	public boolean response_parameter(	client_request_response 	client_interface,
		int target_id,					int render_target_id,		int parameter_channel_id,
		int framebuffer_width,			int framebuffer_height,		int render_target_number,
		target_viewport my_viewport[])
	{
		if(target_parameter==null){
			target_parameter=new long[target_id+1][];
			viewport=new target_viewport[target_parameter.length][];
			
			for(int i=0;i<target_parameter.length;i++){
				target_parameter[i]=null;
				viewport[i]=null;
			}
		}else if(target_id>=target_parameter.length){
			long bak_target_parameter[][]=target_parameter;
			target_viewport bak_viewport[][]=viewport;
			target_parameter=new long[target_id+1][];
			viewport=new target_viewport[target_parameter.length][];
	
			for(int i=0;i<bak_target_parameter.length;i++){
				target_parameter[i]=bak_target_parameter[i];
				viewport[i]=bak_viewport[i];
			}
			for(int i=bak_target_parameter.length;i<target_parameter.length;i++){
				target_parameter[i]=null;
				viewport[i]=null;
			}
		};
		long new_target_parameter[]=new long[]
		{
				render_target_id,
				parameter_channel_id,
				framebuffer_width,
				framebuffer_height,
				render_target_number
		};
		boolean should_update_flag;
		if(target_parameter[target_id]==null)
			should_update_flag=true;
		else{
			should_update_flag=false;
			for(int i=0,ni=new_target_parameter.length;i<ni;i++)
				if(target_parameter[target_id][i]!=new_target_parameter[i]){
					should_update_flag=true;
					break;
				}
			if(!should_update_flag)
				should_update_flag=target_viewport.is_not_same(viewport[target_id], my_viewport);
		}
		if(should_update_flag){
			target_parameter[target_id]=new_target_parameter;
			target_viewport vp[];
			if(my_viewport==null){
				viewport[target_id]=null;
				vp=new target_viewport[]{new target_viewport()};
			}else if(my_viewport.length<=0){
				viewport[target_id]=null;
				vp=new target_viewport[]{new target_viewport()};
			}else{
				vp=new target_viewport[my_viewport.length];
				for(int i=0,ni=vp.length;i<ni;i++)
					vp[i]=new target_viewport(my_viewport[i]);
				viewport[target_id]=vp;
			}
			client_interface.print(",[",new_target_parameter);
			
			client_interface.print(",[");
			for(int i=0,ni=vp.length;i<ni;i++){
				client_interface.print((i<=0)?"[":",[",	vp[i].x);
				client_interface.print(",",				vp[i].y);
				client_interface.print(",",				vp[i].width);
				client_interface.print(",",				vp[i].height);
				client_interface.print(",",				vp[i].method_id);
				
				if(vp[i].clear_color!=null){
					for(int j=0,nj=vp[i].clear_color.length;j<nj;j++)
						client_interface.print((j<=0)?",[":",",vp[i].clear_color[j]);
					client_interface.print("]");
				}
				client_interface.print("]");
			}
			client_interface.print("]");
			
			client_interface.print("]");
		}
		return should_update_flag;
	}
}