package kernel_render;

import kernel_transformation.box;
import kernel_transformation.plane;
import kernel_component.component;

public class render_target
{
	public int target_id;
	
	public int target_comonent_id,target_driver_id,target_texture_id;

	public component comp[];
	public int driver_id[];
	
	public int camera_id,parameter_channel_id;
	public box view_volume_box;

	public plane clip_plane,mirror_plane;

	public boolean main_display_target_flag;
	public boolean do_discard_lod_flag,do_selection_lod_flag;

	public void destroy()
	{
		if(comp!=null)
			for(int i=0,ni=comp.length;i<ni;i++)
				if(comp[i]!=null)
					comp[i]=null;
		comp=null;
		driver_id=null;
		view_volume_box=null;
		clip_plane=null;
		mirror_plane=null;
	}
	public render_target(
			int my_target_comonent_id,				int my_target_driver_id,		int my_target_texture_id,
			component my_comp[],					int my_driver_id[],
			int my_camera_id,						int my_parameter_channel_id,	box my_view_volume_box,
			plane my_clip_plane,					plane my_mirror_plane,
			boolean my_main_display_target_flag,	boolean my_do_discard_lod_flag,	boolean my_do_selection_lod_flag)
	{
		target_id				=0;
		target_comonent_id		=my_target_comonent_id;
		target_driver_id		=my_target_driver_id;
		target_texture_id		=my_target_texture_id;
		
		comp					=my_comp;
		driver_id				=my_driver_id;
		
		camera_id				=my_camera_id;
		parameter_channel_id	=my_parameter_channel_id;
		if(my_view_volume_box==null)
			view_volume_box=new box(-1,-1,-1,1,1,1);
		else
			view_volume_box=new box(my_view_volume_box);

		clip_plane				=my_clip_plane;
		mirror_plane			=my_mirror_plane;

		main_display_target_flag=my_main_display_target_flag;
		do_discard_lod_flag		=my_do_discard_lod_flag;
		do_selection_lod_flag	=my_do_selection_lod_flag;
	}
	public int get_render_buffer_id(boolean precision_flag)
	{
		return target_id+target_id+(precision_flag?0:1);
	}
}