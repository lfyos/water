package kernel_component;

import kernel_file_manager.file_reader;
import kernel_part.part;
import kernel_transformation.box;

public class component_core_5 extends component_core_4
{
	private box component_box,model_box;

	private long box_absolute_location_version;

	public void destroy()
	{
		super.destroy();
		
		component_box	=null;
		model_box		=null;
	}
	private void caculate_box_by_driver()
	{
		for(int i=0,ni=driver_array.size();i<ni;i++){
			part p=driver_array.get(i).component_part;
			model_box=p.secure_caculate_part_box((component)this,i);
			if(model_box!=null){
				component_box=absolute_location.multiply(model_box);
				return;
			}
		}
		model_box		=null;
		component_box	=null;
		return;
	}
	public box get_model_box()
	{
		return model_box;
	}
	public box get_component_box(boolean mandatory_flag)
	{
		if(mandatory_flag)
			return component_box;
		if(get_absolute_location_version()!=box_absolute_location_version)
			return null;
		if(get_should_caculate_absolute_location_flag())
			return null;
		return component_box;
	}
	public void caculate_box(component_container component_cont)
	{
		if(get_should_caculate_absolute_location_flag())
			box_absolute_location_version=0;
		else{
			long new_absolute_location_version=get_absolute_location_version();
			if(box_absolute_location_version==new_absolute_location_version)
				return;
			box_absolute_location_version=new_absolute_location_version;
		}
		
		var p=this;
		if((p=component_cont.get_component(parent_component_id))!=null)
			p.box_absolute_location_version=0;
		
		component_box	=null;
		model_box		=null;
		
		int child_number;
		if((child_number=children.size())>0)
			for(int i=0;i<child_number;i++){
				p=children.get(i);
				if(p.component_box==null){
					component_box	=null;
					model_box		=null;
					break;
				}
				if(component_box==null) {
					component_box=p.component_box;
					model_box=p.relative_location.multiply(p.model_box);
				}else {
					component_box=p.component_box.add(component_box);
					model_box=p.relative_location.multiply(p.model_box).add(model_box);
				}
			}
		if(component_box==null)
			caculate_box_by_driver();
	}
	public component_core_5(String token_string,file_reader fr,
			boolean part_list_flag,boolean normalize_location_flag,
			component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		component_box					=null;
		model_box						=null;
		box_absolute_location_version	=0;
	}
}
