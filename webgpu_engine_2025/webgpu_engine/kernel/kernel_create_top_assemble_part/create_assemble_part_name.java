package kernel_create_top_assemble_part;

import kernel_component.component;
import kernel_part.part;

public class create_assemble_part_name 
{
	private String reference_part_name[];

	private int do_create(component comp)
	{
		String s,t;
		part comp_part;
		
		if(comp.children.size()<=0){
			for(var my_driver:comp.driver_array)
				if((comp_part=my_driver.component_part)!=null)
					if(comp_part.driver!=null)
						if(comp_part.secure_caculate_part_box()!=null){
							s=comp_part.part_par.reference_part_name;
							reference_part_name[comp.component_id]=s;
							return comp_part.render_id;
						}
			reference_part_name[comp.component_id]=null;
			return -1;
		}
		
		component my_child_comp=comp.children.get(0);
		int do_test_result=do_create(my_child_comp);
		s=reference_part_name[my_child_comp.component_id];
		reference_part_name[comp.component_id]=s;
		
		for(int i=1,ni=comp.children.size();i<ni;i++){
			my_child_comp=comp.children.get(i);
			int child_do_test_result=do_create(my_child_comp);
			if((child_do_test_result>=0)&&(do_test_result==child_do_test_result)) {
				s=reference_part_name[comp.component_id];
				t=reference_part_name[my_child_comp.component_id];
				if(s.compareTo(t)==0)
					continue;
			}
			do_test_result=-1;
			reference_part_name[comp.component_id]=null;
		}
		return do_test_result;
	}
	private create_assemble_part_name(component comp,int component_number)
	{
		reference_part_name=new String[component_number];
		for(int i=0;i<component_number;i++)
			reference_part_name[i]=null;
		do_create(comp);
	}
	public static String[]create(component comp,int component_number)
	{
		return new create_assemble_part_name(comp,component_number).reference_part_name;
	}
}
