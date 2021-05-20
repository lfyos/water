package driver_movement;

import kernel_common_class.common_reader;
import kernel_common_class.class_file_reader;

public class movement_channel_id
{
	public int display_parameter_channel_id[];
	public int hide_parameter_channel_id[];
	public int box_parameter_channel_id;
	
	public int all_parameter_channel_id[];
	
	public int movement_modifier_id;

	public movement_channel_id()
	{
		common_reader f=class_file_reader.get_reader("channel_id.txt",movement_channel_id.class);
		
		display_parameter_channel_id=new int[f.get_int()];
		for(int i=0,ni=display_parameter_channel_id.length;i<ni;i++)
			display_parameter_channel_id[i]=f.get_int();
		
		hide_parameter_channel_id=new int[f.get_int()];
		for(int i=0,ni=hide_parameter_channel_id.length;i<ni;i++)
			hide_parameter_channel_id[i]=f.get_int();
		
		box_parameter_channel_id=f.get_int();

		movement_modifier_id=f.get_int();
		
		f.close();
		
		all_parameter_channel_id=new int[display_parameter_channel_id.length+hide_parameter_channel_id.length];
		int j=0;
		for(int i=0,ni=display_parameter_channel_id.length;i<ni;i++)
			all_parameter_channel_id[j++]=display_parameter_channel_id[i];
		for(int i=0,ni=hide_parameter_channel_id.length;i<ni;i++)
			all_parameter_channel_id[j++]=hide_parameter_channel_id[i];
	}
}
