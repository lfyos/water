package driver_interface;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_render.render_target_view;
import kernel_common_class.const_value;
import kernel_common_class.jason_string;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private boolean menu_type;
	private String file_name,file_charset;
	private double x0,y0,dx,dy,depth;
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

		hide_show_flag=true;
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
		ci.request_response.println("{");

		ci.request_response.print  ("	\"dx\"	:	",		dx).	println(",");
		ci.request_response.print  ("	\"dy\"	:	",		dy).	println(",");
		ci.request_response.print  ("	\"depth\"	:	",	depth).	println(",");
		
		ci.request_response.println("	\"type\"	:	",menu_type?"true,":"false,");
		if(menu_type) {
			ci.request_response.println("	\"canvas\"	:	");
			file_reader.get_text(ci.request_response,file_name,file_charset);
		}else{
			String url=ci.get_component_request_url_header(comp.component_id,driver_id);
			ci.request_response.println("	\"url\"	:	",jason_string.change_string(url+"&operation=file"));
		}
		ci.request_response.println("}");
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		return always_show_flag?false:hide_show_flag;
	}
	public void create_render_parameter(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[",x0).print(",",y0).print(",",dx).print(",",dy).print("]");
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
			render_target_view tv=ci.display_camera_result.target.target_view;
			double p[]=tv.caculate_view_local_xy(ci.parameter.x,ci.parameter.y);
			x0=p[0]-dx/2.0;
			y0=p[1]-dy/2.0;
		}
		if(x0>=1)
			x0=1.0-const_value.min_value;
		if((x0+dx)<=-1)
			x0=-1-dx+const_value.min_value;
		if(y0>=1)
			y0=1.0-const_value.min_value;
		if((y0+dy)<=-1)
			y0=-1-dy+const_value.min_value;
		
		if((str=ci.request_response.get_parameter("all_in_view"))!=null){
			if((x0+dx)>=1)
				x0=1.0-dx-const_value.min_value;
			if(x0<=-1)
				x0=-1+const_value.min_value;
			if((y0+dy)>=1)
				y0=1.0-dy-const_value.min_value;
			if(y0<=-1)
				y0=-1+const_value.min_value;
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
			get_parameter(ek,ci);
			hide_show_flag=false;
			comp.driver_array.get(driver_id).update_component_parameter_version();
			return null;
		case "parameter":
			get_parameter(ek,ci);
			return null;
		default:
			return null;
		}
	}
}
