function construct_component_driver(component_ids,init_data,part_object,part_driver,render_driver,scene)
{
	this.component_ids=component_ids;
	
	this.draw_component=function(method_data,render_parameter,
			target_data,part_object,part_driver,render_driver,scene)
	{
	}
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)  
	{
		var ep;
		if(typeof(ep=scene.component_call_processor[this.component_ids.component_id])!="object"){
			scene.component_call_processor[this.component_ids.component_id]=new Object();
			ep=scene.component_call_processor[this.component_ids.component_id];
		}
		ep.suspend_status=buffer_data_item;	
	}
};
