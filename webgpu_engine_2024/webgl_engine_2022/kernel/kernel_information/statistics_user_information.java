package kernel_information;

import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class statistics_user_information  extends jason_creator
{
	private scene_kernel sk;
	private client_information ci;
	
	public void print()
	{
		print("user_scene_kernel_number",			ci.statistics_user.user_scene_kernel_number);
		print("user_scene_component_number",		ci.statistics_user.user_scene_component_number);
		
		print("user_max_scene_kernel_number",		ci.statistics_user.user_max_scene_kernel_number);
		print("user_max_scene_component_number",	ci.statistics_user.user_max_scene_component_number);
		
		print("scene_kernel_number",				ci.scene_counter.scene_kernel_number);
		print("scene_component_number",				ci.scene_counter.scene_component_number);
		
		print("max_scene_kernel_number",			sk.system_par.max_scene_kernel_number);
		print("max_scene_component_number",			sk.system_par.max_scene_component_number);
	}
	public statistics_user_information(scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		
		sk=my_sk;
		ci=my_ci;
	}
}
