package kernel_client_interface;

import kernel_part.part;
import kernel_component.component;
import kernel_engine.engine_kernel;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_common_class.debug_information;
import kernel_render.response_render_component_request;

public class dispatch_component_request
{
	static public String[] do_dispatch(int main_call_id,
			long delay_time_length,engine_kernel ek,client_information ci)
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
			ci.statistics_client.register_system_call_execute_number(main_call_id,0);
			ci.parameter.get_call_parameter(ek,ci);
			ek.modifier_cont.process(ek,ci);
			response_render_component_request.do_render(ek,ci,delay_time_length);
			return null;
		case "event":
			ci.statistics_client.register_system_call_execute_number(main_call_id,1);
			if((str=ci.request_response.get_parameter("event_component_id"))!=null){
				if((comp=ek.component_cont.get_component(Integer.decode(str)))==null){
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
				ci.statistics_client.register_component_response_execute_number(comp.component_id);
				String ret_val[]=null;
				for(int driver_id=end_driver_id;driver_id>=begin_driver_id;driver_id--){
					instance_driver in_dr;
					if((in_dr=ci.instance_container.get_driver(comp,driver_id))!=null) {
						int parameter_channel_id=ci.display_camera_result.target.parameter_channel_id;
						try{
							ret_val=in_dr.response_event(parameter_channel_id,ek,ci);
						}catch(Exception e){
							ret_val=null;
							part my_part=comp.driver_array[driver_id].component_part;
							debug_information.println("1.Execute response_event of instance driver fail:	",e.toString());
							debug_information.println("Component name:	",	comp.component_name);
							debug_information.println("Driver ID:		",	driver_id);
							debug_information.println("Part user name:	",	my_part.user_name);
							debug_information.println("Part system name:",	my_part.system_name);
							debug_information.println("Mesh file name:	",	my_part.directory_name+my_part.mesh_file_name);
							e.printStackTrace();
						}
					}
				}
				return ret_val;
			}
			if((str=ci.request_response.get_parameter("event_component_name"))!=null){
				try {
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				}catch(Exception e) {
					debug_information.println(
							"Can't decode component name in component_request_dispatch of dispatch_component_request\t:\t",str);
					return null;
				}
				if((comp=ek.component_cont.search_component(str))==null){
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
				ci.statistics_client.register_component_response_execute_number(comp.component_id);
				String ret_val[]=null;
				for(int driver_id=end_driver_id;driver_id>=begin_driver_id;driver_id--){
					instance_driver in_dr;
					if((in_dr=ci.instance_container.get_driver(comp,driver_id))!=null) {
						int parameter_channel_id=ci.display_camera_result.target.parameter_channel_id;
						try{
							ret_val=in_dr.response_event(parameter_channel_id,ek,ci);
						}catch(Exception e){
							ret_val=null;
							part my_part=comp.driver_array[driver_id].component_part;
							
							debug_information.println("2.Execute response_event of instance driver fail:	",e.toString());
							debug_information.println("Component name:	",	comp.component_name);
							debug_information.println("Driver ID:		",	driver_id);
							debug_information.println("Part user name:	",	my_part.user_name);
							debug_information.println("Part system name:",	my_part.system_name);
							debug_information.println("Mesh file name:	",	my_part.directory_name+my_part.mesh_file_name);
							e.printStackTrace();
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
