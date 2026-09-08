package kernel_scene;

public class search_file_content_type_result 
{
	public String zip_link_str,content_str,ext_str,path_name;
	public boolean link_flag;
	public search_file_content_type_result(
			String my_zip_link_str,String my_content_str,
			String my_ext_str,boolean my_link_flag,String my_path_name)
	{
		zip_link_str=my_zip_link_str;
		content_str	=my_content_str;
		ext_str		=my_ext_str;
		link_flag	=my_link_flag;
		path_name	=my_path_name;
	}
}
