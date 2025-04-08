package kernel_driver;

import kernel_network.client_request_response;
import kernel_part.part;
import kernel_render.render;
import kernel_scene.scene_kernel;

public class part_instance_driver_container
{
	private part_instance_driver part_instance_driver_array[][];
	
	public part_instance_driver_container(scene_kernel sk,client_request_response request_response)
	{
		part_instance_driver_array=new part_instance_driver[sk.render_cont.renders.size()][];
		for(int i=0,ni=part_instance_driver_array.length;i<ni;i++) {
			render r=sk.render_cont.renders.get(i);
			part_instance_driver_array[i]=new part_instance_driver[r.parts.size()];
			for(int j=0,nj=part_instance_driver_array[i].length;j<nj;j++) {
				part_instance_driver_array[i][j]=null;
				part p=r.parts.get(j);
				if(p.driver!=null)
					part_instance_driver_array[i][j]=p.driver.create_part_instance_driver(p,sk,request_response);
			}
		}	
	}
	public void destroy()
	{
		for(int i=0,ni=part_instance_driver_array.length;i<ni;i++) {
			for(int j=0,nj=part_instance_driver_array[i].length;j<nj;j++)
				if(part_instance_driver_array[i][j]!=null){
					part_instance_driver_array[i][j].destroy();
					part_instance_driver_array[i][j]=null;
				}
			part_instance_driver_array[i]=null;
		}
		part_instance_driver_array=null;
	}
	public part_instance_driver get_part_instance_driver(part p)
	{
		if(p==null)
			return null;
		if((p.render_id<0)||(p.render_id>=part_instance_driver_array.length))
			return null;
		if((p.part_id<0)||(p.part_id>=part_instance_driver_array[p.render_id].length))
			return null;
		return part_instance_driver_array[p.render_id][p.part_id];
	}
}
