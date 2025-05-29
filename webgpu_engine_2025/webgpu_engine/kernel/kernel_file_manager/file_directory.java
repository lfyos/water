package kernel_file_manager;

import java.io.File;

import kernel_part.part;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_driver.component_driver;
import kernel_common_class.debug_information;

public class file_directory 
{
	public static String delete_separator(String directory)
	{
		if((directory=file_reader.separator(directory))==null)
			return "";
		for(int i=0,ni=(directory=directory.trim()).length();i<ni;i++)
			if(directory.charAt(i)!=File.separatorChar) {
				directory=directory.substring(i);
				break;
			}
		for(int i=directory.length()-1;i>=0;i--)
			if(directory.charAt(i)!=File.separatorChar) {
				directory=directory.substring(0,i+1);
				break;
			}
		return directory.trim();
	}
	public static String part_file_directory(part p,
			system_parameter system_par,scene_parameter scene_par)
	{
		String part_directory=system_par.temporary_file_par.temporary_root_directory_name;
		
		switch(p.part_type_id){
		case 0:
			part_directory+="system_part_directory"+File.separatorChar;
			break;
		case 1:
			part_directory =scene_par.scene_temporary_directory_name;
			part_directory+="scene_part_directory"+File.separatorChar;
			break;
		default:
			part_directory+="type_part_directory"+File.separatorChar;
			if(((p.part_type_id-2)>=0)&&((p.part_type_id-2)<scene_par.type_sub_directory.length))
				part_directory+=scene_par.type_sub_directory[p.part_type_id-2];	
			else{
				debug_information.println(
						"Find error part_type_id in part_file_directory	:	",p.part_type_id);
				part_directory+="error_directory_"+p.part_type_id
						+"_"+scene_par.type_sub_directory.length+File.separatorChar;
			}
			break;
		}
		part_directory+=file_reader.separator(p.part_par.part_type_string);
		if(part_directory.charAt(part_directory.length()-1)!=File.separatorChar)
			part_directory+=File.separatorChar;
		
		if(p.is_normal_part())
			part_directory	+="part_";
		else if(p.is_bottom_box_part())
			part_directory	+="part_bottom_box_";
		else if(p.is_top_box_part())
			part_directory	+="part_top_box_";
		else
			part_directory	+="part_unknown_";
		
		return part_directory+p.permanent_part_id+File.separator;
	}
	public static String package_file_directory(int part_type_id,
			system_parameter system_par,scene_parameter scene_par)
	{
		String package_directory;
		switch(part_type_id){
		case 0:
			package_directory=system_par.temporary_file_par.temporary_root_directory_name;
			package_directory+="system_package_directory"+File.separatorChar;
			break;
		case 1:
			package_directory =scene_par.scene_temporary_directory_name;
			package_directory+="scene_package_directory"+File.separatorChar;
			break;
		default:
			package_directory=system_par.temporary_file_par.temporary_root_directory_name;
			package_directory+="type_package_directory"+File.separatorChar;
			
			if(((part_type_id-2)>=0)&&((part_type_id-2)<scene_par.type_sub_directory.length))
				package_directory+=scene_par.type_sub_directory[part_type_id-2];
			else{
				package_directory+="error_package_directory_"+part_type_id;
				package_directory+="_"+scene_par.type_sub_directory.length;
				package_directory+=File.separatorChar;
			}
			break;
		}
		return package_directory;
	}
	public static String component_driver_temparatory_directory(
			component_driver comp_driver,system_parameter system_par,scene_parameter scene_par)
	{
		String temp_directory_name=file_directory.part_file_directory(
				comp_driver.component_part,system_par,scene_par)+"component_";
		String id_str=comp_driver.same_part_component_driver_id+File.separator;
		return temp_directory_name+id_str;
	}
}
