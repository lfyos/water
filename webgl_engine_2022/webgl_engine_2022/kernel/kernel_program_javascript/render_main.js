async function render_main(create_engine_sleep_time_length_scale,
	create_engine_sleep_time_length,create_engine_max_sleep_time_length,
	my_canvas,my_url,my_user_name,my_pass_word,my_language_name,
	my_scene_name,my_link_name,my_initialization_parameter,
	user_process_bar_function)
{
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