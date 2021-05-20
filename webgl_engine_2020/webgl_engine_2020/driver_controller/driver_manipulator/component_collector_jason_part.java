package driver_manipulator;

import kernel_component.component_collector;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_component.component_link_list;
import kernel_part.part;

public class component_collector_jason_part 
{
	private String pre_str;
	private client_information ci;
	private engine_kernel ek;
	
	private void print(String str)
	{
		ci.request_response.println(pre_str,str);
	};
	private String normal_str(String str)
	{
		return str.replace('\\','/').replace("\"","");
	}	
	public component_collector_jason_part(
			component_collector collector,client_information my_ci,engine_kernel my_ek)
	{
		ci=my_ci;
		ek=my_ek;
		
		if(collector==null) {
			pre_str="";
			print("{");
			pre_str="\t";
			
			print("\"list_id\":	-1");
			
			pre_str="";
			print("}");
			return;
		}
		
		print("{");
		pre_str="\t";
		
		print("\"list_id\":	"			+collector.list_id+",");
		print("\"title\":	\""			+normal_str(collector.title)+"\",");
		print("\"description\":	\""		+normal_str(collector.description)+"\",");
		print("\"audio_file_name\":	\""	+normal_str(collector.audio_file_name)+"\",");
		
		print("\"render_list\":	[");
		for(int i=0,ni=collector.component_collector.length;i<ni;i++) {
			pre_str="\t\t";
			print("{");
			
			pre_str="\t\t\t";
			print("\"render_id\":"+i+",");
			print("\"part_list\":[");
			if(collector.component_collector[i]!=null)
				for(int j=0,nj=collector.component_collector[i].length;j<nj;j++){
					part p=ek.render_cont.renders[i].parts[j];
					
					pre_str="\t\t\t\t";
					print("{");
					
					pre_str="\t\t\t\t\t";
					print("\"render_id\":"			+p.render_id+",");
					print("\"part_id\":"			+p.part_id+",");
					print("\"permanent_render_id\":"+p.permanent_render_id+",");
					print("\"permanent_part_id\":"	+p.permanent_part_id+",");
					print("\"part_from_id\":"		+p.part_from_id+",");
					print("\"system_name\":\""		+p.system_name+"\",");
					print("\"user_name\":\""		+p.user_name+"\",");
					
					print("\"component_list\":[");
					for(component_link_list cll=collector.component_collector[i][j];cll!=null;cll=cll.next_list_item) {
						pre_str="\t\t\t\t\t\t";
						print("{");
						
						String id_str	 =Integer.toString(	(cll.comp==null)?-1:(cll.comp.component_id));
						String driver_str=Integer.toString(	(cll.comp==null)?-1:(cll.driver_id));
						String name_str	 =					(cll.comp==null)?"NULL_name":normal_str(cll.comp.component_name);
						
						pre_str="\t\t\t\t\t\t\t";
						
						print("\"component_id\"\t\t:\t"		+id_str+",");
						print("\"driver_id\"\t\t:\t"		+driver_str+",");
						print("\"component_name\"\t:\t\""	+name_str+"\"");

						pre_str="\t\t\t\t\t\t";
						print((cll.next_list_item==null)?"}":"},");
					}
					
					pre_str="\t\t\t\t\t";
					print("]");
					
					pre_str="\t\t\t\t";
					print((j==(nj-1))?"}":"},");
				}
			pre_str="\t\t\t";
			print("]");
			pre_str="\t\t";
			print((i==(ni-1))?"}":"},");
		}
		
		pre_str="\t";
		print("]");
		
		pre_str="";
		print("}");
	}
}
