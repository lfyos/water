package kernel_servlet;

import kernel_interface.client_request_switcher;

public class servlet_server
{
	private client_request_switcher switcher;
	
	public servlet_server()
	{
		switcher=new client_request_switcher();
	}
	public void destroy()
	{
		if(switcher!=null){
			switcher.destroy();
			switcher=null;
		}
	}
	public void system_call(
			String data_configure_environment_variable,
			String proxy_configure_environment_variable,
			
			javax.servlet.http.HttpServletRequest	request,
			javax.servlet.http.HttpServletResponse	response)
	{
		switcher.process_system_call(
				data_configure_environment_variable,
				proxy_configure_environment_variable,
				new servlet_network_implementation(request,response));
	}
}
