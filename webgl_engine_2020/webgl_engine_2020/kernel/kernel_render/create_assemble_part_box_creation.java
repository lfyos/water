package kernel_render;

import kernel_component.component;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_transformation.box;
import kernel_transformation.location;

public class create_assemble_part_box_creation 
{
	private int pointer;
	
	private location loca[];
	private box b[];
	private String material[][];
	
	private void create_location_box_and_material(
			create_assemble_part_driver_selection selection,
			component comp,location nega,double length2)
	{
		int children_number;
		
		if((children_number=comp.children_number())>0){
			for(int i=0;i<children_number;i++)
				create_location_box_and_material(selection,comp.children[i],nega,length2);
			return;
		}
		
		part p;
		for(int i=0,ni=comp.driver_number();i<ni;i++)	
			if((p=comp.driver_array[i].component_part)!=null){
				if((b[pointer]=p.secure_caculate_part_box(
						null,-1,-1,-1,-1,-1,-1,null,null))!=null)
					if(b[pointer].distance2()>length2){
						loca[pointer]=nega.multiply(comp.absolute_location);
						material[pointer]=p.part_mesh.box_material;
						selection.register(p,b[pointer].volume_for_compare());
						pointer++;
						break;
					}
			}
	}
	private int part_component_number(component comp)
	{
		int child_number=comp.children_number();
		
		if(child_number<=0)
			return (comp.driver_number()>0)?1:0;
		
		int ret_val=0;
		for(int i=0;i<child_number;i++)
			ret_val+=part_component_number(comp.children[i]);
		return ret_val;
	}
	
	public part_rude topbox_part_rude;
	public part biggest_part;
	
	public create_assemble_part_box_creation(component comp,double discard_top_part_component_precision2)
	{
		box my_box=comp.get_component_box(false);
		if(my_box==null)
			return;
		
		if((pointer=part_component_number(comp))<=0)
			return;
		
		loca	=new location	[pointer];
		b		=new box		[pointer];
		material=new String 	[pointer][];
		
		pointer	=0;
		
		create_assemble_part_driver_selection selection=new create_assemble_part_driver_selection();
		
		create_location_box_and_material(selection,comp,comp.absolute_location.negative(),
										my_box.distance2()*discard_top_part_component_precision2);
		topbox_part_rude=null;
		biggest_part=null;
		if(pointer>0)
			if((biggest_part=selection.get_biggest_part())!=null)
				topbox_part_rude=new part_rude(pointer,loca,b,material,
						biggest_part.part_mesh.origin_material,
						biggest_part.part_mesh.box_attribute_double,
						biggest_part.part_mesh.box_attribute_string);
	}
}