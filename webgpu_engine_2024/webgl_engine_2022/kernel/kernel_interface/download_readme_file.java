package kernel_interface;

import java.util.Date;

import kernel_common_class.class_file_reader;
import kernel_common_class.common_reader;
import kernel_common_class.http_modify_string;
import kernel_common_class.debug_information;
import kernel_engine.engine_call_result;
import kernel_network.client_request_response;
import kernel_file_manager.file_reader;

public class download_readme_file
{
	private static engine_call_result  download_driver_configure(
			client_request_response request_response,String shader_file_name,
			String shader_file_system_charset,String file_download_cors_string,String max_age_string)
	{
		file_reader f=new file_reader(shader_file_name,shader_file_system_charset);
		String last_modified_str=Long.toString(f.lastModified_time);
		
		String file_date;
		if((file_date=request_response.get_parameter("date"))!=null)
			if(file_date.compareTo(last_modified_str)!=0)
				file_date=null;
		
		if(file_date==null){
			f.close();
			String url=request_response.implementor.get_url();
			url+="?channel=readme&date="+last_modified_str;
			request_response.implementor.redirect_url(url,file_download_cors_string);
			return null;
		}

		String request_modified_str=request_response.implementor.get_header("If-Modified-Since");
		String file_modified_str=http_modify_string.string(new Date(f.lastModified_time));
		if(request_modified_str!=null){
			long current_time=http_modify_string.parse(file_modified_str);
			long request_time=http_modify_string.parse(request_modified_str);
			if((request_time>=0)&&(current_time>=0)&&(request_time>=current_time)){
				f.close();
				request_response.implementor.response_not_modify(
					"download_readme_file response_not_modify",file_modified_str,max_age_string);
				return null;
			}
		}
		f.get_text(request_response);
		f.close();

		return new engine_call_result(null,null,null,null,
				file_modified_str,file_download_cors_string);
	}
	
	public static engine_call_result  download_driver_readme(
			client_request_response request_response,String shader_file_name,
			String shader_file_system_charset,String file_download_cors_string,String max_age_string,
			String class_charset,String jar_file_charset)
	{
		String package_name,driver_name;
		if((package_name=request_response.get_parameter("package"))==null)
			return download_driver_configure(request_response,
					shader_file_name,shader_file_system_charset,file_download_cors_string,max_age_string);
		
		if((driver_name=request_response.get_parameter("driver"))==null)
			driver_name="extended_render_driver";
		String path_name=package_name+"."+driver_name;
	
		Class<?>my_class;
		try{
			my_class=Class.forName(path_name);
		}catch(Exception e){
			debug_information.println(
					"Find render driver class fail in download_readme_file:		",path_name);
			debug_information.println(e.toString());
			e.printStackTrace();
			return null;
		}

		common_reader reader=class_file_reader.get_reader(
				"readme.txt",my_class,class_charset,jar_file_charset);
		if(reader==null) {
			debug_information.println(
					"Create common_reader fail in download_readme_file:		",path_name);
			return null;
		}
		if(reader.error_flag()) {
			reader.close();
			debug_information.println(
					"Common_reader error in download_readme_file:		",path_name);
			return null;
		}
		
		String last_modified_str=Long.toString(reader.lastModified_time);
		
		String package_date;
		if((package_date=request_response.get_parameter("date"))!=null)
			if(package_date.compareTo(last_modified_str)!=0)
				package_date=null;
		
		if(package_date==null){
			reader.close();
			
			String url=request_response.implementor.get_url();
			url+="?channel=readme&package="+package_name;
			url+="&driver="+driver_name+"&date="+last_modified_str;
			request_response.implementor.redirect_url(url,file_download_cors_string);
			
			return null;
		}

		String request_modified_str=request_response.implementor.get_header("If-Modified-Since");
		String file_modified_str=http_modify_string.string(new Date(reader.lastModified_time));

		if(request_modified_str!=null){
			long request_time=http_modify_string.parse(request_modified_str);
			long current_time=http_modify_string.parse(file_modified_str);
			if((request_time>=0)&&(current_time>=0)&&(request_time>=current_time)){
				reader.close();
				
				request_response.implementor.response_not_modify(
						"download_readme_file response_not_modify",
						file_modified_str,max_age_string);
				
				return null;
			}
		}
		reader.get_text(request_response);
		reader.close();
		
		return new engine_call_result(null,null,null,null,
				file_modified_str,file_download_cors_string);
	}
}
