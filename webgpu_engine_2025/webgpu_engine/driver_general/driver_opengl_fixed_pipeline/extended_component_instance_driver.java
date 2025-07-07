package driver_opengl_fixed_pipeline;

import java.util.ArrayList;

import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_scene.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private ArrayList<Long> display_bitmap;
	
	private double transparency_value;
	private int close_clip_plane_number;
	private boolean effective_selected_flag;
	
	public void destroy()
	{
		super.destroy();
		
		if(display_bitmap!=null) {
			display_bitmap.clear();
			display_bitmap=null;
		}
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		
		display_bitmap=new ArrayList<Long>();
		
		transparency_value=-1;
		close_clip_plane_number=-1;
		effective_selected_flag=true;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		while(cr.target.target_id>=display_bitmap.size())
			display_bitmap.add((long)-1);

		long new_display_bitmap=comp.multiparameter[cr.target.parameter_channel_id].display_bitmap;
		if(display_bitmap.get(cr.target.target_id)!=new_display_bitmap){
			display_bitmap.set(cr.target.target_id,new_display_bitmap);
			update_component_render_version(cr.target.target_id,0);
		}
		if(	  (transparency_value!=comp.uniparameter.transparency_value) 
			||(comp.clip.close_clip_plane_number!=close_clip_plane_number)
			||(effective_selected_flag^comp.uniparameter.effective_selected_flag))
		{
			transparency_value		=comp.uniparameter.transparency_value;
			close_clip_plane_number	=comp.clip.close_clip_plane_number;
			effective_selected_flag	=comp.uniparameter.effective_selected_flag;
			
			update_component_parameter_version(0);
		}
		return false;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print(display_bitmap.get(cr.target.target_id));
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
