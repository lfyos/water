package kernel_component;

import kernel_driver.component_driver;
import kernel_file_manager.file_reader;

public class component_core_4 extends component_core_3
{
	public component_multiparameter	multiparameter[];
	
	public void destroy()
	{
		super.destroy();
		
		multiparameter=null;
	}
	private void caculate_assembly_flag(int parameter_channel_id)
	{
		component_multiparameter my_multipar=multiparameter[parameter_channel_id];
		my_multipar.can_display_assembly_flag=true;

		for(component my_child:children){
			component_uniparameter my_child_unipar		=my_child.uniparameter;
			component_multiparameter my_child_multipar	=my_child.multiparameter[parameter_channel_id];
			
			if(my_child_multipar.effective_display_flag)
				if(my_child_multipar.can_display_assembly_flag)
					if(my_child_multipar.display_bitmap==my_multipar.display_bitmap)
						if(!(my_child.get_children_location_modify_flag()))
							if(!(my_child_unipar.effective_selected_flag))
								continue;
			my_multipar.can_display_assembly_flag=false;
			break;
		}
	}
	private void caculate_effective_display_flag(int parameter_channel_id)
	{
		component_multiparameter my_multipar=multiparameter[parameter_channel_id];

		if(children.size()<=0)
			my_multipar.effective_display_flag=my_multipar.display_flag;
		else{
			my_multipar.effective_display_flag=false;
			if(my_multipar.display_flag)
				for(component my_child:children){
					component_multiparameter my_child_multipar=my_child.multiparameter[parameter_channel_id];
					my_multipar.effective_display_flag|=my_child_multipar.effective_display_flag;
				}
		}
	}
	public void caculate_component_flag(int parameter_channel_id)
	{
		caculate_effective_display_flag(parameter_channel_id);
		caculate_assembly_flag(parameter_channel_id);
	}
	public void modify_display_flag(int parameter_channel_id[],
		boolean new_display_flag[],component_container component_cont)
	{
		int number=parameter_channel_id.length;
		if(number>=new_display_flag.length)
			number=new_display_flag.length;
		
		for(int i=0;i<number;i++)
			this.multiparameter[parameter_channel_id[i]].display_flag=new_display_flag[i];
		
		for(var p=this;p!=null;p=component_cont.get_component(p.parent_component_id)) 
			for(int i=0;i<number;i++)
				p.caculate_component_flag(parameter_channel_id[i]);
	}
	public void modify_display_flag(int parameter_channel_id[],
		boolean new_display_flag,component_container component_cont)
	{
		boolean new_display_flag_array[]=new boolean[parameter_channel_id.length];
		for(int i=0,ni=parameter_channel_id.length;i<ni;i++)
			new_display_flag_array[i]=new_display_flag;
		modify_display_flag(parameter_channel_id,new_display_flag_array,component_cont);
	}
	public void recurse_caculate_component_flag(component_container component_cont,component parent)
	{
		uniparameter.do_response_location_flag=true;

		caculate_location(component_cont,true);

		if(parent==null)
			uniparameter.effective_selected_flag=uniparameter.selected_flag;
		else
			uniparameter.effective_selected_flag=parent.uniparameter.effective_selected_flag|uniparameter.selected_flag;
		
		for(int i=0,n=children.size();i<n;i++)
			children.get(i).recurse_caculate_component_flag(component_cont,(component)this);

		caculate_children_location_modify_flag();
		
		for(int i=0,ni=multiparameter.length;i<ni;i++)
			caculate_component_flag(i);
		
		caculate_box(component_cont);
		
		uniparameter.discard_precision2=-1;
		for(int i=0,ni=driver_array.size();i<ni;i++) {
			component_driver c_d=driver_array.get(i);
			if(c_d.component_part.part_par.discard_precision2>0.0){
				if(uniparameter.discard_precision2<0.0)
					uniparameter.discard_precision2=c_d.component_part.part_par.discard_precision2;
				else if(c_d.component_part.part_par.discard_precision2<uniparameter.discard_precision2)
					uniparameter.discard_precision2=c_d.component_part.part_par.discard_precision2;
			}
		}
		for(int i=0,n=children.size();i<n;i++){
			component my_child=children.get(i);			
			
			double child_discard_precision2=my_child.uniparameter.discard_precision2;
			if(child_discard_precision2>0.0){
				if(uniparameter.discard_precision2<0.0)
					uniparameter.discard_precision2=child_discard_precision2;
				else if(uniparameter.discard_precision2>child_discard_precision2)
					uniparameter.discard_precision2=child_discard_precision2;
			}
			
			if(uniparameter.file_last_modified_time<my_child.uniparameter.file_last_modified_time)
				uniparameter.file_last_modified_time=my_child.uniparameter.file_last_modified_time;

		}
		if(uniparameter.discard_precision2<0)
			uniparameter.discard_precision2=1;
	}
	public component_core_4(String token_string,file_reader fr,
			boolean part_list_flag,boolean normalize_location_flag,
			component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		long display_bitmap=ccp.sk.scene_par.default_display_bitmap;
		int number=ccp.sk.scene_par.multiparameter_number;
		multiparameter=new component_multiparameter[number];
		for(int i=0;i<number;i++)
			multiparameter[i]=new component_multiparameter(display_bitmap);
	}
}
