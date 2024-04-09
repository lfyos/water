package kernel_component;

import kernel_file_manager.file_reader;
import kernel_transformation.location;

public class component_core_8 extends component_core_7
{
	private long location_version,absolute_location_version;
	
	public void update_location_version()
	{
		location_version++;
	}
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
	private location negative_absolute_location,negative_parent_and_relative_location;
	
	public void destroy()
	{
		super.destroy();
		
		move_location=null;
		parent_and_relative_location=null;
		absolute_location=null;
		negative_absolute_location=null;
		negative_parent_and_relative_location=null;
	}
	public location caculate_negative_absolute_location()
	{
		if(negative_absolute_location==null)
			negative_absolute_location=absolute_location.negative();
		return negative_absolute_location;
	}
	public location caculate_negative_parent_and_relative_location()
	{
		if(negative_parent_and_relative_location==null)
			negative_parent_and_relative_location=parent_and_relative_location.negative();
		return negative_parent_and_relative_location;
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
			
			negative_absolute_location=null;
			negative_parent_and_relative_location=null;
			
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
	public void set_component_move_location(location new_move_location,component_container component_cont)
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
	public component_core_8(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);

		location_version						=1;
		absolute_location_version				=1;
		move_location							=new location();
		parent_and_relative_location			=new location();
		absolute_location						=new location();
		negative_absolute_location				=null;
		negative_parent_and_relative_location	=null;
		should_caculate_location_flag			=true;
		should_caculate_box_flag				=true;
	}
}
