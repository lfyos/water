package driver_render_target_register;

import java.util.ArrayList;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_transformation.box;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_render.render_target;
import kernel_scene.client_information;
import kernel_common_class.const_value;
import kernel_render.render_target_view;
import kernel_render.render_target_parameter;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private int main_target_id;
	private ArrayList<int[]>canvas_width_height;
	private register_target_parameter register_parameter[];
	private ArrayList<double[]> clear_color;
	
	private boolean do_discard_lod_flag,do_selection_lod_flag;

	public void destroy()
	{
		super.destroy();
		
		canvas_width_height=null;
		register_parameter=null;
		clear_color=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		
		main_target_id=-1;
		
		(canvas_width_height=new ArrayList<int[]>()).add(new int[]{1,1});

		part p=comp.driver_array.get(driver_id).component_part;
		String file_name=p.directory_name+p.material_file_name;
		register_parameter=register_target_parameter.load_parameter(file_name,p.file_charset);
		
		clear_color=new ArrayList<double[]>();
		for(int i=0,ni=register_parameter.length;i<ni;i++)
			clear_color.add(new double[] {0,0,0,1});
		do_discard_lod_flag		=true;
		do_selection_lod_flag	=true;
	}
	private void register_target(scene_kernel sk,client_information ci)
	{
		main_target_id=-1;
		for(int i=0,target_number=register_parameter.length;i<target_number;i++){
			int my_canvas_width_height[]=canvas_width_height.get(register_parameter[i].canvas_id);

			int view_x0		=(int)(Math.round(register_parameter[i].target_x0		*my_canvas_width_height[0]));
			int view_y0		=(int)(Math.round(register_parameter[i].target_y0		*my_canvas_width_height[1]));
			int view_width	=(int)(Math.round(register_parameter[i].target_width	*my_canvas_width_height[0]));
			int view_height	=(int)(Math.round(register_parameter[i].target_height	*my_canvas_width_height[1]));
			
			render_target_view rtv=new render_target_view(
				view_x0,view_y0,view_width,view_height,my_canvas_width_height[0],my_canvas_width_height[1]);
			
			double aspect_value	=(double)(rtv.view_width)/(double)(rtv.view_height);
			box view_volume_box=new box(-aspect_value,-1,-1,aspect_value,1,1);
			
			var cam_par=sk.camera_cont.get(register_parameter[i].camera_id).parameter;
			var target_par=render_target_parameter.create_render_parameter(
					do_discard_lod_flag,do_selection_lod_flag,
					ci.parameter.high_or_low_precision_flag
					?cam_par.high_precision_scale:cam_par.low_precision_scale);

			render_target rt=new render_target(-1,
				target_par,register_parameter[i].render_target_name,
				comp.component_id,driver_id,i+i+(ci.parameter.high_or_low_precision_flag?0:1),
				new component[] {sk.component_cont.root_component},register_parameter[i].camera_id,
				register_parameter[i].parameter_channel_id,rtv,view_volume_box,ci.clip_plane,null);

			if(ci.parameter.current_canvas_id==register_parameter[i].canvas_id) {
				double view_x=(ci.parameter.x+1.0)/2.0;
				double view_y=(ci.parameter.y+1.0)/2.0;
				double x0=register_parameter[i].target_x0;
				double x1=register_parameter[i].target_x0+register_parameter[i].target_width;
				double y0=register_parameter[i].target_y0;
				double y1=register_parameter[i].target_y0+register_parameter[i].target_height;
				if((x0<=view_x)&&(view_x<=x1)&&(y0<=view_y)&&(view_y<=y1)) {
					rt.main_display_target_flag=true;
					main_target_id=i;
				}
			}
			ci.target_container.register_target(rt);
		}
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
		ci.request_response.print("[");
		for(int i=0,ni=register_parameter.length;i<ni;i++)
			ci.request_response.print((i<=0)?"":",",register_parameter[i].canvas_id).
								print(register_parameter[i].load_operation_flag?",1":",0");
		ci.request_response.print("]");
		register_target(sk,ci);
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		register_target(sk,ci);
		return false;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print("0");
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print("[");
		for(int i=0,ni=register_parameter.length;i<ni;i++) {
			double my_clear_color[]=clear_color.get(i);
			ci.request_response.
							print((i<=0)?"[":",[",	my_clear_color[0]).
							print(",",				my_clear_color[1]).
							print(",",				my_clear_color[2]).
							print(",",				my_clear_color[3]).
							print("]");
		}
		ci.request_response.print("]");
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{
		String str=ci.request_response.get_parameter("operation");
		switch((str==null)?"":str) {
		case "camera":
		{	
			if((main_target_id<0)||(main_target_id>=register_parameter.length))
				break;
			if((str=ci.request_response.get_parameter("camera"))==null)
				break;
			int new_camera_id=Integer.parseInt(str);
			if(new_camera_id<0)
				break;
			if(new_camera_id>=sk.camera_cont.size())
				break;
			register_parameter[main_target_id].camera_id=new_camera_id;
			break;
		}
		case "width_height":
		{
			if((str=ci.request_response.get_parameter("width_height"))==null)
				break;
			for(canvas_width_height.clear();str.length()>0;) {
				int index_id,width,height;
				if((index_id=str.indexOf('_'))<0)
					break;
				width=Integer.parseInt(str.substring(0,index_id));
				str=str.substring(index_id+1);
				if((index_id=str.indexOf('_'))<0) {
					height=Integer.parseInt(str);
					str="";
				}else {
					height=Integer.parseInt(str.substring(0,index_id));
					str=str.substring(index_id+1);
				}
				canvas_width_height.add(new int[]{width,height});
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
				for(target_id=0;target_id<register_parameter.length;target_id++)
					register_parameter[target_id].parameter_channel_id=parameter_channel_id;
			else
				if(target_id<register_parameter.length)
					register_parameter[target_id].parameter_channel_id=parameter_channel_id;
			break;
		}
		case "set_clear_color":
		{
			if((str=ci.request_response.get_parameter("target"))==null)
				break;
			int target_id=Integer.parseInt(str);
			int begin_target_id=0,end_target_id=register_parameter.length-1;
			if((target_id>=0)&&(target_id<end_target_id)) {
				begin_target_id=target_id;
				end_target_id=target_id;
			}
			for(target_id=begin_target_id;target_id<=end_target_id;target_id++) {
				double my_clear_color[]=clear_color.get(target_id);
				if((str=ci.request_response.get_parameter("red"))!=null)
					my_clear_color[0]=Double.parseDouble(str);
				if((str=ci.request_response.get_parameter("green"))!=null)
					my_clear_color[1]=Double.parseDouble(str);
				if((str=ci.request_response.get_parameter("blue"))!=null)
					my_clear_color[2]=Double.parseDouble(str);
				if((str=ci.request_response.get_parameter("alf"))!=null)
					my_clear_color[3]=Double.parseDouble(str);
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
