async function request_create_engine(create_engine_sleep_time_length_scale,
		create_engine_sleep_time_length,create_engine_max_sleep_time_length,
		my_webgpu,request_url,my_url,my_user_name,my_pass_word,my_language_name,
		default_fetch_parameter)
{
	var create_data;
	
	for(var continue_flag=true;continue_flag;){
		var engine_promise=await fetch(request_url,default_fetch_parameter.request_create_engine);
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
		switch(typeof(create_data)){
		case "object":
			if(create_data!=null){
				continue_flag=false;
				break;
			}
			create_engine_sleep_time_length*=create_engine_sleep_time_length_scale;
			if(create_engine_sleep_time_length>create_engine_max_sleep_time_length){
				alert("Web server error,try too many times to create scene!");
				return null;
			}
			await new Promise((resolve)=>{setTimeout(resolve,create_engine_sleep_time_length);});
			break;
		case "number":
			alert("Web server create scene fail:	"+create_data.toString());
			return null;
		default:
			alert("Web server error, response_data type error!");
			return null;
		}
	}
	
	var my_container_id			=create_data[0][0];
	var my_channel_id			=create_data[0][1];
	var	my_render_init_data		=create_data[1];
	var	my_part_init_data		=create_data[2];
	var	my_component_init_data	=create_data[3];
	var	my_render_data			=create_data[4];
	var my_init_promise			=import(create_data.pop());

	var render=new construct_render_routine(
			my_webgpu,my_url,my_user_name,my_pass_word,my_language_name,
			my_container_id,my_channel_id,my_render_data,default_fetch_parameter);

	var render_init_data=new Array();
	for(var i=0,ni=my_render_init_data.length-1;i<ni;){
		var my_data		=my_render_init_data[i++];
		var render_id	=my_render_init_data[i++];
		render_init_data[render_id]=my_data;
	};

	var part_init_data=new Array(render.part_driver.length);
	for(var i=0,ni=part_init_data.length;i<ni;i++)
		part_init_data[i]=new Array();
	for(var i=0,ni=my_part_init_data.length-1;i<ni;){
		var my_data		=my_part_init_data[i++];
		var render_id	=my_part_init_data[i++];
		var part_id		=my_part_init_data[i++];
		part_init_data[render_id][part_id]=my_data;
	};
			
	var component_init_data=new Array(render.component_location_data.component_number);
	for(var i=0,ni=component_init_data.length;i<ni;i++)
		component_init_data[i]=new Array();
	for(var i=0,ni=my_component_init_data.length-1;i<ni;){
		var my_data				=my_component_init_data[i++];
		var my_component_id		=my_component_init_data[i++];
		var my_driver_id		=my_component_init_data[i++];
		component_init_data[my_component_id][my_driver_id]=my_data;
	}
	
	var	init_data=(await my_init_promise).initialization_data;

	var	sorted_component_name_id		=init_data[0];
	var	part_component_id_and_driver_id	=init_data[1];
	var	component_init_fun_array		=init_data[2];
	var	program_data					=init_data[3];
	var	common_shader_code				=init_data[4];
				
	init_ids_of_part_and_component(
		sorted_component_name_id,part_component_id_and_driver_id,render);
					
	render.system_buffer.init_id_buffer_and_system_bindgroup(render);
				
	for(var i=0,ni=component_init_fun_array.length;i<ni;i++){
		if(typeof(component_init_fun_array[i])!="object")
			continue;
		if(component_init_fun_array[i]==null)
			continue;	
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
		var my_shader_program			=program_data[render_id].shift();
		var my_text_array				=program_data[render_id].shift();
				
		var combined_shader_program=common_shader_code;
		for(var i=0,ni=my_shader_program.length;i<ni;i++)
			combined_shader_program+=my_shader_program[i];
					
		render.render_driver[render_id]=my_render_driver_function(
			render_id,my_render_name,render_init_data[render_id],
			combined_shader_program,my_text_array,render);

		if(Array.isArray(render.render_driver[render_id].method_render_flag)){
			for(var i=0,ni=render.render_driver[render_id].method_render_flag.length;i<ni;i++)
				if(typeof(render.render_driver[render_id].method_render_flag[i])!="boolean")
					render.render_driver[render_id].method_render_flag[i]=false;
		}else
			render.render_driver[render_id].method_render_flag=new Array();
	}

	request_render_data(render);
	draw_scene_main(part_init_data,component_init_data,render);
	
	return render;
}

