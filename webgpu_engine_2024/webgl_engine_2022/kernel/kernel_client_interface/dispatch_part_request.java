package kernel_client_interface;

import java.util.ArrayList;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_part.part;
import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_driver.component_driver;
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
	static public String[] do_dispatch(engine_kernel ek,client_information ci)
	{
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
					if(render_id<(ek.render_cont.renders.size()))
						if(ek.render_cont.renders.get(render_id).parts!=null)
							if((str=ci.request_response.get_parameter("event_part_id"))!=null)
								if((part_id=Integer.decode(str))>=0)
									if(part_id<(ek.render_cont.renders.get(render_id).parts.size()))
										return response_part_event(
											ek.render_cont.renders.get(render_id).parts.get(part_id),ek,ci);
			}
			if((str=ci.request_response.get_parameter("event_part_name"))!=null){
				try {
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e) {
					debug_information.println("Can NOT decode part name in part_request_dispatch");
					return null;
				}
				part p;
				ArrayList<part> parts;
				if((parts=ek.part_cont.search_part(str))!=null)
					if((str=ci.request_response.get_parameter("event_driver_id"))==null){
						for(int i=0,ni=parts.size();i<ni;i++)
							if((p=parts.get(i))!=null)
								if(p.driver!=null)
									return response_part_event(p,ek,ci);
					}else{
						int driver_id=Integer.decode(str);
						if((driver_id>=0)&&(driver_id<parts.size()))
							if((p=parts.get(driver_id))!=null)
								if(p.driver!=null)
									return response_part_event(p,ek,ci);
					}
			}
			
			if((str=ci.request_response.get_parameter("event_component_id"))!=null) {
				int component_id;
				if((component_id=Integer.decode(str))>=0)
					if(component_id<=(ek.component_cont.root_component.component_id)) {
						component comp;
						if((comp=ek.component_cont.get_component(component_id))!=null){
							component_driver c_d;
							int driver_number=comp.driver_number();
							if((str=ci.request_response.get_parameter("event_driver_id"))==null){
								for(int i=0;i<driver_number;i++)
									if((c_d=comp.driver_array.get(i))!=null)
										return response_part_event(
												c_d.component_part,ek,ci);
							}else{
								int driver_id=Integer.decode(str);
								if((driver_id>=0)&&(driver_id<driver_number))
									if((c_d=comp.driver_array.get(driver_id))!=null)
										return response_part_event(	c_d.component_part,ek,ci);
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
					component_driver c_d;
					int driver_number=comp.driver_number();
					if((str=ci.request_response.get_parameter("event_driver_id"))==null){
						for(int i=0;i<driver_number;i++)
							if((c_d=comp.driver_array.get(i))!=null)
								return response_part_event(
										c_d.component_part,ek,ci);
					}else{
						int driver_id=Integer.decode(str);
						if((driver_id>=0)&&(driver_id<driver_number))
							if((c_d=comp.driver_array.get(driver_id))!=null)
								return response_part_event(
										c_d.component_part,ek,ci);
					}
				}
			}
			debug_information.println(
				"Execute response_part_event fail in part_request_dispatch of dispatch_part_request");
			
			return null;
		}
	}
}
