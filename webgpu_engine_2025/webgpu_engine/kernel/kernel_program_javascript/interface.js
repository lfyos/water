function construct_scene_interface(my_scene)
{
	this.scene=my_scene;
	
	this.set_system_buffer_and_compute_component_location=function()
	{
		set_system_buffer_and_compute_component_location_routine(this.scene);
	}
	this.get_target_number=function()
	{
		return get_target_number_routine(this.scene)
	}
	this.get_target_parameter=function(target_id)
	{
		return get_target_parameter_routine(target_id,this.scene);
	}
	this.front_process_scene=function(scene_id)
	{
		return front_process_scene_routine(scene_id,this.scene);
	}
	this.back_process_scene=function()
	{
		back_process_scene_routine(this.scene);
	}
	this.scene_target_begin=function(target_id,scene_target_array)
	{
		return scene_target_begin_routine(target_id,scene_target_array,this.scene);
	}
	this.scene_target_end=function(target_id,scene_target_array)
	{
		scene_target_end_routine(target_id,scene_target_array,this.scene);
	}
	this.scene_target_complete=async function(target_id)
	{
		await scene_target_complete_routine(target_id,this.scene);
	}
	this.draw_scene_target=function(target_id,scene_target_array,pass_id)
	{
		draw_scene_target_routine(target_id,scene_target_array,pass_id,this.scene);
	}
}
