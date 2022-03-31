package kernel_render;

import kernel_transformation.plane;
import kernel_transformation.box;
import kernel_component.component;

public class render_target
{
	public String target_name;
	
	public int target_id,render_target_id;
	
	public int camera_id,parameter_channel_id;
	
	public component comp[];
	public int driver_id[];

	public boolean main_display_target_flag,selection_target_flag;
	
	public box view_volume_box;

	public plane clip_plane,mirror_plane;
	
	public int framebuffer_width,framebuffer_height,render_target_number;
	public target_viewport viewport[];

	public void destroy()
	{
		if(comp!=null)
			for(int i=0,ni=comp.length;i<ni;i++)
				if(comp[i]!=null)
					comp[i]=null;
		comp=null;
		driver_id=null;
		clip_plane=null;
		mirror_plane=null;
		
		if(viewport!=null)
			for(int i=0,ni=viewport.length;i<ni;i++)
				if(viewport[i]!=null){
					viewport[i].destroy();
					viewport[i]=null;
				}
		viewport=null;
	}
	public render_target(String my_target_name,int my_camera_id,int my_parameter_channel_id,
			component my_comp[],int my_driver_id[],plane my_clip_plane,
			int my_framebuffer_width,int my_framebuffer_height,int my_render_target_number,
			box my_view_volume_box,target_viewport my_viewport[])
	{
		target_name				=my_target_name;
		
		camera_id				=my_camera_id;
		parameter_channel_id	=my_parameter_channel_id;
		comp					=my_comp;
		driver_id				=my_driver_id;

		main_display_target_flag=false;
		selection_target_flag	=false;
		
		clip_plane				=my_clip_plane;
		mirror_plane			=null;
		
		target_id				=0;
		render_target_id		=0;
		
		framebuffer_width		=my_framebuffer_width;
		framebuffer_height		=my_framebuffer_height;
		render_target_number	=my_render_target_number;

		if(my_view_volume_box==null)
			view_volume_box=new box(-1,-1,-1,1,1,1);
		else
			view_volume_box=new box(my_view_volume_box);

		if((viewport=my_viewport)!=null)
			if(viewport.length>0)
				return;
		viewport=new target_viewport[]
		{
			new target_viewport(-1,-1,2,2,0,0,new double[]{0.0,0.0,0.0,1.0})
		};
	}
	public int get_render_buffer_id(boolean precision_flag)
	{
		return target_id+target_id+(precision_flag?0:1);
	}
	public double []caculate_view_coordinate(double canvas_x,double canvas_y,double aspect)
	{
		if(viewport!=null)
			for(int view_id=0,view_number=viewport.length;view_id<view_number;view_id++){
				target_viewport tv=viewport[view_id];
				double my_viewport_x=2.0*(canvas_x-tv.x)/tv.width -1.0;
				double my_viewport_y=2.0*(canvas_y-tv.y)/tv.height-1.0;;
				if((my_viewport_x>=-1.0)&&(my_viewport_x<=1.0))
					if((my_viewport_y>=-1.0)&&(my_viewport_y<=1.0))
						return new double[]
							{
								my_viewport_x,my_viewport_y,
								aspect*tv.width/tv.height,
								tv.width/tv.height,
								view_id
							};
			}
		return null;
	}
}