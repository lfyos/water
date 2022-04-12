function construct_pickup_object()
{
	this.component_id		=-1;
	
	this.body_id			=-1;
	this.face_id			=-1;
	this.loop_id			=-1;
	this.edge_id			=-1;
	this.vertex_id			=-1;

	this.body_value			= 0;
	this.face_value			= 0;
	this.loop_value			= 0;
	this.edge_value			= 0;
	this.vertex_value		= 0;
	
	this.point_id			=-1;
	
	this.depth				=-1;
	this.value				=-1;
	
	this.render_id			=-1;
	this.part_id			=-1;
	this.buffer_id			=-1;
	
	this.pickup_time		=0;
	this.read_time_length	=0;
	
	this.compare=function(p)
	{
		if(this.component_id!=p.component_id)
			return true;
		if(this.body_id!=p.body_id)
			return true;
		if(this.face_id!=p.face_id)
			return true;
		if(this.loop_id!=p.loop_id)
			return true;
		if(this.edge_id!=p.edge_id)
			return true;
		return false;
	}
	this.fork=function()
	{
		var ret_val=new construct_pickup_object();

		ret_val.component_id	=this.component_id;
		
		ret_val.body_id			=this.body_id;
		ret_val.face_id			=this.face_id;
		ret_val.loop_id			=this.loop_id;
		ret_val.edge_id			=this.edge_id;
		ret_val.vertex_id		=this.vertex_id;

		ret_val.body_value		=this.body_value;
		ret_val.face_value		=this.face_value;
		ret_val.loop_value		=this.loop_value;
		ret_val.edge_value		=this.edge_value;
		ret_val.vertex_value	=this.vertex_value;
		
		ret_val.point_id		=this.point_id;
		
		ret_val.depth			=this.depth;
		ret_val.value			=this.value;
		
		ret_val.render_id		=this.render_id;
		ret_val.part_id			=this.part_id;
		ret_val.buffer_id		=this.buffer_id;
		
		ret_val.pickup_time		=this.pickup_time;
		ret_val.read_time_length=this.read_time_length;
		
		return ret_val;
	}
};
