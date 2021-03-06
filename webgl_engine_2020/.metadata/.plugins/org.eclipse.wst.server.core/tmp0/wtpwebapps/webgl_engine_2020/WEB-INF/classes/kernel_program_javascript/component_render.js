function construct_component_render_object(max_render_number,my_parameter)
{
	this.parameter=my_parameter;
	this.component_render_object=new Array();
	this.collect_array=new Array(max_render_number);
	
	for(var i=0;i<max_render_number;i++)
		this.collect_array[i]=new Array();
	
	this.get_render_list=function(render_buffer_id)
	{
		var p=this.component_render_object;
    	if(p.length<=render_buffer_id)
    		for(var i=p.length;i<=render_buffer_id;i++)
    			p[i]={
    				render_data	:	new Array(),
    				render_list	:	new Array()
    			}
    	p=p[render_buffer_id];
    	var render_id_collect=new Array();
    	for(var i=0,ni=p.render_list.length;i<ni;i++){
    		var render_id	=p.render_list[i][0];
    		var part_id		=p.render_list[i][1];
    		
    		var render_data	=p.render_data[render_id][part_id];
    		
    		if((p.render_list[i][2]=render_data.component_render_data).length<=0)
    			render_data.in_render_list_flag=false;
    		else{
    			this.collect_array[render_id].push(p.render_list[i]);
    			if(this.collect_array[render_id].length==1)
    				render_id_collect.push(render_id);
    		}
    	}
    	var new_render_number=0;
    	for(var pointer=0,i=0,ni=render_id_collect.length;i<ni;i++){
    		var render_id=render_id_collect[i];
    		
    		var my_array=this.collect_array[render_id];
    		new_render_number+=my_array.length;
    		for(var j=0,nj=my_array.length;j<nj;j++)
    			p.render_list[pointer++]=my_array[j];
    		my_array.length=0;
    	}
    	if(p.render_list.length!=new_render_number)
    		p.render_list.length=new_render_number;
    	
    	return p.render_list;
	};
	
	this.get_component_render_data=function(render_id,part_id,render_buffer_id)
	{
		var p=this.component_render_object;
    	if(p.length<=render_buffer_id)
    		for(var i=p.length;i<=render_buffer_id;i++)
    			p[i]={
    				render_data	:	new Array(),
    				render_list	:	new Array()
    			}

    	p=p[render_buffer_id].render_data;
		if(p.length<=render_id)
    		for(var i=p.length;i<=render_id;i++)
    			p[i]=new Array();

    	p=p[render_id];
    	if(p.length<=part_id)
    		for(var i=p.length;i<=part_id;i++)
    			p[i]={
    				component_render_data	:	new Array(),
    				in_render_list_flag		:	false
    			};
    	return p[part_id];
	};
	
	this.modify_component_render_data=function(render_buffer_id,component_append_data,component_delete_data)
	{
		for(var i=0,ni=component_append_data.length;i<ni;){
	    	var render_id				=component_append_data[i++];
	    	var	part_id					=component_append_data[i++];
	    	var	component_append_array	=component_append_data[i++];
	    	
	    	var p=this.get_component_render_data(render_id,part_id,render_buffer_id);
	    	for(var j=0,nj=component_append_array.length;j<nj;){
    	    	var instance_id		=component_append_array[j++];
    	    	var instance_data	=component_append_array[j++];
    	    	p.component_render_data[instance_id]=instance_data;
    	    }
    	    if(!(p.in_render_list_flag)){
	    	    p.in_render_list_flag=true;
	    	    this.component_render_object[render_buffer_id].render_list.push([render_id,part_id,null]);
    	    }
		}
		for(var i=0,ni=component_delete_data.length;i<ni;){
		   	var render_id				=component_delete_data[i++];
		   	var	part_id					=component_delete_data[i++];
		   	var	request_delete_id_array	=component_delete_data[i++];    	
		   	var p=this.get_component_render_data(render_id,part_id,render_buffer_id);
		   	for(var j=0,nj=request_delete_id_array.length;j<nj;){
	   			var delete_index_id=request_delete_id_array[j++];
	   			var pp=p.component_render_data.pop();
	   			if(p.component_render_data.length!=delete_index_id)
	   				p.component_render_data[delete_index_id]=pp;
	   		}	
		}
	};
};
