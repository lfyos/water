package driver_movement;

import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.location;

public class movement_add_point extends movement_design_base
{
	public movement_add_point(engine_kernel ek,client_information ci,movement_manager manager)
	{
		super(ek,ci,manager);
		
		if(comp==null)
			return;
		
		String start_parameter[]=null;
		for(int i=0;;i++) {
			String str;
			if((str=ci.request_response.get_parameter("par_"+i))==null)
				break;
			try {
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
			}catch(Exception e) {
				continue;
			}
			if(start_parameter==null)
				start_parameter=new String[]{str};
			else {
				String bak[]=start_parameter;
				start_parameter=new String[bak.length+1];
				for(int j=0,nj=start_parameter.length-1;j<nj;j++)
					start_parameter[j]=bak[j];
				start_parameter[start_parameter.length-1]=str;
			}
		}
		movement_item_container mic=manager.designed_move.move;
		movement_item tmp[]=mic.movement;
		movement_item p=new movement_item(
			get_double(ci,"t",1000000000),start_parameter,comp.move_location,null,new location());
		
		if(tmp==null)
			mic.movement=new movement_item[1];
		else{
			mic.movement=new movement_item[mic.movement.length+1];
			for(int i=0;i<tmp.length;i++)
				mic.movement[i]=tmp[i];
		}
		mic.movement[mic.movement.length-1]=p;
		
		if(mic.movement.length>1){
			movement_item p0=mic.movement[mic.movement.length-2];
			movement_item p1=mic.movement[mic.movement.length-1];
			mic.movement[mic.movement.length-2]=new movement_item(p0.time_length,
				p0.start_parameter,p0.start_location,p1.start_parameter,p1.start_location);
		}
	}
}
