package driver_movement;

import kernel_engine.client_information;

public class movement_edit_jason 
{
	public movement_edit_jason(long movement_tree_id,client_information ci,movement_manager manager)
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
		if(searcher.result==null)
			if(manager.root_movement.movement_tree_id!=movement_tree_id) {
				movement_tree_id=manager.root_movement.movement_tree_id;
				searcher=new movement_searcher(manager.root_movement,movement_tree_id);
			}
		
		movement_jason.set_current_movement_flag(manager.root_movement,manager.parameter.current_movement_id,false);
		
		ci.request_response.println("{");
		ci.request_response.print  ("	\"buffer_number\"		:	",
			(manager.buffer_movement==null)?0:(manager.buffer_movement.length)).println(",");
		
		if(searcher.result==null){
			ci.request_response.println("}");
			return;
		}

		movement_jason.create_tree_node_jason(searcher.result, ci.request_response, "\t",",");
	
		if(searcher.result_parent!=null) {
			ci.request_response.println("	\"parent\"	:	{");
			movement_jason.create_tree_node_jason(searcher.result_parent, ci.request_response, "\t\t","");
			ci.request_response.println("	},");
		}

		ci.request_response.println("	\"children\"	:	[");
		if(searcher.result_children!=null)
			for(int i=0,ni=searcher.result_children.length;i<ni;i++){
				ci.request_response.println("		{");
				movement_jason.create_tree_node_jason(searcher.result_children[i],ci.request_response,"\t\t\t","");
				ci.request_response.println("		}",(i==(ni-1))?"":",");
			}
		ci.request_response.println("	]");

		ci.request_response.println("}");
	}
}
