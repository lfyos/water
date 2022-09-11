package driver_component_pickup;

import kernel_driver.part_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_part.part;

public class extended_part_instance_driver extends part_instance_driver
{
	private double id_keeep_time_length;
	
	public extended_part_instance_driver(double my_id_keeep_time_length)
	{
		super();
		id_keeep_time_length=my_id_keeep_time_length;
	}
	public void destroy()
	{
		super.destroy();
	}
	public void response_init_part_data(part p,engine_kernel ek,client_information ci)
	{
		ci.request_response.print(id_keeep_time_length);
	}
	public String[] response_part_event(part p,engine_kernel ek,client_information ci)
	{			
		return super.response_part_event(p,ek,ci);
	}
}