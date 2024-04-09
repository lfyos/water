function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.draw_component=function(method_data,render_data,
			render_id,part_id,component_id,driver_id,component_render_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
	}
	this.append_component_parameter=function(
			component_id,		driver_id,		render_id,		part_id,
			buffer_data_item,	part_object,	part_driver,	render_driver,	render)
	{
		var ep;
		if(typeof(ep=render.component_call_processor[component_id])!="object"){
			render.component_call_processor[component_id]=new Object();
			ep=render.component_call_processor[component_id];
		}
		ep.suspend_status=buffer_data_item;	
	};
	this.destroy=function(render)
	{
		this.draw_component				=null;
		this.append_component_parameter	=null;
	}
};
