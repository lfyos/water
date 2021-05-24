package driver_text;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.point;


public class extended_instance_driver extends instance_driver
{
	private int direction_code[];
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		direction_code=new int[0];
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		text_item dt=((extended_component_driver)(comp.driver_array[driver_id])).get_text_item();
		
		if(dt.display_information==null)
			return true;
		int line_number=0;
		for(int j,i=0,ni=dt.display_information.length;i<ni;i++)
			if(dt.display_information[i]!=null)
				if(dt.display_information[i].length()>0)
					if((j=line_number++)!=i)
						dt.display_information[j]=dt.display_information[i];
		if(line_number<=0) {
			dt.display_information=null;
			return true;
		}
		if(dt.display_information.length!=line_number) {
			String bak[]=dt.display_information;
			dt.display_information=new String[line_number];
			for(int i=0;i<line_number;i++)
				dt.display_information[i]=bak[i];
		}
		if(!(cr.target.selection_target_flag))
			if((cr.target.framebuffer_width>0)||(cr.target.framebuffer_height>0))
				return true;
		
		if(direction_code.length<=render_buffer_id){
			int bak[]=direction_code;
			direction_code=new int[render_buffer_id+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				direction_code[i]=bak[i];
			for(int i=bak.length;i<=render_buffer_id;i++)
				direction_code[i]=-1;
		}
		
		if(dt.view_or_model_coordinate_flag) {
			if(direction_code[render_buffer_id]!=0){
				direction_code[render_buffer_id]=0;
				update_component_render_version(render_buffer_id,0);
			}
			return cr.target.main_display_target_flag?false:true;
		}
		
		int new_direction_code=0;
		point p0=comp.absolute_location.multiply(new point(0,0,0));
		point dx=comp.absolute_location.multiply(new point(1,0,0)).sub(p0);
		point dy=comp.absolute_location.multiply(new point(0,1,0)).sub(p0);
		point dz=comp.absolute_location.multiply(new point(0,0,1)).sub(p0);
		
		new_direction_code+=(cr.right_direct.dot(dx)<0)?1:0;
		new_direction_code+=(cr.   up_direct.dot(dy)<0)?2:0;
		
		
		if(direction_code[render_buffer_id]!=new_direction_code){
			direction_code[render_buffer_id]=new_direction_code;
			update_component_render_version(render_buffer_id,0);
		}
		return Math.abs(dz.expand(1).dot(cr.to_me_direct))<0.25;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print("[",data_buffer_id).
			print(",",direction_code[render_buffer_id]).print("]");
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		text_item dt=((extended_component_driver)(comp.driver_array[driver_id])).get_text_item();
		String my_display_information[];
		if((my_display_information=dt.display_information)==null)
			my_display_information=new String[] {};
		
		int text_line_number=my_display_information.length;
		int total_height=dt.canvas_height*text_line_number;

		ci.request_response.print("[[");
		for(int i=0;i<text_line_number;i++){
			String str=my_display_information[i].
				replace("\\","\\\\").
				replace("\n","\\n").
				replace("\r","\\r").
				replace("\"","\\\"").
				replace("\t","    ");
			ci.request_response.print((i==0)?"\"":",\"",str);
			ci.request_response.print("\"");
		}
		ci.request_response.print("],",new int 		[]{dt.canvas_width,total_height,dt.canvas_height}).
							print(",", new double 	[]{dt.text_square_width,dt.text_square_height*text_line_number}).
							print(",",comp.component_id).
							print(",",dt.view_or_model_coordinate_flag	?1:0).
							print("]");
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		return null;
	}
}
