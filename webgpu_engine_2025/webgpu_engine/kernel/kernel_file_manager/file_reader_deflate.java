package kernel_file_manager;

import java.io.InputStream;
import java.util.zip.DeflaterInputStream;

public class file_reader_deflate extends file_reader
{
	public file_reader_deflate(String my_user_file_name,String my_file_system_charset)
	{
		super(my_user_file_name,my_file_system_charset);
	}
	public static InputStream create_stream(InputStream my_input_stream)
	{
		if(my_input_stream!=null)
			try{
				return new DeflaterInputStream(my_input_stream);
			}catch(Exception e) {
				;
			}
		return null;
	}
}
