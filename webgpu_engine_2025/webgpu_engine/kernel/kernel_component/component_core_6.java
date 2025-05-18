package kernel_component;

import kernel_file_manager.file_reader;

public class component_core_6 extends component_core_5
{
	public void destroy()
	{
		super.destroy();
	}
	public boolean caculate_assembly_flag(int parameter_channel_id)
	{
		var my_multipar=multiparameter[parameter_channel_id];
		boolean old_value=my_multipar.can_display_assembly_flag;
		my_multipar.can_display_assembly_flag=true;

		for(int i=0,ni=children.size();i<ni;i++){
			var my_child			=children.get(i);
			var my_child_unipar		=my_child.uniparameter;
			var my_child_multipar	=my_child.multiparameter[parameter_channel_id];
			
			if(my_child_multipar.effective_display_flag)
				if(my_child_multipar.can_display_assembly_flag)
					if(my_child_multipar.display_bitmap==my_multipar.display_bitmap)
						if(!(my_child.get_children_location_modify_flag()))
							if(!(my_child_unipar.effective_selected_flag))
								continue;
			my_multipar.can_display_assembly_flag=false;
			break;
		}
		return my_multipar.can_display_assembly_flag^old_value;
	}
	public boolean caculate_effective_display_flag(int parameter_channel_id)
	{
		var my_multipar=multiparameter[parameter_channel_id];
		boolean old_effective_display_flag=my_multipar.effective_display_flag;

		int child_number;
		if((child_number=children.size())<=0)
			my_multipar.effective_display_flag=my_multipar.display_flag;
		else{
			my_multipar.effective_display_flag=false;
			if(my_multipar.display_flag) {
				var my_child=this;
				for(int i=0;i<child_number;i++) {
					my_child=children.get(i);
					var my_child_multipar=my_child.multiparameter[parameter_channel_id];
					my_multipar.effective_display_flag|=my_child_multipar.effective_display_flag;
				}
			}
		}
		return my_multipar.effective_display_flag^old_effective_display_flag;
	}
	public void modify_display_flag(int parameter_channel_id[],
			boolean new_display_flag[],component_container component_cont)
	{
		int number=parameter_channel_id.length;
		if(number>=new_display_flag.length)
			number=new_display_flag.length;
		for(int i=0;i<number;i++)
			this.multiparameter[parameter_channel_id[i]].display_flag=new_display_flag[i];
		var p=this;
		for(;p!=null;p=component_cont.get_component(p.parent_component_id)) 
			for(int i=0;i<number;i++){
				p.caculate_effective_display_flag(parameter_channel_id[i]);
				p.caculate_assembly_flag(parameter_channel_id[i]);
			}
	}
	public void modify_display_flag(int parameter_channel_id[],
			boolean new_display_flag,component_container component_cont)
	{
		boolean new_display_flag_array[]=new boolean[parameter_channel_id.length];
		for(int i=0,ni=parameter_channel_id.length;i<ni;i++)
			new_display_flag_array[i]=new_display_flag;
		modify_display_flag(parameter_channel_id,new_display_flag_array,component_cont);
	}
	public component_core_6(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
	}
}