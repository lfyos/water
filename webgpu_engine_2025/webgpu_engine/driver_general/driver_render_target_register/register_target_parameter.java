package driver_render_target_register;

import java.util.ArrayList;

import kernel_file_manager.file_reader;

public class register_target_parameter 
{
	public String	render_target_name;
	public int		camera_id,parameter_channel_id,canvas_id;
	public String	load_operation,store_operation;
	public double	target_x0,target_y0,target_width,target_height;
	public double	adjust_eye_position_parameter[],clear_color[];
	
	private register_target_parameter()
	{
		adjust_eye_position_parameter=new double[]{0,0,0,0,0,0};
		clear_color=new double[] {0,0,0,1};
	}
	private static register_target_parameter load_parameter(file_reader fr)
	{
		register_target_parameter rtp=new register_target_parameter();
		
		rtp.render_target_name	=fr.get_string();
		if(fr.eof()||rtp.render_target_name==null)
			return null;
		rtp.render_target_name=rtp.render_target_name.trim();
		if(rtp.render_target_name.compareTo("")==0)
			return null;
		
		rtp.camera_id			=fr.get_int();
		if(fr.eof())
			return null;
		rtp.parameter_channel_id=fr.get_int();
		if(fr.eof())
			return null;
		rtp.canvas_id			=fr.get_int();
		if(fr.eof())
			return null;
		rtp.load_operation		=fr.get_string();
		if(fr.eof())
			return null;
		rtp.store_operation		=fr.get_string();
		if(fr.eof())
			return null;
		rtp.target_x0			=fr.get_double();
		if(fr.eof())
			return null;
		rtp.target_y0			=fr.get_double();
		if(fr.eof())
			return null;
		rtp.target_width		=fr.get_double();
		
		if(fr.eof())
			return null;
		
		rtp.target_height=fr.get_double();

		return rtp;
	}
	public static ArrayList<register_target_parameter> load_parameter(String file_name,String file_charset)
	{
		ArrayList<register_target_parameter> rtp_list=new ArrayList<register_target_parameter>();
		file_reader fr=new file_reader(file_name,file_charset);
		for(register_target_parameter rtp;(rtp=register_target_parameter.load_parameter(fr))!=null;)
			rtp_list.add(rtp_list.size(),rtp);
		fr.close();
		return rtp_list;
	}
}
