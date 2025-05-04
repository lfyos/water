package kernel_render;

import kernel_component.component;
import kernel_transformation.box;
import kernel_transformation.plane;
import kernel_transformation.location;

public class render_target
{
	public boolean do_render_flag,target_or_bundle_flag;
	public int target_id,camera_target_id;
	
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
	
	public render_target(render_target rt)
	{
		do_render_flag				=rt.do_render_flag;
		target_or_bundle_flag		=rt.target_or_bundle_flag;
		
		target_id					=rt.target_id;
		camera_target_id			=rt.camera_target_id;
		
		target_name					=new String(rt.target_name);
		target_comonent_id			=rt.target_comonent_id;
		target_driver_id			=rt.target_driver_id;
		target_texture_id			=rt.target_texture_id;
		
		if((comp=rt.comp)!=null) {
			comp=new component[rt.comp.length];
			for(int i=0,ni=comp.length;i<ni;i++)
				comp[i]=rt.comp[i];
		};
		if((driver_id=rt.driver_id)!=null) {
			driver_id=new int[rt.driver_id.length];
			for(int i=0,ni=driver_id.length;i<ni;i++)
				driver_id[i]=rt.driver_id[i];
		};
		
		camera_id					=rt.camera_id;
		parameter_channel_id		=rt.parameter_channel_id;
		
		target_view					=(rt.target_view==null)
				?null:(new render_target_view(rt.target_view));
		view_volume_box				=(rt.view_volume_box==null)
				?null:(new box(rt.view_volume_box));

		clip_plane					=(rt.clip_plane==null)
				?null:(new plane(rt.clip_plane));
		camera_transformation_matrix=(rt.camera_transformation_matrix==null)
				?null:(new location(rt.camera_transformation_matrix));

		main_display_target_flag	=rt.main_display_target_flag;
		do_discard_lod_flag			=rt.do_discard_lod_flag;
		do_selection_lod_flag		=rt.do_selection_lod_flag;
	}
	public render_target(String my_target_name,
			int my_target_comonent_id,				int my_target_driver_id,		int my_target_texture_id,
			component my_comp[],					int my_driver_id[],
			int my_camera_id,						int my_parameter_channel_id,
			render_target_view my_target_view,		box my_view_volume_box,					
			plane my_clip_plane,					location my_camera_transformation_matrix,
			boolean my_do_discard_lod_flag,			boolean my_do_selection_lod_flag)
	{
		do_render_flag				=true;
		target_or_bundle_flag		=true;
		
		target_id					=0;
		camera_target_id			=-1;
		
		target_name					=(my_target_name==null)?"No_target_name":my_target_name;
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