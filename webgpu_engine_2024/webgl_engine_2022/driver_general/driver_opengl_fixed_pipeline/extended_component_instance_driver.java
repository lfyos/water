package driver_opengl_fixed_pipeline;

import kernel_component.component;
import kernel_camera.camera_result;
import kernel_driver.component_instance_driver;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class extended_component_instance_driver extends component_instance_driver
{
	private long display_bitmap[];
	
	private double transparency_value;
	private int close_clip_plane_number;
	private boolean effective_selected_flag;
	
	public void destroy()
	{
		super.destroy();
		
		display_bitmap=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		
		display_bitmap=new long[0];
		
		transparency_value=-1;
		close_clip_plane_number=-1;
		effective_selected_flag=true;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		if(render_buffer_id>=display_bitmap.length){
			long bak[]=display_bitmap;
			display_bitmap=new long[render_buffer_id+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				display_bitmap[i]=bak[i];
			for(int i=bak.length,ni=display_bitmap.length;i<ni;i++)
				display_bitmap[i]=-1;
		}
		long new_display_bitmap=comp.multiparameter[cr.target.parameter_channel_id].display_bitmap;
		if(display_bitmap[render_buffer_id]!=new_display_bitmap){
			display_bitmap[render_buffer_id]=new_display_bitmap;
			update_component_render_version(render_buffer_id,0);
		}
		if(	  (transparency_value!=comp.uniparameter.transparency_value) 
			||(comp.clip.close_clip_plane_number!=close_clip_plane_number)
			||(effective_selected_flag^comp.uniparameter.effective_selected_flag))
		{
			transparency_value=comp.uniparameter.transparency_value;
			close_clip_plane_number=comp.clip.close_clip_plane_number;
			effective_selected_flag=comp.uniparameter.effective_selected_flag;
			
			update_component_parameter_version(0);
		}
		return false;
	}
	public void create_render_parameter(int render_buffer_id,scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(display_bitmap[render_buffer_id]);
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.
			print  ("[",transparency_value).
			print  (",",close_clip_plane_number).
			print  (",",display_parameter.display_value_id).
			print  (effective_selected_flag?",1]":",0]");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		return null;
	}
}
