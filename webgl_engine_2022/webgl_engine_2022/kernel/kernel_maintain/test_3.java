package kernel_maintain;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

import kernel_common_class.debug_information;
import kernel_file_manager.travel_through_directory;

public class test_3 extends travel_through_directory
{
	private int sequence_id,test_file_number;
	private String directory_name,ex_directory_name;
	public void operate_file(String file_name)
	{
		test_file_number++;
		
		String ex_file_name=ex_directory_name+file_name.substring(directory_name.length());
		
		File f=new File(file_name),ex_f=new File(ex_file_name);
		
		if(f.exists()) {
			if(!(ex_f.exists())){
				debug_information.println(Integer.toString(sequence_id++),"	ex file  NOT  exist:	"+ex_file_name);
				debug_information.println("file name 1:	",	file_name);
				debug_information.println("file name 2:	",	ex_file_name);
				return;
			}
		}else{
			if(ex_f.exists()){
				debug_information.println(Integer.toString(sequence_id++),"	file NOT  exist:	"+file_name);
				debug_information.println("file name 1:	",	file_name);
				debug_information.println("file name 2:	",	ex_file_name);
			}
			return;
		}
		
		if(f.length()!=ex_f.length()) {
			debug_information.println(Integer.toString(sequence_id++),".length:"+f.length()+"/"+ex_f.length());
			debug_information.println("file name 1:	",	file_name);
			debug_information.println("file name 2:	",	ex_file_name);
			return;
		}
		
		int read_length;
		byte data_buf[]=new byte[(int)(f.length())];
		try{
			FileInputStream s_stream	=new FileInputStream(f);
			BufferedInputStream s_buf	=new BufferedInputStream(s_stream);	
			read_length=s_buf.read(data_buf);
			s_buf.close();
			s_stream.close();
		}catch(Exception e){
			debug_information.println(Integer.toString(sequence_id++),".read error:"+e.toString());
			debug_information.println("file name 1:	",	file_name);
			debug_information.println("file name 2:	",	ex_file_name);
			e.printStackTrace();
			return;
		}
		if(f.length()!=read_length) {
			debug_information.println(Integer.toString(sequence_id++),".read length:"+read_length+"/"+f.length());
			debug_information.println("file name 1:	",	file_name);
			debug_information.println("file name 2:	",	ex_file_name);
			return;
		}
		
		int ex_read_length;
		byte ex_data_buf[]=new byte[(int)(ex_f.length())];
		try{
			FileInputStream s_stream	=new FileInputStream(ex_f);
			BufferedInputStream s_buf	=new BufferedInputStream(s_stream);	
			ex_read_length=s_buf.read(ex_data_buf);
			s_buf.close();
			s_stream.close();
		}catch(Exception e){
			debug_information.println(Integer.toString(sequence_id++),".read error ex:"+e.toString());
			debug_information.println("file name 1:	",	file_name);
			debug_information.println("file name 2:	",	ex_file_name);
			e.printStackTrace();
			return;
		}
		if(ex_f.length()!=ex_read_length) {
			debug_information.println(Integer.toString(sequence_id++),"read length ex:"+ex_read_length+"/"+ex_f.length());
			debug_information.println("file name 1:	",	file_name);
			debug_information.println("file name 2:	",	ex_file_name);
			return;
		}
		
		for(int i=0;i<read_length;i++)
			if(data_buf[i]!=ex_data_buf[i]) {
				debug_information.println(Integer.toString(sequence_id++),".compare error:"+i);
				debug_information.println("file name 1:	",	file_name);
				debug_information.println("file name 2:	",	ex_file_name);
				return;
			}
		
		if(f.lastModified()!=ex_f.lastModified()) {
			debug_information.println(Integer.toString(sequence_id++),".last time:"+f.lastModified()+"/"+ex_f.lastModified());
			debug_information.println("file name 1:	",	file_name);
			debug_information.println("file name 2:	",	ex_file_name);
			return;
		}
	}
	
	public test_3(String my_directory_name,String my_ex_directory_name)
	{
		sequence_id=0;
		test_file_number=0;
		directory_name=my_directory_name;
		ex_directory_name=my_ex_directory_name;
		
		do_travel(directory_name, true);
		
		debug_information.println("sequence_id:"+sequence_id+",test_file_number:"+test_file_number);
	}
	
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		long tl=System.nanoTime();
		new test_3(
				"F:\\temp\\temporary_root_directory\\package_directory",
				"F:\\temp\\temporary_root_directory\\bak\\package_directory");
		new test_3(
				"F:\\temp\\temporary_root_directory\\bak\\scene_directory",
				"F:\\temp\\temporary_root_directory\\scene_directory");	
		tl=System.nanoTime()-tl;
		
		debug_information.println("End,time length:",tl/1000);
	}
}
