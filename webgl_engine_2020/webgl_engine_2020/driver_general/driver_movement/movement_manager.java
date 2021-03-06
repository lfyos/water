package driver_movement;

import kernel_engine.component_container;
import kernel_driver.modifier_container;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class movement_manager
{
	public void destroy()
	{
		move_channel_id=null;
		parameter=null;
		directory_name=null;
		
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
		if(movement_stack!=null) {
			for(int i=0,ni=movement_stack.length;i<ni;i++)
				movement_stack[i]=null;
			movement_stack=null;
		}
	}
	
	public movement_configuration_parameter config_parameter;
	public movement_channel_id move_channel_id;
	public movement_parameter parameter;
	public movement_tree root_movement,designed_move;
	public movement_tree buffer_movement[];
	
	public boolean  mount_direction_flag;
	
	private void set_direction(
			component_container component_cont,modifier_container modifier_cont,
			boolean new_mount_direction_flag,long camera_switch_time_length)
	{
		if(root_movement==null)
			return ;
		if((mount_direction_flag)&&(new_mount_direction_flag))
			return ;
		if((!mount_direction_flag)&&(!new_mount_direction_flag))
			return ;
		
		mount_direction_flag=new_mount_direction_flag;
		
		root_movement.reverse();
		long current_movement_time=modifier_cont.get_timer().get_current_time();
		long t=current_movement_time-(root_movement.terminate_time-current_movement_time);
		double mount_precision=parameter.movement_precision;
		double min_box_volume=root_movement.caculate_component_minmal_volume(component_cont);
		root_movement.caculate_time(component_cont,t,camera_switch_time_length,
				min_box_volume*mount_precision*mount_precision*mount_precision);
	}
	public void create_render_modifier(
			int movement_modifier_container_id,int audio_component_id,
			int location_component_id,modifier_container movement_modifier_cont,
			component_container component_cont,boolean direction_flag,
			long camera_switch_time_length,String sound_pre_string)
	{
		if(root_movement==null)
			return;
		movement_tree t=root_movement.search_movement(parameter.current_movement_id);
		if(t==null)
			for(t=root_movement;t.children!=null;t=t.children[0])
				if(t.children.length<=0)
					break;
		parameter.current_movement_id=t.movement_tree_id;	
		movement_start(movement_modifier_cont,parameter.current_movement_id,
				component_cont,direction_flag,camera_switch_time_length);
		movement_switch_camera_modifier swcm=new movement_switch_camera_modifier(
				movement_modifier_cont.get_timer().get_current_time(),
				move_channel_id.movement_modifier_id,audio_component_id);
		root_movement.register_modifier(move_channel_id,location_component_id,
				component_cont,parameter,movement_modifier_cont,swcm,directory_name,sound_pre_string,
				t.start_time-camera_switch_time_length,camera_switch_time_length,
				null,0,1.0,mount_direction_flag);
		movement_modifier_cont.add_modifier(swcm);
	}

	public void set_component_state(component_container component_cont,modifier_container modifier_cont)
	{
		if(root_movement!=null){
			long my_current_time=modifier_cont.get_timer().get_current_time();
			root_movement.reset_component_state(component_cont,
					move_channel_id.display_parameter_channel_id,move_channel_id.hide_parameter_channel_id);
			if(mount_direction_flag){
				root_movement.sequence_set_component_state(move_channel_id.display_parameter_channel_id,
					move_channel_id.hide_parameter_channel_id,component_cont,my_current_time,false);
				root_movement.sequence_set_component_state(move_channel_id.display_parameter_channel_id,
					move_channel_id.hide_parameter_channel_id,component_cont,my_current_time,true);
			}else{
				root_movement.sequence_set_component_state(move_channel_id.display_parameter_channel_id,
					move_channel_id.hide_parameter_channel_id,component_cont,my_current_time,true);
				root_movement.sequence_set_component_state(move_channel_id.display_parameter_channel_id,
					move_channel_id.hide_parameter_channel_id,component_cont,my_current_time,false);
			}
		}
	}
	public void movement_start(modifier_container modifier_cont,
			int my_movement_id,component_container component_cont,
			boolean new_mount_direction_flag,long camera_switch_time_length)
	{
		movement_tree tree,ct;
		
		if(root_movement!=null)
			if((tree=(new movement_searcher(root_movement,my_movement_id)).result)!=null){
								
				set_direction(component_cont,modifier_cont,new_mount_direction_flag,camera_switch_time_length);
				double mount_precision=parameter.movement_precision;
				double min_box_volume=root_movement.caculate_component_minmal_volume(component_cont);
				root_movement.caculate_time(component_cont,0,camera_switch_time_length,min_box_volume*mount_precision*mount_precision*mount_precision);
				
				long time_distance=modifier_cont.get_timer().get_current_time()-tree.start_time+camera_switch_time_length;
				
				root_movement.caculate_time(component_cont,time_distance,camera_switch_time_length,min_box_volume*mount_precision*mount_precision*mount_precision);
								
				for(ct=tree;ct.children!=null;ct=ct.children[0])
					if(ct.children.length<=0)
						break;
				parameter.current_movement_id=ct.movement_tree_id;
				
				set_component_state(component_cont,modifier_cont);
			}
	}
	public String directory_name;
	
	public void flush(component_container component_cont,
			modifier_container modifier_cont,file_writer f,long camera_switch_time_length)
	{
		f.println("version	2015.10");
		if(root_movement!=null){
			boolean old_mount_direction_flag=mount_direction_flag;
			set_direction(component_cont,modifier_cont,true,camera_switch_time_length);
		
			root_movement.flush(f,0,true,0);
			set_direction(component_cont,modifier_cont,old_mount_direction_flag,camera_switch_time_length);
		}
	}
	
	public void init(modifier_container modifier_cont,component_container component_cont,
			String my_move_file_name,long camera_switch_time_length,String file_system_charset)
	{	
		root_movement=null;
		last_edit_move_id=-1;
		
		file_reader f=new file_reader(my_move_file_name,file_system_charset);
		if(f.eof()){
			f.close();
			reset(modifier_cont,component_cont,camera_switch_time_length);
			return;
		}
		String version_str1=f.get_string(),version_str2=f.get_string();
		double file_version_id=Double.parseDouble(version_str2);
		
		directory_name=f.directory_name;
		if(file_version_id==2015.10)
			root_movement=new movement_tree(f);
		else{
			f.close();
			f=new file_reader(my_move_file_name,file_system_charset);
			movement_load_old_file old=new movement_load_old_file(f);
			root_movement=old.combine_to_tree();
		}
		
		f.close();
		
		root_movement.mount_component(component_cont,"");

		reset(modifier_cont,component_cont,camera_switch_time_length);
		
		if(file_version_id!=2015.10){
			String user_file_name=f.directory_name+f.file_name;
			file_writer.file_rename(user_file_name,user_file_name+"."+version_str1+version_str2);
			file_writer fw=new file_writer(user_file_name,file_system_charset);
			flush(component_cont,modifier_cont,fw,camera_switch_time_length);
			fw.close();
		}	
	}
	public void reset(modifier_container modifier_cont,
			component_container component_cont,long camera_switch_time_length)
	{
		if(root_movement==null)
			set_component_state(component_cont,modifier_cont);
		else{
			double mount_precision=parameter.movement_precision;
			root_movement.set_tree_node_id(0);
			double min_box_volume=root_movement.caculate_component_minmal_volume(component_cont);
			root_movement.caculate_time(component_cont,0,0,
					min_box_volume*mount_precision*mount_precision*mount_precision);
			movement_start(modifier_cont,root_movement.movement_tree_id,
					component_cont,true,camera_switch_time_length);
		}
		designed_move=null;
	}
	private file_reader movement_stack[];
	public int movement_stack_pointer;

	private long create_file_id;
	
	public void push_movement(component_container component_cont,
			modifier_container modifier_cont,long camera_switch_time_length,String file_system_charset)
	{
		String file_name=config_parameter.temporary_file_directory+"temp_move_"+Long.toString(create_file_id++)+".txt";
		
		file_writer f=new file_writer(file_name,file_system_charset);
		flush(component_cont,modifier_cont,f,camera_switch_time_length);
		f.close();
		f=null;
		movement_stack[movement_stack_pointer]=new file_reader(file_name,file_system_charset);
		movement_stack_pointer++;
		if(movement_stack_pointer>=movement_stack.length)
			movement_stack_pointer=0;
	}
	public movement_tree pop_movement(modifier_container modifier_cont,
			component_container loader,long camera_switch_time_length,String file_system_charset)
	{
		movement_stack_pointer--;
		if(movement_stack_pointer<0)
			movement_stack_pointer=movement_stack.length-1;
		file_reader f=movement_stack[movement_stack_pointer];
		movement_stack[movement_stack_pointer]=null;
		
		if(f==null)
			return root_movement;
		f.close();
		file_reader ff=new file_reader(f.directory_name+f.file_name,file_system_charset);
		if(ff.eof())
			return root_movement;
		ff.close();
		
		init(modifier_cont,loader,f.directory_name+f.file_name,camera_switch_time_length,file_system_charset);
		
		file_writer.file_delete(f.directory_name+f.file_name);
		
		return root_movement;
	}
	public boolean test_movement_stack()
	{
		int index_id;
		
		index_id =movement_stack_pointer;
		index_id+=movement_stack.length-1;
		index_id%=movement_stack.length;
		
		return (movement_stack[index_id]!=null)?true:false;
	}
	public int last_edit_move_id;

	public movement_manager(engine_kernel ek,movement_configuration_parameter my_config_parameter)
	{
		create_file_id=0;
		
		move_channel_id=new movement_channel_id();
		parameter=new movement_parameter();
		
		mount_direction_flag=true;
		
		movement_stack=new file_reader[100];
		movement_stack_pointer=0;
		for(int i=0;i<movement_stack.length;i++)
			movement_stack[i]=null;
		root_movement=null;
		buffer_movement=null;
		
		config_parameter=my_config_parameter;
		init(ek.modifier_cont[move_channel_id.movement_modifier_id],ek.component_cont,
				config_parameter.movement_file_name,1000,config_parameter.movement_file_charset);
	}
}
