package kernel_file_manager;

import java.io.File;

import kernel_part.part;
import kernel_engine.system_parameter;
import kernel_engine.scene_parameter;

public class file_directory 
{
	private static String render_root_directory(int part_type_id,
			system_parameter system_par,scene_parameter scene_par)
	{
		switch(part_type_id){
		default:
		case 0:
			return system_par.proxy_par.proxy_data_root_directory_name;
		case 1:
			return scene_par.type_proxy_directory_name;
		case 2:
			return scene_par.scene_proxy_directory_name;
		}
	}
	public static String render_file_directory(int part_type_id,
			int permanent_render_id,system_parameter system_par,scene_parameter scene_par)
	{
		return render_root_directory(part_type_id,system_par,scene_par)
				+"part_directory"+File.separator+"render_"+permanent_render_id+File.separator;
	}
	public static String part_file_directory(part p,system_parameter system_par,scene_parameter scene_par)
	{
		String part_directory=render_file_directory(p.part_type_id,p.permanent_render_id,system_par,scene_par);
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
	public static String extract_file_directory(int part_type_id,
			int permanent_render_id,int extract_id,system_parameter system_par,scene_parameter scene_par)
	{
		String part_directory=render_file_directory(part_type_id,permanent_render_id,system_par,scene_par);
		part_directory=part_directory.substring(system_par.proxy_par.proxy_data_root_directory_name.length());
		part_directory=system_par.proxy_par.extract_data_root_directory+part_directory;
		
		return part_directory+"extract_"+extract_id+File.separator;
	}
	public static String system_package_directory(
			int part_type_id,system_parameter system_par,scene_parameter scene_par)
	{
		return  render_root_directory(part_type_id,system_par,scene_par)+"package_directory"+File.separator;
	}
}
