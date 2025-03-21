package driver_movement;

import kernel_common_class.common_writer;
import kernel_common_class.jason_string;
import kernel_file_manager.file_reader;

public class movement_jason 
{
	public static void set_current_movement_flag(movement_tree t,
			long my_current_movement_tree_id,boolean parent_current_movement_flag)
	{
		if(t==null)
			return;
		t.current_movement_flag=(t.movement_tree_id==my_current_movement_tree_id)|parent_current_movement_flag;
		if(t.children==null)
			return;
		if(t.children.length<=0)
			return;
		for(int i=0,ni=t.children.length;i<ni;i++)
			set_current_movement_flag(t.children[i],my_current_movement_tree_id,t.current_movement_flag);
		for(int i=0,ni=t.children.length;i<ni;i++)
			t.current_movement_flag|=t.children[i].current_movement_flag;
		return;
	}
	public static void create_tree_node_jason(movement_tree t,
			common_writer cw,String child_space,String follow_str,long switch_time_length)
	{
		cw.println(child_space+"\"movement_tree_id\"	:	",	t.movement_tree_id								+",");
		
		cw.println(child_space+"\"time_length\"		:	",		(t.terminate_time-t.start_time+switch_time_length)+",");
		cw.println(child_space+"\"mount_only_time_length\":	",	t.mount_only_time_length+",");
		
		cw.println(child_space+"\"node_name\"		:	",		jason_string.change_string(t.node_name)			+",");
		cw.println(child_space+"\"description\"		:	",		jason_string.change_string(t.description)		+",");
		cw.println(child_space+"\"sound_file_name\"	:	",
			jason_string.change_string(file_reader.separator(t.sound_file_name))	+",");
		cw.println(child_space+"\"sequence_flag\"		:	",	t.sequence_flag?"true,":"false,");
		cw.println(child_space+"\"current_movement_flag\" :	",	t.current_movement_flag?"true,":"false,");

		cw.print  (child_space+"\"direction\"		:	[");
		if(t.direction!=null){
			double p[]=t.direction.get_location_data();
			for(int i=0,ni=p.length;i<ni;i++)
				cw.print((i==0)?"":",",p[i]);
		}
		cw.println("],");
		
		cw.println(child_space+"\"scale_type\"		:	",		t.scale_type				+",");
		cw.println(child_space+"\"scale_value\"		:	",		t.scale_value				+",");
		
		if(t.move==null){
			cw.println(child_space+"\"component_name\"	:	\"\",");
			cw.println(child_space+"\"component_id\"		:	-1,");
			cw.println(child_space+"\"follow_component_name\"	:	[],");
			cw.println(child_space+"\"follow_component_id\"	:	[],");
			
			cw.println(child_space+"\"start_state_flag\"	:	",		"false,");
			cw.println(child_space+"\"terminate_state_flag\"	:	",	"false,");
			cw.println(child_space+"\"movement_time_length\"	:	[],");
		}else{
			cw.println(child_space+"\"component_name\"	:	",
					jason_string.change_string(t.move.moved_component_name)+",");
			cw.println(child_space+"\"component_id\"		:	",	t.move.moved_component_id+",");
			
			cw.print  (child_space+"\"follow_component_name\"	:	[");
			if(t.move.follow_component_name!=null)
				for(int i=0,ni=t.move.follow_component_name.length;i<ni;i++)
					cw.print((i==0)?"\"":",\"",t.move.follow_component_name[i]+"\"");
			cw.println("],");
			
			cw.print  (child_space+"\"follow_component_id\"	:	[");
			if(t.move.follow_component_id!=null)
				for(int i=0,ni=t.move.follow_component_id.length;i<ni;i++)
					cw.print((i==0)?"":",",t.move.follow_component_id[i]);
			cw.println("],");
			
			cw.println(child_space+"\"start_state_flag\"	:	",		t.move.start_state_flag?"true,":"false,");
			cw.println(child_space+"\"terminate_state_flag\"	:	",	t.move.terminate_state_flag?"true,":"false,");
			
			cw.print  (child_space+"\"movement_time_length\"	:	[");
			if(t.move.movement!=null)
				for(int i=0,ni=t.move.movement.length;i<ni;i++)
					cw.print((i==0)?"":",",t.move.movement[i].time_length);
			cw.println("],");
		}
		
		cw.print  (child_space+"\"match\"			:	");
		if(t.match==null) {
			cw.println("[]",follow_str);
			return;
		}
		if(t.match.match==null) {
			cw.println("[]",follow_str);
			return;
		}
		cw.println("[");
		String pre_str="";
		for(int i=0,ni=t.match.match.length;i<ni;i++) {
			switch((t.match.match[i].match_type==null)?"no_match":t.match.match[i].match_type) {
			case "component_part_selection":
				cw.println(pre_str);pre_str=",";
				cw.println(child_space+"			{");
				cw.println(child_space+"				\"match_type\"					:	\"component_part_selection\"");
				cw.print  (child_space+"			}");
				break;
			case "component_face_match":
				cw.println(pre_str);pre_str=",";
				cw.println(child_space+"			{");
				cw.println(child_space+"				\"match_type\"					:	\"component_face_match\",");
				
				cw.println(child_space+"				\"source_component_name\"		:	\"",
						t.match.match[i].source_component_name+"\",");
				cw.println(child_space+"				\"source_body_id\"				:	",
						t.match.match[i].source_body_id+",");
				cw.println(child_space+"				\"source_face_id\"				:	",
						t.match.match[i].source_face_id+",");
				
				cw.println(child_space+"				\"destatination_component_name\":	\"",
						t.match.match[i].destatination_component_name+"\",");
				cw.println(child_space+"				\"destatination_body_id\"		:	",
						t.match.match[i].destatination_body_id+",");
				cw.println(child_space+"				\"destatination_face_id\"		:	",
						t.match.match[i].destatination_face_id);
				cw.print  (child_space+"	}");
				break;
			default:
				break;
			}
		}
		cw.println();
		cw.println(child_space+"]",follow_str);
		return;
	}
	private static void create_jason(movement_tree t,common_writer cw,
			String space,String follow_string,long switch_time_length)
	{
		String child_space=space+"\t\t";
		cw.println(space,"{");
		
		create_tree_node_jason(t,cw,space+"\t",",",switch_time_length);
		
		cw.println(space+"\t\"children\"		:	[");
		if(t.children!=null)
			for(int i=0,ni=t.children.length;i<ni;i++)
				create_jason(t.children[i],cw,child_space,(i==(ni-1))?"":",",switch_time_length);
		cw.println(space,"\t]");

		cw.println(space,"}"+follow_string);
	}
	public static void create_jason(long current_movement_tree_id,
			movement_tree t,common_writer cw,long switch_time_length)
	{
		if(t==null)
			cw.println("null");
		else {
			set_current_movement_flag(t,current_movement_tree_id,false);
			create_jason(t,cw,"","",switch_time_length);
			cw.println();
		}
	}
}