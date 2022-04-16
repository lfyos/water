package kernel_buffer;

import kernel_engine.engine_kernel;

public class buffer_container
{
	public component_render_buffer		component_buffer;
	public component_location_buffer	location_buffer;
	public camera_buffer				cam_buffer;
	public target_parameter_buffer		target_buffer;
	public clip_plane_buffer			clip_buffer;
	
	public part_mesh_loader 			mesh_loader;
	public current_information_buffer	current_buffer;
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
		
		if(clip_buffer!=null)
			clip_buffer.destroy();
		clip_buffer=null;
		
		if(mesh_loader!=null)
			mesh_loader.destroy();
		mesh_loader=null;
	
		if(current_buffer!=null)
			current_buffer=null;
		
		if(modifier_parameter!=null){
			for(int i=0,ni=modifier_parameter.length;i<ni;i++)
				modifier_parameter[i]=null;
			modifier_parameter=null;
		}
	}
	public buffer_container(engine_kernel ek)
	{
		component_buffer=new component_render_buffer(ek.render_cont.renders,
				(ek.component_cont.root_component==null)
				?1
				:(ek.component_cont.root_component.component_id+1));
		location_buffer		=new component_location_buffer(ek);	
		cam_buffer			=new camera_buffer(ek.camera_cont.camera_array);
		target_buffer		=new target_parameter_buffer();
		clip_buffer			=new clip_plane_buffer();
		mesh_loader			=new part_mesh_loader(ek.render_cont);
		current_buffer		=new current_information_buffer();
		modifier_parameter	=new modifier_parameter_buffer[ek.modifier_cont.length];
		for(int i=0,ni=modifier_parameter.length;i<ni;i++)
			modifier_parameter[i]=null;
		response_current_time_pointer=0;
	}
}
