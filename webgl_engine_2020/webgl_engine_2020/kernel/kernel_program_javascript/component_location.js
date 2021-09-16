function construct_component_location_object(component_number,my_computer,my_gl)
{
	this.gl=my_gl;
	this.computer=my_computer;
	this.component_number=component_number;

	this.version_id=2;
	this.component=new Array();
	for(var i=0;i<component_number;i++){
		this.component[i]							=new Object();
		this.component[i].relative_version_id		=1;
		this.component[i].absolute_version_id		=0;
		this.component[i].caculate_location_flag	=false;
		this.component[i].uniform_block_modify_flag	=true;
		this.component[i].absolute_location			=this.computer.create_move_rotate_matrix(0, 0, 0,	0,	0,	0);
		this.component[i].move_matrix				=this.component[i].absolute_location;
		
		this.component[i].parent					=-1;
		this.component[i].relative					=this.computer.create_move_rotate_matrix(0, 0, 0,	0,	0,	0);
	};
	this.component_buffer_object=new Array(component_number);
	for(var i=0;i<component_number;i++)
		this.component_buffer_object[i]=null;
	
	this.component_buffer_object[0]=this.gl.createBuffer();
	this.gl.bindBuffer(this.gl.UNIFORM_BUFFER,this.component_buffer_object[0]);
	this.gl.bufferData(this.gl.UNIFORM_BUFFER,128,this.gl.DYNAMIC_DRAW);
	this.gl.bufferSubData(this.gl.UNIFORM_BUFFER,0,
			new Float32Array([1,0,0,0,	0,1,0,0,	0,0,1,0,	0,0,0,1]),0);
	this.gl.bufferSubData(this.gl.UNIFORM_BUFFER,64,new Int32Array([0]),0);
	
	this.component_bind_point_id=this.gl.getParameter(this.gl.MAX_UNIFORM_BUFFER_BINDINGS)-4;
	this.gl.bindBufferBase(this.gl.UNIFORM_BUFFER,this.component_bind_point_id,this.component_buffer_object[0]);

	this.terminate_component_location_object=function()
	{
		for(var i=0;i<this.component_number;i++){
			this.component[i].absolute_location	=null;
			this.component[i].move_matrix		=null;
			this.component[i].relative			=null;
			this.component[i]=null;
		};
		this.component=null;
		
		for(var i=0;i<this.component_number;i++){
			if(typeof(this.component_buffer_object[i])!="object")
				continue;
			if(this.component_buffer_object[i]==null)
				continue;
			this.gl.deleteBuffer(this.component_buffer_object[i]);
			this.component_buffer_object[i]=null;
		}
		this.component_buffer_object=null;
	}
	this.modify_one_component_location=function(component_id,loca)
	{
		if(component_id<this.component.length){
			this.component[component_id].move_matrix		=loca;
			this.component[component_id].relative_version_id=this.version_id++;
		}
	};
	this.decode_location=function(data)
	{
		if(data.length<=0)
			return [
				1,	0,	0,	0,
				0,	1,	0,	0,
				0,	0,	1,	0,
				0,	0,	0,	1
			];
		
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
		if((component_id<0)||(component_id>=(this.component.length)))
			return [1,	0,	0,	0,		0,	1,	0,	0,		0,	0,	1,	0,		0,	0,	0,	1];
		return this.component[component_id].move_matrix;
	};
	this.get_component_location_routine=function(component_id)
	{
		if((component_id<0)||(component_id>=(this.component.length)))
			return [1,	0,	0,	0,		0,	1,	0,	0,		0,	0,	1,	0,		0,	0,	0,	1];
		if(typeof(this.component[component_id])=="undefined")
			return [1,	0,	0,	0,		0,	1,	0,	0,		0,	0,	1,	0,		0,	0,	0,	1];
		
		if(this.component[component_id].caculate_location_flag){
			if(this.component[component_id].absolute_version_id<this.component[component_id].relative_version_id){
				this.component[component_id].absolute_version_id=this.component[component_id].relative_version_id;
				this.component[component_id].absolute_location	=this.component[component_id].move_matrix;
				this.component[component_id].uniform_block_modify_flag=true;
			}
			return this.component[component_id].absolute_location;
		}
		
		var parent_id;

		if((parent_id=this.component[component_id].parent)<0){
			if(this.component[component_id].absolute_version_id<this.component[component_id].relative_version_id){
				this.component[component_id].absolute_version_id=this.component[component_id].relative_version_id;
				this.component[component_id].absolute_location=this.computer.matrix_multiplication(
						this.component[component_id].relative,this.component[component_id].move_matrix);
				
				this.component[component_id].uniform_block_modify_flag=true;
			}
			return this.component[component_id].absolute_location;
		}

		if(typeof(this.component[parent_id])=="undefined")
			return this.computer.create_move_rotate_matrix(0,0,0,0,0,0);

		var number=0,loca=this.get_component_location_routine(parent_id);

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
		
		this.component[component_id].uniform_block_modify_flag=true;
		
		return loca;
	}
	this.get_component_location=function(component_id)
	{
		var loca;
		if((loca=this.get_component_location_routine(component_id))==null)
			return [1,	0,	0,	0,		0,	1,	0,	0,		0,	0,	1,	0,		0,	0,	0,	1];
		
		if(this.component[component_id].uniform_block_modify_flag){
			if(this.component_buffer_object[component_id]!=null)
				this.gl.bindBuffer(this.gl.UNIFORM_BUFFER,this.component_buffer_object[component_id]);
			else{
				this.component_buffer_object[component_id]=this.gl.createBuffer();
				this.gl.bindBuffer(this.gl.UNIFORM_BUFFER,this.component_buffer_object[component_id]);
				this.gl.bufferData(this.gl.UNIFORM_BUFFER,128,this.gl.DYNAMIC_DRAW);
				this.gl.bufferSubData(this.gl.UNIFORM_BUFFER,64,new Int32Array([component_id]),0);
			}
			this.gl.bufferSubData(this.gl.UNIFORM_BUFFER,0,new Float32Array(loca),0);
			this.component[component_id].uniform_block_modify_flag=false;
		}
		this.gl.bindBufferBase(this.gl.UNIFORM_BUFFER,
			this.component_bind_point_id,this.component_buffer_object[component_id]);
		return loca;
	}
};
