package kernel_component;

import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_container_for_part_search;
import kernel_transformation.box;
import kernel_common_class.change_name;
import kernel_engine.engine_kernel;
import kernel_engine.part_type_string_sorter;


public class component_core_11 extends component_core_10
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
					part p=driver_array[i].component_part;
					model_box=p.secure_caculate_part_box((component)this,i,-1,-1,-1,-1,-1,null,null);
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
						part p=driver_array[i].component_part;
						model_box=p.secure_caculate_part_box(
								(component)this,i,-1,-1,-1,-1,-1,null,null);
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
						part p=driver_array[i].component_part;
						box my_model_box=p.secure_caculate_part_box(
									(component)this,i,-1,-1,-1,-1,-1,null,null);
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
	public component_core_11(String token_string,
			engine_kernel ek,client_request_response request_response,
			file_reader fr,part_container_for_part_search pcfps,
			change_name change_part_name,change_name mount_component_name,
			part_type_string_sorter type_string_sorter,boolean normalize_location_flag,
			boolean part_list_flag,long default_display_bitmap)
	{
		super(token_string,ek,request_response,fr,pcfps,change_part_name,
			mount_component_name,type_string_sorter,normalize_location_flag,
			part_list_flag,default_display_bitmap);

		should_caculate_box_flag=true;
		caculate_box_result_flag=false;
		component_box=null;
		model_box=null;
	}
}
