package driver_movement;

import kernel_engine.client_information;

public class movement_edit_jason 
{
	public movement_edit_jason(long movement_tree_id,
			long switch_time_length,client_information ci,movement_manager manager)
	{
		if(manager.root_movement==null) {
			ci.request_response.println("null");
			return;
		}
		if(movement_tree_id<0) {
			String str;
			if((str=ci.request_response.get_parameter("id"))!=null)
				try{
					movement_tree_id=Integer.decode(str);
				}catch(Exception e){
					movement_tree_id=-1;
				}
			if(movement_tree_id<0)
				movement_tree_id=manager.root_movement.movement_tree_id;
		}
		movement_searcher searcher=new movement_searcher(manager.root_movement,movement_tree_id);
		if(searcher.search_link_list==null)
			if(manager.root_movement.movement_tree_id!=movement_tree_id) {
				movement_tree_id=manager.root_movement.movement_tree_id;
				searcher=new movement_searcher(manager.root_movement,movement_tree_id);
			}
		
		movement_jason.set_current_movement_flag(manager.root_movement,manager.parameter.current_movement_id,false);
		
		ci.request_response.println("{");

		ci.request_response.print  ("	\"buffer_number\"		:	",
			(manager.buffer_movement==null)?0:(manager.buffer_movement.length)).println(",");
		
		ci.request_response.println("	\"switch_time_length\"	:	",switch_time_length+",");
		
		if(searcher.search_link_list==null){
			ci.request_response.println("}");
			return;
		}

		movement_jason.create_tree_node_jason(
				searcher.search_link_list.tree_node, 
				ci.request_response, "\t",",",switch_time_length);
	
		if(searcher.search_link_list!=null)
			if(searcher.search_link_list.next!=null){
				ci.request_response.println("	\"parent\"	:	{");
				movement_jason.create_tree_node_jason(
						searcher.search_link_list.next.tree_node,
						ci.request_response, "\t\t","",switch_time_length);
				ci.request_response.println("	},");
			}

		ci.request_response.println("	\"children\"	:	[");
		if(searcher.search_link_list.tree_node!=null)
			if(searcher.search_link_list.tree_node.children!=null)
				for(int i=0,ni=searcher.search_link_list.tree_node.children.length;i<ni;i++){
					ci.request_response.println("		{");
					movement_jason.create_tree_node_jason(
							searcher.search_link_list.tree_node.children[i],
							ci.request_response,"\t\t\t","",switch_time_length);
					ci.request_response.println("		}",(i==(ni-1))?"":",");
				}
		ci.request_response.println("	]");

		ci.request_response.println("}");
	}
}
