function construct_part_driver(init_data,create_data,part_object,render_driver,scene)
{
	this.decoder=scene.system_call_processor.default_vertex_data_decoder;
	
	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		return this.decoder.discard_data([],request_type_string,buffer_object_data,part_object);
	}
	
	this.new_component_driver=construct_component_driver;
}