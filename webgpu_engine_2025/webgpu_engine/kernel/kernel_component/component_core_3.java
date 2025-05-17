package kernel_component;

import java.util.ArrayList;

import kernel_part.part;
import kernel_driver.component_driver;
import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_scene.part_type_string_sorter;
import kernel_common_class.debug_information;

public class component_core_3 extends component_core_2
{
	public ArrayList<component_driver>	driver_array;
	
	public void destroy()
	{
		super.destroy();

		component_driver c_d;
		for(int i=driver_array.size()-1;i>=0;i--)
			if((c_d=driver_array.remove(i))!=null)
				try {
					c_d.destroy();
				}catch(Exception e) {
					e.printStackTrace();
					debug_information.println("Execute component driver destroy fail:	",e.toString());
				}
		driver_array.clear();
	}
	private void create_driver(file_reader fr,component_construction_parameter ccp)
	{
		ArrayList<part> search_parts;
		change_name change_part_name;
		
		driver_array=new ArrayList<component_driver>();
		
		if((change_part_name=ccp.get_change_part_name())==null)
			search_parts=ccp.pcfps.search_part(part_name);
		else{
			String search_part_name=change_part_name.search_change_name(part_name,part_name);
			if((search_parts=ccp.pcfps.search_part(search_part_name))==null){
				search_part_name=change_part_name.search_change_name(search_part_name,search_part_name);
				search_parts=ccp.pcfps.search_part(search_part_name);
			}
		}
		if(search_parts==null)
			return;
		if(search_parts.size()<=0)
			return;

		part_type_string_sorter ptss=ccp.get_part_type_string_sorter();
		int type_string_number=(ptss==null)?0:ptss.get_number();
		
		part p;
		ArrayList<part> effective_parts;
		
		if(type_string_number<=0)
			effective_parts=search_parts;
		else{
			effective_parts=new ArrayList<part>();
			for(int i=0,part_number=search_parts.size();i<part_number;i++)
				if((p=search_parts.get(i))!=null)
					if(ptss.search(p.part_par.part_type_string)>=0)
						effective_parts.add(p);
		}

		for(int i=0,ni=effective_parts.size();i<ni;i++){
			p=effective_parts.get(i);
			
			fr.mark_start();
			component_driver comp_driver;
			
			try{
				comp_driver=p.driver.create_component_driver(fr,
						(i<(ni-1))?true:false,p,ccp.clsc,ccp.sk,ccp.request_response);
			}catch(Exception e){
				comp_driver=null;
				e.printStackTrace();
				
				debug_information.println("create_component_driver fail:	",e.toString());
				debug_information.println("Part user name:",	p.user_name);
				debug_information.println("Part system name:",	p.system_name);
				debug_information.println("Mesh_file_name:",	p.directory_name+p.mesh_file_name);
				debug_information.println("Material_file_name:",p.directory_name+p.material_file_name);
			}
			if(comp_driver!=null)
				driver_array.add(comp_driver);
			fr.mark_terminate((i<(ni-1))?true:false);
		}
		return;
	}
	public component_core_3(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		create_driver(fr,ccp);
	}
}
