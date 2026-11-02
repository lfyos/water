package kernel_render;

public class render_target_flag
{
	public boolean do_discard_lod_flag;
	public boolean do_selection_lod_flag;
	
	public boolean part_list_only_flag;
	public boolean discard_cross_clip_plane_flag;
	public boolean discard_unload_component_flag;
	
	public double lod_precision_scale;

	private render_target_flag(
		boolean my_do_discard_lod_flag,
		boolean my_do_selection_lod_flag,
		boolean my_part_list_only_flag,
		boolean my_discard_cross_clip_plane_flag,
		boolean my_discard_unload_component_flag,
		double	my_lod_precision_scale)
	{
		do_discard_lod_flag				=my_do_discard_lod_flag;
		do_selection_lod_flag			=my_do_selection_lod_flag;
		part_list_only_flag				=my_part_list_only_flag;
		discard_cross_clip_plane_flag	=my_discard_cross_clip_plane_flag;
		discard_unload_component_flag	=my_discard_unload_component_flag;
		
		lod_precision_scale				=my_lod_precision_scale;
	}

	public static render_target_flag create_client_information_flag()
	{
		return new render_target_flag(false,false,false,false,false,1.0);
	}
	public static render_target_flag create_selection_flag(
					boolean my_discard_cross_clip_plane_flag)
	{
		return new render_target_flag(
					false,false,true,my_discard_cross_clip_plane_flag,false,1.0);
	}
	public static render_target_flag create_pickup_flag()
	{
		return new render_target_flag(false,false,false,false,true,1.0);
	}
	public static render_target_flag create_render_flag(
			boolean my_do_discard_lod_flag,boolean my_do_selection_lod_flag,
			double	my_lod_precision_scale)
	{
		return new render_target_flag(
						my_do_discard_lod_flag,my_do_selection_lod_flag,
						false,false,true,my_lod_precision_scale);
	}
}
