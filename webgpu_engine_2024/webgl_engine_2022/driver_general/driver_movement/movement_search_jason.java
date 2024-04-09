package driver_movement;

import kernel_common_class.jason_string;
import kernel_component.component;
import kernel_component.component_container;
import kernel_engine.client_information;

public class movement_search_jason 
{
	private String follow_str;
	private movement_tree display_stack[];
	private component comp;
	
	private void create_component_movement(movement_tree t,int depth_number,client_information ci)
	{
		if(t==null)
			return;
		if(depth_number>=display_stack.length){
			movement_tree new_display_stack[]=new movement_tree[depth_number+1];
			for(int i=0,n=display_stack.length;i<n;i++)
				new_display_stack[i]=display_stack[i];
			display_stack=new_display_stack;
		}
		display_stack[depth_number]=t;
		
		if(t.children!=null){
			for(int i=0,ni=t.children.length;i<ni;i++)
				create_component_movement(t.children[i],depth_number+1,ci);
			return;
		}
		if(t.move==null)
			return;
		
		int number=0;
		if(t.move.moved_component_id==comp.component_id)
			number++;
		if(t.move.follow_component_id!=null)
			for(int i=0,ni=t.move.follow_component_id.length;i<ni;i++)
				if(t.move.follow_component_id[i]==comp.component_id)
					number++;
		if(number<=0)
			return;
		String pre_str="	";
		for(int i=0;i<=depth_number;i++,pre_str+="	") {
			ci.request_response.println(follow_str);
			follow_str=",";
			ci.request_response.println(pre_str,"{");
			ci.request_response.print  (pre_str,"	\"movement_tree_id\"	:	").
				print(display_stack[i].movement_tree_id).println(",");
			ci.request_response.print  (pre_str,"	\"node_name\"		:	").
				print(jason_string.change_string(display_stack[i].node_name)).println(",");
			ci.request_response.print  (pre_str,"	\"depth_number\"		:	").println(i);
			ci.request_response.print  (pre_str,"}");
		}
	}
	public movement_search_jason(int my_movement_component_id,component my_initial_comp,
			component_container my_component_cont,movement_tree my_move_root,client_information ci)
	{
		follow_str="";
		display_stack=new movement_tree[0];
		ci.request_response.print  ("[");
		for(comp=my_initial_comp;comp!=null;comp=my_component_cont.get_component(comp.parent_component_id)) {
			long output_data_length=ci.request_response.output_data_length;
			create_component_movement(my_move_root,0,ci);
			if(ci.request_response.output_data_length>output_data_length)
				ci.request_response.println().println().println().println().println();
		}
		ci.request_response.println("]");
	}
}
