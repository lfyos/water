package kernel_driver;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_network.client_request_response;
import kernel_common_class.debug_information;

public class component_driver
{
	public void destroy()
	{
		if(component_part!=null) {
			try {
				component_part.destroy();
			}catch(Exception e) {
				e.printStackTrace();
				
				debug_information.println("Execute part destroy() fail:	",e.toString());
				debug_information.println("Part user name:",			component_part.user_name);
				debug_information.println("Part system name:",			component_part.system_name);
				debug_information.println("Part mesh_file_name:",		component_part.directory_name+component_part.mesh_file_name);
				debug_information.println("Part material_file_name:",	component_part.directory_name+component_part.material_file_name);

			}
			component_part=null;
		}
	}
	private long component_parameter_version,component_render_version;
	
	public void update_component_parameter_version()
	{
		component_parameter_version++;
	}
	public long get_component_parameter_version()
	{
		return component_parameter_version;
	}
	public void update_component_render_version()
	{
		component_render_version++;
	}
	public long get_component_render_version()
	{
		return component_render_version;
	}
	public part component_part;
	public int 	same_render_component_driver_id;
	public int 	same_part_component_driver_id;

	public component_driver(part my_component_part)
	{
		component_parameter_version		=2;
		component_render_version		=2;
		
		component_part=my_component_part;
		
		same_render_component_driver_id	=0;
		same_part_component_driver_id	=0;
	}
	public void initialize_component_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
	}
	public component_instance_driver create_component_instance_driver(component comp,int driver_id,
			scene_kernel sk,client_request_response request_response)
	{
		return new component_instance_driver(comp,driver_id);
	}
}
