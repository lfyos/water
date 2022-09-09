package driver_orientation_camera;

import kernel_component.component;
import kernel_driver.part_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_part.part;
import kernel_transformation.location;

public class extended_part_instance_driver extends part_instance_driver
{
	public extended_part_instance_driver()
	{
		super();
	}
	public void destroy()
	{
		super.destroy();
	}
	public void response_init_part_data(part p,engine_kernel ek,client_information ci)
	{
	}
	public String[] response_event(part p,engine_kernel ek,client_information ci)
	{
		//return super.response_event(p,ek,ci);
		
		if(ci.display_camera_result==null)
			return null;
		
		String str;
		
		if((str=ci.request_response.get_parameter("component"))==null)
			return null;
		component comp;
		if((comp=ek.component_cont.get_component(Integer.decode(str)))==null)
			return null;
		
		if((str=ci.request_response.get_parameter("data"))==null)
			return null;
		
		double data[]= {
				1,0,0,0,
				0,1,0,0,
				0,0,1,0,
				0,0,0,1
		};
		for(int index_id,i=0,ni=data.length;i<ni;i++)
			if((index_id=str.indexOf(','))<0) {
				data[i]=Double.parseDouble(str);
				break;
			}else {
				data[i]=Double.parseDouble(str.substring(0,index_id));
				str=str.substring(index_id+1);
			}

		comp.modify_location(new location(data),ek.component_cont);
		comp.uniparameter.cacaulate_location_flag=true;
		ci.render_buffer.location_buffer.synchronize_location_version(comp,ek,false);
		return null;
	}
}