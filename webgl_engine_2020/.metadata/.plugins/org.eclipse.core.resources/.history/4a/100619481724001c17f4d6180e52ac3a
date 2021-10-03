package driver_water;


import kernel_driver.part_driver;
import kernel_driver.render_driver;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_parameter;


public class extended_render_driver extends render_driver 
{
	private int user_parameter_channel_id[];
	
	public extended_render_driver()
	{
		super(	"only_face.txt",
				"javascript.draw.txt",
				"vertex.shader.txt",
				"fragment.shader.txt",
				"geometry.shader.txt",
				"tess_control.shader.txt",
				"tess_evalue.shader.txt");
		
		user_parameter_channel_id=new int[] {};
	}
	public void destroy()
	{
		super.destroy();
		user_parameter_channel_id=null;
	}
	public String[] get_part_list(boolean giveup_part_load_flag,
			file_reader render_fr,String load_sub_directory_name,String par_list_file_name,
			part_parameter part_par,system_parameter system_par,client_request_response request_response)
	{
		String file_name=render_fr.directory_name+render_fr.get_string();
		file_reader f=new file_reader(file_name,render_fr.get_charset());
		
		user_parameter_channel_id=new int[f.get_int()];
		for(int i=0,ni=user_parameter_channel_id.length;i<ni;i++)
			user_parameter_channel_id[i]=f.get_int();

		f.close();
		
		return new String[] {render_fr.directory_name+par_list_file_name,render_fr.get_charset()};
	}
	public part_driver create_part_driver(file_reader part_fr,part p,
			system_parameter system_par,client_request_response request_response)
	{
		return new extended_part_driver(user_parameter_channel_id);
	}
}
