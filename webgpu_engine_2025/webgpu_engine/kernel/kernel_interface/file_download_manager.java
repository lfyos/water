package kernel_interface;

import java.net.URI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;

import kernel_scene.system_parameter;
import kernel_scene.scene_call_result;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_common_class.tree_string_locker_container;

public class file_download_manager
{
	private boolean not_success_flag;
	private system_parameter system_par;
	private client_request_response request_response;
	
	private String proxy_server,file_name,undecode_file_name;
	private String code_str,file_ext,file_date,link_token,info_str;
	
	private file_download_manager(client_request_response my_request_response,system_parameter my_system_par)
	{
		not_success_flag	=true;
		system_par			=my_system_par;
		request_response	=my_request_response;

		if((proxy_server=request_response.get_parameter("proxy_server"))!=null)
			if((proxy_server=proxy_server.trim()).length()<=0)
				proxy_server=null;
		
		file_name			=null;
		undecode_file_name	=null;
		code_str			=null;
		file_ext			=null;
		file_date			=null;
		link_token			=null;
		info_str			=request_response.get_parameter("proxy_info");

		String str;
		if((str=info_str)==null)
			return;
		int index_id;
		if((index_id=str.indexOf(";"))<0)
			return;
		code_str=str.substring(0,index_id);
		
		if((index_id=(str=str.substring(index_id+1)).indexOf(";"))<0)
			return;
		String file_charset;
		if((file_charset=str.substring(0,index_id).trim()).length()<=0)
			file_charset=system_par.network_data_charset;
		
		if((index_id=(str=str.substring(index_id+1)).indexOf(";"))<0)
			return;
		String file_content;
		if((file_content=str.substring(0,index_id).trim()).length()<=0)
			file_content="text/plain";

		if((index_id=(str=str.substring(index_id+1)).indexOf(";"))<0)
			return;
		if((file_ext=str.substring(0,index_id)).length()<=0)
			file_ext="txt";
		
		if((index_id=(str=str.substring(index_id+1)).indexOf(";"))<0)
			return;
		if((link_token=str.substring(0,index_id).trim()).length()<=0)
			link_token="false";
		
		if((file_date=str.substring(index_id+1).trim()).length()<=0)
			file_date="0";
		
		if((undecode_file_name=request_response.get_parameter("file"))==null) {
			debug_information.println("Request file is null in downloader,url is ",
					request_response.implementor.get_url());
			debug_information.println();
			return;
		}
		if((undecode_file_name=undecode_file_name.trim()).length()<=0){
			undecode_file_name=null;
			debug_information.println("Request file name is empty in downloader,url is ",
					request_response.implementor.get_url());
			debug_information.println();
			return;
		}
		try{
			file_name=java.net.URLDecoder.decode(undecode_file_name,code_str);
			file_name=java.net.URLDecoder.decode(file_name,code_str);
		}catch(Exception e){
			file_name=null;
			undecode_file_name=null;
			e.printStackTrace();
			debug_information.println("Decode file name fail in downloader,url is ",
					request_response.implementor.get_url());
			debug_information.println(e.toString());
			debug_information.println();
			return;
		}
		request_response.set_charset_name(file_charset);
		request_response.set_content_type(file_content);
		not_success_flag=false;
	}
	private static boolean download_frm_url(String url,String path_name,int buffer_size)
	{
		file_writer.file_touch(path_name,true);
		
		BufferedInputStream input_stream=null;
		FileOutputStream output_stream=null;
		try {
			input_stream=new BufferedInputStream(new URI(url).toURL().openStream());
			output_stream=new FileOutputStream(path_name) ;
			byte dataBuffer[]=new byte[buffer_size];
			for(int bytes_read_counter;(bytes_read_counter=input_stream.read(dataBuffer,0,buffer_size))>=0;)
				output_stream.write(dataBuffer,0,bytes_read_counter);
			output_stream.close();
			input_stream.close();
			return true;
		} catch (Exception e) {
			debug_information.println("Download fail:	",e.toString());
			debug_information.println("URL:	",url);
			debug_information.println("file:	",path_name);
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
		return false;
	}
	private static scene_call_result download_routine(client_request_response request_response,
			system_parameter system_par,tree_string_locker_container string_locker_container)
	{
		file_download_manager downloader=new file_download_manager(request_response,system_par);
		if(downloader.not_success_flag)
			return null;
	
		if(downloader.proxy_server==null) {
			String directory_name=system_par.temporary_file_par.temporary_root_directory_name;
			File f=new File(directory_name+downloader.file_name);
			return f.exists()?new scene_call_result(f,system_par):null;
		}
		
		String directory_name =system_par.temporary_file_par.temporary_proxy_directory_name;
		directory_name+=file_reader.separator(downloader.proxy_server);
		if(directory_name.charAt(directory_name.length()-1)!=File.separatorChar)
			directory_name+=File.separatorChar;
		String my_file_name=file_reader.separator(downloader.file_name);
		if(downloader.link_token.compareTo("true")==0) {
			int index_id=my_file_name.lastIndexOf('.');
			my_file_name =(index_id<0)?(my_file_name+"."):(my_file_name.substring(0,index_id+1));
			my_file_name+=downloader.file_ext;
		}
		my_file_name=directory_name+my_file_name;
		
		String my_lock_key=my_file_name+".lock";
		string_locker_container.write_lock(my_lock_key);
		
		File f=new File(my_file_name);
		if(f.exists()){
			if(f.lastModified()==Long.decode(downloader.file_date)){
				string_locker_container.write_unlock(my_lock_key);
				return new scene_call_result(f,system_par);
			}
		}	
		String proxy_url=system_par.proxy_server_change_name.
				search_change_name(downloader.proxy_server,null);
		if(proxy_url!=null)
			if((proxy_url=proxy_url.trim()).length()<=0)
				proxy_url=null;
		if(proxy_url==null){
			string_locker_container.write_unlock(my_lock_key);
			debug_information.println("proxy_url error in downloader:	",downloader.proxy_server);
			return null;
		}
		proxy_url+=downloader.undecode_file_name+"&proxy_info="+downloader.info_str;
		boolean down_load_result=download_frm_url(
 					proxy_url,my_file_name,system_par.response_block_size);
 		
		debug_information.println();
		debug_information.println("Download from ",	proxy_url);
		debug_information.println("Download to ",	my_file_name);
		debug_information.println("Download ",down_load_result?"success":"fail");
		
		if(down_load_result){
			f=new File(my_file_name);
			f.setLastModified(Long.decode(downloader.file_date));
			string_locker_container.write_unlock(my_lock_key);
			return new scene_call_result(f,system_par);
		}else {
			string_locker_container.write_unlock(my_lock_key);
			return null;
		}	
	}
	
	public static scene_call_result download(client_request_response request_response,
			system_parameter system_par,tree_string_locker_container string_locker_container)
	{
		scene_call_result ret_val=download_routine(request_response,system_par,string_locker_container);
		if(ret_val==null)
			return null;
		String request_modified_str=request_response.implementor.get_header("If-Modified-Since");
		if(request_modified_str!=null)
			if(ret_val.last_modified_time<=system_par.http_date_str.parse(request_modified_str)){
				request_response.implementor.response_not_modify(
					"response_not_modify in execute_file_call of client_interface\n file name is "
							+ret_val.original_file_name);
				return null;
			}
		return ret_val;
	}
}
