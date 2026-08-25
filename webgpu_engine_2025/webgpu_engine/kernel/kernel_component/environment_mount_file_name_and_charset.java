package kernel_component;

import java.io.File;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_directory;
import kernel_common_class.debug_information;

public class environment_mount_file_name_and_charset 
{
	public static boolean select_mount(
			String select_token,				String select_file_name,
			String select_assemble_file_name,	String select_file_charset,
			file_reader fr,component_construction_parameter ccp)
	{
		if((select_token==null)||(select_file_name==null)
			||(select_assemble_file_name==null)||(select_file_charset==null)) 
		{
			debug_information.println("select_charset_mount:",
				"((select_token==null)||(select_file_name==null)||(select_assemble_file_name==null))||(select_file_charset==null)");
			return false;
		}

		select_token				=file_directory.replace_special_char(select_token.trim());
		select_file_name			=file_directory.replace_special_char(select_file_name.trim());
		select_file_name			=file_directory.replace_special_char(select_file_name.trim());
		select_assemble_file_name	=file_directory.replace_special_char(select_assemble_file_name.trim());
		select_assemble_file_name	=file_directory.replace_special_char(select_assemble_file_name.trim());
		select_file_charset			=file_directory.replace_special_char(select_file_charset.trim());

		if((select_token.length()<=0)||(select_file_name.length()<=0)
			||(select_assemble_file_name.length()<=0)||(select_file_charset.length()<=0)) 
		{
			debug_information.println("select_charset_mount:",
				"((select_token.length()<=0)||(select_file_name.length()<=0)||(select_assemble_file_name.length()<=0)||(select_file_charset.length()<=0))");
			return false;
		}
		
		if((select_token=ccp.sk.get_scene_environment_parameter(select_token))==null){
			debug_information.println("select_charset_mount:",
					"((select_token=ccp.sk.get_client_parameter(select_token))==null)");
			return false;
		}
		
		file_reader f_select=new file_reader(fr.directory_name+select_file_name,fr.get_charset());
		while(!(f_select.eof())){
			String my_select_token=f_select.get_string();
			String my_select_directory_name=f_select.get_string();
			if((my_select_token==null)||(my_select_directory_name==null))
				continue;
			
			my_select_token			=file_directory.replace_special_char(my_select_token);
			my_select_directory_name=file_directory.replace_special_char(my_select_directory_name);
			if((my_select_token.length()<=0)||(my_select_directory_name.length()<=0))
				continue;
			if(select_token.compareTo(my_select_token)!=0)
				continue;
			my_select_directory_name=fr.directory_name+my_select_directory_name;
			
			if(my_select_directory_name.charAt(my_select_directory_name.length()-1)!=File.separatorChar)
				my_select_directory_name+=File.separatorChar;
			
			fr.push_string(new String[]{
					my_select_directory_name+select_assemble_file_name,
					select_file_charset});
			return true;
		}
		f_select.close();
		return false;
	}
	public static boolean parameter_mount(
			String my_directory_name,String my_file_name,String my_file_charset,
			file_reader fr,component_construction_parameter ccp)
	{
		if((my_directory_name==null)||(my_file_name==null)||(my_file_charset==null)) {
			debug_information.println("client_parameter_charset_mount error",
					"((my_directory_name==null)||(my_file_name==null)||(my_file_charset==null))");
			return false;
		}
		if((my_directory_name=ccp.sk.get_scene_environment_parameter(my_directory_name.trim()))==null) {
			debug_information.println("client_parameter_charset_mount error",
					"((my_directory_name=ccp.sk.get_client_parameter(my_directory_name.trim()))==null)");
			return false;
		}
		if((my_directory_name=file_directory.replace_special_char(my_directory_name))==null) {
			debug_information.println("client_parameter_charset_mount error",
					"((my_directory_name=cut_string.do_cut(file_reader.separator(my_directory_name)))==null)");
			return false;
		}
		if((my_file_name=file_directory.replace_special_char(my_file_name)).length()<=0){
			debug_information.println("client_parameter_charset_mount error",
					"((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name))).length()<=0)");
			return false;
		}
		if(my_directory_name.charAt(my_directory_name.length()-1)!=File.separatorChar)
			my_directory_name+=File.separatorChar;
		fr.push_string(new String[]{my_directory_name+my_file_name,my_file_charset});
		
		return true;
	}
	public static boolean scene_sub_directory_mount(
			String my_directory_name,String my_file_name,String my_file_charset,
			file_reader fr,component_construction_parameter ccp)
	{
		if((my_directory_name==null)||(my_file_name==null)||(my_file_charset==null)) {
			debug_information.println("environment_scene_sub_directory_charset_mount:",
					"((my_directory_name==null)||(my_file_name==null)||(my_file_charset==null))");
			return false;
		}
		if((my_file_name=file_directory.replace_special_char(my_file_name)).length()<=0){
			debug_information.println("environment_scene_sub_directory_charset_mount:",
					"((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name))).length()<=0)");
			return false;
		}
		if((my_directory_name=ccp.sk.scene_par.scene_environment.search_change_name(my_directory_name.trim(),null))==null) {
			debug_information.println("environment_scene_sub_directory_charset_mount:",
					"((my_directory_name=get_environment(my_directory_name.trim()))==null)");
			return false;
		}
		if((my_directory_name=file_directory.replace_special_char(my_directory_name.trim())).length()<=0){
			debug_information.println("environment_scene_sub_directory_charset_mount:",
					"((my_directory_name=cut_string.do_cut(file_reader.separator(my_directory_name.trim()))).length()<=0)");
			return false;
		}
		
		if(my_directory_name.charAt(my_directory_name.length()-1)!=File.separatorChar)
			my_directory_name+=File.separatorChar;
		
		my_directory_name+=ccp.sk.scene_par.scene_sub_directory;
		fr.push_string(new String[]{my_directory_name+my_file_name,my_file_charset});

		return true;
	}
}
