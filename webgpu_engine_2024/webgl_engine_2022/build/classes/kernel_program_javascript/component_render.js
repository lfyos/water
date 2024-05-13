function construct_component_render_parameter(render_number)
{
	this.modify_component_render_parameter=function(component_append_data,component_delete_data,render)
	{
		for(var p,render_id=-1,part_id=-1,render_buffer_id=-1,i=0,ni=component_append_data.length;i<ni;i++){
			switch((p=component_append_data[i]).length){
			default:
				alert("error component_render_parameter length:"+p.length);
				continue;
			case 6:
				render_id		=p.shift();
				part_id			=p.shift();
			case 4:
				render_buffer_id=p.shift();
			case 3:
				break;
			}
			var data_buffer_id		=p[0];
			var instance_id			=p[1];
    	    var new_instance_data	=p[2];
			var part_object			=render.part_array[render_id][part_id];
    	    var render_parameter	=part_object.component_render_parameter; 
    		
    		while(render_parameter.length<=render_buffer_id)
    			render_parameter.push(new Array());
    		
    		render_parameter=render_parameter[render_buffer_id];
			render_parameter[instance_id]=[data_buffer_id,new_instance_data];
		}
		
		var delete_index_id,render_id=-1,part_id=-1,render_buffer_id=-1;
		for(var i=0,ni=component_delete_data.length;i<ni;i++){
			if(typeof(delete_index_id=component_delete_data[i])!="number"){
				switch(delete_index_id.length){
				default:
					alert("error component_delete_data length:"+delete_index_id.length);
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
    	    var render_parameter=render.part_array[render_id][part_id].component_render_parameter; 
    		while(render_parameter.length<=render_buffer_id)
    			render_parameter.push(new Array());
			render_parameter=render_parameter[render_buffer_id];
			
	   		var last_instance_data=render_parameter.pop();
	   		if(delete_index_id<render_parameter.length)
	   			render_parameter[delete_index_id]=last_instance_data;
		}
	}
	this.modify_component_buffer_parameter=function(component_buffer_data,render)
	{
		for(var p,render_id=-1,part_id=-1,i=0,ni=component_buffer_data.length;i<ni;i++){
			switch((p=component_buffer_data[i]).length){
			default:
				alert("error component_buffer_data length: "+p.length);
				continue;
			case 4:
				render_id		=p.shift();
				part_id			=p.shift();
			case 2:
				break;
			}
			var data_buffer_id	=p.shift();
			var buffer_data_item=p.shift();
			
			var part_object		=render.part_array	[render_id][part_id];
			var part_driver		=render.part_driver	[render_id][part_id];
			var render_driver	=render.render_driver[render_id];
			var component_id	=part_object.part_component_id_and_driver_id[data_buffer_id][0];
			var driver_id		=part_object.part_component_id_and_driver_id[data_buffer_id][1];
			var component_driver=part_object.component_driver_array[data_buffer_id];
			
			if((typeof(component_driver)=="object")&&(component_driver!=null))
				if(typeof(component_driver.append_component_parameter)=="function")
					component_driver.append_component_parameter(
						component_id,		driver_id,		render_id,		part_id,
						buffer_data_item,	part_object,	part_driver,	render_driver,	render);
		}
	}
}
