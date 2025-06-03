package engine_interface;

@jakarta.servlet.annotation.WebServlet(	
/*	
	initParams= {
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_data_configure_file",
			value	=	"F:/water_all/data/configure.txt"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_temparatory_configure_file",
			value	=	"F:/temp/configure.txt"
		),
		@jakarta.servlet.annotation.WebInitParam(
			name	=	"lfy_environment_configure_file",
			value	=	"F:/water_all/environment.txt"
		)
	},
*/	
	asyncSupported = true,
	urlPatterns = { 
		"/water" 
	}
)
public class annotation_webgpu_servlet extends scene_servlet
{
	private static final long serialVersionUID = 1L;

	public annotation_webgpu_servlet()
	{		
		super(	"system_environment_variable",
				"lfy_data_configure_file",
				"lfy_temparatory_configure_file",
				"lfy_environment_configure_file");
		
/*		
		super(	"servlet_initialization_parameter",
    			"lfy_data_configure_file",
    			"lfy_temparatory_configure_file",
    			"lfy_environment_configure_file");

*/		
/*
		super(	"webserver_configure_file",
				"configure.txt",null,null);
*/		

/*
		super(	"file_initialization_parameter",
				"F:/water_all/data/configure.txt",
				"F:/temp/configure.txt",
				"F:/water_all/environment.txt");
*/
	};
}
