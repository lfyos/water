package kernel_render;

import kernel_transformation.box;
import kernel_component.component;
import kernel_transformation.plane;

public class render_target
{
	public int target_id,target_id_from;
	
	public boolean do_render_flag,target_or_bundle_flag,main_display_target_flag;

	public int target_comonent_id,target_driver_id,target_texture_id;
	public String target_name;

	public component target_component_array[];
	
	public int camera_id,parameter_channel_id;
	public box view_volume_box;
	public render_target_viewport target_viewport;
	
	public plane clip_plane;
	
	public render_target_flag target_flag;
	
	public double adjust_eye_position_parameter[];

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
		
		if((target_component_array=rt.target_component_array)!=null) {
			target_component_array=new component[rt.target_component_array.length];
			for(int i=0,ni=target_component_array.length;i<ni;i++)
				target_component_array[i]=rt.target_component_array[i];
		};

		camera_id			=rt.camera_id;
		parameter_channel_id=rt.parameter_channel_id;
		
		target_viewport	=(rt.target_viewport==null)?null:(new render_target_viewport(rt.target_viewport));
		view_volume_box	=(rt.view_volume_box==null)?null:(new box(rt.view_volume_box));

		clip_plane		=(rt.clip_plane==null)?null:(new plane(rt.clip_plane));
		
		target_flag		=rt.target_flag;
		
		adjust_eye_position_parameter=new double[] {0,0,0,0,0,0};
		if(rt.adjust_eye_position_parameter!=null) {
			int ni1=adjust_eye_position_parameter.length;
			int ni2=rt.adjust_eye_position_parameter.length;
			for(int i=0,ni=(ni1<ni2)?ni1:ni2;i<ni;i++)
				adjust_eye_position_parameter[i]=rt.adjust_eye_position_parameter[i];
		}
	}
	public render_target(
			int my_target_id_from,			render_target_flag 	my_target_flag,	String my_target_name,
			int my_target_comonent_id,		int my_target_driver_id,			int my_target_texture_id,
			component my_target_component_array[],	int my_camera_id,			int my_parameter_channel_id,
			render_target_viewport 			my_target_viewport,					box my_view_volume_box,
			plane my_clip_plane,			double my_adjust_eye_position_parameter[])
	{
		target_id				=0;
		target_id_from			=my_target_id_from;
		
		do_render_flag			=true;
		target_or_bundle_flag	=true;
		main_display_target_flag=false;

		target_comonent_id		=my_target_comonent_id;
		target_driver_id		=my_target_driver_id;
		target_texture_id		=my_target_texture_id;
		target_name				=(my_target_name==null)?"No_target_name":my_target_name;
		
		target_component_array	=my_target_component_array;
		
		camera_id				=my_camera_id;
		parameter_channel_id	=my_parameter_channel_id;
		
		target_viewport			=(my_target_viewport==null)
				?new render_target_viewport():new render_target_viewport(my_target_viewport);
		view_volume_box			=(my_view_volume_box==null)
				?new box(-1,-1,-1,1,1,1):new box(my_view_volume_box);

		clip_plane				=my_clip_plane;

		target_flag				=my_target_flag;
		
		adjust_eye_position_parameter=new double[] {0,0,0,0,0,0};
		if(my_adjust_eye_position_parameter!=null) {
			int ni1=adjust_eye_position_parameter.length;
			int ni2=my_adjust_eye_position_parameter.length;
			for(int i=0,ni=(ni1<ni2)?ni1:ni2;i<ni;i++)
				adjust_eye_position_parameter[i]=my_adjust_eye_position_parameter[i];
		}
	}
}