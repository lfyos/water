package kernel_component;

import java.io.File;
import java.util.ArrayList;

import kernel_driver.component_driver;
import kernel_file_manager.file_reader;

public class file_mount_switch 
{
	public static void switch_file_mount(String mount_type,
			String component_name,ArrayList<component_driver> driver_array,
			ArrayList<component> children,component_uniparameter uniparameter,
			String token_string,file_reader fr,component_construction_parameter ccp)
	{
		switch(mount_type) {
		default:
			return;
		case "component_mount":
			ccp.clsc.add_source_item(fr.get_string(),token_string, 
					fr.directory_name+file_reader.separator(fr.get_string()),fr.get_charset());
			return;
		case "charset_component_mount":
			ccp.clsc.add_source_item(fr.get_string(),token_string, 
					fr.directory_name+file_reader.separator(fr.get_string()),fr.get_string());
			return;
		case "absulate_component_mount":
			ccp.clsc.add_source_item(fr.get_string(),token_string, 
					file_reader.separator(fr.get_string()),fr.get_charset());
			return;
		case "absulate_charset_component_mount":
			ccp.clsc.add_source_item(fr.get_string(),token_string, 
					file_reader.separator(fr.get_string()),fr.get_string());
			return;
		case "environment_component_mount":	
		{
			String add_component_name=fr.get_string();
			String add_file_name=file_reader.separator(
					ccp.sk.system_par.scene_environment.search_change_name(fr.get_string(),null));
			if(add_file_name.charAt(add_file_name.length()-1)!=File.separatorChar)
				add_file_name+=File.separatorChar;
			ccp.clsc.add_source_item(
					add_component_name,token_string,add_file_name,fr.get_charset());
			return;
		}
		case "environment_charset_component_mount":	
		{
			String add_component_name=fr.get_string();
			String add_file_name=file_reader.separator(
					ccp.sk.system_par.scene_environment.search_change_name(fr.get_string(),null));
			if(add_file_name.charAt(add_file_name.length()-1)!=File.separatorChar)
				add_file_name+=File.separatorChar;
			ccp.clsc.add_source_item(
					add_component_name,token_string,add_file_name,fr.get_string());
			return;
		}
		case "part_driver_mount":
			file_mount_component.part_driver_mount(component_name,driver_array,fr,token_string,
				uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
			return;
		case "external_part_driver_mount":
			file_mount_component.external_part_driver_mount(component_name,fr,token_string,
				uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
			return;
		case "mount":
			file_mount_component.file_mount(component_name,fr,false,token_string,
				uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
			return;
		case "charset_mount":
			file_mount_component.charset_file_mount(component_name,fr,false,token_string,
				uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
			return;
		case "client_select_mount":
			if(file_mount_file_name_and_charset.client_select_mount(fr,ccp))
				file_mount_component.file_mount(component_name,fr,false,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
			return;
		case "client_select_charset_mount":
			if(file_mount_file_name_and_charset.client_select_charset_mount(fr,ccp))
				file_mount_component.charset_file_mount(component_name,fr,true,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
			return;
		case "client_parameter_mount":
			if(file_mount_file_name_and_charset.client_parameter_mount(fr,ccp))
				file_mount_component.file_mount(component_name,fr,false,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
			return;
		case "client_parameter_charset_mount":
			if(file_mount_file_name_and_charset.client_parameter_charset_mount(fr,ccp))
				file_mount_component.charset_file_mount(component_name,fr,false,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
			return;
		case "environment_scene_sub_directory_mount":
			if(file_mount_file_name_and_charset.environment_scene_sub_directory_mount(fr,ccp))
				file_mount_component.file_mount(component_name,fr,true,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
			return;
		case "environment_scene_sub_directory_charset_mount":
			if(file_mount_file_name_and_charset.environment_scene_sub_directory_charset_mount(fr,ccp))
				file_mount_component.charset_file_mount(component_name,fr,true,token_string,
					uniparameter.part_list_flag,uniparameter.normalize_location_flag,children,ccp);
			return;
		}
	}
}
