package kernel_client_interface;

import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_driver.render_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_part.part;
import kernel_render.render;

public class dispatch_render_request 
{
	static private String[] render_response_event(render r,engine_kernel ek,client_information ci)
	{
		render_instance_driver my_render_instance_driver;
		
		if(r==null)
			debug_information.println("Render is null when execute call render driver");
		else if(r.driver==null)
			debug_information.println("Render driver is null when execute call render driver");
		else if((my_render_instance_driver=ci.render_instance_driver_cont.get_render_instance_driver(r))==null)
			debug_information.println("render instance_driver is null when execute call render driver");
		else
			try{
				return my_render_instance_driver.response_event(r,ek,ci);
			}catch(Exception e){
				debug_information.println("execute render response_event fail:",e.toString());
				e.printStackTrace();
			}
		debug_information.println("Render  name:	",	r.render_name);
		debug_information.println("Render  ID:	",		r.render_id);
		return null;
	}
	
	static public String[] do_dispatch(int main_call_id,engine_kernel ek,client_information ci)
	{
		render r;
		part p;
		component comp;
		int render_id,driver_id,component_id;
		
		
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
				"wrong method in render_request_dispatch of dispatch_render_request\t:\t",str);
			return null;
			
		case "event":
			if((str=ci.request_response.get_parameter("event_render_id"))!=null)
				if((render_id=Integer.decode(str))>=0)
					if(render_id<(ek.render_cont.renders.length))
						return render_response_event(ek.render_cont.renders[render_id],ek,ci);
			
			if((str=ci.request_response.get_parameter("event_render_name"))!=null){
				try {
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e) {
					debug_information.println("Can NOT decode render name in render_request_dispatch");
					return null;
				}
				if((r=ek.render_cont.search_render(str))!=null) 
					return render_response_event(r,ek,ci);
				else{
					debug_information.println("No render exist with name ",str+" in render_request_dispatch");
					return null;
				}
			}
			if((str=ci.request_response.get_parameter("event_part_name"))!=null){
				try {
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e) {
					debug_information.println("Can NOT decode part name in render_request_dispatch");
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
								if((render_id=parts[i].render_id)>=0)
									if(render_id<(ek.render_cont.renders.length))
										return render_response_event(ek.render_cont.renders[render_id],ek,ci);
					}else if((driver_id=Integer.decode(str))>=0)
							if(driver_id<parts.length)
								if(parts[driver_id]!=null)
									return render_response_event(ek.render_cont.renders[parts[driver_id].render_id],ek,ci);
			}
			if((str=ci.request_response.get_parameter("event_component_id"))!=null) {
				if((component_id=Integer.decode(str))>=0)
					if(component_id<=(ek.component_cont.root_component.component_id))
						if((comp=ek.component_cont.get_component(component_id))!=null)
							if((str=ci.request_response.get_parameter("event_driver_id"))==null){
								for(int i=0,driver_number=comp.driver_number();i<driver_number;i++)
									if(comp.driver_array[i]!=null)
										if((p=comp.driver_array[i].component_part)!=null)
											return render_response_event(ek.render_cont.renders[p.render_id],ek,ci);
							}else {
								if((driver_id=Integer.decode(str))>=0)
									if(driver_id<comp.driver_number())
										if(comp.driver_array[driver_id]!=null)
											if((p=comp.driver_array[driver_id].component_part)!=null)
												return render_response_event(ek.render_cont.renders[p.render_id],ek,ci);
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
				if((comp=ek.component_cont.search_component(str))!=null)
					if((str=ci.request_response.get_parameter("event_driver_id"))==null){
						for(int i=0,driver_number=comp.driver_number();i<driver_number;i++)
							if(comp.driver_array[i]!=null)
								if((p=comp.driver_array[i].component_part)!=null)
									return render_response_event(ek.render_cont.renders[p.render_id],ek,ci);
					}else {
						if((driver_id=Integer.decode(str))>=0)
							if(driver_id<comp.driver_number())
								if(comp.driver_array[driver_id]!=null)
									if((p=comp.driver_array[driver_id].component_part)!=null)
										return render_response_event(ek.render_cont.renders[p.render_id],ek,ci);
					}
			}
			debug_information.println(
				"Execute response_event of render driver fail in render_request_dispatch of dispatch_render_request");
			
			return null;
		}
	}
}
