function construct_operate_component(my_scene)
{
	this.scene=my_scene;
	this.last_operate_component_id=-1;
	
	this.get_component_object=function(my_component_name_or_id)
	{
		switch(typeof(my_component_name_or_id)){
		case "string":
			var ret_val=this.scene.component_object[my_component_name_or_id];
			return (typeof(ret_val)=="object")?ret_val:null;
		case "number":
			if(my_component_name_or_id<0)
				return null;
			if(my_component_name_or_id>=this.scene.component_array_sorted_by_id.length)
				return null;
			return this.scene.component_array_sorted_by_id[my_component_name_or_id];
		default:
			return null;
		}
	}
	this.get_component_event_processor=function(my_component_name_or_id)
	{
		this.last_operate_component_id=-1;
		
		var my_component_object;
		if((my_component_object=this.get_component_object(my_component_name_or_id))==null)
			return null;
		var ret_val=this.scene.component_event_processor[my_component_object.component_id];
		if((typeof(ret_val)!="object")||(ret_val==null))
			return null;
		this.last_operate_component_id=my_component_object.component_id;
		return ret_val;
	};
	this.get_component_call_processor=function(my_component_name_or_id)
	{
		this.last_operate_component_id=-1;
		var my_component_object;
		if((my_component_object=this.get_component_object(my_component_name_or_id))==null)
			return null;
		var ret_val=this.scene.component_call_processor[my_component_object.component_id];
		if((typeof(ret_val)!="object")||(ret_val==null))
			return null;
		this.last_operate_component_id=my_component_object.component_id;
		return ret_val;
	};
}