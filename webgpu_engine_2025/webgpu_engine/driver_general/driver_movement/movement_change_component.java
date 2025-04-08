package driver_movement;

import kernel_component.component;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class movement_change_component 
{
	public static long change_component(
			scene_kernel sk,client_information ci,movement_manager manager,long switch_time_length)
	{
		String str;
		component comp,old_comp;
		
		if(manager.root_movement==null)
			return manager.parameter.current_movement_id;
		if((str=ci.request_response.get_parameter("id"))==null)
			return manager.parameter.current_movement_id;
		movement_searcher searcher;
		if((searcher=new movement_searcher(manager.root_movement,Long.decode(str))).search_link_list==null)
			return manager.parameter.current_movement_id;
		if(searcher.search_link_list.tree_node.move==null)
			return manager.parameter.current_movement_id;

		if((str=ci.request_response.get_parameter("component_id"))!=null) {
			if((comp=sk.component_cont.get_component(Integer.decode(str)))==null)
				return manager.parameter.current_movement_id;
		}else if((str=ci.request_response.get_parameter("component_name"))!=null) {
			String request_charset=ci.request_response.implementor.get_request_charset();
			try {
				str=java.net.URLDecoder.decode(str,request_charset);
				str=java.net.URLDecoder.decode(str,request_charset);
			}catch(Exception e) {
				return manager.parameter.current_movement_id;
			}
			if((comp=sk.component_cont.search_component(str))==null)
				return manager.parameter.current_movement_id;
		}else if((comp=sk.component_cont.search_component())==null)
			return manager.parameter.current_movement_id;
		
		if(comp.component_id==sk.component_cont.root_component.component_id)
			return manager.parameter.current_movement_id;
		
		if((old_comp=sk.component_cont.get_component(searcher.search_link_list.tree_node.move.moved_component_id))!=null)
			old_comp.modify_display_flag(manager.move_channel_id.all_parameter_channel_id,true,sk.component_cont);
		
		searcher.search_link_list.tree_node.move.moved_component_id=comp.component_id;
		searcher.search_link_list.tree_node.move.moved_component_name=comp.component_name;
		
		manager.movement_start(sk.modifier_cont[manager.config_parameter.movement_modifier_container_id],
				searcher.search_link_list.tree_node.movement_tree_id,sk.component_cont,true,switch_time_length);
		
		return searcher.search_link_list.tree_node.movement_tree_id;
	}
}
