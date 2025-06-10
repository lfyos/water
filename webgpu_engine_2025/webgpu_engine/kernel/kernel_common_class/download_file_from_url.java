package kernel_common_class;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.URI;

import kernel_file_manager.file_writer;

public class download_file_from_url 
{
	public static boolean do_download(String proxy_url,String path_name,int buffer_size)
	{
		file_writer.file_touch(path_name,true);
		
		BufferedInputStream input_stream=null;
		FileOutputStream output_stream=null;
		try {
			input_stream=new BufferedInputStream(new URI(proxy_url).toURL().openStream());
			output_stream=new FileOutputStream(path_name) ;
			byte dataBuffer[]=new byte[buffer_size];
			int bytes_read_counter;
			while((bytes_read_counter=input_stream.read(dataBuffer,0,buffer_size))>=0)
				output_stream.write(dataBuffer,0,bytes_read_counter);
			output_stream.close();
			input_stream.close();
			
			return false;
		} catch (Exception e) {
			debug_information.println("Download fail:	",	e.toString());
			debug_information.println("URL:	",				proxy_url);
			debug_information.println("file:	",			path_name);
		}
		if(input_stream!=null)
			try {
				input_stream.close();
				input_stream=null;
			} catch (Exception e) {
				;
			}
		if(output_stream!=null)
			try {
				output_stream.close();
				output_stream=null;
			} catch (Exception e) {
				;
			}
		file_writer.file_delete(path_name);
		return true;
	}
}
