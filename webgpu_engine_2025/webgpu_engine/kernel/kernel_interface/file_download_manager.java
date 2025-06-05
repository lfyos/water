package kernel_interface;

import java.io.File;

import kernel_scene.system_parameter;
import kernel_scene.scene_call_result;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;

public class file_download_manager
{
	private boolean not_success_flag;
	private system_parameter system_par;
	private client_request_response request_response;
	private String request_url,request_charset,file_charset,request_modified_str;
	private String request_file_date,file_origin_url,decode_file_origin_url;
	private String file_name;
	
	private file_download_manager(client_request_response my_request_response,system_parameter my_system_par)
	{
		not_success_flag	=true;
		system_par			=my_system_par;
		request_response	=my_request_response;
		
		request_url			=request_response.implementor.get_url();
		request_charset		=request_response.implementor.get_request_charset();
		request_modified_str=request_response.implementor.get_header("If-Modified-Since");
		
		if((file_charset=request_response.get_parameter("file_charset"))==null)
			file_charset=system_par.network_data_charset;
		else if((file_charset=file_charset.trim()).length()<=0)
			file_charset=system_par.network_data_charset;
		
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
		
		if((request_file_date=request_response.get_parameter("date"))!=null){
			if((request_file_date=request_file_date.trim()).length()<=0){
				debug_information.println("Request file date is empty in downloader,url is ",request_url);
				debug_information.println();
				return;
			}
			try {
				if(Long.decode(request_file_date)<=0) {
					debug_information.println("Request file date is less or equal than zero in downloader,url is ",request_url);
					debug_information.println();
					return;
				};
			}catch(Exception e) {
				debug_information.println("Request file date is NOT digital in downloader,url is ",request_url);
				debug_information.println();
				return;
			}
		}
		
		if((file_origin_url=request_response.get_parameter("file_origin"))==null)
			decode_file_origin_url=null;
		else 
			try{
				decode_file_origin_url=java.net.URLDecoder.decode(file_origin_url,		request_charset);
				decode_file_origin_url=java.net.URLDecoder.decode(decode_file_origin_url,request_charset);
			}catch(Exception e){
				e.printStackTrace();
				debug_information.println("Decode file_origin_url fail in downloader,file_origin_url is ",file_origin_url);
				debug_information.println(e.toString());
				debug_information.println();
				return;
			}
		not_success_flag=false;
	}
	
	private scene_call_result download_exist_file(File f)
	{
		if(request_file_date!=null){
			if(Long.decode(request_file_date)!=f.lastModified()){
				debug_information.println("Request file date error in downloader,url is ",request_url);
				debug_information.println(f.getAbsolutePath());
				debug_information.println("Request file date:	",request_file_date);
				debug_information.println("Real file date:		",f.lastModified());
				return null;
			}
		}
		if(request_modified_str!=null) {
			if(f.lastModified()<=system_par.http_date_str.parse(request_modified_str)){
				request_response.implementor.response_not_modify(
					"response_not_modify in execute_file_call of client_interface\n file name is "+f.getAbsolutePath());
				return null;
			}
		}
		return new scene_call_result(f,file_charset,true,system_par);
	}
	private scene_call_result download_not_exist_file()
	{
		if(decode_file_origin_url!=null) {
			request_response.implementor.redirect_url(decode_file_origin_url);
			return null;
		}
		debug_information.println("Request file NOT exist in downloader,url is ",request_url);
		debug_information.println(file_name);
		return null;
	}
	public static scene_call_result download(
			client_request_response request_response,system_parameter system_par)
	{
		file_download_manager downloader =new file_download_manager(request_response,system_par);
		if(downloader.not_success_flag)
			return null;
		
		File f;
		String file_name=system_par.temporary_file_par.temporary_root_directory_name+downloader.file_name;
		if((f=new File(file_name)).exists())
			return downloader.download_exist_file(f);
		else
			return downloader.download_not_exist_file();
	}
}
