package kernel_scene;

import java.io.File;
import java.util.Date;

import kernel_common_class.debug_information;

public class scene_call_result
{
	public String file_name,compress_file_name;
	public long last_modified_time;
	public boolean already_compress_file_flag;
	
	public String file_charset,response_content_type;
	
	
	public scene_call_result(File f,String my_file_charset,system_parameter system_par)
	{
		file_charset=(my_file_charset!=null)?my_file_charset:system_par.network_data_charset;
		
		file_name			=f.getAbsolutePath();
		last_modified_time	=f.lastModified();

		if(file_name.indexOf(system_par.temporary_file_par.temporary_root_directory_name)!=0)
			compress_file_name	=null;
		else{
			int dir_length=system_par.temporary_file_par.temporary_root_directory_name.length();
			String dir_name=system_par.temporary_file_par.temporary_compress_directory_name;
			compress_file_name=dir_name+file_name.substring(dir_length);
		}
		already_compress_file_flag	=false;
		response_content_type		="text/plain";
		
		int index_id;
		String str;
		
		do{
			if((index_id=file_name.lastIndexOf('.'))>=0)
				if((index_id=system_par.content_type_change_name.search(file_name.substring(index_id+1)))>=0)
					if((str=system_par.content_type_change_name.get_search_result(index_id,null))!=null)
						if((index_id=str.indexOf(":"))>=0)
							break;
			compress_file_name=null;
			return;
		}while(false);
		
		response_content_type=str.substring(index_id+1);
		str=str.substring(0,index_id).toLowerCase();
		
		switch(str){
		case "gzip":
			compress_file_name=null;
			already_compress_file_flag=true;
			break;
		default:
			try{
				long compress_length=Long.decode(str);
				if((compress_length<=0)||(f.length()<compress_length))
					compress_file_name=null;
			}catch(Exception e) {
				debug_information.println("Unknown compress_str:	",str);
				compress_file_name=null;
			}
			break;
		}
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
		last_modified_time	=(new Date()).getTime();
		already_compress_file_flag=false;
		
		file_charset		=system_par.network_data_charset;
		response_content_type=(my_response_content_type==null)?"text/plain":my_response_content_type;
	}
}
