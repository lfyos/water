function construct_part_driver(init_data,part_object,render_driver,render)
{
	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		var p=render.system_call_processor.default_vertex_data_decoder;
		return p.discard_data([],request_type_string,buffer_object_data,part_object);
	}
	
	this.new_component_driver=construct_component_driver;

}