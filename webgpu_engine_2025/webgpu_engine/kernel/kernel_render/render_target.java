package kernel_render;

import kernel_component.component;
import kernel_transformation.box;
import kernel_transformation.plane;
import kernel_transformation.location;

public class render_target
{
	public boolean do_render_flag;
	public int target_id;
	
	public int target_comonent_id,target_driver_id,target_texture_id;
	public String target_name;

	public component comp[];
	public int driver_id[];
	
	public int camera_id,parameter_channel_id;
	public box view_volume_box;
	public render_target_view target_view;

	public location camera_transformation_matrix;
	
	public plane clip_plane;

	public boolean main_display_target_flag;
	public boolean do_discard_lod_flag,do_selection_lod_flag;

	public void destroy()
	{
		if(comp!=null)
			for(int i=0,ni=comp.length;i<ni;i++)
				if(comp[i]!=null)
					comp[i]=null;
		target_name=null;
		comp=null;
		driver_id=null;
		target_view=null;
		view_volume_box=null;
		clip_plane=null;
		camera_transformation_matrix=null;
	}
	public render_target()
	{
		do_render_flag				=false;
		target_id					=0;
		
		target_name					=null;
		target_comonent_id			=0;
		target_driver_id			=0;
		target_texture_id			=0;
		
		comp						=null;
		driver_id					=null;
		
		camera_id					=0;
		parameter_channel_id		=0;
		
		target_view					=new render_target_view();
		view_volume_box				=new box(-1,-1,-1,1,1,1);

		clip_plane					=null;
		camera_transformation_matrix=null;

		main_display_target_flag	=false;
		do_discard_lod_flag			=false;
		do_selection_lod_flag		=false;
	}
	public render_target(
			boolean my_do_render_flag,				String my_target_name,
			int my_target_comonent_id,				int my_target_driver_id,		int my_target_texture_id,
			component my_comp[],					int my_driver_id[],
			int my_camera_id,						int my_parameter_channel_id,
			render_target_view my_target_view,		box my_view_volume_box,					
			plane my_clip_plane,					location my_camera_transformation_matrix,
			boolean my_do_discard_lod_flag,			boolean my_do_selection_lod_flag)
	{
		do_render_flag				=my_do_render_flag;
		target_id					=0;
		
		target_name					=my_target_name;
		target_comonent_id			=my_target_comonent_id;
		target_driver_id			=my_target_driver_id;
		target_texture_id			=my_target_texture_id;
		
		comp						=my_comp;
		driver_id					=my_driver_id;
		
		camera_id					=my_camera_id;
		parameter_channel_id		=my_parameter_channel_id;
		
		target_view					=(my_target_view==null)
										?new render_target_view():new render_target_view(my_target_view);
		view_volume_box				=(my_view_volume_box==null)
										?new box(-1,-1,-1,1,1,1):new box(my_view_volume_box);

		clip_plane					=my_clip_plane;
		camera_transformation_matrix=my_camera_transformation_matrix;

		main_display_target_flag	=false;
		do_discard_lod_flag			=my_do_discard_lod_flag;
		do_selection_lod_flag		=my_do_selection_lod_flag;
	}
	public int get_render_buffer_id(boolean precision_flag)
	{
		return target_id+target_id+(precision_flag?0:1);
	}
}