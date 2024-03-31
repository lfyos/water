package kernel_file_manager;

import java.io.File;

import kernel_part.part;
import kernel_engine.system_parameter;
import kernel_engine.scene_parameter;

public class file_directory 
{
	public static String part_file_directory(
		part p,system_parameter system_par,scene_parameter scene_par)
	{
		String part_directory=system_par.temporary_file_par.temporary_root_directory_name;
		
		switch(p.part_type_id){
		default:
		case 0:
			part_directory+="engine_part_directory"+File.separatorChar;
			break;
		case 1:
			part_directory+="type_part_directory"+File.separatorChar;
			break;
		case 2:
			part_directory =scene_par.scene_temporary_directory_name;
			part_directory+="scene_part_directory"+File.separatorChar;
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
	
	public static String package_file_directory(
		int part_type_id,system_parameter system_par,scene_parameter scene_par)
	{
		String package_directory=system_par.temporary_file_par.temporary_root_directory_name;
		switch(part_type_id){
		default:
		case 0:
			package_directory+="engine_package_directory"+File.separatorChar;
			break;
		case 1:
			if(scene_par.type_sub_directory.length()>0)
				package_directory+="type_package_directory"+File.separatorChar+scene_par.type_sub_directory;
			else {
				package_directory =scene_par.scene_temporary_directory_name;
				package_directory+="type_package_directory"+File.separatorChar;
			}
			break;
		case 2:
			package_directory =scene_par.scene_temporary_directory_name;
			package_directory+="scene_package_directory"+File.separatorChar;
			break;
		}
		return package_directory;
	}
}
