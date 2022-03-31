package driver_movement;

import kernel_camera.camera_parameter;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class movement_add_movement extends movement_design_base
{
	public movement_add_movement(engine_kernel ek,client_information ci,movement_manager manager)
	{
		super(ek,ci,manager);

		if(comp==null)
			return;
	
		manager.designed_move.move.start_state_flag		=get_boolean(ci,"start",true);
		manager.designed_move.move.terminate_state_flag	=get_boolean(ci,"terminate",false);
		
		movement_item bak[]=manager.designed_move.move.movement;
		if(bak.length<=1)
			return;
		manager.designed_move.move.movement=new movement_item[bak.length-1];
		for(int i=0,ni=bak.length-1;i<ni;i++)
			manager.designed_move.move.movement[i]=bak[i];

		camera_parameter cam_par=ci.display_camera_result.cam.parameter;
		long switch_time_length=cam_par.movement_flag?cam_par.switch_time_length:0;
		
		if(manager.root_movement==null)
			manager.root_movement=new movement_tree(manager.id_creator);

		if(manager.root_movement.children==null)
			manager.root_movement.children=new movement_tree[] {manager.designed_move};
		else{
			manager.movement_start(ek.modifier_cont[manager.config_parameter.movement_modifier_id],
				manager.root_movement.movement_tree_id,ek.component_cont,true,switch_time_length);
			movement_tree tmp[]=new movement_tree[manager.root_movement.children.length+1];
			for(int i=0;i<(manager.root_movement.children.length);i++)
				tmp[i+1]=manager.root_movement.children[i];
			tmp[0]=manager.designed_move;
			manager.root_movement.children=tmp;
		}
		manager.designed_move=null;
		manager.reset(ek.modifier_cont[manager.config_parameter.movement_modifier_id],
				ek.component_cont,switch_time_length);
	}
}
