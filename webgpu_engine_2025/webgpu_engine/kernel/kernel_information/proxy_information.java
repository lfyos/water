package kernel_information;

import kernel_scene.client_information;

public class proxy_information extends jason_creator
{
	private String url_array[];
	
	public void print()
	{
		print("proxy",url_array);
	}
	public proxy_information(client_information my_ci)
	{
		super(my_ci.request_response);
		url_array=my_ci.get_all_file_proxy_url();
	}
}
