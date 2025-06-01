async function request_create_scene(create_scene_sleep_time_length_scale,
		create_scene_sleep_time_length,create_scene_max_sleep_time_length,
		my_webgpu,request_url,my_url,my_user_name,my_pass_word,my_language_name,
		my_default_fetch_parameter)
{
	var create_data;

	for(var start_time=new Date().getTime;;){
		var engine_promise=await fetch(request_url,my_default_fetch_parameter.request_create_scene);
		if(!(engine_promise.ok)){
			alert("Web server error when create scene,fetch fail:"+engine_promise.status);
			return null;
		}
		try{
			create_data=await engine_promise.json();
		}catch(e){
			alert("Web server error when create scene, NOT jason scene date:  "+e.toString());
			return null;
		}
		if(!(Array.isArray(create_data))){
			alert("Web server create scene fail(!(Array.isArray(create_data))):	"+create_data.toString());
			return null;
		}
		if(create_data.length>0)
			break;
		if((new Date().getTime-start_time)>create_scene_max_sleep_time_length){
			alert("Web server create scene fail:	try creation timeout!");
			return null;
		}
		await new Promise(resolve=>{setTimeout(resolve,create_scene_sleep_time_length);});
		if(create_scene_sleep_time_length_scale>1.0)			
			create_scene_sleep_time_length*=create_scene_sleep_time_length_scale;
	};
	
	var	my_render_init_data		=create_data[0];
	var	my_part_init_data		=create_data[1];
	var	my_component_init_data	=create_data[2];
	var	my_init_url				=create_data[3];
	var	my_scene_parameter		=create_data[4];
	
	var	my_init_promise			=import(my_init_url);

	var scene=new construct_scene(my_webgpu,my_url,
			my_user_name,my_pass_word,my_language_name,
			my_scene_parameter,my_default_fetch_parameter);
	
	scene.init_data=new Object();

	scene.init_data.render_init_data=new Array();
	for(var i=0,ni=my_render_init_data.length-1;i<ni;){
		var my_data		=my_render_init_data[i++];
		var render_id	=my_render_init_data[i++];
		scene.init_data.render_init_data[render_id]=my_data;
	};

	scene.init_data.part_init_data=new Array(scene.part_driver.length);
	for(var i=0,ni=scene.init_data.part_init_data.length;i<ni;i++)
		scene.init_data.part_init_data[i]=new Array();
	for(var i=0,ni=my_part_init_data.length-1;i<ni;){
		var my_data		=my_part_init_data[i++];
		var render_id	=my_part_init_data[i++];
		var part_id		=my_part_init_data[i++];
		scene.init_data.part_init_data[render_id][part_id]=my_data;
	};

	scene.init_data.component_init_data=new Array(scene.component_location_data.component_number);
	for(var i=0,ni=scene.init_data.component_init_data.length;i<ni;i++)
		scene.init_data.component_init_data[i]=new Array();
	for(var i=0,ni=my_component_init_data.length-1;i<ni;){
		var my_data				=my_component_init_data[i++];
		var my_component_id		=my_component_init_data[i++];
		var my_driver_id		=my_component_init_data[i++];
		scene.init_data.component_init_data[my_component_id][my_driver_id]=my_data;
	}

	var	init_data=(await my_init_promise).initialization_data;

	var	sorted_component_name_id			=init_data[0];
	var	part_component_id_and_driver_id		=init_data[1];
	var	component_init_fun_array			=init_data[2];
	var	program_data						=init_data[3];
	var common_shader_data_structure		=init_data[4][0];
	var common_shader_variable_declaration	=init_data[4][1];
	var location_shader_program				=init_data[4][2];
	var init_parameter						=init_data[5]
			
	init_ids_of_part_and_component(scene,
		sorted_component_name_id,part_component_id_and_driver_id);
	
	scene.system_buffer=new construct_system_buffer(
		init_parameter.max_target_number,init_parameter.max_method_number,scene);
	
	scene.component_location_data.do_component_location_initialization(
			scene.component_array_sorted_by_id,
			scene.system_buffer.id_buffer,scene.system_buffer.camera_buffer,
			scene.system_bindgroup_id.length,scene.camera.camera_number,
			common_shader_data_structure,location_shader_program);

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
			init_function(component_name,component_id,scene);
		}catch(e){
			alert("Error execute component init_function:	"
				+component_name+"		"+component_id+"		"+e.toString());
			alert(component_init_fun_array[i].initialization_function);
			continue;
		}
	}
	
	var common_shader_code=common_shader_data_structure+common_shader_variable_declaration;
	for(var render_id=0,render_number=program_data.length;render_id<render_number;render_id++){
		var my_render_name				=program_data[render_id][0];
		var my_render_driver_function	=program_data[render_id][1];
		var my_shader_program			=program_data[render_id][2];
		var my_text_array				=program_data[render_id][3];
		for(var i=4,ni=program_data[render_id].length;i<ni;i++)
			my_text_array=my_text_array.concat(program_data[render_id][i]);
		
		var combined_shader_program="const scene_camera_number=";
		combined_shader_program+=scene.camera.camera_number.toString();
		combined_shader_program+=";\n"+common_shader_code+"\n";

		for(var i=0,ni=my_shader_program.length;i<ni;i++)
			combined_shader_program+=my_shader_program[i];
					
		scene.render_driver[render_id]=my_render_driver_function(
			render_id,my_render_name,scene.init_data.render_init_data[render_id],
			combined_shader_program,my_text_array,scene);

		if(Array.isArray(scene.render_driver[render_id].method_render_flag)){
			for(var i=0,ni=scene.render_driver[render_id].method_render_flag.length;i<ni;i++)
				if(typeof(scene.render_driver[render_id].method_render_flag[i])!="boolean")
					scene.render_driver[render_id].method_render_flag[i]=false;
		}else
			scene.render_driver[render_id].method_render_flag=new Array();
	}
	
	request_render_data(scene);
	
	return scene;
}

