package kernel_file_manager;

import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class file_reader_gzip extends file_reader
{
	public file_reader_gzip(String my_user_file_name,String my_file_system_charset)
	{
		super(my_user_file_name,my_file_system_charset);
	}
	public static InputStream create_stream(InputStream my_input_stream)
	{
		if(my_input_stream!=null)
			try{
				return new GZIPInputStream(my_input_stream);
			}catch(Exception e) {
				;
			}
		return null;
	}
}
