package driver_render_target_register;

import kernel_part.part;
import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_render.render_target;
import kernel_common_class.const_value;
import kernel_render.render_target_view;
import kernel_engine.client_information;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private int main_target_id,canvas_width_height[][];
	private render_target_parameter target_parameter[];
	private double clear_color[][];
	private boolean do_discard_lod_flag,do_selection_lod_flag;

	public void destroy()
	{
		super.destroy();
		
		canvas_width_height=null;
		target_parameter=null;
		clear_color=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		
		main_target_id=-1;
		canvas_width_height=new int[][]	{new int[]{1,1}	};

		part p=comp.driver_array.get(driver_id).component_part;
		String file_name=p.directory_name+p.material_file_name;
		target_parameter=render_target_parameter.load_parameter(file_name,p.file_charset);
		
		clear_color=new double[target_parameter.length][];
		for(int i=0,ni=target_parameter.length;i<ni;i++)
			clear_color[i]=new double[] {0,0,0,1};
		do_discard_lod_flag		=true;
		do_selection_lod_flag	=true;
	}
	private void register_target(engine_kernel ek,client_information ci)
	{
		main_target_id=-1;
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

			render_target rt=new render_target(true,
				target_parameter[i].render_target_name,comp.component_id,driver_id,i,
				new component[] {ek.component_cont.root_component},null,
				target_parameter[i].camera_id,target_parameter[i].parameter_channel_id,rtv,
				view_volume_box,ci.clip_plane,null,do_discard_lod_flag,do_selection_lod_flag);
			
			if(ci.parameter.current_canvas_id==target_parameter[i].canvas_id) {
				double view_x=(ci.parameter.x+1.0)/2.0;
				double view_y=(ci.parameter.y+1.0)/2.0;
				double x0=target_parameter[i].target_x0;
				double x1=target_parameter[i].target_x0+target_parameter[i].target_width;
				double y0=target_parameter[i].target_y0;
				double y1=target_parameter[i].target_y0+target_parameter[i].target_height;
				if((x0<=view_x)&&(view_x<=x1)&&(y0<=view_y)&&(view_y<=y1)) {
					rt.main_display_target_flag=true;
					main_target_id=i;
				}
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
	public void create_render_parameter(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print("0");
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[");
		for(int i=0,ni=target_parameter.length;i<ni;i++)
			ci.request_response.
							print((i<=0)?"[":",[",	clear_color[i][0]).
							print(",",				clear_color[i][1]).
							print(",",				clear_color[i][2]).
							print(",",				clear_color[i][3]).
							print("]");
		ci.request_response.print("]");
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str=ci.request_response.get_parameter("operation");
		switch((str==null)?"":str) {
		case "camera":
		{
			if((main_target_id<0)||(main_target_id>=target_parameter.length))
				break;
			if((str=ci.request_response.get_parameter("camera"))==null)
				break;
			int new_camera_id;
			if((new_camera_id=Integer.parseInt(str))>=0)
				if(new_camera_id<ek.camera_cont.size())
					target_parameter[main_target_id].camera_id=new_camera_id;
			break;
		}
		case "width_height":
		{
			if((str=ci.request_response.get_parameter("width_height"))==null)
				break;
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
			break;
		}
		case "parameter_channel":
		{
			if((str=ci.request_response.get_parameter("parameter_channel"))==null)
				break;
			int parameter_channel_id=Integer.parseInt(str);
			if((str=ci.request_response.get_parameter("target"))==null)
				break;
			int target_id=Integer.parseInt(str);
			if(target_id<0)
				for(target_id=0;target_id<target_parameter.length;target_id++)
					target_parameter[target_id].parameter_channel_id=parameter_channel_id;
			else
				if(target_id<target_parameter.length)
					target_parameter[target_id].parameter_channel_id=parameter_channel_id;
			break;
		}
		case "set_clear_color":
		{
			if((str=ci.request_response.get_parameter("target"))==null)
				break;
			int target_id=Integer.parseInt(str);
			int begin_target_id=0,end_target_id=target_parameter.length-1;
			if((target_id>=0)&&(target_id<end_target_id)) {
				begin_target_id=target_id;
				end_target_id=target_id;
			}
			for(target_id=begin_target_id;target_id<=end_target_id;target_id++) {
				if((str=ci.request_response.get_parameter("red"))!=null)
					clear_color[target_id][0]=Double.parseDouble(str);
				if((str=ci.request_response.get_parameter("green"))!=null)
					clear_color[target_id][1]=Double.parseDouble(str);
				if((str=ci.request_response.get_parameter("blue"))!=null)
					clear_color[target_id][2]=Double.parseDouble(str);
				if((str=ci.request_response.get_parameter("alf"))!=null)
					clear_color[target_id][3]=Double.parseDouble(str);
			}
			update_component_parameter_version(0);
			break;
		}
		case "display_precision":
		{
			double value;
			if((str=ci.request_response.get_parameter("low_value"))!=null)
				if((value=Double.parseDouble(str))>=(const_value.min_value))
					ci.display_camera_result.cam.parameter.low_precision_scale=value;
			if((str=ci.request_response.get_parameter("high_value"))!=null)
				if((value=Double.parseDouble(str))>=(const_value.min_value))
					ci.display_camera_result.cam.parameter.high_precision_scale=value;
			break;
		}
		case "turnon_off_lod":
			str=ci.request_response.get_parameter("discard");
			switch((str==null)?"":str.toLowerCase()) {
			case "true":
				do_discard_lod_flag=true;
				break;
			case "false":
				do_discard_lod_flag=false;
				break;
			}
			
			str=ci.request_response.get_parameter("selection");
			switch((str==null)?"":str.toLowerCase()) {
			case "true":
				do_selection_lod_flag=true;
				break;
			case "false":
				do_selection_lod_flag=false;
				break;
			}
			break;
		}
		return null;
	}
}
