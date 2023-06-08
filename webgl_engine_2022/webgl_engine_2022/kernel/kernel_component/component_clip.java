package kernel_component;

import java.util.ArrayList;

import kernel_transformation.plane;

public class component_clip 
{
	public boolean has_done_clip_flag,can_be_clipped_flag;
	
	public ArrayList<plane> clip_plane;
	public int close_clip_plane_number,clipper_test_depth;
	
	public void clear_clip_plane()
	{
		close_clip_plane_number=0;
		clipper_test_depth=0;
		clip_plane.clear();
	}
	public int add_clip_plane(plane my_clip_plane)
	{
		clip_plane.add(my_clip_plane);
		return my_clip_plane.close_clip_plane_flag?1:0;
	}
	public component_clip()
	{
		has_done_clip_flag		=false;
		can_be_clipped_flag		=false;
		clip_plane				=new ArrayList<plane>();
		close_clip_plane_number	=0;
		clipper_test_depth		=0;
	}
}
