package kernel_security;

import kernel_engine.system_parameter;
import kernel_interface.user_statistics;
public class client_session
{
	public String scene_file_name,scene_file_charset;
	public String client_id,user_name;

	public user_statistics statistics_user;
	
	public client_session(
			String my_scene_file_name,String my_scene_file_charset,
			String my_client_id,String my_user_name,
			user_statistics my_statistics_user,system_parameter system_par) 
	{	
		scene_file_name		=my_scene_file_name;
		scene_file_charset	=my_scene_file_charset;
		client_id			=my_client_id;
		user_name			=my_user_name;
		statistics_user		=my_statistics_user;
	}
	public void destroy()
	{
		if(scene_file_name!=null)
			scene_file_name=null;
		if(statistics_user!=null)
			statistics_user=null;
	}
}
