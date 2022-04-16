package kernel_information;

import kernel_driver.modifier_container;
import kernel_engine.client_information;

public class modifier_information extends jason_creator
{
	private modifier_container modifier_cont;
	
	public void print()
	{
		print("modifier_number",				modifier_cont.get_modifier_number());
		print("modifier_terminated_time_length",modifier_cont.get_timer().get_current_time());
		return;
	}
	public modifier_information(modifier_container my_modifier_cont,client_information my_ci)
	{
		super(my_ci.request_response);
		modifier_cont=my_modifier_cont;
	}
}
