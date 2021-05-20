package driver_opengl_fixed_pipeline;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.debug_information;

public class change_material
{
	private String processed_file_name[];
	
	private void process_one_file(String file_name,String charset)
	{
		debug_information.println(file_name);
		
		file_writer fw=new file_writer(file_name+".liufuyan.bak",charset);
		file_reader fr=new file_reader(file_name,charset);
				
		int material_number=fr.get_int();
		
		if(material_number<0)
			material_number=0;
		
		fw.println("/*	material number	*/  ",material_number);
		fw.println();

		for(int i=0;i<material_number;i++) {

			String caculate_color_method=fr.get_string();
			
			String par_0=fr.get_string();
			String par_1=fr.get_string();
			String par_2=fr.get_string();
			String par_3=fr.get_string();
			
			double ambient		=fr.get_double();
			double diffuse		=fr.get_double();
			double specular		=fr.get_double();
			double shininess	=fr.get_double();
			double emission		=fr.get_double();
			double transparency	=fr.get_double();

			String texture_file		=fr.get_string();

			if(fr.eof())
				break;
			
			double texture_move_x	=fr.get_double();
			double texture_move_y	=fr.get_double();
			double texture_alf		=fr.get_double();
			double texture_scale	=fr.get_double();
			
			fr.get_string();
			
			fw.println();
			
			fw.println("/*	Material NO.",i+"		*/");
			
			fw.println("/*	caculate_color_method										*/		",caculate_color_method);
			
			fw.println("/*	caculate_color_parameter									*/		",par_0+"\t\t"+par_1+"\t\t"+par_2+"\t\t"+par_3);
			
			fw.println("/*	ambient,diffuse,specular,shininess,emission,transparency	*/		",
					ambient+"\t\t"+diffuse+"\t\t"+specular+"\t\t"+shininess+"\t\t"+emission+"\t\t"+transparency);	
			
			fw.println("/*	texture file name											*/		",texture_file);
					
			fw.println("/*	texture_move_x,texture_move_y,texture_alf,texture_scale		*/		",
					texture_move_x+"\t\t"+texture_move_y+"\t\t"+texture_alf+"\t\t"+texture_scale);	
			
			fw.println();
		}
		
		fr.close();
		fw.close();
		
		file_writer.file_delete(fr.directory_name+fr.file_name);
		file_writer.file_rename(fw.directory_name+fw.file_name, fr.directory_name+fr.file_name);
	}
	
	public void process(String file_name)
	{
		for(file_reader render_list_f=new file_reader(file_name,null);;)
		{
			render_list_f.get_string();
			file_name=render_list_f.directory_name+render_list_f.get_string();
			render_list_f.get_string();
			if(render_list_f.eof()) {
				render_list_f.close();
				break;
			}
			render_list_f.get_string();
			
			for(file_reader part_list_f=new file_reader(file_name,render_list_f.get_charset());;) {
				part_list_f.get_string();
				part_list_f.get_string();
				part_list_f.get_string();
				file_name=part_list_f.directory_name+part_list_f.get_string();
				part_list_f.get_string();
				
				if(part_list_f.eof()) {
					part_list_f.close();
					break;
				}
				part_list_f.get_string();
				
				for(int i=0,ni=processed_file_name.length;i<ni;i++)
					if(processed_file_name[i].compareTo(file_name)==0) {
						file_name=null;
						break;
					}
				if(file_name!=null) {
					process_one_file(file_name,part_list_f.get_charset());
					debug_information.println(file_name);
				}
			}
		}
	}
	public change_material()
	{
		processed_file_name=new String[0];
	}
	public static void main(String args[])
	{ 
		System.out.println("Begin");
		
		change_material p=new change_material();
		
		p.process("J:\\water_all\\data\\project\\part\\bridge\\bridge\\render.list");
		p.process("J:\\water_all\\data\\project\\part\\other_part\\render.list");
		
        System.out.println("Done!");
    } 
}
