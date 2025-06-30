function extract_driver_create_data(
		sorted_component_name_id_and_create_data,
		part_component_id_and_driver_id_and_create_data)
{
	var my_component_create_data=new Array();
	for(var i=0,ni=sorted_component_name_id_and_create_data.length;i<ni;i++)
		my_component_create_data[my_component_id]=new Array();
	for(var i=0,ni=sorted_component_name_id_and_create_data.length;i<ni;i++){
		var my_component_id=sorted_component_name_id_and_create_data[i][1];
		my_component_create_data[my_component_id]=sorted_component_name_id_and_create_data[i].pop();
	}
	
	var my_render_create_data=new Array();
	for(var i=0,j=0,ni=part_component_id_and_driver_id_and_create_data.length;i<ni;i++,i++,j++){
		my_render_create_data[j]={
			render_create_data	:	part_component_id_and_driver_id_and_create_data[i+1],
			part_create_data	:	new Array()
		}
		part_component_id_and_driver_id_and_create_data[j]=part_component_id_and_driver_id_and_create_data[i];
	}
	part_component_id_and_driver_id_and_create_data.length=part_component_id_and_driver_id_and_create_data.length/2;
	
	for(var i=0,ni=part_component_id_and_driver_id_and_create_data.length;i<ni;i++){
		for(var j=0,k=0,nj=part_component_id_and_driver_id_and_create_data[i].length;j<nj;j++,j++,k++){
			my_render_create_data[i].part_create_data[k]=part_component_id_and_driver_id_and_create_data[i][j+1];
			part_component_id_and_driver_id_and_create_data[i][k]=part_component_id_and_driver_id_and_create_data[i][j];
		}
		part_component_id_and_driver_id_and_create_data[i].length=part_component_id_and_driver_id_and_create_data[i].length/2;
	}
	
	return {
				component_create_data	:	my_component_create_data,
				render_create_data		:	my_render_create_data
			};
}