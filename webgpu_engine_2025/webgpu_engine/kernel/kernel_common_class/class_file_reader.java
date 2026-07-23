package kernel_common_class;

import java.net.URL;
import java.io.InputStream;
import java.net.URLConnection;

public class class_file_reader
{
	public static common_reader get_reader(String my_file_name,Class<?>my_class,String my_charset)
	{
		try{
			URL my_url=my_class.getResource(my_file_name);
			URLConnection connection=my_url.openConnection();
			long lastModified_time=connection.getLastModified();
			InputStream input_stream=connection.getInputStream();
			common_reader ret_val=new common_reader(input_stream,my_charset);
			ret_val.lastModified_time=lastModified_time;
			return ret_val;
		}catch(Exception e){
			return null;
		}
	}
	public static long get_last_time(String my_file_name,Class<?>my_class,String my_charset)
	{
		try{
			URL my_url=my_class.getResource(my_file_name);
			URLConnection connection=my_url.openConnection();
			long lastModified_time=connection.getLastModified();
			connection.getInputStream().close();
			return lastModified_time;
		}catch(Exception e){
			return 0;
		}
	}
}
