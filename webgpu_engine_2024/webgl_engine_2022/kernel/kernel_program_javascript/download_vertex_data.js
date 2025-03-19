function construct_download_vertex_data(my_webgpu,my_max_loading_number)
{
	this.webgpu							=my_webgpu;
	this.max_loading_number				=my_max_loading_number;
	
	this.request_render_part_id			=new Array();
	this.buffer_head_request_queue		=new Array();
	
	this.current_loading_mesh_number	=0;
	
	this.loading_render_id				=-1;
	this.loading_part_id				=-1;
	this.loaded_buffer_object_file_number=0;
	this.loaded_buffer_object_data_length=0;
	
	this.response_loaded_length			=0;
	
	this.acknowledge_render_part_id		=null; 
	
	this.save_data_into_buffer_object=function(my_material_id,object_pointer,buffer_object_data)
	{
		var my_item_size,my_item_number;
		
		if(typeof(my_item_size=buffer_object_data.item_size)!="number")
			my_item_size=-1;
		else
			my_item_size=Math.round(my_item_size);
		
		if(my_item_size<=0){
			if(typeof(my_item_number=buffer_object_data.item_number)!="number")
				return;
			if((my_item_number=Math.round(my_item_number))<=0)
				return;
			my_item_size=Math.round(buffer_object_data.region_data.length/my_item_number);
		}	
		my_item_number=Math.floor(buffer_object_data.region_data.length/my_item_size);
		
		object_pointer.region_data.push(
			{
				buffer		:	this.webgpu.device.createBuffer(
					{
						size	:	buffer_object_data.region_data.length*Float32Array.BYTES_PER_ELEMENT,
						usage	:	GPUBufferUsage.VERTEX|GPUBufferUsage.COPY_DST
					}),
				material_id	:	my_material_id,

				item_size	:	my_item_size,
				item_number	:	my_item_number,

				region_box	:	buffer_object_data.region_box,
				private_data:	buffer_object_data.private_data
			});
			
		this.webgpu.device.queue.writeBuffer(
				object_pointer.region_data[object_pointer.region_data.length-1].buffer,
				0,new Float32Array(buffer_object_data.region_data));

		for(var i=object_pointer.region_data.length-2;i>=0;i--){
			var p0=object_pointer.region_data[i+0];
			var p1=object_pointer.region_data[i+1];
			if(p0.material_id<=p1.material_id)
				break;
			object_pointer.region_data[i+0]=p1;
			object_pointer.region_data[i+1]=p0;
		};
	};
	
	this.process_buffer_object_data=function(render_id,part_id,request_str,
		response_data,object_pointer,max_buffer_object_data_length,request_file_id,scene)		
	{
		var p=object_pointer.server_region_data[request_file_id];
		var my_material_id	=p.material_id;
		var my_region_box	=p.region_box;
		var my_item_number	=p.item_number;
		
		while(my_material_id>=(object_pointer.data_collector.length))
			object_pointer.data_collector.push(null);
		
		if((p=object_pointer.data_collector[my_material_id])==null){
			p={
				material_id	:	my_material_id,
				region_data	:	response_data,
				region_box	:	my_region_box,
				item_number	:	my_item_number
			};
			object_pointer.data_collector[my_material_id]=p;
		}else{
			p.region_data	 = p.region_data.concat(response_data);
			p.region_box	 = scene.computer.combine_box(p.region_box,my_region_box);
			p.item_number	+= my_item_number;
		}
		p.item_size=Math.round(p.region_data.length/p.item_number);
		
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
		};
		
		var part_object=scene.part_array[render_id][part_id];
		var part_driver=scene.part_driver[render_id][part_id];

		for(var i=begin_material_id;i<=end_material_id;i++){
			var processed_buffer_object_data=object_pointer.data_collector[i];
			object_pointer.data_collector[i]=null;
			if(processed_buffer_object_data==null)
				continue;
			if(processed_buffer_object_data.length<=0)
				continue;
			if(typeof(part_driver)=="object")
				if(part_driver!=null)
					if(typeof(part_driver.decode_vertex_data)=="function")
						processed_buffer_object_data=part_driver.decode_vertex_data(
							request_str,processed_buffer_object_data,part_object);
			if(processed_buffer_object_data.region_data.length>0)
				this.save_data_into_buffer_object(i,object_pointer,processed_buffer_object_data);
		};
	};
	
	this.fetch_buffer_object_data=async function(
			render_id,part_id,request_str,data_url,data_length,
			object_pointer,max_buffer_object_data_length,request_file_id,scene)
	{
		if(scene.terminate_flag)
			return;
		
		this.current_loading_mesh_number++;
		var buffer_data_promise=await fetch(data_url,scene.fetch_parameter.load_part_data);
		this.current_loading_mesh_number--;
		
		if(scene.terminate_flag)
			return;
		if(!(buffer_data_promise.ok)){
			alert("request request_buffer_object data fail: "+data_url);
			return;
		}
		var response_data;
		try{
			response_data = await buffer_data_promise.json();
		}catch(e){
			if(scene.terminate_flag)
				return;
			alert("parse request_buffer_object data fail: "+e.toString());
			alert(data_url);
			return;
		}
		if(scene.terminate_flag)
			return;
		
		this.loaded_buffer_object_file_number++;
		this.loaded_buffer_object_data_length+=data_length;
		
		object_pointer.loaded_number--;
		this.process_buffer_object_data(render_id,part_id,request_str,response_data,
			object_pointer,max_buffer_object_data_length,request_file_id,scene);
		return;
	};
	
	this.request_buffer_object_data=function(scene)
	{
		if(scene.terminate_flag)
			return this.max_loading_number;
		if(this.test_busy()<=0)
			return this.max_loading_number;
		if(this.request_render_part_id.length<=0)
			return this.max_loading_number;

		var p								=this.request_render_part_id[0];
		var render_id						=p[0];
		var part_id							=p[1];
		var max_buffer_object_data_length	=p[2];
		var part_file_proxy_url				=p[3];
		var part_affiliated_data			=p[4];
		
		p=scene.part_array[render_id][part_id].buffer_object;
		
		var request_str,object_pointer;
		
		if((object_pointer=p.face).file_number>0){
			request_str="face";
			part_file_proxy_url=part_file_proxy_url[0];
		}else if((object_pointer=p.edge).file_number>0){
			request_str="edge";
			part_file_proxy_url=part_file_proxy_url[1];
		}else if((object_pointer=p.point).file_number>0){
			request_str="point";
			part_file_proxy_url=part_file_proxy_url[2];
		}else{
			this.request_render_part_id.shift();
			return 0;
		}
		
		object_pointer.file_number--;
		
		if(part_file_proxy_url[object_pointer.file_number].length<2){
			for(var i=0,ni=part_affiliated_data.length;i<ni;i++){
				if(part_affiliated_data[i].file_type!=request_str)
					continue;
				if(part_affiliated_data[i].file_id!=object_pointer.file_number)
					continue;
				var my_data=part_affiliated_data[i].file_data;
				
				part_affiliated_data[i]=part_affiliated_data[ni-1];
				part_affiliated_data.pop();
				
				object_pointer.loaded_number--;
				this.process_buffer_object_data(render_id,part_id,request_str,my_data,
						object_pointer,max_buffer_object_data_length,
						object_pointer.file_number,scene);
				return 0;
			}
			alert("Loading Buffer Object Data part_file_proxy_url error");
			return 0;
		}
		
		this.loading_render_id	=render_id;
		this.loading_part_id	=part_id;
		
		var data_length	=part_file_proxy_url[object_pointer.file_number][0];
		var data_url	=part_file_proxy_url[object_pointer.file_number][1];
		
		this.fetch_buffer_object_data(
			render_id,part_id,request_str,data_url,data_length,
			object_pointer,max_buffer_object_data_length,
			object_pointer.file_number,scene);
		return 1;
	};
	
	this.create_part_array_and_vertex_data_request=function(
			render_id,part_id,part_file_proxy_url,part_head_data,part_affiliated_data,
			part_init_data,component_init_data,scene)
	{
		if(this.acknowledge_render_part_id==null)
			this.acknowledge_render_part_id="";
		this.acknowledge_render_part_id+=render_id+"_"+part_id+"_";
		
		scene.part_array[render_id][part_id]={
			find_error_flag					:	false,
			
			render_id						:	render_id,
			part_id							:	part_id,
			
			information						:	part_head_data.information,
			material						:	part_head_data.material,
			property						:	part_head_data.property,
			
			part_component_id_and_driver_id	:	scene.part_component_id_and_driver_id[render_id][part_id],

			component_render_parameter		:	new Array(),
			component_driver_array			:	new Array(),

			buffer_object					:
			{
				face:{
					server_region_data	:	part_head_data.data.face.region_data,
		    		file_number			:	part_head_data.data.face.region_data.length,
		    		loaded_number		:	part_head_data.data.face.region_data.length,
		    		region_data			:	new Array(),
		    		data_collector		:	new Array(),
		    		error_flag			:	false
				},
				edge:{
					server_region_data	:	part_head_data.data.edge.region_data,
		    		file_number			:	part_head_data.data.edge.region_data.length,
		    		loaded_number		:	part_head_data.data.edge.region_data.length,
		    		region_data			:	new Array(),
		    		data_collector		:	new Array(),
		    		error_flag			:	false
				},
				point:{
					server_region_data	:	part_head_data.data.point.region_data,
		    		file_number			:	part_head_data.data.point.region_data.length,
		    		loaded_number		:	part_head_data.data.point.region_data.length,
		    		region_data			:	new Array(),
		    		data_collector		:	new Array(),
		    		error_flag			:	false
				}
			},
			destroy						:	function()
			{
				var data_array=[
						this.buffer_object.face,
						this.buffer_object.edge,
						this.buffer_object.point
				];
				for(var i=0,ni=data_array.length;i<ni;i++)
					for(var p,j=0,nj=data_array[i].region_data.length;j<nj;j++)
						if(typeof(p=data_array[i].region_data[j])=="object")
							if(p!=null)
								if(p.buffer!=null){
									p.buffer.destroy();
									p.buffer=null;
								}
			}
		}

		var render_driver=scene.render_driver[render_id];
		var part_object=scene.part_array[render_id][part_id];
		var part_driver=new render_driver.new_part_driver(
				part_init_data[render_id][part_id],part_object,render_driver,scene);
		scene.part_driver[render_id][part_id]=part_driver;

		for(var i=0,ni=part_object.part_component_id_and_driver_id.length;i<ni;i++){
			var my_component_id	=part_object.part_component_id_and_driver_id[i][0];
			var my_driver_id	=part_object.part_component_id_and_driver_id[i][1];

			part_object.component_driver_array[i]=new part_driver.new_component_driver(
				my_component_id,my_driver_id,render_id,part_id,i,
				component_init_data[my_component_id][my_driver_id],
				part_object,part_driver,render_driver,scene);
		}
		
		this.request_render_part_id.push([render_id,part_id,
				part_head_data.data.max_buffer_object_data_length,
				part_file_proxy_url,part_affiliated_data]);
		return;
	};

	this.request_part_package=async function(package_proxy_url,package_length,
			package_data_head,part_init_data,component_init_data,scene)
	{
		if(scene.terminate_flag)
			return;
		this.current_loading_mesh_number++;
		var head_promise=await fetch(package_proxy_url,
					scene.fetch_parameter.load_part_package);
		this.current_loading_mesh_number--;
		if(scene.terminate_flag)
			return;
		
		if(!(head_promise.ok)){
			alert("request request_buffer_head_package fail: "+initialization_url);
			return;
		}
		var package_data_array;
		try{
			package_data_array = await head_promise.json();
		}catch(e){
			if(scene.terminate_flag)
				return;
			alert("parse request_buffer_head_package data fail: "+e.toString());
			alert(package_proxy_url);
			return;
		}
		if(scene.terminate_flag)
			return;

		this.loaded_buffer_object_file_number++;
		this.loaded_buffer_object_data_length+=package_length;
		
		for(var i=0,ni=package_data_head.length;(i<ni)&&(!(scene.terminate_flag));i++){
			var render_id					=package_data_head[i][0];
			var part_id						=package_data_head[i][1];
			var part_package_sequence_id	=package_data_head[i][2];
			var part_file_proxy_url			=package_data_head[i][3];
			
			if((part_package_sequence_id<0)||(part_package_sequence_id>=package_data_array.length)){
				console.log("package_proxy_url: "			+package_proxy_url);
				console.log("render_id: "					+render_id.toString()+
							",part_id: "					+part_id.toString());
				console.log("part_package_sequence_id: "	+part_package_sequence_id+
							",package_data_array.length:"	+package_data_array.length);
				console.log();
			}else{
				var part_head_data		=package_data_array[part_package_sequence_id].shift();
				var part_affiliated_data=package_data_array[part_package_sequence_id];
		
				this.create_part_array_and_vertex_data_request(render_id,part_id,
					part_file_proxy_url,part_head_data,part_affiliated_data,
					part_init_data,component_init_data,scene);
			}
		}
	};
	
	this.process_buffer_head_request_queue=function(part_init_data,component_init_data,scene)
	{
		do{
			if(scene.terminate_flag)
				break;
				
			for(var i=this.current_loading_mesh_number,ni=this.max_loading_number;i<ni;)
				i+=this.request_buffer_object_data(scene);
			
			if(this.test_busy()<=0)
				break;
			if(this.buffer_head_request_queue.length<=0)
				break;
			var p=this.buffer_head_request_queue.shift();
			var package_proxy_url	=p[0];
			var package_length		=p[1];
			var package_data_head	=p[2];

			this.request_part_package(package_proxy_url,package_length,
					package_data_head,part_init_data,component_init_data,scene);
		}while(true);
	};
	
	this.test_busy=function()
	{
		return this.max_loading_number-this.current_loading_mesh_number;
	};
}
