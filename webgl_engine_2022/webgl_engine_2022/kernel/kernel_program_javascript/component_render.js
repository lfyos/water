function construct_component_render_parameter()
{
	this.destroy=function()
	{
		this.get_render_list					=null;
		this.modify_component_render_parameter	=null;
		this.modify_component_buffer_parameter	=null;
	};
	this.get_render_list=function(render_buffer_id,render)
	{
		var ret_val=new Array();
		
		for(var render_id=0,render_number=render.part_array.length;render_id<render_number;render_id++){
			if(typeof(render.part_array[render_id])=="undefined")
				continue;
			for(var part_id=0,part_number=render.part_array[render_id].length;part_id<part_number;part_id++){
				if(typeof(render.part_array[render_id][part_id])=="undefined")
					continue;
				var part_object=render.part_array[render_id][part_id];
				var component_render_parameter=part_object.component_render_parameter;
				var component_buffer_parameter=part_object.component_buffer_parameter;
				if(render_buffer_id>=component_render_parameter.length)
					continue;
		    	if((component_render_parameter=component_render_parameter[render_buffer_id]).length<=0)
		    		continue;
		    	ret_val.push([render_id,part_id,component_render_parameter,component_buffer_parameter]);
		    }
		}
    	return ret_val;
	};
	this.modify_component_render_parameter=function(component_append_data,component_delete_data,render)
	{
		for(var p,render_id=-1,part_id=-1,render_buffer_id=-1,i=0,ni=component_append_data.length;i<ni;i++){
			switch((p=component_append_data[i]).length){
			default:
				continue;
			case 5:
				render_id		=p.shift();
				part_id			=p.shift();
			case 3:
				render_buffer_id=p.shift();
			case 2:
				break;
			}
			var instance_id			=p[0];
    	    var new_instance_data	=p[1];
    	    
			var part_object=render.part_array[render_id][part_id];
    	    
    	    p=part_object.component_render_parameter;
    		if(p.length<=render_buffer_id)
    			for(var i=p.length;i<=render_buffer_id;i++)
    				p[i]=new Array();
			p=p[render_buffer_id];
    	    
    	    if(instance_id<p.length){	
				if(typeof(part_object.part_driver_object.replace_render_parameter)=="function")
					part_object.part_driver_object.replace_render_parameter(
						instance_id,p[instance_id],new_instance_data,part_object,render_buffer_id);
			}else{
				if(typeof(part_object.part_driver_object.append_render_parameter)=="function")
					part_object.part_driver_object.append_render_parameter(
						instance_id,new_instance_data,part_object,render_buffer_id);
			}
			p[instance_id]=new_instance_data;
		}
		
		for(var delete_index_id,render_id=-1,part_id=-1,render_buffer_id=-1,i=0,ni=component_delete_data.length;i<ni;i++){
			if(typeof(delete_index_id=component_delete_data[i])!="number"){
				switch(delete_index_id.length){
				default:
					continue;
				case 4:
					render_id		=delete_index_id.shift();
					part_id			=delete_index_id.shift();
				case 2:
					render_buffer_id=delete_index_id.shift();
					delete_index_id	=delete_index_id.shift();
					break;
				}
			}
			var part_object=render.part_array[render_id][part_id];
		   	var p=part_object.component_render_parameter;
    		if(p.length<=render_buffer_id)
    			for(var i=p.length;i<=render_buffer_id;i++)
    				p[i]=new Array();
			p=p[render_buffer_id];
			
			if(typeof(part_object.part_driver_object.delete_render_parameter)=="function")
				part_object.part_driver_object.delete_render_parameter(
					delete_index_id,p[delete_index_id],part_object,render_buffer_id);
	   		var last_instance_data=p.pop();
	   		if(delete_index_id<p.length)
	   			p[delete_index_id]=last_instance_data;
		}
	}
	this.modify_component_buffer_parameter=function(component_buffer_data,render)
	{
		for(var p,render_id=-1,part_id=-1,i=0,ni=component_buffer_data.length;i<ni;i++){
			if((p=component_buffer_data[i]).length==4){
				render_id		=p.shift();
				part_id			=p.shift();
			}
			var buffer_id		=p.shift();
			var buffer_data_item=p.shift();
			
			var part_object=render.part_array[render_id][part_id];
			
			for(p=part_object.component_buffer_parameter;p.length<=buffer_id;)
				p.push(new Array());					
			p=p[buffer_id];
			p.push(buffer_data_item);
			
			while(p.length>part_object.property.max_component_data_buffer_number){
				var shift_data=p.shift();
				if(typeof(part_object.part_driver_object.shift_component_parameter)=="function")
					part_object.part_driver_object.shift_component_parameter(buffer_id,shift_data,part_object);
			}
		}
	}
}
