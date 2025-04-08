function construct_component_driver(component_ids,init_data,part_object,part_driver,render_driver,scene)
{
	this.component_ids	=component_ids;
	
	this.draw_component=function(method_data,render_parameter,
			project_matrix,target_data,part_object,part_driver,render_driver,scene)	
	{
		return;
	}
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)  
	{
	}
};
