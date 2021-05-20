package kernel_information;

import kernel_engine.client_information;

public class client_creation_parameter_information extends jason_creator
{
	private client_information ci;
	public void print()
	{
		print("max_client_loading_number",
				ci.creation_parameter.max_client_loading_number);
	}
	public client_creation_parameter_information(client_information my_ci)
	{
		super(my_ci.request_response);
		ci=my_ci;
	}
}
