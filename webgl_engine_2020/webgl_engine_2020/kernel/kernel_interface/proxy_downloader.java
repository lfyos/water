package kernel_interface;

import java.io.File;
import java.util.Date;

import kernel_common_class.debug_information;
import kernel_common_class.http_modify_string;
import kernel_engine.caculate_charset_compress_file_name;
import kernel_engine.engine_call_result;
import kernel_engine.system_parameter;
import kernel_engine.proxy_information;
import kernel_network.client_request_response;
import kernel_engine.interface_statistics;

public class proxy_downloader 
{
	private update_file_buffer ufb;
	
	public void destroy()
	{
		if(ufb!=null){
			ufb.destroy();
			ufb=null;
		}
	}
	public proxy_downloader()
	{
		ufb=new update_file_buffer(); 
	}
	public engine_call_result download(client_request_response request_response,
			system_parameter system_par,interface_statistics statistic)
	{
		String request_url=request_response.implementor.get_url();
		String file_name;
		if((file_name=request_response.get_parameter("file"))==null) {
			debug_information.println(
					"Request file is null in download of proxy_downloader,url is ",request_url);
			debug_information.println();
			return null;
		}
		if((file_name=file_name.trim()).length()<=0){
			debug_information.println(
					"Request file name  is empty in download of proxy_downloader,url is ",request_url);
			debug_information.println();
			return null;
		}
		String original_url;
		if((original_url=request_response.get_parameter("original"))==null){
			debug_information.println(
					"Request original_url  is null in download of proxy_downloader,url is ",request_url);
			debug_information.println();
			return null;
		}
		String date_str;
		if((date_str=request_response.get_parameter("date"))==null){
			debug_information.println(
					"Request data_str  is null in download of proxy_downloader,url is ",request_url);
			debug_information.println();
			return null;
		}
		String file_length_str;
		if((file_length_str=request_response.get_parameter("length"))==null){
			debug_information.println(
					"Request file_length_str  is null in download of proxy_downloader,url is ",request_url);
			debug_information.println();
			return null;
		}
		
		String decode_file_name,decode_original_url;
		long file_last_modified_time,file_length;
		try{
			decode_file_name=java.net.URLDecoder.decode(file_name,			system_par.network_data_charset);
			decode_file_name=java.net.URLDecoder.decode(decode_file_name,	system_par.network_data_charset);

			decode_original_url=java.net.URLDecoder.decode(original_url,		system_par.network_data_charset);
			decode_original_url=java.net.URLDecoder.decode(decode_original_url,	system_par.network_data_charset);
			
			String str;
			str=java.net.URLDecoder.decode(date_str,system_par.network_data_charset);
			str=java.net.URLDecoder.decode(str,		system_par.network_data_charset);
			file_last_modified_time=Long.decode(str.trim());
			
			str=java.net.URLDecoder.decode(file_length_str,	system_par.network_data_charset);
			str=java.net.URLDecoder.decode(str,				system_par.network_data_charset);
			file_length=Long.decode(str.trim());
		}catch(Exception e){
			debug_information.println(
					"Decode parameter fail in download of proxy_downloader,url is ",request_url);
			debug_information.println(e.toString());
			e.printStackTrace();
			debug_information.println();
			return null;
		}

		proxy_information proxy_info;
		
		if(decode_original_url.compareTo(request_url)==0)
			proxy_info=new proxy_information(decode_original_url);
		else{
			int search_id;
			if((search_id=system_par.proxy_par.search(decode_original_url))<0){
				String my_url=original_url+"?channel=proxy&file="+decode_file_name;
				my_url+="&length="+file_length_str+"&date="+date_str+"&original="+original_url;
				request_response.implementor.redirect_url(my_url,system_par.file_download_cors_string);

				debug_information.println(
						"Search decode_original_url fail in download of proxy_downloader");
				debug_information.println("Original url is ",decode_original_url);
				debug_information.println("Request  url is ",request_url);
				debug_information.println("Do redirect_url to ",my_url);
				debug_information.println();
				
				return null;
			}
			proxy_info=system_par.proxy_par.data_array[search_id];
		}
		
		File f;
		String proxy_file_name,proxy_charset_name;
		caculate_charset_compress_file_name cccfn;
		if(proxy_info.next_url==null){
			proxy_file_name=system_par.proxy_par.proxy_data_root_directory_name+decode_file_name;
			if(!((f=new File(proxy_file_name)).exists())) {
				debug_information.println(
						"Request fail NOT exist in download of proxy_downloader,url is ",request_url);
				debug_information.println(proxy_file_name);
				return null;
			}
			proxy_charset_name=system_par.network_data_charset;
			cccfn=new caculate_charset_compress_file_name(f,system_par);
		}else{
			String next_url=proxy_info.next_url+decode_file_name;
			if(proxy_info.encode_flag)
				next_url+="&length="+file_length_str+"&date="+date_str+"&original="+original_url;
			proxy_file_name=proxy_info.proxy_root_directory_name+decode_file_name;
			f=new File(proxy_file_name);
			boolean file_not_exist_flag=true;
			if(f.exists()){
				if(f.lastModified()>=file_last_modified_time)
					file_not_exist_flag=false;
				else{
					f.delete();
					f=new File(proxy_file_name);
					debug_information.println(
						"Delete buffered File because of Out of date in download of proxy_downloader");
					debug_information.println("Original url is ",decode_original_url);
					debug_information.println("Request  url is ",request_url);
					debug_information.println("Next     url is ",next_url);
					debug_information.println("File name is ",proxy_file_name);
				}
			}
			if(file_not_exist_flag){
				if(file_length>proxy_info.max_download_file_length){
					request_response.implementor.redirect_url(
							next_url,system_par.file_download_cors_string);
					debug_information.println(
							"Do redirect_url because Request file is too large in download of proxy_downloader:",
							Long.toString(file_length)+"/"+Long.toString(proxy_info.max_download_file_length));
					debug_information.println("next_url is ",next_url);
					debug_information.println("Original url is ",decode_original_url);
					debug_information.println("Request  url is ",request_url);
					debug_information.println("Next     url is ",next_url);
					debug_information.println("File name is ",proxy_file_name);
					debug_information.println();
					
					return null;
				}
				if(!(ufb.down_load(next_url,proxy_file_name,system_par.response_block_size,file_last_modified_time))){
					request_response.implementor.redirect_url(next_url,system_par.file_download_cors_string);
					debug_information.println("Download file fail in download of proxy_downloader,do redirect_url");
					debug_information.println("Original url is ",decode_original_url);
					debug_information.println("Request  url is ",request_url);
					debug_information.println("Next     url is ",next_url);
					debug_information.println("File name is ",proxy_file_name);
					debug_information.println();
					
					return null;
				}
				f=new File(proxy_file_name);
				statistic.proxy_request_file_number++;
				statistic.proxy_request_data_length+=f.length();
			}
			proxy_charset_name=proxy_info.proxy_charset_name;
			cccfn=new caculate_charset_compress_file_name(f,system_par,
				proxy_info.proxy_root_directory_name,proxy_info.compress_proxy_root_directory_name);
		}
		
		String request_modified_str=request_response.implementor.get_header("If-Modified-Since");
		String current_modified_str=http_modify_string.string(new Date(f.lastModified()));
		if(request_modified_str!=null){
			long request_time=http_modify_string.parse(request_modified_str);
			if((request_time>=0)&&(f.lastModified()>=0)&&(request_time>=f.lastModified())){
				request_response.implementor.response_not_modify(
					"response_not_modify fail in execute_file_call of client_interface\n file name is "+file_name,
					current_modified_str,Long.toString(system_par.file_buffer_expire_time_length));
				
				debug_information.println(
					"File unmodified in download of proxy_downloader",
					Long.toString(request_time)+"/"+Long.toString(f.lastModified()));
				debug_information.println("Original url is ",decode_original_url);
				debug_information.println("Request  url is ",request_url);
				debug_information.println("File name is ",proxy_file_name);
				debug_information.println();

				return null;
			}
		}
		
		statistic.proxy_response_file_number++;
		statistic.proxy_response_data_length+=f.length();
		
		request_response.response_content_type=(cccfn.content_type_str==null)?"text/plain":(cccfn.content_type_str);
		
		return new engine_call_result(cccfn.file_name,
				proxy_charset_name,cccfn.charset_file_name,cccfn.compress_file_name,
				current_modified_str,system_par.file_download_cors_string);
	}
}
