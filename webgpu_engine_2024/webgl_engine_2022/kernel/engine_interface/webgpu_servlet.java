package engine_interface;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.annotation.WebServlet;

@WebServlet(
		name="water_servlet",
		urlPatterns = { 
			"/water"
		},
		asyncSupported = true
)
public class webgpu_servlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
    private webgpu_scene scene;
    
    public webgpu_servlet() 
    {
    	scene=new webgpu_scene();
    }
	protected void doGet(HttpServletRequest request,HttpServletResponse response)
						throws ServletException,IOException 
	{
		scene.process_system_call(request, response);
	}
	protected void doPost(HttpServletRequest request,HttpServletResponse response)
						throws ServletException,IOException 
	{
		scene.process_system_call(request,response);
	}
}
