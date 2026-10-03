function extract_driver_create_data(
		sorted_component_name_id_and_create_data,
		part_component_id_and_driver_id_and_create_data)
{
	var my_component_create_data=new Array();
	var p=sorted_component_name_id_and_create_data;
	for(var i=0,ni=p.length;i<ni;i++)
		my_component_create_data[i]=new Array();
	for(var i=0,ni=p.length;i<ni;i++){
		var my_component_id=p[i][1];
		my_component_create_data[my_component_id]=p[i].pop();
	}
	
	var my_render_create_data=new Array();
	var p=part_component_id_and_driver_id_and_create_data;
	for(var i=0,j=0,ni=p.length;i<ni;i++,i++,j++){
		my_render_create_data[j]={
			render_create_data	:	p[i+1],
			part_create_data	:	new Array()
		}
		p[j]=p[i];
	}
	part_component_id_and_driver_id_and_create_data.length/=2;
	
	var ni=part_component_id_and_driver_id_and_create_data.length;
	for(var i=0;i<ni;i++){
		var p=part_component_id_and_driver_id_and_create_data[i];
		for(var j=0,k=0,nj=p.length;j<nj;j++,j++,k++){
			my_render_create_data[i].part_create_data[k]=p[j+1];
			p[k]=p[j];
		}
		part_component_id_and_driver_id_and_create_data[i].length/=2;
	}

	return	{
				component_create_data	:	my_component_create_data,
				render_create_data		:	my_render_create_data
			};
}