package driver_show_component_box;

import java.util.Date;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_driver.component_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;


public class extended_component_instance_driver extends component_instance_driver
{
	private boolean show_type_flag;
	private long time_length,last_touch_time;
	private int old_component_id,new_component_id;
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,boolean my_show_type_flag,long my_time_length)
	{
		super(my_comp,my_driver_id);
		show_type_flag=my_show_type_flag;
		time_length=my_time_length;
		last_touch_time=0;
		old_component_id=-2;
		new_component_id=-1;
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		if(!(cr.target.main_display_target_flag))
			return true;
		if(show_type_flag) {
			new_component_id=-1;
			if(ci.parameter.comp!=null)
				if(ci.parameter.comp.model_box!=null)
					new_component_id=ci.parameter.comp.component_id;
		}
		if(new_component_id<0)
			return true;
		if(old_component_id!=new_component_id) {
			old_component_id=new_component_id;
			update_component_render_version(0);
			last_touch_time=new Date().getTime();
		}
		return show_type_flag?false:((new Date().getTime()-last_touch_time)>time_length)?true:false;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		double loca[]=new double[] {1,0,0,0,	0,1,0,0,	0,0,1,0,	0,0,0,1};
		double p0  []=new double[] {-1,-1,-1,-1};
		double p1  []=new double[] {-1,-1,-1,-1};
		
		component my_comp;
		if((my_comp=ek.component_cont.get_component(new_component_id))!=null){
			if(my_comp.model_box!=null) {
				loca=my_comp.absolute_location.get_location_data();
				p0=new double[] {
						my_comp.model_box.p[0].x,
						my_comp.model_box.p[0].y,
						my_comp.model_box.p[0].z,
						1};
				p1=new double[] {
						my_comp.model_box.p[1].x,
						my_comp.model_box.p[1].y,
						my_comp.model_box.p[1].z,
						1};
			}
		}

		ci.request_response.print("[",data_buffer_id);
		for(int i=0,ni=loca.length;i<ni;i++)
			ci.request_response.print((i==0)?",[":",",loca[i]);
		for(int i=0,ni=p0.length;i<ni;i++)
			ci.request_response.print((i==0)?"],[":",",p0[i]);
		for(int i=0,ni=p1.length;i<ni;i++)
			ci.request_response.print((i==0)?"],[":",",p1[i]);
		ci.request_response.print("]]");
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(comp.component_id);
	}
	public String[] response_event(engine_kernel ek,client_information ci)
	{
		String str,request_charset=ci.request_response.implementor.get_request_charset();
		
		last_touch_time=new Date().getTime();
		
		if((str=ci.request_response.get_parameter("type"))!=null) {
			update_component_render_version(0);
			
			switch(str.toLowerCase()) {
			case "true":
				show_type_flag=true;
				break;
			default:
				show_type_flag=false;
				break;
			}
		}
		do {
			if((str=ci.request_response.get_parameter("component_name"))==null)
				break;
			new_component_id=-1;
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
			new_component_id=my_comp.component_id;
		}while(false);
		
		do {
			if((str=ci.request_response.get_parameter("component_id"))==null)
				break;
			new_component_id=-1;
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
			new_component_id=my_comp.component_id;
		}while(false);
		
		return null;
	}
}
