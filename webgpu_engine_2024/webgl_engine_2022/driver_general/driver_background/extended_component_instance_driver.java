package driver_background;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_common_class.jason_string;
import kernel_driver.component_instance_driver;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class extended_component_instance_driver extends component_instance_driver
{
	private int mode,user_parameter_channel_id;
	private String directory_name;
	
	public void destroy()
	{
		super.destroy();
		directory_name=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,
			int my_mode,int my_user_parameter_channel_id,String my_directory_name)
	{
		super(my_comp,my_driver_id);
		mode=my_mode;
		user_parameter_channel_id=my_user_parameter_channel_id;
		directory_name=my_directory_name;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		return (cr.target.parameter_channel_id!=user_parameter_channel_id);
	}
	public void create_render_parameter(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(0);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{	
		ci.request_response.print("[",mode).print(",",jason_string.change_string(directory_name)).print("]");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		String str=ci.request_response.get_parameter("operation");
		
		switch((str==null)?"":str) {
		default:
			return null;
		case "mode":
			if((str=ci.request_response.get_parameter("mode"))==null)
				return null;
			int new_mode;
			if(mode==(new_mode=Integer.decode(str)))
				return null;
			mode=new_mode;
			break;
		case "directory":
			if((str=ci.request_response.get_parameter("directory"))==null)
				return null;
			if(directory_name.compareTo(str)==0)
				return null;
			directory_name=str;
			break;
		}
		update_component_parameter_version(0);
		return null;
	}
}