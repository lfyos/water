package driver_opengl_fixed_pipeline;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class light_parameter
{
	public long last_modified_time;
	
	private double global_ambient[];
	private light_one_parameter light[];

	public light_parameter(String file_name,String charset)
	{
		file_reader f=new file_reader(file_name,charset);
		last_modified_time=f.lastModified_time;
		
		global_ambient=new double[] {f.get_double(),f.get_double(),f.get_double()};
		
		int light_number=f.get_int();
		if(light_number<0)
			light_number=0;
		
		light=new light_one_parameter[light_number];
		for(int i=0;i<light_number;i++)
			light[i]=new light_one_parameter(f);

		f.close();
	}
	public void create_light_in_part_head(file_writer fw)
	{
		fw.print  ("			\"global_ambient\"		:	[",global_ambient[0]);
		fw.print  (",",global_ambient[1]);
		fw.print  (",",global_ambient[2]);
		fw.println(",1.0],");
		fw.println();
		
		fw.println("			\"light\"					:	[");
		for(int i=0,ni=light.length;i<ni;i++) {
			light[i].create_one_light_in_part_head(fw);
			if(i<(ni-1))
				fw.println(",");
			else
				fw.println();
		}
		fw.println("			],");
	}
}
