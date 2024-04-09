package kernel_driver;

public class modifier_driver_holder 
{
	public long sequence_id;
	public modifier_driver md;
	
	public modifier_driver_holder(modifier_driver my_md,long my_sequence_id)
	{
		md=my_md;
		sequence_id=my_sequence_id;
	}
}
