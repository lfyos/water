package kernel_common_class;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import kernel_common_class.debug_information;
import kernel_common_class.zip_file;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class download_file_from_webserver 
{
	private static boolean save_to_file(String my_url,
			BufferedInputStream stream,String file_name,int buffer_length)
	{
		byte buf[]=new byte[buffer_length];
		FileOutputStream out=null;

		try{
			file_writer.make_directory(file_name);
			out=new FileOutputStream(new File(file_name));
			for(int size;(size=stream.read(buf))>=0;)
				out.write(buf,0,size);
			out.close();
			return true;
		}catch(Exception e){
			debug_information.println(
					"download_file_from_webserver:save_to_file() fail:",
					e.toString()+"		url:"+my_url+"		file:"+file_name);
			e.printStackTrace();
		}
		if(out!=null)
			try{
				out.close();
				out=null;
			}catch(Exception e) {
				;
			}
		(new File(file_name)).delete();
		return false;
	}
	private static boolean uncompress(String source_file_name,
			 String destination_file_name,int buffer_length)
	 {  
		FileInputStream			is=null;  
		FileOutputStream		os=null;
		InputStream				gzis=null;  
		byte 					buffer[]=new byte[buffer_length];
		boolean uncompress_fail_flag=false;
		try{
			is	=new FileInputStream(new File(source_file_name));  
			os	=new FileOutputStream(new File(destination_file_name));  
			gzis=new GZIPInputStream(is);  
			for(int len;(len=gzis.read(buffer))>=0;)  
			   os.write(buffer,0,len);
		}catch(Exception e){
			uncompress_fail_flag=true;
			e.printStackTrace();  
		}
		if(os!=null)
			try {
				os.flush();
				os.close();
			}catch(Exception e) {
				;
			}
		if(gzis!=null)
			try{
				gzis.close();
			}catch(Exception e) {
				;
			}
		if(is!=null)
			try {
				is.close();
			}catch(Exception e) {
				;
			}
		return uncompress_fail_flag;
	}
	public static boolean download(String urlPath,String file_name,String directory_name,int buffer_length)
	{
		debug_information.println("download_file_from_webserver start, url:"+urlPath+"	file:",file_name);
		
		file_writer.file_delete(directory_name+file_name);
		
		HttpURLConnection connection=null;
		BufferedInputStream stream=null;
		try{
			connection=(HttpURLConnection)((new URL(urlPath)).openConnection());
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept-Encoding","gzip");
			connection.connect();
			if(connection.getResponseCode()==HttpURLConnection.HTTP_OK)
				stream=new BufferedInputStream(connection.getInputStream());
			else
				debug_information.println(
					"download_file_from_webserver:	connection.getResponseCode()!=HttpURLConnection.HTTP_OK");
		}catch(Exception e){
			debug_information.println("download_file_from_webserver:	Setup connection fail:",e.toString());
		}
		if(stream==null)
			debug_information.println("download_file_from_webserver:	Create connection stream fail");
		else{
			String code_str;
			String uncompress_fail_flag_file_name=directory_name+file_name+".uncompress_fail_flag";
			String uncompress_file_name=directory_name+file_name+".uncompress";
			if(save_to_file(urlPath,stream,uncompress_fail_flag_file_name,buffer_length)){
				if((code_str=connection.getContentEncoding())!=null)
					if((code_str=code_str.toLowerCase()).indexOf("gzip")>=0){
						if(uncompress(uncompress_fail_flag_file_name,uncompress_file_name,buffer_length)){
							new File(uncompress_fail_flag_file_name).delete();
							new File(uncompress_file_name).delete();
							debug_information.println(
								"download_file_from_webserver:When do file download,UnCompress file fail",
								"url:	"+urlPath+"		file:	"+file_name);
						}else{
							new File(uncompress_fail_flag_file_name).delete();
							new File(uncompress_file_name).renameTo(new File(uncompress_fail_flag_file_name));
						}
					}
				new File(uncompress_fail_flag_file_name).renameTo(new File(directory_name+file_name));
			}
			try{
				stream.close();
			}catch(Exception e) {
				debug_information.println(
						"When do file download,stream.close() fail,url is "+urlPath+",file is ",file_name);
				debug_information.println(e.toString());
				e.printStackTrace();
			}
			stream=null;
		}
		if(connection!=null){
			connection.disconnect();
			connection=null;
		}
		if(new File(directory_name+file_name).exists()) {
			debug_information.println("download_file_from_webserver success,url:"+urlPath+"	file:",file_name);
			return true;
		}else {
			debug_information.println("download_file_from_webserver fail,url:"+urlPath+"	file:",file_name);
			return false;
		}		
	}
	
	public static String unzip_download(String urlPath,
			String file_name,String directory_name,int buffer_length)
	{
		file_name=file_reader.separator(file_name);
		directory_name=file_reader.separator(directory_name);
		
		for(int length;(length=directory_name.length())>0;){
			if(directory_name.charAt(length-1)!=File.separatorChar)
				break;
			directory_name=directory_name.substring(0,length-1);
		}
		directory_name+=File.separator;
		
		if(download(urlPath,file_name,directory_name,buffer_length))
			return zip_file.try_unzip_file(directory_name+file_name);
		else
			return null;
	}
}
