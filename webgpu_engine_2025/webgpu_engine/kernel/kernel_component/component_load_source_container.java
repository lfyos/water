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
		var list=clsc.tree.get_tree_node_list(false,false);
		tree=new tree_string_search_container<component_load_source_item>();
		
		for(int i=0,ni=list.size();i<ni;i++){
			var list_item=list.get(i).list;
			for(int j=0,nj=list_item.size();j<nj;j++){
				var p=list_item.get(j);
				tree.add(
						new String[] {p.component_name},
						new component_load_source_item(
								p.component_name,		p.token_string,
								p.component_file_name,	p.component_file_charset
					));
			}
		}
	}
	public int add_component(String component_name,ArrayList<component>child_component_list,
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
			file_reader fr=new file_reader(clsi.component_file_name,clsi.component_file_charset);
			var p=new component(clsi.token_string,fr,part_list_flag,normalize_location_flag,ccp);
			child_component_list.add(p);
			ret_val++;
			fr.close();
		}
		return ret_val;
	}
	public void add_source_item(String component_name,
		String token_string,String component_file_name,String component_file_charset)
	{
		if((component_name!=null)&&(new File(component_file_name).exists())) {
			if(component_file_charset==null)
				component_file_charset=Charset.defaultCharset().name();
			tree.add(
					new String[] {component_name},
					new component_load_source_item(component_name,
							token_string,component_file_name,component_file_charset));
		}
	}
	public int get_source_item_number()
	{
		return tree.size();
	}
}
