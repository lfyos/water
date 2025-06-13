package kernel_component;

import java.util.ArrayList;

public class component_load_source_item 
{
	public String component_name,token_string;
	
	public String create_component_data[];
	public long component_last_time;
	
	public String component_file_name,component_file_charset;
	
	public component_load_source_item(component_load_source_item clsi)
	{
		component_name				=clsi.component_name;
		token_string				=clsi.token_string;
		create_component_data		=clsi.create_component_data;
		component_last_time			=clsi.component_last_time;
		component_file_name			=clsi.component_file_name;
		component_file_charset		=clsi.component_file_charset;
	}
	public component_load_source_item(
			String my_component_name,String my_token_string,
			String my_component_file_name,String my_component_file_charset)
	{
		component_name			=my_component_name;
		token_string			=my_token_string;
		create_component_data	=null;
		component_last_time		=0;
		component_file_name		=my_component_file_name;
		component_file_charset	=my_component_file_charset;
	}
	public component_load_source_item(
			String my_component_name,String my_token_string,
			String my_create_component_data[],long my_component_last_time)
	{
		component_name			=my_component_name;
		token_string			=my_token_string;
		create_component_data	=my_create_component_data;
		component_last_time		=my_component_last_time;
		component_file_name		=null;
		component_file_charset	=null;
	}
	public component_load_source_item(
			String my_component_name,String my_token_string,
			ArrayList<String> my_create_component_list,long my_component_last_time)
	{
		component_name			=my_component_name;
		token_string			=my_token_string;
		create_component_data	=my_create_component_list.toArray(
									new String[my_create_component_list.size()]);	
		component_last_time		=my_component_last_time;
		component_file_name		=null;
		component_file_charset	=null;
	}
}
