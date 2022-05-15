package kernel_component;

import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_driver.component_driver;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_container_for_part_search;

public class component_core_3 extends component_core_2
{
	public int fix_render_driver_id;
	public component_driver	driver_array[];
	
	public void destroy()
	{
		super.destroy();
		
		if(driver_array==null)
			return;
		for(int i=0,ni=driver_number();i<ni;i++)
			if(driver_array[i]!=null){
				try {
					driver_array[i].destroy();
				}catch(Exception e) {
					debug_information.println("Execute component driver destroy fail:	",e.toString());
					e.printStackTrace();
				}
				driver_array[i]=null;
			}
		driver_array=null;
	}
	public int driver_number()
	{
		if(driver_array==null)
			return 0;
		else
			return driver_array.length;	
	}
	private void create_driver(engine_kernel ek,component_load_source_container component_load_source_cont,
			client_request_response request_response,file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,part_type_string_sorter type_string_sorter)
	{
		part parts[];
		String search_part_name=change_part_name.search_change_name(part_name,part_name);
		if((parts=pcfps.search_part(search_part_name))==null){
			search_part_name=change_part_name.search_change_name(search_part_name,search_part_name);
			parts=pcfps.search_part(search_part_name);
		}
		fix_render_driver_id=-1;
		if(parts==null) {
			driver_array=null;
			return;
		}
		int type_string_number=type_string_sorter.get_number();
		driver_array=new component_driver[parts.length];
		
		for(int i=parts.length-1;i>=0;i--){
			driver_array[i]=null;
			if(type_string_number>0)
				if(type_string_sorter.search(parts[i].part_par.part_type_string)<0)
					continue;
			fr.mark_start();
			try{
				driver_array[i]=parts[i].driver.create_component_driver(
						fr,i>0,parts[i],component_load_source_cont,ek,request_response);
			}catch(Exception e){
				debug_information.println("create_component_driver fail:	",e.toString());
				debug_information.println("Part user name:",	parts[i].user_name);
				debug_information.println("Part system name:",	parts[i].system_name);
				debug_information.println("Mesh_file_name:",	parts[i].directory_name+parts[i].mesh_file_name);
				debug_information.println("Material_file_name:",parts[i].directory_name+parts[i].material_file_name);
				e.printStackTrace();
			}
			fr.mark_terminate(i>0);
		}
		int n=0;
		for(int i=0,ni=driver_array.length;i<ni;i++)
			if(driver_array[i]!=null)
				driver_array[n++]=driver_array[i];
		if(n==0){
			driver_array=null;
			return;
		}
		if(parts.length==n)
			return;
		component_driver old_driver[]=driver_array;
		driver_array=new component_driver[n];
		for(int i=0;i<n;i++)
			driver_array[i]=old_driver[i];
		return;
	}
	public component_core_3(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,change_name change_part_name,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,change_part_name,ccp);
		
		create_driver(ccp.ek,ccp.clsc,ccp.request_response,fr,ccp.pcfps,change_part_name,ccp.type_string_sorter);
	}
}
