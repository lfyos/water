package kernel_scene;

public class scene_environment_varible 
{
	public String environment_varible_name;
	public String environment_varible_value;
	
	public boolean add_to_temporary_directory_flag;
	
	public scene_environment_varible(
			String my_environment_varible_name,
			String my_environment_varible_value,
			boolean my_add_to_temporary_directory_flag)
	{
		environment_varible_name		=new String(my_environment_varible_name);
		environment_varible_value		=new String(my_environment_varible_value);
		add_to_temporary_directory_flag	=my_add_to_temporary_directory_flag;
	}
}
