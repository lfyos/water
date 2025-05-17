package kernel_component;

import kernel_file_manager.file_reader;

public class component_core_11 extends component_core_10
{
	public boolean selected_component_family_flag;
	
	private boolean can_display_assembly_flag[];
	
	public void destroy()
	{
		super.destroy();
		can_display_assembly_flag=null;
	}
	public boolean get_can_display_assembly_flag(int parameter_channel_id)
	{
		return can_display_assembly_flag[parameter_channel_id];
	}
	public boolean caculate_assembly_flag(int parameter_channel_id)
	{
		boolean old_value=can_display_assembly_flag[parameter_channel_id];
		can_display_assembly_flag[parameter_channel_id]=true;
		
		long my_display_bitmap=multiparameter[parameter_channel_id].display_bitmap;
		for(int i=0,my_children_number=children.size();i<my_children_number;i++){
			component my_child=children.get(i);
			if(	  (my_child.get_children_location_modify_flag())
				||(my_child.uniparameter.effective_selected_flag)
				||(my_child.multiparameter[parameter_channel_id].display_bitmap!=my_display_bitmap)
				||(my_child.multiparameter[parameter_channel_id].effective_display_flag?false:true)
				||(my_child.get_can_display_assembly_flag(parameter_channel_id)?false:true)
			){
				can_display_assembly_flag[parameter_channel_id]=false;
				break;
			}
		}
		return (can_display_assembly_flag[parameter_channel_id])^old_value;
	}
	public component_core_11(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);

		selected_component_family_flag	=false;
		can_display_assembly_flag		=new boolean[multiparameter.length];
		for(int i=0,ni=can_display_assembly_flag.length;i<ni;i++)
			can_display_assembly_flag[i]=true;
	}
}
