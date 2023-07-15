function construct_part_driver(init_data,part_object,render_driver,render)
{
	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		var ret_val=
		{
			material_id		:	buffer_object_data.material_id,
			region_box		:	buffer_object_data.region_box,
			region_data		:	(request_type_string!="face")?new Array():buffer_object_data.region_data,
			item_number		:	(request_type_string!="face")?0:(buffer_object_data.item_number),
			item_size		:	(request_type_string!="face")?4:
						(buffer_object_data.region_data.length/buffer_object_data.item_number),
			private_data	:	null
		};
		return ret_val;
	}
	
	this.create_component_driver=construct_component_driver;
	
	this.destroy=function()
	{
		this.create_component_driver=null;
		this.decode_vertex_data		=null;
	}
}