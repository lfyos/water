package kernel_component;

import java.util.ArrayList;

import kernel_part.part;
import kernel_driver.component_driver;
import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_common_class.name_exist_tester;
import kernel_common_class.debug_information;

public class component_core_2 extends component_core_1
{
	public ArrayList<component_driver> driver_array;
	
	public void destroy()
	{
		super.destroy();
		
		if(driver_array!=null){
			for(component_driver my_driver:driver_array)
				if(my_driver!=null)
					try {
						my_driver.destroy();
					}catch(Exception e) {
						e.printStackTrace();
						debug_information.println("Execute component driver destroy fail:	",e.toString());
					}
			driver_array.clear();
		}
	}
	private void create_driver(file_reader fr,component_construction_parameter ccp)
	{
		part my_part;
		change_name change_part_name;
		ArrayList<part> search_parts,effective_parts;

		if((change_part_name=ccp.get_change_part_name())==null)
			search_parts=ccp.sk.part_search_cont.search_part(part_name);
		else{
			String search_part_name=change_part_name.search_change_name(part_name,part_name);
			if((search_parts=ccp.sk.part_search_cont.search_part(search_part_name))==null){
				search_part_name=change_part_name.search_change_name(search_part_name,search_part_name);
				search_parts=ccp.sk.part_search_cont.search_part(search_part_name);
			}
		}
		if(search_parts==null)
			return;
		if(search_parts.size()<=0)
			return;
		effective_parts=new ArrayList<part>();
		boolean top_flag=false,bottom_flag=false;
		for(int i=0,ni=search_parts.size();i<ni;i++){
			my_part=search_parts.get(i);
			if(my_part.is_bottom_box_part()){
				if(bottom_flag)
					continue;
				bottom_flag=true;
			}
			if(my_part.is_top_box_part()){
				if(top_flag)
					continue;
				top_flag=true;
			}
			effective_parts.add(my_part);
		}
		if(effective_parts.size()<=0)
			return;
		
		name_exist_tester tester;
		if((tester=ccp.get_part_type_string_tester())!=null)
			if(tester.size()>0){
				search_parts=effective_parts;
				effective_parts=new ArrayList<part>();
				for(int i=0,part_number=search_parts.size();i<part_number;i++) {
					my_part=search_parts.get(i);
					if(tester.test_exist(my_part.part_par.part_type_string))
						effective_parts.add(my_part);
				}
				if(effective_parts.size()<=0)
					return;
			}

		for(int i=0,ni=effective_parts.size();i<ni;i++){
			fr.mark_start();
			my_part=effective_parts.get(i);

			component_driver comp_driver;
			try{
				comp_driver=my_part.driver.create_component_driver(fr,
						(i<(ni-1))?true:false,my_part,ccp.clsc,ccp.sk,ccp.request_response);
			}catch(Exception e){
				comp_driver=null;
				e.printStackTrace();
				
				debug_information.println("create_component_driver fail:	",e.toString());
				debug_information.println("Part user name:",	my_part.user_name);
				debug_information.println("Part system name:",	my_part.system_name);
				debug_information.println("Mesh_file_name:",	my_part.directory_name+my_part.mesh_file_name);
				debug_information.println("Material_file_name:",my_part.directory_name+my_part.material_file_name);
			}
			if(comp_driver!=null)
				driver_array.add(comp_driver);
			fr.mark_terminate((i<(ni-1))?true:false);
		}
		return;
	}
	public component_core_2(String token_string,file_reader fr,
			boolean part_list_flag,boolean normalize_location_flag,
			component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		driver_array=new ArrayList<component_driver>();
		create_driver(fr,ccp);
	}
}
