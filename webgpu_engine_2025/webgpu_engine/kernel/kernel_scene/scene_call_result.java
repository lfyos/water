package kernel_scene;

import java.io.File;

import kernel_common_class.debug_information;

public class scene_call_result
{
	public String original_file_name,result_file_name,compress_file_name;
	public boolean already_compress_file_flag;
	public long last_modified_time;
	
	public scene_call_result(File f,boolean cache_flag,system_parameter system_par)
	{
		original_file_name			=f.getAbsolutePath();
		result_file_name			=null;
		compress_file_name			=null;
		last_modified_time			=cache_flag?f.lastModified():-1;
		already_compress_file_flag	=false;

		String content_str[];
		if((content_str=system_par.search_file_content_type(original_file_name))==null)
			return;
		result_file_name=content_str[2];
		switch(content_str[0]) {
		case "gzip":
			already_compress_file_flag=true;
			return;
		case "link":
			result_file_name=null;
			return;
		default:
			long compress_length;
			try{
				compress_length=Long.decode(content_str[0]);
			}catch(Exception e){
				debug_information.println("Unknown compress_str:	",	content_str[0]);
				debug_information.println("			",					f.getAbsolutePath());
				return;
			}
			if((compress_length<=0)||(f.length()<=compress_length))
				return;
			if(original_file_name.indexOf(system_par.temporary_file_par.temporary_root_directory_name)==0){
				int dir_length=system_par.temporary_file_par.temporary_root_directory_name.length();
				String dir_name=system_par.temporary_file_par.temporary_compress_directory_name;
				compress_file_name=dir_name+original_file_name.substring(dir_length);
			}
			return;
		}
	}
	public scene_call_result(long my_last_modified_time)
	{
		original_file_name	=null;
		result_file_name	=null;
		compress_file_name	=null;
		last_modified_time	=my_last_modified_time;
		already_compress_file_flag	=false;
	}
	public scene_call_result()
	{
		original_file_name	=null;
		result_file_name	=null;
		compress_file_name	=null;
		last_modified_time	=-1;
		already_compress_file_flag=false;
	}
}
