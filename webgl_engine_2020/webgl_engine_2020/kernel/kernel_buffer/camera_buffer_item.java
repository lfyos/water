package kernel_buffer;

import kernel_common_class.const_value;
import kernel_network.client_request_response;

public class camera_buffer_item 
{
	private double buffer_data[];
	private int buffer_type_id;
	
	public camera_buffer_item(int my_buffer_type_id)
	{
		buffer_data=null;
		buffer_type_id=my_buffer_type_id;
	}
	public String response(client_request_response out,double new_buffer_data[],String pre_string)
	{
		if(buffer_data!=null)
			if(buffer_data.length==new_buffer_data.length){
				boolean is_same_flag=true;
				for(int i=0,ni=buffer_data.length;i<ni;i++)
					if(Math.abs(buffer_data[i]-new_buffer_data[i])>const_value.min_value){
						is_same_flag=false;
						break;
					}
				if(is_same_flag)
					return pre_string;
			}
		out.print(pre_string);
		out.print("[",buffer_type_id);
		buffer_data=new double[new_buffer_data.length];
		for(int i=0,ni=buffer_data.length;i<ni;i++)
			out.print(",",buffer_data[i]=new_buffer_data[i]);
		out.print("]");
		return ",";
	}
}
