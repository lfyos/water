package kernel_common_class;

public class balance_tree_item<balance_tree_compare_data_struct>
{
	public balance_tree_compare_data_struct compare_data;
	
	public int compare(balance_tree_compare_data_struct my_compare_data)
	{
		return 0;
	}
	public void destroy()
	{
		compare_data=null;
	}
	public balance_tree_item(balance_tree_compare_data_struct my_compare_data)
	{	
		compare_data=my_compare_data;
	}
}
