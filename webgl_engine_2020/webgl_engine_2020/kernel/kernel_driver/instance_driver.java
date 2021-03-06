package kernel_driver;

import kernel_camera.camera_result;
import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_network.network_parameter;
import kernel_network.network_result;
import kernel_part.part;


public class instance_driver
{
	private long parameter_version,render_version[];
	
	public component comp;
	public int driver_id;
	
	public double instance_driver_lod_precision_scale;
	
	public instance_display_parameter display_parameter;

	public void destroy()
	{
		render_version=null;
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
		if(new_version>0)
			parameter_version=new_version;
		else if(parameter_version>0)
			parameter_version=1;
		else
			parameter_version=0;
	}
	private void test_component_render_version(int render_buffer_id)
	{
		if(render_version==null){
			render_version=new long[render_buffer_id+1];
			for(int i=0;i<=render_buffer_id;i++)
				render_version[i]=0;
		}else if(render_version.length<=render_buffer_id){
			long bak[]=render_version;
			render_version=new long[render_buffer_id+1];
			for(int i=0;i<bak.length;i++)
				render_version[i]=bak[i];
			for(int i=bak.length;i<=render_buffer_id;i++)
				render_version[i]=0;
		}	
	}
	public long get_component_render_version(int render_buffer_id)
	{
		test_component_render_version(render_buffer_id);
		return render_version[render_buffer_id];
	}
	public void update_component_render_version(int render_buffer_id,long new_version)
	{
		test_component_render_version(render_buffer_id);
		if(new_version>0)
			render_version[render_buffer_id]=new_version;
		else if(render_version[render_buffer_id]>0)
			render_version[render_buffer_id]=1;
		else
			render_version[render_buffer_id]=0;
	}
	public void update_component_render_version(long new_version)
	{
		if(render_version!=null){
			int render_buffer_number=render_version.length;
			for(int render_buffer_id=0;render_buffer_id<render_buffer_number;render_buffer_id++) {
				test_component_render_version(render_buffer_id);
				if(new_version>0)
					render_version[render_buffer_id]=new_version;
				else if(render_version[render_buffer_id]>0)
					render_version[render_buffer_id]=1;
				else
					render_version[render_buffer_id]=0;
			}
		}
	}
	public static network_result execute_component_function(
			int component_id,int driver_id,network_parameter parameter[],
			engine_kernel ek,client_information ci)
	{	
		component comp;
		instance_driver in_dr;
		network_result my_result=null;
		
		if((comp=ek.component_cont.get_component(component_id))!=null)
			if((driver_id>=0)&&(driver_id<comp.driver_number()))
				if((in_dr=ci.instance_container.get_driver(comp, driver_id))!=null){
					ci.request_response.install_parameter(parameter);
					int parameter_channel_id=ci.display_camera_result.target.parameter_channel_id;
					String ret_val[];
					try{
						ret_val=in_dr.response_event(parameter_channel_id,ek,ci);
					}catch(Exception e){
						ret_val=null;
						part my_part=comp.driver_array[driver_id].component_part;
						
						debug_information.println("3.Execute response_event of instance driver fail:	",e.toString());
						debug_information.println("Component name:	",	comp.component_name);
						debug_information.println("Driver ID:		",	driver_id);
						debug_information.println("Part user name:	",	my_part.user_name);
						debug_information.println("Part system name:",	my_part.system_name);
						debug_information.println("Mesh file name:	",	my_part.directory_name+my_part.mesh_file_name);
						e.printStackTrace();
					}
					my_result=new network_result(ret_val);
					my_result.next=ci.request_response.get_network_result();
				}
		return my_result;
	}
	
	public instance_driver(component my_comp,int my_driver_id)
	{
		comp						=my_comp;
		driver_id					=my_driver_id;

		parameter_version			=0;
		render_version				=null;
		
		instance_driver_lod_precision_scale	=1.0;
		
		display_parameter			=new instance_display_parameter();
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		return true;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(comp.component_id);
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{	
		return null;
	}
}
