package kernel_common_class;

import kernel_part.part;
import kernel_component.component_collector;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_component.component_link_list;
import kernel_transformation.box;
import kernel_transformation.point;
import kernel_transformation.location;

public class component_collector_jason_part 
{
	private String system_pre_str;
	
	private void print(String pre_str,client_information ci,String str)
	{
		ci.request_response.print  (system_pre_str);
		ci.request_response.println(pre_str,str);
	};
	private void print(String pre_str,client_information ci,String str1,String str2,String str3)
	{
		ci.request_response.print  (system_pre_str);
		ci.request_response.print  (pre_str,str1);
		ci.request_response.println(str2,str3);
	};
	private void print(String pre_str,client_information ci,String str1,int str2,String str3)
	{
		ci.request_response.print  (system_pre_str);
		ci.request_response.print  (pre_str,str1);
		ci.request_response.println(Integer.toString(str2),str3);
	};
	private void print(String pre_str,client_information ci,String str1,long str2,String str3)
	{
		ci.request_response.print  (system_pre_str);
		ci.request_response.print  (pre_str,str1);
		ci.request_response.println(Long.toString(str2),str3);
	};
	private void print(String pre_str,client_information ci,String str,location loca,String follow_str)
	{
		double p[]=loca.get_location_data();
		ci.request_response.print  (system_pre_str);
		ci.request_response.print  (pre_str,str);
		for(int i=0,ni=p.length;i<ni;i++)
			ci.request_response.print  ((i<=0)?"[":",",p[i]);
		ci.request_response.println("]",follow_str);
	};
	private void print(String pre_str,client_information ci,String str,box b,String follow_str)
	{
		ci.request_response.print  (system_pre_str);
		ci.request_response.print  (pre_str,str);
		if(b==null)
			ci.request_response.print  ("null",follow_str);
		else{
			ci.request_response.print  ("[",b.p[0].x).print(",",b.p[0].y).print(",",b.p[0].z);
			ci.request_response.print  (",",b.p[1].x).print(",",b.p[1].y).print(",",b.p[1].z);
			ci.request_response.println("]",follow_str);
		}
	};
	public component_collector_jason_part(String my_system_pre_str,
			boolean only_title_flag,boolean last_list_flag,boolean location_flag,boolean difference_flag,
			component_collector collector,component_collector match_collector,client_information ci,engine_kernel ek)
	{
		system_pre_str=my_system_pre_str;
		
		String pre_str="",unprint_str=null;
		print(pre_str,ci,"{");
		pre_str="\t";
		
		print(pre_str,ci,"\"list_id\":		",		collector.list_id,									",");
		print(pre_str,ci,"\"title\":		",		jason_string.change_string(collector.title),		",");
		print(pre_str,ci,"\"description\":		",	jason_string.change_string(collector.description),	",");
		print(pre_str,ci,"\"render_number\":	",	collector.render_number,							",");
		print(pre_str,ci,"\"part_number\":		",	collector.part_number,								",");
		
		if(only_title_flag) {
			print(pre_str,ci,"\"component_number\":	"	,collector.component_number,"");
			pre_str="";
			print(pre_str,ci,last_list_flag?"}":"},");
			return;
		}

		print(pre_str,ci,"\"component_number\":	"	,collector.component_number,",");
		
		print(pre_str,ci,"\"render_list\":[");
		
		for(int i=0,ni=collector.component_collector.length;i<ni;i++) {
			if(collector.render_component_number[i]<=0)
				continue;
			if(unprint_str!=null){
				print(unprint_str,ci,",");
				unprint_str=null;
			}
			
			pre_str="\t\t";
			print(pre_str,ci,"{");
			
			pre_str="\t\t\t";
			print(pre_str,ci,"\"render_id\":			",		i,										",");
			print(pre_str,ci,"\"render_part_number\":		",	collector.render_part_number[i],		",");
			print(pre_str,ci,"\"render_component_number\":	",	collector.render_component_number[i],	",");
			print(pre_str,ci,"\"part_list\":[");
			if(collector.component_collector[i]!=null)
				for(int j=0,nj=collector.component_collector[i].length;j<nj;j++){
					if(collector.part_component_number[i][j]<=0)
						continue;
					
					if(unprint_str!=null) {
						print(unprint_str,ci,",");
						unprint_str=null;
					}
					
					part p=ek.render_cont.renders[i].parts[j];

					pre_str="\t\t\t\t";
					print(pre_str,ci,"{");
					
					pre_str="\t\t\t\t\t";
					print(pre_str,ci,"\"render_id\":		",		p.render_id,",");
					print(pre_str,ci,"\"part_id\":		",			p.part_id,",");
					print(pre_str,ci,"\"part_component_number\":",	collector.part_component_number[i][j],",");
					print(pre_str,ci,"\"permanent_render_id\":	",	p.permanent_render_id,",");
					print(pre_str,ci,"\"permanent_part_id\":	",	p.permanent_part_id,",");
					print(pre_str,ci,"\"part_from_id\":		",		p.part_from_id,",");
					print(pre_str,ci,"\"system_name\":		",		jason_string.change_string(p.system_name),",");
					print(pre_str,ci,"\"user_name\":		",		jason_string.change_string(p.user_name),",");
					
					print(pre_str,ci,"\"component_list\":[");
					
					component_link_list cll=collector.component_collector[i][j];
					component_link_list match_cll=null;
					if(match_collector!=null)
						if(match_collector.component_collector[i]!=null)
							match_cll=match_collector.component_collector[i][j];

					for(;cll!=null;cll=cll.next_list_item,match_cll=(match_cll==null)?null:(match_cll.next_list_item)) {
						pre_str="\t\t\t\t\t\t";
						print(pre_str,ci,"{");
						
						String id_str	 =Integer.toString(	(cll.comp==null)?-1:(cll.comp.component_id));
						String driver_str=Integer.toString(	(cll.comp==null)?-1:(cll.driver_id));
						String name_str	 =					(cll.comp==null)?"NULL_name":(cll.comp.component_name);
						
						pre_str="\t\t\t\t\t\t\t";
						
						print(pre_str,ci,"\"component_id\"	:	",	id_str,",");
						print(pre_str,ci,"\"driver_id\"	:	",		driver_str,",");
						print(pre_str,ci,"\"component_name\":	",	jason_string.change_string(name_str),location_flag?",":"");

						if(location_flag) {
							location relative_location=			(cll.comp==null)?new location():cll.comp.relative_location;
							location move_location=				(cll.comp==null)?new location():cll.comp.move_location;
							location absolute_location=			(cll.comp==null)?new location():cll.comp.absolute_location;
							print(pre_str,ci,"\"relative_location\":	",	relative_location,",");
							print(pre_str,ci,"\"move_location\":	",		move_location,",");
							print(pre_str,ci,"\"absolute_location\":	",	absolute_location,difference_flag?",":"");
						}
						if(difference_flag) {	
							box ab=cll.comp.get_component_box(false);
							print(pre_str,ci,"\"absolute_box\":		",(ab!=null)?ab:(cll.comp.get_component_box(true)),",");
							print(pre_str,ci,"\"model_box\":		",cll.comp.model_box,",");
							
							if(match_cll==null) {
								print(pre_str,ci,"\"location_difference\":	0,");
								print(pre_str,ci,"\"direction_difference\":	[]");
							}else{
								point s_point=(cll.comp.model_box==null)?new point(0,0,0):(cll.comp.model_box.center());
								point t_point=(match_cll.comp.model_box==null)?new point(0,0,0):(match_cll.comp.model_box.center());
								point absulate_s_point=cll.comp.absolute_location.multiply(s_point);
								point absulate_t_point=match_cll.comp.absolute_location.multiply(t_point);
								print(pre_str,ci,"\"location_difference\":	"+(absulate_s_point.sub(absulate_t_point).distance())+",");
								String str="";
								for(int k=0,nk=p.part_par.symmetry_flag.length;k<nk;k++) {
									point dir=new point(p.part_par.location_match_direction[3*k+0],
														p.part_par.location_match_direction[3*k+1],
														p.part_par.location_match_direction[3*k+2]).expand(1.0);
									point s=cll.		comp.absolute_location.multiply(s_point.add(dir)).sub(absulate_s_point).expand(1.0);
									point t=match_cll.	comp.absolute_location.multiply(t_point.add(dir)).sub(absulate_t_point).expand(1.0);
									double match_value=s.dot(t),match_value_ex;
									if(p.part_par.symmetry_flag[k]) {
										dir=dir.scale(-1);
										t=match_cll.comp.absolute_location.multiply(t_point.add(dir)).sub(absulate_t_point).expand(1.0);
										if(match_value<(match_value_ex=s.dot(t)))
											match_value=match_value_ex;
									}
									str+=match_value+((k==(nk-1))?"":",");
								}
								print(pre_str,ci,"\"direction_difference\":	["+str+"]");
							}
						}
						pre_str="\t\t\t\t\t\t";
						print(pre_str,ci,(cll.next_list_item==null)?"}":"},");
					}
					
					pre_str="\t\t\t\t\t";
					print(pre_str,ci,"]");
					
					pre_str="\t\t\t\t";
					unprint_str=pre_str+"}";
				}
			if(unprint_str!=null) {
				print(unprint_str,ci,"");
				unprint_str=null;
			}
			
			pre_str="\t\t\t";
			print(pre_str,ci,"]");
			pre_str="\t\t";
			unprint_str=pre_str+"}";
		}
		
		if(unprint_str!=null) {
			print(unprint_str,ci,"");
			unprint_str=null;
		}

		pre_str="\t";
		print(pre_str,ci,"]");

		pre_str="";
		print(pre_str,ci,last_list_flag?"}":"},");
		return;
	}
}
