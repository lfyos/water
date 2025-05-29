package engine_interface;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(
	asyncSupported = true,
	urlPatterns = { 
		"/water" 
	}
)
public class annotation_webgpu_servlet extends environment_webgpu_servlet
{
	private static final long serialVersionUID = 1L;
}
