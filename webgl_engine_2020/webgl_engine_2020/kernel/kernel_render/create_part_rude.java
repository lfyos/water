package kernel_render;

import kernel_component.component;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_transformation.box;
import kernel_transformation.location;

public class create_part_rude 
{
	private part_rude topbox_part_rude;
	
	private int box_number;
	private location box_loca[];
	private box box_array[];
	private String default_material[][];
	private double default_attribute_double[][];
	private String default_attribute_string[][];
	
	private void create_location_box_and_material(component comp,location nega,double length2)
	{
		part p;
		int children_number;
		
		if((children_number=comp.children_number())>0)
			for(int i=0;i<children_number;i++)
				create_location_box_and_material(comp.children[i],nega,length2);
		else
			for(int i=0,ni=comp.driver_number();i<ni;i++)
				if((p=comp.driver_array[i].component_part)!=null){
					if((box_array[box_number]=p.secure_caculate_part_box(null,-1,-1,-1,-1,-1,-1,null,null))!=null)
						if(box_array[box_number].distance2()>length2){
							box_loca				[box_number]=nega.multiply(comp.absolute_location);
							default_material		[box_number]=p.part_mesh.default_material;
							default_attribute_double[box_number]=p.part_mesh.default_attribute_double;
							default_attribute_string[box_number]=p.part_mesh.default_attribute_string;
							box_number++;
							break;
						}
				}
	}
	private create_part_rude(component comp,String origin_material[],
			int max_component_number,double discard_top_part_component_precision2)
	{
		box my_box;
		topbox_part_rude=null;
		if((my_box=comp.get_component_box(false))==null)
			return;
		box_number				=0;
		box_loca				=new location	[max_component_number];
		box_array				=new box		[max_component_number];
		default_material		=new String		[max_component_number][];
		default_attribute_double=new double		[max_component_number][];
		default_attribute_string=new String		[max_component_number][];

		create_location_box_and_material(comp,comp.absolute_location.negative(),
				my_box.distance2()*discard_top_part_component_precision2);
		
		if(box_number<=0)
			return;
		String extra_data[]=new String[box_number];
		for(int i=0;i<box_number;i++)
			extra_data[i]="1";
		topbox_part_rude=new part_rude(origin_material,
				default_material,default_attribute_double,default_attribute_string,
				box_number,box_loca,box_array,extra_data);
	}
	public static part_rude create(component comp,String origin_material[],
			int max_component_number,double discard_top_part_component_precision2)
	{
		return new create_part_rude(comp,origin_material,
			max_component_number,discard_top_part_component_precision2).topbox_part_rude;
	}
}
