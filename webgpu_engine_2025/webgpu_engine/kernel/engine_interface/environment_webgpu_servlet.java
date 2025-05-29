package engine_interface;

public class environment_webgpu_servlet extends scene_servlet
{
	private static final long serialVersionUID = 1L;
	
    public environment_webgpu_servlet() 
    {
    	super(	"system_environment_variable",
    			"lfy_data_configure_file",
    			"lfy_temparatory_configure_file",
    			"lfy_environment_configure_file");
    }
}
