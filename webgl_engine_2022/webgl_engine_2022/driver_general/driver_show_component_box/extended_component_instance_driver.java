package driver_show_component_box;

import java.util.Date;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_transformation.point;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private boolean show_type_flag;
	private long last_touch_time,time_length;
	private int show_component_id;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,
			boolean my_show_type_flag,long my_time_length)
	{
		super(my_comp,my_driver_id);
		show_type_flag		=my_show_type_flag;
		time_length			=my_time_length;
		show_component_id	=-1;
		last_touch_time		=0;
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		if(show_type_flag){
			int old_component_id=show_component_id;
			show_component_id=-1;
			if(ci.parameter.comp!=null)
				if(ci.parameter.comp.model_box!=null)
					show_component_id=ci.parameter.comp.component_id;
			if(old_component_id!=show_component_id)
				update_component_parameter_version(0);
		}
		if(show_component_id<0)
			return true;
		if(show_type_flag)
			return false;
		if((new Date().getTime()-last_touch_time)<=time_length)
			return false;
		return true;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{
		component my_comp=ek.component_cont.get_component(show_component_id);
		point p[]=my_comp.model_box.p;
		ci.request_response.	print("[[",	p[0].x).	print(",",p[0].y).	print(",",p[0].z).print(",1").
								print(",",	p[1].x).	print(",",p[1].y).	print(",",p[1].z).print(",1]").
								print(",",my_comp.component_id).			print("]");
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str,request_charset=ci.request_response.implementor.get_request_charset();

		do {
			if((str=ci.request_response.get_parameter("component_name"))==null)
				break;
			show_component_id=-1;
			try {
				str=java.net.URLDecoder.decode(str,request_charset);
				str=java.net.URLDecoder.decode(str,request_charset);
			}catch(Exception e) {
				break;
			}
			component my_comp;
			if((my_comp=ek.component_cont.search_component(str))==null)
				break;
			if(my_comp.model_box==null)
				break;
			show_component_id=my_comp.component_id;
			update_component_parameter_version(0);
			last_touch_time=new Date().getTime();
		}while(false);
		
		do {
			if((str=ci.request_response.get_parameter("component_id"))==null)
				break;
			show_component_id=-1;
			int my_component_id;
			try {
				str=java.net.URLDecoder.decode(str,request_charset);
				str=java.net.URLDecoder.decode(str,request_charset);
				my_component_id=Integer.decode(str);
			}catch(Exception e) {
				break;
			}
			component my_comp;
			if((my_comp=ek.component_cont.get_component(my_component_id))==null)
				break;
			if(my_comp.model_box==null)
				break;
			show_component_id=my_comp.component_id;
			update_component_parameter_version(0);
			last_touch_time=new Date().getTime();
		}while(false);

		return null;
	}
}
