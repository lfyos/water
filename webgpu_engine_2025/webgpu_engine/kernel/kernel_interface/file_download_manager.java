package kernel_interface;

import java.io.File;

import kernel_scene.system_parameter;
import kernel_scene.scene_call_result;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;

public class file_download_manager 
{
	private static scene_call_result download_routine(
			String request_url,String request_charset,
			client_request_response request_response,system_parameter system_par)
	{
		String file_name;
		
		if((file_name=request_response.get_parameter("file"))==null) {
			debug_information.println("Request file is null in downloader,url is ",request_url);
			debug_information.println();
			return null;
		}
		if((file_name=file_name.trim()).length()<=0){
			debug_information.println("Request file name is empty in downloader,url is ",request_url);
			debug_information.println();
			return null;
		}
		try{
			file_name=java.net.URLDecoder.decode(file_name,request_charset);
			file_name=java.net.URLDecoder.decode(file_name,request_charset);
		}catch(Exception e){
			e.printStackTrace();
			debug_information.println("Decode file name fail in downloader,url is ",request_url);
			debug_information.println(e.toString());
			debug_information.println();
			return null;
		}
		
		File f=new File(file_name=system_par.temporary_file_par.temporary_root_directory_name+file_name);
		if(!(f.exists())){
			debug_information.println("Request file NOT exist in downloader,url is ",request_url);
			debug_information.println(file_name);
			return null;
		}
		
		long file_last_time=f.lastModified();
		String request_file_date;
		if((request_file_date=request_response.get_parameter("date"))!=null){
			if((request_file_date=request_file_date.trim()).length()<=0){
				debug_information.println("Request file date is empty in downloader,url is ",request_url);
				debug_information.println();
				return null;
			}
			if(Long.decode(request_file_date)!=file_last_time){
				debug_information.println("Request file date error in downloader,url is ",request_url);
				debug_information.println(file_name);
				debug_information.println("Request file date:	",request_file_date);
				debug_information.println("Real file date:		",f.lastModified());
				return null;
			}
		}
		String request_modified_str;
		if((request_modified_str=request_response.implementor.get_header("If-Modified-Since"))!=null)
			if(system_par.http_date_str.parse(request_modified_str)>=file_last_time){
				request_response.implementor.response_not_modify(
					"response_not_modify in execute_file_call of client_interface\n file name is "+file_name);
				return new scene_call_result(null,system_par);
			}
		String file_charset;
		if((file_charset=request_response.get_parameter("file_charset"))==null)
			file_charset=system_par.network_data_charset;
		else if((file_charset=file_charset.trim()).length()<=0)
			file_charset=system_par.network_data_charset;
		return new scene_call_result(f,file_charset,true,system_par);
	}
	public static scene_call_result download(
			client_request_response request_response,system_parameter system_par)
	{
		String request_url	=request_response.implementor.get_url();
		String request_charset=request_response.implementor.get_request_charset();
	
		scene_call_result ret_val;
		if((ret_val=download_routine(request_url,request_charset,request_response,system_par))!=null)
			return (ret_val.file_name==null)?null:ret_val;
		
		String file_origin_url,decode_file_origin_url;
		if((file_origin_url=request_response.get_parameter("file_origin"))==null)
			return null;
		try{
			decode_file_origin_url=java.net.URLDecoder.decode(file_origin_url,		request_charset);
			decode_file_origin_url=java.net.URLDecoder.decode(decode_file_origin_url,request_charset);
		}catch(Exception e){
			e.printStackTrace();
			debug_information.println("Decode file_origin_url fail in downloader,file_origin_url is ",file_origin_url);
			debug_information.println(e.toString());
			debug_information.println();
			return null;
		}
		request_response.implementor.redirect_url(decode_file_origin_url);
		return null;
	}
}
