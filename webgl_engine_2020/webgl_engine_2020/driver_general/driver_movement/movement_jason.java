package driver_movement;

import kernel_common_class.common_writer;

public class movement_jason 
{
	private static String replace(String str)
	{
		if(str==null)
			return null;
		else
			return str.replace('\\','/').replace('\r','\t').replace('\n','\t').replace('\"',' ');
	}
	
	private static void create_jason(movement_tree t,common_writer cw,String space,String follow_string)
	{
		String child_space=space+"\t";
		cw.println(space,"{");

		cw.println(child_space+"\"movement_tree_id\"	:	",	t.movement_tree_id			+",");
		cw.println(child_space+"\"node_name\"		:	\"",	replace(t.node_name)		+"\",");
		cw.println(child_space+"\"description\"		:	\"",	replace(t.description)		+"\",");
		cw.println(child_space+"\"sound_file_name\"	:	\"",	replace(t.sound_file_name)	+"\",");
		cw.println(child_space+"\"sequence_flag\"		:	",	t.sequence_flag?"true,":"false,");
		
		cw.println(child_space+"\"time_length\"		:	",		(t.terminate_time-t.start_time)+",");
		
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
		}else{
			cw.println(child_space+"\"component_name\"	:	\"",	replace(t.move.moved_component_name)+"\",");
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
		}
		
		cw.println(child_space+"\"child\"			:	[");
		if(t.children!=null)
			for(int i=0,ni=t.children.length;i<ni;i++)
				create_jason(t.children[i],cw,child_space,(i==(ni-1))?"":",");
		cw.println(child_space,"]");

		cw.println(space,"}"+follow_string);
	}
	public static void create_jason_with_current_movement_tree_id(
			int current_movement_tree_id,movement_tree t,common_writer cw)
	{
		cw.println("{");
		cw.println("	\"current_movement_tree_id\"	:	",current_movement_tree_id+",");
		cw.print  ("	\"movement_tree\"			:");
		if(t==null)
			cw.println("	null");
		else {
			cw.println();
			create_jason(t,cw,"\t","");
		}
		cw.println("}");
	}
}