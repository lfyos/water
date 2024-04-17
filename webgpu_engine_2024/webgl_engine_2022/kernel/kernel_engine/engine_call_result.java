package kernel_engine;

import java.nio.charset.Charset;

public class engine_call_result
{
	public String file_name,charset_file_name,compress_file_name;
	public long last_modified_time;
	public boolean already_compress_file_flag;
	
	public String file_charset,cors_string,response_content_type;
	
	
	public engine_call_result(caculate_charset_compress_file_name cccfn,
			String my_file_charset,String my_cors_string)
	{
		file_name					=cccfn.file_name;
		charset_file_name			=cccfn.charset_file_name;
		compress_file_name			=cccfn.compress_file_name;
		last_modified_time			=cccfn.last_modified_time;
		already_compress_file_flag	=cccfn.already_compress_file_flag;
		
		file_charset				=(my_file_charset==null)
						?Charset.defaultCharset().name():new String(my_file_charset);
		cors_string					=(my_cors_string==null)?"*":new String(my_cors_string);
		response_content_type		=cccfn.response_content_type;
	}
	public engine_call_result(String my_cors_string,long my_last_modified_time,String my_response_content_type)
	{
		file_name			=null;

		charset_file_name	=null;
		compress_file_name	=null;
		
		last_modified_time	=my_last_modified_time;
		already_compress_file_flag	=false;
		
		file_charset		=null;
		cors_string			=(my_cors_string==null)?"*":new String(my_cors_string);
		response_content_type=(my_response_content_type==null)?"text/plain":new String(my_response_content_type);
	}
	public engine_call_result(String my_cors_string,String my_response_content_type)
	{
		file_name			=null;
		
		charset_file_name	=null;
		compress_file_name	=null;
		
		last_modified_time	=-1;
		already_compress_file_flag=false;
		
		file_charset		=null;
		cors_string			=(my_cors_string==null)?"*":new String(my_cors_string);
		response_content_type=(my_response_content_type==null)?"text/plain":new String(my_response_content_type);
	}
}
