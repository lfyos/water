package driver_movement;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class movement_delete_point extends movement_design_base
{
	public movement_delete_point(engine_kernel ek,client_information ci,movement_manager manager)
	{
		super(ek,ci,manager);
		
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
