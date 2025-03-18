package kernel_buffer;

import  kernel_camera.camera;
import kernel_scene.scene_kernel;

public class buffer_container
{
	public component_render_buffer		component_buffer;
	public component_location_buffer	location_buffer;
	public camera_buffer				cam_buffer;
	public target_parameter_buffer		target_buffer;
	
	public part_mesh_loader 			mesh_loader;
	public modifier_parameter_buffer	modifier_parameter[];
	public long response_current_time_pointer;
	
	public void destroy()
	{
		if(component_buffer!=null)
			component_buffer.destroy();
		component_buffer=null;
		
		if(location_buffer!=null)
			location_buffer.destroy();
		location_buffer=null;
		
		if(cam_buffer!=null)
			cam_buffer.destroy();
		cam_buffer=null;
		
		if(target_buffer!=null)
			target_buffer.destroy();
		target_buffer=null;
		
		if(mesh_loader!=null)
			mesh_loader.destroy();
		mesh_loader=null;
		
		if(modifier_parameter!=null)
			modifier_parameter=null;
	}
	public buffer_container(scene_kernel sk)
	{
		component_buffer	=new component_render_buffer(sk.render_cont.renders);
		location_buffer		=new component_location_buffer(sk);	
		cam_buffer			=new camera_buffer(sk.camera_cont.toArray(new camera[sk.camera_cont.size()]));
		target_buffer		=new target_parameter_buffer();
		mesh_loader			=new part_mesh_loader(sk.render_cont);
		modifier_parameter	=new modifier_parameter_buffer[sk.scene_par.max_modifier_container_number];
		for(int i=0;i<sk.scene_par.max_modifier_container_number;i++)
			modifier_parameter[i]=new modifier_parameter_buffer(0);
		response_current_time_pointer=0;
	}
}
