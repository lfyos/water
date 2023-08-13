async function request_create_engine(create_engine_sleep_time_length_scale,
		create_engine_sleep_time_length,create_engine_max_sleep_time_length,
		my_webgpu,request_url,my_url,my_user_name,my_pass_word,my_language_name,process_bar_id)
{
	while(true){
		var create_data;
		var engine_promise=await fetch(request_url+"&process_bar="+process_bar_id);
		if(!(engine_promise.ok)){
			alert("request_create_engine fail:"+engine_promise.status);
			return null;
		}
		try{
			create_data = await engine_promise.json();
		}catch(e){
			alert("Web server error, or Create Too many scenes:  "+e.toString());
			return null;
		}
		if(typeof(create_data)=="object"){
			if(create_data==null){
				alert("Web server error, response_data==null!");
				return null;
			}

			var my_channel_id					=create_data[0]
		    var	my_render_initialize_data		=create_data[1];
			var	my_part_initialize_data			=create_data[2];
			var	my_component_initialize_data	=create_data[3];
			var	my_render_data					=create_data[4];
			
			var render=new construct_render_routine(my_webgpu,my_url,
					my_user_name,my_pass_word,my_language_name,my_channel_id,my_render_data);

		    var render_init_data=new Array();
		    for(var i=0,ni=my_render_initialize_data.length-1;i<ni;i++){
				var my_data		=my_render_initialize_data[i++];
				var render_id	=my_render_initialize_data[i++];
				render_init_data[render_id]=my_data;
			};

			var part_init_data=new Array(render.part_driver.length);
			for(var i=0,ni=part_init_data.length;i<ni;i++)
				part_init_data[i]=new Array();
			for(var i=0,ni=my_part_initialize_data.length-1;i<ni;){
				var my_data		=my_part_initialize_data[i++];
				var render_id	=my_part_initialize_data[i++];
				var part_id		=my_part_initialize_data[i++];
				part_init_data[render_id][part_id]=my_data;
			};
			
			var component_init_data=new Array(render.component_location_data.component_number);
			for(var i=0,ni=component_init_data.length;i<ni;i++)
				component_init_data[i]=new Array();
			for(var i=0,ni=my_component_initialize_data.length-1;i<ni;){
				var my_data				=my_component_initialize_data[i++];
				var my_component_id		=my_component_initialize_data[i++];
				var my_driver_id		=my_component_initialize_data[i++];
				component_init_data[my_component_id][my_driver_id]=my_data;
			}
			
			var init_data,initialization_url=create_data.pop();
			try{
				init_data=await import(initialization_url);
			}catch(e){
				console.log("download initialization program fail:	"+e.toString());
				console.log(initialization_url);
			}
			
			init_data=init_data.init_data;

			var sorted_component_name_id		=init_data[0];
			var part_component_id_and_driver_id	=init_data[1];
			var component_init_fun_array		=init_data[2];
			var program_data					=init_data[3];
			var common_shader_code				=init_data[4];
				
			init_ids_of_part_and_component(
				sorted_component_name_id,part_component_id_and_driver_id,render);
					
			init_system_bindgroup(render);
				
			for(var i=0,ni=component_init_fun_array.length;i<ni;i++)
				if(typeof(component_init_fun_array[i])=="object"){
					var component_id=component_init_fun_array[i].component_id;
					var component_name=component_init_fun_array[i].component_name;
					var init_function=component_init_fun_array[i].initialization_function;
			
					if(typeof(init_function)!="function"){
						alert("component init_function is NOT FUNCTION:	"
							+component_name+"		"+component_id+"		"+e.toString());
						alert(component_init_fun_array[i].initialization_function);
						continue;
					}
					try{
						init_function(component_name,component_id,render);
					}catch(e){
						alert("Error execute component init_function:	"
							+component_name+"		"+component_id+"		"+e.toString());
						alert(component_init_fun_array[i].initialization_function);
						continue;
					}
				}

			for(var render_id=0,render_number=program_data.length;render_id<render_number;render_id++){
				var my_render_name				=program_data[render_id].shift();
				var my_render_driver_function	=program_data[render_id].shift();
				var my_shader_program			=common_shader_code;	
				var str=program_data[render_id].shift();
				for(var i=0,ni=str.length;i<ni;i++)
					my_shader_program+=str[i];
		
				render.render_driver[render_id]=null;
				if(typeof(my_render_driver_function)!="function"){
					alert("render my_render_driver_function is not a function:	"+my_render_name);
					continue;
				}
				try{
					render.render_driver[render_id]=my_render_driver_function(
						render_id,my_render_name,render_init_data[render_id],
						program_data[render_id].shift(),my_shader_program,
						render);
				}catch(e){
					render.render_driver[render_id]=null;
					alert("create render driver fail	"+my_render_name);
					alert(e.toString());
					continue;
				}
	
				if(Array.isArray(render.render_driver[render_id].method_render_flag)){
					var my_method_render_flag=render.render_driver[render_id].method_render_flag;
					if(my_method_render_flag.length>render.system_buffer.method_buffer_number)
						my_method_render_flag.length=render.system_buffer.method_buffer_number;
					for(var i=0,ni=render.system_buffer.method_buffer_number;i<ni;i++)
						if(typeof(my_method_render_flag[i])!="boolean")
							my_method_render_flag[i]=false;
				}else{
					render.render_driver[render_id].method_render_flag=new Array();
					for(var i=0,ni=render.system_buffer.method_buffer_number;i<ni;i++)
						render.render_driver[render_id].method_render_flag[i]=true;
				}
			}
				
			request_render_data(render);
				
			draw_scene_main(part_init_data,component_init_data,render);
				
			return render;
		}
		if(typeof(create_data)!="boolean"){
			alert("Web server error, response_data type is NOT boolean!");
			return null;
		}
		if(create_data){
			alert("Web server error, get_client_interface fail!");
			return null;
		}
		create_engine_sleep_time_length*=create_engine_sleep_time_length_scale;
		if(create_engine_sleep_time_length>create_engine_max_sleep_time_length){
			alert("Web server error,try too many times to create scene!");
			return null;
		}
		await new Promise((resolve)=>{setTimeout(resolve,create_engine_sleep_time_length);});
	};
}

