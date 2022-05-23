package kernel_driver;

import kernel_common_class.const_value;
import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_part.part;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;

public class instance_driver_container
{
	private instance_driver	driver_array[][];
	private double instance_lod_precision_scale[];
	
	public void destroy()
	{
		if(driver_array!=null) {
			for(int i=0,ni=driver_array.length;i<ni;i++)
				if(driver_array[i]!=null){
					for(int j=0,nj=driver_array[i].length;j<nj;j++)
						if(driver_array[i][j]!=null){
							try {
								driver_array[i][j].destroy();
							}catch(Exception e) {
								debug_information.println("instance driver destroy fail:	",e.toString());
								e.printStackTrace();
							}
							driver_array[i][j]=null;
						}
					driver_array[i]=null;
				}
			driver_array=null;
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
			double scale=driver_array[comp.component_id][i].instance_driver_lod_precision_scale;
			scale*=comp.driver_array[i].component_part.part_par.lod_precision_scale;
			if(instance_lod_precision_scale[comp.component_id]<scale)
				instance_lod_precision_scale[comp.component_id]=scale;
		}
		if(instance_lod_precision_scale[comp.component_id]<const_value.min_value)
			instance_lod_precision_scale[comp.component_id]=1.0;
	}
	private void reset_drivers(component comp,engine_kernel ek,
			client_request_response request_response)
	{
		int driver_number=comp.driver_number(),child_number=comp.children_number();
		
		for(int i=0;i<child_number;i++)
			reset_drivers(comp.children[i],ek,request_response);
		
		if(driver_number<=0)
			driver_array			[comp.component_id]=null;
		else{
			driver_array			[comp.component_id]=new instance_driver[driver_number];
			for(int i=0;i<driver_number;i++){
				instance_driver i_d;
				part p=comp.driver_array[i].component_part;
				try{
					i_d=comp.driver_array[i].create_instance_driver(comp,i,ek,request_response);
				}catch(Exception e){
					i_d=null;
					debug_information.println("create_instance_driver fail:	",e.toString());
					
					debug_information.println("Component name:",comp.component_name);
					debug_information.println("Component file:",comp.component_directory_name+comp.component_file_name);
					debug_information.println("Component driver id:",i);
					
					debug_information.println("Part user name:	",		p.user_name);
					debug_information.println("Part system name:	",	p.system_name);
					debug_information.println("Directory name:	",		p.directory_name);
					debug_information.println("Mesh file name:	",		p.mesh_file_name);
					
					e.printStackTrace();
				}
				driver_array[comp.component_id][i]=i_d;
			}
		}
	}
	public instance_driver get_driver(component comp,int driver_id)
	{
		if(driver_array[comp.component_id]==null)
			return null;
		if((driver_id<0)||(driver_id>=(driver_array[comp.component_id].length)))
			return null;
		return driver_array[comp.component_id][driver_id];
	}
	public instance_driver_container(engine_kernel ek,client_request_response request_response)
	{
		component root_component		=ek.component_cont.root_component;
		int max_component_number		=root_component.component_id+1;
		driver_array					=new instance_driver[max_component_number][];
		instance_lod_precision_scale	=new double[max_component_number];
		reset_drivers(root_component,ek,request_response);
		reset_precision_scale(root_component);
	}
}
