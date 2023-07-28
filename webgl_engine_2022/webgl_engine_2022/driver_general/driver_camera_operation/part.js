function construct_part_driver(init_data,part_object,render_driver,render)
{
	this.decode_vertex_data=function(request_type_string,buffer_object_data,part_object)
	{
		var giveup_flag;
		switch(request_type_string){
		case "face":
		case "edge":
			giveup_flag=false;
			break;
		default:
			giveup_flag=true;
			break;
		}
		var ret_val=
		{
			material_id		:	buffer_object_data.material_id,
			region_box		:	buffer_object_data.region_box,
			region_data		:	giveup_flag?new Array():buffer_object_data.region_data,
			item_number		:	giveup_flag?0:(buffer_object_data.item_number),
			item_size		:	giveup_flag?4:(buffer_object_data.item_size),
			private_data	:	null
		};
		return ret_val;
	}
	
	this.new_component_driver=construct_component_driver;

	this.destroy=function()
	{
		this.new_component_driver	=null;
		this.decode_vertex_data		=null;
	}
}
