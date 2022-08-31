package kernel_service;

import kernel_common_class.debug_information;
import kernel_interface.client_request_switcher;

public class jsp_server extends client_request_switcher
{
	public javax.servlet.jsp.JspWriter system_call(
			String data_configure_environment_variable,
			String proxy_configure_environment_variable,
			
			javax.servlet.http.HttpServletRequest	request,
			javax.servlet.http.HttpSession			session,
			javax.servlet.http.HttpServletResponse	response,
			javax.servlet.ServletContext			application,
			javax.servlet.jsp.PageContext 			pageContext,
			javax.servlet.jsp.JspWriter 			out)
	{
		process_system_call(	new jsp_network_implementation(request,session,response,application),
								data_configure_environment_variable,proxy_configure_environment_variable);
		try{
			out.clear();
		}catch(Exception e) {
			debug_information.println("jsp_server out.clear() exception:	",e.toString());
			e.printStackTrace();
		}
		return pageContext.pushBody();
	}
}
