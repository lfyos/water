package engine_interface;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
		name="lfy_webgpu_engine",
		urlPatterns = 
		{ 
			"/lfy_webgpu_engine" 
		},
		asyncSupported = true
)
public class webgpu_servlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
    private webgpu_engine engine;   
    
    public webgpu_servlet() 
    {
    	engine=new webgpu_engine("lfy_data_dir","lfy_temp_dir");
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		engine.process_call(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		engine.process_call(request, response);
	}
}
