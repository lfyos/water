package kernel_component;

import kernel_file_manager.file_reader;

public class component_core_7 extends component_core_6
{
	public void destroy()
	{
		super.destroy();
	}
	public boolean get_effective_display_flag(int parameter_channel_id)
	{
		return multiparameter[parameter_channel_id].effective_display_flag;
	}
	public boolean caculate_effective_display_flag(int parameter_channel_id)
	{
		int child_number=children_number();
		boolean old_effective_display_flag=multiparameter[parameter_channel_id].effective_display_flag;

		if(child_number<=0)
			multiparameter[parameter_channel_id].effective_display_flag=multiparameter[parameter_channel_id].display_flag;
		else{
			multiparameter[parameter_channel_id].effective_display_flag=false;
			if(multiparameter[parameter_channel_id].display_flag)
				for(int i=0;i<child_number;i++)
					multiparameter[parameter_channel_id].effective_display_flag
						|=children[i].get_effective_display_flag(parameter_channel_id);
		}
		return multiparameter[parameter_channel_id].effective_display_flag^old_effective_display_flag;
	}
	public void modify_display_flag(int parameter_channel_id[],boolean new_display_flag,component_container component_cont)
	{
		int buffer_channel_number=0,buffer_channel_id[]=new int[parameter_channel_id.length];

		for(int i=0,ni=parameter_channel_id.length;i<ni;i++) {
			if(multiparameter[parameter_channel_id[i]].display_flag!=new_display_flag){
				multiparameter[parameter_channel_id[i]].display_flag=new_display_flag;
				buffer_channel_id[buffer_channel_number++]=parameter_channel_id[i];
			}
		}
		if(buffer_channel_number<=0)
			return;
		for(component p=(component)this;p!=null;p=component_cont.get_component(p.parent_component_id)) {
			int modify_number=0;
			for(int i=0;i<buffer_channel_number;i++){
				if(p.caculate_effective_display_flag(buffer_channel_id[i]))
					modify_number++;
				if(p.caculate_assembly_flag(buffer_channel_id[i]))
					modify_number++;
			}
			if(modify_number<=0)
				break;
		}
	}
	public component_core_7(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,double lod_precision_scale,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,lod_precision_scale,ccp);
	}
}
