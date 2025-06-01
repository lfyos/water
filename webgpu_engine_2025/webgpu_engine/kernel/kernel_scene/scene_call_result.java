package kernel_scene;

import java.io.File;

import kernel_common_class.debug_information;

public class scene_call_result
{
	public String file_name,compress_file_name;
	public long last_modified_time;
	public boolean already_compress_file_flag;
	
	public String file_charset,response_content_type;
	
	public scene_call_result(File f,String my_file_charset,boolean cache_flag,system_parameter system_par)
	{
		file_name					=f.getAbsolutePath();
		compress_file_name			=null;
		last_modified_time			=cache_flag?f.lastModified():-1;
		already_compress_file_flag	=false;
		file_charset				=(my_file_charset!=null)?my_file_charset:system_par.network_data_charset;
		response_content_type		="text/plain";
		
		int index_id;
		if((index_id=file_name.lastIndexOf('.'))<0) 
			return;
		
		String str=file_name.substring(index_id+1);
		if((str=system_par.content_type_change_name.search_change_name(str,null))==null)
			return;
		
		if((index_id=str.indexOf(":"))<0)
			return;
		
		response_content_type=str.substring(index_id+1);
		str=str.substring(0,index_id).toLowerCase();
		
		if(str.compareTo("gzip")==0){
			already_compress_file_flag=true;
			return;
		}
		long compress_length;
		try{
			compress_length=Long.decode(str);
		}catch(Exception e) {
			debug_information.println("Unknown compress_str:	",str);
			return;
		}
		if((compress_length<=0)||(f.length()<=compress_length))
			return;
		
		if(file_name.indexOf(system_par.temporary_file_par.temporary_root_directory_name)==0){
			int dir_length=system_par.temporary_file_par.temporary_root_directory_name.length();
			String dir_name=system_par.temporary_file_par.temporary_compress_directory_name;
			compress_file_name=dir_name+file_name.substring(dir_length);
		}
		return;
	}
	public scene_call_result(long my_last_modified_time,
			String my_response_content_type,system_parameter system_par)
	{
		file_name			=null;
		compress_file_name	=null;
		last_modified_time	=my_last_modified_time;
		already_compress_file_flag	=false;
		file_charset		=system_par.network_data_charset;
		response_content_type=(my_response_content_type==null)?"text/plain":my_response_content_type;
	}
	public scene_call_result(String my_response_content_type,system_parameter system_par)
	{
		file_name			=null;
		compress_file_name	=null;
		last_modified_time	=-1;
		already_compress_file_flag=false;
		
		file_charset		=system_par.network_data_charset;
		response_content_type=(my_response_content_type==null)?"text/plain":my_response_content_type;
	}
}
