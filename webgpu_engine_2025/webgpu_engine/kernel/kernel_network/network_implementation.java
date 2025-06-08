package kernel_network;

import java.io.InputStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kernel_common_class.http_date_string;
import kernel_common_class.debug_information;

public class network_implementation
{
	//request
	private HttpServletRequest	request;
	private HttpServletResponse response;
	
	private String request_charset,client_id;
	
	public network_implementation(HttpServletRequest my_request,HttpServletResponse	my_response)
	{
		request			=my_request;
		response		=my_response;

		if((request_charset=request.getCharacterEncoding())==null)
			request_charset=network_implementation_default_parameter.network_request_charset;
		try {			
			request.setCharacterEncoding(request_charset);
		}catch(Exception e) {
			;
		}
		if((client_id=request.getRemoteAddr())==null)
			client_id="NoRemoteAddr";
	}
	static private void print_error(String front_msg,Exception e,String end_msg_1,String end_msg_2)
	{
		debug_information.println();
		debug_information.println(front_msg,e.toString());
		debug_information.println(end_msg_1,end_msg_2);
		e.printStackTrace();
		return;
	}
	public void set_status_code(int new_code)
	{
		this.response.setStatus(new_code);
	}
	public String get_header(String name)
	{
		return request.getHeader(name);
	}
	public void set_header(String name,String value)
	{
		response.setHeader(name,value);
	}
	public String get_parameter(String name)
	{
		String str;
		if((str=request.getParameter(name))!=null)
			try{
				return new String(str.getBytes(request_charset));
			}catch(Exception e){
				print_error("Error in get_parameter\t",e,"Client_id:"+client_id,"parameter name is "+name);
			}
		return null;
	}
	public String get_url()
	{
		return request.getRequestURL().toString();
	}
	public String get_request_charset()
	{
		return request_charset;
	}
	public String get_client_id()
	{
		return client_id;
	}
	public void redirect_url(String url)
	{
		response.setHeader("Access-Control-Allow-Origin","*");
		try{
			response.sendRedirect(url);
		}catch(Exception e){
			print_error("Error in sendRedirect\t",e,"URL:\t",url);
		}
	}
	public void response_not_modify(String error_msg)
	{
		response.setHeader("Access-Control-Allow-Origin","*");
		try{	
			response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
		}catch(Exception e){
			print_error("Error in response_not_modify\t",e,"Client_id:"+client_id,error_msg);
		}
	}
	public void set_response_http_header(String response_charset,String content_type,
			String compress_header,long last_time,long max_time_length,http_date_string http_date_str)
	{
		response.setCharacterEncoding(response_charset);
		response.setContentType(content_type);
		if(compress_header!=null)
			response.setHeader("Content-Encoding",compress_header);

		if(last_time<=0)
			response.setHeader("Cache-Control","no-store");
		else{
			response.setHeader("Cache-Control","public, max-age="+max_time_length);
			response.setHeader("Last-Modified",http_date_str.date_string(last_time));
		}
		
		response.setHeader("Access-Control-Allow-Origin","*");
	}
	public boolean response_binary_data(String error_msg,byte data_buf[],int length)
	{
		try{
			response.getOutputStream().write(data_buf,0,length);
			return false;
		}catch(Exception e){
			print_error("response_binary_data fail:\t",e,"Client_id:"+client_id,error_msg);
			return true;
		}
	}
	public void terminate_response_binary_data(String error_msg)
	{
		try{
			response.getOutputStream().flush();
		}catch(Exception e){
			print_error("terminate_response_binary_data:output_stream.flush() fail:\t",
						e,"Client_id:"+client_id,error_msg);
		}
		try{
			response.flushBuffer();
		}catch(Exception e){
			print_error("terminate_response_binary_data: response.flushBuffer() fail:\t",
					e,"Client_id:"+client_id+"\n",error_msg);
		}
	}
	public InputStream get_content_stream()
	{
		try{
			return request.getInputStream();
		}catch(Exception e) {
			print_error("get_input_stream() fail:\t",e,"Client_id:"+client_id,"");
			return null;
		}
	}
	public void set_option_http_header(long access_control_max_age)
	{
		response.setHeader("Access-Control-Allow-Origin",	request.getHeader("Origin"));
		response.setHeader("Access-Control-Max-Age",		Long.toString(access_control_max_age));
		response.setHeader("Access-Control-Allow-Methods",	request.getHeader("Access-Control-Request-Method"));
		response.setHeader("Access-Control-Allow-Headers",	request.getHeader("Access-Control-Request-Headers"));
	}
}
