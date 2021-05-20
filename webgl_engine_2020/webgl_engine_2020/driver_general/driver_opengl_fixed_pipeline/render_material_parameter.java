package driver_opengl_fixed_pipeline;

import kernel_file_manager.file_reader;

public class render_material_parameter
{
	public long last_modified_time;
	public String render_material_charset,render_directory_name,texture_directory_name;
	
	public double transparency_scale,emission_scale,texture_scale;
	public String top_box_part_material_file_name;
	public boolean do_clip_close_flag,part_clip_mode;
	
	public render_material_parameter(String file_name,String charset)
	{
		file_reader f=new file_reader(file_name,charset);
		last_modified_time=f.lastModified_time;
		
		render_material_charset			=f.get_charset();
		render_directory_name			=f.directory_name;
		texture_directory_name			=render_directory_name+f.get_string();
		
		transparency_scale				=f.get_double();
		emission_scale					=f.get_double();
		texture_scale					=f.get_double();
		
		top_box_part_material_file_name	=f.get_string();
		do_clip_close_flag				=f.get_boolean();
		part_clip_mode					=f.get_boolean();
		
		f.close();
	}
}
