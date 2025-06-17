package kernel_content_type;

import kernel_component.component;
import kernel_part.part;

public class create_assemble_part_name 
{
	public String can_create_assemble_part_name[];

	private int do_create(component comp)
	{
		if(comp.children.size()<=0){
			for(int i=0,ni=comp.driver_array.size();i<ni;i++){
				part comp_part=comp.driver_array.get(i).component_part;
				if(comp_part==null)
					continue;
				if(comp_part.driver==null)
					continue;
				if(comp_part.secure_caculate_part_box()==null)
					continue;
				can_create_assemble_part_name[comp.component_id]=comp_part.part_par.assemble_part_name;
				return comp_part.render_id;
			}
			can_create_assemble_part_name[comp.component_id]=null;
			return -1;
		}
		
		component my_child_comp=comp.children.get(0);
		int do_test_result=do_create(my_child_comp);
		can_create_assemble_part_name[comp.component_id]
				=can_create_assemble_part_name[my_child_comp.component_id];
		
		for(int child_do_test_result,i=1,ni=comp.children.size();i<ni;i++){
			my_child_comp=comp.children.get(i);
			if((child_do_test_result=do_create(my_child_comp))>=0)
				if(do_test_result==child_do_test_result)
					if(can_create_assemble_part_name[comp.component_id].compareTo(
						can_create_assemble_part_name[my_child_comp.component_id])==0)
							continue;
			do_test_result=-1;
			can_create_assemble_part_name[comp.component_id]=null;
		}
		return (can_create_assemble_part_name[comp.component_id]==null)?-1:do_test_result;
	}
	private create_assemble_part_name(component comp,int component_number)
	{
		can_create_assemble_part_name=new String[component_number];
		for(int i=0;i<component_number;i++)
			can_create_assemble_part_name[i]=null;
		do_create(comp);
	}
	public static String[]create(component comp,int component_number)
	{
		return new create_assemble_part_name(comp,component_number).can_create_assemble_part_name;
	}
}
