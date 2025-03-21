function construct_pickup_object()
{
	this.render_id			=-1;
	this.part_id			=-1;
	this.data_buffer_id		=-1;
	
	this.component_id		=-1;
	this.driver_id			=-1;
	
	this.primitive_type_id	=0;
	
	this.body_id			=-1;
	this.face_id			=-1;
	this.loop_id			=-1;
	this.edge_id			=-1;
	this.primitive_id		=-1;
	this.vertex_id			=-1;
	
	this.depth				=-1;
	this.value				=[-1,-1,-1];

	this.fork=function()
	{
		var ret_val=new construct_pickup_object();

		ret_val.render_id			=this.render_id;
		ret_val.part_id				=this.part_id;
		ret_val.data_buffer_id		=this.data_buffer_id;

		ret_val.component_id		=this.component_id;
		ret_val.driver_id			=this.driver_id;
		
		ret_val.primitive_type_id	=this.primitive_type_id;
		
		ret_val.body_id				=this.body_id;
		ret_val.face_id				=this.face_id;
		ret_val.loop_id				=this.loop_id;
		ret_val.edge_id				=this.edge_id;
		ret_val.primitive_id		=this.primitive_id;
		ret_val.vertex_id			=this.vertex_id;

		ret_val.depth				=this.depth;
		ret_val.value				=[this.value[0],this.value[1],this.value[2]];

		return ret_val;
	}
};
