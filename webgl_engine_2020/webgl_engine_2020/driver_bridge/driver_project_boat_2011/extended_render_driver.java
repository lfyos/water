package driver_project_boat_2011;

import kernel_part.part;

import kernel_driver.part_driver;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_engine.system_parameter;

public class extended_render_driver extends driver_opengl_fixed_pipeline.extended_render_driver
{
	private String part_name[];
	private int part_number;
	
	public extended_render_driver()
	{
		super();
		part_name=new String[100];
		part_number=0;
	}
	public void destroy()
	{
		super.destroy();
		part_name=null;
	}
	public part_driver create_part_driver(file_reader part_fr,part p,
			system_parameter system_par,client_request_response request_response)
	{
		for(int i=0;i<part_number;i++)
			if(part_name[i].compareTo(p.system_name)==0)
				return null;
		
		if(part_name.length<=part_number){
			String bak_part_name[]=part_name;
			part_name=new String[part_number+100];
			for(int i=0,ni=bak_part_name.length;i<ni;i++)
				part_name[i]=bak_part_name[i];
		}
		part_name[part_number++]=p.system_name;
		return new extended_part_driver(light_par,render_material_par,null,null);
	}
}
