package engine_interface;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.annotation.WebServlet;

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
