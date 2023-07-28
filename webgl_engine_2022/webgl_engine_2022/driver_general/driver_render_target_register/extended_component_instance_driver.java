package driver_render_target_register;

import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_render.render_target;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private int width_height[][];
	
	public void destroy()
	{
		super.destroy();
		width_height=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		
		width_height=new int[][] 
		{
			new int[] {1,1}
		};
	}
	private void register_target(engine_kernel ek,client_information ci)
	{
		int current_canvas_id=ci.parameter.current_canvas_id;
		for(int i=0,ni=width_height.length;i<ni;i++) {
			int width=width_height[i][0],height=width_height[i][1];
			double aspect_value=((double)width)/((double)height);
	
			ci.target_container.register_target(
				new render_target(
						true,	comp.component_id,	driver_id,	i,
						new component[] {ek.component_cont.root_component},		null,	0,	2,
						new box(-aspect_value,-1,-1,aspect_value,1,1),
						null,	null,	(current_canvas_id==i),	true,	true,	true));
		}
	}
	private void register_value_depth_target(engine_kernel ek,client_information ci)
	{
		camera_result cr=ci.display_camera_result;
		int current_canvas_id=ci.parameter.current_canvas_id;
		component comp_array[];
		int driver_id_array[];
		
		if((ci.parameter.comp==null)||(current_canvas_id<0)||(current_canvas_id>=width_height.length)) {
			comp_array=new component[] {comp};
			driver_id_array=new int[] {driver_id};
		}else {
			comp_array=new component[] {ci.parameter.comp,comp};
			driver_id_array=new int[] {0,driver_id};
		}
		
		int width	=width_height[current_canvas_id][0];
		int height	=width_height[current_canvas_id][1];
		box view_volume_box=new box(
				ci.parameter.x*(double)width/(double)height,
				ci.parameter.y,
				-1,
				ci.parameter.x*(double)width/(double)height+1.0/(double)height,
				ci.parameter.y+1.0/(double)height,
				1);
		render_target rt=new render_target(
			true,								//do_render_flag
			comp.component_id,	driver_id,	-1,	//target IDS
			comp_array,		 					//components
			driver_id_array,					//driver_id
			
			cr.target.camera_id,cr.target.parameter_channel_id,		//camera_id,parameter_channel_id
			view_volume_box,					//view box
			cr.target.clip_plane,null,			//clip_plane,mirror_plane
			false,								//main_display_target_flag
			false,								//canvas_display_target_flag
			false,								//do_discard_lod_flag
			false);								//do_selection_lod_flag
		ci.target_container.register_target(rt);
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
		register_target(ek,ci);
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		if(cr.target.main_display_target_flag)
			if(ci.display_camera_result!=null)
				register_value_depth_target(ek,ci);
		return false;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[",comp.component_id).print(",",driver_id).print("]");
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("width_height"))!=null) {
			for(int i=0;str.length()>0;i++) {
				int index_id=str.indexOf('_');
				if(index_id<0)
					break;
				int width=Integer.parseInt(str.substring(0,index_id));
				str=str.substring(index_id+1);
				
				int height;
				if((index_id=str.indexOf('_'))<0) {
					height=Integer.parseInt(str);
					str="";
				}else {
					height=Integer.parseInt(str.substring(0,index_id));
					str=str.substring(index_id+1);
				}
				if(width_height.length<=i) {
					int bak[][]=width_height;
					width_height=new int[i+1][];
					for(int j=0,nj=bak.length;j<nj;j++)
						width_height[j]=bak[j];
				}
				width_height[i]=new int[] {width,height};
			}
			register_target(ek,ci);
		}
		return null;
	}
}
