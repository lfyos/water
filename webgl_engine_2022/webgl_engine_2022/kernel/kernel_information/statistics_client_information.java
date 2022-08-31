package kernel_information;

import kernel_component.component;
import kernel_engine.engine_kernel;
import kernel_engine.client_information;
import kernel_engine.client_statistics;

public class statistics_client_information extends jason_creator
{
	private engine_kernel ek;
	private client_information ci;
	
	public void print()
	{
		client_statistics cs=ci.statistics_client;

		print("transportation_time_length",					cs.transportation_time_length);
		print("caculate_time_length",						cs.caculate_time_length);
		print("collect_time_length",						cs.collect_time_length);
		print("output_time_length",							cs.output_time_length);
		print("all_time_length",							cs.all_time_length);

		print("data_time_length",							cs.data_time_length);
		print("render_time_length",							cs.render_time_length);
		print("read_time_length",							cs.read_time_length);
		print("render_interval_length",						cs.render_interval_length);
		print("render_data_length",							cs.render_data_length);
		print("delay_time_length",							cs.delay_time_length);

		print("collect_component_number",					cs.collect_component_number);
		print("clip_plane_component_number",				cs.clip_plane_component_number);
		print("update_component_parameter_number",			cs.update_component_parameter_number);
		print("should_update_component_parameter_number",	cs.should_update_component_parameter_number);
	
		print("component_keep_number",						cs.component_keep_number);
		print("component_delete_number",					cs.component_delete_number);
		print("component_refresh_number",					cs.component_refresh_number);
		print("component_append_number",					cs.component_append_number);
		print("should_component_delete_number",				cs.should_component_delete_number);
		print("should_component_refresh_number",			cs.should_component_refresh_number);
		print("should_component_append_number",				cs.should_component_append_number);

		print("update_location_number",						cs.update_location_number);		
		print("should_update_location_number",				cs.should_update_location_number);

		print("top_box_component_number",					cs.top_box_component_number);
		print("bottom_box_component_number",				cs.bottom_box_component_number);
		print("hide_component_number",						cs.hide_component_number);
		print("clip_component_number",						cs.clip_component_number);
		print("lod_component_number",						cs.lod_component_number);

		print("discard_component_number",					cs.discard_component_number);
		print("abandon_component_number",					cs.abandon_component_number);
		print("no_driver_component_number",					cs.no_driver_component_number);
		print("not_load_component_number",					cs.not_load_component_number);

		print("system_call_execute_number",					cs.system_call_execute_number);
		
		String component_response_number[][]=new String[0][];
		if(cs.response_component_id!=null)
			component_response_number=new String[cs.response_component_id.length][];
		for(int i=0,ni=component_response_number.length;i<ni;i++) {
			int component_id=cs.response_component_id[i];
			component comp=ek.component_cont.get_component(component_id);
			if(comp==null)
				component_response_number[i]=new String[]{
					Integer.toString(component_id),
					Integer.toString(component_id),
					"0"
				};
			else
				component_response_number[i]=new String[] {
					comp.component_name,
					Integer.toString(component_id),
					Long.toString(cs.component_response_number[component_id])
				};
		}
		print("component_response_number",component_response_number);

		int length=0;
		if(cs.response_file_data_length!=null)
			for(int i=0,ni=cs.response_file_data_length.length;i<ni;i++)
				if(cs.response_file_data_length[i]>0)
					length++;
		String response_file_data_str[]=new String[length];
		if(length>0) {
			String target_name_array[]=ek.system_par.content_type_change_name.get_target_name();
			for(int i=0,j=0,ni=cs.response_file_data_length.length;i<ni;i++)
				if(cs.response_file_data_length[i]>0)
					response_file_data_str[j++]=target_name_array[i]+":"
							+Long.toString(cs.response_file_data_length[i]);
		}
		print("response_file_data_length",response_file_data_str);
		print("response_no_type_file_data_length",		cs.response_no_type_file_data_length);
		print("response_network_data_length",			cs.response_network_data_length);
		print("response_proxy_data_length",				cs.response_proxy_data_length);
		print("loading_request_number",					cs.loading_request_number);

	}
	
	public statistics_client_information(engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
	}
}
