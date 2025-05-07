package kernel_driver;

import java.util.ArrayList;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_network.network_result;
import kernel_scene.client_information;
import kernel_network.network_parameter;
import kernel_common_class.debug_information;

public class component_instance_driver
{
	private long parameter_version;
	private ArrayList<Long>render_version;
	
	public component comp;
	public int driver_id;
	
	public double instance_driver_lod_precision_scale;
	
	public instance_display_parameter display_parameter;
	
	public boolean instance_driver_can_not_bundle_render_flag;

	public void destroy()
	{
		if(render_version!=null) {
			render_version.clear();
			render_version=null;
		}
		if(comp!=null)
			comp=null;
		if(display_parameter!=null) {
			display_parameter.destroy();
			display_parameter=null;
		}
	}
	public long get_component_parameter_version()
	{
		return parameter_version;
	}
	public void update_component_parameter_version(long new_version)
	{
		parameter_version=(new_version>0)?new_version:(parameter_version>0)?1:0;
	}
	public long get_component_render_version(int target_id)
	{
		return (target_id>=render_version.size())?0:(render_version.get(target_id).longValue());
	}
	public void update_component_render_version(int target_id,long new_version)
	{
		while (target_id>=render_version.size())
			render_version.add(Long.valueOf(0));
		
		long my_version=(new_version>0)?new_version:(get_component_render_version(target_id)>0)?1:0;
		render_version.set(target_id,Long.valueOf(my_version));
	}
	public static network_result execute_component_function(
			int component_id,int driver_id,network_parameter parameter[],
			scene_kernel sk,client_information ci)
	{	
		component comp;
		component_instance_driver in_dr;
		network_result my_result=null;
		
		if((comp=sk.component_cont.get_component(component_id))!=null)
			if((driver_id>=0)&&(driver_id<comp.driver_number()))
				if((in_dr=ci.component_instance_driver_cont.
					get_component_instance_driver(comp, driver_id))!=null)
				{
					ci.request_response.install_parameter(parameter);
					String ret_val[];
					try{
						ret_val=in_dr.response_component_event(sk,ci);
					}catch(Exception e){
						e.printStackTrace();
						
						ret_val=null;
						part my_part=comp.driver_array.get(driver_id).component_part;
						
						debug_information.println("3.Execute response_component_event fail:	",e.toString());
						debug_information.println("Component name:	",	comp.component_name);
						debug_information.println("Driver ID:		",	driver_id);
						debug_information.println("Part user name:	",	my_part.user_name);
						debug_information.println("Part system name:",	my_part.system_name);
						debug_information.println("Mesh file name:	",	my_part.directory_name+my_part.mesh_file_name);
						
					}
					my_result=new network_result(ret_val);
					my_result.next=ci.request_response.get_network_result();
				}
		return my_result;
	}
	public component_instance_driver(component my_comp,int my_driver_id)
	{
		comp						=my_comp;
		driver_id					=my_driver_id;

		parameter_version			=0;
		render_version				=new ArrayList<Long>();
		
		instance_driver_lod_precision_scale	=1.0;
		
		display_parameter			=new instance_display_parameter();
		
		instance_driver_can_not_bundle_render_flag=true;
	}
	public void response_init_component_data(scene_kernel sk,client_information ci)
	{
	}
	public boolean check(scene_kernel sk,client_information ci,camera_result cr)
	{
		return true;
	}
	public void create_render_parameter(scene_kernel sk,client_information ci,camera_result cr)
	{
		ci.request_response.print("0");
	}
	public void create_component_parameter(scene_kernel sk,client_information ci)
	{
		ci.request_response.print(comp.component_id);
	}
	public String[] response_component_event(scene_kernel sk,client_information ci)
	{	
		return null;
	}
}
