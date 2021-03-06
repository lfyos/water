package kernel_information;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class statistics_user_information  extends jason_creator
{
	private engine_kernel ek;
	private client_information ci;
	
	public void print()
	{
		print("user_kernel_number",				ci.statistics_user.user_kernel_number);
		print("user_component_number",			ci.statistics_user.user_component_number);
		print("max_user_kernel_number",			ci.statistics_user.max_user_kernel_number);
		print("max_user_component_number",		ci.statistics_user.max_user_component_number);
		
		print("engine_kernel_number",			ci.engine_current_number[0]);
		print("engine_component_number",		ci.engine_current_number[1]);
		print("max_engine_kernel_number",		ek.system_par.max_engine_kernel_number);
		print("max_engine_component_number",	ek.system_par.max_component_number);
	}
	public statistics_user_information(engine_kernel my_ek,client_information my_ci)
	{
		super(my_ci.request_response);
		ek=my_ek;
		ci=my_ci;
	}
}
