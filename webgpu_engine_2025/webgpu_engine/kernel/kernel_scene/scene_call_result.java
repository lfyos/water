package kernel_scene;

import java.io.File;

public class scene_call_result
{
	public String original_file_name,result_file_name;
	public boolean already_compress_file_flag;
	public long last_modified_time;
	
	public search_file_content_type_result content_type;
	
	public scene_call_result(File f,system_parameter system_par)
	{
		original_file_name			=f.getAbsolutePath();
		result_file_name			=original_file_name;
		last_modified_time			=f.lastModified();
		already_compress_file_flag	=false;

		if((content_type=system_par.search_file_content_type(original_file_name))!=null){
			result_file_name=content_type.path_name;
			last_modified_time=new File(result_file_name).lastModified();
			if(content_type.zip_link_str.compareTo("gzip")==0)
				already_compress_file_flag=true;
		}
	}
	public scene_call_result(long my_last_modified_time)
	{
		original_file_name	=null;
		result_file_name	=null;
		content_type		=null;
		last_modified_time	=my_last_modified_time;
		already_compress_file_flag	=false;
	}
	public scene_call_result()
	{
		original_file_name	=null;
		result_file_name	=null;
		content_type		=null;
		last_modified_time	=-1;
		already_compress_file_flag=false;
	}
}
