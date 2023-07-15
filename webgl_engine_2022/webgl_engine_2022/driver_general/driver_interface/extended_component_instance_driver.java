package driver_interface;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_common_class.const_value;
import kernel_common_class.jason_string;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private boolean menu_type;
	private String file_name,file_charset;
	private double x0,y0,dx,dy,depth,high_light_x0,high_light_y0,high_light_x1,high_light_y1;
	private boolean hide_show_flag,always_show_flag;
	
	public void destroy()
	{
		super.destroy();
		file_name=null;
		file_charset=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,
			boolean my_menu_type,double my_depth,double my_dx,double my_dy,
			String my_file_name,String my_file_charset,boolean my_always_show_flag)
	{
		super(my_comp,my_driver_id);
		
		menu_type=my_menu_type;
		file_name=my_file_name;
		file_charset=my_file_charset;
		always_show_flag=my_always_show_flag;
		
		x0=0;
		y0=0;
		dx=my_dx;
		dy=my_dy;
		depth=my_depth;
		high_light_x0=0;
		high_light_y0=0;
		high_light_x1=0;
		high_light_y1=0;
		hide_show_flag=true;
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
		if(menu_type)
			file_reader.get_text(ci.request_response,file_name,file_charset);
		else{
			String url=ci.get_component_request_url_header(comp.component_id,driver_id);
			ci.request_response.print(jason_string.change_string(url+"&operation=file"));
		}
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		return always_show_flag?false:hide_show_flag;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[",x0).				print(",",y0).
							print(",",dx).				print(",",dy).
							print(",",depth).	
							print(",",high_light_x0).	print(",",high_light_y0).
							print(",",high_light_x1).	print(",",high_light_y1).
							print("]");
	}
	private void get_parameter(engine_kernel ek,client_information ci)
	{
		String str;
		
		if(ci.display_camera_result==null) 
			return;
		if((str=ci.request_response.get_parameter("hx0"))!=null)
			high_light_x0=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("hy0"))!=null)
			high_light_y0=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("hx1"))!=null)
			high_light_x1=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("hy1"))!=null)
			high_light_y1=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("dx"))!=null)
			dx=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("dy"))!=null)
			dy=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("x0"))!=null)
			x0=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("y0"))!=null)
			y0=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("center"))!=null) {
			x0=ci.parameter.x-dx/2.0;
			y0=ci.parameter.y-dy/2.0;
		}
		
		if(x0>=1)
			x0=1.0-const_value.min_value;
		if((x0+dx)<=-1)
			x0=-1-dx+const_value.min_value;
		if(y0>=1)
			y0=1.0-const_value.min_value;
		if((y0+dy)<=-1)
			y0=-1-dy+const_value.min_value;
		
		if((str=ci.request_response.get_parameter("all_in_view"))!=null)
			switch(str.trim().toLowerCase()){
			case "all_in_view":
			case "yes":
			case "true":
				if((x0+dx)>=1)
					x0=1.0-dx-const_value.min_value;
				if(x0<=-1)
					x0=-1+const_value.min_value;
				if((y0+dy)>=1)
					y0=1.0-dy-const_value.min_value;
				if(y0<=-1)
					y0=-1+const_value.min_value;
				break;
			}
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str=ci.request_response.get_parameter("operation");
		
		switch((str==null)?"":(str.toLowerCase())){
		case "file":
			return menu_type?null:new String[]{file_name,file_charset};
		case "hide":
			hide_show_flag=true;
			return null;
		case "show":
			hide_show_flag=false;
			get_parameter(ek,ci);
			update_component_parameter_version(0);
			return null;
		default:
			return null;
		}
	}
}
