package engine_interface;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class webgpu_servlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
    private webgpu_engine engine;
    
    public webgpu_servlet() 
    {
    	engine=new webgpu_engine();
    }
	protected void doGet(HttpServletRequest request,HttpServletResponse response)
						throws ServletException,IOException 
	{
		engine.process_call(request, response);
	}
	protected void doPost(HttpServletRequest request,HttpServletResponse response)
						throws ServletException,IOException 
	{
		engine.process_call(request,response);
	}
	public void process_call(HttpServletRequest request,HttpServletResponse response)
	{
		engine.process_call(request,response);
	}
}
