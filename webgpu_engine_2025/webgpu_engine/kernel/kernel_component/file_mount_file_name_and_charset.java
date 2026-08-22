package kernel_component;

import java.io.File;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_directory;
import kernel_common_class.debug_information;

public class file_mount_file_name_and_charset 
{
	public static boolean client_parameter_mount(file_reader fr,component_construction_parameter ccp)
	{
		String my_directory=fr.get_string();
		String my_file_name=fr.get_string();
		
		if((my_directory==null)||(my_file_name==null)) {
			debug_information.println("client_parameter_mount:	",
					"((my_directory==null)||(my_file_name==null))");
			return false;
		}
		if(((my_directory=my_directory.trim()).length()<=0)
			||((my_file_name=file_directory.replace_special_char(my_file_name)).length()<=0)) 
		{
			debug_information.println("client_parameter_mount:	",
					"(((my_directory=my_directory).length()<=0)||((my_file_name=my_file_name).length()<=0))");
			return false;
		}
		if((my_directory=ccp.sk.get_scene_environment_parameter(my_directory))==null) {
			debug_information.println("client_parameter_mount:	",
					"((my_directory=ccp.sk.get_client_parameter(my_directory))==null)");
			return false;
		}
		if((my_directory=file_directory.delete_begin_end_separator(my_directory)).length()<0) {
			debug_information.println("client_parameter_mount:	",
					"((my_directory=cut_string.do_cut(file_reader.separator(my_directory.trim()))).length()<0)");
			return false;
		}
		fr.push_string(new String[]{my_directory+File.separatorChar+my_file_name});
		return true;
	}
	public static boolean client_select_mount(file_reader fr,component_construction_parameter ccp)
	{
		String my_select_token		=fr.get_string();
		String my_select_file_name	=fr.get_string();
		String my_assemble_file_name=fr.get_string();
		
		if((my_select_token==null)||(my_select_file_name==null)||(my_assemble_file_name==null)) {
			debug_information.println("client_select_mount:",
				"((my_select_token==null)||(my_select_file_name==null)||(my_assemble_file_name==null))");
			return false;
		}
		my_select_token		 =file_directory.replace_special_char(my_select_token);
		my_select_file_name	 =file_directory.delete_begin_end_separator(my_select_file_name);
		my_assemble_file_name=file_directory.delete_begin_end_separator(my_assemble_file_name);

		if((my_select_token.length()<=0)||(my_select_file_name.length()<=0)||(my_assemble_file_name.length()<=0)){
			debug_information.println("client_select_mount:",
					"((my_select_token.length()<=0)||(my_select_file_name.length()<=0)||(my_assemble_file_name.length()<=0))");
			return false;
		}
		if((my_select_token=ccp.sk.get_scene_environment_parameter(my_select_token))==null) {
			debug_information.println("client_select_mount:",
					"((my_select_token=ccp.sk.get_client_parameter(my_select_token))==null)");
			return false;
		}
		
		file_reader f_select=new file_reader(fr.directory_name+my_select_file_name,fr.get_charset());
		
		debug_information.print  ("client_select_mount,select_token:	",my_select_token);
		debug_information.println("	file_name:	",f_select.directory_name+f_select.file_name);

		while(!(f_select.eof())){
			String f_select_token			=f_select.get_string();
			String f_select_directory_name	=f_select.get_string();
			if((f_select_token==null)||(f_select_directory_name==null))
				continue;
			f_select_token			=file_directory.replace_special_char(f_select_token);
			f_select_directory_name	=file_directory.delete_begin_end_separator(f_select_directory_name);
			if((f_select_token.length()<=0)||(f_select_directory_name.length()<=0))
				continue;
			if(my_select_token.compareTo(f_select_token)!=0)
				continue;
			my_assemble_file_name=f_select_directory_name+File.separatorChar+my_assemble_file_name;
			fr.push_string(new String[]{my_assemble_file_name});
			
			return true;
		}
		f_select.close();
		return false;
	}
	public static boolean environment_scene_sub_directory_mount(file_reader fr,component_construction_parameter ccp)
	{
		String my_directory_name=fr.get_string(),my_file_name=fr.get_string();
		if((my_directory_name==null) ||(my_file_name==null)){
			debug_information.println("environment_scene_sub_directory_mount:",
					"((my_directory_name==null) ||(my_file_name==null))");
			return false;
		}
		if((my_directory_name=ccp.sk.scene_par.scene_environment.search_change_name(my_directory_name.trim(),null))==null) {
			debug_information.println("environment_scene_sub_directory_mount:",
					"((my_directory_name=get_environment(my_directory_name.trim()))==null)");
			return false;
		}
		if((my_directory_name=file_directory.delete_begin_end_separator(my_directory_name.trim())).length()<=0) {
			debug_information.println("environment_scene_sub_directory_mount:",
					"((my_directory_name=cut_string.do_cut(file_reader.separator(my_directory_name.trim()))).length()<=0)");
			return false;
		}
		if((my_file_name=file_directory.delete_begin_end_separator(my_file_name.trim())).length()<=0) {
			debug_information.println("environment_scene_sub_directory_mount:",
					"((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name.trim()))).length()<=0)");
			return false;
		}
		my_file_name=my_directory_name+File.separatorChar+ccp.sk.scene_par.scene_sub_directory+my_file_name;
		fr.push_string(new String[] {my_file_name});

		return true;
	}
	public static boolean client_parameter_charset_mount(file_reader fr,component_construction_parameter ccp)
	{
		String my_directory_name=fr.get_string();
		String my_file_name		=fr.get_string();
		String my_file_charset	=fr.get_string();
		
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
		if((my_directory_name=file_directory.delete_begin_end_separator(my_directory_name))==null) {
			debug_information.println("client_parameter_charset_mount error",
					"((my_directory_name=cut_string.do_cut(file_reader.separator(my_directory_name)))==null)");
			return false;
		}
		if((my_file_name=file_directory.delete_begin_end_separator(my_file_name)).length()<=0){
			debug_information.println("client_parameter_charset_mount error",
					"((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name))).length()<=0)");
			return false;
		}
		fr.push_string(new String[]{my_directory_name+File.separatorChar+my_file_name,my_file_charset});
		
		return true;
	}
	public static boolean client_select_charset_mount(file_reader fr,component_construction_parameter ccp)
	{
		String select_token				=fr.get_string();
		String select_file_name			=fr.get_string();
		String select_assemble_file_name=fr.get_string();
		String select_file_charset		=fr.get_string();
		
		if((select_token==null)||(select_file_name==null)
			||(select_assemble_file_name==null)||(select_file_charset==null)) 
		{
			debug_information.println("client_select_charset_mount:",
				"((select_token==null)||(select_file_name==null)||(select_assemble_file_name==null))||(select_file_charset==null)");
			return false;
		}

		select_token				=file_directory.replace_special_char(select_token);
		select_file_name			=file_directory.delete_begin_end_separator(select_file_name);
		select_file_name			=file_directory.delete_begin_end_separator(select_file_name);
		select_assemble_file_name	=file_directory.delete_begin_end_separator(select_assemble_file_name);
		select_assemble_file_name	=file_directory.delete_begin_end_separator(select_assemble_file_name);
		select_file_charset			=file_directory.replace_special_char(select_file_charset);

		if((select_token.length()<=0)||(select_file_name.length()<=0)
			||(select_assemble_file_name.length()<=0)||(select_file_charset.length()<=0)) 
		{
			debug_information.println("client_select_charset_mount:",
				"((select_token.length()<=0)||(select_file_name.length()<=0)||(select_assemble_file_name.length()<=0)||(select_file_charset.length()<=0))");
			return false;
		}
		
		if((select_token=ccp.sk.get_scene_environment_parameter(select_token))==null){
			debug_information.println("client_select_charset_mount:",
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
			my_select_directory_name=file_directory.delete_begin_end_separator(my_select_directory_name);
			if((my_select_token.length()<=0)||(my_select_directory_name.length()<=0))
				continue;
			if(select_token.compareTo(my_select_token)!=0)
				continue;
			my_select_directory_name=fr.directory_name+my_select_directory_name;
			fr.push_string(new String[]{
					my_select_directory_name+File.separatorChar+select_assemble_file_name,
					select_file_charset});
			return true;
		}
		f_select.close();
		return false;
	}
	
	public static boolean environment_scene_sub_directory_charset_mount(
			file_reader fr,component_construction_parameter ccp)
	{
		String my_directory_name=fr.get_string();
		String my_file_name		=fr.get_string();
		String my_file_charset	=fr.get_string();
		
		if((my_directory_name==null)||(my_file_name==null)||(my_file_charset==null)) {
			debug_information.println("environment_scene_sub_directory_charset_mount:",
					"((my_directory_name==null)||(my_file_name==null)||(my_file_charset==null))");
			return false;
		}
		if((my_file_name=file_directory.delete_begin_end_separator(my_file_name)).length()<=0){
			debug_information.println("environment_scene_sub_directory_charset_mount:",
					"((my_file_name=cut_string.do_cut(file_reader.separator(my_file_name))).length()<=0)");
			return false;
		}
		if((my_directory_name=ccp.sk.scene_par.scene_environment.search_change_name(my_directory_name.trim(),null))==null) {
			debug_information.println("environment_scene_sub_directory_charset_mount:",
					"((my_directory_name=get_environment(my_directory_name.trim()))==null)");
			return false;
		}
		if((my_directory_name=file_directory.delete_begin_end_separator(my_directory_name.trim())).length()<=0){
			debug_information.println("environment_scene_sub_directory_charset_mount:",
					"((my_directory_name=cut_string.do_cut(file_reader.separator(my_directory_name.trim()))).length()<=0)");
			return false;
		}
		
		my_directory_name+=File.separatorChar+ccp.sk.scene_par.scene_sub_directory;
		fr.push_string(new String[]{my_directory_name+my_file_name,my_file_charset});
		
		return true;
	}
}
