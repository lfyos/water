package kernel_part;

import java.io.File;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_interface.client_process_bar;

public class buffer_object_file_modify_time_and_length_container 
{
	private long last_modify_time;
	private String boftal_file_name,boftal_token_array[];
	private buffer_object_file_modify_time_and_length boftal_array[];
	
	public buffer_object_file_modify_time_and_length_container(
			buffer_object_file_modify_time_and_length_container s,
			buffer_object_file_modify_time_and_length_container t)
	{
		int s_number=s.boftal_token_array.length;
		int t_number=t.boftal_token_array.length;

		last_modify_time=(s.last_modify_time<t.last_modify_time)?t.last_modify_time:s.last_modify_time;
		boftal_file_name=null;
		
		boftal_token_array	=new String[s_number+t_number];
		boftal_array		=new buffer_object_file_modify_time_and_length[s_number+t_number];
		
		for(int pointer=0,s_pointer=0,t_pointer=0;;pointer++){
			if(s_pointer>=s_number) {
				if(t_pointer>=t_number)
					break;
				boftal_array		[pointer]	=t.boftal_array			[t_pointer];
				boftal_token_array	[pointer]	=t.boftal_token_array	[t_pointer++];
				continue;
			}
			if(t_pointer>=t_number) {
				boftal_array		[pointer]	=s.boftal_array			[s_pointer];
				boftal_token_array	[pointer]	=s.boftal_token_array	[s_pointer++];
				continue;
			}
			String s_str=s.boftal_token_array[s_pointer];
			String t_str=t.boftal_token_array[t_pointer];
			if(s_str.compareTo(t_str)<=0) {
				boftal_array		[pointer]	=s.boftal_array			[s_pointer];
				boftal_token_array	[pointer]	=s.boftal_token_array	[s_pointer++];
			}else {
				boftal_array		[pointer]	=t.boftal_array			[t_pointer];
				boftal_token_array	[pointer]	=t.boftal_token_array	[t_pointer++];
			}
		}
	}
	public void destroy()
	{
		if(boftal_token_array!=null)
			boftal_token_array=null;
		if(boftal_array!=null) {
			for(int i=0,ni=boftal_array.length;i<ni;i++)
				if(boftal_array[i]!=null) {
					boftal_array[i].simple_part_mesh=null;
					boftal_array[i]=null;
				}
		}
	}
	
	public int get_boftal_number()
	{
		return (boftal_array==null)?0:(boftal_array.length);
	}
	
	public buffer_object_file_modify_time_and_length_container(
			client_process_bar process_bar,String my_boftal_file_name,String boftal_file_charset)
	{
		boftal_file_name=my_boftal_file_name;
		if(!(new File(boftal_file_name).exists())) {
			boftal_file_name	=null;
			last_modify_time	=0;
			boftal_token_array	=new String[0];
			boftal_array		=new buffer_object_file_modify_time_and_length[0];
			return;
		}
		file_reader fr		=new file_reader(boftal_file_name,boftal_file_charset);
		int number			=fr.get_int();
		
		last_modify_time	=fr.lastModified_time;
		boftal_token_array	=new String[number];
		boftal_array		=new buffer_object_file_modify_time_and_length[number];
		
		String process_title="load_buffer_object_file_information";
		
		if(process_bar!=null)
			process_bar.set_process_bar(true,process_title,"", 0, number);
		for(int i=0;i<number;i++) {
			if(process_bar!=null)
				process_bar.set_process_bar(false,process_title,"",i, number);
			boftal_token_array	[i]=fr.get_string();
			boftal_array		[i]=new buffer_object_file_modify_time_and_length(fr);
		}
		if(process_bar!=null)
			process_bar.set_process_bar(true,process_title,"", number, number);
		fr.close();
	}
	
	public buffer_object_file_modify_time_and_length search_boftal(	String boftal_token,long my_boftal_last_modify_time)
	{
		if(last_modify_time<my_boftal_last_modify_time) {
			if(boftal_file_name!=null)
				file_writer.file_delete(boftal_file_name);
		}else
			for(int begin_pointer=0,end_pointer=boftal_token_array.length-1;begin_pointer<=end_pointer;) {
				int middle_pointer=(begin_pointer+end_pointer)/2;
				int cmp_result=boftal_token_array[middle_pointer].compareTo(boftal_token);
				if(cmp_result>0)
					end_pointer=middle_pointer-1;
				else if(cmp_result<0)
					begin_pointer=middle_pointer+1;
				else 
					return boftal_array[middle_pointer];
			}
		return null;
	}
}
