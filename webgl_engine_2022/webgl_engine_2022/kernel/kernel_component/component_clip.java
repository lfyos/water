package kernel_component;

import kernel_transformation.plane;

public class component_clip 
{
	public boolean has_done_clip_flag,can_be_clipped_flag;
	
	public plane clip_plane[];
	public int close_clip_plane_number,clipper_test_depth;
	
	public void clear_clip_plane()
	{
		close_clip_plane_number=0;
		clipper_test_depth=0;
		clip_plane=null;
	}
	public int add_clip_plane(plane my_clip_plane)
	{
		if(clip_plane==null)
			clip_plane=new plane[1];
		else{
			plane bak_clip_plane[]=clip_plane;
			clip_plane=new plane[clip_plane.length+1];
			for(int i=0,ni=bak_clip_plane.length;i<ni;i++)
				clip_plane[i]=bak_clip_plane[i];
		}
		clip_plane[clip_plane.length-1]=my_clip_plane;
		return my_clip_plane.close_clip_plane_flag?1:0;
	}
	public component_clip()
	{
		clear_clip_plane();
		
		has_done_clip_flag		=false;
		can_be_clipped_flag		=false;
	}
}
