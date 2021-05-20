package kernel_information;

import kernel_driver.modifier_container;
import kernel_engine.client_information;

public class modifier_information extends jason_creator
{
	private modifier_container modifier_cont[];
	
	public void print()
	{
		int modifier_number[]					=new int   [modifier_cont.length];
		double modifier_speed[]					=new double[modifier_cont.length];
		long modifier_terminated_time_length[]	=new long  [modifier_cont.length];
		
		
		for(int i=0;i<modifier_speed.length;i++){
			modifier_number[i]					=modifier_cont[i].get_modifier_number();
			modifier_speed[i]					=modifier_cont[i].get_timer().get_speed();
			modifier_terminated_time_length[i]	=modifier_cont[i].get_timer().get_current_time();
		}
		print("modifier_number",				modifier_number);
		print("modifier_speed",					modifier_speed);
		print("modifier_terminated_time_length",modifier_terminated_time_length);
		return;
	}
	public modifier_information(modifier_container my_modifier_cont[],client_information my_ci)
	{
		super(my_ci.request_response);
		modifier_cont=my_modifier_cont;
	}
}
