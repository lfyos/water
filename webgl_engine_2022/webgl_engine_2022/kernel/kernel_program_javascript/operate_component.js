function construct_operate_component(my_render)
{
	this.render=my_render;
	this.last_operate_component_id=-1;
	
	this.destroy=function()
	{
		this.render									=null;
		this.get_component_object_by_component_id	=null;
		this.get_component_object_by_component_name	=null;
		this.get_component_event_processor			=null;
		this.get_component_call_processor			=null;
		this.set_event_component					=null;
	};
	this.get_component_object_by_component_id=function(my_component_id)
	{
		if(my_component_id<0)
			return null;
		if(my_component_id>=this.render.component_array_sorted_by_id.length)
			return null;
		return this.render.component_array_sorted_by_id[my_component_id];
	};
	this.get_component_object_by_component_name=function(my_component_name)
	{
		for(var begin_pointer=0,end_pointer=this.render.component_array_sorted_by_name.length-1;begin_pointer<=end_pointer;){
			var middle_pointer=Math.floor((begin_pointer+end_pointer)/2.0);
			var array_component_name=this.render.component_array_sorted_by_name[middle_pointer].component_name;
			if(array_component_name<my_component_name)
				begin_pointer=middle_pointer+1;
			else if(array_component_name>my_component_name)
				end_pointer=middle_pointer-1;
			else 
				return this.render.component_array_sorted_by_name[middle_pointer];
		};
		return null;
	};
	this.get_component_event_processor=function(my_component_name_or_id)
	{
		this.last_operate_component_id=-1;
		switch(typeof(my_component_name_or_id)){
		case "string":
			my_component_name_or_id=this.get_component_object_by_component_name(my_component_name_or_id);
			if(my_component_name_or_id==null)
				return null;
			my_component_name_or_id=my_component_name_or_id.component_id;
			break;
		case "number":
			break;
		default:
			return null;
		}
		if((my_component_name_or_id<0)||(my_component_name_or_id>=this.render.component_event_processor.length))
			return null;
		var ret_val=this.render.component_event_processor[my_component_name_or_id];
		if((typeof(ret_val)!="object")||(ret_val==null))
			return null;
		this.last_operate_component_id=my_component_name_or_id;
		return ret_val;
	};
	this.get_component_call_processor=function(my_component_name_or_id)
	{
		this.last_operate_component_id=-1;
		switch(typeof(my_component_name_or_id)){
		case "string":
			my_component_name_or_id=this.get_component_object_by_component_name(my_component_name_or_id);
			if(my_component_name_or_id==null)
				return null;
			my_component_name_or_id=my_component_name_or_id.component_id;
			break;
		case "number":
			break;
		default:
			return null;
		}
		if((my_component_name_or_id<0)||(my_component_name_or_id>=this.render.component_call_processor.length))
			return null;
		var ret_val=this.render.component_call_processor[my_component_name_or_id];
		if((typeof(ret_val)!="object")||(ret_val==null))
			return null;
		this.last_operate_component_id=my_component_name_or_id;
		return ret_val;
	};
	this.set_event_component=function(my_component_name_or_id)
	{
		switch(typeof(my_component_name_or_id)){
		case "string":
			my_component_name_or_id=this.get_component_object_by_component_name(my_component_name_or_id);
			if(my_component_name_or_id==null)
				return;
			my_component_name_or_id=my_component_name_or_id.component_id;
			break;
		case "number":
			break;
		default:
			return;
		}
		if((my_component_name_or_id<0)||(my_component_name_or_id>=this.render.component_event_processor.length))
			return;
		var cep=this.render.component_event_processor[my_component_name_or_id];
		if(typeof(cep)!="object")
			return;
		if(cep==null)
			return;
		if(typeof(cep.set_event_component)!="function")
			return;
		cep.set_event_component(my_component_name_or_id,this);
	};
}