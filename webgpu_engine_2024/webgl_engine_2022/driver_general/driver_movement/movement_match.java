package driver_movement;

import kernel_common_class.common_reader;
import kernel_common_class.common_writer;

public class movement_match
{
	public void destroy()
	{
		match_type=null;
		
		source_component_name=null;
		source_body_id=-1;
		source_face_id=-1;
		
		destatination_component_name=null;
		destatination_body_id=-1;
		destatination_face_id=-1;
	}
	public String match_type;
	
	public String source_component_name;
	public int source_body_id,source_face_id;
	
	public String destatination_component_name;
	public int destatination_body_id,destatination_face_id;
	
	public void flush(common_writer writer)
	{
		switch((match_type==null)?"no_match":match_type) {
		case "component_part_selection":
			writer.println(match_type);
			break;
		case "component_face_match":
			writer.println(match_type);
			
			writer.println(source_component_name);
			writer.println(source_body_id);
			writer.println(source_face_id);
			
			writer.println(destatination_component_name);
			writer.println(destatination_body_id);
			writer.println(destatination_face_id);
			break;
		default:
			writer.println("no_match		no_component	0	0	no_component	0	0");
			break;
		}
	}
	public boolean check_match(String name,int body_id,int face_id)
	{
		switch(match_type) {
		case "component_face_match":
			if(destatination_body_id!=body_id)
				return false;
			if(destatination_face_id!=face_id)
				return false;
			if(destatination_component_name.compareTo(name)==0)
				return false;
			return true;
		case "component_part_selection":
		default:
			return true;
		}
	}
	public movement_match()
	{
		match_type="component_part_selection";
		source_component_name="no_source_component_name";
		source_body_id=-1;
		source_face_id=-1;
		
		destatination_component_name="no_destatination_component_name";
		destatination_body_id=-1;
		destatination_face_id=-1;
	}
	public movement_match(
			String my_source_component_name,
			int my_source_body_id,int my_source_face_id,
			
			String my_destatination_component_name,
			int my_destatination_body_id,int my_destatination_face_id)
	{
		match_type="component_face_match";
		source_component_name=my_source_component_name;
		source_body_id=my_source_body_id;
		source_face_id=my_source_face_id;
		
		destatination_component_name=my_destatination_component_name;
		destatination_body_id=my_destatination_body_id;
		destatination_face_id=my_destatination_face_id;
	}
	public movement_match(common_reader reader)
	{
		if((match_type=reader.get_string())==null)
			match_type="no_match";
		switch(match_type) {
		case "component_part_selection":
			source_component_name=null;
			source_body_id=-1;
			source_face_id=-1;
			
			destatination_component_name=null;
			destatination_body_id=-1;
			destatination_face_id=-1;
			return;
		default:
			match_type="no_match";
		case "component_face_match":
			if((source_component_name=reader.get_string())==null)
				source_component_name="";
			source_body_id					=reader.get_int();
			source_face_id					=reader.get_int();
			
			if((destatination_component_name=reader.get_string())==null)
				destatination_component_name="";
			destatination_body_id			=reader.get_int();
			destatination_face_id			=reader.get_int();
			return;
		}
	}
}
