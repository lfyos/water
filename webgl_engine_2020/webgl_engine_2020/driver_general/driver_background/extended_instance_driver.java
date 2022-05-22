package driver_background;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;

public class extended_instance_driver extends instance_driver
{
	private String directory_name;
	private int mode,user_parameter_channel_id;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id,
			int my_mode,String my_directory_name,int my_user_parameter_channel_id)
	{
		super(my_comp,my_driver_id);
		
		mode=my_mode;
		directory_name=my_directory_name;
		user_parameter_channel_id=my_user_parameter_channel_id;
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		return (mode<0)||(cr.target.parameter_channel_id!=user_parameter_channel_id);
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(mode);
	}
	public String[] response_event(engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;
		switch(str) {
		case "file":
			if((str=ci.request_response.get_parameter("file"))!=null)
				return new String[] {
						file_directory.part_file_directory(comp.driver_array[driver_id].component_part,
								ek.system_par,ek.scene_par)+file_reader.separator(directory_name+str),
						ek.system_par.local_data_charset
					};
			break;
		case "directory":
			if(ci.display_camera_result.target.parameter_channel_id!=user_parameter_channel_id)
				break;
			if((str=ci.request_response.get_parameter("directory"))==null)
				break;
			if(directory_name.compareTo(str)!=0) {
				directory_name=str;
				update_component_parameter_version(0);
			}
			break;
		case "mode":
			if(ci.display_camera_result.target.parameter_channel_id!=user_parameter_channel_id)
				break;
			if((str=ci.request_response.get_parameter("mode"))==null)
				break;
			int new_mode=Integer.decode(str);
			if(mode!=new_mode) {
				mode=new_mode;
				update_component_parameter_version(0);
			}
			break;
		}
		return null;
	}
}
