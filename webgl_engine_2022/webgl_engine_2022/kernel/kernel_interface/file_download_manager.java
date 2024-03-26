package kernel_interface;

import java.io.File;
import java.util.Date;

import kernel_common_class.debug_information;
import kernel_common_class.http_modify_string;
import kernel_engine.caculate_charset_compress_file_name;
import kernel_engine.engine_call_result;
import kernel_engine.system_parameter;
import kernel_network.client_request_response;

public class file_download_manager 
{
	public static engine_call_result download(client_request_response request_response,system_parameter system_par)
	{
		String file_name,file_date,real_file_date,request_url=request_response.implementor.get_url();
		
		if((file_name=request_response.get_parameter("file"))==null) {
			debug_information.println("Request file is null in proxy_downloader,url is ",request_url);
			debug_information.println();
			return null;
		}
		if((file_name=file_name.trim()).length()<=0){
			debug_information.println("Request file name is empty in proxy_downloader,url is ",request_url);
			debug_information.println();
			return null;
		}
		if((file_date=request_response.get_parameter("date"))==null) {
			debug_information.println("Request file date is null in proxy_downloader,url is ",request_url);
			debug_information.println();
			return null;
		}
		if((file_date=file_date.trim()).length()<=0){
			debug_information.println("Request file date is empty in proxy_downloader,url is ",request_url);
			debug_information.println();
			return null;
		}
		String request_charset=request_response.implementor.get_request_charset();
		try{
			file_name=java.net.URLDecoder.decode(file_name,	request_charset);
			file_name=java.net.URLDecoder.decode(file_name,	request_charset);
			
			file_date=java.net.URLDecoder.decode(file_date,	request_charset);
			file_date=java.net.URLDecoder.decode(file_date,	request_charset);
			
		}catch(Exception e){
			debug_information.println("Decode parameter fail in proxy_downloader,url is ",request_url);
			debug_information.println(e.toString());
			e.printStackTrace();
			debug_information.println();
			return null;
		}
		
		File f=new File(file_name=system_par.temporary_file_par.temporary_root_directory_name+file_name);
		if(!(f.exists())){
			debug_information.println("Request file NOT exist in proxy_downloader,url is ",request_url);
			debug_information.println(file_name);
			return null;
		}
		real_file_date=Long.toString(f.lastModified());
		if(file_date.compareTo(real_file_date)!=0){
			debug_information.println("Request file date error in proxy_downloader,url is ",request_url);
			debug_information.println(file_name);
			debug_information.println(file_date);
			debug_information.println(real_file_date);
			return null;
		}		
		String request_modified_str,current_modified_str=http_modify_string.string(new Date(f.lastModified()));
		if((request_modified_str=request_response.implementor.get_header("If-Modified-Since"))!=null)
			if(http_modify_string.parse(request_modified_str)>=f.lastModified()){
				request_response.implementor.response_not_modify(
					"response_not_modify in execute_file_call of client_interface\n file name is "+file_name,
					current_modified_str,Long.toString(system_par.file_buffer_expire_time_length));
				return null;
			}

		caculate_charset_compress_file_name cccfn=new caculate_charset_compress_file_name(f,system_par);
		request_response.response_content_type=(cccfn.content_type_str==null)?"text/plain":(cccfn.content_type_str);

		return new engine_call_result(
				cccfn.file_name,system_par.network_data_charset,
				cccfn.charset_file_name,cccfn.compress_file_name,
				current_modified_str,system_par.file_download_cors_string);
	}
}
