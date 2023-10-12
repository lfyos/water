package kernel_servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kernel_interface.client_request_switcher;

@WebServlet(
	name="servlet_server",
	urlPatterns = 
	{ 
		"/graphics_engine_interface" 
	},
	asyncSupported = true
)
public class servlet_server extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private client_request_switcher switcher;
	
    public servlet_server()
    {
        super();
        switcher=null;
    }
    public void init(ServletConfig config) throws ServletException 
    {
    	if(switcher!=null)
			switcher.destroy();
        switcher=new client_request_switcher(config.getServletContext().getRealPath("configure.txt"));
	}
	public void destroy()
	{
		if(switcher!=null){
			switcher.destroy();
			switcher=null;
		}
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		if(switcher!=null)
			switcher.process_system_call(new servlet_network_implementation(request,response));
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		if(switcher!=null)
			switcher.process_system_call(new servlet_network_implementation(request,response));
	}
}
