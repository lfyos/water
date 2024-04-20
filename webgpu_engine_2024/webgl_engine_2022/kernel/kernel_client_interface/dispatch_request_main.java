package kernel_client_interface;

import java.io.File;
import java.nio.charset.Charset;

import kernel_engine.engine_kernel;
import kernel_engine.engine_call_result;
import kernel_engine.caculate_charset_compress_file_name;
import kernel_engine.client_information;
import kernel_common_class.debug_information;

public class dispatch_request_main
{
	static private String[] get_engine_result_routine(
			long delay_time_length,engine_kernel ek,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("command"))==null) {
			debug_information.println(
				"command string is null in do_get_engine_result_routine() of dispatch_request_main");
			return null;
		}
		if(ek.component_cont.root_component==null){
			debug_information.println(
				"(ek.component_cont.root_component==null) in do_get_engine_result_routine of dispatch_request_main");
			return null;
		}
		switch(str){
		case "creation":
			dispatch_create_engine_request.do_dispatch(ek,ci);
			return null;
		case "initialization":
			return new String[] {
				ek.scene_par.scene_temporary_directory_name+"initialization.gzip_js",
				ek.system_par.network_data_charset
			};
		case "buffer":
			str=dispatch_buffer_request.do_dispatch(ek,ci);
			return (str==null)?null:(new String[] {str,ek.system_par.local_data_charset});
		case "render":
			return dispatch_render_request.do_dispatch(ek,ci);
		case "part":
			return dispatch_part_request.do_dispatch(ek,ci);
		case "component":
			return dispatch_component_request.do_dispatch(delay_time_length,ek,ci);
		case "modifier":
			return dispatch_modifier_request.do_dispatch(ek, ci);
		case "collector":
			return dispatch_collector_request.do_dispatch(ek, ci);
		case "information":
			return dispatch_information_request.do_dispatch(ek,ci);
		default:
			debug_information.println(
				"Unknown command in do_get_engine_result_routine() of dispatch_request_main\t:\t",str);
			return null;
		}
	}
	static public engine_call_result get_engine_result(long delay_time_length,engine_kernel ek,client_information ci)
	{
		ek.current_time.refresh_timer();
		String file_name[]=get_engine_result_routine(delay_time_length,ek,ci);
		ek.process_reset();

		String scene_cors_string	=ek.scene_par.scene_cors_string;
		String response_content_type=ci.request_response.response_content_type;
		
		if(file_name==null)
			return new engine_call_result(scene_cors_string,response_content_type);
		if(file_name.length<=0)
			return new engine_call_result(scene_cors_string,response_content_type);
		if(file_name[0]==null)
			return new engine_call_result(scene_cors_string,response_content_type);

		ci.request_response.reset();
		
		File f=new File(file_name[0]);
		
		if(!(f.exists())){
			debug_information.println(
				"create engine_call_result error in get_engine_result,file NOT exist\t",
				f.getAbsolutePath());
			return null;
		}
		if(!(f.isFile())){
			debug_information.println(
				"create engine_call_result error in get_engine_result,file NOT normal file\t",
				f.getAbsolutePath());
			return null;
		}
		if(!(f.canRead())){
			debug_information.println(
				"create engine_call_result error in get_engine_result,file CAN NOT read\t",
				f.getAbsolutePath());
			return null;
		}
		
		String url;
		if((url=ci.get_file_proxy_url(file_name[0],ek.system_par))!=null) {
			ci.request_response.implementor.redirect_url(url,ek.scene_par.scene_cors_string);
			return null;
		}

		caculate_charset_compress_file_name cccf;
		cccf=new caculate_charset_compress_file_name(f,ek.system_par);
		
		if(file_name.length>1)
			if(file_name[1]!=null)
				return new engine_call_result(cccf,	file_name[1],scene_cors_string);
		
		return new engine_call_result(cccf,Charset.defaultCharset().name(),scene_cors_string);
	}
}
