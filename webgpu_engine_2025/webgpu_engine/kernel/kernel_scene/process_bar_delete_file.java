package kernel_scene;

import java.io.File;

import kernel_common_class.debug_information;
import kernel_file_manager.travel_through_directory;
import kernel_interface.client_process_bar;

public class process_bar_delete_file extends travel_through_directory
{
	class file_delete_counter extends travel_through_directory
	{
		public int total_number;
		public void operate_directory_terminate(String directory_name)
		{
			total_number++;
		}
		public void operate_file(String file_name)
		{
			total_number++;
		}
		public file_delete_counter(String counter_file_name)
		{
			total_number=0;
			do_travel(counter_file_name,false);
		}
	};
	
	public int total_number,delete_number;
	private client_process_bar process_bar;
	
	public void operate_directory_terminate(String directory_name)
	{
		try{
			var f=new File(directory_name);
			if(process_bar!=null)
				process_bar.set_process_bar(false,
						"delete_scene_temporary_file",f.getName(),
						delete_number++,total_number);
			f.delete();
		}catch(Exception e){
			debug_information.println("Delete directory fail:", directory_name);
			e.printStackTrace();
		}	
	}
	public void operate_file(String file_name)
	{
		try{
			var f=new File(file_name);
			if(process_bar!=null)
				process_bar.set_process_bar(false,
						"delete_scene_temporary_file",f.getName(),
						delete_number++,total_number);
			f.delete();
		}catch(Exception e) {
			debug_information.println("Delete file fail:",file_name);
			e.printStackTrace();
		}
	}
	private process_bar_delete_file(String my_file_name,client_process_bar my_process_bar)
	{
		delete_number	=0;
		process_bar		=my_process_bar;
		if((total_number=new file_delete_counter(my_file_name).total_number)<=0)
			total_number=1;

		if(process_bar!=null)
			process_bar.set_process_bar(true,
					"delete_scene_temporary_file", "",0,total_number);
		do_travel(my_file_name,false);
		if(process_bar!=null)
			process_bar.set_process_bar(false,
					"delete_scene_temporary_file", "",delete_number,total_number);
	};
	public static int do_delete(String my_file_name,client_process_bar my_process_bar)
	{
		return new process_bar_delete_file(my_file_name,my_process_bar).delete_number;
	}
}
