package kernel_create_top_assemble_part;

import java.util.ArrayList;

import kernel_part.part;
import kernel_part.part_rude;
import kernel_transformation.box;
import kernel_component.component;
import kernel_transformation.location;

public class create_part_rude 
{
	public part select_ref_part;
	public part_rude topbox_part_rude;
	
	private ArrayList<part> reference_part;
	private ArrayList<location> box_loca;
	private ArrayList<box> box_array;
	
	private double max_distance2;
	
	private void create_location_box_and_material(component comp,location nega,double length2)
	{
		part p;
		int children_number;
		double my_distance2;
		
		if((children_number=comp.children.size())>0) {
			for(int i=0;i<children_number;i++)
				create_location_box_and_material(comp.children.get(i),nega,length2);
			return;
		}
		for(int i=0,ni=comp.driver_array.size();i<ni;i++) {
			if((p=comp.driver_array.get(i).component_part)==null)
				continue;
			box my_box=p.secure_caculate_part_box(null,-1,-1,-1,-1,-1,-1,-1,null,null);
			if(my_box==null)
				continue;
			if((my_distance2=my_box.distance2())<length2)
				continue;
			if((select_ref_part==null)||(max_distance2<my_distance2)){
				max_distance2=my_distance2;
				select_ref_part=p;
			}
			reference_part.add(p);
			box_array.add(my_box);
			box_loca.add(nega.multiply(comp.absolute_location));
			return;
		}
	}
	public create_part_rude(component comp,double discard_top_part_component_precision2,part my_ref_part)
	{
		box my_box;
		select_ref_part	=null;
		topbox_part_rude=null;
		if((my_box=comp.get_component_box(false))==null)
			return;
		max_distance2	=0;
		reference_part	=new ArrayList<part>();
		box_loca		=new ArrayList<location>();
		box_array		=new ArrayList<box>();
		create_location_box_and_material(comp,comp.caculate_negative_absolute_location(),
			my_box.distance2()*discard_top_part_component_precision2);
		
		int box_number=box_array.size();
		if((box_number>1)&&(select_ref_part!=null)) {
			select_ref_part=(my_ref_part==null)?select_ref_part:my_ref_part;
			topbox_part_rude=new part_rude(
				select_ref_part.part_mesh,box_number,
				reference_part.toArray(new part[box_number]),
				box_loca.toArray(new location[box_number]),
				box_array.toArray(new box[box_number]));
		}else{
			select_ref_part	=null;
			topbox_part_rude=null;
		}
	}
}
