package kernel_network;

public class network_result {
	public String result[];
	public network_result next;
	
	public network_result(String my_result[])
	{
		result=my_result;
		next=null;
	}
}
