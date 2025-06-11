package kernel_common_class;

import java.net.URI;
import java.io.File;
import java.util.Date;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;

import kernel_file_manager.file_writer;

public class download_file_from_url 
{
	public static boolean do_download(String proxy_url,String path_name,int buffer_size)
	{
		File f;
		if((f=new File(path_name)).exists())
			f.setLastModified(new Date().getTime());
		else{
			file_writer.make_directory(path_name);
			try{
				f.createNewFile();
			}catch(Exception e) {
				debug_information.println("Creating file for download fail:	",	e.toString());
				debug_information.println("URL:	",				proxy_url);
				debug_information.println("file:	",			path_name);
				return true;
			}
		}
		
		BufferedInputStream input_stream=null;
		FileOutputStream output_stream	=null;

		try {
			output_stream=new FileOutputStream(f);
			input_stream=new BufferedInputStream(new URI(proxy_url).toURL().openStream());
			byte dataBuffer[]=new byte[buffer_size];
			for(int counter;(counter=input_stream.read(dataBuffer,0,buffer_size))>=0;)
				output_stream.write(dataBuffer,0,counter);
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
		f.delete();
		return true;
	}
}
