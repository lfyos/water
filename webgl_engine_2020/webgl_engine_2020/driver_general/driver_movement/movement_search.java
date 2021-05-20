package driver_movement;

import kernel_common_class.change_name;
import kernel_common_class.web_page;
import kernel_component.component;
import kernel_engine.client_information;
import kernel_engine.component_container;

public class movement_search extends web_page
{
	private component initial_comp;
	private component_container component_cont;
	private movement_tree move_root,display_stack[];
	private String mount_call_processor_string;
	
	public void create_head()
	{
		out.println("<script type=\"text/javascript\">");
		out.println(mount_call_processor_string);
		out.println("</script>");
	}
	private int create_component_movement(component comp,movement_tree t,int depth_number)
	{
		if(t==null)
			return 0;
		if(display_stack==null)
			display_stack=new movement_tree[1];
		else if(depth_number>=display_stack.length){
			movement_tree new_display_stack[]=new movement_tree[depth_number+1];
			for(int i=0,n=display_stack.length;i<n;i++)
				new_display_stack[i]=display_stack[i];
			display_stack=new_display_stack;
		}
		display_stack[depth_number]=t;

		if(t.children!=null){
			int return_value=0;
			for(int i=0,n=t.children.length;i<n;i++)
				return_value+=create_component_movement(comp,t.children[i],depth_number+1);
			return return_value;
		}
		if(t.move==null)
			return 0;
		component moved_component;
		if((moved_component=component_cont.get_component(t.move.moved_component_id))==null)
			return 0;
		if(moved_component.component_id!=comp.component_id)
			return 0;
		for(int i=0;i<=depth_number;i++)
			if(display_stack[i]!=null){
				for(int j=0;j<i;j++)
					out.print("&nbsp;&nbsp;");
				out.print  ("<a href=\"#\" onclick=\"mcp.locate_camera_movement(",t.movement_tree_id);
				out.print  (");\" title=\"",language_change("click_to_locate_camera_on_parts"));
				out.print  ("\">",display_stack[i].node_name);
				out.println("</a><br/>");
				display_stack[i]=null;
			}
		return 1;
	}
	public void create_body()
	{
		display_stack=null;
		for(component comp=initial_comp;comp!=null;comp=component_cont.get_component(comp.parent_component_id))
			if(create_component_movement(comp,move_root,0)>0)
				out.println("<hr>");
	}
	public movement_search(
			int my_movement_component_id,component my_initial_comp,
			component_container my_component_cont,movement_tree my_move_root,
			client_information ci,change_name language_change_name)
	{
		super(ci,language_change_name,"movement_search");

		initial_comp=my_initial_comp;
		component_cont=my_component_cont;
		move_root=my_move_root;
		
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
			+"render.component_call_processor["+my_movement_component_id+"];";
	}
}
