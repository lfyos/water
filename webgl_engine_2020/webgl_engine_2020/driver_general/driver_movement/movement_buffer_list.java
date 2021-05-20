package driver_movement;

import kernel_common_class.web_page;
import kernel_component.component;
import kernel_engine.component_container;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;


public class movement_buffer_list extends web_page
{
	private component comp;
	private component_container component_cont;
	private String design_buffer_file_name,file_system_charset,mount_call_processor_string;
	public movement_buffer_list(engine_kernel ek,client_information ci,movement_manager my_manager)
	{
		super(ci,my_manager.config_parameter.language_change_name,"");
		
		String str;
		comp=null;
		if((str=ci.request_response.get_parameter("component_id"))!=null)
			try{
				comp=ek.component_cont.get_component(Integer.decode(str));
			}catch(Exception e) {
				;
			}
		component_cont=ek.component_cont;
		design_buffer_file_name=my_manager.config_parameter.design_file_name;
		file_system_charset=my_manager.config_parameter.movement_file_charset;

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
			+"render.component_call_processor["+my_manager.config_parameter.component_id+"];";
	}
	public void create_head()
	{
		out.println("<script type=\"text/javascript\">");
		out.println(mount_call_processor_string);
		out.println("</script>");
	}
	public void create_body()
	{
		if(comp==null)
			return;
		
		boolean print_hr_flag=false;
		for(;comp!=null;comp=component_cont.get_component(comp.parent_component_id)){
			if(print_hr_flag){
				print_hr_flag=false;
				out.print("<hr/>");
			}
			file_reader f=new file_reader(design_buffer_file_name,file_system_charset);
			for(int i=0;;i++){
				String node_name=f.get_string();
				String part_name=f.get_string();
				f.get_string();
				if(f.eof())
					break;
				
				if(node_name==null)
					node_name="";
				if(part_name==null)
					part_name="";
				
				movement_tree t=new movement_tree(f);
				if(comp.part_name.compareTo(part_name)==0){
					out.println("<br/>",i+1);

					out.print  ("&nbsp;<a href=\"#\" onclick=\"mcp.fromdesignbuffer_movement(",i);
					out.print  (",",comp.component_id);
					out.print  (");window.open(mcp.mount_edit_url(true,-1),\'_self\');\" title=\"",t.description);
					out.print  ("\">",node_name);
					out.println("</a>");
					
					out.print  ("&nbsp;&nbsp;<a href=\"#\" onclick=\"mcp.deletedesignbuffer_movement(",i);
					out.print  (");window.open(mcp.mount_extract_url(",comp.component_id);
					out.print  ("),\'_self\');\" title=\"",language_change("delete_buffer_content"));
					out.print  ("\">",language_change("delete_buffer"));
					out.println("</a>");

					print_hr_flag=true;
				}
			}
			f.close();
		}
	}
}
