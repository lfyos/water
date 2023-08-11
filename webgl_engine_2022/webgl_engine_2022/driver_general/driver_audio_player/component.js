function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.should_response_flag	=false;
	this.audio					=document.createElement("audio");
	this.audio.autoplay			="autoplay";
	this.audio.controls			="controls";
	this.audio.hidden			="hidden";

	this.draw_component=function(method_data,render_data,
			render_id,part_id,data_buffer_id,component_id,driver_id,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		if(method_data.method_id=1)
			if(this.should_response_flag)
				if(this.audio.ended){
					this.should_response_flag=false;
					render.caller.call_server_component(component_id,driver_id,
							[["operation","ended"],["fresh",Math.random()]]);
				}
	};
	this.append_component_parameter=function(component_id,	driver_id,
			render_id,		part_id,		data_buffer_id,	buffer_data_item,p,
			part_object,	part_driver,	render_driver,	render)
	{
		this.should_response_flag=true;
		this.audio.src=render.caller.create_component_request_string(
				component_id,driver_id,[["operation","audio"],["fresh",Math.random()]]);
	};
};
