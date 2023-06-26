async function render_main(create_engine_sleep_time_length_scale,
	create_engine_sleep_time_length,create_engine_max_sleep_time_length,
	my_canvas,my_url,my_user_name,my_pass_word,my_language_name,
	my_scene_name,my_link_name,my_initialization_parameter,
	user_process_bar_function)
{
	function render_initialization(init_data,render)
	{
		var sorted_component_name_id		=init_data[0];
		var	part_component_id_and_driver_id	=init_data[1];
		var response_fun_array				=init_data[2];
		var program_data					=init_data[3];
		var common_shader_data				=init_data[4];

		init_ids_of_part_and_component(
			sorted_component_name_id,part_component_id_and_driver_id,render);

		for(var i=0,ni=response_fun_array.length;i<ni;i++){
			if(typeof(response_fun_array[i])!="object")
				continue;
			var component_id=response_fun_array[i].component_id;
			var component_name=response_fun_array[i].component_name;
			var init_function=response_fun_array[i].initialization_function;
			if(typeof(init_function)!="string"){
				console.log("component init_function program is not string:	"+component_name+"		"+component_id);
				console.log(response_fun_array[i].initialization_function);
				continue;
			}
			if((init_function=init_function.trim()).length<=0){
				console.log("component init_function program is empty:	"+component_name+"		"+component_id);
				console.log(response_fun_array[i].initialization_function);
				continue;
			}
			try{
				init_function=(eval("["+init_function+"]"))[0];
			}catch(e){
				console.log("Error compile component init_function:	"
					+component_name+"		"+component_id+"		"+e.toString());
				console.log(response_fun_array[i].initialization_function);
				continue;
			}
			if(typeof(init_function)!="function"){
				console.log("component init_function is NOT FUNCTION:	"
					+component_name+"		"+component_id+"		"+e.toString());
				console.log(response_fun_array[i].initialization_function);
				continue;
			}
			try{
				init_function(component_name,component_id,render);
			}catch(e){
				console.log("Error execute component init_function:	"
					+component_name+"		"+component_id+"		"+e.toString());
				console.log(response_fun_array[i].initialization_function);
				continue;
			}
		}
		
		for(var render_id=0,render_number=program_data.length;render_id<render_number;render_id++){
			var my_render_name				=	program_data[render_id][0];
			var my_driver_function			=	program_data[render_id][1];
			var my_shader_data				=	program_data[render_id][2];
			
			render.render_shader_data[i]	=	my_shader_data;
			render.render_driver[render_id]	=	null;
			
			try{
				my_driver_function=(eval("["+my_driver_function+"]"))[0];
			}catch(e){
				alert("Error render driver_function:	"+my_render_name+"		"+e.toString());
				continue;
			}
			if(typeof(my_driver_function)!="function"){
				alert("render driver_function is not a function:	"+my_render_name);
				continue;
			}
			try{
				render.render_driver[render_id]=my_driver_function(render_id,my_render_name,
					render.render_initialize_data[render_id],render.render_shader_data[i],render);
			}catch(e){
				render.render_driver[render_id]=null;
				alert("create render driver fail	"+my_render_name);
				continue;
			}
		};
		
		render.common_shader_data=common_shader_data;
		
		request_render_data(render);
		draw_scene(render);
		
		return render;
	};
	
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
				try{
					return render_initialization(init_data,
							new construct_render_routine(my_webgpu,my_url,
								my_user_name,my_pass_word,my_language_name,create_data));
				}catch(e){
					return null;
				}
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
	
	my_user_name	=(typeof(my_user_name		)!="string")?"NoName"	 :(my_user_name.trim());
	my_pass_word	=(typeof(my_pass_word		)!="string")?"NoPassword":(my_pass_word.trim());
	my_language_name=(typeof(my_language_name	)!="string")?"english"	 :(my_language_name.trim());
	my_scene_name	=(typeof(my_scene_name		)!="string")?""			 :(my_scene_name.trim());
	my_link_name	=(typeof(my_link_name		)!="string")?""			 :(my_link_name.trim());
	
	my_user_name=encodeURIComponent(encodeURIComponent(my_user_name));
	my_pass_word=encodeURIComponent(encodeURIComponent(my_pass_word));
	my_scene_name=encodeURIComponent(encodeURIComponent(my_scene_name));
	my_link_name=encodeURIComponent(encodeURIComponent(my_link_name));
	
	var process_bar_url=my_url+"?channel=process_bar&language="+my_language_name;
	process_bar_url+="&user_name="	+my_user_name		+"&pass_word="	+my_pass_word;
	
	var request_url=my_url+"?channel=creation&command=creation&language="	+my_language_name;
	request_url+="&user_name="	+my_user_name		+"&pass_word="	+my_pass_word;
	request_url+="&scene_name="	+my_scene_name		+"&link_name="	+my_link_name;
	if(typeof(my_initialization_parameter)=="object")
		if(typeof(my_initialization_parameter.length)=="number")
			for(var i=0,ni=my_initialization_parameter.length;i<ni;i++){
				var parameter_item	=my_initialization_parameter[i];
				var parameter_name	=parameter_item[0].toString().trim();
				var parameter_value	=parameter_item[1].toString().trim();

				request_url+="&"+parameter_name+"="+parameter_value;
			};

	var webgpu;
	if((webgpu=await create_webgpu(my_canvas))==null)
		return null;
	var process_bar_object=new construct_process_bar(
			webgpu,user_process_bar_function,process_bar_url);

	var ret_val=await request_create_engine(create_engine_sleep_time_length_scale,
			create_engine_sleep_time_length,create_engine_max_sleep_time_length,
			webgpu,request_url,my_url,my_user_name,my_pass_word,my_language_name,
			await process_bar_object.start());
	
	process_bar_object.destroy();
	
	return ret_val;
};
