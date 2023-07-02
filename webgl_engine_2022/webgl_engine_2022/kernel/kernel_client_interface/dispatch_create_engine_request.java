package kernel_client_interface;

import kernel_part.part;
import kernel_component.component;
import kernel_engine.engine_kernel;
import kernel_engine.client_information;
import kernel_file_manager.file_directory;
import kernel_common_class.debug_information;
import kernel_common_class.jason_string;
import kernel_driver.component_instance_driver;
import kernel_driver.component_instance_driver_container;
import kernel_driver.part_instance_driver;
import kernel_driver.render_instance_driver;
import kernel_render.render;

public class dispatch_create_engine_request
{
	static private void do_response_init_render_data(engine_kernel ek,client_information ci)
	{
		debug_information.println();
		debug_information.println("Begin render response_init_data");
		int render_initialize_number=0;
		ci.request_response.print("[");
		for(int render_id=0,render_number=ek.render_cont.renders.size();render_id<render_number;render_id++){
			render r;
			if((r=ek.render_cont.renders.get(render_id))==null)
				continue;
			if(r.driver==null)
				continue;
			
			render_instance_driver r_i_driver=ci.render_instance_driver_cont.get_render_instance_driver(r);
			if(r_i_driver==null)
				continue;
			long old_length=ci.request_response.output_data_length;
			try {
				r_i_driver.response_init_render_data(r,ek,ci);
			}catch(Exception e){
				debug_information.println("Render driver response_init_data fail:	",e.toString());
				debug_information.println("Driver name:		",r.driver.getClass().getName());
				debug_information.println("render_id:		",render_id);
				e.printStackTrace();
			}
			if(ci.request_response.output_data_length!=old_length){
				ci.request_response.print(",",render_id).print(",");
				render_initialize_number++;
			}
		}
		ci.request_response.print("0]");
		debug_information.println("End render response_init_data: ",render_initialize_number);
	}
	static private void do_response_init_part_data(engine_kernel ek,client_information ci)
	{
		debug_information.println();
		debug_information.println("Begin part response_init_data");

		int part_initialize_number=0;
		int process_parts_sequence[][]=ek.process_part_sequence.process_parts_sequence;
		ci.request_response.print("[");
		for(int i=0,ni=process_parts_sequence.length;i<ni;i++){
			int render_id=process_parts_sequence[i][0],part_id=process_parts_sequence[i][1];
			part my_p=ek.render_cont.renders.get(render_id).parts.get(part_id);
			if(my_p.driver!=null){
				long old_length=ci.request_response.output_data_length;
				part_instance_driver  my_part_instance_driver=ci.part_instance_driver_cont.get_part_instance_driver(my_p);
				if(my_part_instance_driver!=null)
				try {
					my_part_instance_driver.response_init_part_data(my_p,ek,ci);
				}catch(Exception e){
					debug_information.println("Part driver response_init_data fail:	",e.toString());
					
					debug_information.println("Part user name:		",	my_p.user_name);
					debug_information.println("Part system name:	",	my_p.system_name);
					debug_information.println("Part mesh_file_name:",	my_p.directory_name+my_p.mesh_file_name);
					debug_information.println("Part material_file_name:",my_p.directory_name+my_p.material_file_name);
					debug_information.println("part_file_directory:",
							file_directory.part_file_directory(my_p,ek.system_par,ek.scene_par));
					e.printStackTrace();
				}
				if(ci.request_response.output_data_length!=old_length){
					ci.request_response.print(",",render_id);
					ci.request_response.print(",",part_id);
					ci.request_response.print(",");
					part_initialize_number++;
				}
			}
		}
		ci.request_response.print("0]");
		debug_information.println("End part response_init_data: ",part_initialize_number);
	}
	static private void do_response_init_component_data(engine_kernel ek,client_information ci)
	{
		debug_information.println();
		debug_information.println("Begin component response_init_data");
	
		int instance_initialize_number=0;
		component comp_array[]=ek.component_cont.get_sort_component_array();
		if(comp_array==null)
			comp_array=new component[] {};
		
		ci.request_response.print("[");
		component_instance_driver_container	cidc=ci.component_instance_driver_cont;
		for(int i=0,ni=comp_array.length;i<ni;i++)
			for(int driver_id=0,driver_number=comp_array[i].driver_number();driver_id<driver_number;driver_id++) {
				component_instance_driver i_d=cidc.get_component_instance_driver(comp_array[i],driver_id);
				if(i_d==null)
					continue;
				
				long old_length=ci.request_response.output_data_length;
					
				try {
					i_d.response_init_component_data(ek, ci);
				}catch(Exception e) {
					debug_information.println("response_init_component_data fail:	",e.toString());
					debug_information.println("component_name:",comp_array[i].component_name);
					debug_information.println("component file:",
							comp_array[i].component_directory_name+comp_array[i].component_file_name);
					debug_information.println("component driver id:",driver_id);
					e.printStackTrace();
				}
				if(ci.request_response.output_data_length!=old_length){
					ci.request_response.print(",",comp_array[i].component_id);
					ci.request_response.print(",",driver_id);
					ci.request_response.print(",");
					
					instance_initialize_number++;
				}
			}
		ci.request_response.print("0]");
		
		debug_information.println("End component response_init_data: ",instance_initialize_number);
		
		return;
	}
	
	static public void do_dispatch(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[ "+ci.channel_id+" ,");				//parameter	0
		
		do_response_init_render_data(ek,ci);							//parameter	1
		ci.request_response.print(",");
		
		do_response_init_part_data(ek,ci);								//parameter	2
		ci.request_response.print(",");
		
		do_response_init_component_data(ek,ci);//parameter3

		ci.request_response.print(",[",ek.component_cont.get_sort_component_array().length);
																		//parameter	4	0
		ci.request_response.print(",",ek.render_cont.renders.size());	//parameter	4	1
		ci.request_response.print(",",ek.modifier_cont.length);			//parameter	4	2
		ci.request_response.print(",",
				(ek.camera_cont==null)?0:ek.camera_cont.size());		//parameter	4	3
		
		ci.request_response.print(",",ek.scene_par.max_target_number);	//parameter	4	4
		ci.request_response.print(",",ek.scene_par.max_method_number);	//parameter	4	5
		
		ci.request_response.print(",\"",	ek.create_parameter.link_name);			//parameter	4	6
		ci.request_response.print("\",",	jason_string.change_string(
				ek.create_parameter.scene_title+ek.scene_par.scene_sub_directory));	//parameter	4	7
	
		ci.request_response.print(",{");							//parameter	4	8

		ci.request_response.print( "\"max_loading_number\":",		ci.parameter.max_client_loading_number);
		ci.request_response.print(",\"engine_touch_time_length\":",	ek.system_par.engine_touch_time_length);
			
		ci.request_response.print("}");
		ci.request_response.print("],\"");

		String initialization_url=ek.scene_par.scene_proxy_directory_name+"initialization.gzip_text";
		if((initialization_url=ci.get_file_proxy_url(initialization_url,ek.system_par))==null)
			initialization_url=ci.request_url_header+"&command=initialization";
		ci.request_response.print(initialization_url,"\"");			//parameter	5   last
		
		ci.request_response.print("]");

		kernel_common_class.debug_information.println();
		kernel_common_class.debug_information.println(
			"Create scene response data length:	",ci.request_response.output_data_length);
		kernel_common_class.debug_information.println();
		
		return;
	}
}