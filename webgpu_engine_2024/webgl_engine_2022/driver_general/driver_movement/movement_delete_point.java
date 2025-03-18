package driver_movement;

import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class movement_delete_point extends movement_design_base
{
	public movement_delete_point(scene_kernel sk,client_information ci,movement_manager manager)
	{
		super(sk,ci,manager);
		
		if(comp==null)
			return;
		
		movement_item_container mic=manager.designed_move.move;
		if(mic.movement==null)
			return;
		if(mic.movement.length<=1){
			mic.movement=null;
			return;
		}
		movement_item tmp[]=new movement_item[mic.movement.length-1];
		for(int i=0,ni=tmp.length;i<ni;i++)
			tmp[i]=mic.movement[i];
		mic.movement=tmp;
	}
}
