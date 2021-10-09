package driver_text_token;

import driver_text.text_item;
import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

import kernel_transformation.location;
import kernel_transformation.point;

public class extended_instance_driver extends instance_driver
{
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
	}
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if(!(cr.target.main_display_target_flag))
			return true;
		
		extended_component_driver ecd=(extended_component_driver)(comp.driver_array[driver_id]);
		ecd.test_pickup_token(ek, ci);
		
		component token_comp,text_comp;
		for(int i=0,ni=ecd.token_point.length;i<ni;i++){
			if(ecd.token_point[i]==null)
				continue;
			if((token_comp=ek.component_cont.get_component(ecd.token_component_id[i]))==null)
				continue;
			if((text_comp=ek.component_cont.get_component(ecd.text_component_id[i]))==null)
				continue;
			if(!(text_comp.driver_array[0] instanceof driver_text.extended_component_driver))
				continue;

			point p0=token_comp.absolute_location.multiply(ecd.token_point[i]);
			point p=ci.display_camera_result.matrix.multiply(p0);
			p=ci.display_camera_result.negative_matrix.multiply(new point(p.x,p.y+0.2,p.z));
			double text_distance=p.sub(p0).distance();
			
			driver_text.extended_component_driver text_ecd;
			text_ecd=(driver_text.extended_component_driver)(text_comp.driver_array[0]);
			text_comp.modify_location(new location(p,
				ci.display_camera_result.right_direct.	add(p),
				ci.display_camera_result.up_direct.		add(p),
				ci.display_camera_result.to_me_direct.	add(p)).
				multiply(location.standard_negative),ek.component_cont);

			text_item dt					=text_ecd.get_text_item();
			int text_length					=ecd.token_text[i].length();
			dt.display_information			=new String[]{ecd.token_text[i]};
			dt.text_square_width			=text_length*text_distance*0.5;
			dt.text_square_height			=text_distance;

			dt.canvas_width					=text_length*32;
			dt.canvas_height				=64;
			dt.view_or_model_coordinate_flag=false;
			
			text_ecd.set_text(dt);
		}
		return false;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		extended_component_driver ecd=(extended_component_driver)(comp.driver_array[driver_id]);
		
		ci.request_response.print("[",comp.component_id);
		for(int i=0,ni=ecd.token_point.length;i<ni;i++)
			if(ecd.token_point[i]!=null)
				ci.request_response.print(",[",		ecd.token_point[i].x).
									print(",",		ecd.token_point[i].y).
									print(",",		ecd.token_point[i].z).
									print(",",		ecd.token_component_id[i]).
									print("]");
		ci.request_response.print("]");
		return;
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		extended_component_driver ecd;
		ecd=(extended_component_driver)(comp.driver_array[driver_id]);
		return ecd.response_event(comp,parameter_channel_id, ek, ci);
	}
}
