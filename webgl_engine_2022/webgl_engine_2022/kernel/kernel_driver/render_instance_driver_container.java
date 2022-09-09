package kernel_driver;

import kernel_render.render;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;

public class render_instance_driver_container 
{
	private render_instance_driver render_instance_driver_array[];
	
	public render_instance_driver_container(
			engine_kernel ek,client_request_response request_response)
	{
		render_instance_driver_array=new render_instance_driver[0];
		if(ek.render_cont.renders==null)
			return;
		if(ek.render_cont.renders.length<=0)
			return;
		render_instance_driver_array=new render_instance_driver[ek.render_cont.renders.length];
		for(int i=0,ni=render_instance_driver_array.length;i<ni;i++)
			render_instance_driver_array[i]=ek.render_cont.renders[i].driver.create_render_instance_driver(
					ek.render_cont.renders[i],ek,request_response);
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
