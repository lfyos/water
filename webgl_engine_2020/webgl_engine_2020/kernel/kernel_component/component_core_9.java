package kernel_component;

import kernel_common_class.change_name;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_transformation.location;
import kernel_engine.component_container;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;

public class component_core_9 extends component_core_8
{
	private long location_version,absolute_location_version;
	public long get_location_version()
	{
		return location_version;
	}
	public long get_absolute_location_version()
	{
		return absolute_location_version;
	}
	public boolean should_caculate_location_flag,should_caculate_box_flag;
	public location move_location,parent_and_relative_location,absolute_location;
	
	public void destroy()
	{
		super.destroy();
		
		move_location=null;
		parent_and_relative_location=null;
		absolute_location=null;
	}
	public location caculate_location(component_container component_cont)
	{
		component parent;
		if(should_caculate_location_flag){
			should_caculate_location_flag=false;
			absolute_location_version++;
			
			should_caculate_box_flag=true;
			parent_and_relative_location=relative_location;
			if((parent=component_cont.get_component(parent_component_id))!=null)
				parent_and_relative_location=parent.absolute_location.multiply(parent_and_relative_location);	
			if(uniparameter.cacaulate_location_flag)
				absolute_location=move_location;
			else
				absolute_location=parent_and_relative_location.multiply(move_location);
			for(int i=0,n=children_number();i<n;i++){
				children[i].should_caculate_location_flag=true;
				children[i].should_caculate_box_flag=true;
			}
		}
		return absolute_location;
	}
	
	public void recurse_caculate_location(component_container component_cont)
	{
		component parent_comp;
		if((parent_comp=component_cont.get_component(parent_component_id))!=null)
			parent_comp.recurse_caculate_location(component_cont);
		caculate_location(component_cont);
	}
	public void modify_location(location new_move_location,component_container component_cont)
	{	
		component parent;
		if((parent=component_cont.get_component(parent_component_id))==null)
			return;
		
		move_location=new location(new_move_location);
		location_version++;
		should_caculate_location_flag=true;
		should_caculate_box_flag=true;
		
		for(component p=parent;p!=null;p=component_cont.get_component(p.parent_component_id)){
			p.should_caculate_box_flag=true;
			p.caculate_children_location_modify_flag();
			for(int i=0,ni=p.multiparameter.length;i<ni;i++)
				p.caculate_assembly_flag(i);
		}
		caculate_location(component_cont);
	}
	public component_core_9(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			change_name reverse_mount_component_name,part_type_string_sorter type_string_sorter,
			boolean part_list_flag,long default_display_bitmap,int max_child_number)
	{
		super(token_string,ek,request_response,fr,pcfps,
			change_part_name,mount_component_name,reverse_mount_component_name,
			type_string_sorter,part_list_flag,default_display_bitmap,max_child_number);

		location_version				=1;
		absolute_location_version		=1;
		move_location					=new location();
		parent_and_relative_location	=new location();
		absolute_location				=new location();
		should_caculate_location_flag	=true;
		should_caculate_box_flag		=true;
	}
}
