package kernel_client_interface;

import java.io.File;

import kernel_scene.scene_kernel;
import kernel_scene.scene_call_result;
import kernel_scene.client_information;
import kernel_common_class.debug_information;

public class dispatch_request_main
{
	static private String[] get_scene_result_routine(
			long delay_time_length,scene_kernel sk,client_information ci)
	{
		String str;
		if((str=ci.request_response.get_parameter("command"))==null) {
			debug_information.println(
				"command string is null in do_get_scene_result_routine() of dispatch_request_main");
			return null;
		}
		if(sk.component_cont.root_component==null){
			debug_information.println(
				"(sk.component_cont.root_component==null) in do_get_scene_result_routine of dispatch_request_main");
			return null;
		}
		switch(str){
		case "creation":
			dispatch_create_scene_request.do_dispatch(sk,ci);
			return null;
		case "initialization":
			return new String[] {
				sk.scene_par.scene_temporary_directory_name+"initialization.gzip_js",
				sk.system_par.network_data_charset
			};
		case "buffer":
			str=dispatch_buffer_request.do_dispatch(sk,ci);
			return (str==null)?null:(new String[] {str,sk.system_par.local_data_charset});
		case "render":
			return dispatch_render_request.do_dispatch(sk,ci);
		case "part":
			return dispatch_part_request.do_dispatch(sk,ci);
		case "component":
			return dispatch_component_request.do_dispatch(delay_time_length,sk,ci);
		case "modifier":
			return dispatch_modifier_request.do_dispatch(sk, ci);
		case "collector":
			return dispatch_collector_request.do_dispatch(sk, ci);
		case "information":
			return dispatch_information_request.do_dispatch(sk,ci);
		default:
			debug_information.println(
				"Unknown command in get_scene_result_routine() of dispatch_request_main\t:\t",str);
			return null;
		}
	}
	static public scene_call_result get_scene_result(
			long delay_time_length,scene_kernel sk,client_information ci)
	{
		sk.current_time.refresh_timer();
		String file_name[]=get_scene_result_routine(delay_time_length,sk,ci);
		sk.test_and_caculate_scene_component_flag();

		if(file_name==null)
			return new scene_call_result();
		if(file_name.length<=0)
			return new scene_call_result();
		if(file_name[0]==null)
			return new scene_call_result();

		ci.request_response.reset();
		
		File f=new File(file_name[0]);
		
		if(!(f.exists())){
			debug_information.println(
				"create scene_call_result error in get_scene_result,file NOT exist\t",
				f.getAbsolutePath());
			return null;
		}
		if(!(f.isFile())){
			debug_information.println(
				"create scene_call_result error in get_scene_result,file NOT normal file\t",
				f.getAbsolutePath());
			return null;
		}
		if(!(f.canRead())){
			debug_information.println(
				"create scene_call_result error in get_scene_result,file CAN NOT read\t",
				f.getAbsolutePath());
			return null;
		}
		
		String url,file_charset=null;
		if(file_name.length>1)
			if(file_name[1]!=null)
				if((file_name[1]=file_name[1].trim()).length()>0)
					file_charset=file_name[1];
		if(file_charset==null)
			file_charset=sk.system_par.network_data_charset;
		
		if((url=ci.caculate_file_proxy_url(file_name[0],file_charset,sk.system_par))!=null){
			ci.request_response.implementor.redirect_url(url);
			return null;
		}

		scene_call_result ret_val=new scene_call_result(f,sk.system_par);
		ci.request_response.set_content_type(
			(ret_val.content_type==null)?"text/plain":ret_val.content_type.content_str);
		ci.request_response.set_charset(file_charset);
		
		return ret_val;
	}
}
