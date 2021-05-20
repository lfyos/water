package driver_movement;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class movement_match
{
	public void destroy()
	{
		match_tag=null;
		source_component_name=null;
		destatination_component_name=null;
	}
	public String match_tag;
	public String source_component_name;
	public int source_body_id,source_face_id;
	
	public String destatination_component_name;
	public int destatination_body_id,destatination_face_id;
	
	private void test_tag()
	{
		if(destatination_component_name==null)
			match_tag="no_target_name";
		else if(destatination_component_name.length()<=0)
			match_tag="no_target_name";
		
		if(source_component_name==null)
			match_tag="no_source_name";
		else if(source_component_name.length()<=0)
			match_tag="no_source_name";
			
		if(match_tag==null)
			match_tag="no_name";
		else if(match_tag.length()<=0)
			match_tag="no_name";
	}
	public void flush(file_writer f)
	{
		test_tag();
		
		f.println(match_tag);
		
		f.println(source_component_name);
		f.println(source_body_id);
		f.println(source_face_id);
		
		f.println(destatination_component_name);
		f.println(destatination_body_id);
		f.println(destatination_face_id);
	}
	public movement_match(file_reader f)
	{
		if((match_tag=f.get_string())==null)
			match_tag="";
		
		if((source_component_name=f.get_string())==null)
			source_component_name="";
		source_body_id					=f.get_int();
		source_face_id					=f.get_int();
		if(source_component_name==null)
			source_component_name="";
		
		if((destatination_component_name=f.get_string())==null)
			destatination_component_name="";
		destatination_body_id			=f.get_int();
		destatination_face_id			=f.get_int();
		
		if(destatination_component_name==null)
			destatination_component_name="";
		
		test_tag();
	}
	
	public movement_match(
		String my_match_tag,		String my_source_component_name,		int my_source_body_id,			int my_source_face_id,
									String my_destatination_component_name,	int my_destatination_body_id,	int my_destatination_face_id)
	{
		match_tag						=my_match_tag;
		source_component_name			=my_source_component_name;
		source_body_id					=my_source_body_id;
		source_face_id					=my_source_face_id;
		
		destatination_component_name	=my_destatination_component_name;
		destatination_body_id			=my_destatination_body_id;
		destatination_face_id			=my_destatination_face_id;
		
		test_tag();
	}
}
