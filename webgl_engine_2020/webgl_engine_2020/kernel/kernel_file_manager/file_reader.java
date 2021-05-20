package kernel_file_manager;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.charset.Charset; 
import kernel_common_class.common_reader;
import kernel_common_class.common_writer;
import kernel_common_class.debug_information;

public class file_reader extends common_reader
{
	public static String separator(String file_name)
	{
		if(file_name==null)
			return null;
		else
			return file_name.replace('\\',File.separatorChar).replace('/', File.separatorChar);
	}
	
	public String directory_name,file_name;
	
	private static InputStream create_input_stream_reader(String my_user_file_name)
	{
		File f=new File(my_user_file_name=separator(my_user_file_name));
		if(!(f.exists()))
			return null;
		try{
			return new FileInputStream(f);
		}catch(Exception e){
			debug_information.println("file_reader open file error:\t",my_user_file_name);
			debug_information.println(e.toString());
			e.printStackTrace();
			return null;
		}
	}
	public file_reader(String my_user_file_name,String my_file_system_charset)
	{
		super(create_input_stream_reader(my_user_file_name),
			(my_file_system_charset!=null)?my_file_system_charset:Charset.defaultCharset().name());

		directory_name="";
		file_name=separator(my_user_file_name);

		if(error_flag()) {
			lastModified_time=0;
			return;
		}
		
		File f=new File(file_name);
		
		directory_name	=f.getParent();
		file_name		=f.getName();
		lastModified_time=f.lastModified();
		
		if(directory_name==null)
			directory_name=".";
		else if(directory_name.compareTo("")==0)
			directory_name=".";
		directory_name+=File.separator;
	}
	public static boolean is_exist(String file_name)
	{
		if(file_name==null)
			return false;
		return (new File(separator(file_name))).exists(); 
	}
	public static boolean is_not_exist(String file_name)
	{
		return is_exist(file_name)?false:true; 
	}
	public static int modify_time_compare(String s_file_name,String d_file_name)
	{
		long s_lastModified_time=(new File(separator(s_file_name))).lastModified();
		long d_lastModified_time=(new File(separator(d_file_name))).lastModified();
		
		if(s_lastModified_time<d_lastModified_time)
			return -1;
		if(s_lastModified_time>d_lastModified_time)
			return 1;
		return 0;
	}
	public static void get_text(common_writer cw,String file_name,String file_charset)
	{
		file_reader f=new file_reader(file_name,file_charset);
		f.get_text(cw);
		f.close();
	}
	public static String get_text(String file_name,String file_charset)
	{
		file_reader f;
		f=new file_reader(file_name,file_charset);
		String ret_val=f.get_text();
		f.close();
		return ret_val;
	}
}