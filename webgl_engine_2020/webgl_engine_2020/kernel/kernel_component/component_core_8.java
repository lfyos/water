package kernel_component;

import kernel_common_class.change_name;
import kernel_engine.component_container;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;

public class component_core_8 extends component_core_7
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
	public int modify_display_flag(int parameter_channel_id[],
		boolean new_display_flag,component_container component_cont)
	{
		int buffer_channel_number=0;
		int buffer_channel_id[]=new int[parameter_channel_id.length];
		for(int i=0,ni=parameter_channel_id.length;i<ni;i++)
			if(display_flag[parameter_channel_id[i]]^new_display_flag){
				display_flag[parameter_channel_id[i]]=new_display_flag;
				buffer_channel_id[buffer_channel_number++]=parameter_channel_id[i];
			}
		if(buffer_channel_number>0)
			for(component p=(component)this;p!=null;) {
				for(int i=0;i<buffer_channel_number;i++){
					boolean flag=false;
					flag|=p.caculate_effective_display_flag(buffer_channel_id[i]);
					flag|=p.caculate_assembly_flag(buffer_channel_id[i]);
					if(!flag)
						return i;
				}
				p=component_cont.get_component(p.parent_component_id);
			}
		return buffer_channel_number;
	}
	
	public component_core_8(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			change_name reverse_mount_component_name,part_type_string_sorter type_string_sorter,
			boolean part_list_flag,long default_display_bitmap,int max_child_number)
	{
		super(token_string,ek,request_response,fr,pcfps,
			change_part_name,mount_component_name,reverse_mount_component_name,
			type_string_sorter,part_list_flag,default_display_bitmap,max_child_number);
		
		int n=multiparameter.length;
		display_flag			=new boolean[n];
		effective_display_flag	=new boolean[n];
		for(int i=0;i<n;i++) {
			display_flag[i]=true;
			effective_display_flag[i]=true;
		}
	}
}
