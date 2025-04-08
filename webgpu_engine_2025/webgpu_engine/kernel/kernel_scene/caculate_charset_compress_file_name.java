package kernel_scene;

import java.io.File;

import kernel_common_class.debug_information;

public class caculate_charset_compress_file_name
{
	public String file_name;
	public long last_modified_time;
	
	public String charset_file_name,compress_file_name;
	
	public boolean already_compress_file_flag;
	
	public String response_content_type;

	public caculate_charset_compress_file_name(File f,system_parameter system_par)
	{
		file_name			=f.getAbsolutePath();
		last_modified_time	=f.lastModified();

		if(file_name.indexOf(system_par.temporary_file_par.temporary_root_directory_name)!=0){
			charset_file_name	=null;
			compress_file_name	=null;
		}else{
			int dir_length=system_par.temporary_file_par.temporary_root_directory_name.length();
			String dir_name=system_par.temporary_file_par.compress_temporary_root_directory_name;
			dir_name+=file_name.substring(dir_length);
			charset_file_name	=dir_name+".charset";
			compress_file_name	=dir_name+".compress";
		}
		already_compress_file_flag	=false;
		response_content_type		="text/plain";
		
		int index_id;
		String str,compress_str;
		
		do{
			if((index_id=file_name.lastIndexOf('.'))>=0)
				if((index_id=system_par.content_type_change_name.search(file_name.substring(index_id+1)))>=0)
					if((str=system_par.content_type_change_name.get_search_result(index_id,null))!=null)
						if((index_id=str.indexOf(":"))>=0)
							break;
			compress_file_name=null;
			return;
		}while(false);
		
		compress_str=str.substring(0,index_id).toLowerCase();
		str=str.substring(index_id+1);
		
		switch(compress_str) {
		case "gzip":
			compress_file_name=null;
			already_compress_file_flag=true;
			break;
		default:
			try{
				long compress_length=Long.decode(compress_str);
				if((compress_length<=0)||(f.length()<compress_length))
					compress_file_name=null;
			}catch(Exception e) {
				debug_information.println("Unknown compress_str:	",compress_str);
				compress_file_name=null;
			}
			break;
		}

		if((index_id=str.indexOf(":"))<0) {
			response_content_type=str.trim();
			charset_file_name=null;
		}else{
			response_content_type=str.substring(index_id+1);
			if(str.substring(0,index_id).compareTo("charset")!=0)
				charset_file_name=null;
		}
	}
}
