package driver_movement;

import kernel_file_manager.file_reader;
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
	int current_check_pointer;
	
	public movement_match_container()
	{
		match=null;
		current_check_pointer=0;
	}
	public movement_match_container(file_reader f,boolean current_format_flag)
	{
		if(!current_format_flag)
			f.get_string();
		int match_number=f.get_int();
		
		current_check_pointer=0;
		
		if(match_number<=0){
			match=null;
			return;
		}
		match=new movement_match[match_number];
		
		for(int i=0;i<match_number;i++)
			match[i]=new movement_match(f);
		
		current_check_pointer=match.length;
	}
	public void flush(file_writer f,int space_number,boolean flush_match_flag)
	{
		f.set_pace(space_number);
		
		f.print("/*	match number	*/	");	
		if((match==null)||(!flush_match_flag))
			f.println(0);
		else if(match.length<=0)
			f.println(0);
		else{
			f.println(match.length);
			for(int i=0;i<(match.length);i++)
				match[i].flush(f);
		}
	}
	public void reset()
	{
		current_check_pointer=0;
	}
	public boolean is_passed()
	{
		if(match==null)
			return true;
		if(current_check_pointer>=(match.length))
			return true;
		return false;
	}
	public boolean check(String name,int body_id,int face_id)
	{
		if(match==null)
			return true;
		if(current_check_pointer>=(match.length))
			return true;
		if(match[current_check_pointer].destatination_component_name.compareTo(name)==0)
			if(match[current_check_pointer].destatination_body_id==body_id)
				if(match[current_check_pointer].destatination_face_id==face_id){
					current_check_pointer++;
					return is_passed();
				}
		return false;
	}
}
