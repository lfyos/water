package driver_cad;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;


public class extended_instance_driver extends instance_driver
{
	private int close_clip_plane_number;
	private long display_bitmap[];
	private component_parameter parameter;
	public void destroy()
	{
		super.destroy();
		display_bitmap=null;
	}
	public extended_instance_driver(component my_comp,int my_driver_id,component_parameter my_parameter)
	{
		super(my_comp,my_driver_id);
		display_bitmap=new long[0];
		parameter=my_parameter;
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if(render_buffer_id>=display_bitmap.length){
			long bak[]=display_bitmap;
			display_bitmap=new long[render_buffer_id+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				display_bitmap[i]=bak[i];
			for(int i=bak.length,ni=display_bitmap.length;i<ni;i++)
				display_bitmap[i]=-1;
		}
		long new_display_bitmap=comp.multiparameter[parameter_channel_id].display_bitmap;
		if(display_bitmap[render_buffer_id]!=new_display_bitmap){
			display_bitmap[render_buffer_id]=new_display_bitmap;
			update_component_render_version(render_buffer_id,0);
		}
		
		if(	  (parameter.effective_selected_flag^comp.uniparameter.effective_selected_flag) 
			||(parameter.transparency_value!=comp.uniparameter.transparency_value))
		{
			parameter.effective_selected_flag=comp.uniparameter.effective_selected_flag;
			parameter.transparency_value=comp.uniparameter.transparency_value;
			comp.driver_array[driver_id].update_component_parameter_version();
		}
		if(comp.clip.close_clip_plane_number!=close_clip_plane_number){
			close_clip_plane_number=comp.clip.close_clip_plane_number;
			update_component_parameter_version(0);
		}
		return false;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print("[",data_buffer_id).print(",",display_bitmap[render_buffer_id]).print("]");
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.
			print  ("[",comp.component_id).
			print  (",",parameter.transparency_value).
			print  (",",close_clip_plane_number).
			print  (",",parameter.effective_selected_flag?"1":"0").
			print  (",[",parameter.x_scale).
			print  (",",parameter.y_scale).
			print  (",",parameter.z_scale).
			println(",1]]");
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		return null;
	}
}
