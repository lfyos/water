package driver_movement;

import kernel_file_manager.file_reader;

public class movement_channel_id
{
	public int display_parameter_channel_id[];
	public int hide_parameter_channel_id[];
	
	public int all_parameter_channel_id[];

	public movement_channel_id(file_reader f)
	{
		display_parameter_channel_id=new int[f.get_int()];
		for(int i=0,ni=display_parameter_channel_id.length;i<ni;i++)
			display_parameter_channel_id[i]=f.get_int();
		
		hide_parameter_channel_id=new int[f.get_int()];
		for(int i=0,ni=hide_parameter_channel_id.length;i<ni;i++)
			hide_parameter_channel_id[i]=f.get_int();
		
		all_parameter_channel_id=new int[display_parameter_channel_id.length+hide_parameter_channel_id.length];
		for(int i=0,ni=display_parameter_channel_id.length;i<ni;i++)
			all_parameter_channel_id[i]=display_parameter_channel_id[i];
		for(int i=0,j=display_parameter_channel_id.length,ni=hide_parameter_channel_id.length;i<ni;i++,j++)
			all_parameter_channel_id[j]=hide_parameter_channel_id[i];
	}
}
