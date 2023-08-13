function construct_component_driver(
	component_id,	driver_id,		render_id,		part_id,		data_buffer_id,
	init_data,		part_object,	part_driver,	render_driver,	render)
{
	this.location_data=new Array();
	
	this.draw_component=function(method_data,render_data,
			render_id,part_id,data_buffer_id,component_id,driver_id,
			component_render_parameter,component_buffer_parameter,
			project_matrix,part_object,part_driver,render_driver,render)	
	{
		var computer				=render.computer;
		var component_location		=render.component_location_data;
		
		var modifier_container_id	=component_render_parameter;
		var modifier_current_time	=render.modifier_current_time[modifier_container_id];
		
		while(component_buffer_parameter.length>0)
			for(var p=component_buffer_parameter.shift();p.length>0;)
				this.location_data.push(p.shift());
		
		var old_location_data=this.location_data;
		this.location_data=new Array();

		for(var i=0,ni=old_location_data.length;i<ni;i++){
			if(typeof(old_location_data[i])=="number")
				this.location_data=new Array();
			else
				this.location_data.push(old_location_data[i]);
		}
		
		old_location_data=this.location_data;
		this.location_data=new Array();	
		for(var i=0,ni=old_location_data.length;i<ni;i++){
			var location_item		=old_location_data[i];
			var component_id		=location_item[0];
			var start_time			=location_item[1];
			var start_location		=location_item[2];
			var terminate_time		=location_item[3];
			var terminate_location	=location_item[4];
					
			if(start_location.length<16){
				start_location=component_location.decode_location(start_location);
				location_item[2]=start_location;
			}
			if(terminate_location.length<16){
				terminate_location=component_location.decode_location(terminate_location);
				location_item[4]=terminate_location;
			}
			var p=(modifier_current_time-start_time)/(terminate_time-start_time);
			var not_terminated_flag=(p<1.0)?true:false;
			p=(p<=0.0)?0.0:(p>=1.0)?1.0:p;
	
			var loca=new Array();
			for(var j=0,nj=start_location.length;j<nj;j++)
				loca[j]=start_location[j]*(1.0-p)+terminate_location[j]*p;
						
			var p0=computer.caculate_coordinate(loca,0,0,0);
			var dx=computer.sub_operation(computer.caculate_coordinate(loca,1,0,0),p0);
			var dy=computer.sub_operation(computer.caculate_coordinate(loca,0,1,0),p0);
			var dz=computer.cross_operation(dx,dy);
				dx=computer.cross_operation(dy,dz);
						
			var px=computer.add_operation(p0,computer.expand_operation(dx,1.0));
			var py=computer.add_operation(p0,computer.expand_operation(dy,1.0));
			var pz=computer.add_operation(p0,computer.expand_operation(dz,1.0));
					
			loca=computer.create_point_location(p0,px,py,pz);
			loca=computer.matrix_multiplication(loca,computer.standard_negative);
							
			component_location.modify_one_component_location(component_id,loca);
			loca=component_location.get_component_location(component_id);
						
			for(var j=5,nj=location_item.length;j<nj;j+=2){
				var follow_component_id	=location_item[k+0];
				var follow_loca			=location_item[k+1];
				if(follow_loca.length<16){
					follow_loca=component_location.decode_location(follow_loca);
					location_item[k+1]=follow_loca;
				}
				follow_loca=computer.matrix_multiplication(loca,follow_loca)
				component_location.modify_one_component_location(follow_component_id,follow_loca);
				component_location.component[follow_component_id].caculate_location_flag=true;
			}
			if(not_terminated_flag)
				this.location_data.push(location_item);
		}	
	}
};
