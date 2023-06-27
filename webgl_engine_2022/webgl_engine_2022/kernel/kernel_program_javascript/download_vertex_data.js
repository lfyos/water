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
	
	this.destroy=function(render)
	{
		for(var render_id=0,render_number=render.part_array.length;render_id<render_number;render_id++){
			for(var part_id=0,part_number=render.part_array[render_id].length;part_id<part_number;part_id++){
				var driver_object=render.part_driver[render_id][part_id];
				var p=render.part_array[render_id][part_id];
				if(typeof(driver_object)=="object")
					if(driver_object!=null)
						if(typeof(driver_object.destroy)=="function")
							try{
								driver_object.destroy(this.webgpu,p);
							}catch(e){
								;
							}
				render.part_driver[render_id][part_id]	=null;							
				render.part_array[render_id][part_id]	=null;

				p.information						=null;
				p.material							=null;
				p.property							=null;
				p.part_component_id_and_driver_id	=null;
				p.render_initialize_data			=null;
				p.part_initialize_data				=null;
				p.component_initialize_data			=null;
				
				var buffer_object_array=[
						p.buffer_object.face,
						p.buffer_object.frame,
						p.buffer_object.edge,
						p.buffer_object.point
				];
				p.buffer_object.face	=null
				p.buffer_object.frame	=null;
				p.buffer_object.edge	=null;
				p.buffer_object.point	=null;
				p.buffer_object			=null;
				
				for(var i=0,ni=buffer_object_array.length;i<ni;i++){
					p=buffer_object_array[i];
					p.server_region_data	=null;
		    		p.data_collector		=null;
		    		for(var j=0,nj=p.region_data.length;j<nj;j++){
						p.region_data[j].buffer.destroy();
						p.region_data[j].buffer			=null;
						p.region_data[j].region_box		=null;
						p.region_data[j].private_data	=null;					
					}
				}
			}
		}
		
		render.part_array								=null;
		
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

		this.destroy									=null;
	};
	this.save_data_into_buffer_object=function(object_pointer,buffer_object_data)
	{
		var my_region_data={
				buffer		:	
					this.webgpu.device.createBuffer({
							size	:	buffer_object_data.region_data.length*4,
							usage	:	GPUBufferUsage.VERTEX|GPUBufferUsage.COPY_DST
					}),
				material_id	:	buffer_object_data.material_id,
				item_size	:	buffer_object_data.item_size,
				item_number	:	buffer_object_data.region_data.length/buffer_object_data.item_size,
				region_box	:	buffer_object_data.region_box,
				private_data:	buffer_object_data.private_data
		};
		
		this.webgpu.device.queue.writeBuffer(my_region_data.buffer,0,
				new Float32Array(buffer_object_data.region_data));
		
		object_pointer.region_data.push(my_region_data);
		
		for(var i=object_pointer.region_data.length-2;i>=0;i--){
			var p0=object_pointer.region_data[i+0];
			var p1=object_pointer.region_data[i+1];
			if(p0.material_id<=p1.material_id)
				break;
			object_pointer.region_data[i+0]=p1;
			object_pointer.region_data[i+1]=p0;
		}
	};
	
	this.test_busy=function()
	{
		return this.max_loading_number-this.current_loading_mesh_number;
	};
	this.process_buffer_object_data=function(render_id,part_id,request_str,response_data,
				object_pointer,frame_object_pointer,max_buffer_object_data_length,request_file_id,render)		
	{
		var p=object_pointer.server_region_data[request_file_id];
		var my_material_id	=p.material_id;
		var my_region_box	=p.region_box;
		var my_item_number	=p.item_number;
		
		if(my_material_id>=(object_pointer.data_collector.length)){
			var bak=object_pointer.data_collector;
			object_pointer.data_collector=new Array(my_material_id+1);
			for(var i=0,ni=bak.length;i<ni;i++)
				object_pointer.data_collector[i]=bak[i];
			for(var i=bak.length,ni=object_pointer.data_collector.length;i<ni;i++)
				object_pointer.data_collector[i]=null;
		}
		if((p=object_pointer.data_collector[my_material_id])==null){
			object_pointer.data_collector[my_material_id]={
				material_id	:	my_material_id,
				region_data	:	response_data,
				region_box	:	my_region_box,
				item_number	:	my_item_number
			};
			p=object_pointer.data_collector[my_material_id];
		}else{
			p.region_data=p.region_data.concat(response_data);
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
		
		var part_driver_object=render.part_driver[render_id][part_id];

		for(var i=begin_material_id;i<=end_material_id;i++){
			var vertex_data=object_pointer.data_collector[i];
			object_pointer.data_collector[i]=null;
			if(vertex_data==null)
				continue;
			if(vertex_data.length<=0)
				continue;
			
			var processed_buffer_object_data=vertex_data;
			var frame_processed_buffer_object_data=vertex_data;
				
			if(typeof(part_driver_object)=="object")	
				if(typeof(part_driver_object.decode_vertex_data)=="function")
					try{
						processed_buffer_object_data=part_driver_object.decode_vertex_data(
							request_str,vertex_data,render.part_array[render_id][part_id]);
						if(request_str=="face")
							frame_processed_buffer_object_data=part_driver_object.decode_vertex_data(
								"frame",vertex_data,render.part_array[render_id][part_id]);
					}catch(e){
						alert("Execute decode_function() fail:"+e.toString());
						alert("request_str:"+request_str
								+",request_file_id:"+request_file_id.toString()
								+",begin_material_id:"+begin_material_id.toString()
								+",end_material_id:"+end_material_id.toString());
						continue;
					}
			if(processed_buffer_object_data.region_data.length>0){
				processed_buffer_object_data.material_id=i;
				this.save_data_into_buffer_object(object_pointer,processed_buffer_object_data);
			}
			if(request_str=="face")
				if(frame_processed_buffer_object_data.region_data.length>0){
					frame_processed_buffer_object_data.material_id=i;
					this.save_data_into_buffer_object(frame_object_pointer,frame_processed_buffer_object_data);
				}
		}
	};
	
	this.fetch_buffer_object_data=async function(render_id,part_id,request_str,data_url,data_length,
			object_pointer,frame_object_pointer,max_buffer_object_data_length,request_file_id,render)
	{
		this.current_loading_mesh_number++;
		
		var buffer_data_promise=await fetch(data_url);
		if(!(buffer_data_promise.ok)){
			this.current_loading_mesh_number--;
			if(render.terminate_flag)
				return;
			alert("request request_buffer_object data fail: "+data_url);
			return;
		}
		var response_data;
		try{
			response_data = await buffer_data_promise.json();
		}catch(e){
			this.current_loading_mesh_number--;
			if(render.terminate_flag)
				return;
			alert("parse request_buffer_object data fail: "+e.toString());
			alert(data_url);
			return;
		}
		this.current_loading_mesh_number--;
		
		this.loaded_buffer_object_file_number++;
		this.loaded_buffer_object_data_length+=data_length;
		
		object_pointer.loaded_number--;
		
		if(render.terminate_flag)
			return;
			
		this.process_buffer_object_data(render_id,part_id,request_str,response_data,
				object_pointer,frame_object_pointer,max_buffer_object_data_length,
				request_file_id,render);
		return;
	}
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

		var request_file_id	=(--(object_pointer.file_number));
		
		if(part_file_proxy_url[request_file_id].length<2){
			for(var i=0,ni=part_affiliated_data.length;i<ni;i++){
				if(part_affiliated_data[i].file_type!=request_str)
					continue;
				if(part_affiliated_data[i].file_id!=request_file_id)
					continue;
				var my_data=part_affiliated_data[i].file_data;
				
				part_affiliated_data[i]=part_affiliated_data[ni-1];
				part_affiliated_data.pop();
				
				object_pointer.loaded_number--;
				this.process_buffer_object_data(render_id,part_id,request_str,my_data,
						object_pointer,frame_object_pointer,max_buffer_object_data_length,
						request_file_id,render);
				return 0;
			}
			alert("Loading Buffer Object Data part_file_proxy_url error");
			return 0;
		}
		
		this.loading_render_id	=render_id;
		this.loading_part_id	=part_id;
		
		var data_length	=part_file_proxy_url[request_file_id][0];
		var data_url	=part_file_proxy_url[request_file_id][1];
		
		this.fetch_buffer_object_data(render_id,part_id,request_str,data_url,data_length,
				object_pointer,frame_object_pointer,max_buffer_object_data_length,
				request_file_id,render);
		return 1;
	};
	
	this.create_part_array_and_vertex_data_request=function(
			render_id,part_id,part_file_proxy_url,part_head_data,part_affiliated_data,render)
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
			item_ids						:	part_head_data.item_ids,
			
			part_component_id_and_driver_id	:	render.part_component_id_and_driver_id[render_id][part_id],
			
			render_shader_data				:	render.render_shader_data[render_id],
			render_initialize_data			:	render.render_initialize_data[render_id],
			part_initialize_data			:	render.part_initialize_data[render_id][part_id],
			component_initialize_data		:	new Array(),
			
			component_render_parameter		:	new Array(),
			component_buffer_parameter		:	new Array(),

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
			}
		}
		var my_part=render.part_array[render_id][part_id];
		for(var i=0,ni=my_part.part_component_id_and_driver_id.length;i<ni;i++){
			var my_component_id					=my_part.part_component_id_and_driver_id[i][0];
			var my_driver_id					=my_part.part_component_id_and_driver_id[i][1];
			my_part.component_initialize_data[i]=render.component_initialize_data[my_component_id][my_driver_id];
		}
		
		render.part_driver[render_id][part_id]=null;
		if(typeof(render.render_driver[render_id])=="object")
			if(render.render_driver[render_id]!=null)
				if(typeof(render.render_driver[render_id].create_part_driver)=="function")
					try{
						render.part_driver[render_id][part_id]=render.render_driver[render_id].
									create_part_driver(my_part,render.render_driver[render_id],render);
					}catch(e){
						render.part_driver[render_id][part_id]=null;
						alert("Create Part Driver Object fail:	"+e.toString());
						alert("part user name:			"+my_part.information.user_name);
						alert("part mesh_file:			"+my_part.information.mesh_file);
					}	
		this.request_render_part_id.push([render_id,part_id,
				part_head_data.data.max_buffer_object_data_length,
				part_file_proxy_url,part_affiliated_data]);
		return;
	};
	
	this.request_part_package=async function(package_proxy_url,package_length,package_data_head,render)
	{
		this.current_loading_mesh_number++;

		var head_promise=await fetch(package_proxy_url);
		if(!(head_promise.ok)){
			this.current_loading_mesh_number--;
			if(render.terminate_flag)
				return;
			alert("request request_buffer_head_package fail: "+initialization_url);
			return;
		}
		var package_data_array;
		try{
			package_data_array = await head_promise.json();
		}catch(e){
			this.current_loading_mesh_number--;
			if(render.terminate_flag)
				return;
			alert("parse request_buffer_head_package data fail: "+e.toString());
			alert(package_proxy_url);
			return;
		}
		this.current_loading_mesh_number--;
	
		if(render.terminate_flag)
			return;

		this.loaded_buffer_object_file_number++;
		this.loaded_buffer_object_data_length+=package_length;
		
		for(var i=0,ni=package_data_head.length;i<ni;i++){
			var render_id					=package_data_head[i][0];
			var part_id						=package_data_head[i][1];
			var part_package_sequence_id	=package_data_head[i][2];
			var part_file_proxy_url			=package_data_head[i][3];
			
			var part_head_data				=package_data_array[part_package_sequence_id].shift();
			var part_affiliated_data		=package_data_array[part_package_sequence_id];

			this.create_part_array_and_vertex_data_request(render_id,part_id,
					part_file_proxy_url,part_head_data,part_affiliated_data,render);
		}
	}
	this.process_buffer_head_request_queue=function(render)
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

			this.request_part_package(package_proxy_url,package_length,package_data_head,render);
		}
	}
}
