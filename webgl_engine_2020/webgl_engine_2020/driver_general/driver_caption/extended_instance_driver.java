package driver_caption;

import kernel_part.part;
import driver_text.text_item;
import kernel_camera.camera_result;
import kernel_common_class.format_change;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;


public class extended_instance_driver  extends instance_driver
{
	private String bak_display_information;
	private int text_component_id;
	private long last_time,time_step;
	private String fps_string,fps_name;
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id,int my_text_component_id)
	{
		super(my_comp,my_driver_id);
		bak_display_information="";
		text_component_id=my_text_component_id;
		last_time=0;
		fps_string="";
		
		part p=comp.driver_array[driver_id].component_part;
		String file_name=p.directory_name+p.material_file_name;
		file_reader f=new file_reader(file_name,p.file_charset);
		
		time_step=f.get_long();
		if((fps_name=f.get_string())==null)
			fps_name="";
		
		f.close();
	}
	public void response_init_data(engine_kernel ek,client_information ci)
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
			part p=ci.parameter.comp.driver_array[i].component_part;
			if(p==null)
				continue;
			instance_driver in_d=ci.instance_container.get_driver(ci.parameter.comp,i);
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
			if(((str=in_d.display_parameter.point_title)!=null)&&(ci.parameter.point_id>=0))
				ret_val+=str+Integer.toString(ci.parameter.point_id);
			
			if(ret_val.compareTo("")!=0)
				if((str=in_d.display_parameter.value_title)!=null){
					double my_min_value=in_d.display_parameter.value_min;
					double my_max_value=in_d.display_parameter.value_max;
					double my_value=my_min_value+ci.parameter.value*(my_max_value-my_min_value);
					if(in_d.display_parameter.display_absolute_value_flag)
						my_value=(my_value<0.0)?(0.0-my_value):my_value;
						ret_val+=in_d.display_parameter.value_title;
						ret_val+=format_change.double_to_decimal_string(my_value,display_precision);
				}
			if(ret_val.compareTo("")!=0)
				break;
		}	
		return ret_val;
	}
	private String caculate_fps_string(engine_kernel ek,client_information ci)
	{
		long my_current_time=ek.current_time.nanoseconds();
		if((my_current_time-last_time)<=time_step)
			return fps_string;
		last_time=my_current_time;
		
		long fps=1000*1000*1000;
		switch(fps_name) {
		default:
			return "";
		case "data_time_length":
			fps/=ci.statistics_client.data_time_length;
			break;
		case "render_time_length":
			fps/=ci.statistics_client.render_time_length;
			break;
		case "read_time_length":
			fps/=ci.statistics_client.read_time_length;
			break;
		case "render_interval_length":
			fps/=ci.statistics_client.render_interval_length;
			break;
		}	
		fps_string=" ["+Long.toString(fps)+"fps]";
		
		return fps_string;
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if(!(cr.target.main_display_target_flag))
			return true;

		String new_display_information=pickup_string(ci,ek.scene_par.display_precision);
		
		if(new_display_information.isEmpty())
			new_display_information=ci.message_display.get_display_message();
		
		if(new_display_information.isEmpty())
			if(ek.collector_stack.get_top_collector()!=null)
				if((new_display_information=ek.collector_stack.get_top_collector().description)==null)
					new_display_information="";
		new_display_information+=caculate_fps_string(ek,ci);
		if(new_display_information.compareTo(bak_display_information)==0)
			return true;
		
		bak_display_information=new String(new_display_information);
		
		extended_component_driver ccd=(extended_component_driver)(comp.driver_array[driver_id]);
		text_item t_item=new text_item();
		
		t_item.canvas_width			=ccd.canvas_width;
		t_item.canvas_height		=ccd.canvas_height;
		t_item.text_square_width	=ccd.text_square_width;
		t_item.text_square_height	=ccd.text_square_height;
		
		t_item.display_information	=new String[]
			{
				new String(new_display_information.trim())
			};
		component text_comp=ek.component_cont.get_component(text_component_id);
		if(text_comp!=null)
			for(int i=0,ni=text_comp.driver_number();i<ni;i++)
				if(text_comp.driver_array[i] instanceof driver_text.extended_component_driver)
					((driver_text.extended_component_driver)(text_comp.driver_array[i])).set_text(t_item);
		return true;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(comp.component_id);
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		return null;
	}
}
