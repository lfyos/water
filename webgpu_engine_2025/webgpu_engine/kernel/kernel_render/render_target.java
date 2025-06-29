package kernel_render;

import kernel_component.component;
import kernel_transformation.box;
import kernel_transformation.plane;
import kernel_transformation.location;

public class render_target
{
	public int target_id,target_id_from;
	
	public boolean do_render_flag,target_or_bundle_flag,main_display_target_flag;

	public int target_comonent_id,target_driver_id,target_texture_id;
	public String target_name;

	public component comp[];
	
	public int camera_id,parameter_channel_id;
	public box view_volume_box;
	public render_target_view target_view;

	public location camera_transformation_matrix;
	
	public plane clip_plane;
	
	public render_target_parameter parameter;

	public render_target(render_target rt)
	{
		target_id					=rt.target_id;
		target_id_from				=rt.target_id_from;
		
		do_render_flag				=rt.do_render_flag;
		target_or_bundle_flag		=rt.target_or_bundle_flag;
		main_display_target_flag	=rt.main_display_target_flag;

		target_comonent_id			=rt.target_comonent_id;
		target_driver_id			=rt.target_driver_id;
		target_texture_id			=rt.target_texture_id;
		target_name					=new String(rt.target_name);
		
		if((comp=rt.comp)!=null) {
			comp=new component[rt.comp.length];
			for(int i=0,ni=comp.length;i<ni;i++)
				comp[i]=rt.comp[i];
		};
		
		camera_id			=rt.camera_id;
		parameter_channel_id=rt.parameter_channel_id;
		
		target_view		=(rt.target_view==null)?null:(new render_target_view(rt.target_view));
		view_volume_box	=(rt.view_volume_box==null)?null:(new box(rt.view_volume_box));

		clip_plane		=(rt.clip_plane==null)?null:(new plane(rt.clip_plane));
		camera_transformation_matrix=(rt.camera_transformation_matrix==null)
									?null:(new location(rt.camera_transformation_matrix));
		parameter=rt.parameter;
	}
	public render_target(int my_target_id_from,
			render_target_parameter 	my_parameter,					String my_target_name,
			int my_target_comonent_id,				int my_target_driver_id,		int my_target_texture_id,
			component my_comp[],					int my_camera_id,				int my_parameter_channel_id,
			render_target_view my_target_view,		box my_view_volume_box,					
			plane my_clip_plane,					location my_camera_transformation_matrix)
	{
		target_id				=0;
		target_id_from			=my_target_id_from;
		
		do_render_flag			=true;
		target_or_bundle_flag	=true;
		main_display_target_flag=false;

		target_comonent_id	=my_target_comonent_id;
		target_driver_id	=my_target_driver_id;
		target_texture_id	=my_target_texture_id;
		target_name			=(my_target_name==null)?"No_target_name":my_target_name;
		
		comp				=my_comp;
		
		camera_id			=my_camera_id;
		parameter_channel_id=my_parameter_channel_id;
		
		target_view		=(my_target_view==null)?new render_target_view():new render_target_view(my_target_view);
		view_volume_box	=(my_view_volume_box==null)?new box(-1,-1,-1,1,1,1):new box(my_view_volume_box);

		clip_plane		=my_clip_plane;
		camera_transformation_matrix=my_camera_transformation_matrix;

		parameter		=my_parameter;
	}
}