package engine_example;

import engine_servlet.engine_servlet;

@jakarta.servlet.annotation.WebServlet(
	initParams= {
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_data_configure_file",
			value	=	"G:/water_all/data/configure.txt"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_temparatory_configure_file",
			value	=	"G:/temp/configure.txt"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_user_environment_file",
			value	=	"G:/water_all/user_environment.txt"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_data_charset",
			value	=	"GBK"
		)
	},
	asyncSupported = true,
	urlPatterns = { 
		"/water" 
	}
)
public class engine_example extends engine_servlet
{
	private static final long serialVersionUID = 1L;
	
	public engine_example()
	{
		super("engine_example.engine_example","configure.txt","UTF-8");
	}
}
