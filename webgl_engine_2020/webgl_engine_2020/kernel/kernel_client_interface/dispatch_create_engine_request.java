package kernel_client_interface;

import kernel_part.part;
import kernel_component.component;
import kernel_engine.engine_kernel;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_file_manager.file_directory;

import kernel_common_class.debug_information;
import kernel_common_class.jason_string;

public class dispatch_create_engine_request
{
	static private void do_part_response_init_data(engine_kernel ek,client_information ci)
	{
		debug_information.println();
		debug_information.println("Begin part response_init_data");

		int part_initialize_number=0;
		int process_parts_sequence[][]=ek.process_part_sequence.process_parts_sequence;
		ci.request_response.print("[");
		for(int i=0,ni=process_parts_sequence.length;i<ni;i++){
			int render_id=process_parts_sequence[i][0],part_id=process_parts_sequence[i][1];
			if(ek.render_cont.renders[render_id].parts[part_id].driver!=null) {
				long old_length=ci.request_response.output_data_length;
				part my_p=ek.render_cont.renders[render_id].parts[part_id];
				try {
					my_p.driver.response_init_data(my_p,ek,ci);
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
	static private int do_instance_response_init_data(engine_kernel ek,client_information ci)
	{
		debug_information.println();
		debug_information.println("Begin instance response_init_data");
	
		int instance_initialize_number=0;
		component comp_array[]=ek.component_cont.get_sort_component_array();
		if(comp_array==null)
			comp_array=new component[] {};
		
		instance_driver i_d;
		ci.request_response.print("[");
		for(int i=0,ni=comp_array.length;i<ni;i++)
			for(int driver_id=0,driver_number=comp_array[i].driver_number();driver_id<driver_number;driver_id++)
				if((i_d=ci.instance_container.get_driver(comp_array[i],driver_id))!=null){
					long old_length=ci.request_response.output_data_length;
					
					try {
						i_d.response_init_data(ek,ci);
					}catch(Exception e) {
						debug_information.println("instance driver response_init_data fail:	",e.toString());
						debug_information.println("instance name:",comp_array[i].component_name);
						debug_information.println("instance file:",
								comp_array[i].component_directory_name+comp_array[i].component_file_name);
						debug_information.println("instance driver id:",driver_id);
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
		
		debug_information.println("End instance response_init_data: ",instance_initialize_number);
		
		return comp_array.length;
	}
	
	static public void do_dispatch(int main_call_id,engine_kernel ek,client_information ci)
	{
		ci.statistics_client.register_system_call_execute_number(main_call_id,0);

		ci.request_response.print("[ "+ci.channel_id+" ,");	//parameter	0
		
		do_part_response_init_data(ek,ci);								//parameter	1
		ci.request_response.print(",");
		
		int total_component_number=do_instance_response_init_data(ek,ci);//parameter2

		ci.request_response.print(",[",total_component_number);			//parameter	3	0
		ci.request_response.print(",",ek.render_cont.renders.length);	//parameter	3	1
		ci.request_response.print(",",ek.modifier_cont.length);			//parameter	3	2
		
		ci.request_response.print(",[");								//parameter	3	3
			if(ek.camera_cont.camera_array!=null)
				for(int i=0,n=ek.camera_cont.camera_array.length;i<n;i++){
					component eye_comp=ek.camera_cont.camera_array[i].eye_component;
					ci.request_response.print((i<=0)?"":",",(eye_comp==null)?-1:eye_comp.component_id);
				}
		ci.request_response.print("]");
			
		ci.request_response.print(",\"",	ek.link_name);			//parameter	3	4
		ci.request_response.print("\",",	jason_string.change_string(
				ek.title+ek.scene_par.scene_sub_directory));		//parameter	2	6
	
		ci.request_response.print(",{");							//parameter	3	5
			
		ci.request_response.print( "\"max_loading_number\":",
			ci.creation_parameter.max_client_loading_number);
			
		ci.request_response.print(",\"total_buffer_object_data_length\":",	ek.process_part_sequence.total_buffer_object_text_data_length);
		ci.request_response.print(",\"engine_touch_time_length\":",			ek.system_par.engine_touch_time_length);
		ci.request_response.print(",\"download_start_time_length\":",		ek.system_par.download_start_time_length/1000000);
		ci.request_response.print(",\"download_minimal_time_length\":",		ek.system_par.download_minimal_time_length/1000000);
		ci.request_response.print(",\"debug_mode_flag\":",					ek.system_par.debug_mode_flag?"true":"false");
			
		ci.request_response.print("}");
		ci.request_response.print("],\"");

		String initialization_url=ek.scene_par.scene_proxy_directory_name+"initialization.gzip_text";
		if((initialization_url=ci.get_file_proxy_url(initialization_url,ek.system_par))==null)
			initialization_url=ci.get_request_url_header()+"&command=initialization";
		ci.request_response.print(initialization_url,"\"");	//parameter	4   last
		
		ci.request_response.print("]");

		kernel_common_class.debug_information.println();
		kernel_common_class.debug_information.println(
			"Create scene response data length:	",ci.request_response.output_data_length);
		kernel_common_class.debug_information.println();
		
		return;
	}
}