package kernel_component;

import kernel_transformation.location;
import kernel_file_manager.file_reader;
import kernel_transformation.input_location;

public class component_core_0 
{
	public int		component_id,parent_component_id;
	public String	component_name,part_name; 
	
	public String	component_directory_name,component_file_name,component_charset;
	
	public boolean	pickup_component_family_flag;
	public long		render_touch_time;

	public component_clip							clip;
	public component_uniparameter					uniparameter;
	public component_multiparameter					multiparameter[];
	public component_initialization					initialization;
	public component_location_modification_locker	location_modification_locker;
	
	public location relative_location;

	public void destroy()
	{
		component_name			=null;
		part_name				=null;
		component_directory_name=null;
		component_file_name		=null;
		component_charset		=null;

		clip					=null;
		uniparameter			=null;
		multiparameter			=null;
		
		if(initialization!=null){
			initialization.destroy();
			initialization=null;
		}
		location_modification_locker=null;
		
		relative_location=null;
	}
	public component_core_0(String token_string,file_reader fr,
			boolean part_list_flag,boolean normalize_location_flag,
			component_construction_parameter ccp)
	{
		component_id		=-1;
		parent_component_id	=-1;

		if((component_name=fr.get_string())==null)
			component_name=new String(token_string);
		else
			component_name=token_string+component_name;

		if((part_name=fr.get_string())==null)
			part_name="";

		component_directory_name	=fr.directory_name;
		component_file_name			=fr.file_name;
		component_charset			=fr.get_charset();
		
		pickup_component_family_flag=false;
		render_touch_time			=0;
		
		clip		=new component_clip();
		uniparameter=new component_uniparameter(
			fr.lastModified_time,normalize_location_flag,part_list_flag);
		long display_bitmap=ccp.sk.scene_par.default_display_bitmap;
		int number=ccp.sk.scene_par.multiparameter_number;
		multiparameter=new component_multiparameter[number];
		for(int i=0;i<number;i++)
			multiparameter[i]		=new component_multiparameter(display_bitmap);
		initialization				=new component_initialization();
		location_modification_locker=new component_location_modification_locker();
		
		relative_location=input_location.do_input(
			fr,ccp.request_response,ccp.sk.system_par,ccp.sk.scene_par);
		if(uniparameter.normalize_location_flag)
			relative_location=relative_location.normalize();
	}
}
