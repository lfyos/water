package kernel_client_interface;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_part.part;
import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_driver.part_instance_driver;

public class dispatch_part_request 
{
	static private String[] response_part_event(part p,engine_kernel ek,client_information ci)
	{
		part_instance_driver  my_part_instance_driver;
		
		if(p==null)
			debug_information.println("Part is null when execute call part driver");
		else if(p.driver==null)
			debug_information.println("Part driver is null when execute call part driver");
		else if((my_part_instance_driver=ci.part_instance_driver_cont.get_part_instance_driver(p))==null)
			debug_information.println("Part instance_driver is null when execute call part driver");
		else {
			try{
				return my_part_instance_driver.response_part_event(p, ek, ci);
			}catch(Exception e){
				debug_information.println("execute response_part_event fail:",e.toString());
				e.printStackTrace();
			}
		}

		debug_information.println("Part user name:	",		p.user_name);
		debug_information.println("Part system name:	",	p.system_name);
		debug_information.println("Directory name:	",		p.directory_name);
		debug_information.println("Mesh file name:	",		p.mesh_file_name);
		
		return null;
	}
	static public String[] do_dispatch(
			int main_call_id,engine_kernel ek,client_information ci)
	{
		ci.statistics_client.register_system_call_execute_number(main_call_id,0);
		
		String str,request_charset=ci.request_response.implementor.get_request_charset();
		if((str=ci.request_response.get_parameter("method"))==null){
			debug_information.println(
				"method is null in part_request_dispatch of dispatch_part_request");
			return null;
		}
		switch(str){
		default:
			debug_information.println(
				"wrong method in part_request_dispatch of dispatch_part_request\t:\t",str);
			return null;
			
		case "event":
			if((str=ci.request_response.get_parameter("event_render_id"))!=null) {
				int render_id,part_id;
				if((render_id=Integer.decode(str))>=0)
					if(render_id<(ek.render_cont.renders.length))
						if(ek.render_cont.renders[render_id].parts!=null)
							if((str=ci.request_response.get_parameter("event_part_id"))!=null)
								if((part_id=Integer.decode(str))>=0)
									if(part_id<(ek.render_cont.renders[render_id].parts.length))
										return response_part_event(
											ek.render_cont.renders[render_id].parts[part_id],ek,ci);
			}
			if((str=ci.request_response.get_parameter("event_part_name"))!=null){
				try {
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e) {
					debug_information.println("Can NOT decode part name in part_request_dispatch");
					return null;
				}
				part parts[];
				String part_name=str;
				String search_part_name=ek.component_cont.change_part_name.search_change_name(part_name,part_name);
				if((parts=ek.part_cont.search_part(search_part_name))==null){
					search_part_name=ek.component_cont.change_part_name.search_change_name(search_part_name,search_part_name);
					parts=ek.part_cont.search_part(search_part_name);
				}
				if(parts!=null)
					if((str=ci.request_response.get_parameter("event_driver_id"))==null){
						for(int i=0,ni=parts.length;i<ni;i++)
							if(parts[i]!=null)
								if(parts[i].driver!=null)
									return response_part_event(parts[i],ek,ci);
					}else{
						int driver_id=Integer.decode(str);
						if((driver_id>=0)&&(driver_id<parts.length))
							if(parts[driver_id]!=null)
								if(parts[driver_id].driver!=null)
									return response_part_event(parts[driver_id],ek,ci);
					}
			}
			
			if((str=ci.request_response.get_parameter("event_component_id"))!=null) {
				int component_id;
				if((component_id=Integer.decode(str))>=0)
					if(component_id<=(ek.component_cont.root_component.component_id)) {
						component comp;
						if((comp=ek.component_cont.get_component(component_id))!=null){
							int driver_number=comp.driver_number();
							if((str=ci.request_response.get_parameter("event_driver_id"))==null){
								for(int i=0;i<driver_number;i++)
									if(comp.driver_array[i]!=null)
										return response_part_event(
												comp.driver_array[i].component_part,ek,ci);
							}else{
								int driver_id=Integer.decode(str);
								if((driver_id>=0)&&(driver_id<driver_number))
									if(comp.driver_array[driver_id]!=null)
										return response_part_event(
												comp.driver_array[driver_id].component_part,ek,ci);
							}
						}
					}
			}
			if((str=ci.request_response.get_parameter("event_component_name"))!=null){
				try {
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e) {
					debug_information.println("Can NOT decode component name in part_request_dispatch");
					return null;
				}
				
				component comp;
				if((comp=ek.component_cont.search_component(str))!=null){
					int driver_number=comp.driver_number();
					if((str=ci.request_response.get_parameter("event_driver_id"))==null){
						for(int i=0;i<driver_number;i++)
							if(comp.driver_array[i]!=null)
								return response_part_event(
										comp.driver_array[i].component_part,ek,ci);
					}else{
						int driver_id=Integer.decode(str);
						if((driver_id>=0)&&(driver_id<driver_number))
							if(comp.driver_array[driver_id]!=null)
								return response_part_event(
										comp.driver_array[driver_id].component_part,ek,ci);
					}
				}
			}
			debug_information.println(
				"Execute response_part_event fail in part_request_dispatch of dispatch_part_request");
			
			return null;
		}
	}
}
