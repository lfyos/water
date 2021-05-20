package kernel_engine;

public class engine_kernel_link_list_and_client_information
{
	public engine_kernel_link_list	engine_kernel_link_list;
	public client_information 		client_information;
	public volatile int 			lock_number;
	public engine_kernel_link_list_and_client_information(
			engine_kernel_link_list my_engine_kernel_link_list)
	{
		engine_kernel_link_list		=my_engine_kernel_link_list;
		client_information			=null;
		lock_number					=0;
	}
}