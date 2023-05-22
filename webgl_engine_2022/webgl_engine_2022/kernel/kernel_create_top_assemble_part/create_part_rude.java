package kernel_create_top_assemble_part;

import kernel_component.component;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_transformation.box;
import kernel_transformation.location;

public class create_part_rude 
{
	public part_rude topbox_part_rude;
	public part max_part;
	
	private int box_number;
	private part reference_part[];
	private location box_loca[];
	private box box_array[];
	
	private double max_distance2;
	
	private void create_location_box_and_material(component comp,location nega,double length2)
	{
		part p;
		int children_number;
		double my_distance2;
		
		if((children_number=comp.children_number())>0) {
			for(int i=0;i<children_number;i++)
				create_location_box_and_material(comp.children[i],nega,length2);
			return;
		}
		for(int i=0,ni=comp.driver_number();i<ni;i++) {
			if((p=comp.driver_array.get(i).component_part)==null)
				continue;
			if((box_array[box_number]=p.secure_caculate_part_box(null,-1,-1,-1,-1,-1,-1,null,null))==null)
				continue;
			if((my_distance2=box_array[box_number].distance2())<length2)
				continue;
			if((max_part==null)||(max_distance2<my_distance2)){
				max_part=p;
				max_distance2=my_distance2;
			}
			reference_part	[box_number]=p;
			box_loca		[box_number]=nega.multiply(comp.absolute_location);
			box_number++;
			return;
		}
	}
	public create_part_rude(component comp,
			int max_component_number,double discard_top_part_component_precision2)
	{
		box my_box;
		max_part		=null;
		topbox_part_rude=null;
		if((my_box=comp.get_component_box(false))==null)
			return;
		max_distance2	=0;
		box_number		=0;
		reference_part	=new part		[max_component_number];
		box_loca		=new location	[max_component_number];
		box_array		=new box		[max_component_number];
		create_location_box_and_material(comp,comp.caculate_negative_absolute_location(),
				my_box.distance2()*discard_top_part_component_precision2);
		if((box_number>1)&&(max_part!=null))
			topbox_part_rude=new part_rude(box_number,reference_part,box_loca,box_array);
		return;
	}
}
