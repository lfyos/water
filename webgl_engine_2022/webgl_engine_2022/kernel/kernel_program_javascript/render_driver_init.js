function render_driver_initialization(program_data,common_shader_code,render)
{
	for(var render_id=0,render_number=program_data.length;render_id<render_number;render_id++){
		var my_init_data				=render.render_initialize_data[render_id];
		var my_render_name				=program_data[render_id].shift();
		
		str=program_data[render_id].shift();
		var my_render_driver_function_code	=""
		for(var i=0,ni=str.length;i<ni;i++)
			my_render_driver_function_code+=str[i];
		my_render_driver_function_code	=
				"["+
					"function(													\n"	+
					"			render_id,				render_name,			\n"	+
					"			init_data,				text_array,				\n"	+
					"			shader_code,			render)					\n"	+
					"{															\n"	+
							my_render_driver_function_code					+  "\n" +							  
					"		return new main(									\n"	+
					"			render_id,				render_name,			\n"	+
					"			init_data,				text_array,				\n"	+
					"			shader_code,			render);				\n"	+			
					"}															\n"	+
				"]";
		
		var my_shader_program="";	
		for(var i=0,ni=common_shader_code.length;i<ni;i++)
			my_shader_program+=common_shader_code[i];
		str=program_data[render_id].shift();
		for(var i=0,ni=str.length;i<ni;i++)
			my_shader_program+=str[i];

		render.render_driver[render_id]=null;

		try{
			my_render_driver_function=eval(my_render_driver_function_code)[0];
		}catch(e){
			alert("Error my_render_driver_function:	"+my_render_name+"		"+e.toString());
			console.log(my_render_driver_function_code);
			continue;
		}
		if(typeof(my_render_driver_function)!="function"){
			alert("render my_render_driver_function is not a function:	"+my_render_name);
			console.log(my_render_driver_function_code);
			continue;
		}

		try{
			render.render_driver[render_id]=my_render_driver_function(
				render_id,				my_render_name,
				my_init_data,			program_data[render_id],
				my_shader_program,		render);
		}catch(e){
			render.render_driver[render_id]=null;
			alert("create render driver fail	"+my_render_name);
			alert(e.toString());
			console.log(my_render_driver_function_code);
			continue;
		}
	};
	return render;
};