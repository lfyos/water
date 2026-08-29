package kernel_component;

import java.util.ArrayList;

import kernel_file_manager.file_reader;

public class component_core_0 
{
	public int		component_id,parent_component_id;
	public String	component_name,part_name; 
	
	public String	component_directory_name,component_file_name,component_charset;
	
	public component_uniparameter	uniparameter;
	
	public ArrayList<component> 	children;

	public void destroy()
	{
		component_name			=null;
		part_name				=null;
		component_directory_name=null;
		component_file_name		=null;
		component_charset		=null;

		uniparameter			=null;
		
		if(children!=null){
			for(component my_child:children)
				if(my_child!=null)
					my_child.destroy();
			children.clear();
		}
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

		uniparameter=new component_uniparameter(
			fr.lastModified_time,normalize_location_flag,part_list_flag);

		children					=new ArrayList<component>();
	}
}
