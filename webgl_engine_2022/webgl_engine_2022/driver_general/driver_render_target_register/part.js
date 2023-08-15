function construct_part_driver(init_data,part_object,render_driver,render)
{
	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		var ret_val=
		{
			material_id		:	buffer_object_data.material_id,
			region_box		:	buffer_object_data.region_box,
			region_data		:	new Array(),
			item_number		:	0,
			item_size		:	4,
			private_data	:	null
		};
		return ret_val;
	}
	
	this.new_component_driver=construct_component_driver;
	
	this.destroy=function()
	{
		this.decode_vertex_data		=null;
		this.new_component_driver	=null;
	};
}
