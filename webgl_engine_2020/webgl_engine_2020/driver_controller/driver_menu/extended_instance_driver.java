package driver_menu;

import kernel_camera.camera_result;
import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

import kernel_part.part;
import kernel_transformation.point;

public class extended_instance_driver extends instance_driver
{
	private String file_name;
	private double x0,y0,dx,dy,min_x,max_x,min_y,max_y;
	private double operated_point_x,operated_point_y,operated_point_z;

	private boolean file_type;
	private int operate_component_id;

	public boolean show_hide_flag;
	
	public void destroy()
	{
		super.destroy();
		file_name=null;
	}
	public extended_instance_driver(component my_comp,int my_driver_id,
			String my_file_name,boolean my_file_type,double my_dx,double my_dy,
			double my_min_x,double my_max_x,double my_min_y,double my_max_y)
	{
		super(my_comp,my_driver_id);
		
		file_name=my_file_name;
		file_type=my_file_type;
		
		x0=0;
		y0=0;
		dx=my_dx;
		dy=my_dy;
		min_x=my_min_x;
		max_x=my_max_x;
		min_y=my_min_y;
		max_y=my_max_y;
		
		operated_point_x=0;
		operated_point_y=0;
		operated_point_z=0;

		show_hide_flag=true;
		operate_component_id=-1;
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		return show_hide_flag;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		String url=null;
		if(file_type)
			url=ci.get_file_proxy_url(file_name,ek.system_par);
		if(url==null)
			url=ci.get_component_request_url_header(comp.component_id,driver_id)+"&operation=file";

		ci.request_response.
			print("[",comp.component_id).
			print(",",operate_component_id).
			print(",",operated_point_x).
			print(",",operated_point_y).
			print(",",operated_point_z).
			print(",",x0).
			print(",",y0).
			print(",",dx).
			print(",",dy).
			print(",\"",url).
			print("\"]");
	}
	private void test_parameter()
	{
		if(dx<const_value.min_value)
			dx=const_value.min_value;
		if(dy<const_value.min_value)
			dy=const_value.min_value;
		
		if(dx>(max_x-min_x))
			x0=(max_x+min_x)/2.0-dx/2.0;
		else{
			if((x0+dx)>max_x)
				x0=max_x-dx;
			if(x0<min_x)
				x0=min_x;
		}
		if(dy>(max_y-min_y))
			y0=(max_y+min_y)/2.0-dy/2.0;
		else{
			if((y0+dy)>max_y)
				y0=max_y-dy;
			if(y0<min_y)
				y0=min_y;
		}
		if(operate_component_id<0) {
			operated_point_x=0;
			operated_point_y=0;
			operated_point_z=0;
		}
	}
	private void hide_all(engine_kernel ek,client_information ci)
	{
		part my_part;
		if((my_part=comp.driver_array[driver_id].component_part)==null)
			return;
		int id_array[][]=ek.component_cont.part_component_id_and_driver_id[my_part.render_id][my_part.part_id];
		if(id_array==null)
			return;

		for(int i=0,ni=id_array.length;i<ni;i++) {
			int my_component_id=id_array[i][0],my_driver_id=id_array[i][1];
			component my_comp;
			if((my_comp=ek.component_cont.get_component(my_component_id))==null)
				continue;
			extended_instance_driver eid=(extended_instance_driver)
				(ci.instance_container.get_driver(my_comp,my_driver_id));
			if(eid==null)
				continue;
			eid.show_hide_flag=true;
		}
		return;
	}
	private void get_parameter(engine_kernel ek,client_information ci)
	{
		String str;
		
		show_hide_flag=false;
		update_component_parameter_version(0);
		
		if((str=ci.request_response.get_parameter("x0"))!=null) 
			x0=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("y0"))!=null)
			y0=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("center"))!=null) 
			switch(str.trim().toLowerCase()){
			case "center":
				x0=ci.parameter.x-dx/2.0;
				y0=ci.parameter.y-dy/2.0;
				break;
			}
		
		if((str=ci.request_response.get_parameter("dx"))!=null)
			dx=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("dy"))!=null)
			dy=Double.parseDouble(str);

		component my_comp=null;
		if((str=ci.request_response.get_parameter("operate_component_id"))!=null) {
			if(str.length()>0)
				try{
					my_comp=ek.component_cont.get_component(Integer.decode(str));
				}catch(Exception e){
					;
				}
			if(my_comp!=null)
				operate_component_id=my_comp.component_id;
			else
				operate_component_id=-1;
		}
		if(operate_component_id<0) {
			operated_point_x=0;
			operated_point_y=0;
			operated_point_z=0;
		}else{
			point operated_point=ci.selection_camera_result.negative_matrix.multiply(new point(0,0,ci.parameter.depth));
			operated_point=ci.parameter.comp.absolute_location.negative().multiply(operated_point);
			operated_point_x=operated_point.x;
			operated_point_y=operated_point.y;
			operated_point_z=operated_point.z;
		}
		test_parameter();
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		String str=ci.request_response.get_parameter("operation");
		
		switch((str==null)?"":(str.toLowerCase())){
		case "file":
			return new String[]{file_name,null};
		case "adjust":
			update_component_parameter_version(0);
			if((str=ci.request_response.get_parameter("x"))!=null) 
				x0+=Double.parseDouble(str);
			if((str=ci.request_response.get_parameter("y"))!=null)
				y0+=Double.parseDouble(str);
			
			test_parameter();
			
			return null;
		case "hide":
			show_hide_flag=true;
			return null;
		case "hide_all":
			hide_all(ek,ci);
			return null;
		case "show_me_only":
			hide_all(ek,ci);
		case "show":
			get_parameter(ek,ci);
			return null;
		default:
			return null;
		}
	}
}