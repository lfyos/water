package driver_movement;

import kernel_common_class.common_reader;
import kernel_file_manager.file_writer;

public class movement_match_container 
{
	public void destroy()
	{
		if(match!=null) {
			for(int i=0,ni=match.length;i<ni;i++)
				if(match[i]!=null) {
					match[i].destroy();
					match[i]=null;
				}
			match=null;
		}
	}
	public movement_match match[];
	
	public void reset()
	{
		destroy();
		match=new movement_match[0];
	}
	public void add_or_delete_component_part_selection(boolean add_or_clear_flag)
	{
		if(match==null) 
			match=new movement_match[] {};
		int number=0;
		for(int i=0,ni=match.length;i<ni;i++) {
			switch(match[i].match_type){
			case "component_part_selection":
				match[i].destroy();
				match[i]=null;
				break;
			default:
				movement_match p=match[i];
				match[i]=null;
				match[number++]=p;
				break;
			}
		}
		movement_match bak[]=match;
		match=new movement_match[number+(add_or_clear_flag?1:0)];
		for(int i=0;i<number;i++)
			match[i]=bak[i];
		if(add_or_clear_flag)
			match[match.length-1]=new movement_match();
		return;
	}
	public void clear_component_face_match()
	{
		if(match==null) {
			match=new movement_match[] {};
			return;
		}
		int number=0;
		for(int i=0,ni=match.length;i<ni;i++)
			switch(match[i].match_type){
			case "component_face_match":
				match[i].destroy();
				match[i]=null;
				break;
			default:
				movement_match p=match[i];
				match[i]=null;
				match[number++]=p;
				break;
			}
		movement_match bak[]=match;
		match=new movement_match[number];
		for(int i=0;i<number;i++)
			match[i]=bak[i];
	}
	public void add_component_face_match(
			String source_component_name,		int source_body_id,			int source_face_id,
			String destatination_component_name,int destatination_body_id,	int destatination_face_id)
	{
		for(int i=0,ni=match.length;i<ni;i++)
			if(match[i].source_body_id==source_body_id)
				if(match[i].source_face_id==source_face_id)
					if(match[i].destatination_body_id==destatination_body_id)
						if(match[i].destatination_face_id==destatination_face_id)
							if(match[i].source_component_name.compareTo(source_component_name)==0)
								if(match[i].destatination_component_name.compareTo(destatination_component_name)==0)
									return;
		movement_match bak[]=match;
		match=new movement_match[match.length+1];
		for(int i=0,ni=bak.length;i<ni;i++)
			match[i]=bak[i];
		match[bak.length]=new movement_match(
				source_component_name,source_body_id,source_face_id,
				destatination_component_name,destatination_body_id,destatination_face_id);
	}
	public movement_match_container()
	{
		match=new movement_match[] {};
	}
	public movement_match_container(common_reader reader)
	{
		int number;
		if((number=reader.get_int())<0)
			number=0;
		movement_match bak[]=new movement_match[number];
		boolean component_part_selection_flag=false;
		number=0;
		for(int i=0,ni=bak.length;i<ni;i++) {
			switch((bak[number]=new movement_match(reader)).match_type) {
			case "component_part_selection":
				if(component_part_selection_flag)
					break;
				component_part_selection_flag=true;
			case "component_face_match":
				number++;
				break;
			}
		}
		match=new movement_match[number];
		for(int i=0;i<number;i++)
			match[i]=bak[i];
	}
	public void flush(file_writer f,int space_number,boolean flush_match_flag)
	{
		f.set_pace(space_number);
		f.print("/*	match number	*/	");	
		if((match==null)||(!flush_match_flag)) {
			f.println(0);
			return;
		}
		int number=0;
		boolean component_part_selection_flag=false;
		for(int i=0,ni=match.length;i<ni;i++)
			switch(match[i].match_type) {
			case "component_part_selection":
				if(component_part_selection_flag)
					break;
				component_part_selection_flag=true;
			case "component_face_match":
				number++;
				break;
			}
		f.println(number);
		component_part_selection_flag=false;
		for(int i=0,ni=match.length;i<ni;i++)
			switch(match[i].match_type) {
			case "component_part_selection":
				if(component_part_selection_flag)
					break;
				component_part_selection_flag=true;
			case "component_face_match":
				match[i].flush(f);
				break;
			}
	}
}
