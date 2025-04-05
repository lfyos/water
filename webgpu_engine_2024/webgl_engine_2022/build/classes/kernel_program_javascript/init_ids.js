function init_ids_of_part_and_component(
	scene,sorted_component_name_id,part_component_id_and_driver_id)
{
	var component_number=sorted_component_name_id.length;

	scene.component_array_sorted_by_id		=new Array(component_number);
	scene.component_object					=new Object();
	
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
		scene.component_array_sorted_by_id[my_component_id]=p;
		
		if(typeof(scene.component_object[my_component_name])!="undefined")
			console.log("several components have same component name:	"+my_component_name);
			
		scene.component_object[my_component_name]=p;
	};

	for(var i=0;i<component_number;i++){
		var p=scene.component_array_sorted_by_id[i];
		var my_component_children=new Array(p.component_children.length);
		for(var j=0,nj=my_component_children.length;j<nj;j++)
			my_component_children[j]=scene.component_array_sorted_by_id[p.component_children[j]];
		p.component_children=my_component_children;
	};
	
	for(var i=0;i<component_number;i++){
		var p=scene.component_array_sorted_by_id[i];
		for(var j=0,nj=p.component_children.length;j<nj;j++)
			p.component_children[j].component_parent=p;
	};
	
	var system_bindgroup_id=new Array();
	
	var render_number=part_component_id_and_driver_id.length;
	for(var render_id=0;render_id<render_number;render_id++){
		var part_number=part_component_id_and_driver_id[render_id].length;
		for(var part_id=0;part_id<part_number;part_id++){
			var id_array=part_component_id_and_driver_id[render_id][part_id];
			var data_buffer_number=id_array.length;
			for(var data_buffer_id=0;data_buffer_id<data_buffer_number;data_buffer_id++){				
				var my_component_id			=id_array[data_buffer_id][0];
				var my_driver_id			=id_array[data_buffer_id][1];				
				var my_system_bindgroup_id	=system_bindgroup_id.length;
				
				system_bindgroup_id[my_system_bindgroup_id]={
					render_id			:	render_id,
					part_id				:	part_id,
					data_buffer_id		:	data_buffer_id,
						
					component_id		:	my_component_id,
					driver_id			:	my_driver_id,
						
					system_bindgroup_id	:	my_system_bindgroup_id
				}
				id_array[data_buffer_id]=system_bindgroup_id[my_system_bindgroup_id];
				scene.component_array_sorted_by_id[my_component_id].
					component_ids[my_driver_id]=id_array[data_buffer_id];
			}
		};
	};

	for(var p,i=0;i<component_number;i++)
		if((p=scene.component_array_sorted_by_id[i]).component_ids.length>0)
			p.component_system_bindgroup_id=p.component_ids[p.component_ids.length-1].system_bindgroup_id;
		else{
			p.component_system_bindgroup_id=system_bindgroup_id.length;
			system_bindgroup_id[p.component_system_bindgroup_id]={
				render_id			:	-1,
				part_id				:	-1,
				data_buffer_id		:	-1,
						
				component_id		:	i,
				driver_id			:	-1,
						
				system_bindgroup_id	:	p.component_system_bindgroup_id
			};
		};
	scene.system_bindgroup_id				=system_bindgroup_id;
	scene.part_component_id_and_driver_id	=part_component_id_and_driver_id;
	
	return;
}
