package kernel_scene;

import java.io.File;
import java.nio.charset.Charset;

import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;

public class scene_environment
{
	private	String scene_data_path_name,scene_temparatory_path_name;
	private	change_name environment_variable;
	
	public String get_data_path_name()
	{
		return scene_data_path_name;
	}
	public String get_temparatory_path_name()
	{
		return scene_temparatory_path_name;
	}
	public String get_environment(String name)
	{
		return environment_variable.search_change_name(name,null);
	}
	public scene_environment(String file_name)
	{
		if(!(new File(file_name).exists())) {
			debug_information.println("scene_environment file NOT exist:	",file_name);
			System.exit(0);
			return;
		}
		file_name=file_reader.separator(file_name);
		file_reader f=new file_reader(file_name,Charset.defaultCharset().name());
		if(f.error_flag()) {
			debug_information.println("scene_environment file error:	",file_name);
			System.exit(0);
			return;
		}
		String file_charset=f.get_string();
		f.close();
		
		f=new file_reader(file_name,file_charset);
		f.get_string();
		
		if((scene_data_path_name=f.get_string())==null) {
			debug_information.println(
				"scene_environment file error,read scene_data_path_name fail:	",file_name);
			System.exit(0);
			return;
		}
		if(scene_data_path_name.length()<=0) {
			debug_information.println(
				"scene_environment file error,scene_data_path_name length is zero:	",file_name);
			System.exit(0);
			return;
		}
		scene_data_path_name=file_reader.separator(scene_data_path_name);
		if(scene_data_path_name.charAt(scene_data_path_name.length()-1)!=File.separatorChar)
			scene_data_path_name+=File.separatorChar;
		scene_data_path_name+="configure.txt";
		
		if(!(new File(scene_data_path_name).exists())) {
			debug_information.println(
				"scene_environment file error,system configure is NOT exist:	",file_name);
			debug_information.println(
					"scene_environment file error,scene_data_path_name is:	",scene_data_path_name);
			System.exit(0);
			return;
		}
		
		if((scene_temparatory_path_name=f.get_string())==null) {
			debug_information.println(
				"scene_environment file error,read scene_temparatory_path_name fail:	",file_name);
			System.exit(0);
			return;
		}
		if(scene_temparatory_path_name.length()<=0) {
			debug_information.println(
				"scene_environment file error,scene_temparatory_path_name length is zero:	",file_name);
			System.exit(0);
			return;
		}
		scene_temparatory_path_name=file_reader.separator(scene_temparatory_path_name);
		if(scene_temparatory_path_name.charAt(scene_temparatory_path_name.length()-1)!=File.separatorChar)
			scene_temparatory_path_name+=File.separatorChar;
		scene_temparatory_path_name+="configure.txt";
		
		environment_variable=new change_name(new file_reader[] {f},null);
		
		f.close();
	}
	public scene_environment(scene_environment scene_env)
	{
		scene_data_path_name		=new String(scene_env.scene_data_path_name);
		scene_temparatory_path_name	=new String(scene_env.scene_temparatory_path_name);
		environment_variable		=new change_name(scene_env.environment_variable,false);
		return;
	}
}
