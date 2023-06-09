package driver_movement;

import java.util.ArrayList;

import kernel_camera.camera;
import kernel_camera.locate_camera;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_component.component_container;
import kernel_component.component_array;
import kernel_driver.modifier_driver;
import kernel_driver.component_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_common_class.debug_information;

public class movement_switch_camera_modifier extends modifier_driver
{
	class movement_switch_information
	{
		public int main_component_id,component_id;
		public location direction,start_location,terminate_location;
		public movement_switch_information(int my_main_component_id,int my_component_id,
					location my_direction,location my_start_location,location my_terminate_location)
		{
			main_component_id	=my_main_component_id;
			component_id		=my_component_id;
			direction			=my_direction;
			start_location		=my_start_location;
			terminate_location	=my_terminate_location;
		}
	};
	
	private ArrayList<movement_switch_information> container;
	
	private boolean single_step_flag;
	private int switch_camera_number;
	private double scale_value;
	private int audio_component_id,parameter_channel_id;
	private int movement_modifier_container_id,camera_modifier_container_id;
	public String title_string,information_string,sound_file_name;
	
	public void destroy()
	{
		super.destroy();
		
		if(container!=null) {
			container.clear();
			container=null;
		}
		
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
		box b,return_value=null;
		component comp,main_comp;
		for(int i=0,ni=container.size();i<ni;i++) {
			movement_switch_information p=container.get(i);
			if((main_comp=component_cont.get_component(p.main_component_id))!=null)
				if((comp=component_cont.get_component(p.component_id))!=null){
					caculate_component_location(main_comp,component_cont);
					main_comp.caculate_box(true);
					caculate_component_location(comp,component_cont);
					comp.caculate_box(true);

					if(p.start_location!=null)
						if((b=caculate_move_box(comp,main_comp.parent_and_relative_location.multiply(p.start_location)))!=null)
							return_value=(return_value==null)?b:return_value.add(b);
					if(p.terminate_location!=null)
						if((b=caculate_move_box(comp,main_comp.parent_and_relative_location.multiply(p.terminate_location)))!=null)
							return_value=(return_value==null)?b:return_value.add(b);
				}
		}
		return return_value;
	}
	public void reset()
	{
		scale_value=-1.0;
		container.clear();
	}
	public void register_move_component(int my_main_component_id,int my_component_id,
			double my_scale_value,location my_direction,
			location my_start_location,location my_terminate_location,
			String my_title_string,String my_information_string,String my_sound_file_name)
	{
		container.add(
			new movement_switch_information(
					my_main_component_id,my_component_id,
					my_direction, my_start_location,my_terminate_location));
		
		if(my_scale_value>scale_value)
			scale_value=my_scale_value;
		
		title_string		=my_title_string;
		information_string	=my_information_string;
		sound_file_name		=(my_sound_file_name==null)?sound_file_name:file_reader.separator(my_sound_file_name);
	}
	public boolean can_start(long my_current_time,engine_kernel ek,client_information ci)
	{
		if(super.can_start(my_current_time,ek,ci))
			if(can_start_routine(my_current_time,ek,ci)){
				if(container.size()>0){
					if(single_step_flag)
						if(switch_camera_number>0){
							ek.modifier_cont[movement_modifier_container_id].set_clear_modifier_flag();
							ek.modifier_cont[camera_modifier_container_id].set_clear_modifier_flag();
							reset();
							return false;
						}
					process_routine(my_current_time,ek,ci);
					reset();
					switch_camera_number++;
				}
				return true;
			}
		return false;
	}
	private driver_audio_player.extended_component_driver get_acd(engine_kernel ek)
	{
		component audio_comp=ek.component_cont.get_component(audio_component_id);
		if(audio_comp==null){
			debug_information.println(
				"movement_switch_camera_modifier finds audio component NOT EXIST",
				audio_component_id);
				return null;
		}
		component_driver c_d=audio_comp.driver_array.get(0);
		
		if(!(c_d instanceof driver_audio_player.extended_component_driver)){
			debug_information.print(
				"movement_switch_camera_modifier finds audio component IS NOT audio_component_driver:\t",
				audio_comp.component_name);
			debug_information.println("\taudio_component_id",audio_component_id);
			return null;
		}
		return (driver_audio_player.extended_component_driver)c_d;
	}
	private boolean can_start_routine(long my_current_time,engine_kernel ek,client_information ci)
	{
		if((sound_file_name==null)||(audio_component_id<0))
			return true;
		
		driver_audio_player.extended_component_driver acd;
		if((acd=get_acd(ek))==null)
			return true;
		if(!(acd.get_state()))
			return true;
		
		String play_audio_file_name;
		if((play_audio_file_name=acd.get_audio_file_name())==null)
			return true;
		if(sound_file_name.compareTo(play_audio_file_name)==0)
			return true;
		
		return acd.get_terminate_flag();
	}
	private void register_visible_component(component comp,component_array comp_array,int depth)
	{
		int child_number;
		if(depth>0)
			if(!(comp.get_effective_display_flag(parameter_channel_id)))
				return;
		if((child_number=comp.children_number())<=0)
			comp_array.add_component(comp);
		else
			for(int i=0;i<child_number;i++)
				register_visible_component(comp.children[i],comp_array,depth+1);
	}
	private void process_routine(long my_current_time,engine_kernel ek,client_information ci)
	{
		box b;
		location direction[]=new location[container.size()];
		for(int i=0,ni=direction.length;i<ni;i++)
			direction[i]=container.get(i).direction;
		
		if((b=caculate_box(ek.component_cont))!=null){
			location dir=location.combine_location(direction);
			for(int i=0,n=ek.camera_cont.size();i<n;i++) {
				camera cam=ek.camera_cont.get(i);
				if(cam.parameter.movement_flag)
					(new locate_camera(cam)).locate_on_components(
						ek.modifier_cont[camera_modifier_container_id],b,
						cam.parameter.direction_flag?dir:null,
						(cam.parameter.scale_value<=0)?-1.0:scale_value,
						true,false,false);
			}
		}
		component comp;
		component_array comp_array=new component_array();
		for(int i=0,ni=container.size();i<ni;i++)
			if((comp=ek.component_cont.get_component(container.get(i).component_id))!=null)
				register_visible_component(comp,comp_array,0);

		ek.collector_stack.push_component_array(true,
				ek.system_par,ek.scene_par,comp_array,ek.component_cont,ek.render_cont.renders);

		component_collector cc=ek.collector_stack.get_top_collector();
		
		cc.title=title_string;
		cc.description=information_string;	
		cc.audio_file_name=sound_file_name;
		
		if(sound_file_name==null)
			return;
		driver_audio_player.extended_component_driver acd=get_acd(ek);
		if(acd==null)
			return;
		acd.set_audio(sound_file_name);
	}
	public void modify(long my_current_time,engine_kernel ek,client_information ci)
	{
		super.modify(my_current_time,ek,ci);
	}
	public void last_modify(long my_current_time,engine_kernel ek,client_information ci,boolean terminated_flag)
	{
		super.last_modify(my_current_time,ek,ci,terminated_flag);
	}
	public movement_switch_camera_modifier(boolean my_single_step_flag,long current_time,
			int my_audio_component_id,int my_parameter_channel_id,
			int my_movement_modifier_container_id,int my_camera_modifier_container_id)
	{
		super(current_time,Long.MAX_VALUE);
		
		container=new ArrayList<movement_switch_information>();
		
		single_step_flag=my_single_step_flag;
		switch_camera_number=0;

		audio_component_id			=my_audio_component_id;
		parameter_channel_id		=my_parameter_channel_id;
		movement_modifier_container_id=my_movement_modifier_container_id;
		camera_modifier_container_id=my_camera_modifier_container_id;
		
		title_string		="";
		information_string	="";
		sound_file_name		=null;

		reset();
	}
}
