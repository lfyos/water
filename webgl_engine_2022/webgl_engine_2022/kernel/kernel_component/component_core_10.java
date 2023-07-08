package kernel_component;

import kernel_file_manager.file_reader;
import kernel_part.part;
import kernel_transformation.box;

public class component_core_10 	extends component_core_9
{
	public boolean caculate_box_result_flag;
	public box component_box,model_box;
	
	public void destroy()
	{
		super.destroy();
		
		component_box=null;
		model_box=null;
	}
	public box get_component_box(boolean mandatory_flag)
	{
		if(mandatory_flag)
			return component_box;
		if(should_caculate_location_flag)
			return null;		
		if(should_caculate_box_flag)
			return null;	
		if(caculate_box_result_flag)
			return component_box;
		return null;
	}
	public void caculate_box(boolean caculate_model_box_flag)
	{
		if(should_caculate_box_flag){
			int i,n,child_number;
			if(should_caculate_location_flag){
				caculate_box_result_flag=false;
				model_box=null;
				return;
			}
			if((child_number=children_number())<=0){
				for(i=0,n=driver_number();i<n;i++){
					part p=driver_array.get(i).component_part;
					model_box=p.secure_caculate_part_box((component)this,i,-1,-1,-1,-1,-1,-1,null,null);
					if(model_box!=null){
						caculate_box_result_flag=true;
						should_caculate_box_flag=false;
						component_box=absolute_location.multiply(model_box);
						return;
					}
				}
				model_box=null;
				caculate_box_result_flag=false;
				should_caculate_box_flag=false;
				component_box=absolute_location.multiply(new box());
				return;
			}
			if(caculate_model_box_flag){
				for(i=0,model_box=null;i<child_number;i++)
					if(children[i].model_box==null){
						model_box=null;
						break;
					}else{
						box vbox=children[i].relative_location.multiply(children[i].model_box);
						if(model_box==null)
							model_box=vbox;
						else
							model_box=model_box.add(vbox);
					}
				if(model_box==null)
					for(i=0,n=driver_number();i<n;i++){
						part p=driver_array.get(i).component_part;
						model_box=p.secure_caculate_part_box(
								(component)this,i,-1,-1,-1,-1,-1,-1,null,null);
						if(model_box!=null)
							break;
					}
			}
			if((!children_location_modify_flag)&&(model_box!=null)){
				caculate_box_result_flag=true;
				should_caculate_box_flag=false;
				component_box=absolute_location.multiply(model_box);
				return;
			}
			box all_box=null,only_box=null;
			for(i=0,n=0;i<child_number;i++){
				if(children[i].should_caculate_box_flag){
					caculate_box_result_flag=false;
					return;
				}
				box vbox=children[i].component_box;
				if(children[i].caculate_box_result_flag){
					if((n++)<=0)
						only_box=vbox;
					else
						only_box=vbox.add(only_box);
				}
				if(i==0)
					all_box=vbox;
				else
					all_box=vbox.add(all_box );
			}
			if((n>0)&&(children_number()==n)){
				caculate_box_result_flag=true;
				component_box=only_box;
			}else{
				if(!children_location_modify_flag)
					for(i=0,n=driver_number();i<n;i++){
						part p=driver_array.get(i).component_part;
						box my_model_box=p.secure_caculate_part_box(
									(component)this,i,-1,-1,-1,-1,-1,-1,null,null);
						if(my_model_box!=null){
							caculate_box_result_flag=true;
							should_caculate_box_flag=false;
							model_box=my_model_box;
							component_box=absolute_location.multiply(model_box);
							return;
						}
					}
				caculate_box_result_flag=false;
				component_box=all_box;
			}		
			if(component_box==null)
				component_box=absolute_location.multiply(new box());
			should_caculate_box_flag=false;
		}
		return;
	}
	public component_core_10(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,double lod_precision_scale,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,lod_precision_scale,ccp);

		should_caculate_box_flag=true;
		caculate_box_result_flag=false;
		component_box=null;
		model_box=null;
	}
}

