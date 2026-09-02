package kernel_render;

import java.util.ArrayList;

import kernel_common_class.debug_information;
import kernel_common_class.tree_string_search_container;

public class render_container_for_render_search 
{
	private tree_string_search_container<render> render_tree;
	
	public void destroy()
	{
		if(render_tree!=null) {
			render_tree.destroy();
			render_tree=null;
		}
	}
	public render search_render(String render_name)
	{
		ArrayList<render> render_list=render_tree.search_value_list(render_name);
		return (render_list==null)?null:render_list.get(0);
	}
	public render_container_for_render_search(ArrayList<render> my_render_list)
	{
		render_tree=new tree_string_search_container<render>(null);
		for(var my_render:my_render_list)
			render_tree.add(my_render.render_name,my_render);
		int number;
		for(var my_tree_node:render_tree.tree_get_node_collection())
			if((number=my_tree_node.list.size())>1)
				debug_information.println("Find same name render:	",
					my_tree_node.list.get(0).render_name+"	number:	"+number);
	}
}
