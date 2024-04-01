async function render_main(
	my_canvas,my_create_parameter,user_process_bar_function,
	my_url,default_fetch_parameter,create_engine_sleep_time_length_scale,
	create_engine_sleep_time_length,create_engine_max_sleep_time_length)
{
	var webgpu=await create_webgpu(my_canvas);
	if(webgpu.error_flag)
		return null;
		
	if(typeof(my_create_parameter)!="object")
		my_create_parameter={};
	else if(my_create_parameter==null)
		my_create_parameter={};
		
	my_create_parameter.user_name	=(typeof(my_create_parameter.user_name)	!="string")	?"NoName"		:(my_create_parameter.user_name.trim());
	my_create_parameter.user_name	=(my_create_parameter.user_name.length<=0)			?"NoName"		:(my_create_parameter.user_name);
	
	my_create_parameter.pass_word	=(typeof(my_create_parameter.pass_word)	!="string")	?"NoPassword"	:(my_create_parameter.pass_word.trim());
	my_create_parameter.pass_word	=(my_create_parameter.pass_word.length<=0)			?"NoPassword"	:(my_create_parameter.pass_word);
	
	my_create_parameter.language	=(typeof(my_create_parameter.language)	!="string")	?"chinese"		:(my_create_parameter.language.trim());
	my_create_parameter.language	=(my_create_parameter.language.length<=0)			?"chinese"		:(my_create_parameter.language);
	
	my_create_parameter.scene_name	=(typeof(my_create_parameter.scene_name)!="string")	?""				:(my_create_parameter.scene_name.trim());
	my_create_parameter.link_name	=(typeof(my_create_parameter.link_name)	!="string")	?""				:(my_create_parameter.link_name.trim());	
	
	var my_create_parameter_string="";
	for (var my_item_name in my_create_parameter){
		var my_item_value=my_create_parameter[my_item_name].toString().trim();
		my_create_parameter_string+="&"+my_item_name+"="+my_item_value;
	}
	var process_bar_object=new construct_process_bar(webgpu,user_process_bar_function,
			my_url+"?channel=process_bar"+my_create_parameter_string);
	var process_bar_data=await process_bar_object.start(default_fetch_parameter);
	if(process_bar_data==null)
		return null;

	var request_url=my_url+"?channel=creation&command=creation";
	request_url+="&container="	+process_bar_data.container_id;
	request_url+="&process_bar="+process_bar_data.process_bar_id;
	request_url+=my_create_parameter_string;
	
	var ret_val=await request_create_engine(
			create_engine_sleep_time_length_scale,
			create_engine_sleep_time_length,
			create_engine_max_sleep_time_length,
			webgpu,request_url,my_url,
			my_create_parameter.user_name,
			my_create_parameter.pass_word,
			my_create_parameter.language,
			default_fetch_parameter);
	
	process_bar_object.destroy();
	
	return ret_val;
};