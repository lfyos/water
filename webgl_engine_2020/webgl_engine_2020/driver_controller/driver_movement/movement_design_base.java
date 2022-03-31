package driver_movement;

import kernel_component.component;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class movement_design_base 
{
	public double get_double(client_information ci,String str,double default_value)
	{	
		double ret_val=default_value;
		try{
			if((str=ci.request_response.get_parameter(str))!=null)
				ret_val=Double.parseDouble(str);
		}catch(Exception e){
			;
		}
		return ret_val;
	}
	public boolean get_boolean(client_information ci,String str,boolean default_value)
	{	
		try{
			if((str=ci.request_response.get_parameter(str))==null) 
				return default_value;
			return (str.compareTo("true")==0)?true:false;
		}catch(Exception e){
			return default_value;
		}
	}
	
	public component comp;
	
	public movement_design_base(engine_kernel ek,client_information ci,movement_manager manager)
	{
		String str;
		comp=null;
		do{
			if((str=ci.request_response.get_parameter("component_name"))==null)
				break;
			if(str.length()<=0)
				break;
			try {
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
			}catch(Exception e) {
				break;
			}
			comp=ek.component_cont.search_component(str);
		}while(false);
		
		if(comp==null)
			do{
				if((str=ci.request_response.get_parameter("component_id"))==null)
					break;
				if(str.length()<=0)
					break;
				try {
					comp=ek.component_cont.get_component(Integer.decode(str));
				}catch(Exception e) {
					break;
				}
			}while(false);
		
		if(comp==null)
			comp=ek.component_cont.search_component();
		
		if(ek.component_cont.root_component.component_id==comp.component_id) {
			comp=null;
			return;
		}
		if(manager.designed_move==null)
			manager.designed_move=new movement_tree(manager.id_creator);
		manager.designed_move.node_name			="no_node_name";
		manager.designed_move.sound_file_name	="no_sound_file";
		manager.designed_move.description		="no_description";
		manager.designed_move.sequence_flag		=true;
		
		if(manager.designed_move.move==null)
			manager.designed_move.move=new movement_item_container(comp);
		manager.designed_move.move.moved_component_id	=comp.component_id;
		manager.designed_move.move.moved_component_name	=comp.component_name;
		manager.designed_move.move.start_state_flag		=true;
		manager.designed_move.move.terminate_state_flag	=false;
		if(manager.designed_move.match==null)
			manager.designed_move.match					=new movement_match_container();
		manager.designed_move.children					=null;	
	}
}
