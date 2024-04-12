package kernel_client_interface;


import kernel_part.part;
import kernel_render.render;
import kernel_common_class.debug_information;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_directory;


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
		int render_number=ek.render_cont.renders.size();
		if(render_id>=render_number){
			debug_information.println(
				"render_id more than max value in do_buffer_dispatch of dispatch_system_request\t:\t",
				Integer.toString(render_id)+"/"+Integer.toString(render_number));
			return null;
		}
		render  r;
		if((r=ek.render_cont.renders.get(render_id))==null){
			debug_information.println(
				"(ek.render_cont.renders[render_id]==null) in do_buffer_dispatch of dispatch_system_request");
			return null;
		}
		if(r.parts==null){
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
		if(part_id>=r.parts.size()){
			debug_information.println(
				"part_id more than max value in do_buffer_dispatch of dispatch_system_request\t:\t",
				Integer.toString(part_id)+"/"+Integer.toString(r.parts.size()));
			return null;
		}

		part p;
		if((p=r.parts.get(part_id))==null){
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
		str=file_directory.part_file_directory(p,ek.system_par,ek.scene_par)+"mesh."+str+".gzip_text";
		
		return str;
	}
	static public String do_dispatch(engine_kernel ek,client_information ci)
	{
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
				switch(index_id=Integer.decode(str.substring(0,index_id))){
				case 0:
					str=ek.render_cont.system_part_package.package_file_name[package_id];
					break;
				case 2:
					str=ek.render_cont.scene_part_package.package_file_name[package_id];
					break;
				default:
					str=ek.render_cont.type_part_package[index_id-2].package_file_name[package_id];
					break;
				}
				return str;
			}catch(Exception e) {
				e.printStackTrace();
				
				debug_information.println(
					"Wrong package in do_buffer_dispatch of dispatch_system_request\t:\t",
					str+"\t"+e.toString());
				
				return null;
			}
		case "buffer_data":
			return download_data(ek,ci);
		}
	}
}