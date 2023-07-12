package driver_caption;

import kernel_part.part;
import kernel_component.component;

import java.util.Date;

import kernel_camera.camera_result;
import kernel_engine.engine_kernel;
import kernel_common_class.jason_string;
import kernel_engine.client_information;
import kernel_common_class.format_change;
import kernel_driver.component_instance_driver;

public class extended_component_instance_driver extends component_instance_driver
{
	private String display_information;
	private long last_time=0,max_time_length;

	public void destroy()
	{
		super.destroy();
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id,long my_max_time_length)
	{
		super(my_comp,my_driver_id);
	
		max_time_length=my_max_time_length;
		display_information="";
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
		
	}
	private String pickup_string(client_information ci,int display_precision)
	{
		String ret_val="";

		if(ci.parameter==null)
			return ret_val;
		if(ci.parameter.comp==null)
			return ret_val;
		for(int i=0,ni=ci.parameter.comp.driver_number();i<ni;i++){
			part p=ci.parameter.comp.driver_array.get(i).component_part;
			if(p==null)
				continue;
			component_instance_driver in_d=ci.component_instance_driver_cont.get_component_instance_driver(ci.parameter.comp,i);
			if(in_d==null)
				continue;
			if(ci.parameter.comp.uniparameter.display_part_name_or_component_name_flag){
				if(p.user_name!=null)
					ret_val+=p.user_name;
			}else{
				if(ci.parameter.comp.component_name!=null)
					ret_val+=ci.parameter.comp.component_name;
			}
			ret_val+="["+Integer.toString(p.permanent_render_id);
			ret_val+=","+Integer.toString(p.permanent_part_id)+"]";

			String str;
			if(((str=in_d.display_parameter.body_title)!=null)&&(ci.parameter.body_id>=0))
				ret_val+=str+Integer.toString(ci.parameter.body_id);
			if(((str=in_d.display_parameter.face_title)!=null)&&(ci.parameter.face_id>=0))
				ret_val+=str+Integer.toString(ci.parameter.face_id);
			if(((str=in_d.display_parameter.vertex_title)!=null)&&(ci.parameter.vertex_id>=0))
				ret_val+=str+Integer.toString(ci.parameter.vertex_id);
			if(((str=in_d.display_parameter.loop_title)!=null)&&(ci.parameter.loop_id>=0))
				ret_val+=str+Integer.toString(ci.parameter.loop_id);
			if(((str=in_d.display_parameter.edge_title)!=null)&&(ci.parameter.edge_id>=0))
				ret_val+=str+Integer.toString(ci.parameter.edge_id);
			if(((str=in_d.display_parameter.primitive_title)!=null)&&(ci.parameter.primitive_id>=0))
				ret_val+=str+Integer.toString(ci.parameter.primitive_id);
			if(((str=in_d.display_parameter.vertex_title)!=null)&&(ci.parameter.vertex_id>=0))
				ret_val+=str+Integer.toString(ci.parameter.vertex_id);
			
			if(ret_val.compareTo("")!=0)
				if((str=in_d.display_parameter.value_title)!=null){
					double my_min_value[]=in_d.display_parameter.value_min;
					double my_max_value[]=in_d.display_parameter.value_max;
					double my_value[]=new double[ci.parameter.value.length];
					for(int j=0,nj=my_value.length;j<nj;j++) {
						my_value[j]=my_min_value[j]+ci.parameter.value[j]*(my_max_value[j]-my_min_value[j]);
						if(in_d.display_parameter.display_absolute_value_flag)
							my_value[j]=Math.abs(my_value[j]);
					}
					ret_val+=in_d.display_parameter.value_title;
					for(int j=0,nj=my_value.length;j<nj;j++) {
						ret_val+=format_change.double_to_decimal_string(my_value[j],display_precision);
						ret_val+=(j>0)?",":"";
					}
				}
			if(ret_val.compareTo("")!=0)
				break;
		}	
		return ret_val;
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		String new_display_information;
		if((new_display_information=pickup_string(ci,ek.scene_par.display_precision)).isEmpty())
			if((new_display_information=ci.message_display.get_display_message()).isEmpty())
				if(ek.collector_stack.get_top_collector()!=null)
					if((new_display_information=ek.collector_stack.get_top_collector().description)==null)
						new_display_information="";
		if(new_display_information.isEmpty()) {
			display_information=new_display_information;
			return true;
		}
		if(new_display_information.compareTo(display_information)!=0){
			display_information=new_display_information;
			update_component_parameter_version(0);
			last_time=new Date().getTime();
		}
		return ((new Date().getTime()-last_time)>max_time_length)?true:false;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(int data_buffer_id,engine_kernel ek,client_information ci)
	{
		ci.request_response.print(jason_string.change_string(display_information));
	}
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		return null;
	}
}
