function construct_component_render_parameter(render_number)
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
    	    
			var part_object					=render.part_array[render_id][part_id];
			var part_driver					=render.part_driver[render_id][part_id];
			var component_data_buffer_id	=part_object.component_data_buffer_id;
    	    var render_parameter			=part_object.component_render_parameter; 
    		
    		while(render_parameter.length<=render_buffer_id){
    			component_data_buffer_id.push(new Array());
    			render_parameter.push(new Array());
    		}
    		component_data_buffer_id=component_data_buffer_id[render_buffer_id];
			render_parameter		=render_parameter[render_buffer_id];

    	    if(instance_id>render_parameter.length)
				alert("error instance_id of component_render_parameter length:"
						+instance_id+"/"+render_parameter.length);
			else if(instance_id==render_parameter.length){
				if((typeof(part_driver)=="object")&&(part_driver!=null))
					if(typeof(part_driver.append_render_parameter)=="function")
						part_driver.append_render_parameter(render_buffer_id,
							instance_id,data_buffer_id,new_instance_data,
							part_object,part_driver,render.render_driver[render_id],render);	
				var component_driver=part_object.component_driver_array[data_buffer_id];
				if((typeof(component_driver)=="object")&&(component_driver!=null))
					if(typeof(component_driver.show_component_parameter)=="function")
						component_driver.show_component_parameter(render_buffer_id,instance_id,
							data_buffer_id,new_instance_data,
							part_object,part_driver,render.render_driver[render_id],render);
			}else if(component_data_buffer_id[instance_id]==data_buffer_id){
				if((typeof(part_driver)=="object")&&(part_driver!=null))
					if(typeof(part_driver.replace_render_parameter)=="function")
						part_driver.replace_render_parameter(render_buffer_id,instance_id,
							data_buffer_id,render_parameter[instance_id],new_instance_data,
							part_object,part_driver,render.render_driver[render_id],render);			
				var component_driver=part_object.component_driver_array[data_buffer_id];
				if((typeof(component_driver)=="object")&&(component_driver!=null))
					if(typeof(component_driver.replace_component_parameter)=="function")
						component_driver.replace_component_parameter(render_buffer_id,instance_id,
							data_buffer_id,render_parameter[instance_id],new_instance_data,
							part_object,part_driver,render.render_driver[render_id],render);
			}else{
				if((typeof(part_driver)=="object")&&(part_driver!=null))
					if(typeof(part_driver.replace_render_component)=="function")
						part_driver.replace_render_component(render_buffer_id,instance_id,
							component_data_buffer_id[instance_id],	render_parameter[instance_id],
							data_buffer_id,							new_instance_data,
							part_object,part_driver,render.render_driver[render_id],render);
				var component_driver=part_object.component_driver_array[component_data_buffer_id[instance_id]];
				if((typeof(component_driver)=="object")&&(component_driver!=null))
					if(typeof(component_driver.hide_component_parameter)=="function")
						component_driver.hide_component_parameter(render_buffer_id,instance_id,
							component_data_buffer_id[instance_id],render_parameter[instance_id],
							part_object,part_driver,render.render_driver[render_id],render);
				var component_driver=part_object.component_driver_array[data_buffer_id];
				if((typeof(component_driver)=="object")&&(component_driver!=null))
					if(typeof(component_driver.show_component_parameter)=="function")
						component_driver.show_component_parameter(render_buffer_id,instance_id,
							data_buffer_id,new_instance_data,
							part_object,part_driver,render.render_driver[render_id],render);
			}
			component_data_buffer_id[instance_id]	=data_buffer_id;
			render_parameter[instance_id]			=new_instance_data;
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
			var data_buffer_id=part_object.component_data_buffer_id;
    	    var render_parameter=part_object.component_render_parameter; 
    		while(render_parameter.length<=render_buffer_id){
    			data_buffer_id.push(new Array());
    			render_parameter.push(new Array());
    		}
    		data_buffer_id	=data_buffer_id[render_buffer_id];
			render_parameter=render_parameter[render_buffer_id];
	
			if((typeof(part_driver)=="object")&&(part_driver!=null))
				if(typeof(part_driver.delete_render_parameter)=="function"){
						part_driver.delete_render_parameter(render_buffer_id,
							delete_index_id,
							data_buffer_id[delete_index_id],
							render_parameter[delete_index_id],
							
							render_parameter.length-1,
							data_buffer_id[render_parameter.length-1],
							render_parameter[render_parameter.length-1],
							
							part_object,part_driver,render.render_driver[render_id],render);
				}
			var component_driver=part_object.component_driver_array[data_buffer_id[delete_index_id]];
			if((typeof(component_driver)=="object")&&(component_driver!=null))
					if(typeof(component_driver.delete_component_parameter)=="function")
						component_driver.delete_component_parameter(render_buffer_id,
							delete_index_id,
							data_buffer_id[delete_index_id],
							render_parameter[delete_index_id],
							
							render_parameter.length-1,
							data_buffer_id[render_parameter.length-1],
							render_parameter[render_parameter.length-1],
							
							part_object,part_driver,render.render_driver[render_id],render);
	
			var last_data_buffer_id	=data_buffer_id.pop();
	   		var last_instance_data	=render_parameter.pop();
	   		if(delete_index_id<render_parameter.length){
				data_buffer_id[delete_index_id]		=last_data_buffer_id;
	   			render_parameter[delete_index_id]	=last_instance_data;
	   		}
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
			
			var part_object		=render.part_array	[render_id][part_id];
			var part_driver		=render.part_driver	[render_id][part_id];
			var render_driver	=render.render_driver[render_id];
			var component_id	=part_object.part_component_id_and_driver_id[buffer_id][0];
			var driver_id		=part_object.part_component_id_and_driver_id[buffer_id][1];

			for(p=part_object.component_buffer_parameter;p.length<=buffer_id;)
				p.push(new Array());					
			p=p[buffer_id];
			p.push(buffer_data_item);
			
			if((typeof(part_driver)=="object")&&(part_driver!=null)){
				if(typeof(part_driver.append_component_parameter)=="function")
					part_driver.append_component_parameter(	component_id,	driver_id,
							render_id,		part_id,		buffer_id,		buffer_data_item,
							part_object,	part_driver,	render_driver,	render);
								
				if(typeof(part_driver.create_component_driver)=="function")
					if(typeof(part_object.component_driver_array[buffer_id])=="undefined"){
						part_object.component_driver_array[buffer_id]=new part_driver.create_component_driver(
							component_id,	driver_id,		render_id,			part_id,		buffer_id,
							part_object.component_initialize_data[buffer_id],
							part_object,	part_driver,	render_driver,		render);
						if(typeof(part_object.component_driver_array[buffer_id])=="undefined")
							part_object.component_driver_array[buffer_id]=null;
					}
				var component_driver=part_object.component_driver_array[buffer_id];
				if((typeof(component_driver)=="object")&&(component_driver!=null))
					if(typeof(component_driver.append_component_parameter)=="function")
						component_driver.append_component_parameter(component_id,	driver_id,
									render_id,		part_id,		buffer_id,		buffer_data_item,
									part_object,	part_driver,	render_driver,	render);
			}
			while(p.length>part_object.property.max_component_data_buffer_number){
				if((typeof(part_driver)=="object")&&(part_driver!=null))
					if(typeof(part_driver.shift_component_parameter)=="function")
						part_driver.shift_component_parameter(	component_id,	driver_id,
								render_id,		part_id,		buffer_id,		p[0],
								part_object,	part_driver,	render_driver,	render);
				
				var component_driver=part_object.component_driver_array[buffer_id];
				if((typeof(component_driver)=="object")&&(component_driver!=null))
					if(typeof(component_driver.shift_component_parameter)=="function")
						component_driver.shift_component_parameter(	component_id,	driver_id,
									render_id,		part_id,		buffer_id,		p[0],
									part_object,	part_driver,	render_driver,	render);
				p.shift();
			}
		}
	}
}
