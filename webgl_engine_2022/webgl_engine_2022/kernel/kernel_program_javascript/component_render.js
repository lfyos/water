function construct_component_render_parameter()
{
	this.destroy=function()
	{
		this.get_render_list					=null;
		this.modify_component_render_parameter	=null;
		this.get_component_render_parameter		=null;
		this.get_component_buffer_parameter		=null;
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
				if(render_buffer_id>=component_render_parameter.length)
					continue;
		    	if((component_render_parameter=component_render_parameter[render_buffer_id]).length<=0)
		    		continue;
		    	ret_val.push([render_id,part_id,component_render_parameter,part_object.component_buffer_parameter]);
		    }
		}
    	return ret_val;
	};
	
	this.modify_component_render_parameter=function(
			render_buffer_id,component_append_data,component_delete_data,render)
	{
		for(var i=0,ni=component_append_data.length;i<ni;){
	    	var render_id				=component_append_data[i++];
	    	var	part_id					=component_append_data[i++];
	    	var	component_append_array	=component_append_data[i++];

    	    var part_object=render.part_array[render_id][part_id];
    	    var p=part_object.component_render_parameter;
    		if(p.length<=render_buffer_id)
    			for(var i=p.length;i<=render_buffer_id;i++)
    				p[i]=new Array();
			p=p[render_buffer_id];
 
	    	for(var j=0,nj=component_append_array.length;j<nj;){
    	    	var instance_id			=component_append_array[j++];
    	    	var new_instance_data	=component_append_array[j++];
    	    	
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
		}
		for(var i=0,ni=component_delete_data.length;i<ni;){
		   	var render_id				=component_delete_data[i++];
		   	var	part_id					=component_delete_data[i++];
		   	var	request_delete_id_array	=component_delete_data[i++];
		   	
		   	var part_object=render.part_array[render_id][part_id];
		   	var p=part_object.component_render_parameter;
    		if(p.length<=render_buffer_id)
    			for(var i=p.length;i<=render_buffer_id;i++)
    				p[i]=new Array();
			p=p[render_buffer_id];
			
		   	for(var j=0,nj=request_delete_id_array.length;j<nj;j++){
	   			var delete_index_id=request_delete_id_array[j];
	   			if(typeof(part_object.part_driver_object.delete_render_parameter)=="function")
					part_object.part_driver_object.delete_render_parameter(
						delete_index_id,p[delete_index_id],part_object,render_buffer_id);
	   			var last_instance_data=p.pop();
	   			if(delete_index_id<p.length)
	   				p[delete_index_id]=last_instance_data;
	   		}
		}
	}
	
	this.get_component_render_parameter=function(response_data,render)
	{
		var p=response_data,render_buffer_id;
		try{
			render_buffer_id=p[0];
			render.camera.modify_camera_data(render_buffer_id,p[1]);
			this.modify_component_render_parameter(render_buffer_id,p[2][0],p[2][1],render);
			if(p.length>=4){
				if(p[3].length<4){
					render.clip_plane_array[render_buffer_id]				=[0,0,0,0];
					render.clip_plane_matrix_array[render_buffer_id]		=render.computer.create_move_rotate_matrix(0,0,0,0,0,0);
				}else{
					render.clip_plane_array[render_buffer_id]				=[p[3][0],p[3][1],p[3][2],p[3][3]];
					render.clip_plane_matrix_array[render_buffer_id]	
								=render.computer.project_to_plane_location( p[3][0],p[3][1],p[3][2],p[3][3],1.0);
				};
			}else if(typeof(render.clip_plane_array[render_buffer_id])=="undefined"){
				render.clip_plane_array[render_buffer_id]					=[0,0,0,0];
				render.clip_plane_matrix_array[render_buffer_id]			=render.computer.create_move_rotate_matrix(0,0,0,0,0,0);
			};
		}catch(e){
			alert("get_component_render_parameter fail:	"+e.toString());
			return -1;
		};
	    return render_buffer_id;
	};
	this.get_component_buffer_parameter=function(buffer_data,render)
	{
		for(var i=0,ni=buffer_data.length;i<ni;i++){
			var render_id		=buffer_data[i][0];
			var part_id			=buffer_data[i][1];
			var part_buffer_data=buffer_data[i][2];
			var part_object=render.part_array[render_id][part_id];
			var max_buffer_number=part_object.property.max_component_data_buffer_number;
			
			for(var j=0,nj=part_buffer_data.length;j<nj;j++){
				var buffer_id			=part_buffer_data[j][0];
				var buffer_data_item	=part_buffer_data[j][1];
				var p=part_object.component_buffer_parameter;
				while(p.length<=buffer_id)
					p.push(new Array());
					
				p=p[buffer_id];
				p.push(buffer_data_item);

				if(typeof(part_object.part_driver_object.push_component_parameter)=="function")
					part_object.part_driver_object.push_component_parameter(
						buffer_id,buffer_data_item,part_object);

				if(typeof(part_object.part_driver_object.shift_component_parameter)=="function")
					for(;p.length>max_buffer_number;p.shift())
						part_object.part_driver_object.shift_component_parameter(buffer_id,p[0],part_object);
				else
					while(p.length>max_buffer_number)
						p.shift();
			}
		}
	}
}
