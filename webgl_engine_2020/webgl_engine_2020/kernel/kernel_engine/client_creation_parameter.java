package kernel_engine;

import kernel_network.client_request_response;

public class client_creation_parameter
{
	public int max_client_loading_number;
	
	public client_creation_parameter(engine_kernel ek,
			client_request_response request_response)
	{
		String str;
		int new_max_loading_number;
		max_client_loading_number=ek.system_par.default_max_loading_number;
		if((str=request_response.get_parameter("max_loading_number"))!=null)	
			if((new_max_loading_number=Integer.decode(str))>0)
				max_client_loading_number=new_max_loading_number;
	}
}
