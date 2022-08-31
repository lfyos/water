package kernel_interface;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;
import kernel_common_class.download_file_from_webserver;
  
public class update_file_buffer
{
	volatile private String 		file_name_array[];
	volatile private int 			file_number_array[];
	volatile private ReentrantLock 	lock_array[],update_file_buffer_lock;
	
	public void destroy()
	{
		file_name_array=null;
		file_number_array=null;
		lock_array=null;
		update_file_buffer_lock=null;
	}
	private ReentrantLock operate_lock(boolean operation,String my_file_name)
	{
		update_file_buffer_lock.lock();
		ReentrantLock ret_val=operate_lock_routine(operation,my_file_name);
		update_file_buffer_lock.unlock();
		return ret_val;
	}
	private ReentrantLock operate_lock_routine(boolean operation,String my_file_name)
	{
		int slot_number=file_name_array.length;
		if(operation){
			for(int i=0;i<slot_number;i++)
				if(my_file_name.compareTo(file_name_array[i])==0){
					ReentrantLock ret_val=lock_array[i];
					if((--(file_number_array[i]))<=0){
						String 			bak_file_name_array[]	=file_name_array;
						int 			bak_file_number_array[]	=file_number_array;
						ReentrantLock 	bak_lock_array[]		=lock_array;
						
						file_name_array		=new String[slot_number-1];
						file_number_array	=new int[slot_number-1];
						lock_array			=new ReentrantLock[slot_number-1];
						
						for(int j=0,k=0;j<slot_number;j++)
							if(i!=j){
								file_name_array[k]		=bak_file_name_array[j];
								file_number_array[k]	=bak_file_number_array[j];
								lock_array[k++]			=bak_lock_array[j];
							}
					}
					return ret_val;
				}
			return null;
		}else{
			for(int i=0;i<slot_number;i++)
				if(my_file_name.compareTo(file_name_array[i])==0){
					file_number_array[i]++;
					return lock_array[i];
				}
			String 			bak_file_name_array[]	=file_name_array;
			int 			bak_file_number_array[]	=file_number_array;
			ReentrantLock 	bak_lock_array[]		=lock_array;
			
			
			file_name_array		=new String[slot_number+1];
			file_number_array	=new int[slot_number+1];
			lock_array			=new ReentrantLock[slot_number+1];
			
			for(int i=0;i<slot_number;i++) {
				file_name_array[i]		=bak_file_name_array[i];
				file_number_array[i]	=bak_file_number_array[i];
				lock_array[i]			=bak_lock_array[i];
			}
		
			file_name_array[slot_number]=my_file_name;
			file_number_array[slot_number]=1;
			lock_array[slot_number]=new ReentrantLock();
		
			return lock_array[slot_number];
		}
	}
	
	public boolean down_load(String urlPath,
			String file_name,int buffer_length,long file_last_modified_time)
	{
		file_name=file_reader.separator(file_name);
		
		if(new File(file_name).exists())
			return true;

		int index_id=file_name.lastIndexOf(File.separatorChar);
		String directory_name,file_name_only;
		if(index_id>=0) {
			directory_name=file_name.substring(0,index_id+1);
			file_name_only=file_name.substring(index_id+1);
		}else{
			directory_name="."+File.separator;
			file_name_only=file_name;
		}
		boolean success_flag=false;
		ReentrantLock my_lock;
		if((my_lock=operate_lock(false,file_name))!=null)
			my_lock.lock();
		
		debug_information.println("From : "+urlPath+"\n\tdownload to : "+file_name);
		
		try {
			success_flag=download_file_from_webserver.download(
				urlPath,file_name_only,directory_name,buffer_length);
		}catch(Exception e) {
			debug_information.println("down_load exception : "+urlPath+"\n\tdownload to : "+file_name);
			debug_information.println(e.toString());
			e.printStackTrace();
		}
		
		if((my_lock=operate_lock(true,file_name))!=null)
			my_lock.unlock();
		
		if(!success_flag)
			return false;
		
		new File(file_name).setLastModified(file_last_modified_time);
		return true;
	}
	public update_file_buffer()
	{
		file_name_array			=new String[0];
		file_number_array		=new int[0];
		lock_array				=new ReentrantLock[0];
		update_file_buffer_lock	=new ReentrantLock();
	}
}
