function construct_component_location_object(my_component_number,my_computer,my_webgpu)
{
	this.component_number	=my_component_number;
	this.computer			=my_computer;
	this.webgpu				=my_webgpu;

	this.version_id			=3;
	this.identify_matrix	=[	1,	0,	0,	0,		0,	1,	0,	0,		0,	0,	1,	0,		0,	0,	0,	1];
	
	this.component	=new Array();
	
	for(var i=0,ni=this.component_number;i<ni;i++){
		this.component[i]={
			relative_version_id		:	2,
			absolute_version_id		:	1,
			caculate_location_flag	:	false,
			
			relative				:	this.identify_matrix,
			move_matrix				:	this.identify_matrix,
			absolute_location		:	this.identify_matrix,
			
			parent					:	-1
		}
	}

	this.destroy=function()
	{
		for(var i=0;i<this.component_number;i++){
			this.component[i].absolute_location	=null;
			this.component[i].move_matrix		=null;
			this.component[i].relative			=null;
			this.component[i]					=null;

		};
		this.computer	=null;
		this.webgpu		=null;
		this.component	=null;
		
		this.modify_one_component_location	=null;
		this.decode_location				=null;
		this.modify_component_location		=null;
		this.get_one_component_location		=null;
		this.get_component_location			=null;
	}
	
	this.modify_one_component_location=function(component_id,loca)
	{
		if((component_id>=0)&&(component_id<this.component.length)){
			this.component[component_id].move_matrix		=loca;
			this.component[component_id].relative_version_id=this.version_id++;
		}
	};
	this.decode_location=function(data)
	{
		if(data.length<=0)
			return this.identify_matrix;
		
		if(data.length>=16)
			return [
				data[ 0],data[ 1],data[ 2],data[ 3],
				data[ 4],data[ 5],data[ 6],data[ 7],
				data[ 8],data[ 9],data[10],data[11],
				data[12],data[13],data[14],data[15]
			];
		
		var my_data=[0,0,0,0,0,0],code=Math.round(data[data.length-1]);
		for(var i=5,j=data.length-2;((i>=0)&&(j>=0));i--,code=(code>>1))
			if((code&1)!=0)
				my_data[i]=data[j--];
		return this.computer.create_move_rotate_matrix(
				my_data[0],my_data[1],my_data[2],my_data[3],my_data[4],my_data[5]);
	};
	this.modify_component_location=function(component_loca_buffer)
	{
		for(var i=0,ni=component_loca_buffer.length,my_version_id=this.version_id++;i<ni;i++){
			var component_id									= component_loca_buffer[i][0];
			this.component[component_id].caculate_location_flag	=(component_loca_buffer[i][1]>0)?true:false;
			this.component[component_id].move_matrix			=this.decode_location(component_loca_buffer[i][2]);
			
			if(component_loca_buffer[i].length>3){
				this.component[component_id].relative		=this.decode_location(component_loca_buffer[i][3]);
				this.component[component_id].parent			=component_loca_buffer[i][4];
			}
			this.component[component_id].relative_version_id=my_version_id;
		}
	};
	this.get_one_component_location=function(component_id)
	{
		return ((component_id<0)||(component_id>=(this.component.length)))
				?(this.identify_matrix):(this.component[component_id].move_matrix);
	};
	this.get_component_location=function(component_id)
	{
		if((component_id<0)||(component_id>=(this.component.length)))
			return this.identify_matrix;
		if(typeof(this.component[component_id])!="object")
			return this.identify_matrix;
		
		if(this.component[component_id].caculate_location_flag){
			if(this.component[component_id].absolute_version_id<this.component[component_id].relative_version_id){
				this.component[component_id].absolute_version_id=this.component[component_id].relative_version_id;
				this.component[component_id].absolute_location	=this.component[component_id].move_matrix;
			}
			return this.component[component_id].absolute_location;
		}
		
		var parent_id;

		if((parent_id=this.component[component_id].parent)<0){
			if(this.component[component_id].absolute_version_id<this.component[component_id].relative_version_id){
				this.component[component_id].absolute_version_id=this.component[component_id].relative_version_id;
				this.component[component_id].absolute_location=this.computer.matrix_multiplication(
						this.component[component_id].relative,this.component[component_id].move_matrix);
			}
			return this.component[component_id].absolute_location;
		}
		if(typeof(this.component[parent_id])=="undefined")
			return this.identify_matrix;

		var number=0,loca=this.get_component_location(parent_id);

		if(this.component[component_id].absolute_version_id<this.component[parent_id].absolute_version_id){
			this.component[component_id].absolute_version_id=this.component[parent_id].absolute_version_id;
			number++;
		}
		if(this.component[component_id].absolute_version_id<this.component[component_id].relative_version_id){
			this.component[component_id].absolute_version_id=this.component[component_id].relative_version_id;
			number++;
		}
		
		if(number<=0)
			return this.component[component_id].absolute_location;
		
		loca=this.computer.matrix_multiplication(loca,this.component[component_id].relative);
		loca=this.computer.matrix_multiplication(loca,this.component[component_id].move_matrix);
		this.component[component_id].absolute_location=loca;
		
		return loca;
	};
	
	this.get_component_matrix_and_version=function(component_id)
	{
		if((component_id<0)||(component_id>=(this.component.length)))
			return this.identify_matrix;
		var my_matrix=this.get_component_location(component_id);
		var my_version=this.component[component_id].absolute_version_id;
		
		return {
			matrix	:	my_matrix,
			version	:	my_version
		};
	};
};
