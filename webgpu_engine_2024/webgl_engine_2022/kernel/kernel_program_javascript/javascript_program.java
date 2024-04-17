package kernel_program_javascript;

import java.io.File;

import kernel_engine.engine_call_result;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_common_class.http_modify_string;
import kernel_common_class.class_file_reader;
import kernel_common_class.common_reader;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;

public class javascript_program
{
	private String default_fetch_parameter_filename,default_draw_process_bar_filename;
	private long last_modified_time;
	
	private static final String javascript_file_name[]=new String[] 
	{
		"call_server.js",			"camera.js",				"collector_loader.js",			"component_location.js",
		"component_render.js",		"computer.js",				"download_vertex_data.js",		"draw_scene.js",
		"event_listener.js",		"init_ids.js",				"modifier_time.js",				"operate_component.js",	
		"pickup.js",				"process_bar.js",			"render_main.js",				"render.js",
		"request_create_engine.js",	"request_render_data.js",	"system_buffer.js",				"webgpu.js"
	};
	
	public void destroy()
	{
	}	
	public javascript_program(system_parameter system_par)
	{
		default_fetch_parameter_filename =system_par.default_parameter_directory;
		default_fetch_parameter_filename+="network_parameter/fetch_parameter.txt";
		default_fetch_parameter_filename =file_reader.separator(default_fetch_parameter_filename);
		
		long t1=last_modified_time=new File(default_fetch_parameter_filename).lastModified();
		
		default_draw_process_bar_filename =system_par.default_parameter_directory;
		default_draw_process_bar_filename+="javascript_program/draw_process_bar.txt";
		default_draw_process_bar_filename =file_reader.separator(default_draw_process_bar_filename);
		
		long t2=new File(default_draw_process_bar_filename).lastModified();
		
		last_modified_time=(t1>t2)?t1:t2;
		
		for(int i=0,ni=javascript_file_name.length;i<ni;i++) {
			common_reader cr=class_file_reader.get_reader(javascript_file_name[i],
				getClass(),system_par.js_class_charset,system_par.js_jar_file_charset);
			if(cr==null)
				continue;
			if(cr.error_flag()){
				cr.close();
				continue;
			}
			if(cr.lastModified_time>last_modified_time)
				last_modified_time=cr.lastModified_time;
			cr.close();
		}
	}
	
	public engine_call_result create(
			client_request_response request_response,system_parameter system_par)
	{
		String function_date;
		if((function_date=request_response.get_parameter("function_date"))!=null)
			if(Long.parseLong(function_date)!=last_modified_time)
				function_date=null;
		
		if(function_date==null){
			String url=request_response.implementor.get_url();
			url+="?channel=javascript&function_date="+last_modified_time;
			request_response.implementor.redirect_url(url,system_par.system_cors_string);
			return null;
		}

		String request_modified_str;
		if((request_modified_str=request_response.implementor.get_header("If-Modified-Since"))!=null)
			if(http_modify_string.parse(request_modified_str)>=last_modified_time){
				request_response.implementor.response_not_modify(
					"javascript_program response_not_modify()",system_par.system_cors_string);
				return null;
			}
		
		String str[]=new String[]
		{
			"export var main=async function(my_canvas,my_create_parameter,user_process_bar_function)",
			"{"
		};
		for(int i=0,ni=str.length;i<ni;i++)
			request_response.println(str[i]);
		
		request_response.println("	var default_fetch_parameter=");
		file_reader fr=new file_reader(default_fetch_parameter_filename,system_par.local_data_charset);
		fr.get_text(request_response,"	");
		request_response.println();
		fr.close();
		
		request_response.println("	var default_user_process_bar_function=");
		fr=new file_reader(default_draw_process_bar_filename,system_par.local_data_charset);
		fr.get_text(request_response,"	");
		request_response.println();
		fr.close();

		for(int i=0,ni=javascript_file_name.length;i<ni;i++) {
			common_reader cr=class_file_reader.get_reader(javascript_file_name[i],
					getClass(),system_par.js_class_charset,system_par.js_jar_file_charset);
			if(cr==null){
				debug_information.println("Javascript file can not be opened!	",javascript_file_name[i]);
				System.exit(0);
				continue;
			}	
			if(cr.error_flag()){
				cr.close();
				debug_information.println("Javascript file error!	",javascript_file_name[i]);
				System.exit(0);
				continue;
			}
			cr.get_text("\t",request_response);
			cr.close();
		}
		
		str=new String[]{
				"	return await render_main(my_canvas,my_create_parameter,",
				"				(typeof(user_process_bar_function)==\"function\")",
				"					?user_process_bar_function",
				"					:default_user_process_bar_function,",
				"				\""	+request_response.implementor.get_url()+"\",",
				"				default_fetch_parameter,"+
									system_par.create_engine_sleep_time_length_scale+","+
									system_par.create_engine_sleep_time_length		+","+
									system_par.create_engine_max_sleep_time_length	+");",
				"};"
		};
		for(int i=0,ni=str.length;i<ni;i++)
			request_response.println(str[i]);

		return new engine_call_result(system_par.system_cors_string,
							last_modified_time,"application/javascript");
	}
}
