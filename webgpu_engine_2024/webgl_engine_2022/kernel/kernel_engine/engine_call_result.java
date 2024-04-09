package kernel_engine;

import java.nio.charset.Charset;

public class engine_call_result
{
	public String file_name,file_charset,charset_file_name,compress_file_name,date_string,cors_string;
	
	public engine_call_result(
			String my_file_name,		String my_file_charset,
			String my_charset_file_name,String my_compress_file_name,	
			String my_date_string,		String my_cors_string)
	{
		file_name			=(my_file_name==null)			?null:new String(my_file_name);
		file_charset		=(my_file_charset==null)		?Charset.defaultCharset().name():new String(my_file_charset);
		charset_file_name	=(my_charset_file_name==null)	?null:new String(my_charset_file_name);
		compress_file_name	=(my_compress_file_name==null)	?null:new String(my_compress_file_name);
		date_string			=(my_date_string ==null)		?null:new String(my_date_string);
		cors_string			=(my_cors_string==null)			?"*":new String(my_cors_string);
	}
}
