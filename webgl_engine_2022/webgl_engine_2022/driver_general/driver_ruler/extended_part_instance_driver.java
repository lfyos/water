package driver_ruler;

import kernel_driver.part_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_part.part;

public class extended_part_instance_driver extends part_instance_driver
{
	public extended_part_instance_driver()
	{
		super();
	}
	public void destroy()
	{
		super.destroy();
	}
	public void response_init_part_data(part p,engine_kernel ek,client_information ci)
	{
		
	}
	public String[] response_event(part p,engine_kernel ek,client_information ci)
	{			
		return super.response_event(p,ek,ci);
	}
}