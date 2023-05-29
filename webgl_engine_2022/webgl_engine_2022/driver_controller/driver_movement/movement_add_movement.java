package driver_movement;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class movement_add_movement extends movement_design_base
{
	public movement_add_movement(long switch_time_length,
			engine_kernel ek,client_information ci,movement_manager manager)
	{
		super(ek,ci,manager);

		if(comp==null)
			return;
	
		manager.designed_move.move.start_state_flag		=get_boolean(ci,"start",true);
		manager.designed_move.move.terminate_state_flag	=get_boolean(ci,"terminate",false);
		
		movement_item movement_bak[];
		if((movement_bak=manager.designed_move.move.movement).length<=1)
			return;
		manager.designed_move.move.movement=new movement_item[movement_bak.length-1];
		for(int i=0,ni=movement_bak.length-1;i<ni;i++)
			manager.designed_move.move.movement[i]=movement_bak[i];
		
		if(manager.root_movement==null)
			manager.root_movement=new movement_tree(manager.id_creator);

		movement_tree tree_bak[];
		if((tree_bak=manager.root_movement.children)==null)
			manager.root_movement.children=new movement_tree[] {manager.designed_move};
		else{
			manager.movement_start(ek.modifier_cont[manager.config_parameter.movement_modifier_container_id],
					manager.root_movement.movement_tree_id,ek.component_cont,true,switch_time_length);
			manager.root_movement.children=new movement_tree[tree_bak.length+1];
			
			if(manager.mount_direction_flag^get_boolean(ci,"place",true)){
				for(int i=0,ni=tree_bak.length;i<ni;i++)
					manager.root_movement.children[i+1]=tree_bak[i];
				manager.root_movement.children[0]=manager.designed_move;
			}else {
				for(int i=0,ni=tree_bak.length;i<ni;i++)
					manager.root_movement.children[i]=tree_bak[i];
				manager.root_movement.children[tree_bak.length]=manager.designed_move;
			}
		}
		manager.reset(manager.designed_move.movement_tree_id,
				ek.modifier_cont[manager.config_parameter.movement_modifier_container_id],
				ek.component_cont,switch_time_length);
	}
}