package driver_lession_18_dynamic_environment_mapping_texture;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_render.render_target;


public class extended_instance_driver extends instance_driver
{
	private int target_id[],camera_id[];
	public void destroy()
	{
		super.destroy();
		target_id=null;
		camera_id=null;
	}
	public extended_instance_driver(
			component my_comp,int my_driver_id,int my_camera_id[])
	{
		super(my_comp,my_driver_id);
		
		camera_id=my_camera_id;
		target_id=new int[camera_id.length];
		for(int i=0,ni=target_id.length;i<ni;i++)
			target_id[i]=-1;
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if(ci.display_camera_result.target.target_id!=cr.target.target_id)
			return true;
		int bak_target_id[]=target_id;
		target_id=new int[bak_target_id.length];
		for(int i=0,ni=target_id.length;i<ni;i++){
			render_target rt=new render_target(
				comp.component_name+"/"+Integer.toString(i),camera_id[i],parameter_channel_id,
				new component[]{ek.component_cont.root_component},null,null,512,512,1,null,null);
			ci.target_container.register_target(rt,-1,null);
			target_id[i]=rt.target_id;
		}
		for(int i=0,ni=target_id.length;i<ni;i++)
			if(target_id[i]!=bak_target_id[i]){
				update_component_parameter_version(0);
				break;
			}
		return false;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[",comp.component_id);
		for(int i=0;i<6;i++)
			ci.request_response.print(",",target_id[i]);
		ci.request_response.print("]");
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		return null;
	}
}
