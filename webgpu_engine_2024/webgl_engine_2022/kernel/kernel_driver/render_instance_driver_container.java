package kernel_driver;

import kernel_render.render;
import kernel_scene.scene_kernel;
import kernel_network.client_request_response;

public class render_instance_driver_container 
{
	private render_instance_driver render_instance_driver_array[];
	
	public render_instance_driver_container(
			scene_kernel sk,client_request_response request_response)
	{
		render_instance_driver_array=new render_instance_driver[0];
		if(sk.render_cont.renders==null)
			return;
		if(sk.render_cont.renders.size()<=0)
			return;
		render_instance_driver_array=new render_instance_driver[sk.render_cont.renders.size()];
		for(int i=0,ni=render_instance_driver_array.length;i<ni;i++) {
			render r=sk.render_cont.renders.get(i);
			render_instance_driver_array[i]=r.driver.create_render_instance_driver(r,sk,request_response);
		}
		return;
	}
	public void destroy()
	{
		if(render_instance_driver_array!=null) {
			for(int i=0,ni=render_instance_driver_array.length;i<ni;i++)
				if(render_instance_driver_array[i]!=null) {
					render_instance_driver_array[i].destroy();
					render_instance_driver_array[i]=null;
				}
			render_instance_driver_array=null;
		}
	}
	public render_instance_driver get_render_instance_driver(render r)
	{
		if((r.render_id<0)||(r.render_id>=render_instance_driver_array.length))
			return null;
		else
			return render_instance_driver_array[r.render_id];
	}
}
