package kernel_common_class;

public class string_link_list {
	public String str;
	public boolean error_flag;
	public string_link_list next_list;
	public string_link_list(
			String my_str,boolean my_error_flag)
	{
		str=my_str;
		next_list=null;
		error_flag=my_error_flag;
	}
	public string_link_list(
			String my_str,string_link_list my_next_list)
	{
		str=my_str;
		next_list=my_next_list;
		error_flag=false;
	}
}