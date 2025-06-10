package kernel_scene;

public class search_file_content_type_result 
{
	public String zip_link_str,content_str,ext_str,link_token,path_name;
	public search_file_content_type_result(
			String my_zip_link_str,String my_content_str,
			String my_ext_str,String my_link_token,String my_path_name)
	{
		zip_link_str=my_zip_link_str;
		content_str	=my_content_str;
		ext_str		=my_ext_str;
		link_token	=my_link_token;
		path_name	=my_path_name;
	}
}
