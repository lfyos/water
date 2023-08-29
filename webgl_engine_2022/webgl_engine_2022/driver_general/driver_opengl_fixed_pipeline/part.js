function construct_part_driver(init_data,part_object,render_driver,render)
{
	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		var p=render.system_call_processor.default_vertex_data_decoder;
		var buffer_object_data=p.create_frame(request_type_string,buffer_object_data,part_object);
		if(request_type_string=="point")
			p.convert_point_to_face(buffer_object_data);
			
		return buffer_object_data;
	}
	
	this.new_component_driver=construct_component_driver;
	
	this.destroy=function()
	{
		this.new_component_driver	=null;
		this.decode_vertex_data		=null;
	}
}

