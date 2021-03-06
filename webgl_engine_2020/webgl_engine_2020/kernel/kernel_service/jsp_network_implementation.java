package kernel_service;

import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_network.network_implementation;

public class jsp_network_implementation implements network_implementation
{
	//request

	private javax.servlet.http.HttpServletRequest	request;
	private javax.servlet.http.HttpServletResponse 	response;
	private ServletOutputStream 					output_stream;
	
	private String request_charset,client_id,application_directory;
	
	public jsp_network_implementation(
			javax.servlet.http.HttpServletRequest	my_request,
			javax.servlet.http.HttpServletResponse	my_response,
			javax.servlet.ServletContext			my_application)
	{
		request			=my_request;
		response		=my_response;
		output_stream	=null;

		if((request_charset=request.getCharacterEncoding())==null)
			request_charset="UTF-8";
		try {			
			request.setCharacterEncoding(request_charset);
		}catch(Exception e) {
			;
		}
		
		String str;
		client_id =	   (((str=request.getRemoteAddr())==null)?"NoRemoteAddr":(str.trim()));
		client_id+="/"+(((str=request.getRemoteUser())==null)?"NoRemoteUser":(str.trim()));
		client_id+="/"+(((str=request.getRemoteHost())==null)?"NoRemoteHost":(str.trim()));
		
		if((application_directory=my_application.getRealPath(""))==null)
			application_directory="";
		else {
			int index_id;
			application_directory=file_reader.separator(application_directory);
			if((index_id=application_directory.lastIndexOf(File.separatorChar))>=0)
				application_directory=application_directory.substring(0,index_id+1);
		}
	}
	static private void print_error(boolean print_stack_trace_flag,
			String front_msg,Exception e,String end_msg_1,String end_msg_2)
	{
		debug_information.println();
		debug_information.println(front_msg,e.toString());
		debug_information.println(end_msg_1,end_msg_2);
		if(print_stack_trace_flag)
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
				print_error(true,"Error in get_parameter\t",e,
						"Client_id:"+get_client_id(),"parameter name is "+name);
			}
		return null;
	}
	public String get_request_charset()
	{
		return request_charset;
	}
	
	public String get_url()
	{
		return request.getRequestURL().toString();
	}
	public String get_client_id()
	{
		return client_id;
	}
	public String get_application_directory()
	{
		return application_directory;
	}
	public void redirect_url(String url,String cors_string)
	{
		response.setHeader("Access-Control-Allow-Origin",cors_string);
		try{
			response.sendRedirect(url);
		}catch(Exception e){
			print_error(true,"Error in sendRedirect\t",e,"URL:\t",url);
		}
	}
	public void response_not_modify(String error_msg,String date_string,String max_age_string)
	{
		try{
			response.setHeader("Cache-Control","public");
			response.setHeader("Last-Modified",date_string);
			response.addHeader("Cache-Control","max-age="+max_age_string);
			response.sendError(javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED);
		}catch(Exception e){
			print_error(true,"Error in response_not_modify\t",e,"Client_id:"+get_client_id(),error_msg);
		}
	}
	public void set_response_http_header(
			String error_msg,String server_response_charset,String content_type,
			String compress_response_header,String cors_string,String date_string,String max_age_string)
	{
		response.setCharacterEncoding(server_response_charset);
		response.setContentType(content_type);

		if(compress_response_header!=null)
			response.setHeader("Content-Encoding",compress_response_header);
		response.setHeader("Access-Control-Allow-Origin",cors_string);
		if(date_string==null)
			response.setHeader("Cache-Control","no-store");
		else {
			response.setHeader("Last-Modified",date_string);
			response.setHeader("Cache-Control","public");
			response.addHeader("Cache-Control","max-age="+max_age_string);
		}
		try{
			output_stream=response.getOutputStream();
		}catch(Exception e){
			output_stream=null;
			print_error(true,"output_stream=response.getOutputStream() fail:\t",
					e,"Client_id:"+get_client_id(),error_msg);
		}
	}
	public boolean response_binary_data(String error_msg,byte data_buf[],int length)
	{
		if(output_stream==null)
			debug_information.println("response_binary_data find output_stream==null");
		else
			try{
				output_stream.write(data_buf,0,length);
				return false;
			}catch(Exception e){
				print_error(false,"response_binary_data fail:\t",
						e,"Client_id:"+get_client_id()+"\n",error_msg);
			}
		return true;
	}
	public void terminate_response_binary_data(String error_msg)
	{
		if(output_stream==null)
			debug_information.println("terminate_response_binary_data find output_stream==null");
		else
			try{
				output_stream.flush();
			}catch(Exception e){
				print_error(false,"terminate_response_binary_data:output_stream.flush() fail:\t",
						e,"Client_id:"+get_client_id(),error_msg);
			}
		try{
			response.flushBuffer();
		}catch(Exception e){
			print_error(false,
					"terminate_response_binary_data: response.flushBuffer() fail:\t",e,"Client_id:"+get_client_id()+"\n",error_msg);
		}
	}
	public InputStream get_content_stream()
	{
		try{
			return request.getInputStream();
		}catch(Exception e) {
			print_error(true,"get_input_stream() fail:\t",e,"Client_id:"+get_client_id(),"");
			return null;
		}
	};
}
