package kernel_client_interface;

import kernel_part.part;
import kernel_component.component;
import kernel_driver.component_instance_driver;
import kernel_common_class.debug_information;
import kernel_render.response_render_component_request;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class dispatch_component_request
{
	static public String[] do_dispatch(long delay_time_length,scene_kernel sk,client_information ci)
	{
		String str;
		component comp;

		if((str=ci.request_response.get_parameter("method"))==null){
			debug_information.println(
				"method is null in component_request_dispatch of dispatch_component_request");
			return null;
		}
		switch(str){
		default:
			debug_information.println(
				"unknown method is in component_request_dispatch of dispatch_component_request\t:\t",str);
			return null;
		case "update_render":
			ci.parameter.get_call_parameter(sk,ci);
			
			for(int i=0,ni=sk.modifier_cont.length;i<ni;i++)
				sk.modifier_cont[i].process(sk,ci,false);
			
			response_render_component_request.do_render(sk,ci,delay_time_length);
			return null;
		case "event":
			for(int i=0,ni=sk.modifier_cont.length;i<ni;i++)
				sk.modifier_cont[i].process(sk,ci,false);
			
			if((str=ci.request_response.get_parameter("event_component_id"))!=null){
				if((comp=sk.component_cont.get_component(Integer.decode(str)))==null){
					debug_information.println(
						"Can't Find component by ID in component_request_dispatch of dispatch_component_request\t:\t",str);
					return null;
				}
				int begin_driver_id=0,end_driver_id=comp.driver_number()-1;
				if(end_driver_id<0){
					debug_information.println(
						"No component driver in component_request_dispatch of dispatch_component_request\t:\t",
						comp.component_name);
					return null;
				}
				if((str=ci.request_response.get_parameter("event_driver_id"))==null)
					end_driver_id=0;
				else if((str=str.trim()).length()<=0)
					end_driver_id=0;
				else if(str.compareTo("all")!=0){
					begin_driver_id=Integer.decode(str);
					end_driver_id=begin_driver_id;
				}
				if(	  (begin_driver_id<0)||(end_driver_id<0)||(begin_driver_id>end_driver_id)
					||(begin_driver_id>=comp.driver_number())||(end_driver_id>=comp.driver_number()))
				{
					debug_information.println(
						"driver_id is wrong in component_request_dispatch of dispatch_component_request");
					debug_information.println("component_name is\t:\t",comp.component_name);
					debug_information.println("driver_number is\t:\t",comp.driver_number());
					debug_information.println("Request begin_driver_id is\t:\t",begin_driver_id);
					debug_information.println("Request end_driver_id is\t:\t",end_driver_id);
					return null;
				}
				String ret_val[]=null;
				for(int driver_id=end_driver_id;driver_id>=begin_driver_id;driver_id--){
					component_instance_driver in_dr;
					if((in_dr=ci.component_instance_driver_cont.
							get_component_instance_driver(comp,driver_id))!=null)
					{
						try{
							ret_val=in_dr.response_component_event(sk,ci);
						}catch(Exception e){
							e.printStackTrace();
							
							ret_val=null;
							part my_part=comp.driver_array.get(driver_id).component_part;
							debug_information.println("1.Execute response_component_event fail:	",e.toString());
							debug_information.println("Component name:	",	comp.component_name);
							debug_information.println("Driver ID:		",	driver_id);
							debug_information.println("Part user name:	",	my_part.user_name);
							debug_information.println("Part system name:",	my_part.system_name);
							debug_information.println("Mesh file name:	",	my_part.directory_name+my_part.mesh_file_name);
							
						}
					}
				}
				return ret_val;
			}
			if((str=ci.request_response.get_parameter("event_component_name"))!=null){
				String request_charset=ci.request_response.implementor.get_request_charset();
				try {
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e) {
					e.printStackTrace();
					
					debug_information.println(
							"Can't decode component name in component_request_dispatch of dispatch_component_request\t:\t",str);
					return null;
				}
				if((comp=sk.component_cont.search_component(str))==null){
					debug_information.println(
						"Can't Find component by name in component_request_dispatch of dispatch_component_request\t:\t",str);
					return null;
				}
				int begin_driver_id=0,end_driver_id=comp.driver_number()-1;
				if(end_driver_id<0){
					debug_information.println(
						"No component driver in component_request_dispatch of dispatch_component_request\t:\t",
						comp.component_name);
					return null;
				}
				if((str=ci.request_response.get_parameter("event_driver_id"))==null)
					end_driver_id=0;
				else if((str=str.trim()).length()<=0)
					end_driver_id=0;
				else if(str.compareTo("all")!=0){
					begin_driver_id=Integer.decode(str);
					end_driver_id=begin_driver_id;
				}
				if(	  (begin_driver_id<0)||(end_driver_id<0)||(begin_driver_id>end_driver_id)
					||(begin_driver_id>=comp.driver_number())||(end_driver_id>=comp.driver_number()))
				{
					debug_information.println(
						"driver_id is wrong in component_request_dispatch of dispatch_component_request");
					debug_information.println("component_name is\t:\t",comp.component_name);
					debug_information.println("driver_number is\t:\t",comp.driver_number());
					debug_information.println("Request begin_driver_id is\t:\t",begin_driver_id);
					debug_information.println("Request end_driver_id is\t:\t",end_driver_id);
					return null;
				}
				String ret_val[]=null;
				for(int driver_id=end_driver_id;driver_id>=begin_driver_id;driver_id--){
					component_instance_driver in_dr;
					if((in_dr=ci.component_instance_driver_cont.
							get_component_instance_driver(comp,driver_id))!=null)
					{
						try{
							ret_val=in_dr.response_component_event(sk,ci);
						}catch(Exception e){
							e.printStackTrace();
							
							ret_val=null;
							part my_part=comp.driver_array.get(driver_id).component_part;
							
							debug_information.println("2.Execute response_component_event fail:	",e.toString());
							debug_information.println("Component name:	",	comp.component_name);
							debug_information.println("Driver ID:		",	driver_id);
							debug_information.println("Part user name:	",	my_part.user_name);
							debug_information.println("Part system name:",	my_part.system_name);
							debug_information.println("Mesh file name:	",	my_part.directory_name+my_part.mesh_file_name);
							
						}
					}
				}
				return ret_val;	
			}
			debug_information.println(
				"No component ID or Component Name in component_request_dispatch of dispatch_component_request");
			return null;
		}
	}
}
