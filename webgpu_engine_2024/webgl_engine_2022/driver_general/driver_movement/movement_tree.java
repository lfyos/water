package driver_movement;

import kernel_common_class.common_reader;
import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_container;
import kernel_driver.modifier_container;
import kernel_file_manager.file_writer;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_file_manager.file_reader;

public class movement_tree {
	
	public void destroy()
	{
		node_name=null;
		description=null;
		sound_file_name=null;
		direction=null;
		
		if(move!=null) {
			move.destroy();
			move=null;
		}
		if(match!=null) {
			match.destroy();
			match=null;
		}
		if(children!=null) {
			for(int i=0,ni=children.length;i<ni;i++)
				if(children[i]!=null) {
					children[i].destroy();
					children[i]=null;
				}
			children=null;
		}
	}
	public long movement_tree_id;
	public movement_tree search_movement(long my_movement_tree_id)
	{
		if(movement_tree_id==my_movement_tree_id)
			return this;
		if(children!=null)
			for(int i=0,ni=children.length;i<ni;i++) {
				movement_tree t;
				if((t=children[i].search_movement(my_movement_tree_id))!=null)
					return t;
			}
		return null;
	}
	public String node_name,description,sound_file_name;
	
	public boolean sequence_flag,current_movement_flag;
	
	public movement_item_container	move;
	public movement_match_container	match;

	public movement_tree children[];
	
	public long start_time,terminate_time,mount_only_time_length;

	public void register_modifier(movement_suspend suspend,
			movement_channel_id move_channel_id,int location_component_id,
			component_container component_cont,movement_parameter parameter,
			modifier_container modifier_cont,movement_switch_camera_modifier swcm,
			String directory_name,String sound_pre_string,
			long camera_switch_time,location parent_direction,
			int  parent_scale_type,double parent_scale_value,boolean direction_flag)
	{
		int my_scale_type;
		double my_scale_value;
		switch(scale_type){
		case 1://起点相机比例
		case 2://终点相机比例   
		case 3://起点终点相机比例  
			my_scale_type=scale_type;
			my_scale_value=scale_value;
			break;
		case 0://上层相机比例
		default:
			my_scale_type=parent_scale_type;
			my_scale_value=parent_scale_value;
			break;
		}
		location my_direction=(direction!=null)?direction:parent_direction;
		if(children!=null)
			if(children.length>0){
				for(int i=0,ni=children.length;i<ni;i++)
					children[i].register_modifier(
						suspend,move_channel_id,location_component_id,
						component_cont,parameter,modifier_cont,swcm,directory_name,
						sound_pre_string,camera_switch_time,
						my_direction,my_scale_type,my_scale_value,direction_flag);	
				return;
			}
		if(move==null)
			return;
		if(move.movement==null)
			return;
		if(move.movement.length<=0)
			return;
		
		long my_current_time=modifier_cont.get_timer().get_current_time();
		if(terminate_time<=my_current_time)
			return;
		component moved_component;
		if((moved_component=component_cont.get_component(move.moved_component_id))==null)
			return;
		for(int i=0,ni=move.movement.length;i<ni;i++) {
			movement_item p=move.movement[i];
			modifier_cont.add_modifier(new movement_move_modifier(	
				move.start_state_flag,	move.terminate_state_flag,
				suspend,				moved_component,		location_component_id,
				move_channel_id.hide_parameter_channel_id,		move_channel_id.display_parameter_channel_id,
				p.start_time,			p.start_parameter,		p.start_location,
				p.terminate_time,		p.terminate_parameter,	p.terminate_location,
				move.follow_component_id,						move.follow_component_location));
		}	
		if(start_time>=terminate_time)
			return;
		if((start_time-camera_switch_time)<=my_current_time)
			return;
		
		location my_start_location=move.movement[0].start_location;
		location my_terminate_location=move.movement[move.movement.length-1].terminate_location;
		
		switch(my_scale_type){
		case 1://起点相机比例
			if(direction_flag)
				my_terminate_location=null;
			else
				my_start_location=null;
			break;
		case 2://终点相机比例   
			if(direction_flag)
				my_start_location=null;
			else
				my_terminate_location=null;
			break;
		case 3://起点终点相机比例  
		case 0://上层相机比例
		default:
			break;
		}
		movement_focus_modifier fm=new movement_focus_modifier(
				parameter,movement_tree_id,suspend,match,
				moved_component.component_id,move.follow_component_id,move.follow_component_location,
				start_time-camera_switch_time,swcm,my_scale_value,
				my_direction,my_start_location,my_terminate_location,node_name,description,
				file_reader.separator(directory_name+sound_pre_string+sound_file_name));
		modifier_cont.add_modifier(fm);
		return;
	}
	
	public void reset_component_state(component_container component_cont,
			int display_parameter_channel_id[],int hide_parameter_channel_id[])
	{
		if(children!=null){
			for(int i=0;i<(children.length);i++)
				children[i].reset_component_state(
					component_cont,display_parameter_channel_id,hide_parameter_channel_id);		
			return;
		}
		if(move==null)
			return;
		component moved_component;
		if((moved_component=component_cont.get_component(move.moved_component_id))==null)
			return;
		moved_component.set_component_move_location(new location(),component_cont);
		moved_component.modify_display_flag(display_parameter_channel_id,true,component_cont);
		moved_component.modify_display_flag(hide_parameter_channel_id,true,component_cont);
		return;
	}
	public void sequence_set_component_state(
			int display_parameter_channel_id[],int hide_parameter_channel_id[],
			component_container component_cont,long current_time,boolean start_terminate_flag)
	{
		if(children!=null){
			for(int i=0,j=children.length-1,n=children.length;i<n;i++,j--)
				children[start_terminate_flag?i:j].sequence_set_component_state(
						display_parameter_channel_id,hide_parameter_channel_id,
						component_cont,current_time,start_terminate_flag);
			return ;				
		}
		if(move==null)
			return ;
		if(move.movement==null)
			return ;
		
		int move_id=move.movement.length-1;
		if(move_id<0)
			return ;
		component moved_component,follow_component;
		if((moved_component=component_cont.get_component(move.moved_component_id))==null)
			return;
		
		location loca;
		boolean display_flag,hide_flag;
		
		if(move.movement[move_id].terminate_time<current_time) {
			if(!start_terminate_flag)
				return;
			display_flag=move.terminate_state_flag?false:true;
			hide_flag=move.start_state_flag?false:true;
			
			loca=display_flag?move.movement[move_id].terminate_location:new location();
			
		}else if(current_time<move.movement[0].start_time) {
			if(start_terminate_flag)
				return;
			display_flag=move.start_state_flag?false:true;
			hide_flag=move.terminate_state_flag?false:true;
			
			loca=display_flag?move.movement[0].start_location:new location();
		}else{
			loca=move.movement[move_id].terminate_location;
			display_flag=true;
			hide_flag=true;
		}
		moved_component.set_component_move_location(display_flag?loca:new location(),component_cont);
		moved_component.modify_display_flag(display_parameter_channel_id,display_flag,component_cont);
		moved_component.modify_display_flag(hide_parameter_channel_id,hide_flag,component_cont);
		
		if(move.follow_component_id==null)
			return;
		
		location main_loca=moved_component.parent_and_relative_location;
		main_loca=main_loca.multiply(moved_component.move_location);
		
		for(int i=0,ni=move.follow_component_id.length;i<ni;i++)
			if((follow_component=component_cont.get_component(move.follow_component_id[i]))!=null){
				loca=main_loca.multiply(move.follow_component_location[i]);
				loca=follow_component.caculate_negative_parent_and_relative_location().multiply(loca);
				follow_component.uniparameter.cacaulate_location_flag=false;
				follow_component.set_component_move_location(loca,component_cont);
				
				follow_component.modify_display_flag(display_parameter_channel_id,display_flag,component_cont);
				follow_component.modify_display_flag(hide_parameter_channel_id,hide_flag,component_cont);
			}
	}
	public double caculate_component_minmal_volume(component_container component_cont)
	{
		int i,child_number;
		double my_min_box_volume=-1.0,new_box_volume;
		component comp;
		box my_box;
		
		if(children!=null){
			for(i=0,child_number=children.length;i<child_number;i++)
				if((my_min_box_volume=children[i].caculate_component_minmal_volume(component_cont))>const_value.min_value)
					for(;i<child_number;i++)
						if((new_box_volume=children[i].caculate_component_minmal_volume(component_cont))>const_value.min_value)
							if(new_box_volume<my_min_box_volume)
								my_min_box_volume=new_box_volume;
			return my_min_box_volume;
		}
		if(move==null)
			return -1.0;
		if((comp=component_cont.get_component(move.moved_component_id))==null)
			return -1.0;
		if((my_box=comp.get_component_box(true))==null)
			return -1.0;
		if((my_min_box_volume=my_box.volume_for_compare())<(const_value.min_value2*const_value.min_value))
			return -1.0;
		return my_min_box_volume;
	}
	
	public long caculate_time(component_container component_cont,
			long my_start_time,long camera_time_length,double min_volume)
	{	
		if(children!=null)
			if(children.length>0){
				start_time=my_start_time;
				terminate_time=my_start_time;
				mount_only_time_length=0;
				if(sequence_flag)
					for(int i=0,ni=children.length;i<ni;i++){
						terminate_time=children[i].caculate_time(
								component_cont,terminate_time,camera_time_length,min_volume);
						mount_only_time_length+=children[i].mount_only_time_length;
					}
				else
					for(int i=0,ni=children.length;i<ni;i++){
						long children_time=children[i].caculate_time(
								component_cont,start_time,camera_time_length,min_volume);
						if(children_time>terminate_time)
							terminate_time=children_time;
						if(mount_only_time_length<children[i].mount_only_time_length)
							mount_only_time_length=children[i].mount_only_time_length;
					}
				start_time=children[0].start_time;
				return terminate_time;
			}
		start_time=my_start_time;
		terminate_time=my_start_time;
		mount_only_time_length=0;
		if(move==null)
			return terminate_time;
		if(move.movement==null)
			return terminate_time;
		if(move.movement.length<=0)
			return terminate_time;
		if((component_cont.get_component(move.moved_component_id))==null)
			return terminate_time;
		start_time=my_start_time+camera_time_length;
		double comp_min_volume=caculate_component_minmal_volume(component_cont);
		if((comp_min_volume>=min_volume)||(comp_min_volume<=(const_value.min_value2*const_value.min_value))){
			terminate_time=move.caculate_time(component_cont,start_time,false);
			mount_only_time_length=terminate_time-start_time;
			return terminate_time;
		}else{
			terminate_time=move.caculate_time(component_cont,start_time,true);
			return my_start_time;
		}
	}
	public void mount_component(component_container component_cont,String location_string)
	{
		if(move!=null)
			move.mount_component(component_cont,location_string);
		if(children!=null)
			for(int i=0,ni=children.length;i<ni;i++)
				children[i].mount_component(component_cont,location_string+"        "+node_name);
	}
	public void reverse()
	{
		switch(scale_type){
		case 1://起点相机比例
			scale_type=2;
			break;
		case 2://终点相机比例   
			scale_type=1;
			break;
		case 3://起点终点相机比例  
			break;		
		case 0://上层相机比例
		default:
			scale_type=0;
			break;
		}
		
		if(children==null)
			move.reverse();
		else{
			for(int begin_pointer=0,end_pointer=children.length-1;begin_pointer<end_pointer;begin_pointer++,end_pointer--){
				movement_tree p=children[begin_pointer],q=children[end_pointer];
				children[begin_pointer]=q;
				children[end_pointer]=p;
			}
			for(int i=0,ni=children.length;i<ni;i++)
				children[i].reverse();
		}
	}
	public boolean modify_component_name(component_container component_cont,String old_name,String new_name)
	{
		int i,n;
		
		if(children!=null){
			for(i=0,n=0;i<children.length;i++)
				if(children[i].modify_component_name(component_cont,old_name,new_name))
					children[n++]=children[i];
			if(n<=0)
				return false;
			if(children.length==n)
				return true;
			movement_tree tmp[]=new movement_tree[n];
			for(i=0;i<n;i++)
				tmp[i]=children[i];
			children=tmp;
			return true;
		}
		
		if(move==null)
			return false;
		if(move.moved_component_name.length()<old_name.length())
			return false;
	
		move.moved_component_name=move.moved_component_name.substring(old_name.length());
		move.moved_component_name=new_name+(move.moved_component_name);
		component moved_component;
		if((moved_component=component_cont.search_component(move.moved_component_name))==null)
			return false;
		move.moved_component_id=moved_component.component_id;
		
		int my_follow_number=0;
		if(move.follow_component_id!=null)
			for(i=0,n=move.follow_component_id.length;i<n;i++){
				move.follow_component_name[i]=move.follow_component_name[i].substring(old_name.length());
				move.follow_component_name[i]=new_name+(move.follow_component_name[i]);
				component follow_component;
				if((follow_component=component_cont.search_component(move.follow_component_name[i]))!=null){
					move.follow_component_name		[my_follow_number]	=follow_component.component_name;
					move.follow_component_id		[my_follow_number]	=follow_component.component_id;
					move.follow_component_location	[my_follow_number++]=move.follow_component_location[i];
				}
			}
		if(my_follow_number<=0){
			move.follow_component_id		=null;
			move.follow_component_name		=null;
			move.follow_component_location	=null;
		}else if(move.follow_component_id.length>my_follow_number){
			String follow_component_name[]		=move.follow_component_name;
			int follow_component_id[]			=move.follow_component_id;
			location follow_component_location[]=move.follow_component_location;
			move.follow_component_name		=new String[my_follow_number];
			move.follow_component_id		=new int[my_follow_number];
			move.follow_component_location	=new location[my_follow_number];
			for(i=0;i<my_follow_number;i++){
				move.follow_component_name[i]		=follow_component_name[i];
				move.follow_component_id[i]			=follow_component_id[i];
				move.follow_component_location[i]	=follow_component_location[i];
			}	
		}
		return true;
	}
	public movement_tree(movement_tree_id_creator id_creator)
	{	
		movement_tree_id=id_creator.create_movement_tree_id();
		
		node_name		="movement";
		description		="description";
		sound_file_name	="sound.mp3";
		scale_type		=0;
		
		sequence_flag	=true;
		current_movement_flag=false;
		
		move=null;
		match=null;
		children=null;
		direction=null;
		
		scale_value=1.0;
	}
	public location direction;
	public int scale_type;
	public double scale_value;
	
	public void flush(file_writer f,int space_number,boolean flush_match_flag)
	{
		if(node_name==null)
			node_name="no_name";
		else if(node_name.compareTo("")<=0)
			node_name="no_name";
		
		if(sound_file_name==null)
			sound_file_name="no_sound_file";
		else if(sound_file_name.compareTo("")<=0)
			sound_file_name="no_sound_file";
		
		if(description==null)
			description="no_description";
		else if(description.compareTo("")<=0)
			description="no_description";
		
		f.set_pace(space_number);
		f.print("/*	name				*/	");		f.println(node_name);
		f.print("/*	audio				*/	");		f.println(file_reader.separator(sound_file_name));
		f.print("/*	description			*/	");		f.println(description);
		
		f.print("/*	sequence/parallel	*/	");	f.println(((children==null)||sequence_flag)?"sequence":"parallel");
		
		f.print("/*	direction			*/	");
		
		if(direction==null)
			f.println("no_direction");
		else
			direction.get_location_data(f);
		
		f.print("/*	camera scale		*/	");
		switch(scale_type){
		case 1:
			f.println("start	",		scale_value);
			break;
		case 2:
			f.println("end	",			scale_value);
			break;
		case 3:
			f.println("start_end	",	scale_value);
			break;		
		case 0:
		default:
			f.println("upper	",		scale_value);
			break;
		}
		
		f.print("/*	children number		*/	");
		if(children==null){
			f.println(0);
			move.flush(f,space_number);
			match.flush(f,space_number,flush_match_flag);
		}else{
			f.println(children.length);	
			for(int i=0;i<(children.length);i++)
				 children[i].flush(f,space_number+8,flush_match_flag);
		}
	}
	public movement_tree(common_reader reader,movement_tree_id_creator id_creator)
	{	
		movement_tree_id=id_creator.create_movement_tree_id();
		
		String str;
		move=null;
		children=null;
		match=null;
		direction=null;
		scale_value=1.0;
		current_movement_flag=false;
		
		if((node_name=reader.get_string())==null)
			node_name="";
		if((sound_file_name=reader.get_string())==null)
			sound_file_name="";
		else
			sound_file_name=file_reader.separator(sound_file_name);
	
		if((description=reader.get_string())==null)
			description="";
		if((str=reader.get_string())==null)
			str="";
		sequence_flag=(str.compareTo("sequence")==0)?true:false;

		reader.mark_start();
		if((str=reader.get_string())==null)
			str="";
		if(str.compareTo("no_direction")==0)
			reader.mark_terminate(false);
		else{
			reader.mark_terminate(true);
			direction=new location(reader);
		}
	
		if((str=reader.get_string())==null)
			scale_type=0;
		else if(str.compareTo("start")==0)
			scale_type=1;
		else if(str.compareTo("end")==0)
			scale_type=2;
		else if(str.compareTo("start_end")==0)
			scale_type=3;
		else if(str.compareTo("upper")==0)
			scale_type=0;
		else
			scale_type=0;
				
		if((scale_value=reader.get_double())<=const_value.min_value)
			scale_value=1.0;
		
		int i,child_number=Integer.decode(reader.get_string());
		
		if(child_number<=0){
			String component_name,follow_component_name[]=null;
			location follow_component_location[]=null;
			reader.get_string();
			if((component_name=reader.get_string())==null)
				component_name="";
			for(String my_follow_component_name;;){
				if((my_follow_component_name=reader.get_string())==null)
					break;
				if(my_follow_component_name.compareTo("component_end")==0)
					break;
				if(follow_component_name==null){
					follow_component_name		=new String[1];
					follow_component_name[0]	=my_follow_component_name;
					follow_component_location	=new location[]{new location(reader)};
				}else{
					String bak_name[]			=follow_component_name;
					location bak_location[]		=follow_component_location;
					follow_component_name		=new String[bak_name.length+1];
					follow_component_location	=new location[bak_location.length+1];
					for(int j=0,nj=bak_name.length;j<nj;j++){
						follow_component_name[j]	=bak_name[j];
						follow_component_location[j]=bak_location[j];
					}
					follow_component_name[follow_component_name.length-1]		=my_follow_component_name;
					follow_component_location[follow_component_location.length-1]=new location(reader);
				}
			}
			move=new movement_item_container(component_name,
					follow_component_name,follow_component_location,reader,true);
			match=new movement_match_container(reader);
			sequence_flag=true;
		}else{
			for(i=0,children=new movement_tree[child_number];i<child_number;i++)
				children[i]=new movement_tree(reader,id_creator);
		}
	}
}
