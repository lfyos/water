package kernel_information;

import kernel_engine.client_information;

public class browser_information extends jason_creator
{
	private client_information ci;
	
	public void print()
	{
		print("client_id",			ci.request_response.implementor.get_client_id());
		print("client_charset",		ci.request_response.implementor.get_request_charset());
		print("channel_id",			ci.channel_id);
		print("url",				ci.request_response.implementor.get_url());
	}
	public browser_information(client_information my_ci)
	{
		super(my_ci.request_response);
		ci=my_ci;
	}
}
