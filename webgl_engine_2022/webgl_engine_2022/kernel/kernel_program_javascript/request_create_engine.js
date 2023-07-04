async function request_create_engine(create_engine_sleep_time_length_scale,
		create_engine_sleep_time_length,create_engine_max_sleep_time_length,
		my_webgpu,request_url,my_url,my_user_name,my_pass_word,my_language_name,process_bar_id)
{
	while(true){
		var create_data,init_data;
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
			var initialization_url=create_data.pop();
			engine_promise=await fetch(initialization_url);
			if(!(engine_promise.ok)){
				alert("request render_initialization data fail: "+initialization_url);
				return null;
			}
			try{
				init_data = await engine_promise.json();
			}catch(e){
				alert("parse render_initialization data fail: "+e.toString());
				alert(initialization_url);
				return null;
			}
				
			var render=new construct_render_routine(my_webgpu,my_url,
								my_user_name,my_pass_word,my_language_name,create_data);
				
			var sorted_component_name_id		=init_data[0];
			var part_component_id_and_driver_id	=init_data[1];
			var component_init_fun_array		=init_data[2];
			var program_data					=init_data[3];
			var common_shader_code				=init_data[4];
				
			init_ids_of_part_and_component(sorted_component_name_id,part_component_id_and_driver_id,render);
					
			init_system_bindgroup(render);
				
			component_init_function(component_init_fun_array,render);
				
			render_driver_initialization(program_data,common_shader_code,render);
				
			request_render_data(render);
				
			draw_scene_main(render);
				
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

