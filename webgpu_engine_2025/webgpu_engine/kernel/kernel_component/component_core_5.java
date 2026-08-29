package kernel_component;

import kernel_file_manager.file_reader;

public class component_core_5  extends component_core_4
{
	public long		render_touch_time;
	public boolean	pickup_component_family_flag;

	public component_clip clip;
	public component_initialization	initialization;
	public component_location_modification_locker location_modification_locker;
	
	public void destroy()
	{
		super.destroy();
		
		clip=null;
		
		if(initialization!=null){
			initialization.destroy();
			initialization=null;
		}

		location_modification_locker=null;
	}
	public component_core_5(String token_string,file_reader fr,
			boolean part_list_flag,boolean normalize_location_flag,
			component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		render_touch_time			=0;
		pickup_component_family_flag=false;

		clip						=new component_clip();
		initialization				=new component_initialization();
		location_modification_locker=new component_location_modification_locker();
	}
}
