package driver_opengl_fixed_pipeline;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;


public class extended_instance_driver extends instance_driver
{
	private long display_code[];
	private int  display_flag[],close_clip_plane_number,display_value_id;
	private boolean effective_selected_flag;
	
	public void destroy()
	{
		super.destroy();
		display_code=null;
	}
	public extended_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		display_code=new long[0];
		display_flag=new int[0];
		close_clip_plane_number=0;
		display_value_id=display_parameter.display_value_id;
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if(render_buffer_id>=display_code.length) {
			long bak_display_code[]=display_code;
			int  bak_display_flag[]=display_flag;
			display_code=new long[render_buffer_id+1];
			display_flag=new int[render_buffer_id+1];
			for(int i=0;i<bak_display_code.length;i++) {
				display_code[i]=bak_display_code[i];
				display_flag[i]=bak_display_flag[i];
			}
			for(int i=bak_display_code.length,ni=display_code.length;i<ni;i++) {
				display_code[i]=-1;
				display_flag[i]=-1;
			}
		}
		int new_close_clip_plane_number=comp.clip.close_clip_plane_number;
		long new_display_code=comp.multiparameter[cr.target.parameter_channel_id].display_bitmap;
		int  new_display_flag=cr.target.main_display_target_flag?1:cr.target.selection_target_flag?1:0;
		if((display_code[render_buffer_id]!=new_display_code)||(display_flag[render_buffer_id]!=new_display_flag)) {
			display_code[render_buffer_id]=new_display_code;
			display_flag[render_buffer_id]=new_display_flag;
			update_component_render_version(render_buffer_id,0);
		}
		if(	  (close_clip_plane_number!=new_close_clip_plane_number)
			||(display_value_id!=display_parameter.display_value_id)
			||(comp.uniparameter.effective_selected_flag^effective_selected_flag))
		{
			close_clip_plane_number=new_close_clip_plane_number;
			display_value_id=display_parameter.display_value_id;
			effective_selected_flag=comp.uniparameter.effective_selected_flag;
			update_component_parameter_version(0);
		}
		return false;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print("[",data_buffer_id);
		ci.request_response.print(",",display_code[render_buffer_id]);
		ci.request_response.print(",",display_flag[render_buffer_id]);
		ci.request_response.print("]");
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[",close_clip_plane_number);
		ci.request_response.print(",",display_value_id);
		ci.request_response.print(",",effective_selected_flag?"1":"0");
		ci.request_response.print("]");
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		return null;
	}
}
