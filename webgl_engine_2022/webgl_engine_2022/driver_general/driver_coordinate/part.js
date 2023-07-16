function construct_part_driver(init_data,part_object,render_driver,render)
{
	this.new_component_driver=construct_component_driver;

	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		var ret_val=
		{
			material_id		:	buffer_object_data.material_id,
			region_box		:	buffer_object_data.region_box,
			region_data		:	(request_type_string!="edge")?new Array():buffer_object_data.region_data,
			item_number		:	(request_type_string!="edge")?0:(buffer_object_data.item_number),
			item_size		:	(request_type_string!="edge")?4:(buffer_object_data.item_size),
			private_data	:	null
		};
		return ret_val;
	}
	this.destroy=function()
	{
			this.draw_component		=null;
			this.decode_vertex_data	=null;
	}
}


