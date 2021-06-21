package kernel_client_interface;

import kernel_common_class.debug_information;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_directory;
import kernel_part.part;

public class dispatch_buffer_request
{
	private static String download_data(engine_kernel ek,client_information ci)
	{
		String str;
		int render_id,part_id;

		if(ek.render_cont.renders==null){
			debug_information.println(
				"(ek.render_cont.renders==null) in do_buffer_dispatch of dispatch_system_request");
			return null;
		}
		if((str=ci.request_response.get_parameter("render"))==null){
			debug_information.println(
				"request render is null in do_buffer_dispatch of dispatch_system_request");
			return null;
		}
		
		if((render_id=Integer.decode(str))<0){
			debug_information.println(
				"render_id less than zero in do_buffer_dispatch of dispatch_system_request");
			return null;
		}
		if(render_id>=(ek.render_cont.renders.length)){
			debug_information.println(
				"render_id more than max value in do_buffer_dispatch of dispatch_system_request\t:\t",
				Integer.toString(render_id)+"/"+Integer.toString(ek.render_cont.renders.length));
			return null;
		}
		if(ek.render_cont.renders[render_id].parts==null){
			debug_information.println(
				"(ek.render_cont.renders[render_id].parts==null) in do_buffer_dispatch of dispatch_system_request");
			return null;
		}
		if((str=ci.request_response.get_parameter("part"))==null){
			debug_information.println(
				"request part is null in do_buffer_dispatch of dispatch_system_request");
			return null;
		}
		if((part_id=Integer.decode(str))<0){
			debug_information.println(
				"part_id less than zero in do_buffer_dispatch of dispatch_system_request");
			return null;
		}
		if(part_id>=ek.render_cont.renders[render_id].parts.length){
			debug_information.println(
				"part_id more than max value in do_buffer_dispatch of dispatch_system_request\t:\t",
				Integer.toString(part_id)+"/"+Integer.toString(ek.render_cont.renders[render_id].parts.length));
			return null;
		}

		part p=ek.render_cont.renders[render_id].parts[part_id];
		if(p==null){
			debug_information.println(
				"part object is null in do_buffer_dispatch of dispatch_system_request\t:\t",
				Integer.toString(render_id)+"/"+Integer.toString(part_id));
			return null;
		}
		if((str=ci.request_response.get_parameter("data_file"))==null){
			debug_information.println(
				"No data_file in do_buffer_dispatch of dispatch_system_request");
			return null;
		}
		return file_directory.part_file_directory(p,ek.system_par,ek.scene_par)+"mesh."+str+".gzip_binary";
	}
	static public String do_dispatch(int main_call_id,engine_kernel ek,client_information ci)
	{
		ci.statistics_client.register_system_call_execute_number(main_call_id,0);
		
		String str=ci.request_response.get_parameter("operation");
		str=(str==null)?"":(str.toLowerCase());
		
		switch(str){
		default:
			debug_information.println(
				"Wrong operation in do_buffer_dispatch of dispatch_system_request\t:\t",str);
			return null;
		case "buffer_package":
			if((str=ci.request_response.get_parameter("package"))==null) {
				debug_information.println(
					"Package is NULL in do_buffer_dispatch of dispatch_system_request");
				return null;
			}
			try{
				int index_id,package_id;
				if((index_id=str.indexOf('_'))<0)
					return null;
				if((package_id=Integer.decode(str.substring(index_id+1)))<0)
					return null;
				switch(Integer.decode(str.substring(0,index_id))){
				default:
				case 0:
					return ek.render_cont.system_part_package.package_file_name[package_id];
				case 1:
					return ek.render_cont.type_part_package.package_file_name[package_id];
				case 2:
					return ek.render_cont.scene_part_package.package_file_name[package_id];
				}
			}catch(Exception e) {
				debug_information.println(
					"Wrong package in do_buffer_dispatch of dispatch_system_request\t:\t",
					str+"\t"+e.toString());
				e.printStackTrace();
				return null;
			}
		case "buffer_data":
			str=download_data(ek,ci);
			return str;
		}
	}
}