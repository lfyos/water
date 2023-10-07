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
	
	this.destroy=function()
	{
		this.webgpu										=null;
		this.request_render_part_id						=null;
		this.buffer_head_request_queue					=null;
		this.acknowledge_render_part_id					=null;
	
		this.test_busy									=null
		this.request_part_package						=null;
		this.fetch_buffer_object_data					=null;
		this.process_buffer_object_data					=null
		this.request_buffer_object_data					=null;
		this.save_data_into_buffer_object				=null;
		this.process_buffer_head_request_queue			=null;
		this.create_part_array_and_vertex_data_request	=null;
	};
	
	this.save_data_into_buffer_object=function(my_material_id,object_pointer,buffer_object_data)
	{
		if(typeof(buffer_object_data.item_size)=="number"){
			buffer_object_data.item_size	=Math.round(buffer_object_data.item_size);
			buffer_object_data.item_number	=Math.floor(buffer_object_data.region_data.length/buffer_object_data.item_size);
		}else if(typeof(buffer_object_data.item_number)=="number"){
			buffer_object_data.item_number	=Math.floor(buffer_object_data.item_number);
			buffer_object_data.item_size	=Math.floor(buffer_object_data.region_data.length/buffer_object_data.item_number);
		}else
			return;

		object_pointer.region_data.push(
			{
				buffer		:	this.webgpu.device.createBuffer(
					{
						size	:	buffer_object_data.region_data.length*Float32Array.BYTES_PER_ELEMENT,
						usage	:	GPUBufferUsage.VERTEX|GPUBufferUsage.COPY_DST
					}),
				material_id	:	my_material_id,

				item_size	:	buffer_object_data.item_size,
				item_number	:	buffer_object_data.item_number,

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
	
	this.process_buffer_object_data=function(render_id,part_id,request_str,response_data,
			object_pointer,frame_object_pointer,max_buffer_object_data_length,request_file_id,render)		
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
			p.region_box	 = render.computer.combine_box(p.region_box,my_region_box);
			p.item_number	+= my_item_number;
		}
		p.item_size=p.region_data.length/p.item_number;
		
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
		
		var part_object=render.part_array[render_id][part_id];
		var part_driver=render.part_driver[render_id][part_id];

		for(var i=begin_material_id;i<=end_material_id;i++){
			var processed_buffer_object_data		=object_pointer.data_collector[i];
			var frame_processed_buffer_object_data	=object_pointer.data_collector[i];
			object_pointer.data_collector[i]=null;
			if(processed_buffer_object_data==null)
				continue;
			if(processed_buffer_object_data.length<=0)
				continue;

			if(typeof(part_driver)=="object")
				if(part_driver!=null)
					if(typeof(part_driver.decode_vertex_data)=="function"){
						processed_buffer_object_data=part_driver.decode_vertex_data(
							request_str,processed_buffer_object_data,part_object);
						if(request_str=="face")
							frame_processed_buffer_object_data=part_driver.decode_vertex_data(
								"frame",frame_processed_buffer_object_data,part_object);
					}
			if(processed_buffer_object_data.region_data.length>0)
				this.save_data_into_buffer_object(i,
					object_pointer,processed_buffer_object_data);
			if(request_str=="face")
				if(frame_processed_buffer_object_data.region_data.length>0)
					this.save_data_into_buffer_object(i,
						frame_object_pointer,frame_processed_buffer_object_data);
		};
	};
	
	this.fetch_buffer_object_data=async function(render_id,part_id,request_str,data_url,data_length,
			object_pointer,frame_object_pointer,max_buffer_object_data_length,request_file_id,render)
	{
		if(render.terminate_flag)
			return;
		
		this.current_loading_mesh_number++;
		var buffer_data_promise=await fetch(data_url);
		this.current_loading_mesh_number--;
		
		if(render.terminate_flag)
			return;
		if(!(buffer_data_promise.ok)){
			alert("request request_buffer_object data fail: "+data_url);
			return;
		}
		var response_data;
		try{
			response_data = await buffer_data_promise.json();
		}catch(e){
			if(render.terminate_flag)
				return;
			alert("parse request_buffer_object data fail: "+e.toString());
			alert(data_url);
			return;
		}
		if(render.terminate_flag)
			return;
		
		this.loaded_buffer_object_file_number++;
		this.loaded_buffer_object_data_length+=data_length;
		
		object_pointer.loaded_number--;
		this.process_buffer_object_data(render_id,part_id,request_str,response_data,
			object_pointer,frame_object_pointer,max_buffer_object_data_length,request_file_id,render);
		return;
	};
	
	this.request_buffer_object_data=function(render)
	{
		if(render.terminate_flag)
			return this.max_loading_number;
		if(this.request_render_part_id.length<=0)
			return this.max_loading_number;
		if(this.test_busy()<=0)
			return this.max_loading_number;

		var p								=this.request_render_part_id[0];
		var render_id						=p[0];
		var part_id							=p[1];
		var max_buffer_object_data_length	=p[2];
		var part_file_proxy_url				=p[3];
		var part_affiliated_data			=p[4];
		
		p=render.part_array[render_id][part_id].buffer_object;
		
		var request_str;
		var object_pointer,frame_object_pointer=p.frame;
		
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
						object_pointer,frame_object_pointer,max_buffer_object_data_length,
						object_pointer.file_number,render);
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
			object_pointer,frame_object_pointer,max_buffer_object_data_length,
			object_pointer.file_number,render);
		return 1;
	};
	
	this.create_part_array_and_vertex_data_request=function(
			render_id,part_id,part_file_proxy_url,part_head_data,part_affiliated_data,
			part_init_data,component_init_data,render)
	{
		if(this.acknowledge_render_part_id==null)
			this.acknowledge_render_part_id="";
		this.acknowledge_render_part_id+=render_id+"_"+part_id+"_";
		
		render.part_array[render_id][part_id]={
			find_error_flag					:	false,
			
			render_id						:	render_id,
			part_id							:	part_id,
			
			information						:	part_head_data.information,
			material						:	part_head_data.material,
			property						:	part_head_data.property,
			
			part_component_id_and_driver_id	:	render.part_component_id_and_driver_id[render_id][part_id],

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
				frame:{
					server_region_data	:	null,
		    		file_number			:	0,
		    		loaded_number		:	0,
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
			destroy						:	function(render)
			{
				var p;
				for(var i=0,ni=this.component_driver_array.length;i<ni;i++){
					if(typeof(p=this.component_driver_array[i])=="object")
						if(p!=null)
							if(typeof(p.destroy)=="function")
								p.destroy(render);
					this.component_driver_array[i]=null;
				}
				this.component_driver_array=null;
				
				var data_array=[
					this.buffer_object.face,	this.buffer_object.frame,
					this.buffer_object.edge,	this.buffer_object.point
				];
				
				for(var i=0,ni=data_array.length;i<ni;i++){
					for(var j=0,nj=data_array[i].region_data.length;j<nj;j++){
						if(typeof(p=data_array[i].region_data[j])=="object")
							if(p!=null){
								if(typeof(p.private_data)=="object")
									if(p.private_data!=null)
										if(typeof(p.private_data.destroy)=="function")
											p.private_data.destroy(render);
								p.private_data=null;
								
								p.buffer.destroy();
								p.buffer=null;
						
								p.region_box=null;
							}
						data_array[i].region_data[j]=null;
					}
					data_array[i].region_data=null;
					
					for(var j=0,nj=data_array[i].data_collector.length;j<nj;j++){
						if(typeof(data_array[i].data_collector[j])=="object")
							if(data_array[i].data_collector[j]!=null){
								data_array[i].data_collector[j].region_data=null;
								data_array[i].data_collector[j].region_box=null;
							}
						data_array[i].data_collector[j]=null;
					}
					data_array[i].data_collector=null;
					data_array[i].server_region_data=null;
				}
				
				this.buffer_object.face=null;
				this.buffer_object.frame=null;
				this.buffer_object.edge=null;
				this.buffer_object.point=null;
				this.buffer_object=null;
				
				this.information					=null;
				this.material						=null;
				this.property						=null;
				this.part_component_id_and_driver_id=null;
				this.component_render_parameter		=null;
			}
		}

		var render_driver=render.render_driver[render_id];
		var part_object=render.part_array[render_id][part_id];
		var part_driver=new render_driver.new_part_driver(
				part_init_data[render_id][part_id],part_object,render_driver,render);
		render.part_driver[render_id][part_id]=part_driver;

		for(var i=0,ni=part_object.part_component_id_and_driver_id.length;i<ni;i++){
			var my_component_id	=part_object.part_component_id_and_driver_id[i][0];
			var my_driver_id	=part_object.part_component_id_and_driver_id[i][1];

			part_object.component_driver_array[i]=new part_driver.new_component_driver(
				my_component_id,my_driver_id,render_id,part_id,i,
				component_init_data[my_component_id][my_driver_id],
				part_object,part_driver,render_driver,render);
		}
		
		this.request_render_part_id.push([render_id,part_id,
				part_head_data.data.max_buffer_object_data_length,
				part_file_proxy_url,part_affiliated_data]);
		return;
	};

	this.request_part_package=async function(package_proxy_url,package_length,
			package_data_head,part_init_data,component_init_data,render)
	{
		if(render.terminate_flag)
			return;
		this.current_loading_mesh_number++;
		var head_promise=await fetch(package_proxy_url);
		this.current_loading_mesh_number--;
		if(render.terminate_flag)
			return;
		
		if(!(head_promise.ok)){
			alert("request request_buffer_head_package fail: "+initialization_url);
			return;
		}
		var package_data_array;
		try{
			package_data_array = await head_promise.json();
		}catch(e){
			if(render.terminate_flag)
				return;
			alert("parse request_buffer_head_package data fail: "+e.toString());
			alert(package_proxy_url);
			return;
		}
		if(render.terminate_flag)
			return;

		this.loaded_buffer_object_file_number++;
		this.loaded_buffer_object_data_length+=package_length;
		
		for(var i=0,ni=package_data_head.length;(i<ni)&&(!(render.terminate_flag));i++){
			var render_id					=package_data_head[i][0];
			var part_id						=package_data_head[i][1];
			var part_package_sequence_id	=package_data_head[i][2];
			var part_file_proxy_url			=package_data_head[i][3];
			
			var part_head_data		=package_data_array[part_package_sequence_id].shift();
			var part_affiliated_data=package_data_array[part_package_sequence_id];

			this.create_part_array_and_vertex_data_request(render_id,part_id,
					part_file_proxy_url,part_head_data,part_affiliated_data,
					part_init_data,component_init_data,render);
		}
	};
	
	this.process_buffer_head_request_queue=function(part_init_data,component_init_data,render)
	{
		while(true){
			if(render.terminate_flag)
				return;
			if(this.buffer_head_request_queue.length<=0)
				break;
			if(this.test_busy()<=0)
				break;
			var p=this.buffer_head_request_queue.shift();
			var package_proxy_url	=p[0];
			var package_length		=p[1];
			var package_data_head	=p[2];

			this.request_part_package(package_proxy_url,package_length,
					package_data_head,part_init_data,component_init_data,render);
		};
	};
	
	this.test_busy=function()
	{
		return this.max_loading_number-this.current_loading_mesh_number;
	};
}
