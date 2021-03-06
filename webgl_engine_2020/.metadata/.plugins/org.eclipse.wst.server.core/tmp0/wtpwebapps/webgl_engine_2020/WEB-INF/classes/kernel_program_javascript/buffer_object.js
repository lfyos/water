
function construct_buffer_object(my_gl,my_url,my_channel,my_parameter)
{
	this.gl=my_gl;
	this.url=my_url;
	this.channel=my_channel;
	this.parameter=my_parameter;
	
	this.buffer_object	=new Array();
	this.request_render_part_id=new Array();
	this.buffer_head_request_queue=new Array();
	
	this.current_loading_mesh_number=0;
	
	this.loading_render_id=-1;
	this.loading_part_id=-1;
	this.loaded_buffer_object_file_number=0;
	this.loaded_buffer_object_data_length=0;
	
	this.create_empty_buffer_object=function(
			my_render_id,my_part_id,my_part_from_id,buffer_object_head_data)
	{
		var face_region_data,edge_region_data,point_region_data;
		var face_file_number,edge_file_number,point_file_number;

		try{
			face_region_data=buffer_object_head_data.face.region_data;
			face_file_number=face_region_data.length;
			edge_region_data=buffer_object_head_data.edge.region_data;
			edge_file_number=edge_region_data.length;
			point_region_data=buffer_object_head_data.point.region_data;
			point_file_number=point_region_data.length;
		}catch(e){
			face_region_data=new Array();
			face_file_number=0;
			edge_region_data=new Array();
			edge_file_number=0;
			point_region_data=new Array();
			point_file_number=0;
			
			alert("Part head file error:"+e.toString());
		}
		
		var created_buffer_object;

		if(typeof(this.buffer_object[my_render_id])=="undefined")
			this.buffer_object[my_render_id]=new Array();
		if(typeof(this.buffer_object[my_render_id][my_part_id])=="undefined")
			this.buffer_object[my_render_id][my_part_id]=new Object();

		created_buffer_object = new Object();
		created_buffer_object.server_region_data= face_region_data;
    	created_buffer_object.file_number		= face_file_number;
    	created_buffer_object.loaded_number		= face_file_number;
    	created_buffer_object.region_data		= new Array();
    	created_buffer_object.data_collector	= new Array();
    	created_buffer_object.error_flag		=false;
		this.buffer_object[my_render_id][my_part_id].face=created_buffer_object;

		created_buffer_object = new Object();
		created_buffer_object.server_region_data= null;
    	created_buffer_object.file_number		= 0;
    	created_buffer_object.loaded_number		= 0;
    	created_buffer_object.region_data		= new Array();
    	created_buffer_object.data_collector	= new Array();
    	created_buffer_object.error_flag		=false;
		this.buffer_object[my_render_id][my_part_id].frame=created_buffer_object;
		
		created_buffer_object = new Object();
		created_buffer_object.server_region_data= edge_region_data;
    	created_buffer_object.file_number		= edge_file_number;
    	created_buffer_object.loaded_number		= edge_file_number;
    	created_buffer_object.region_data		= new Array();
    	created_buffer_object.data_collector	= new Array();
    	created_buffer_object.error_flag		=false;
		this.buffer_object[my_render_id][my_part_id].edge=created_buffer_object;

		created_buffer_object = new Object();
		created_buffer_object.server_region_data= point_region_data;
    	created_buffer_object.file_number		= point_file_number;
    	created_buffer_object.loaded_number		= point_file_number;
    	created_buffer_object.region_data		= new Array();
    	created_buffer_object.data_collector	= new Array();
    	created_buffer_object.error_flag		=false;
		this.buffer_object[my_render_id][my_part_id].point=created_buffer_object;
	};
	this.save_data_into_buffer_object=function(object_pointer,buffer_object_data,my_material_id)
	{
		var my_region_data=new Object();
		
		my_region_data.buffer_object 		=this.gl.createBuffer();
		if(typeof(my_region_data.material_id=buffer_object_data.material_id)!="number")
			my_region_data.material_id		=my_material_id;
		my_region_data.item_size			=buffer_object_data.item_size;
		my_region_data.item_number			=buffer_object_data.region_data.length/buffer_object_data.item_size;
		my_region_data.region_box			=buffer_object_data.region_box;
		my_region_data.private_data			=buffer_object_data.private_data;
		
		this.gl.getError();
		
		this.gl.bindBuffer(this.gl.ARRAY_BUFFER,my_region_data.buffer_object);
		this.gl.bufferData(this.gl.ARRAY_BUFFER,
				new Float32Array(buffer_object_data.region_data),this.gl.STATIC_DRAW,0);
		
		object_pointer.error_flag|=(this.gl.getError()==this.gl.NO_ERROR)?false:true;
		
		object_pointer.region_data.push(my_region_data);

		return;
	};
	this.test_busy=function()
	{
		return this.parameter.max_loading_number-this.current_loading_mesh_number;
	};
	this.process_buffer_object_data=function(buffer_object_data,request_str,
			object_pointer,frame_object_pointer,max_buffer_object_data_length,
			decode_function,part_information,part_material,part_property,request_file_id)
	{
		var my_material_id	=object_pointer.server_region_data[request_file_id].material_id;
		var my_region_box	=object_pointer.server_region_data[request_file_id].region_box;	
		var my_item_number	=object_pointer.server_region_data[request_file_id].item_number;
		
		if(my_material_id>=(object_pointer.data_collector.length)){
			var bak=object_pointer.data_collector;
			object_pointer.data_collector=new Array(my_material_id+1);
			for(var i=0,ni=bak.length;i<ni;i++)
				object_pointer.data_collector[i]=bak[i];
			for(var i=bak.length,ni=object_pointer.data_collector.length;i<ni;i++)
				object_pointer.data_collector[i]=null;
		}
		
		var p;
		if(object_pointer.data_collector[my_material_id]==null){
			p={
				material_id	:	my_material_id,
				region_data	:	buffer_object_data,
				region_box	:	my_region_box,
				item_number	:	my_item_number
			};
			object_pointer.data_collector[my_material_id]=p;
		}else{
			p=object_pointer.data_collector[my_material_id];
			
			var old_region_data=p.region_data;
			p.region_data=new Float32Array(old_region_data.length+buffer_object_data.length);
			p.region_data.set(old_region_data,0);
			p.region_data.set(buffer_object_data,old_region_data.length);
			
			for(var i=0,ni=p.region_box[0].length;i<ni;i++){
				if(my_region_box[0][i]<p.region_box[0][i])
					p.region_box[0][i]=my_region_box[0][i];
				if(my_region_box[1][i]>p.region_box[1][i])
					p.region_box[1][i]=my_region_box[1][i];
			}
			p.item_number+=my_item_number;
		}
		var begin_material_id,end_material_id;
		if(object_pointer.loaded_number<=0){
			begin_material_id=0;
			end_material_id=object_pointer.data_collector.length-1;
		}else{
			if(max_buffer_object_data_length<=0)
				return;
			if(p.region_data.length<max_buffer_object_data_length)
				return;
			begin_material_id=my_material_id;
			end_material_id=my_material_id;
		}
		for(var i=begin_material_id;i<=end_material_id;i++){
			var processed_buffer_object_data,frame_processed_buffer_object_data;
			if((p=object_pointer.data_collector[i])==null)
				continue;
			object_pointer.data_collector[i]=null;
			try{
				processed_buffer_object_data=decode_function(request_str,
					part_information,part_material,part_property,p,this.parameter);
				if(request_str=="face")
					frame_processed_buffer_object_data=decode_function("frame",
						part_information,part_material,part_property,p,this.parameter);
			}catch(e){
				var str1="Execute decode_function() fail:"+e.toString();
				var str2="request_str:"+request_str
						+",request_file_id:"+request_file_id.toString()
						+",begin_material_id:"+begin_material_id.toString()
						+",end_material_id:"+end_material_id.toString();
				if(this.parameter.debug_mode_flag)
					{alert(str1);alert(str2);}
				else
					{console.log(str1);console.log(str2);}
				continue;
			}
			if(processed_buffer_object_data.region_data.length>0)
				this.save_data_into_buffer_object(object_pointer,processed_buffer_object_data,i);
			if(request_str=="face")
				if(frame_processed_buffer_object_data.region_data.length>0)
					this.save_data_into_buffer_object(frame_object_pointer,frame_processed_buffer_object_data,i);
		}
	};
	
	this.request_buffer_object_data=function(my_render)
	{
		if(this.request_render_part_id.length<=0)
			return this.parameter.max_loading_number;

		if(this.test_busy()<=0)
			return this.parameter.max_loading_number;
		
		var p								=this.request_render_part_id[0];
		var render_id						=p[0];
		var part_id							=p[1];
		var part_from_id					=p[2];
		var max_buffer_object_data_length	=p[3];
		var part_file_proxy_url				=p[4];
		var buffer_object_affiliated_data	=p[5];
		
		var request_str;
		var this_buffer_object=this.buffer_object[render_id][part_id];
		var object_pointer,frame_object_pointer=this_buffer_object.frame;
		
		if((object_pointer=this_buffer_object.face).file_number>0){
			request_str="face";
			part_file_proxy_url=part_file_proxy_url[0];
		}else if((object_pointer=this_buffer_object.edge).file_number>0){
			request_str="edge";
			part_file_proxy_url=part_file_proxy_url[1];
		}else if((object_pointer=this_buffer_object.point).file_number>0){
			request_str="point";
			part_file_proxy_url=part_file_proxy_url[2];
		}else{
			this.request_render_part_id.shift();
			return 0;
		}
		
		p=my_render.part_information[render_id][part_id];
		var part_information=p.information;
		var part_material	=p.material;
		var part_property	=p.property;
		
		p=my_render.render_program.render_program[render_id];
		var decode_function	=p.decode_function;

		var request_file_id	=(--(object_pointer.file_number));
		
		if(part_file_proxy_url[request_file_id].length<2){
			for(var i=0,ni=buffer_object_affiliated_data.length;i<ni;i++){
				if(buffer_object_affiliated_data[i].file_type!=request_str)
					continue;
				if(buffer_object_affiliated_data[i].file_id!=request_file_id)
					continue;
				var my_data=buffer_object_affiliated_data[i].file_data;
				buffer_object_affiliated_data[i]=buffer_object_affiliated_data[ni-1];
				buffer_object_affiliated_data.pop();
				
				object_pointer.loaded_number--;
				this.process_buffer_object_data(new Float32Array(my_data),request_str,
						object_pointer,frame_object_pointer,max_buffer_object_data_length,
						decode_function,part_information,part_material,part_property,request_file_id);
				return 0;
			}
			if(this.parameter.debug_mode_flag){
				alert("Loading Buffer Object Data part_file_proxy_url error");
			}else{
				console.log("Loading Buffer Object Data part_file_proxy_url error");
			}
			return 0;
		}
		
		var data_length	=part_file_proxy_url[request_file_id][0];
		var data_url	=part_file_proxy_url[request_file_id][1];
		
		this.loading_render_id	=render_id;
		this.loading_part_id	=part_id;
		
		try{
			var my_ajax=new XMLHttpRequest(),cur=this;			
			my_ajax.responseType="arraybuffer";
			my_ajax.onreadystatechange=function()
			{
				if(my_ajax.readyState!=4)
					return;
				cur.current_loading_mesh_number--;
				object_pointer.loaded_number--;
				
				if(my_ajax.status!=200){
					var str1="Loading Buffer Object Data response status error: "+my_ajax.status.toString();
					var str2="request_str:"+request_str+",request_file_id:"+request_file_id.toString();
					if(cur.parameter.debug_mode_flag)
						{alert(str1);alert(str2);alert(data_url);}
					else
						{console.log(str1);console.log(str2);console.log(data_url);}
					return;
				}
				
				cur.loaded_buffer_object_file_number++;
				cur.loaded_buffer_object_data_length+=data_length;
				
				var my_response_data;
				
				try{
					my_response_data=new Float32Array(my_ajax.response);
				}catch(e){
					if(cur.parameter.debug_mode_flag){
						alert("Parse buffer data error, "+e.toString());
						alert(data_url);
					}else{
						console.log("Parse buffer data error, "+e.toString());
						console.log(data_url);
					}
					return;
				}
				try{
					cur.process_buffer_object_data(my_response_data,request_str,
						object_pointer,frame_object_pointer,max_buffer_object_data_length,
						decode_function,part_information,part_material,part_property,request_file_id);
				}catch(e){
					if(cur.parameter.debug_mode_flag){
						alert("process_buffer_object_data error, "+e.toString());
						alert(data_url);
					}else{
						console.log("process_buffer_object_data error, "+e.toString());
						console.log(data_url);
					}
				}
				return;
			};
			my_ajax.open("GET",data_url,true);
			my_ajax.send(null);
			this.current_loading_mesh_number++;
		}catch(e){
			if(this.parameter.debug_mode_flag){
				alert("Loading Buffer Object Data ajax error: "+e.toString());
			}else{
				console.log("Loading Buffer Object Data ajax error: "+e.toString());
			}
		};
		return 1;
	};
	
	this.process_buffer_object_head=function(render_id,part_id,part_from_id,
		part_file_proxy_url,buffer_object_head_data,buffer_object_affiliated_data,my_render)
	{
		if(typeof(my_render.part_information[render_id])=="undefined")
			my_render.part_information[render_id]=new Array();
		if(typeof(my_render.part_information[render_id][part_id])=="undefined")
			my_render.part_information[render_id][part_id]=new Object();
		
		var p=my_render.part_information[render_id][part_id];
		var my_part_component_id_and_driver_id=my_render.part_component_id_and_driver_id[render_id][part_id];
		
		p.find_error_flag					=false;
		p.information						=buffer_object_head_data.information;
		p.material							=buffer_object_head_data.material;
		p.property							=buffer_object_head_data.property;
		p.part_component_id_and_driver_id	=my_part_component_id_and_driver_id;
		p.instance_initialize_data			=new Array();
		p.part_initialize_data				=my_render.part_initialize_data[render_id][part_id];
		
		for(var buffer_id=0,buffer_number=my_part_component_id_and_driver_id.length;buffer_id<buffer_number;buffer_id++){
			var my_component_id					 =my_part_component_id_and_driver_id[buffer_id][0];
			var my_driver_id					 =my_part_component_id_and_driver_id[buffer_id][1];
			p.instance_initialize_data[buffer_id]=my_render.instance_initialize_data[my_component_id][my_driver_id];
		}
		var max_buffer_object_data_length=buffer_object_head_data.data.max_buffer_object_data_length;
		this.request_render_part_id[this.request_render_part_id.length]
			=[	render_id,						part_id,				part_from_id,
				max_buffer_object_data_length,	part_file_proxy_url,	buffer_object_affiliated_data
			];
		this.create_empty_buffer_object(render_id,part_id,part_from_id,buffer_object_head_data.data);
		return;
	};
	this.process_buffer_head_package=function(package_length,package_data,head_data_array,my_render)
	{
		this.loaded_buffer_object_file_number++;
		this.loaded_buffer_object_data_length+=package_length;
		
		for(var i=0,ni=package_data.length;i<ni;i++){
			var render_id						=package_data[i][0];
			var part_id							=package_data[i][1];
			var part_from_id					=package_data[i][2];
			var part_package_sequence_id		=package_data[i][3];
			var part_file_proxy_url				=package_data[i][4];
			
			var buffer_object_head_data			=head_data_array[part_package_sequence_id].shift();
			var buffer_object_affiliated_data	=head_data_array[part_package_sequence_id];

			this.process_buffer_object_head(render_id,part_id,part_from_id,
				part_file_proxy_url,buffer_object_head_data,buffer_object_affiliated_data,my_render);
		}
	};
	
	this.request_buffer_head_package=function(
			package_proxy_url,package_length,package_flag,package_data,my_render)
	{
		try{
			var cur=this,my_ajax=new XMLHttpRequest();
			my_ajax.onreadystatechange=function()
			{
				if(my_ajax.readyState!=4)
					return;
				cur.current_loading_mesh_number--;
				if(my_ajax.status!=200){
					if(cur.parameter.debug_mode_flag){
						alert("this.request_buffer_head_package response status error: "+my_ajax.status.toString());
						alert("package_proxy_url: "+package_proxy_url);
						alert(package_proxy_url);
					}else{
						console.log("this.request_buffer_head_package response status error: "+my_ajax.status.toString());
						console.log("package_proxy_url: "+package_proxy_url);
						console.log(package_proxy_url);
					}
					return;
				}
				var head_data_array;
				try{
					head_data_array=JSON.parse(my_ajax.responseText);
				}catch(e){
					if(cur.parameter.debug_mode_flag){
						alert("this.request_buffer_head_package JSON.parse error: "+e.toString());
						alert("package_proxy_url: "+package_proxy_url);
						alert(my_ajax.responseText);
					}else{
						console.log("this.request_buffer_head_package JSON.parse error: "+e.toString());
						console.log("package_proxy_url: "+package_proxy_url);
						console.log(my_ajax.responseText);
					}
					return;
				}
				if(package_flag)
					head_data_array=[head_data_array];
				
				cur.process_buffer_head_package(package_length,package_data,head_data_array,my_render);
			}
			my_ajax.open("GET",package_proxy_url,true);
			my_ajax.send(null);
			
			this.current_loading_mesh_number++;
		}catch(e){
			if(this.parameter.debug_mode_flag){
				alert("this.request_buffer_head_package fail: "+e.toString());
				alert("package_proxy_url: "+package_proxy_url);
			}else{
				console.log("this.request_buffer_head_package fail: "+e.toString());
				console.log("package_proxy_url: "+package_proxy_url);
			}
		}
	}
	this.process_buffer_head_request_queue=function(my_render)
	{
		while(this.buffer_head_request_queue.length>0){
			if(this.test_busy()<=0)
				break;
			var p=this.buffer_head_request_queue.shift();
			var package_proxy_url	=p[0];
			var package_length		=p[1];
			var package_flag		=p[2];
			var package_data		=p[3];
			this.request_buffer_head_package(package_proxy_url,package_length,package_flag,package_data,my_render);
		}
	}
};