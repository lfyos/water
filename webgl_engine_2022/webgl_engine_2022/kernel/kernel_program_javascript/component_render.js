function construct_component_render_parameter()
{
	this.destroy=function()
	{
		this.get_render_list					=null;
		this.modify_component_render_parameter	=null;
		this.modify_component_buffer_parameter	=null;
	};
	this.modify_component_render_parameter=function(component_append_data,component_delete_data,render)
	{
		for(var p,render_id=-1,part_id=-1,render_buffer_id=-1,i=0,ni=component_append_data.length;i<ni;i++){
			switch((p=component_append_data[i]).length){
			default:
				alert("error component_render_parameter length:"+p.length);
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
			var part_driver=render.part_driver[render_id][part_id];
    	    
    	    p=part_object.component_render_parameter;
    		if(p.length<=render_buffer_id)
    			for(var j=p.length;j<=render_buffer_id;j++)
    				p[j]=new Array();
			p=p[render_buffer_id];
    	    
    	    if(instance_id<p.length){
				if(typeof(part_driver)=="object")
					if(part_driver!=null)
						if(typeof(part_driver.replace_render_parameter)=="function")
							part_driver.replace_render_parameter(instance_id,
								p[instance_id],new_instance_data,part_object,render_buffer_id,render);
			}else if(instance_id==p.length){
				if((typeof(part_driver)=="object")&&(part_driver!=null))
					if(typeof(part_driver.append_render_parameter)=="function")
						part_driver.append_render_parameter(
								instance_id,new_instance_data,part_object,render_buffer_id,render);
			}else{
				alert("error instance_id of component_render_parameter length:"+instance_id+"/"+p.length);
				continue;
			}
			p[instance_id]=new_instance_data;
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
			var part_object=render.part_array[render_id][part_id];
			var part_driver=render.part_driver[render_id][part_id];
			
		   	var p=part_object.component_render_parameter;
    		if(p.length<=render_buffer_id)
    			for(var j=p.length;j<=render_buffer_id;j++)
    				p[j]=new Array();
			p=p[render_buffer_id];
			
			if((typeof(part_driver)=="object")&&(part_driver!=null))
				if(typeof(part_driver.delete_render_parameter)=="function")
						part_driver.delete_render_parameter(
							delete_index_id,	p[delete_index_id],
							p.length-1,			p[p.length-1],
							part_object,		render_buffer_id,
							render);

	   		var last_instance_data=p.pop();
	   		if(delete_index_id<p.length)
	   			p[delete_index_id]=last_instance_data;
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
			var buffer_id		=p.shift();
			var buffer_data_item=p.shift();
			
			var part_object=render.part_array[render_id][part_id];
			var part_driver=render.part_driver[render_id][part_id];
			
			for(p=part_object.component_buffer_parameter;p.length<=buffer_id;)
				p.push(new Array());					
			p=p[buffer_id];
			p.push(buffer_data_item);
			
			if((typeof(part_driver)=="object")&&(part_driver!=null))
				if(typeof(part_driver.append_component_parameter)=="function")
					part_driver.append_component_parameter(
						buffer_id,buffer_data_item,part_object,render);

			while(p.length>part_object.property.max_component_data_buffer_number){
				if((typeof(part_driver)=="object")&&(part_driver!=null))
					if(typeof(part_driver.shift_component_parameter)=="function")
						part_driver.shift_component_parameter(buffer_id,p[0],part_object,render);
				p.shift();
			}
		}
	}
}
