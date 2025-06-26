function construct_part_driver(init_data,part_object,render_driver,scene)
{
	this.init_data=init_data;
	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		return buffer_object_data;
	}
	this.new_component_driver=construct_component_driver;
}
