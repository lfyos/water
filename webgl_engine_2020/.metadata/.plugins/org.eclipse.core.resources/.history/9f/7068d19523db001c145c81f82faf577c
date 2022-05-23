package driver_water;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_render.render_target;
import kernel_render.target_viewport;
import kernel_transformation.box;
import kernel_transformation.plane;
import kernel_transformation.point;

public class extended_instance_driver extends instance_driver
{
	private double height,amplitude,wavelength,attenuation,left,right,down,up;
	private int user_parameter_channel_id[];
	
	private int texture_width,texture_height;
	
	private int bak_mirror_id;
	private plane mirror_plane;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id,
			double my_height,double my_amplitude,double my_wavelength,double my_attenuation,
			double my_left,double my_right,double my_down,double my_up,
			int my_user_parameter_channel_id[],int my_texture_width,int my_texture_height)
	{
		super(my_comp,my_driver_id);

		height=my_height;
		amplitude=my_amplitude;
		wavelength=my_wavelength;
		attenuation=my_attenuation;
		
		left=my_left;
		right=my_right;
		down=my_down;
		up=my_up;
		
		user_parameter_channel_id=my_user_parameter_channel_id;
		
		texture_width=my_texture_width;
		texture_height=my_texture_height;

		bak_mirror_id=-1;
		mirror_plane=new plane(new point(0,height,0),new point(0,height-1.0,0));
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		if(ci.display_camera_result.target.target_id!=cr.target.target_id)
			return true;
		boolean exit_flag=true;
		
		for(int i=0,ni=user_parameter_channel_id.length;i<ni;i++)
			if(cr.target.parameter_channel_id==user_parameter_channel_id[i]) {
				exit_flag=false;
				break;
			}
		if(exit_flag)
			return true;

		double aspect;
		if((aspect=Math.abs(ci.parameter.aspect))<1)
			aspect=1.0/aspect;
		
		render_target rt=new render_target(comp.component_name,
				ci.display_camera_result.target.camera_id,cr.target.parameter_channel_id,
				new component[]{ek.component_cont.root_component},null,
				mirror_plane,texture_width,texture_height,1,
				new box(-aspect,-aspect,-1,aspect,aspect,1),
				
				new target_viewport[]
				{
					new target_viewport(-1,-1,2,2,0,0,new double[]{0.0,0.0,0.0,1.0}),
					new target_viewport(-1,-1,2,2,3,0,null),
					new target_viewport(-1,-1,2,2,4,0,null),
					new target_viewport(-1,-1,2,2,5,0,null)
				},false,false,true,true);
		rt.mirror_plane=mirror_plane;
		ci.target_container.register_target(rt,-1,null);
		
		if(rt.target_id!=bak_mirror_id){
			bak_mirror_id=rt.target_id;
			update_component_render_version(0);
		}
		return false;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print("[",data_buffer_id);
		ci.request_response.print(",",bak_mirror_id);
		ci.request_response.print("]");
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[",left);
		ci.request_response.print(",",right);
		ci.request_response.print(",",down);
		ci.request_response.print(",",up);
		ci.request_response.print(",",height);
		ci.request_response.print(",",amplitude);
		ci.request_response.print(",",wavelength);
		ci.request_response.print(",",attenuation);
		ci.request_response.print("]");
	}
	public String[] response_event(engine_kernel ek,client_information ci)
	{
		return null;
	}
}
