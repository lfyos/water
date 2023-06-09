package driver_proxy_register;

import kernel_part.part;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
		part p=comp.driver_array.get(driver_id).component_part;
		for(file_reader f=new file_reader(p.directory_name+p.material_file_name,p.file_charset);;){
			if(f.eof()) {
				f.close();
				return;
			}
			String my_url;
			f.mark_start();
			if((my_url=f.get_string())==null){
				f.mark_terminate(false);
				continue;
			}
			boolean my_encode_flag;
			switch(my_url.toLowerCase()) {
			case "yes":
			case "no":
			case "true":
			case "false":
				f.mark_terminate(true);
				my_encode_flag=f.get_boolean();
				if(f.eof())
					continue;
				if((my_url=f.get_string())==null)
					continue;
				if((my_url=my_url.trim()).length()<=0)
					continue;
				break;
			default:
				f.mark_terminate(false);
				my_url=ci.request_response.implementor.get_url()+"?channel=buffer&file=";
				my_encode_flag=true;
				break;
			}
			ci.add_file_proxy_url(my_url,my_encode_flag);
		}
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		return true;
	}
	public void create_render_parameter(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print("0");
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.println("0");
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		return null;
	}
}
