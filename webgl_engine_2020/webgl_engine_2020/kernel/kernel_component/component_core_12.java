package kernel_component;

import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;


public class component_core_12  extends component_core_11
{
public boolean selected_component_family_flag,can_display_assembly_set_flag;
	
	private boolean can_display_assembly_flag[];
	
	public void destroy()
	{
		super.destroy();
		can_display_assembly_flag=null;
	}
	
	public boolean get_can_display_assembly_flag(int parameter_channel_id)
	{
		return can_display_assembly_flag[parameter_channel_id]&can_display_assembly_set_flag;
	}
	public boolean caculate_assembly_flag(int parameter_channel_id)
	{
		int my_children_number=children_number();
		boolean old_value=can_display_assembly_flag[parameter_channel_id];
		
		can_display_assembly_flag[parameter_channel_id]=true;
		if(my_children_number<=0)
			return !old_value;
		
		long my_display_bitmap=multiparameter[parameter_channel_id].display_bitmap;
		for(int i=0;i<my_children_number;i++){
			long child_display_bitmap=children[i].multiparameter[parameter_channel_id].display_bitmap;
			if(	  (children[i].uniparameter.effective_selected_flag)
				||(my_display_bitmap!=child_display_bitmap)
				||(children[i].children_location_modify_flag))
			{
				can_display_assembly_flag[parameter_channel_id]=false;
				return old_value;
			}
		}
		for(int i=0;i<my_children_number;i++){
			boolean c_flag=children[i].get_effective_display_flag(parameter_channel_id);
			c_flag&=children[i].get_can_display_assembly_flag(parameter_channel_id);
			can_display_assembly_flag[parameter_channel_id]&=c_flag;
		}
		return (can_display_assembly_flag[parameter_channel_id])^old_value;
	}
	
	public component_core_12(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			change_name reverse_mount_component_name,part_type_string_sorter type_string_sorter,
			boolean part_list_flag,long default_display_bitmap,int max_child_number)
	{
		super(token_string,ek,request_response,fr,pcfps,
			change_part_name,mount_component_name,reverse_mount_component_name,
			type_string_sorter,part_list_flag,default_display_bitmap,max_child_number);

		selected_component_family_flag	=false;
		can_display_assembly_set_flag	=true;
		can_display_assembly_flag		=new boolean[multiparameter.length];
		for(int i=0,ni=can_display_assembly_flag.length;i<ni;i++)
			can_display_assembly_flag[i]=true;
	}
}
