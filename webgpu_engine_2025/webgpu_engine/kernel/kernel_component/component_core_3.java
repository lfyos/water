package kernel_component;

import kernel_file_manager.file_reader;
import kernel_part.part;
import kernel_transformation.box;

public class component_core_3 extends component_core_2
{
	private box component_box,model_box;
	private long box_absolute_location_version;

	public void destroy()
	{
		super.destroy();
		
		component_box	=null;
		model_box		=null;
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
		
		if(children.size()>0) {
			for(var my_child_component:children){
				p=my_child_component;
				if(p.component_box==null){
					component_box	=null;
					model_box		=null;
					break;
				}
				box child_component_box	=p.component_box;
				box child_model_box		=p.relative_location.multiply(p.model_box);
				if(component_box==null) {
					component_box	=child_component_box;
					model_box		=child_model_box;
				}else{
					component_box	=component_box.add(child_component_box);
					model_box		=model_box.add(child_model_box);
				}
			}
			if(component_box!=null)
				return;
		}
		for(int i=0,ni=driver_array.size();i<ni;i++){
			part my_part=driver_array.get(i).component_part;
			model_box=my_part.secure_caculate_part_box((component)this,i);
			if(model_box!=null){
				component_box=absolute_location.multiply(model_box);
				return;
			}
		}
		model_box		=null;
		component_box	=null;
		return;
	}
	public component_core_3(String token_string,file_reader fr,
			boolean part_list_flag,boolean normalize_location_flag,
			component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		component_box					=null;
		model_box						=null;
		box_absolute_location_version	=0;
	}
}
