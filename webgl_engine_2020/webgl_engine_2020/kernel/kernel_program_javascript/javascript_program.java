package kernel_program_javascript;

import java.util.Date;

import kernel_engine.engine_call_result;
import kernel_common_class.http_modify_string;
import kernel_common_class.class_file_reader;
import kernel_common_class.common_reader;
import kernel_network.client_request_response;

public class javascript_program
{
	private String javascript_file_name[];
	private long last_modified_time;
	
	public void destroy()
	{
		javascript_file_name=null;
	}
	
	public javascript_program()
	{
		javascript_file_name=new String[] {
			"buffer_object.js",	"camera.js",			"component_location.js",	"component_render.js",
			"computer.js",		"deviceorientation.js",	"event_listener.js",		"framebuffer.js",
			"pickup.js",		"program.js",			"modifier_time.js",			"render_main.js",
			"render.js",		"uniform_block.js",		"utility.js"
		};
		
		last_modified_time=0;
		
		common_reader cr;
		for(int i=0,ni=javascript_file_name.length;i<ni;i++)
			if((cr=class_file_reader.get_reader(javascript_file_name[i],getClass()))!=null) {
				if(cr.lastModified_time>last_modified_time)
					last_modified_time=cr.lastModified_time;
				cr.close();
			}
	}
	
	public engine_call_result create(client_request_response request_response,
			String max_age_string,double create_engine_sleep_time_length_scale,
			long create_engine_sleep_time_length,long create_engine_max_sleep_time_length)
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
			url+="?function_name="+function_name+"&function_date="+last_modified_str;
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
					"javascript_program response_not_modify()",file_modified_str,max_age_string);
				return null;
			}
		}
		String str[]=new String[]{
				"function "+function_name+"(my_canvas,my_user_name,my_pass_word,my_language_name,",
				"		scene_name,link_name,initialization_parameter,initialization_function)"	,
				"{"
		};
		for(int i=0,ni=str.length;i<ni;i++)
			request_response.println(str[i]);
		
		common_reader cr;
		for(int i=0,ni=javascript_file_name.length;i<ni;i++)
			if((cr=class_file_reader.get_reader(javascript_file_name[i],getClass()))!=null){
				cr.get_text("\t",request_response);
				cr.close();
			}
		
		str=new String[]{
				"	return render_main("
							+create_engine_sleep_time_length_scale	+","
							+create_engine_sleep_time_length		+","
							+create_engine_max_sleep_time_length	+",my_canvas,"			,
				"		\""	+request_response.implementor.get_url()	+"\","					,
				"		my_user_name,my_pass_word,my_language_name,scene_name,link_name,"	,
				"		initialization_parameter,initialization_function);"					,
				"};"
		};
		
		for(int i=0,ni=str.length;i<ni;i++)
			request_response.println(str[i]);
		
		request_response.response_content_type="application/javascript";
		
		return new engine_call_result(null,null,null,null,file_modified_str,"*");
	}
}
