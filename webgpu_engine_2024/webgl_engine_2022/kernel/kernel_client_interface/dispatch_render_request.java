package kernel_client_interface;

import java.util.ArrayList;

import kernel_part.part;
import kernel_render.render;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.render_instance_driver;
import kernel_common_class.debug_information;

public class dispatch_render_request 
{
	static private String[] response_render_event(render r,scene_kernel sk,client_information ci)
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
				return my_render_instance_driver.response_render_event(r,sk,ci);
			}catch(Exception e){
				e.printStackTrace();
				
				debug_information.println("execute render response_render_event fail:",e.toString());
			}
		debug_information.println("Render  name:	",	r.render_name);
		debug_information.println("Render  ID:	",		r.render_id);
		return null;
	}
	
	static public String[] do_dispatch(scene_kernel sk,client_information ci)
	{
		render r;
		part p;
		component comp;
		component_driver c_d;
		int render_id,driver_id,component_id;
		
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
					if(render_id<(sk.render_cont.renders.size()))
						return response_render_event(sk.render_cont.renders.get(render_id),sk,ci);
			
			if((str=ci.request_response.get_parameter("event_render_name"))!=null){
				try {
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e) {
					debug_information.println("Can NOT decode render name in render_request_dispatch");
					return null;
				}
				if((r=sk.render_cont.search_render(str))!=null) 
					return response_render_event(r,sk,ci);
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
				ArrayList<part> parts;
				if((parts=sk.part_cont.search_part(str))!=null)
					if((str=ci.request_response.get_parameter("event_driver_id"))==null){
						for(int i=0,ni=parts.size();i<ni;i++)
							if((p=parts.get(i))!=null)
								if((render_id=p.render_id)>=0)
									if(render_id<(sk.render_cont.renders.size()))
										return response_render_event(sk.render_cont.renders.get(render_id),sk,ci);
					}else if((driver_id=Integer.decode(str))>=0)
							if(driver_id<parts.size())
								if((p=parts.get(driver_id))!=null)
									return response_render_event(sk.render_cont.renders.get(p.render_id),sk,ci);
			}
			if((str=ci.request_response.get_parameter("event_component_id"))!=null) {
				if((component_id=Integer.decode(str))>=0)
					if(component_id<=(sk.component_cont.root_component.component_id))
						if((comp=sk.component_cont.get_component(component_id))!=null)
							if((str=ci.request_response.get_parameter("event_driver_id"))==null){
								for(int i=0,driver_number=comp.driver_number();i<driver_number;i++)
									if((c_d=comp.driver_array.get(i))!=null)
										if((p=c_d.component_part)!=null)
											return response_render_event(sk.render_cont.renders.get(p.render_id),sk,ci);
							}else {
								if((driver_id=Integer.decode(str))>=0)
									if(driver_id<comp.driver_number())
										if((c_d=comp.driver_array.get(driver_id))!=null)
											if((p=c_d.component_part)!=null)
												return response_render_event(
															sk.render_cont.renders.get(p.render_id),sk,ci);
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
				if((comp=sk.component_cont.search_component(str))!=null)
					if((str=ci.request_response.get_parameter("event_driver_id"))==null){
						for(int i=0,driver_number=comp.driver_number();i<driver_number;i++)
							if((c_d=comp.driver_array.get(i))!=null)
								if((p=c_d.component_part)!=null)
									return response_render_event(
												sk.render_cont.renders.get(p.render_id),sk,ci);
					}else {
						if((driver_id=Integer.decode(str))>=0)
							if(driver_id<comp.driver_number())
								if((c_d=comp.driver_array.get(driver_id))!=null)
									if((p=c_d.component_part)!=null)
										return response_render_event(
													sk.render_cont.renders.get(p.render_id),sk,ci);
					}
			}
			debug_information.println(
				"Execute response_render_event  fail in render_request_dispatch of dispatch_render_request");
			
			return null;
		}
	}
}
