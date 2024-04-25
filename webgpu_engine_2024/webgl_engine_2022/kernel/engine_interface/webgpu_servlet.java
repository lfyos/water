package engine_interface;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;

@WebServlet(
		name="servlet_server",
		urlPatterns = { 
			"/interface.servlet" 
		},
		initParams = { 
			@WebInitParam(
				name	=	"data_environment_variable", 
				value	=	"lfy_data_dir"),
			@WebInitParam(
				name	=	"temp_environment_variable", 
				value	=	"lfy_temp_dir")
		},
		asyncSupported = true
)
*/

public class webgpu_servlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
    private webgpu_engine engine;
    
    private String data_environment_variable_name;
    private String temp_environment_variable_name;
    
    public webgpu_servlet() 
    {
    	engine=new webgpu_engine();
    	data_environment_variable_name=null;
    	temp_environment_variable_name=null;
    }
    public void init(ServletConfig config) throws ServletException 
    {
    	data_environment_variable_name=config.getInitParameter("data_environment_variable");
    	temp_environment_variable_name=config.getInitParameter("temp_environment_variable");
	}
	protected void doGet(HttpServletRequest request,HttpServletResponse response)
						throws ServletException,IOException 
	{
		engine.process_system_call(request, response,
				data_environment_variable_name,temp_environment_variable_name);
	}
	protected void doPost(HttpServletRequest request,HttpServletResponse response)
						throws ServletException,IOException 
	{
		engine.process_system_call(request,response,
				data_environment_variable_name,temp_environment_variable_name);
	}
}
