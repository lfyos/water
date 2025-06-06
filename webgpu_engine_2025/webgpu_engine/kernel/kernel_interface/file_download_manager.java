package kernel_interface;

import java.io.File;

import kernel_scene.system_parameter;
import kernel_scene.scene_call_result;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_common_class.download_file_from_webserver;

public class file_download_manager
{
	private boolean not_success_flag;
	private system_parameter system_par;
	private client_request_response request_response;
	private String request_url,request_charset,proxy_server,file_charset,file_content,request_modified_str;
	private String request_file_date;
	private long request_file_date_long;
	private String file_name;
	
	private file_download_manager(client_request_response my_request_response,system_parameter my_system_par)
	{
		not_success_flag	=true;
		system_par			=my_system_par;
		request_response	=my_request_response;
		
		request_url			=request_response.implementor.get_url();
		request_charset		=request_response.implementor.get_request_charset();
		request_modified_str=request_response.implementor.get_header("If-Modified-Since");
		
		if((proxy_server=request_response.get_parameter("proxy_server"))!=null) {
			if((proxy_server=proxy_server.trim()).length()<=0)
				proxy_server=null;
			else if(proxy_server.length()<=0)
				proxy_server=null;
			else if(proxy_server.charAt(proxy_server.length()-1)!=File.separatorChar)
				proxy_server+=File.separatorChar;
		}
		if((file_charset=request_response.get_parameter("file_charset"))==null)
			file_charset=system_par.network_data_charset;
		else if((file_charset=file_charset.trim()).length()<=0)
			file_charset=system_par.network_data_charset;
		
		if((file_content=request_response.get_parameter("file_content"))==null)
			file_content="text/plain";
		else if((file_content=file_content.trim()).length()<=0)
			file_content="text/plain";
		
		if((file_name=request_response.get_parameter("file"))==null) {
			debug_information.println("Request file is null in downloader,url is ",request_url);
			debug_information.println();
			return;
		}
		if((file_name=file_name.trim()).length()<=0){
			debug_information.println("Request file name is empty in downloader,url is ",request_url);
			debug_information.println();
			return;
		}
		try{
			file_name=java.net.URLDecoder.decode(file_name,request_charset);
			file_name=java.net.URLDecoder.decode(file_name,request_charset);
		}catch(Exception e){
			e.printStackTrace();
			debug_information.println("Decode file name fail in downloader,url is ",request_url);
			debug_information.println(e.toString());
			debug_information.println();
			return;
		}
		
		request_file_date_long=0;
		if((request_file_date=request_response.get_parameter("date"))!=null){
			if((request_file_date=request_file_date.trim()).length()<=0){
				debug_information.println("Request file date is empty in downloader,url is ",request_url);
				debug_information.println();
				return;
			}
			try {
				if((request_file_date_long=Long.decode(request_file_date))<=0) {
					request_file_date_long=0;
					debug_information.println("Request file date is less or equal than zero in downloader,url is ",request_url);
					debug_information.println();
					return;
				};
			}catch(Exception e) {
				request_file_date_long=0;
				debug_information.println("Request file date is NOT digital in downloader,url is ",request_url);
				debug_information.println();
				return;
			}
		}
		not_success_flag=false;
	}
	public static scene_call_result download(
			client_request_response request_response,system_parameter system_par)
	{
		file_download_manager downloader;
		
		if((downloader=new file_download_manager(request_response,system_par)).not_success_flag)
			return null;
		
		File f;
		String directory_name;
		
		if(downloader.proxy_server==null) {
			directory_name=system_par.temporary_file_par.temporary_root_directory_name;
			if(!((f=new File(directory_name+downloader.file_name)).exists()))
				return null;
			if(downloader.request_file_date!=null)
				if(f.lastModified()<downloader.request_file_date_long){
					debug_information.println("Request file date error in downloader,url is ",
							downloader.request_url);
					debug_information.println(f.getAbsolutePath());
					debug_information.println("Request file date:	",
							downloader.request_file_date);
					debug_information.println("Real file date:		",f.lastModified());
					return null;
				}
		}else { 
			do{
				directory_name =system_par.temporary_file_par.temporary_proxy_directory_name;
				directory_name+=downloader.proxy_server;
				if((f=new File(directory_name+downloader.file_name)).exists()) {
					if(downloader.request_file_date==null)
						break;
					if(f.lastModified()==downloader.request_file_date_long)
						break;
				}
				file_reader fr=new file_reader(
						directory_name+"proxy_server_url.txt",system_par.local_data_charset);
				String url=fr.get_string();
				fr.close();
				
				if(download_file_from_webserver.download(url,
						downloader.file_name,directory_name, system_par.response_block_size))
				{	
					f=new File(directory_name+downloader.file_name);
					if(downloader.request_file_date!=null)
						f.setLastModified(downloader.request_file_date_long);
					break;
				}
				file_writer.file_delete(directory_name+downloader.file_name);
				debug_information.println("Proxy server download file fail,url:",url);
				debug_information.println("Proxy server download file fail,proxy_server:",
						downloader.proxy_server);
				debug_information.println("Proxy server download file fail,file:",
						downloader.file_name);
				return null;
			}while(false);			
		}
		if(downloader.request_modified_str!=null)
			if(f.lastModified()<=system_par.http_date_str.parse(downloader.request_modified_str)){
				request_response.implementor.response_not_modify(
					"response_not_modify in execute_file_call of client_interface\n file name is "
							+f.getAbsolutePath());
				return null;
			}
		request_response.set_charset_name(downloader.file_charset);
		request_response.set_content_type(downloader.file_content);
		return new scene_call_result(f,true,system_par);
	}
}
