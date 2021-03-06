package kernel_engine;

public class client_statistics
{
	public long last_access_time;
	
	public long transportation_time_length,caculate_time_length,collect_time_length,output_time_length,all_time_length;
	public long data_time_length,render_time_length,read_time_length,render_interval_length,render_data_length;
	public long delay_time_length;
	
	public int collect_component_number,clip_plane_component_number,update_component_parameter_number,should_update_component_parameter_number;
	public int component_keep_number,component_delete_number,component_refresh_number,component_append_number;
	public int should_component_delete_number,should_component_refresh_number,should_component_append_number;
	public int update_location_number,should_update_location_number;
	
	public int top_box_component_number,bottom_box_component_number,hide_component_number,clip_component_number,lod_component_number[];
	public int discard_component_number,abandon_component_number,no_driver_component_number,not_load_component_number;
	
	public long system_call_execute_number[][];
	
	public long component_response_number[];
	public int response_component_id[];
	
	public long response_file_data_length[];
	public long response_no_type_file_data_length;
	public long response_network_data_length;
	public long response_proxy_data_length;
	
	public int	loading_request_number;

	public void clear()
	{
		delay_time_length		=1;
		data_time_length		=1;
		render_time_length		=1;
		read_time_length		=1;
		render_interval_length	=1;
		render_data_length		=1;
				
		last_access_time		=0;
		
		response_file_data_length			=null;
		response_no_type_file_data_length	=0;
		response_network_data_length		=0;
		response_proxy_data_length			=0;
		
		update_location_number=0;
		
		system_call_execute_number		=new long[1][];
		system_call_execute_number[0]	=new long[1];
		system_call_execute_number[0][0]=0;
		
		component_response_number		=null;
		response_component_id			=null;
		
		loading_request_number=0;
		
		start(1,0);
	}
	public void register_component_response_execute_number(int component_id)
	{
		if(component_response_number==null){
			component_response_number=new long[component_id+1];
			for(int i=0,ni=component_response_number.length;i<ni;i++)
				component_response_number[i]=0;
		}else if(component_response_number.length<=component_id){
			long bak[]=component_response_number;
			component_response_number=new long[component_id+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				component_response_number[i]=bak[i];
			for(int i=bak.length,ni=component_response_number.length;i<ni;i++)
				component_response_number[i]=0;
		}
		
		if((component_response_number[component_id]++)<=0){
			if(response_component_id==null){
				response_component_id=new int[1];
				response_component_id[0]=component_id;
			}else{
				int bak[]=response_component_id;
				response_component_id=new int[bak.length+1];
				for(int i=0,ni=bak.length;i<ni;i++)
					response_component_id[i]=bak[i];
				response_component_id[response_component_id.length-1]=component_id;
			}
		}
	}
	public void register_system_call_execute_number(int main_call_id,int min_call_id)
	{
		if(system_call_execute_number.length<=main_call_id){
			long old[][]=system_call_execute_number;
			system_call_execute_number=new long[main_call_id+1][];
			for(int i=0;i<old.length;i++)
				system_call_execute_number[i]=old[i];
			for(int i=old.length;i<system_call_execute_number.length;i++){
				system_call_execute_number[i]	=new long[1];
				system_call_execute_number[i][0]=0;
			}
		}
		if(system_call_execute_number[main_call_id].length<=min_call_id){
			long old[]=system_call_execute_number[main_call_id];
			system_call_execute_number[main_call_id]=new long[min_call_id+1];
			for(int i=0;i<old.length;i++)
				system_call_execute_number[main_call_id][i]=old[i];
			for(int i=old.length;i<system_call_execute_number[main_call_id].length;i++)
				system_call_execute_number[main_call_id][i]=0;
		}
		system_call_execute_number[main_call_id][min_call_id]++;
		return;
	}
	public void start(long my_delay_time_length,int my_loading_request_number)
	{
		delay_time_length				=my_delay_time_length;
		loading_request_number			=my_loading_request_number;
		
		collect_time_length				=0;
		output_time_length				=0;

		collect_component_number		=0;
		clip_plane_component_number		=0;
		update_component_parameter_number=0;
		should_update_component_parameter_number=0;
		
		component_keep_number			=0;
		component_delete_number			=0;
		component_refresh_number		=0;
		component_append_number			=0;
		should_component_delete_number	=0;
		should_component_refresh_number	=0;
		should_component_append_number	=0;
		
		update_location_number=0;
		should_update_location_number=0;

		top_box_component_number		=0;
		bottom_box_component_number		=0;
		discard_component_number		=0;
		hide_component_number			=0;
		clip_component_number			=0;

		lod_component_number=new int[1];
		lod_component_number[0]=0;
		
		abandon_component_number=0;
		no_driver_component_number=0;
		not_load_component_number=0;
	}
	public void modify_response_data_length(int content_type_id,long modify_length)
	{
		if(content_type_id<0){
			response_no_type_file_data_length+=modify_length;
			return;
		}
		if(response_file_data_length==null){
			response_file_data_length=new long[content_type_id+1];
			for(int i=0,ni=response_file_data_length.length;i<ni;i++)
				response_file_data_length[i]=0;
		}else if((content_type_id)>=(response_file_data_length.length)){
			long bak[]=response_file_data_length;
			response_file_data_length=new long[content_type_id+1];
			for(int i=0,ni=bak.length;i<ni;i++)
				response_file_data_length[i]=bak[i];
			for(int i=bak.length,ni=response_file_data_length.length;i<ni;i++)
				response_file_data_length[i]=0;
		}
		response_file_data_length[content_type_id]+=modify_length;
	}
	public client_statistics()
	{
		clear();
	}
}