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
		case "termination":
			ci.request_response.request_time=0;
			ci.process_bar.touch_time=0;
			return null;
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
		
		String cors_string=ek.scene_par.scene_cors_string;
		long min_compress_response_length=ek.system_par.min_compress_response_length;
		long my_length	=ci.request_response.output_data_length;
		String compress_file_name="do_compress_flag";
		if((min_compress_response_length<=0)||(my_length<min_compress_response_length))
			compress_file_name=null;
		
		if(file_name==null)
			return new engine_call_result(null,null,null,compress_file_name,null,cors_string);
		if(file_name.length<=0)
			return new engine_call_result(null,null,null,compress_file_name,null,cors_string);
		if(file_name[0]==null)
			return new engine_call_result(null,null,null,compress_file_name,null,cors_string);
		
		ci.request_response.reset();
		
		if(file_name.length<=1)
			file_name=new String[] {file_name[0],null};
		if(file_name[1]==null)
			file_name[1]=Charset.defaultCharset().name();
		
		File f=new File(file_name[0]);
		
		if(!(f.exists())){
			debug_information.println("create engine_call_result error in get_engine_result,file NOT exist\t",f.getAbsolutePath());
			return new engine_call_result(null,null,null,null,null,cors_string);
		}
		if(!(f.isFile())){
			debug_information.println("create engine_call_result error in get_engine_result,file NOT normal file\t",f.getAbsolutePath());
			return new engine_call_result(null,null,null,null,null,cors_string);
		}
		if(!(f.canRead())){
			debug_information.println("create engine_call_result error in get_engine_result,file CAN NOT read\t",f.getAbsolutePath());
			return new engine_call_result(null,null,null,null,null,cors_string);
		}
		String my_url=ci.get_file_proxy_url(f,ek.system_par);
		if(my_url!=null){
			ci.request_response.implementor.redirect_url(my_url,ek.scene_par.scene_cors_string);
			return new engine_call_result(null,null,null,null,null,cors_string);
		}
		caculate_charset_compress_file_name cccfn=new caculate_charset_compress_file_name(f,ek.system_par);
		ci.request_response.response_content_type=cccfn.content_type_str;

		return new engine_call_result(cccfn.file_name,file_name[1],
			cccfn.charset_file_name,cccfn.compress_file_name,null,cors_string);
	}
}
