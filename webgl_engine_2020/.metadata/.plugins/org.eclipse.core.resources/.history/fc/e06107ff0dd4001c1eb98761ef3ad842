package kernel_component;

import kernel_common_class.change_name;
import kernel_file_manager.file_reader;

public class component_core_7 extends component_core_6
{
	private boolean display_flag[],effective_display_flag[];

	public void destroy()
	{
		super.destroy();
		display_flag=null;
		effective_display_flag=null;
	}
	public boolean get_effective_display_flag(int parameter_channel_id)
	{
		return effective_display_flag[parameter_channel_id];
	}
	public boolean caculate_effective_display_flag(int parameter_channel_id)
	{
		int child_number=children_number();
		boolean old_effective_display_flag=effective_display_flag[parameter_channel_id];

		if(child_number<=0)
			effective_display_flag[parameter_channel_id]=display_flag[parameter_channel_id];
		else{
			effective_display_flag[parameter_channel_id]=false;
			if(display_flag[parameter_channel_id])
				for(int i=0;i<child_number;i++)
					effective_display_flag[parameter_channel_id]|=children[i].get_effective_display_flag(parameter_channel_id);
		}
		return effective_display_flag[parameter_channel_id]^old_effective_display_flag;
	}
	public void modify_display_flag(int parameter_channel_id[],
		boolean new_display_flag,component_container component_cont)
	{
		int buffer_channel_number=0,buffer_channel_id[]=new int[parameter_channel_id.length];
		for(int i=0,ni=parameter_channel_id.length;i<ni;i++)
			if(display_flag[parameter_channel_id[i]]^new_display_flag){
				display_flag[parameter_channel_id[i]]=new_display_flag;
				buffer_channel_id[buffer_channel_number++]=parameter_channel_id[i];
			}
		if(buffer_channel_number<=0)
			return;
		for(component p=(component)this;p!=null;) {
			int modify_number=0;
			for(int i=0;i<buffer_channel_number;i++){
				if(p.caculate_effective_display_flag(buffer_channel_id[i]))
					modify_number++;
				if(p.caculate_assembly_flag(buffer_channel_id[i]))
					modify_number++;
			}
			p=(modify_number<=0)?null:component_cont.get_component(p.parent_component_id);
		}
	}
	public component_core_7(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,change_name change_part_name,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,change_part_name,ccp);

		display_flag			=new boolean[multiparameter.length];
		effective_display_flag	=new boolean[multiparameter.length];
		for(int i=0,ni=multiparameter.length;i<ni;i++) {
			display_flag[i]=true;
			effective_display_flag[i]=true;
		}
	}
}
