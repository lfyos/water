package driver_movement;

import kernel_camera.camera_parameter;
import kernel_common_class.web_page;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class movement_edit  extends web_page
{
	public movement_searcher searcher;
	private movement_manager manager;
	private long current_time;
	private long switch_camera_time_length;
	
	private boolean mount_direction_flag;
	private String mount_call_processor_string;
	
	
	private String edit_locate_camera_string,	edit_locate_camera_title_string;
	private String edit_delete_string,			edit_delete_title_string;
	private String edit_follow_string,			edit_follow_title_string;
	private String edit_up_string,				edit_up_title_string;
	private String edit_down_string,			edit_down_title_string;
	private String edit_from_child_string,		edit_from_child_title_string;
	private String edit_buffer_string,			edit_buffer_title_string;
	private String edit_extract_string,			edit_extract_title_string;
	private String edit_tag_string,				edit_tag_title_string;
	private String edit_right_button_string,	edit_right_button_title_string;
	private String edit_make_view_string,		edit_make_view_title_string;
	private String edit_delete_view_string,		edit_delete_view_title_string;
	
	private String edit_modify_view_location_title_string;
	private String edit_view_start_location_string;
	private String edit_view_end_location_string;
	private String edit_view_start_end_location_string;
	private String edit_view_parent_location_string;
	
	
	private String edit_select_component_string,edit_select_component_title_string;
	private String edit_invert_string,			edit_invert_title_string;
	
	private String edit_synchronous_mount_string,edit_synchronous_mount_title_string;
	private String edit_sequence_mount_string,	edit_sequence_mount_title_string;
	
	
	public void create_head()
	{
		String str[]={
			"",
			"<script type=\"text/javascript\">",
			mount_call_processor_string,
			"function jump_url(url)",
			"{",
			"	window.open(url,\"_self\");",
			"}",
			"function operate_id(id,fun)",
			"{",
			"	fun(id,	function(response_data,render)",
			"			{",
			"				jump_url(mcp.mount_edit_url(response_data));",
			"			});",
			"}",
			"function operate_id_with_alert(id,fun)",
			"{",
			"	fun(id,	function(response_data,render)",
			"			{",
			"				alert(response_data);",
			"			});",
			"}",
			
			"function process_movement(movement_id)",
			"{",
			"	var seletion=document.getElementById(\"move_id_\"+movement_id);",
			"	var seletion_value=seletion.value;",
			"	seletion.value=\"\"",
			"	switch(seletion_value){",
			"	case \""+edit_locate_camera_string+"\":",
			"		operate_id(movement_id,mcp.locate_camera_movement);",
			"		break;",
			"	case \""+edit_delete_string+"\":",
			"		operate_id(movement_id,mcp.delete_movement);",
			"		break;",
			"	case \""+edit_follow_string+"\":",
			"		operate_id(movement_id,mcp.follow_movement);",
			"		break;",
			"	case \""+edit_up_string+"\":",
			"		operate_id(movement_id,mcp.moveup_movement);",
			"		break;",
			"	case \""+edit_down_string+"\":",
			"		operate_id(movement_id,mcp.movedown_movement);",
			"		break;",
			"	case \""+edit_from_child_string+"\":",
			"		operate_id(movement_id,mcp.fromchild_movement);",
			"		break;",
			"	case \""+edit_buffer_string+"\":",
			"		operate_id(movement_id,mcp.tobuffer_movement);",
			"		break;",
			"	case \""+edit_extract_string+"\":",
			"		operate_id(movement_id,mcp.frombuffer_movement);",
			"		break;",
			"	case \""+edit_tag_string+"\":",
			"		jump_url(mcp.mount_tag_url(movement_id,\""+manager.config_parameter.render_window_name+"\"));",
			"		break;",
			"	case \""+edit_right_button_string+"\":",
			"		operate_id_with_alert(movement_id,mcp.todesignbuffer_movement);",
			"		break;",
			"	case \""+edit_make_view_string+"\":",
			"	case \""+edit_delete_view_string+"\":",
			"		operate_id(movement_id,mcp.view_direction_movement);",
			"		break;",
			"	case \""+edit_view_start_location_string+"\":",
			"	case \""+edit_view_end_location_string+"\":",
			"	case \""+edit_view_start_end_location_string+"\":",
			"	case \""+edit_view_parent_location_string+"\":",
			"		operate_id(movement_id,mcp.view_box_movement);",
			"		break;",
			"	case \""+edit_select_component_string+"\":",
			"		mcp.select_component_movement(movement_id);",
			"		break;",
			"	case \""+edit_invert_string+"\":",
			"		operate_id(movement_id,mcp.reverse_movement);",
			"		break;",
			"	case \""+edit_synchronous_mount_string+"\":",
			"		operate_id(movement_id,mcp.synchronization_movement);",
			"		break;",
			"	case \""+edit_sequence_mount_string+"\":",
			"		operate_id(movement_id,mcp.sequence_movement);",
			"		break;",
			"	}",
			"}",
			
			"</script>",
			""
		};
		for(int i=0,ni=str.length;i<ni;i++)
			out.println(str[i]);
	}
	private void print_movement(movement_tree t,movement_tree parent)
	{
		boolean flag=false;
		if((t.start_time-switch_camera_time_length)<=current_time)
			if(current_time<t.terminate_time) {
				out.print("<strong>");
				flag=true;
			}
		out.print  ("<a href=\"#\" onclick=\"jump_url(mcp.mount_edit_url(",t.movement_tree_id);
		out.print  ("));\" title=\""+t.description.trim()+"\">"+t.node_name.trim(),"</a>");
		if(flag)
			out.print  ("</strong>");
	}
	public void create_body()
	{
		if(searcher.result==null)
			return;
		
		out.println();
		if(manager.buffer_movement!=null){
			out.print  (language_change("edit_buffer_number"));
			out.print  (":",manager.buffer_movement.length);
			out.println("<br/>");
		}
		if(searcher.result_parent!=null) {
			print_movement(searcher.result_parent,null);
			out.println("<br/>");
		}
		out.println("<table><tr>");
		out.print  ("<td>");
		out.print  ((searcher.result_id+1)+":");
		out.println("</td>");
		
		out.print  ("<td>");
		print_movement(searcher.result,searcher.result_parent);
		out.println(" </td>");
		out.println("</tr></table><br/>");

		if(searcher.result.children!=null)
			if(searcher.result.children.length>0){
				for(int i=0;i<(searcher.result.children.length);i++){
					int j=mount_direction_flag?i:(searcher.result.children.length-i-1);
					movement_tree t=searcher.result.children[j];
					
					out.println();
					out.println("<table><tr>");
					out.print  ("<td>");	out.print(i+1);						out.println(":</td>");
					out.print  ("<td>");	print_movement(t,searcher.result);	out.println("</td>");
										
					out.println("<td>");
					out.print  ("<select id=\"move_id_",t.movement_tree_id);
					out.print  ("\" onclick=\"process_movement(",t.movement_tree_id);
					out.println(");\">");
					
					out.println("<option title=\"\"></option>");
					
					out.print  ("<option title=\"",edit_locate_camera_title_string);out.print("\">");
					out.println(edit_locate_camera_string,"</option>");
					
					out.print  ("<option title=\"",edit_delete_title_string);out.print("\">");
					out.println(edit_delete_string,"</option>");
					
					do{
						if(t.children!=null)
							if(t.children.length>0)
								break;
						out.print  ("<option title=\"",edit_follow_title_string);out.print("\">");
						out.println(edit_follow_string,"</option>");
					}while(false);
					
					out.print  ("<option title=\"",edit_up_title_string);out.print("\">");
					out.println(edit_up_string,"</option>");
					
					out.print  ("<option title=\"",edit_down_title_string);out.print("\">");
					out.println(edit_down_string,"</option>");
					
					out.print  ("<option title=\"",edit_from_child_title_string);out.print("\">");
					out.println(edit_from_child_string,"</option>");
					
					out.print  ("<option title=\"",edit_buffer_title_string);out.print("\">");
					out.println(edit_buffer_string,"</option>");
					
					if(manager.buffer_movement!=null){
						out.print  ("<option title=\"",edit_extract_title_string);out.print("\">");
						out.println(edit_extract_string,"</option>");
					}
					
					out.print  ("<option title=\"",edit_tag_title_string);out.print("\">");
					out.println(edit_tag_string,"</option>");
					
					out.print  ("<option title=\"",edit_right_button_title_string);out.print("\">");
					out.println(edit_right_button_string,"</option>");
					
					out.print  ("<option title=\"",
						(t.direction==null)?edit_make_view_title_string:edit_delete_view_title_string);out.print("\">");
					out.println((t.direction==null)?edit_make_view_string:edit_delete_view_string);out.println("</option>");
						
					out.print  ("<option title=\"",edit_modify_view_location_title_string);out.print("\">");
					switch(t.scale_type){
					case 1:
						out.print(edit_view_start_location_string);		break;
					case 2:
						out.print(edit_view_end_location_string);		break;
					case 3:
						out.print(edit_view_start_end_location_string);	break;
					case 0:
					default:
						out.print(edit_view_parent_location_string);	break;
					}
					out.println("</option>");
					
					out.print  ("<option title=\"",edit_select_component_title_string);out.print("\">");
					out.println(edit_select_component_string,"</option>");
					
					if(t.children!=null)
						if(t.children.length>0){
							out.print  ("<option title=\"",edit_invert_title_string);
							out.print("\">");
							out.println(edit_invert_string,"</option>");
							
							out.print  ("<option title=\"",
								t.sequence_flag?edit_synchronous_mount_title_string:edit_sequence_mount_title_string);
							out.print("\">");
							out.println(t.sequence_flag?edit_synchronous_mount_string:edit_sequence_mount_string);
							out.println("</option>");
						}
					out.println("</select>");
					
					out.println("</td>");
					
					out.println("</tr></table>");
				}
			}
		
		out.println();
		out.println();
	}
	public movement_edit(engine_kernel ek,client_information ci,movement_manager my_manager,int movement_tree_id)
	{
		super(ci,my_manager.config_parameter.language_change_name,"movement_edit");
		
		manager=my_manager;
				
		if(manager.root_movement==null)
			return;
		camera_parameter cam_par=ci.display_camera_result.cam.parameter;
		switch_camera_time_length=cam_par.movement_flag?cam_par.switch_time_length:0;
		
		for(;;movement_tree_id=searcher.result_parent.movement_tree_id){
			searcher=new movement_searcher(manager.root_movement,movement_tree_id);
			if(searcher.result==null)
				searcher=new movement_searcher(manager.root_movement,manager.root_movement.movement_tree_id);
			if(searcher.result==null)
				return;
			if(searcher.result.children!=null)
				if(searcher.result.children.length>0)
					break;
			if(searcher.result_parent==null)
				return;
		};
		
		movement_tree t;	
		if((t=manager.root_movement.search_movement(my_manager.parameter.current_movement_id))==null)
			t=manager.root_movement;
		current_time=t.start_time;

		mount_direction_flag=manager.mount_direction_flag;
		
		mount_call_processor_string=ci.request_response.get_parameter("windows");
		switch((mount_call_processor_string==null)?"parent":mount_call_processor_string){
		case "top":
			mount_call_processor_string="window.top.";
			break;
		case "parent":
			mount_call_processor_string="window.parent.";
			break;
		case "this":
			mount_call_processor_string="";
			break;
		default:
			mount_call_processor_string ="window.frames[\""+mount_call_processor_string+"\"].";
			break;
		}
		mount_call_processor_string="var mcp="+mount_call_processor_string
			+"render.component_call_processor["+manager.config_parameter.component_id+"];";


		edit_locate_camera_string		=language_change("edit_locate_camera");
		edit_locate_camera_title_string	=language_change("edit_locate_camera_title");
		
		edit_delete_string				=language_change("edit_delete");
		edit_delete_title_string		=language_change("edit_delete_title");
		
		edit_follow_string				=language_change("edit_follow");
		edit_follow_title_string		=language_change("edit_follow_title");
		
		edit_up_string					=language_change("edit_up");
		edit_up_title_string			=language_change("edit_up_title");
		
		edit_down_string				=language_change("edit_down");
		edit_down_title_string			=language_change("edit_down_title");
		
		edit_from_child_string			=language_change("edit_from_child");
		edit_from_child_title_string	=language_change("edit_from_child_title");
		
		edit_buffer_string				=language_change("edit_buffer");
		edit_buffer_title_string		=language_change("edit_buffer_title");
		
		edit_extract_string				=language_change("edit_extract");
		edit_extract_title_string		=language_change("edit_extract_title");
		
		edit_tag_string					=language_change("edit_tag");
		edit_tag_title_string			=language_change("edit_tag_title");
		
		edit_right_button_string		=language_change("edit_right_button");
		edit_right_button_title_string	=language_change("edit_right_button_title");
		
		edit_make_view_string			=language_change("edit_make_view");
		edit_make_view_title_string		=language_change("edit_make_view_title");
		
		edit_delete_view_string			=language_change("edit_delete_view");
		edit_delete_view_title_string	=language_change("edit_delete_view_title");
		
		edit_modify_view_location_title_string=language_change("edit_modify_view_location_title");
		edit_view_start_location_string		=language_change("edit_view_start_location");
		edit_view_end_location_string		=language_change("edit_view_end_location");
		edit_view_start_end_location_string	=language_change("edit_view_start_end_location");
		edit_view_parent_location_string	=language_change("edit_view_parent_location");
		
		edit_select_component_string		=language_change("edit_select_component");
		edit_select_component_title_string	=language_change("edit_select_component_title");
		
		edit_invert_string					=language_change("edit_invert");
		edit_invert_title_string			=language_change("edit_invert_title");
		
		edit_synchronous_mount_string		=language_change("edit_synchronous_mount");
		edit_synchronous_mount_title_string	=language_change("edit_synchronous_mount_title");
		
		edit_sequence_mount_string			=language_change("edit_sequence_mount");
		edit_sequence_mount_title_string	=language_change("edit_sequence_mount_title");
		
		create_web_page();
	}
}
