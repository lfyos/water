package driver_movement;

import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_component.component_container;
import kernel_file_manager.file_writer;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_common_class.common_reader;

public class movement_item_container
{
	public String moved_component_name,follow_component_name[];
	public location follow_component_location[];
	public int moved_component_id,follow_component_id[];
	
	public boolean start_state_flag,terminate_state_flag;
		
	public movement_item  movement[];
	public box start_box,terminate_box;
	
	public void destroy()
	{
		moved_component_name=null;
		
		if(follow_component_name!=null) {
			for(int i=0,ni=follow_component_name.length;i<ni;i++)
				follow_component_name[i]=null;
			follow_component_name=null;
		}
		if(follow_component_location!=null) {
			for(int i=0,ni=follow_component_location.length;i<ni;i++)
				follow_component_location[i]=null;
			follow_component_location=null;
		}
		
		follow_component_id=null;
		
		if(movement!=null) {
			for(int i=0,ni=movement.length;i<ni;i++)
				if(movement[i]!=null) {
					movement[i].destroy();
					movement[i]=null;
				}
			movement=null;
		}
		start_box=null;
		terminate_box=null;
	}
	
	
	public void reverse()
	{
		if(movement==null)
			return;
		for(int begin_pointer=0,end_pointer=movement.length-1;begin_pointer<end_pointer;begin_pointer++,end_pointer--){
			movement_item p=movement[begin_pointer];
			movement_item q=movement[end_pointer];
			movement[begin_pointer]=q;
			movement[end_pointer]=p;
		}
		for(int i=0,ni=movement.length;i<ni;i++)
			movement[i].reverse();
		
		boolean bak_state_flag=start_state_flag;
		start_state_flag=terminate_state_flag;
		terminate_state_flag=bak_state_flag;
	}
	public long caculate_time(component_container component_cont,long new_start_time,boolean discard_flag)
	{
		component moved_component=component_cont.get_component(moved_component_id);
		
		if((movement==null)||(moved_component==null))
			return new_start_time;
		for(int i=0,ni=movement.length;i<ni;i++)
			if(discard_flag){
				movement[i].start_time		=new_start_time;
				movement[i].terminate_time	=new_start_time;
			}else{
				if(i==0)
					movement[i].start_time	=new_start_time;
				else
					movement[i].start_time	=movement[i-1].terminate_time;
				movement[i].terminate_time	=movement[i].start_time+(long)(movement[i].time_length);
			}
		return movement[movement.length-1].terminate_time;
	}
	public void mount_component(component_container component_cont,String location_string)
	{
		moved_component_id=-1;
		component moved_component=component_cont.search_component(moved_component_name);
		if(moved_component!=null)
			moved_component_id=moved_component.component_id;
		else{
			debug_information.println();
			debug_information.print  ("Find a main movement item that has no corresponding component:    ",moved_component_name);
			debug_information.println(",location:    ",location_string);
		}
		if(follow_component_name==null)
			follow_component_id=null;
		else if(follow_component_name.length<=0)
			follow_component_id=null;
		else{
			follow_component_id=new int[follow_component_name.length];
			for(int i=0,ni=follow_component_id.length;i<ni;i++)
				if((moved_component=component_cont.search_component(follow_component_name[i]))!=null)
					follow_component_id[i]=moved_component.component_id;
				else{
					follow_component_id[i]=-1;
					debug_information.println();
					debug_information.print  ("Find an accessorial movement item that has no corresponding component:    ",follow_component_name[i]);
					debug_information.println(",location:    ",location_string);
				}
		}
	}
	private String[] get_movement_parameter(common_reader reader)
	{
		int parameter_number=reader.get_int();
		if(parameter_number<=0)
			return null;
		String result[]=new String[parameter_number];
		for(int i=0;i<result.length;i++)
			if((result[i]=reader.get_string())==null)
				result[i]="";
		return result;
	}
	private void flush_parameter(file_writer f,String p[])
	{
		int n=0;
		if(p!=null)
			n=p.length;
		
		f.print  ("/*	parameter number */	 ",n);
		f.print  ("	/* parameter */	");
		for(int i=0;i<n;i++){
			f.print((i==0)?"":" ");
			f.print(p[i]);
		}
		f.println();
	}
	public void flush(file_writer f,int space_number)
	{
		String str=moved_component_name;

		if(str==null)
			str="no_moved_component_name";
		if(str.compareTo("")==0)
			str="no_moved_component_name";
		
		
		f.set_pace(space_number);
		f.println("component_start");
		f.set_pace(space_number+8);
		f.println(str);
		
		if(follow_component_name!=null)
			for(int i=0,ni=follow_component_name.length;i<ni;i++){	
				f.println();
				f.println(follow_component_name[i]);
				follow_component_location[i].get_location_data(f);
			}
		f.set_pace(space_number);
		f.println("component_end");
		f.println();
		f.println();		
		
		
		f.println("/*	start state	*/	",start_state_flag    ?"invisible":"visible");
		f.println("/*	end state	*/	",terminate_state_flag?"invisible":"visible");
		f.print  ("/*	step number	*/	");
		if(movement==null)
			f.println(0);
		else{	
			f.println(movement.length);
			f.set_pace(space_number+4);
			if(movement.length>0){
				for(int i=0;i<(movement.length);i++){
					f.println("/*	time length	*/	 ",(double)(movement[i].time_length));
					flush_parameter(f,movement[i].start_parameter);
					f.print  ("/*	location	*/	");
					movement[i].start_location.get_location_data(f);
					f.println();
				}
				f.println("/*	time length	*/	 ",(double)(movement[movement.length-1].time_length));
				flush_parameter(f,movement[movement.length-1].terminate_parameter);
				f.print  ("/*	location	*/	");
				movement[movement.length-1].terminate_location.get_location_data(f);
			}
			f.set_pace(space_number);
		}
	}
	public movement_item_container(component comp)
	{
		start_state_flag=false;
		terminate_state_flag=true;
		movement=null;		
		moved_component_id=comp.component_id;
		moved_component_name=(comp.component_name==null)?"No_Name":moved_component_name;
		
		follow_component_name	=null;
		follow_component_location=null;
		follow_component_id		=null;
	}
	public movement_item_container(String my_moved_component_name,
			String my_follow_component_name[],location my_follow_component_location[],
			common_reader reader,boolean current_format_flag)
	{
		String str;

		moved_component_name=my_moved_component_name;
		moved_component_id=-1;
		follow_component_name=my_follow_component_name;
		follow_component_location=my_follow_component_location;
		if(follow_component_name==null)
			follow_component_id=null;
		else if(follow_component_name.length<=0)
			follow_component_id=null;
		else{
			follow_component_id=new int[follow_component_name.length];
			for(int i=0,ni=follow_component_id.length;i<ni;i++)
				follow_component_id[i]=-1;
		}
		
		if(current_format_flag){
			if((str=reader.get_string())==null)
				str="";
			start_state_flag		=(str.compareTo("visible")!=0)?true:false;
			if((str=reader.get_string())==null)
				str="";
			terminate_state_flag	=(str.compareTo("visible")!=0)?true:false;
		}else{
			reader.get_string();
			if((str=reader.get_string())==null)
				str="";
			start_state_flag		=(str.compareTo("��ʾ")!=0)?true:false;
			reader.get_string();
			if((str=reader.get_string())==null)
				str="";
			terminate_state_flag	=(str.compareTo("��ʾ")!=0)?true:false;
			
			reader.get_string();
		}
		
		int move_item_number=reader.get_int();
		if(move_item_number<=0)
			movement=null;
		else{
			movement=new movement_item[move_item_number];
			double time_length=reader.get_double();
			String start_parameter[]=null;
			if(current_format_flag)
				start_parameter=get_movement_parameter(reader);
			location start_location=new location(reader);
			for(int i=0;i<move_item_number;i++){
				double new_time_length=reader.get_double();
				String terminate_parameter[]=null;
				if(current_format_flag)
					terminate_parameter=get_movement_parameter(reader);
				location terminate_location=new location(reader);
				movement[i]=new movement_item(time_length,start_parameter,start_location,terminate_parameter,terminate_location);
				start_parameter=terminate_parameter;
				start_location=terminate_location;
				time_length=new_time_length;
			}
		}
	}
}
