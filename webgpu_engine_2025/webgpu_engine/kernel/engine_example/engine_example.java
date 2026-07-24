package engine_example;

import engine_servlet.engine_parameter_servlet;

@jakarta.servlet.annotation.WebServlet(	
	initParams= {
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_scene_servlet_type",
//			value	=	"servlet_initialization_parameter"
//			value	=	"environment_variable_parameter"
//			value	=	"webserver_configure_parameter"
			value	=	"java_configure_parameter"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_scene_configure_file",
			value	=	"configure.txt"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_scene_configure_charset",
			value	=	"UTF-8"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_data_configure_file",
			value	=	"G:/water_all/data/configure.txt"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_temparatory_configure_file",
			value	=	"G:/temp/configure.txt"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_environment_configure_file",
			value	=	"G:/water_all/environment.txt"
		)
	},
	asyncSupported = true,
	urlPatterns = { 
		"/water" 
	}
)
public class engine_example extends engine_parameter_servlet
{
	private static final long serialVersionUID = 1L;
	
	public Class<?> get_engine_configure_class()
	{
		return getClass();
	};
}
