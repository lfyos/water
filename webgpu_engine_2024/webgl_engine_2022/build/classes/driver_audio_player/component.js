function construct_component_driver(component_ids,init_data,part_object,part_driver,render_driver,scene)
{
	this.component_ids			=component_ids;
	this.should_response_flag	=false;
	this.audio					=document.createElement("audio");
	this.audio.autoplay			="autoplay";
	this.audio.controls			="controls";
	this.audio.hidden			="hidden";

	this.draw_component=function(method_data,render_parameter,
			project_matrix,target_data,part_object,part_driver,render_driver,scene)
	{
		if(this.should_response_flag)
			if(this.audio.ended){
				this.should_response_flag=false;
				scene.caller.call_server_component(
					this.component_ids.component_id,this.component_ids.driver_id,
					[["operation","ended"],["fresh",Math.random()]]);
			}
	};
	this.append_component_parameter=function(buffer_data_item,part_object,part_driver,render_driver,scene)
	{
		this.should_response_flag=true;
		this.audio.src=scene.caller.create_component_request_string(
				this.component_ids.component_id,this.component_ids.driver_id,
				[["operation","audio"],["random",Math.random()]]);
	};
	this.destroy=function()
	{
		if(this.audio!=null){
			this.audio.src="";
			this.audio.muted=true;
			this.audio=null;
		}
	}
};
