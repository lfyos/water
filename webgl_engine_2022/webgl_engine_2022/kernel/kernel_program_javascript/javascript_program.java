package kernel_program_javascript;

import java.util.Date;

import kernel_engine.engine_call_result;
import kernel_engine.system_parameter;
import kernel_common_class.http_modify_string;
import kernel_common_class.class_file_reader;
import kernel_common_class.common_reader;
import kernel_common_class.debug_information;
import kernel_network.client_request_response;

public class javascript_program
{
	private long last_modified_time;
	
	private static final String javascript_file_name[]=new String[] 
	{
		"call_server.js",			"camera.js",				"collector_loader.js",		"component_init_function.js",
		"component_location.js",	"component_render.js",		"computer.js",				"download_vertex_data.js",
		"draw_scene.js",			"event_listener.js",		"init_ids.js",				"init_system_bindgroup.js",
		"modifier_time.js",			"operate_component.js",		"pickup.js",				"process_bar.js",
		"render_driver_init.js",	"render_main.js",			"render.js",				"request_create_engine.js",
		"request_render_data.js",	"system_buffer.js",			"utility.js",				"webgpu.js"
	};
	
	public void destroy()
	{
	}	
	public javascript_program(system_parameter system_par)
	{
		last_modified_time=0;
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
		String function_name;
		if((function_name=request_response.get_parameter("function_name"))==null)
			function_name="construct_render_object";
		else if(function_name.compareTo("construct_render_object_routine")==0)
			function_name="construct_render_object";

		String last_modified_str=Long.toString(last_modified_time);
		
		String function_date;
		if((function_date=request_response.get_parameter("function_date"))!=null)
			if(function_date.compareTo(last_modified_str)!=0)
				function_date=null;
		if(function_date==null){
			String url=request_response.implementor.get_url();
			url+="?channel=javascript&function_name="+function_name+"&function_date="+last_modified_str;
			request_response.implementor.redirect_url(url,"*");
			return null;
		}

		String request_modified_str=request_response.implementor.get_header("If-Modified-Since");
		String file_modified_str=http_modify_string.string(new Date(last_modified_time));
		if(request_modified_str!=null){
			long current_time=http_modify_string.parse(file_modified_str);
			long request_time=http_modify_string.parse(request_modified_str);
			if((request_time>=0)&&(current_time>=0)&&(request_time>=current_time)){
				request_response.implementor.response_not_modify(
					"javascript_program response_not_modify()",file_modified_str,
					Long.toString(system_par.file_buffer_expire_time_length));
				return null;
			}
		}
		String str[]=new String[]{
				"async function "+function_name+"(my_canvas,my_user_name,my_pass_word,my_language_name,",
				"		scene_name,link_name,initialization_parameter,progress_bar_function)",
				"{"
		};
		for(int i=0,ni=str.length;i<ni;i++)
			request_response.println(str[i]);

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
				"	var ret_val=await render_main("
							+system_par.create_engine_sleep_time_length_scale	+","
							+system_par.create_engine_sleep_time_length			+","
							+system_par.create_engine_max_sleep_time_length		+",my_canvas,"		,
				"		\""	+request_response.implementor.get_url()	+"\","							,
				"		my_user_name,my_pass_word,my_language_name,scene_name,link_name,"			,
				"		initialization_parameter,progress_bar_function);"	,
				"	return ret_val;",
				"};"
		};
		
		for(int i=0,ni=str.length;i<ni;i++)
			request_response.println(str[i]);
		
		request_response.response_content_type="application/javascript";

		return new engine_call_result(null,null,null,null,file_modified_str,"*");
	}
}
