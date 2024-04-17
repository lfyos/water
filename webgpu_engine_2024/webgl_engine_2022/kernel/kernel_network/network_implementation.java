package kernel_network;

import java.io.InputStream;

public interface network_implementation 
{
	public void set_status_code(int new_code);
	public String get_header(String name);
	public void set_header(String name,String value);
	
	public String get_parameter(String name);
	public String get_request_charset();
	
	public String get_url();
	public String get_client_id();
	
	public void redirect_url(String url,String cors_string);
	

	public void response_not_modify(String error_msg,String cors_string);
	public void set_response_http_header(
					String server_response_charset,String content_type,
					String compress_response_header,
					String cors_string,long last_time,long max_time_length);
	
	public boolean response_binary_data(String error_msg,byte data_buf[],int length);
	public void terminate_response_binary_data(String error_msg);
	
	public InputStream get_content_stream();
}
