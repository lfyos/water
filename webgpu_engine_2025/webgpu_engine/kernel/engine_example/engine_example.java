package engine_example;

import engine_servlet.engine_servlet;

@jakarta.servlet.annotation.WebServlet(	
	initParams= {
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_init_scene_configure_class_name",
			value	=	"engine_example.engine_example"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_init_scene_configure_file_name",
			value	=	"configure.txt"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_init_scene_configure_charset_name",
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
public class engine_example extends engine_servlet
{
	private static final long serialVersionUID = 1L;
	
	public engine_example()
	{
		super(	"lfy_init_scene_configure_class_name",
				"lfy_init_scene_configure_file_name",
				"lfy_init_scene_configure_charset_name");
	}
}
