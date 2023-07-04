package kernel_engine;

import java.io.File;

import kernel_common_class.change_name;

public class caculate_charset_compress_file_name
{
	public String file_name,charset_file_name,compress_file_name,content_type_str;
	public int content_type_id;
	
	private void caculate(
			File f,									change_name content_type_change_name,
			String data_root_directory_name,		String compress_data_root_directory_name,
			String proxy_data_root_directory_name,	String compress_proxy_root_directory_name)
	{
		file_name=f.getAbsolutePath();
		if(file_name.indexOf(proxy_data_root_directory_name)==0){
			String temp_file_name=file_name.substring(proxy_data_root_directory_name.length());
			charset_file_name=compress_proxy_root_directory_name+temp_file_name+".charset";
			compress_file_name=compress_proxy_root_directory_name+temp_file_name+".compress";
		}else if(file_name.indexOf(data_root_directory_name)==0){
			String temp_file_name=file_name.substring(data_root_directory_name.length());
			charset_file_name=compress_data_root_directory_name+temp_file_name+".charset";
			compress_file_name=compress_data_root_directory_name+temp_file_name+".compress";
		}else{
			charset_file_name=null;
			compress_file_name=null;
		}
		
		int index_id;
		String ext_file_name="txt",fail_response_content_type="text/plain";
		if((index_id=file_name.lastIndexOf('.'))<0){
			content_type_str=fail_response_content_type;
			content_type_id=-1;
		}else{
			ext_file_name=file_name.substring(index_id+1);
			content_type_id=content_type_change_name.search(ext_file_name);
			content_type_str=content_type_change_name.get_search_result(content_type_id,fail_response_content_type);
		}
		
		if((index_id=content_type_str.indexOf(":"))==0){
			compress_file_name=null;
			content_type_str=content_type_str.substring(1);
		}else if(index_id>0){
			String compress_response_header=content_type_str.substring(0,index_id).toLowerCase();
			switch(compress_response_header) {
			case "gzip":
			case "deflate":
			case "br":
				compress_file_name=compress_response_header;
				break;
			default:
				long compress_length=Long.decode(compress_response_header);
				if((compress_length<=0)||(f.length()<compress_length))
					compress_file_name=null;
				break;
			}
			content_type_str=content_type_str.substring(index_id+1);
		}else
			compress_file_name=null;
			
		if((index_id=content_type_str.indexOf(":"))==0){
			charset_file_name=null;
			content_type_str=content_type_str.substring(1);
		}else if(index_id>0){
			if(content_type_str.substring(0,index_id).compareTo("charset")!=0)
				charset_file_name=null;
			content_type_str=content_type_str.substring(index_id+1);
		}else
			charset_file_name=null;
	}
	public caculate_charset_compress_file_name(File f,system_parameter system_par)
	{
		caculate(f,													system_par.content_type_change_name,
				system_par.data_root_directory_name,				system_par.proxy_par.compress_data_root_directory_name,
				system_par.proxy_par.proxy_data_root_directory_name,system_par.proxy_par.compress_proxy_data_root_directory_name);
	}
	public caculate_charset_compress_file_name(File f,system_parameter system_par,
				String proxy_data_root_directory_name,String compress_proxy_root_directory_name)
	{
		caculate(f,													system_par.content_type_change_name,
				system_par.data_root_directory_name,				system_par.proxy_par.compress_data_root_directory_name,
				proxy_data_root_directory_name,						compress_proxy_root_directory_name);
	}
}
