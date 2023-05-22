package kernel_component;

import java.util.ArrayList;

import kernel_common_class.debug_information;
import kernel_driver.component_driver;
import kernel_file_manager.file_reader;
import kernel_part.part;

public class component_core_3 extends component_core_2
{
	public int fix_render_driver_id;
	public ArrayList<component_driver>	driver_array;
	
	public void destroy()
	{
		super.destroy();
		
		if(driver_array==null)
			return;
		component_driver c_d;
		for(int i=0,ni=driver_number();i<ni;i++)
			if((c_d=driver_array.get(i))!=null){
				try {
					c_d.destroy();
				}catch(Exception e) {
					debug_information.println("Execute component driver destroy fail:	",e.toString());
					e.printStackTrace();
				}
				driver_array.set(i,null);
			}
		driver_array=null;
	}
	public int driver_number()
	{
		if(driver_array==null)
			return 0;
		else
			return driver_array.size();	
	}
	private void create_driver(file_reader fr,component_construction_parameter ccp)
	{
		part p;
		ArrayList<part> parts;
		
		String search_part_name=ccp.change_part_name.search_change_name(part_name,part_name);
		if((parts=ccp.pcfps.search_part(search_part_name))==null){
			search_part_name=ccp.change_part_name.search_change_name(search_part_name,search_part_name);
			parts=ccp.pcfps.search_part(search_part_name);
		}
		fix_render_driver_id=-1;
		if(parts==null) {
			driver_array=null;
			return;
		}
		
		int part_number=parts.size(),effective_part_number=0;
		part part_array[]=new part[part_number];
		int type_string_number=ccp.type_string_sorter.get_number();
		for(int i=0;i<part_number;i++) {
			if((p=parts.get(i))==null)
				continue;
			if(type_string_number>0)
				if(ccp.type_string_sorter.search(p.part_par.part_type_string)<0)
					continue;
			part_array[effective_part_number++]=p;
		}
		
		driver_array=new ArrayList<component_driver>();
		
		for(int i=0;i<effective_part_number;i++){
			boolean rollback_flag=(i<(effective_part_number-1))?true:false;
			fr.mark_start();
			try{
				driver_array.add(i,part_array[i].driver.create_component_driver(
						fr,rollback_flag,part_array[i],ccp.clsc,ccp.ek,ccp.request_response));
			}catch(Exception e){
				debug_information.println("create_component_driver fail:	",e.toString());
				debug_information.println("Part user name:",	part_array[i].user_name);
				debug_information.println("Part system name:",	part_array[i].system_name);
				debug_information.println("Mesh_file_name:",	part_array[i].directory_name+part_array[i].mesh_file_name);
				debug_information.println("Material_file_name:",part_array[i].directory_name+part_array[i].material_file_name);
				e.printStackTrace();
			}
			fr.mark_terminate(rollback_flag);
		}
		return;
	}
	public component_core_3(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,double lod_precision_scale,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,lod_precision_scale,ccp);
		
		create_driver(fr,ccp);
	}
}
