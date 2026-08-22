package kernel_interface;

import java.io.File;

import kernel_scene.system_parameter;
import kernel_scene.scene_call_result;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;
import kernel_common_class.download_file_from_url;
import kernel_common_class.tree_string_locker_container;

public class file_download_manager
{
	private system_parameter system_par;
	private client_request_response request_response;
	private tree_string_locker_container string_locker_container;
	
	private String proxy_server,file_name,undecode_file_name;
	private String code_str,file_ext,file_date,link_token,info_str;
	
	private boolean get_information()
	{
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
		int index_id;
		
		if((str=info_str)==null)
			return true;
		if((index_id=str.indexOf(";"))<0)
			return true;
		code_str=str.substring(0,index_id);
		
		str=str.substring(index_id+1);
		if((index_id=str.indexOf(";"))<0)
			return true;
		String file_charset;
		if((file_charset=str.substring(0,index_id)).length()<=0)
			file_charset=system_par.network_data_charset;
		
		str=str.substring(index_id+1);
		if((index_id=str.indexOf(";"))<0)
			return true;
		String file_content;
		if((file_content=str.substring(0,index_id)).length()<=0)
			file_content="text/plain";

		str=str.substring(index_id+1);
		if((index_id=str.indexOf(";"))<0)
			return true;
		if((file_ext=str.substring(0,index_id)).length()<=0)
			file_ext="txt";
		
		str=str.substring(index_id+1);
		if((index_id=str.indexOf(";"))<0)
			return true;
		if((link_token=str.substring(0,index_id)).length()<=0)
			link_token="false";
		
		str=str.substring(index_id+1);
		if((file_date=str).length()<=0)
			file_date="0";
		
		if((undecode_file_name=request_response.get_parameter("file"))==null) {
			debug_information.println("Request file is null in downloader,url is ",
					request_response.implementor.get_url());
			debug_information.println();
			return true;
		}
		if(undecode_file_name.length()<=0){
			undecode_file_name=null;
			debug_information.println("Request file name is empty in downloader,url is ",
					request_response.implementor.get_url());
			debug_information.println();
			return true;
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
			return true;
		}
		request_response.set_charset_name(file_charset);
		request_response.set_content_type(file_content);
		return false;
	}
	private scene_call_result download_routine()
	{
		if(proxy_server==null) {
			File f=new File(system_par.temporary_file_par.temporary_root_directory_name+file_name);
			return f.exists()?new scene_call_result(f,system_par):null;
		}
		String directory_name=system_par.temporary_file_par.temporary_proxy_directory_name;
		directory_name+=file_directory.replace_directory_special_char(proxy_server);
		if(directory_name.charAt(directory_name.length()-1)!=File.separatorChar)
			directory_name+=File.separatorChar;
		
		file_reader fr=new file_reader(directory_name+"proxy_url.txt",system_par.local_data_charset);
		String proxy_url=fr.get_string();
		fr.close();
		
		if(proxy_url!=null)
			if((proxy_url=proxy_url.trim()).length()<=0)
				proxy_url=null;
		if(proxy_url==null){
			debug_information.println("NO proxy_url error in downloader:	",proxy_server);
			return null;
		}
		proxy_url+=undecode_file_name+"&proxy_info="+info_str;
		
		String my_file_name=directory_name+file_directory.replace_directory_special_char(file_name);
		if(link_token.compareTo("true")==0){
			int index_id;
			if((index_id=my_file_name.lastIndexOf('.'))<0)
				my_file_name+="."+file_ext;
			else
				my_file_name=my_file_name.substring(0,index_id+1)+file_ext;
		}

		long file_date_long=Long.decode(file_date);

		String my_lock_key=my_file_name+".lock";
		string_locker_container.write_lock(my_lock_key);
		
		File f;
		if((f=new File(my_file_name)).exists()){
			if(f.lastModified()==file_date_long){
				string_locker_container.write_unlock(my_lock_key);
				return new scene_call_result(f,system_par);
			}
		}
		if(download_file_from_url.do_download(
			proxy_url,my_file_name,system_par.response_block_size))
		{
			string_locker_container.write_unlock(my_lock_key);
			debug_information.println("Download fail:	",my_file_name);
			return null;
		}
		f=new File(my_file_name);
		f.setLastModified(file_date_long);
		string_locker_container.write_unlock(my_lock_key);
		debug_information.println("Download success:	",my_file_name);
		
		return new scene_call_result(f,system_par);
	}
	private scene_call_result download_result;
	
	private file_download_manager(client_request_response my_request_response,
			system_parameter my_system_par,tree_string_locker_container my_string_locker_container)
	{
		system_par				=my_system_par;
		request_response		=my_request_response;
		string_locker_container	=my_string_locker_container;
		
		download_result			=null;
		
		if(get_information())
			return;
		if((download_result=download_routine())==null) 
			return;

		String request_modified_str;
		if((request_modified_str=request_response.implementor.get_header("If-Modified-Since"))==null)
			return;
		
		if(download_result.last_modified_time>system_par.http_date_str.parse(request_modified_str))
			return;
	
		download_result=null;
		request_response.implementor.response_not_modify(
			"response_not_modify in execute_file_call of client_interface\n file name is "
			+download_result.original_file_name);
		
		return;
	}
	public static scene_call_result download(client_request_response my_request_response,
			system_parameter my_system_par,tree_string_locker_container my_string_locker_container)
	{
		return (new file_download_manager(my_request_response,
						my_system_par,my_string_locker_container)).download_result;
	}
}
