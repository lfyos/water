package kernel_driver;

import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

public class part_instance_driver_container
{
	private part_instance_driver part_instance_driver_array[][];
	
	public part_instance_driver_container(engine_kernel ek,client_request_response request_response)
	{
		part p;
		
		part_instance_driver_array=new part_instance_driver[ek.render_cont.renders.length][];
		for(int i=0,ni=part_instance_driver_array.length;i<ni;i++) {
			part_instance_driver_array[i]=new part_instance_driver[ek.render_cont.renders[i].parts.length];
			for(int j=0,nj=part_instance_driver_array[i].length;j<nj;j++) {
				part_instance_driver_array[i][j]=null;
				if((p=ek.render_cont.renders[i].parts[j]).driver!=null)
					part_instance_driver_array[i][j]=p.driver.create_part_instance_driver(p,ek,request_response);
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
