package driver_movement;

import java.io.File;

import kernel_component.component_container;
import kernel_driver.modifier_container;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.string_link_list;

public class movement_manager
{
	public void destroy()
	{
		for(;push_pop_stack_link_list!=null;push_pop_stack_link_list=push_pop_stack_link_list.next_list)
			file_writer.file_delete(push_pop_stack_link_list.str);
	
		move_channel_id	=null;
		parameter		=null;
		directory_name	=null;
		suspend			=null;
		
		if(config_parameter!=null) {
			config_parameter.destroy();
			config_parameter=null;
		}
		if(root_movement!=null) {
			root_movement.destroy();
			root_movement=null;
		}
		if(designed_move!=null) {
			designed_move.destroy();
			designed_move=null;
		}
		if(buffer_movement!=null) {
			for(int i=0,ni=buffer_movement.length;i<ni;i++)
				if(buffer_movement[i]!=null) {
					buffer_movement[i].destroy();
					buffer_movement[i]=null;
				}
			buffer_movement=null;
		}
		if(suspend!=null){
			suspend.destroy();
			suspend=null;
		}
	}
	public movement_tree_id_creator id_creator;
	public movement_configuration_parameter config_parameter;
	public movement_channel_id move_channel_id;
	public movement_parameter parameter;
	public movement_tree root_movement,designed_move;
	public movement_tree buffer_movement[];
	public movement_suspend suspend;
	public boolean  mount_direction_flag;
	
	public String directory_name;
	
	private string_link_list push_pop_stack_link_list;
	private long push_pop_stack_id;
	
	public void create_render_modifier(
			movement_tree t,boolean single_step_flag,int audio_component_id,
			int location_component_id,modifier_container movement_modifier_cont,
			component_container component_cont,long camera_switch_time_length,String sound_pre_string)
	{
		if(root_movement!=null){
			movement_switch_camera_modifier swcm=new movement_switch_camera_modifier(
					single_step_flag,movement_modifier_cont.get_timer().get_current_time(),
					audio_component_id,move_channel_id.display_parameter_channel_id[0],
					config_parameter.camera_modifier_container_id);
			root_movement.register_modifier(suspend,move_channel_id,location_component_id,
					component_cont,parameter,movement_modifier_cont,swcm,directory_name,
					sound_pre_string,camera_switch_time_length,null,0,1.0,mount_direction_flag);
			movement_modifier_cont.add_modifier(swcm);
		}
	}
	public void movement_start(modifier_container modifier_cont,
			long my_movement_id,component_container component_cont,
			boolean new_mount_direction_flag,long camera_switch_time_length)
	{
		movement_tree ct;
		movement_tree_link_list mtll;
		
		if(root_movement!=null)
			if((mtll=(new movement_searcher(root_movement,my_movement_id)).search_link_list)!=null){
				if(mount_direction_flag^new_mount_direction_flag){
					mount_direction_flag=new_mount_direction_flag;
					root_movement.reverse();
				}
				for(ct=mtll.tree_node;ct.children!=null;ct=ct.children[0])
					if(ct.children.length<=0)
						break;
				parameter.current_movement_id=ct.movement_tree_id;

				double mount_precision=parameter.movement_precision;
				double min_box_volume=root_movement.caculate_component_minmal_volume(component_cont);
				root_movement.caculate_time(component_cont,0,camera_switch_time_length,
						min_box_volume*mount_precision*mount_precision*mount_precision);

				long current_time=modifier_cont.get_timer().get_current_time();
				long time_distance=current_time+camera_switch_time_length-ct.start_time;
				
				root_movement.caculate_time(component_cont,root_movement.start_time+time_distance,
						camera_switch_time_length,min_box_volume*mount_precision*mount_precision*mount_precision);

				root_movement.reset_component_state(component_cont,
						move_channel_id.display_parameter_channel_id,
						move_channel_id.hide_parameter_channel_id);
				
				root_movement.sequence_set_component_state(
						move_channel_id.display_parameter_channel_id,
						move_channel_id.hide_parameter_channel_id,
						component_cont,current_time,false);
				root_movement.sequence_set_component_state(
						move_channel_id.display_parameter_channel_id,
						move_channel_id.hide_parameter_channel_id,
						component_cont,current_time,true);
			}
	}
	public void flush(component_container component_cont,
			file_writer f,long camera_switch_time_length,modifier_container modifier_cont)
	{
		f.println("version	2015.10");
		if(root_movement!=null){
			boolean old_mount_direction_flag=mount_direction_flag;
			long current_movement_id=(parameter.current_movement_id<0)
					?root_movement.movement_tree_id:parameter.current_movement_id;
			movement_start(modifier_cont,current_movement_id,
					component_cont,true,camera_switch_time_length);
			root_movement.flush(f,0,true);
			movement_start(modifier_cont,current_movement_id,
					component_cont,old_mount_direction_flag,camera_switch_time_length);
		}
	}
	public void init(modifier_container modifier_cont,component_container component_cont,
			String my_move_file_name,long camera_switch_time_length,String file_system_charset,
			boolean set_directory_name_flag)
	{	
		root_movement=null;
		
		file_reader f=new file_reader(my_move_file_name,file_system_charset);
		f.get_string();
		String version_str=f.get_string();
		
		if(f.eof()){
			f.close();
			reset(-1,modifier_cont,component_cont,camera_switch_time_length);
			
			if(set_directory_name_flag) {
				directory_name=file_reader.separator(my_move_file_name);
				for(int i=directory_name.length()-1;i>=0;i--)
					if(directory_name.charAt(i)==File.separatorChar) {
						directory_name=directory_name.substring(0, i+1);
						return;
					}
				directory_name="."+File.separatorChar;
			}
			return;
		}
		
		if(set_directory_name_flag)
			directory_name=f.directory_name;
		
		int version_compare_result;
		if((version_compare_result=version_str.compareTo("2015.10"))==0)
			root_movement=new movement_tree(f,id_creator);
		else{
			f.close();
			f=new file_reader(my_move_file_name,file_system_charset);
			movement_load_old_file old=new movement_load_old_file(f,id_creator);
			root_movement=old.combine_to_tree(id_creator);
		}
		
		f.close();
		
		root_movement.mount_component(component_cont,"");

		reset(-1,modifier_cont,component_cont,camera_switch_time_length);
		
		if(version_compare_result!=0){
			String user_file_name=f.directory_name+f.file_name;
			file_writer.file_delete(user_file_name+"."+version_str);
			file_writer.file_rename(user_file_name,user_file_name+"."+version_str);
			file_writer fw=new file_writer(user_file_name,file_system_charset);
			flush(component_cont,fw,camera_switch_time_length,modifier_cont);
			fw.close();
		}	
	}
	public void reset(	long target_movement_tree_id,modifier_container modifier_cont,
						component_container component_cont,long camera_switch_time_length)
	{
		if(root_movement!=null)
			movement_start(modifier_cont,
				(target_movement_tree_id<0)?root_movement.movement_tree_id:target_movement_tree_id,
				component_cont,true,camera_switch_time_length);
		designed_move=null;
	}
	
	public String[] push_movement(component_container component_cont,
			long camera_switch_time_length,String file_system_charset,
			modifier_container modifier_cont)
	{
		String my_file_name=config_parameter.temporary_file_directory+(push_pop_stack_id++);
		my_file_name+="_"+Double.toString(Math.random()).replace('.','_')+".txt";
		push_pop_stack_link_list=new string_link_list(my_file_name,push_pop_stack_link_list);
		
		file_writer f=new file_writer(my_file_name,file_system_charset);
		flush(component_cont,f,camera_switch_time_length,modifier_cont);
		f.close();
		
		return (root_movement==null)?null:new String[] {my_file_name,file_system_charset};
	}
	public boolean pop_movement(modifier_container modifier_cont,
			component_container loader,long camera_switch_time_length,String file_system_charset)
	{
		if(push_pop_stack_link_list==null) {
			push_pop_stack_id=0;
			return true;
		}
		String my_file_name=push_pop_stack_link_list.str;
		push_pop_stack_link_list=push_pop_stack_link_list.next_list;
		push_pop_stack_id--;
		
		file_reader ff=new file_reader(my_file_name,file_system_charset);
		if(ff.eof()) {
			ff.close();
			return true;
		}
		ff.close();
		
		init(modifier_cont,loader,my_file_name,camera_switch_time_length,file_system_charset,false);
		
		file_writer.file_delete(my_file_name);
		
		return false;
	}
	public movement_manager(
			engine_kernel ek,long camera_switch_time_length,
			movement_configuration_parameter my_config_parameter,
			movement_channel_id my_move_channel_id)
	{
		push_pop_stack_link_list=null;
		push_pop_stack_id=0;
		
		id_creator=new movement_tree_id_creator();
		
		move_channel_id=my_move_channel_id;
		parameter=new movement_parameter();
		
		mount_direction_flag=true;

		root_movement=null;
		buffer_movement=null;
		suspend=new movement_suspend(ek,my_config_parameter.virtual_mount_root_component_id);
		
		config_parameter=my_config_parameter;
		init(ek.modifier_cont[config_parameter.camera_modifier_container_id],
				ek.component_cont,config_parameter.movement_file_name,
				camera_switch_time_length,config_parameter.movement_file_charset,true);
	}
}
