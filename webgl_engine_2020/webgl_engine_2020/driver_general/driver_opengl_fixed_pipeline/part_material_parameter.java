package driver_opengl_fixed_pipeline;

import java.io.File;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.debug_information;
import kernel_common_class.jason_string;

public class part_material_parameter
{
	public int render_method;
	public double parameter[];

	public double ambient,diffuse,specular,shininess;
	public double emission,transparency;
	
	public String directory_name,texture_file_name;
	public double texture_move_x,texture_move_y,texture_alf,texture_scale;
	
	private part_material_parameter(file_reader fr,String texture_directory_name)
	{
		render_method			=fr.get_int();
		parameter				=new double[]{fr.get_double(),fr.get_double(),fr.get_double(),fr.get_double()};

		ambient					=fr.get_double();
		diffuse					=fr.get_double();
		specular				=fr.get_double();
		shininess				=fr.get_double();
		transparency			=fr.get_double();
		emission				=fr.get_double();
		
		directory_name=fr.directory_name;
		
		switch((texture_file_name=fr.get_string()).toLowerCase()) {
		case "notexture":
		case "√ª”–Œ∆¿Ì":
			texture_file_name=null;
			break;
		default:
			if((new File(fr.directory_name+texture_file_name)).exists())
				break;
			if((new File(texture_directory_name+texture_file_name)).exists())
				break;
			
			debug_information.println();
			debug_information.println("No texture picture file:");
			debug_information.println("\t\t",fr.directory_name+texture_file_name);
			debug_information.println("\t\t",texture_directory_name+texture_file_name);
			
			texture_file_name=null;
			break;
		}
		texture_move_x			=fr.get_double();
		texture_move_y			=fr.get_double();
		texture_alf				=fr.get_double();
		texture_scale			=fr.get_double();
		
		return;
	}
	public static part_material_parameter[]load_material_parameter(
			String file_name,String texture_directory_name,String charset)
	{
		file_reader f=new file_reader(file_name,charset);
		int material_number=f.get_int();
		if(material_number<0)
			material_number=0;
		
		part_material_parameter ret_val[]=new part_material_parameter[material_number];
		for(int i=0;i<material_number;i++)
			ret_val[i]=new part_material_parameter(f,texture_directory_name);
		f.close();
		
		return ret_val;
	}
	private void create_one_material_in_part_head(file_writer fw,
			render_material_parameter render_material)
	{
		fw.println("				{");

		fw.println("					\"do_clip_close\"	:	",
				render_material.do_clip_close_flag?"true,":"false,");
		
		fw.print  ("					\"texture_file\"	:	");
		if(texture_file_name==null)
			fw.println("null,");
		else
			fw.println(jason_string.change_string(texture_file_name),",");

		fw.print  ("					\"parameter\"		:	[",	render_method);
		
		fw.print  (",\t",	parameter[0]);
		fw.print  (",",		parameter[1]);
		fw.print  (",",		parameter[2]);
		fw.print  (",",		parameter[3]);

		fw.print  (",\t",	ambient);
		fw.print  (",",		diffuse);
		fw.print  (",",		specular);
		fw.print  (",",		shininess);

		fw.print  (",\t",	render_material.transparency_scale*transparency);
		fw.print  (",",		render_material.emission_scale*emission);
		
		fw.print  (",\t",	texture_move_x);
		fw.print  (",",		texture_move_y);
		fw.print  (",",		render_material.texture_scale*Math.cos(Math.PI*texture_alf/180.0)*texture_scale);
		fw.print  (",",		render_material.texture_scale*Math.sin(Math.PI*texture_alf/180.0)*texture_scale);
		
		fw.print  (",\t",	render_material.part_clip_mode?1:-1);
		
		fw.println("]");
		
		fw.print  ("				}");
	}
	public static void create_material_in_part_head(
			file_writer fw,part_material_parameter m[],render_material_parameter render_material)
	{
		boolean transparency_flag=false;
		
		fw.println("			\"material\"				:	[");
		for(int i=0,ni=m.length;i<ni;i++) {
			if(render_material.transparency_scale*m[i].transparency>0.0)
				transparency_flag=true;
			m[i].create_one_material_in_part_head(fw,render_material);
			if(i<(ni-1))
				fw.println(",");
			else
				fw.println();
			
		}
		fw.println("			],");
		fw.println("			\"transparency_flag\"		:	",transparency_flag?"true":"false");		
	}
}
