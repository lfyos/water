package driver_opengl_fixed_pipeline;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class light_one_parameter
{
	public int camera_id;
	
	public int light_type;
	
	public double red,green,blue;
	public double direction_x,direction_y,direction_z;
	public double ambient,diffuse,specular,shininess;
	public double k0,k1,k2;
	public double spot_angle,spot_exponent;
	
	public light_one_parameter(file_reader fr)
	{
		camera_id		=fr.get_int();
		
		light_type		=fr.get_int();
		
		red				=fr.get_double();
		green			=fr.get_double();
		blue			=fr.get_double();
		
		direction_x		=fr.get_double();
		direction_y		=fr.get_double();
		direction_z		=fr.get_double();
		
		ambient			=fr.get_double();
		diffuse			=fr.get_double();
		specular		=fr.get_double();
		shininess		=fr.get_double();
		
		k0				=fr.get_double();
		k1				=fr.get_double();
		k2				=fr.get_double();
		
		spot_angle		=fr.get_double();
		spot_exponent	=fr.get_double();
	}
	
	public void create_one_light_in_part_head(file_writer fw)
	{
		fw.println("				{");
		
		fw.print  ("					\"light_type\"	:	",	light_type);
		fw.println(",");
		
		fw.print  ("					\"camera_id\"		:	",	camera_id);
		fw.println(",");
		
		fw.print  ("					\"parameter\"		:	[");
		
		fw.print  (			light_type);
		
		fw.print  (",\t",	red);
		fw.print  (",",		green);
		fw.print  (",",		blue);
		
		fw.print  (",\t",	direction_x);
		fw.print  (",",		direction_y);
		fw.print  (",",		direction_z);
		
		fw.print  (",\t",	ambient);
		fw.print  (",",		diffuse);
		fw.print  (",",		specular);
		fw.print  (",",		shininess);
		
		fw.print  (",\t",	k0);
		fw.print  (",",		k1);
		fw.print  (",",		k2);

		fw.print  (",\t",	Math.cos(Math.PI*spot_angle/180.0));
		fw.print  (",",		spot_exponent);
		
		fw.println("]");
		
		fw.print  ("				}");
	}
}
