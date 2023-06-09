function construct_part_driver(init_data,part_object,render_driver,render)
{
	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		var p=render.system_call_processor.default_vertex_data_decoder;
		return p.no_frame(request_type_string,buffer_object_data,part_object);
	}
	
	this.new_component_driver=construct_component_driver;
	
	this.destroy=function()
	{
		this.decode_vertex_data		=null;
		this.new_component_driver	=null;
	}
}
