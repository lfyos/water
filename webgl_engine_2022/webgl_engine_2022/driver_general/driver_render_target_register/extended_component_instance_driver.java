package driver_render_target_register;

import kernel_part.part;
import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_render.render_target;
import kernel_render.render_target_view;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private int canvas_width_height[][];
	private render_target_parameter target_parameter[];

	public void destroy()
	{
		super.destroy();
		
		canvas_width_height=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		
		canvas_width_height=new int[][]	{new int[]{1,1}	};

		part p=comp.driver_array.get(driver_id).component_part;
		String file_name=p.directory_name+p.material_file_name;
		target_parameter=render_target_parameter.load_parameter(file_name,p.file_charset);
	}
	private void register_target(engine_kernel ek,client_information ci)
	{
		for(int i=0,target_number=target_parameter.length;i<target_number;i++){
			int canvas_id	=target_parameter[i].canvas_id;

			int view_x0		=(int)(Math.round(target_parameter[i].target_x0		*canvas_width_height[canvas_id][0]));
			int view_y0		=(int)(Math.round(target_parameter[i].target_y0		*canvas_width_height[canvas_id][1]));
			int view_width	=(int)(Math.round(target_parameter[i].target_width	*canvas_width_height[canvas_id][0]));
			int view_height	=(int)(Math.round(target_parameter[i].target_height	*canvas_width_height[canvas_id][1]));
			
			render_target_view rtv=new render_target_view(
				view_x0,view_y0,view_width,view_height,
				canvas_width_height[canvas_id][0],canvas_width_height[canvas_id][1]);
			
			double aspect_value	=(double)(rtv.view_width)/(double)(rtv.view_height);
			box view_volume_box=new box(-aspect_value,-1,-1,aspect_value,1,1);

			render_target rt=new render_target(true,comp.component_id,	driver_id,	i,
				new component[] {ek.component_cont.root_component},null,
				target_parameter[i].camera_id,target_parameter[i].parameter_channel_id,
				rtv,view_volume_box,ci.clip_plane,null,true,true);
			
			if(ci.parameter.current_canvas_id==target_parameter[i].canvas_id) {
				double view_x=(ci.parameter.x+1.0)/2.0;
				double view_y=(ci.parameter.y+1.0)/2.0;
				double x0=target_parameter[i].target_x0;
				double x1=target_parameter[i].target_x0+target_parameter[i].target_width;
				double y0=target_parameter[i].target_y0;
				double y1=target_parameter[i].target_y0+target_parameter[i].target_height;
				if((x0<=view_x)&&(view_x<=x1)&&(y0<=view_y)&&(view_y<=y1))
					rt.main_display_target_flag=true;
			}
			ci.target_container.register_target(rt);
		}
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[");
		for(int i=0,ni=target_parameter.length;i<ni;i++)
			ci.request_response.print((i<=0)?"":",",target_parameter[i].canvas_id).
								print(target_parameter[i].load_operation_flag?",1":",0");
		ci.request_response.print("]");
		register_target(ek,ci);
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		register_target(ek,ci);
		return false;
	}
	public void create_render_parameter(int render_buffer_id,
			int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print("0");
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{
		ci.request_response.print("0");
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str;
		
		if((str=ci.request_response.get_parameter("canvas_width_height"))!=null){
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
				if(canvas_width_height.length<=i) {
					int bak[][]=canvas_width_height;
					canvas_width_height=new int[i+1][];
					for(int j=0,nj=bak.length;j<nj;j++)
						canvas_width_height[j]=bak[j];
				}
				canvas_width_height[i]=new int[]{width,height};
			}
		}
		return null;
	}
}
