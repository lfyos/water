package kernel_file_manager;

import java.util.Date;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import kernel_common_class.common_writer;
import kernel_common_class.debug_information;

public class file_writer extends common_writer
{
	private FileOutputStream fos;
	private BufferedOutputStream bos;
	
	public String directory_name,file_name;
	public long file_length,lastModified_time;
	public boolean error_flag;
	
	public file_writer(String my_file_name,String my_file_charset)
	{
		super(my_file_charset,""," "," ");

		error_flag=false;
		
		file_length=0;
		lastModified_time=0;
		
		my_file_name=file_reader.separator(my_file_name);
		make_directory(my_file_name);
		
		File f=new File(my_file_name);
		if(f.exists()) {
			f.delete();
			f=new File(my_file_name);
		}
		
		directory_name=f.getParent();
		file_name=f.getName();
		file_length=f.length();
		lastModified_time=f.lastModified();
			
		if(directory_name==null)
			directory_name=".";
		else if(directory_name.compareTo("")==0)
			directory_name=".";
		directory_name+=File.separator;
		
		try{
			fos=new FileOutputStream(f);
			bos=new BufferedOutputStream(fos);			
		}catch(Exception e){
			e.printStackTrace();
			
			error_flag=true;
			debug_information.println("file_writer exception:\t",e.toString());
			debug_information.println(directory_name,file_name);
		}
	}
	public static void make_directory(String directory_name)
	{
		File f;
		directory_name=file_reader.separator(directory_name);
		for(int pointer=0,length=directory_name.length();pointer<length;pointer++)
			if(directory_name.charAt(pointer)==File.separatorChar)
				if(!((f=new File(directory_name.substring(0,pointer))).exists()))
					f.mkdir();
	}
	static private long copy_file_data(File s_file,BufferedOutputStream	d_buf)
	{
		long total_length=0;
		byte[] data_buf=new byte[16*1024];
		
		try{
			FileInputStream 	s_stream=new FileInputStream(s_file);
			BufferedInputStream	s_buf	=new BufferedInputStream(s_stream);
			
			for(int one_length;(one_length=s_buf.read(data_buf))>=0;total_length+=one_length)
	        	d_buf.write(data_buf,0,one_length);
	       
	        s_buf.close();
			s_stream.close();
		}catch(Exception e){
			e.printStackTrace();
			
			debug_information.println("file_writer copy_file_data exception:\t",e.toString());
			debug_information.println(s_file.getAbsolutePath());
		}
		return total_length;
	}
	static private long merge_file(String source_file_name,BufferedOutputStream d_buf)
	{
		long total_length=0;
		
		File s_file=new File(file_reader.separator(source_file_name));
		
		if(s_file.isDirectory()){
			String file_list[];
			if((file_list=s_file.list())!=null)
				for(int i=0,ni=file_list.length;i<ni;i++)
					total_length+=merge_file(s_file.getAbsolutePath()+File.separator+file_list[i],d_buf);
		}else
			total_length+=copy_file_data(s_file,d_buf);
		
		return total_length;
	}
	static public long merge_file(String my_source_file_name[],String destination_file_name)
	{
		String source_file_name[]=new String[my_source_file_name.length];
		for(int i=0,ni=source_file_name.length;i<ni;i++)
			source_file_name[i]=file_reader.separator(my_source_file_name[i]);
		destination_file_name=file_reader.separator(destination_file_name);

		long total_length=0;
		File d_file=new File(destination_file_name);
		try{
			FileOutputStream		d_stream	=new FileOutputStream(d_file);
			BufferedOutputStream	d_buf		=new BufferedOutputStream(d_stream);
			if(source_file_name!=null)
				for(int i=0,ni=source_file_name.length;i<ni;i++)
					total_length+=merge_file(source_file_name[i],d_buf);
			d_buf.close();
			d_stream.close();
		}catch(Exception e){
			e.printStackTrace();
			
			debug_information.println("file_writer merge_file exception:\t",e.toString());
			if(source_file_name!=null)
				for(int i=0,ni=source_file_name.length;i<ni;i++)
					debug_information.println(source_file_name[i]);
			debug_information.println(destination_file_name);
		}
		return total_length;
	}
	static private long file_copy_routine(String source_file_name,String destination_file_name)
	{
		source_file_name=file_reader.separator(source_file_name);	
		destination_file_name=file_reader.separator(destination_file_name);
		
		long total_length=0;
		
		File s_file=new File(source_file_name);
		File d_file=new File(destination_file_name);
		
		if(s_file.isDirectory()){
			d_file.mkdir();
			String file_list[]=s_file.list();
			if(file_list!=null)
				for(int i=0,ni=file_list.length;i<ni;i++)
					total_length+=file_copy_routine(
							s_file.getAbsolutePath()+File.separator+file_list[i],
							d_file.getAbsolutePath()+File.separator+file_list[i]);
		}else if(s_file.getAbsolutePath().compareTo(d_file.getAbsolutePath())!=0){
			d_file.delete();
			try{
				FileOutputStream		d_stream	=new FileOutputStream(d_file);
				BufferedOutputStream	d_buf		=new BufferedOutputStream(d_stream);
				
				total_length+=copy_file_data(s_file,d_buf);
				
				d_buf.close();
				d_stream.close();
			}catch(Exception e){
				e.printStackTrace();
				
				debug_information.println("file_writer file_copy_routine exception:\t",e.toString());
				debug_information.println(source_file_name);
				debug_information.println(destination_file_name);
			}
		}
		return total_length;
	}
	static private void create_file_directory(String file_name)
	{
		file_name=file_reader.separator(file_name);
		
		for(int i=0,n=file_name.length();i<n;i++)
			if(file_name.charAt(i)==File.separatorChar)
				(new File(file_name.substring(0,i))).mkdir();
	}
	static public long file_copy(String source_file_name,String destination_file_name)
	{
		create_file_directory(destination_file_name);
		return file_copy_routine(source_file_name,destination_file_name);
	}
	static public long file_copy_with_brother(String source_file_name,String destination_file_directory)
	{
		source_file_name=file_reader.separator(source_file_name);
		destination_file_directory=file_reader.separator(destination_file_directory);
		
		file_reader f;
		f=new file_reader(source_file_name,null);
		f.close();
		return file_copy(f.directory_name,destination_file_directory);
	}
	static public void charset_copy(
			String source_file_name,String source_charset,
			String destination_file_name,String destination_charset)
	{
		source_file_name=file_reader.separator(source_file_name);
		destination_file_name=file_reader.separator(destination_file_name);
		
		File f=new File(destination_file_name);
		f.delete();

		byte enter_byte[];
		
		try{
			enter_byte=("\n").getBytes(destination_charset);
		}catch(Exception e){
			e.printStackTrace();
			
			debug_information.println("file_writer charset_copy can not get byte of enter:\t",e.toString());
			debug_information.println(source_file_name);
			debug_information.println(destination_file_name);
			
			return;
		}
		
		file_reader fr=new file_reader(source_file_name,source_charset);
		if(fr.error_flag()||fr.eof()) {
			debug_information.println("file_writer charset_copy open file error");
			debug_information.println(source_file_name);
			debug_information.println(destination_file_name);
			return;
		}
		
		create_file_directory(destination_file_name);
		FileOutputStream		d_stream=null;
		BufferedOutputStream	d_buf=null;
		try{
			d_stream	=new FileOutputStream(f);
			d_buf		=new BufferedOutputStream(d_stream);
			
			while(true){
				if(fr.error_flag()||fr.eof())
					break;
				String str=fr.get_line();
				if(str==null)
					continue;
				if(str.length()<=0)
					continue;
				
				d_buf.write(str.getBytes(destination_charset));
				d_buf.write(enter_byte);
			}
		}catch(Exception e){
			e.printStackTrace();
			
			debug_information.println("file_writer charset_copy exception:\t",e.toString());
			debug_information.println(source_file_name);
			debug_information.println(destination_file_name);
			
		}
		if(d_buf!=null)
			try{
				d_buf.close();
			}catch(Exception e){
			}
		if(d_stream!=null)
			try{
				d_stream.close();
			}catch(Exception e){
			}
		fr.close();
	}
	static public void file_rename(String old_file_name,String new_file_name)
	{
		old_file_name=file_reader.separator(old_file_name);
		new_file_name=file_reader.separator(new_file_name);
		
		(new File(new_file_name)).delete();
		(new File(old_file_name)).renameTo(new File(new_file_name));
	}
	static public void file_delete(String my_file_name)
	{
		class file_writer_file_delete_travel_through_directory_class extends travel_through_directory
		{
			public void operate_directory_terminate(String directory_name)
			{
				try{
					new File(directory_name).delete();
				}catch(Exception e) {
					;
				}	
			}
			public void operate_file(String file_name)
			{
				try{
					new File(file_name).delete();
				}catch(Exception e) {
					;
				}
			}
		};
		(new file_writer_file_delete_travel_through_directory_class()).do_travel(my_file_name,false);
	}
	public void close()
	{
		if(bos!=null)
			try{
				bos.close();
				bos=null;
			}catch(Exception e){
				e.printStackTrace();
				
				debug_information.println("file_writer close exception 1:\t",e.toString());
				debug_information.println(directory_name,file_name);
			}
		if(fos!=null)
			try{
				fos.close();
				fos=null;
			}catch(Exception e){
				e.printStackTrace();
				
				debug_information.println("file_writer close exception 2:\t",e.toString());
				debug_information.println(directory_name,file_name);
				
			}
	}
	public common_writer flush()
	{
		try {
			bos.flush();
		}catch(Exception e) {
			e.printStackTrace();
			
			debug_information.println("bos flush int file writer exception:\t",e.toString());
			debug_information.println(directory_name,file_name);
		}
		try {
			fos.flush();
		}catch(Exception e) {
			e.printStackTrace();
			
			debug_information.println("fos flush int file writer exception:\t",e.toString());
			debug_information.println(directory_name,file_name);
		}
		return this;
	}
	public common_writer write_routine(byte data[],int offset,int length)
	{
		file_length+=length;
		try{
			bos.write(data,offset,length);
		}catch(Exception e){
			e.printStackTrace();
			
			debug_information.println("write_routine int file writer exception:\t",e.toString());
			debug_information.println(directory_name,file_name);
		}
		return this;
	};
	public static void file_touch(String file_name,long touch_t,boolean create_flag)
	{
		File f=new File(file_reader.separator(file_name));
		if(f.exists()) {
			if(f.lastModified()<touch_t)
				f.setLastModified(touch_t);
		}else if(create_flag){
			try{
				f.createNewFile();
			}catch(Exception e) {
				;
			}
			f.setLastModified(touch_t);
		}
	}
	public static void file_touch(String file_name,boolean create_flag)
	{
		file_touch(file_name,(new Date()).getTime(),create_flag);
	}
	public static void delete_comment(String directory_or_file_name,String file_charset)
	{
		class delete_comment_travel_through_directory extends travel_through_directory
		{
			public void operate_file(String file_name)
			{
				file_reader fr=new file_reader(file_name,				file_charset);
				file_writer fw=new file_writer(file_name+".nocomment",	file_charset);
				
				for(String str;!(fr.eof());)
					if((str=fr.get_string())!=null)
						fw.println(str);
				
				fr.close();
				fw.close();
				file_rename(file_name+".nocomment",file_name);
				(new File(fr.directory_name+fr.file_name)).setLastModified(fr.lastModified_time);
			}
		};
		new delete_comment_travel_through_directory().do_travel(directory_or_file_name,false);
	}
}
