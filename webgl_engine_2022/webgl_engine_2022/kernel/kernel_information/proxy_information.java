package kernel_information;

import kernel_engine.client_information;

public class proxy_information extends jason_creator
{
	private client_information ci;
	
	public void print()
	{	
		print("file_proxy_url",ci.get_all_file_proxy_url());
		print("file_proxy_encode_flag",ci.get_all_file_proxy_encode_flag());
	}
	public proxy_information(client_information my_ci)
	{
		super(my_ci.request_response);
		ci=my_ci;
	}
}
