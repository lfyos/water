package driver_movement;

import kernel_camera.locate_camera;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_component.component_array;
import kernel_engine.component_container;
import kernel_driver.modifier_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_common_class.debug_information;


public class movement_switch_camera_modifier extends modifier_driver
{
	private double scale_value;
	
	private int main_component_id[],component_id[],audio_component_id;
	private location direction[],start_location[],terminate_location[];
	private int component_number;
	
	public String title_string,information_string,sound_file_name;
	
	public void destroy()
	{
		super.destroy();
		main_component_id=null;
		component_id=null;
		direction=null;
		start_location=null;
		terminate_location=null;
		
		title_string=null;
		information_string=null;
		sound_file_name=null;
	}
	
	private void caculate_component_location(component comp,component_container component_cont)
	{
		if(comp!=null){
			component comp_parent;
			if((comp_parent=component_cont.get_component(comp.parent_component_id))!=null)
				caculate_component_location(comp_parent,component_cont);
			comp.caculate_location(component_cont);
		}
	}
	private box caculate_move_box(component comp,location loca)
	{
		if((loca==null)||(comp.model_box==null))
			return comp.get_component_box(true);
		else
			return loca.multiply(comp.model_box);
	}
	private box caculate_box(component_container component_cont)
	{
		component comp,main_comp;
		box return_value=null;
		for(int i=0;i<component_number;i++)
			if((main_comp=component_cont.get_component(main_component_id[i]))!=null)
				if((comp=component_cont.get_component(component_id[i]))!=null){
					caculate_component_location(main_comp,component_cont);
					main_comp.caculate_box(true);
					caculate_component_location(comp,component_cont);
					comp.caculate_box(true);
					box b;
					if(start_location[i]!=null)
						if((b=caculate_move_box(comp,main_comp.parent_and_relative_location.multiply(start_location[i])))!=null)
							return_value=(return_value==null)?b:return_value.add(b);
					if(terminate_location[i]!=null)
						if((b=caculate_move_box(comp,main_comp.parent_and_relative_location.multiply(terminate_location[i])))!=null)
							return_value=(return_value==null)?b:return_value.add(b);
				}
		return return_value;
	}
	public void reset()
	{
		scale_value=-1.0;
		component_number=0;
		for(int i=0,n=component_id.length;i<n;i++){
			main_component_id[i]	=-1;
			component_id[i]			=-1;
			direction[i]			=null;
			start_location[i]		=null;
			terminate_location[i]	=null;
		}
	}
	public void register_move_component(
			int my_main_component_id,int my_component_id,
			double my_scale_value,location my_direction,
			location my_start_location,location my_terminate_location,
			String my_title_string,String my_information_string,String my_sound_file_name)
	{
		if(component_number>=component_id.length){
			int bak_main_component_id[]			=main_component_id;
			int bak_component_id[]				=component_id;
			location bak_direction[]			=direction;
			location bak_start_location[]		=start_location;
			location bak_terminate_location[]	=terminate_location;
						
			main_component_id	=new int[component_number+1];
			component_id		=new int[component_number+1];		
			direction			=new location [component_number+1];		
			start_location		=new location [component_number+1];		
			terminate_location	=new location [component_number+1];			
			for(int i=0;i<component_number;i++){
				main_component_id[i]	=bak_main_component_id[i];
				component_id[i]			=bak_component_id[i];
				direction[i]			=bak_direction[i];
				start_location[i]		=bak_start_location[i];
				terminate_location[i]	=bak_terminate_location[i];
			}
		}
		main_component_id	[component_number  ]=my_main_component_id;
		component_id		[component_number  ]=my_component_id;
		direction			[component_number  ]=my_direction;
		start_location		[component_number  ]=my_start_location;
		terminate_location	[component_number++]=my_terminate_location;
		
		if(my_scale_value>scale_value)
			scale_value=my_scale_value;
		
		title_string		=my_title_string;
		information_string	=my_information_string;
		sound_file_name		=my_sound_file_name;
	}
	public boolean can_start(long my_current_time,engine_kernel ek,client_information ci)
	{
		super.can_start(my_current_time,ek,ci);
		terminate_time=my_current_time+1024*1024;
		if((sound_file_name!=null)&&(audio_component_id>=0)){
			component audio_comp=ek.component_cont.get_component(audio_component_id);
			if(audio_comp==null){
				debug_information.println(
						"movement_switch_camera_modifier finds audio component NOT EXIST",
						audio_component_id);
				return true;
			}
			if(!(audio_comp.driver_array[0] instanceof driver_audio.extended_component_driver)){
				debug_information.print(
						"movement_switch_camera_modifier finds audio component IS NOT audio_component_driver:\t",
						audio_comp.component_name);
				debug_information.println("\taudio_component_id",audio_component_id);
				return true;
			}
			driver_audio.extended_component_driver acd=(driver_audio.extended_component_driver)(audio_comp.driver_array[0]);
			if(acd.get_state()){
				String play_audio_file_name;
				if((play_audio_file_name=acd.get_audio_file_name())!=null)
					if(play_audio_file_name.compareTo(sound_file_name)!=0)
						return acd.get_terminate_flag();
			}
		}
		return true;
	}
	private int movement_modifier_id;
	public void modify(long my_current_time,engine_kernel ek,client_information ci)
	{
		super.modify(my_current_time,ek,ci);
		
		if(component_number<=0)
			return;
		box b;
		if((b=caculate_box(ek.component_cont))!=null){
			location dir=location.combine_location(direction);
			for(int i=0,n=ek.camera_cont.camera_array.length;i<n;i++)
				if(ek.camera_cont.camera_array[i].parameter.movement_flag)
					(new locate_camera(ek.camera_cont.camera_array[i])).locate_on_components(
							ek.modifier_cont[movement_modifier_id],b,
							ek.camera_cont.camera_array[i].parameter.direction_flag?dir:null,
							(ek.camera_cont.camera_array[i].parameter.scale_value<=0)?-1.0:scale_value,
							ci.parameter.aspect,true,false,false);
		}
		
		component_array comp_array=new component_array(ek.component_cont.root_component.component_id+1);
		for(int i=0;i<component_number;i++){
			component comp=ek.component_cont.get_component(component_id[i]);
			if(comp!=null)
				comp_array.add_component(comp);
		}
		ek.collector_stack.push_component_array(true,
				ek.system_par,ek.scene_par,comp_array,ek.component_cont,ek.render_cont.renders);

		component_collector cc=ek.collector_stack.get_top_collector();
		
		cc.title=title_string;
		cc.description=information_string;	
		cc.audio_file_name=sound_file_name;
		
		if((sound_file_name!=null)&&(audio_component_id>=0)){
			component audio_comp=ek.component_cont.get_component(audio_component_id);
			if(audio_comp==null)
				debug_information.println(
						"movement_switch_camera_modifier finds audio component NOT EXIST");
			else if(!(audio_comp.driver_array[0] instanceof driver_audio.extended_component_driver)){
				debug_information.print(
						"movement_switch_camera_modifier finds audio component IS NOT audio_component_driver:\t",
						audio_comp.component_name);
				debug_information.println("\taudio_component_id",audio_component_id);
			}else{
				driver_audio.extended_component_driver acd=(driver_audio.extended_component_driver)(audio_comp.driver_array[0]);
				acd.set_audio(sound_file_name);
			}
		}
		reset();
	}
	public void last_modify(long my_current_time,engine_kernel ek,client_information ci,boolean terminated_flag)
	{
		super.last_modify(my_current_time,ek,ci,terminated_flag);
	}
	public movement_switch_camera_modifier(long current_time,
			int my_movement_modifier_id,int my_audio_component_id)
	{
		super(current_time,Long.MAX_VALUE);
	
		movement_modifier_id=my_movement_modifier_id;
		audio_component_id	=my_audio_component_id;
		
		main_component_id	=new int[1];
		component_id		=new int[1];
		direction			=new location[1];
		start_location		=new location[1];
		terminate_location	=new location[1];
		
		title_string		="";
		information_string	="";
		sound_file_name		=null;
		
		reset();
	}
}
