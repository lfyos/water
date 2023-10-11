package kernel_servlet;

import java.io.IOException;
import java.nio.charset.Charset;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kernel_common_class.common_reader;
import kernel_common_class.class_file_reader;
import kernel_interface.client_request_switcher;

@WebServlet(
			urlPatterns = 
			{ 
				"/liufuyan_engine" 
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
        
        String environment_variable_charset=Charset.defaultCharset().name();
        common_reader reader=class_file_reader.get_reader("environment_variable.txt",getClass(),
        		environment_variable_charset,environment_variable_charset);
        String data_configure_environment_variable=reader.get_string();
        String proxy_configure_environment_variable=reader.get_string();
        reader.close();
        
 		switcher=new client_request_switcher(data_configure_environment_variable,proxy_configure_environment_variable);
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
		switcher.process_system_call(new servlet_network_implementation(request,response));
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		switcher.process_system_call(new servlet_network_implementation(request,response));
	}
}
