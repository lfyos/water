package kernel_common_class;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import kernel_file_manager.file_writer;

public class exclusive_file_mutex
{
	private String file_name;
	private RandomAccessFile f_out;
	private FileChannel f_channel;
	private FileLock f_lock;
	
	private exclusive_file_mutex(String my_file_name)
	{
		file_name=my_file_name;
		f_out=null;
		f_channel=null;
		f_lock=null;

		if(new File(file_name).exists())
			return;
		file_writer.make_directory(file_name);
		try{
			(new File(file_name)).createNewFile();
		}catch(Exception e){
			;
		}
	}
	private exclusive_file_mutex lock_routine(String msg)
	{
		for(long sleep_time_length=50,max_time_length=2000;;){
			File f=new File(file_name);
			try{
				if(f_out==null)
					f_out=new RandomAccessFile(f,"rw");
		    	if(f_channel==null)
		    		f_channel=f_out.getChannel();
		    	if(f_lock==null)
		    		f_lock=f_channel.lock();
		    	if(f_lock!=null)
					if(f_lock.isValid())
						return this;
		    }catch(OverlappingFileLockException e){
		    	;
			}catch(Exception e) {
		    	debug_information.println(
		    			"Create file lock fail in exclusive_file_mutex: ",
		    			file_name+"\t\t"+e.toString());
		    	e.printStackTrace();
		    }
			unlock();
			
			long this_sleep_time_length=(long)(Math.round(Math.random()*sleep_time_length));
			this_sleep_time_length=(this_sleep_time_length<1)?1:this_sleep_time_length;
			try{
				Thread.sleep(this_sleep_time_length);
			}catch(Exception e) {
				debug_information.println("exclusive_file_mutex sleep exception:	",e.toString());
				e.printStackTrace();
				Thread.yield();
			}
			if((sleep_time_length=(long)(Math.round(1.5*sleep_time_length)))>max_time_length) {
				sleep_time_length=max_time_length;
				if(msg!=null)
					debug_information.println(this_sleep_time_length+":	"+msg);
			}
		}
	}
	public void unlock()
	{
		if(f_lock!=null){
			try{
	    		f_lock.release();
			}catch(Exception e){
				debug_information.println("f_lock.release() fail in exclusive_file_mutex: ",
						file_name+"\t"+e.toString());
				e.printStackTrace();
			}
			try{
	    		f_lock.close();
			}catch(Exception e){
				debug_information.println("f_lock.close() fail in exclusive_file_mutex: ",
						file_name+"\t"+e.toString());
				e.printStackTrace();
			}
			f_lock=null;
		}
		if(f_channel!=null){
			try{
				f_channel.close();
			}catch(Exception e){
				debug_information.println("f_channel.close() fail in exclusive_file_mutex: ",
						file_name+"\t"+e.toString());
				e.printStackTrace();
			}
			f_channel=null;
		}
		if(f_out!=null){
			try{
				f_out.close(); 
			}catch(Exception e){
				debug_information.println("f_out.close() fail in exclusive_file_mutex: ",
						file_name+"\t"+e.toString());
				e.printStackTrace();
			}
			f_out=null;
		}
	}
	public static exclusive_file_mutex lock(String my_file_name,String msg)
	{
		return (new exclusive_file_mutex(my_file_name)).lock_routine(msg);
	}
}
