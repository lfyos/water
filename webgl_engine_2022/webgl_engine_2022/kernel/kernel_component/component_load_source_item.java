package kernel_component;

public class component_load_source_item 
{
	public String component_name,token_string,component_file_name,component_file_charset;
	public component_load_source_item(
			String my_component_name,String my_token_string,
			String my_component_file_name,String my_component_file_charset)
	{
		component_name=my_component_name;
		token_string=my_token_string;
		component_file_name=my_component_file_name;
		component_file_charset=my_component_file_charset;
	}
}
