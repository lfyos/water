package engine_interface;

@jakarta.servlet.annotation.WebServlet(
	asyncSupported = true,
	urlPatterns = { 
		"/webgpu_engine" 
	}
)
public class webgpu_engine extends webgpu_servlet 
{
	private static final long serialVersionUID = 1L;
}
