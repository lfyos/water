package driver_manipulator;

import kernel_common_class.change_name;
import kernel_common_class.jason_string;
import kernel_common_class.web_page;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_engine.client_information;

public class component_collector_web_page extends web_page
{
	private String list_type_string,component_request_url_header;
	private component_collector collector,all_collector[];
	
	public component_collector_web_page(client_information my_ci,
			component_collector my_collector,component_collector my_all_collector[],
			String my_list_type_string,long my_user_channel_id,int my_manipulator_component_id,
			int component_sort_type,double component_sort_min_distance,change_name language_change_name)
	{
		super(my_ci,language_change_name,"part_list");
		
		collector				=my_collector;
		all_collector			=my_all_collector;
		list_type_string		=my_list_type_string;
		
		component_request_url_header=my_ci.get_component_request_url_header(my_manipulator_component_id,"all");
		
		if(collector!=null)
			collector.component_space_sort(component_sort_type,component_sort_min_distance);
	}
	private void create_part_list_item(int render_id,int part_id,int component_id,String operate_string,String title)
	{
		out.print("<input type=\"button\" value=\"",title);
		out.print("\"   onclick=lfy_send(false,\"",component_request_url_header);
		out.print("&event_method=part_list&operation=selection&selection=",operate_string);
		out.print("&render_id=",render_id);
		out.print("&part_id=",part_id);
		out.print("&component_id=",component_id);
		out.println("\");>");
	}
	private void create_part_list_item(int render_id,int part_id,int component_id)
	{
		create_part_list_item(render_id,part_id,component_id,"select",		language_change("select"));
		create_part_list_item(render_id,part_id,component_id,"unselect",	language_change("unselect"));
		create_part_list_item(render_id,part_id,component_id,"swap",		language_change("swap"));
	}
	private String delete_space(String str)
	{
		if(str!=null)
			if(str.length()>0)
				for(int pointer=0,n=str.length();pointer<n;pointer++)
					switch(str.charAt(pointer)){
					default:
						str=str.substring(pointer);
						pointer=n;
					case ' ':
					case '\n':
					case '\t':
						break;
					}
		if(str!=null)
			if(str.length()>0)
				for(int pointer=str.length()-1;pointer>=0;pointer--)
					switch(str.charAt(pointer)){
					default:
						str=str.substring(0,pointer+1);
						pointer=-1;
					case ' ':
					case '\n':
					case '\t':
						break;
					}
		if(str!=null)
			if(str.length()>0)
				if((str=str.replaceAll("\n", "<br/>"))!=null)
					if(str.length()>0)
						return str;
		return null;
	}
	private void create_part_list()
	{
		String str;
		component_link_list p;

		if((str=delete_space(collector.title.trim()))!=null){
			out.println("<div align=\"center\"><table>");
			out.println("<tr><td>");
			out.println(str);
			out.println("</td></tr>");
			out.println("</table></div>");
		}
		out.println("<div align=\"center\"><table>");
		out.println("<tr>");
		
		out.println("<td>");
		out.print  (language_change("total")+"&nbsp",collector.component_number);
		out.print  (language_change("parts"));
		
		out.println("</td><td>");
		out.print  (language_change("all_parts"));
		
		out.println("</td><td>");
		create_part_list_item(-1,-1,-1);
		out.println("</td>");
		
		out.println("</tr>");
		
		out.println("</table></div>");
		
		out.println("<div align=\"center\"><table>");
		
		boolean simple_or_complex_flag=(list_type_string.toLowerCase().compareTo("simple")==0)?true:false;
		for(int i=0,ki=1,ni=collector.component_collector.length;i<ni;i++){
			if(collector.component_collector[i]==null)
				continue;
			for(int j=0,nj=collector.component_collector[i].length;j<nj;j++){
				if((p=collector.component_collector[i][j])==null)
					continue;
				if(p.comp.driver_number()<=0)
					continue;
				if(p.comp.driver_array[p.driver_id].component_part==null)
					continue;
				
				String component_part_user_name=p.comp.driver_array[p.driver_id].component_part.user_name;

				if(!simple_or_complex_flag)
					out.println("<tr><td></td></tr>");
				
				out.println("<tr>");
				out.print  ("<td>",ki++);	
				out.print  (".</td><td>",language_change("total"));
				out.print  (collector.part_component_number[i][j]);
				out.print  (language_change("parts"));
				out.print  ("</td><td>",component_part_user_name);
				out.println("</td>");
				out.println("<td>");
				create_part_list_item(i,j,-1);
				out.println("</td>");			
				out.println("</tr>");
				out.println();
				
				if(!simple_or_complex_flag)
					for(int n=1;p!=null;p=p.next_list_item,n++){
						if(p.comp.uniparameter.display_part_name_or_component_name_flag)
							component_part_user_name=p.comp.driver_array[p.driver_id].component_part.user_name;
						else
							component_part_user_name=p.comp.component_name;

						out.println("<tr>");
						out.print  ("<td></td><td></td>");
						out.print  ("<td>");
						out.print  (language_change("NO."),n);
						out.print  (":&nbsp;&nbsp;",component_part_user_name);
						out.println("</td>");
						out.println("<td>");
						create_part_list_item(i,j,p.comp.component_id);
						out.println("</td>");
						out.println("</tr>");
					}
				out.println();
			}
		}
		out.println("</table></div>");
		
		out.println("<p align=\"center\">");
		if((str=delete_space(collector.description.trim()))!=null)
			out.println(str);
		out.println("</p>");
	}
	private void create_collector_list_item(String title,String operating_string,int my_list_id,boolean reload_flag)
	{
		out.print("<td><input type=\"button\" value=\"",title);
		out.print("\"   onclick=lfy_send(",reload_flag?"true":"false");
		out.print(",\""+component_request_url_header,"&event_method=part_list&operation=manipulation");
		out.print("&list_type=collector&list_id=",all_collector[my_list_id].list_id);
		out.print("&manipulation=",operating_string);
		out.println("\");></td>");
	}
	private void create_collector_list()
	{
		if(all_collector==null)
			return;
		out.println("<div align=\"center\"><table>");
		for(int i=0;i<all_collector.length;i++)
			if(all_collector[i]!=null){
				out.println("<tr>");

				out.print("<td>",all_collector[i].list_id);
				out.println("</td>");

				create_collector_list_item(language_change("select"),	"select",	i,false);
				create_collector_list_item(language_change("unselect"),	"unselect",	i,false);
				create_collector_list_item(language_change("swap"),		"swap",		i,false);
				create_collector_list_item(language_change("top"),		"top",		i,true);
				create_collector_list_item(language_change("delete"),	"delete",	i,true);
				
				out.print  ("<td><input type=\"text\" value=",jason_string.change_string(all_collector[i].title));
				out.print  (" id=\"list_item_id_",i);
				out.print  ("\"	onchange=lfy_change_title(\"",component_request_url_header);
				out.print  ("\",",all_collector[i].list_id);
				out.print  (",\"list_item_id_",i);
				out.println("\");></td>");

				out.println("</tr>");
			}
		out.println("</table></div>");
	}
	public void create_body()
	{
		String url_string=component_request_url_header;
		
		out.println("<h3>");
		
		out.println("<div align=\"center\"><table>");
		
		out.println("<tr>");
		
		out.println("<td>");
		out.print  ("<a href=\"#\"	onclick=lfy_send(true,\"",url_string+"&event_method=part_list");
		out.print  ("&operation=create&part_list=true\") title=\"",language_change("create_part_list"));
		out.println("\">",language_change("create_list")+"</a>");
		out.println("</td>");

		out.println("<td>");
		out.print  ("<a href=\"#\"	onclick=lfy_send(true,\"",url_string+"&event_method=part_list");
		out.print  ("&operation=create&part_list=false\") title=\"",language_change("create_all_list"));
		out.println("\">",language_change("create_all")+"</a>");
		out.println("</td>");
		
		out.println("<td>");
		out.print  ("<a href=\"#\"	onclick=lfy_send(true,\"",url_string+"&event_method=part_list");
		out.print  ("&operation=target&target=display\") title=\""+language_change("create_display_list"));
		out.println("\"		>"+language_change("create_display")+"</a>");
		out.println("</td>");
		
		out.println("<td>");
		out.print  ("<a href=\"#\"	onclick=lfy_send(true,\"",url_string+"&event_method=part_list");
		out.print  ("&operation=target&target=selection\") title=\""+language_change("create_selection_list"));
		out.println("\"		>"+language_change("create_selection")+"</a>");
		out.println("</td>");
		
		out.println("<td>");
		out.print  ("<a href=\"#\"	onclick=lfy_send(true,\"",url_string+"&event_method=part_list");
		out.print  ("&operation=target&target=all\") title=\""+language_change("create_target_list"));
		out.println("\"		>"+language_change("create_target")+"</a>");
		out.println("</td>");
		
		out.println("</tr>");
		
		out.println("<tr>");
		
		out.print("<td>");
		out.print  ("<a href=\""+url_string,"&event_method=part_list&operation=webpage&list_type=simple	\"	title=\"");
		out.println(language_change("simple_part_list"),"\"		>"+language_change("simple_list")+"</a>");
		out.println("</td>");

		out.print("<td>");
		out.print  ("<a href=\""+url_string,"&event_method=part_list&operation=webpage&list_type=collector	\"	title=\"");
		out.println(language_change("operate_part_list"),"\"		>"+language_change("operate_list")+"</a>");
		out.println("</td>");
		
		out.print("<td></td>");
		
		out.println("<td>");
		out.print  ("<a href=\"#\"	onclick=lfy_send(true,\"",url_string+"&event_method=part_list");
		out.print  ("&operation=load\") title=\""+language_change("load_part_list"));
		out.println("\"		>"+language_change("load_list")+"</a>");
		out.println("</td>");
		
		out.print("<td>");
		out.print  ("<a href=\""+url_string,"&event_method=part_list&operation=upload_webpage");
		out.print  ("&list_type="+list_type_string+"\"	title=\"",language_change("upload_part_list"));
		out.println("\">"+language_change("upload_list")+"</a>");
		out.println("</td>");
		
		out.println("</tr>");
		
		out.println("<tr>");
		
		out.print("<td>");
		out.print  ("<a href=\""+url_string,"&event_method=part_list&operation=webpage&list_type=complex	\"	title=\"");	
		out.println(language_change("complex_part_list"),"\"		>"+language_change("complex_list")+"</a>");
		out.println("</td>");
		
		out.println("<td>");
		out.print  ("<a href=\"#\"	onclick=lfy_send(true,\"",url_string+"&event_method=part_list");
		out.print  ("&operation=clear\") title=\""+language_change("clear_part_list"));
		out.println("\"		>"+language_change("clear_list")+"</a>");
		out.println("</td>");

		out.print("<td></td>");

		out.println("<td>");
		out.print  ("<a href=\"#\" onclick=lfy_send(false,\"",url_string+"&event_method=part_list");
		out.print  ("&operation=save\") title=\""+language_change("save_part_list"));
		out.println("\"		>"+language_change("save_list")+"</a>");
		out.println("</td>");

		out.print("<td>");
		out.print  ("<a href=\""+url_string,"&event_method=part_list&operation=download\"");
		out.print  (" title=\"",language_change("download_part_list")+"\"");
		out.print  ("	download=\"partlist.txt\"");
		out.print  (">",language_change("download_list"));
		out.println("</a>");
		out.println("</td>");
		
		out.println("</tr>");

		out.println("</table></div>");

		out.println("</h3>");
		
		out.println("<hr>");
		
		if(list_type_string.toLowerCase().compareTo("collector")==0)
			create_collector_list();
		else if(collector!=null)
			create_part_list();
	}

	public void create_head()
	{
		out.println("<script type=\"text/javascript\">");
		
		out.println("function lfy_change_title(url,text_id,text_name)");
		out.println("{");
		
		out.println("	var str=encodeURIComponent(encodeURIComponent(document.getElementById(text_name).value));");
		out.println("	url+=\"&event_method=part_list&operation=manipulation&list_id=\"");
		out.println("	url+=text_id.toString();");
		out.println("	url+=\"&manipulation=title&title=\";");
		out.println("	url+=str;");

		out.println("	try{");
		out.println("		var lfy_ajax=new XMLHttpRequest();");
		out.println("		lfy_ajax.onreadystatechange=function()");
		out.println("		{");
		out.println("			if(lfy_ajax.readyState==4)");
		out.println("				window.location.reload();");
		out.println("		};");		
		out.println("		lfy_ajax.open(\"GET\",url,true);");
		out.println("		lfy_ajax.send(null);");
		out.println("	}catch(e){");
		out.println("		;");
		out.println("	};");
		out.println("};");
			
		out.println("function lfy_send(my_reload_flag,url)");
		out.println("{");
		out.println("	try{");
		out.println("		var lfy_ajax=new XMLHttpRequest();");
		out.println("		lfy_ajax.onreadystatechange=function()");
		out.println("		{");
		out.println("			if((lfy_ajax.readyState==4)&&my_reload_flag)");
		out.println("				window.location.reload();");
		out.println("		};");
		out.println("		lfy_ajax.open(\"GET\",url,true);");
		out.println("		lfy_ajax.send(null);");
		out.println("	}catch(e){");
		out.println("		;");
		out.println("	};");
		out.println("};");

		out.println("</script>");
	}
}
