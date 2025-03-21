
package kernel_driver;

import kernel_common_class.const_value;
import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_network.client_request_response;
import kernel_scene.scene_kernel;

public class component_instance_driver_container
{
	private component_instance_driver	component_driver_array[][];
	private double instance_lod_precision_scale[];
	
	public void destroy()
	{
		if(component_driver_array!=null) {
			for(int i=0,ni=component_driver_array.length;i<ni;i++)
				if(component_driver_array[i]!=null){
					for(int j=0,nj=component_driver_array[i].length;j<nj;j++)
						if(component_driver_array[i][j]!=null){
							try {
								component_driver_array[i][j].destroy();
							}catch(Exception e) {
								e.printStackTrace();
								
								debug_information.println("instance driver destroy fail:	",e.toString());
								
							}
							component_driver_array[i][j]=null;
						}
					component_driver_array[i]=null;
				}
			component_driver_array=null;
		}
		instance_lod_precision_scale=null;
	}
	public double get_lod_precision_scale(component comp)
	{
		return instance_lod_precision_scale[comp.component_id];
	}
	public void reset_precision_scale(component comp)
	{
		for(int i=0,child_number=comp.children_number();i<child_number;i++)
			reset_precision_scale(comp.children[i]);
		
		instance_lod_precision_scale[comp.component_id]=-1.0;
		for(int i=0,child_number=comp.children_number();i<child_number;i++){
			double scale=instance_lod_precision_scale[comp.children[i].component_id];
			if(instance_lod_precision_scale[comp.component_id]<scale)
				instance_lod_precision_scale[comp.component_id]=scale;
		}
		for(int i=0,driver_number=comp.driver_number();i<driver_number;i++){
			double scale=component_driver_array[comp.component_id][i].instance_driver_lod_precision_scale;
			scale*=comp.driver_array.get(i).component_part.part_par.lod_precision_scale;
			if(instance_lod_precision_scale[comp.component_id]<scale)
				instance_lod_precision_scale[comp.component_id]=scale;
		}
		if(instance_lod_precision_scale[comp.component_id]<const_value.min_value)
			instance_lod_precision_scale[comp.component_id]=1.0;
	}
	private void reset_drivers(component comp,scene_kernel sk,
			client_request_response request_response)
	{
		int driver_number=comp.driver_number(),child_number=comp.children_number();
		
		for(int i=0;i<child_number;i++)
			reset_drivers(comp.children[i],sk,request_response);
		
		if(driver_number<=0)
			component_driver_array			[comp.component_id]=null;
		else{
			component_driver_array			[comp.component_id]=new component_instance_driver[driver_number];
			for(int i=0;i<driver_number;i++){
				component_instance_driver i_d;
				component_driver c_d=comp.driver_array.get(i);
				try{
					i_d=c_d.create_component_instance_driver(comp,i,sk,request_response);
				}catch(Exception e){
					e.printStackTrace();
					
					i_d=null;
					debug_information.println("create_instance_driver fail:	",e.toString());
					
					debug_information.println("Component name:",comp.component_name);
					debug_information.println("Component file:",comp.component_directory_name+comp.component_file_name);
					debug_information.println("Component driver id:",i);
					
					debug_information.println("Part user name:	",		c_d.component_part.user_name);
					debug_information.println("Part system name:	",	c_d.component_part.system_name);
					debug_information.println("Directory name:	",		c_d.component_part.directory_name);
					debug_information.println("Mesh file name:	",		c_d.component_part.mesh_file_name);
					
				}
				component_driver_array[comp.component_id][i]=i_d;
			}
		}
	}
	public component_instance_driver get_component_instance_driver(component comp,int driver_id)
	{
		if(component_driver_array[comp.component_id]==null)
			return null;
		if((driver_id<0)||(driver_id>=(component_driver_array[comp.component_id].length)))
			return null;
		return component_driver_array[comp.component_id][driver_id];
	}
	public component_instance_driver_container(scene_kernel sk,client_request_response request_response)
	{
		component root_component		=sk.component_cont.root_component;
		int max_component_number		=root_component.component_id+1;
		component_driver_array			=new component_instance_driver[max_component_number][];
		instance_lod_precision_scale	=new double[max_component_number];
		reset_drivers(root_component,sk,request_response);
		reset_precision_scale(root_component);
	}
}
