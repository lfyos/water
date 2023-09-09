function construct_part_driver(init_data,part_object,render_driver,render)
{
	this.new_component_driver=construct_component_driver;

	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		var p=render.system_call_processor.default_vertex_data_decoder;
		return p.only_face(request_type_string,buffer_object_data,part_object);
	}
	this.destroy=function()
	{
		this.new_component_driver	=null;
		this.decode_vertex_data		=null;
	}
};
