package kernel_buffer;

import kernel_driver.component_driver;
import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_network.client_request_response;
import kernel_render.render_component_counter;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_transformation.location;

public class component_location_buffer
{
	private component_collector location_collector; 
	private boolean component_not_in_list_flag[];
	private boolean has_not_response_relative_location_flag[];
	
	private long component_location_version[],touch_time[];
	
	public void destroy()
	{
		if(location_collector!=null) {
			location_collector.destroy();
			location_collector=null;
		}
		
		component_not_in_list_flag				=null;
		has_not_response_relative_location_flag	=null;
		
		component_location_version				=null;
		touch_time								=null;
	}
	
	public component_location_buffer(scene_kernel sk)
	{
		int length=1;
		if(sk.component_cont.root_component!=null)
			length=sk.component_cont.root_component.component_id+1;

		component_location_version						=new long		[length];
		touch_time										=new long		[length];
		component_not_in_list_flag						=new boolean	[length];
		has_not_response_relative_location_flag			=new boolean	[length];
		
		for(int i=0;i<length;i++){
			component_not_in_list_flag[i]				=true;
			has_not_response_relative_location_flag[i]	=true;
			component_location_version[i]				=-1;
			touch_time[i]								=0;
		}
		
		location_collector=new component_collector(sk.render_cont.renders);
	}
	private int []get_render_part_id(component comp,scene_kernel sk)
	{
		component_driver c_d;
		if(comp.driver_number()>0)
			for(int i=0,ni=comp.driver_number();i<ni;i++)
				if((c_d=comp.driver_array.get(i))!=null)
					if(c_d.component_part!=null)
						return new int[] 
							{
								c_d.component_part.render_id,
								c_d.component_part.part_id
							};
		return new int[] 
			{
				sk.process_part_sequence.process_parts_sequence[0][0],
				sk.process_part_sequence.process_parts_sequence[0][1]
			};
	}
	public void put_in_list(component response_component,scene_kernel sk)
	{
		if(response_component!=null){
			int render_part_id[]=get_render_part_id(response_component,sk);
			int render_id=render_part_id[0],part_id=render_part_id[1];
			
			long my_touch_time=response_component.uniparameter.touch_time;
			for(component comp=response_component;comp!=null;comp=sk.component_cont.get_component(comp.parent_component_id))
				if((comp.uniparameter.do_response_location_flag)||(has_not_response_relative_location_flag[comp.component_id]))
					if(comp.get_location_version()!=component_location_version[comp.component_id])
						if(component_not_in_list_flag[comp.component_id])
							if(location_collector.register_component(comp,0,render_id,part_id)>0){
								component_not_in_list_flag[comp.component_id]=false;
								if((touch_time[comp.component_id]=comp.uniparameter.touch_time)<my_touch_time)
									touch_time[comp.component_id]=my_touch_time;
							}
		}
	}
	private static void response_location_data(location loca,
			client_request_response request_response)
	{
		double parameter[]=loca.caculate_translation_rotation(true);
		int code=0,number=0;
		for(int i=0;i<6;i++){
			code+=code;
			if(Math.abs(parameter[i])>const_value.min_value){
				request_response.print(((number++)<=0)?"[":",",parameter[i]);
				code++;
			}
		}
		request_response.print((number<=0)?"[]":(","+code+"]"));		
	}

	public void response_location(scene_kernel sk,client_information ci,render_component_counter rcc)
	{
		if(sk.camera_cont!=null)
			for(int i=0,ni=sk.camera_cont.size();i<ni;i++) {
				component eye_component;
				if((eye_component=sk.camera_cont.get(i).eye_component)==null)
					continue;
				if(	  (sk.camera_cont.get(i).parameter.synchronize_location_flag)
					||(has_not_response_relative_location_flag[eye_component.component_id]))
						put_in_list(eye_component,sk);
			}
		long my_current_time=sk.current_time.nanoseconds();
	
		ci.request_response.print(",[");
		
		for(int response_number=0,i=0,ni=sk.process_part_sequence.process_parts_sequence.length;i<ni;i++){
			int render_id			=sk.process_part_sequence.process_parts_sequence[i][0];
			int part_id				=sk.process_part_sequence.process_parts_sequence[i][1];
			component_link_list p	=location_collector.component_collector[render_id][part_id];
			
			for(;p!=null;p=p.next_list_item){
				component_not_in_list_flag[p.comp.component_id]=true;
				if(rcc.update_location_number>=sk.scene_par.most_update_location_number)
					if((my_current_time-touch_time[p.comp.component_id])>sk.scene_par.touch_time_length)
						continue;
			
				component_location_version[p.comp.component_id]=p.comp.get_location_version();
				
				ci.request_response.print(((response_number++)<=0)?"[":",[",p.comp.component_id);
				ci.request_response.print(",",p.comp.uniparameter.cacaulate_location_flag?"1,":"-1,");
				response_location_data(p.comp.move_location,ci.request_response);
				rcc.update_location_number++;

				if(has_not_response_relative_location_flag[p.comp.component_id]){
					ci.request_response.print(",");
					has_not_response_relative_location_flag[p.comp.component_id]=false;
					response_location_data(p.comp.relative_location,ci.request_response);
					ci.request_response.print(",",p.comp.parent_component_id);
					rcc.update_location_number++;
				}
				ci.request_response.print("]");
			}
		}
		ci.request_response.print("]");
		
		location_collector.reset();
	}
	public void synchronize_location_version(component comp,scene_kernel sk,boolean update_flag)
	{
		int render_part_id[]=get_render_part_id(comp,sk);
		int render_id		=render_part_id[0];
		int part_id			=render_part_id[1];
		
		component_location_version	[comp.component_id]=update_flag?-1:comp.get_location_version();
		
		if(component_not_in_list_flag[comp.component_id])
			return;
		if(location_collector.component_collector==null)
			return;
		if(location_collector.component_collector[render_id]==null)
			return;
		component_link_list q=null;
		component_link_list p=location_collector.component_collector[render_id][part_id];
		for(;p!=null;p=p.next_list_item)
			if(p.comp.component_id!=comp.component_id)
				q=new component_link_list(p.comp,p.driver_id,q);
		location_collector.component_collector[render_id][part_id]=q;
		
		if(update_flag)
			put_in_list(comp,sk);
		
		return;
	}
}
