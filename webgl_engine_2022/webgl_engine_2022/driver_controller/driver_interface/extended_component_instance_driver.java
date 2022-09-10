package driver_interface;

import kernel_camera.camera_result;
import kernel_common_class.const_value;
import kernel_common_class.jason_string;
import kernel_component.component;
import kernel_driver.component_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_render.target_viewport;

public class extended_component_instance_driver extends component_instance_driver
{
	private boolean menu_type;
	private int level;
	private String file_name,file_charset;
	private double x0,y0,dx,dy,x_scale;
	private boolean hide_show_flag,always_show_flag;
	
	public void destroy()
	{
		super.destroy();
		file_name=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,
			boolean my_menu_type,int my_level,double my_dx,double my_dy,
			String my_file_name,String my_file_charset,boolean my_always_show_flag)
	{
		super(my_comp,my_driver_id);
		
		menu_type=my_menu_type;
		level=my_level;
		file_name=my_file_name;
		file_charset=my_file_charset;
		always_show_flag=my_always_show_flag;
		
		x0=0;
		y0=0;
		dx=my_dx;
		dy=my_dy;
		x_scale=1.0;
		hide_show_flag=true;
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
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
		if(hide_show_flag&&(!always_show_flag))
			return true;
		if(cr.target.selection_target_flag)
			return false;
		if(cr.target.main_display_target_flag){
			double p[];
			if((p=ci.display_camera_result.caculate_view_coordinate(ci))!=null) {
				target_viewport tv=ci.display_camera_result.target.viewport[(int)(p[2])];
				double new_x_scale=tv.width/tv.height;
				new_x_scale*=ci.parameter.canvas_width;
				new_x_scale/=ci.parameter.canvas_height;
				if(Math.abs(x_scale-new_x_scale)>const_value.min_value) {
					x_scale=new_x_scale;
					update_component_parameter_version(0);
				}
			}
						
			return false;
		}
		return true;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print("[",data_buffer_id).print(cr.target.main_display_target_flag?",true]":",false]");
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.	print("[",x0*x_scale).	print(",",y0).
								print(",",dx*x_scale).	print(",",dy).
								print(",",x_scale).		print(",",level).		
								print("]");
	}
	private void get_parameter(engine_kernel ek,client_information ci)
	{
		String str;
		
		if(ci.display_camera_result==null) 
			return;
		if((str=ci.request_response.get_parameter("dx"))!=null)
			dx=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("dy"))!=null)
			dy=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("x0"))!=null)
			x0=Double.parseDouble(str);
		if((str=ci.request_response.get_parameter("y0"))!=null)
			y0=Double.parseDouble(str);
		
		if((str=ci.request_response.get_parameter("center"))!=null) {
			double p[];
			if((p=ci.display_camera_result.caculate_view_coordinate(ci))!=null)
				switch(str.trim().toLowerCase()){
				case "center":
					x0=p[0];
					y0=p[1]-dy/2.0;
					break;
				}
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
	public String[] response_event(engine_kernel ek,client_information ci)
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
