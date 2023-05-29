package driver_movement;

import kernel_component.component;
import kernel_engine.engine_kernel;
import kernel_transformation.point;
import kernel_engine.client_information;
import kernel_component.component_array;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_common_class.component_collector_jason_part;

public class movement_collector_compare
{
	private component_collector source_matched_component,target_matched_component;
	private component_collector unmatched_component,unnecessary_component;
	
	private double caculate_component_distances(component s,component t)
	{
		point s_point,t_point;
		
		if(s.model_box!=null)
			s_point=s.absolute_location.multiply(s.model_box.center());
		if(s.component_box!=null)
			s_point=s.component_box.center();
		else
			s_point=s.absolute_location.multiply(0, 0, 0);

		if(t.model_box!=null)
			t_point=t.absolute_location.multiply(t.model_box.center());
		if(t.component_box!=null)
			t_point=t.component_box.center();
		else
			t_point=t.absolute_location.multiply(0, 0, 0);
		
		return s_point.sub(t_point).distance2();
	}
	private void create_collector(component_collector source,component_collector target)
	{
		for(int render_id=0,render_number=source.component_collector.length;render_id<render_number;render_id++) {
			if(source.component_collector[render_id]==null)
				continue;
			for(int part_id=0,part_number=source.component_collector[render_id].length;part_id<part_number;part_id++){
				component_link_list source_cll= source.component_collector[render_id][part_id];
				component_link_list target_cll=null;
				if(target.component_collector[render_id]!=null)
					target_cll=target.component_collector[render_id][part_id];
				if(source_cll==null){
					for(component_link_list cll=target_cll;cll!=null;cll=cll.next_list_item)
						unnecessary_component.register_component(cll.comp,0);
					continue;
				}
				if(target_cll==null){
					for(component_link_list cll=source_cll;cll!=null;cll=cll.next_list_item)
						unmatched_component.register_component(cll.comp,0);
					continue;
				}
				
				component_array  source_array=new component_array();
				for(component_link_list cll=source_cll;cll!=null;cll=cll.next_list_item)
					source_array.add_component(cll.comp);
					
				component_array target_array=new component_array();
				for(component_link_list cll=target_cll;cll!=null;cll=cll.next_list_item)
					target_array.add_component(cll.comp);

				for(int i=0,ni=source_array.comp_list.size();;i++){
					if(i>=ni){
						for(int j=0,nj=target_array.comp_list.size();j<nj;j++)
							unnecessary_component.register_component(target_array.comp_list.get(j),0);
						break;
					}
					if(target_array.comp_list.size()<=0) {
						for(int j=i,nj=ni;j<nj;j++)
							unmatched_component.register_component(source_array.comp_list.get(j),0);
						break;
					}
					source_matched_component.register_component(source_array.comp_list.get(i),0);
					
					int match_id=0;
					double match_distance2=caculate_component_distances(
							source_array.comp_list.get(i),target_array.comp_list.get(0));
					for(int j=1,nj=target_array.comp_list.size();j<nj;j++) {
						double my_match_distance2=caculate_component_distances(
								source_array.comp_list.get(i),target_array.comp_list.get(j));
						if(match_distance2>my_match_distance2) {
							match_id=j;
							match_distance2=my_match_distance2;
						}
					}
					target_matched_component.register_component(target_array.comp_list.get(match_id),0);
					
					int last_target_id=target_array.comp_list.size()-1;
					target_array.comp_list.set(match_id,target_array.comp_list.get(last_target_id));
					target_array.comp_list.remove(last_target_id);
				};
			}
		}
	}
	public movement_collector_compare(client_information ci,engine_kernel ek,
			component_collector source,component_collector target,String pre_str,String follow_str)
	{
		source_matched_component=new component_collector(ek.render_cont.renders);
		target_matched_component=new component_collector(ek.render_cont.renders);
		unmatched_component		=new component_collector(ek.render_cont.renders);
		unnecessary_component	=new component_collector(ek.render_cont.renders);
		
		create_collector(source,target);
		
		ci.request_response.println(pre_str,"{");
		
		ci.request_response.println(pre_str,"	\"matched_collector\":");
		new component_collector_jason_part(pre_str+"		",false,false,false,true,
				source_matched_component,target_matched_component,ci,ek);
		
		ci.request_response.println(pre_str,"	\"unmatched_collector\":");
		new component_collector_jason_part(pre_str+"		",false,false,false,false,
				unmatched_component,null,ci,ek);
		
		ci.request_response.println(pre_str,"	\"unnecessary_collector\":");
		new component_collector_jason_part(pre_str+"		",false,true,false,false,
				unnecessary_component,null,ci,ek);

		ci.request_response.println(pre_str,"}"+follow_str);
	}
}
