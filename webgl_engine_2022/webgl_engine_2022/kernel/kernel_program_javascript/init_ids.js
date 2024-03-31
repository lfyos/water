function init_ids_of_part_and_component(
	sorted_component_name_id,part_component_id_and_driver_id,render)
{
	var component_number=sorted_component_name_id.length;
	
	render.component_array_sorted_by_name	=new Array(component_number);
	render.component_array_sorted_by_id		=new Array(component_number);
	
	for(var i=0;i<component_number;i++){
		var my_component_name		=sorted_component_name_id[i][0];
		var my_component_id	 		=sorted_component_name_id[i][1];
		var my_component_children	=sorted_component_name_id[i][2];
			
		var p={
				component_name		:	my_component_name,
				component_id		:	my_component_id,
				component_parent	:	null,
				component_children	:	my_component_children,
				component_ids		:	new Array(),
				system_bindgroup_id	:	-1
		};
		render.component_array_sorted_by_name[i]=p;
		render.component_array_sorted_by_id[my_component_id]=p;
	};

	for(var i=0;i<component_number;i++){
		var p=render.component_array_sorted_by_id[i];
		var my_component_children=new Array(p.component_children.length);
		for(var j=0,nj=my_component_children.length;j<nj;j++)
			my_component_children[j]=render.component_array_sorted_by_id[p.component_children[j]];
		p.component_children=my_component_children;
	};
	
	for(var i=0;i<component_number;i++){
		var p=render.component_array_sorted_by_id[i];
		for(var j=0,nj=p.component_children.length;j<nj;j++)
			p.component_children[j].component_parent=p;
	};
	
	var system_bindgroup_id			=new Array();
	
	var render_number=part_component_id_and_driver_id.length;
	for(var render_id=0;render_id<render_number;render_id++){
		var part_number=part_component_id_and_driver_id[render_id].length;
		for(var part_id=0;part_id<part_number;part_id++){
			var id_array=part_component_id_and_driver_id[render_id][part_id];
			var data_buffer_number=id_array.length;
			for(var data_buffer_id=0;data_buffer_id<data_buffer_number;data_buffer_id++){				
				var component_id	=id_array[data_buffer_id][0];
				var driver_id		=id_array[data_buffer_id][1];
				
				var my_system_bindgroup_id	=system_bindgroup_id.length;
				id_array[data_buffer_id][2]	=my_system_bindgroup_id;
				
				var p=render.component_array_sorted_by_id[component_id].component_ids;
				p[driver_id]=[render_id,part_id,data_buffer_id,my_system_bindgroup_id];
				
				system_bindgroup_id.push([
					render_id,		part_id,	data_buffer_id,
					component_id,	driver_id,
					my_system_bindgroup_id,
					
					-1,-1
				]);
			}
		};
	};

	for(var i=0;i<component_number;i++){
		var my_system_bindgroup_id=system_bindgroup_id.length;
		render.component_array_sorted_by_id[i].system_bindgroup_id=my_system_bindgroup_id;
		system_bindgroup_id.push([
			-1,	//render_id,
			-1,	//part_id,
			-1,	//data_buffer_id,
			 i,	//component_id,
			-1,	//driver_id,
			my_system_bindgroup_id,
			
			-1,-1
		]);
	};
	
	render.system_bindgroup_id				=system_bindgroup_id;
	render.part_component_id_and_driver_id	=part_component_id_and_driver_id;

	return;
}
