package kernel_component;

import java.io.File;
import java.util.ArrayList;
import java.nio.charset.Charset;
import kernel_file_manager.file_reader;
import kernel_common_class.tree_string_search_container;

public class component_load_source_container 
{
	private tree_string_search_container<component_load_source_item> tree;
	
	public void destroy()
	{
		tree=null;
	}
	public component_load_source_container()
	{
		tree=new tree_string_search_container<component_load_source_item>();
	}
	public component_load_source_container(component_load_source_container clsc)
	{
		tree=new tree_string_search_container<component_load_source_item>();
		ArrayList<component_load_source_item>list=clsc.tree.tree_get_value_list();
		for(int i=0,ni=list.size();i<ni;i++){
			component_load_source_item list_item=list.get(i);
			tree.add(
					new String[] {list_item.component_name},
					new component_load_source_item(list_item));
		}
	}
	private void set_component_last_time(component my_comp,long my_file_last_modified_time)
	{
		my_comp.uniparameter.file_last_modified_time=my_file_last_modified_time;
		for(int i=0,ni=my_comp.children.size();i<ni;i++)
			set_component_last_time(my_comp.children.get(i),my_file_last_modified_time);
		return;
	}
	public int add_component(file_reader component_fr,
		String component_name,ArrayList<component>child_component_list,
		boolean part_list_flag,boolean normalize_location_flag,component_construction_parameter ccp)
	{
		int ret_val=0;
		var list=tree.remove(new String[]{component_name});
		if(list==null)
			return ret_val;
		for(int i=0,ni=list.size();i<ni;i++) {
			var clsi=list.get(i);
			if(clsi==null)
				continue;

			ret_val++;
			if(clsi.create_component_data==null) {
				file_reader fr=new file_reader(clsi.component_file_name,clsi.component_file_charset);
				child_component_list.add(new component(
						clsi.token_string,fr,part_list_flag,normalize_location_flag,ccp));
				fr.close();
			}else {
				component_fr.push_string(clsi.create_component_data);
				component my_comp=new component(clsi.token_string,component_fr,
						part_list_flag,normalize_location_flag,ccp);
				set_component_last_time(my_comp,clsi.component_last_time);
				child_component_list.add(my_comp);
			}
		}
		return ret_val;
	}
	public int file_add_source_item(String component_name,String token_string,
			String component_file_name,String component_file_charset)
	{
		if((component_name!=null)&&(token_string!=null))
			if(new File(component_file_name).exists()) {
				if(component_file_charset==null)
					component_file_charset=Charset.defaultCharset().name();
				tree.add(new String[] {component_name},new component_load_source_item(
						component_name,token_string,component_file_name,component_file_charset));
				return 1;
			}
		return 0;
	}
	public int list_add_source_item(String component_name,String token_string,
			ArrayList<String> create_component_data,long component_last_time)
	{
		if((component_name!=null)&&(token_string!=null)&&(create_component_data!=null)) {
			tree.add(new String[] {component_name},new component_load_source_item(
					component_name,token_string,create_component_data,component_last_time));
			return create_component_data.size();
		}
		return 0;
	}
	
	public int register_data_component(
			file_reader part_fr,String mount_component_name,String token_string)
	{
		ArrayList<String> component_parameter=new ArrayList<String>();
		for(String str,terminated_token_string=part_fr.get_string();;) {
			if(part_fr.eof())
				break;
			if((str=part_fr.get_string())==null)
				continue;
			if(terminated_token_string.compareTo(str)==0)
				break;
			component_parameter.add(str);
		}
		if(component_parameter.size()<=0)
			return 0;
		return list_add_source_item(mount_component_name,token_string,
				component_parameter,part_fr.lastModified_time);
	}
	public int register_file_component(
			file_reader part_fr,String mount_component_name,String token_string)
	{
		int ret_val=0;
		for(String str,mount_component_file_name,terminated_token_string=part_fr.get_string();;) {
			if(part_fr.eof())
				break;
			if((str=part_fr.get_string())==null) 
				continue;
			if(terminated_token_string.compareTo(str)==0)
				break;
			mount_component_file_name=part_fr.directory_name+file_reader.separator(str);
			if(!(new File(mount_component_file_name).exists()))
				continue;
			ret_val+=file_add_source_item(mount_component_name,token_string,
					mount_component_file_name,part_fr.get_charset());
		}
		return ret_val;
	}
	public int register_component(file_reader f,
			String load_assemble_type,String default_system_mount_component_name)
	{
		switch(load_assemble_type) {
		case "data_to_system":
			return register_data_component(f,default_system_mount_component_name,"");
		case "file_to_system":
			return register_file_component(f,default_system_mount_component_name,"");
		case "data_to_component":	
			return register_data_component(f,f.get_string(),"");
		case "file_to_component":
			return register_file_component(f,f.get_string(),"");
		case "data_to_system_with_token":
			return register_data_component(f,default_system_mount_component_name,f.get_string());
		case "file_to_system_with_token":
			return register_file_component(f,default_system_mount_component_name,f.get_string());
		case "data_to_component_with_token":	
			return register_data_component(f,f.get_string(),f.get_string());
		case "file_to_component_with_token":
			return register_file_component(f,f.get_string(),f.get_string());
		default:
			return 0;
		}
	}
	public int get_source_item_number()
	{
		return tree.size();
	}
}
