package kernel_component;

import kernel_file_manager.file_reader;

public class component_core_0 
{
	public component_clip clip;
	public component_uniparameter 	uniparameter;
	public component_multiparameter multiparameter[];
	public component_initialization initialization;
	
	public void destroy()
	{
		clip=null;
		uniparameter=null;
		if(multiparameter!=null) {
			for(int i=0,ni=multiparameter.length;i<ni;i++)
				multiparameter[i]=null;
			multiparameter=null;
		}
		if(initialization!=null) {
			initialization.destroy();
			initialization=null;
		}
	}
	public component_core_0(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		clip=new component_clip();
		uniparameter=new component_uniparameter(
				fr.lastModified_time,normalize_location_flag,part_list_flag);
		multiparameter=new component_multiparameter[ccp.sk.scene_par.multiparameter_number];
		for(int i=0,ni=multiparameter.length;i<ni;i++)
			multiparameter[i]=new component_multiparameter(ccp.default_display_bitmap);
		initialization=new component_initialization();
	}
}
