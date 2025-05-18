package kernel_component;

import kernel_transformation.location;
import kernel_file_manager.file_reader;

public class component_core_4 extends component_core_3
{
	private long		location_version,absolute_location_version;
	private boolean		children_location_modify_flag,should_caculate_absolute_location_flag;
	private location	negative_absolute_location,negative_parent_and_relative_location;
	
	public long get_location_version()
	{
		return location_version;
	}
	public long get_absolute_location_version()
	{
		return absolute_location_version;
	}
	public boolean get_children_location_modify_flag()
	{
		return children_location_modify_flag;
	}
	public boolean get_should_caculate_absolute_location_flag()
	{
		return should_caculate_absolute_location_flag;
	}
	
	public location move_location,parent_and_relative_location,absolute_location;
	
	public void destroy()
	{
		super.destroy();
		
		move_location				=null;
		parent_and_relative_location=null;
		absolute_location			=null;
		negative_absolute_location	=null;
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
	public void caculate_location(component_container component_cont,boolean force_cacuate_flag)
	{
		if(should_caculate_absolute_location_flag||force_cacuate_flag){
			should_caculate_absolute_location_flag=false;
			absolute_location_version++;
			
			parent_and_relative_location=relative_location;
			component parent=component_cont.get_component(parent_component_id);
			if(parent!=null)
				parent_and_relative_location=parent.absolute_location.multiply(parent_and_relative_location);	
			if(uniparameter.caculate_location_flag)
				absolute_location=move_location;
			else
				absolute_location=parent_and_relative_location.multiply(move_location);
			
			negative_absolute_location				=null;
			negative_parent_and_relative_location	=null;
			
			var p=this;
			for(int i=0,ni=children.size();i<ni;i++){
				p=children.get(i);
				p.should_caculate_absolute_location_flag=true;
			}
		}
	}
	public void recurse_caculate_location(component_container component_cont)
	{
		component parent;
		if((parent=component_cont.get_component(parent_component_id))!=null)
			parent.recurse_caculate_location(component_cont);
		caculate_location(component_cont,false);
	}
	public boolean caculate_children_location_modify_flag()
	{
		boolean old_children_location_modify_flag=children_location_modify_flag;
		int child_number=children.size();
		var p=this;
		
		for(int i=0;i<child_number;i++) {
			p=children.get(i);
			if(p.children_location_modify_flag){
				children_location_modify_flag=true;
				return old_children_location_modify_flag^children_location_modify_flag;
			}
		}
		for(int i=0;i<child_number;i++)
			if(children.get(i).move_location.is_not_identity_matrix()){
				children_location_modify_flag=true;
				return old_children_location_modify_flag^children_location_modify_flag;
			}
		children_location_modify_flag=false;
		return old_children_location_modify_flag^children_location_modify_flag;
	}
	public void set_component_move_location(
		location new_move_location,component_container component_cont)
	{
		location_version++;
		move_location=new location(new_move_location);
		caculate_location(component_cont,true);
		
		var p=this;
		p=component_cont.get_component(p.parent_component_id);
		for(;p!=null;p=component_cont.get_component(p.parent_component_id))
			if(p.caculate_children_location_modify_flag())
				break;
	}
	public component_core_4(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);

		location_version						=1;
		absolute_location_version				=1;
		children_location_modify_flag			=false;
		should_caculate_absolute_location_flag	=true;
		
		negative_absolute_location				=null;
		negative_parent_and_relative_location	=null;
		
		move_location							=new location();
		parent_and_relative_location			=new location();
		absolute_location						=new location();
	}
}