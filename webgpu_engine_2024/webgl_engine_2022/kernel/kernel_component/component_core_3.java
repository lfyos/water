package kernel_component;

import java.util.ArrayList;

import kernel_part.part;
import kernel_driver.component_driver;
import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_engine.part_type_string_sorter;
import kernel_common_class.debug_information;

public class component_core_3 extends component_core_2
{
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
					e.printStackTrace();
					
					debug_information.println("Execute component driver destroy fail:	",e.toString());
					
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
		ArrayList<part> search_parts;
		change_name change_part_name;
		
		if((change_part_name=ccp.get_change_part_name())==null)
			search_parts=ccp.pcfps.search_part(part_name);
		else{
			String search_part_name=change_part_name.search_change_name(part_name,part_name);
			if((search_parts=ccp.pcfps.search_part(search_part_name))==null){
				search_part_name=change_part_name.search_change_name(search_part_name,search_part_name);
				search_parts=ccp.pcfps.search_part(search_part_name);
			}
		}
		if(search_parts==null){
			driver_array=null;
			return;
		}
		
		ArrayList<part> effective_parts=new ArrayList<part>();
		part_type_string_sorter ptss=ccp.get_part_type_string_sorter();
		int type_string_number=(ptss==null)?0:ptss.get_number();
		
		for(int i=0,part_number=search_parts.size();i<part_number;i++)
			if((p=search_parts.get(i))!=null){
				if(type_string_number>0)
					if(ptss.search(p.part_par.part_type_string)<0)
						continue;
				effective_parts.add(p);
			}
		
		driver_array=new ArrayList<component_driver>();
		
		for(int i=0,effective_part_number=effective_parts.size();i<effective_part_number;i++){
			fr.mark_start();
			p=effective_parts.get(i);
			boolean rollback_flag=(i<(effective_part_number-1))?true:false;
			component_driver comp_driver=null;
			try{
				comp_driver=p.driver.create_component_driver(
						fr,rollback_flag,p,ccp.clsc,ccp.ek,ccp.request_response);
			}catch(Exception e){
				e.printStackTrace();
				
				debug_information.println("create_component_driver fail:	",e.toString());
				debug_information.println("Part user name:",	p.user_name);
				debug_information.println("Part system name:",	p.system_name);
				debug_information.println("Mesh_file_name:",	p.directory_name+p.mesh_file_name);
				debug_information.println("Material_file_name:",p.directory_name+p.material_file_name);
			}
			if(comp_driver!=null)
				driver_array.add(comp_driver);
			fr.mark_terminate(rollback_flag);
		}
		
		if(driver_array.size()<=0)
			driver_array=null;
		return;
	}
	
	public component_core_3(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		create_driver(fr,ccp);
	}
}
