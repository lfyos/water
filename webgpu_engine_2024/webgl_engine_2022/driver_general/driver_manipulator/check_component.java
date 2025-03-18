package driver_manipulator;

import kernel_component.component;
import kernel_part.part;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class check_component 
{
	public static void check(scene_kernel sk,client_information ci)
	{
		component comp,parent;
		part p;
		
		if((comp=sk.component_cont.search_component())==null)
			return;
		ci.request_response.println("component_id:			",	comp.component_id);
		ci.request_response.println("component_name:		",	comp.component_name);
		ci.request_response.println("component_part_name:	",	comp.part_name);
		ci.request_response.println("component_directory:	",	comp.component_directory_name);
		ci.request_response.println("component_file:		",	comp.component_file_name);
		ci.request_response.println();
		ci.request_response.println();
		ci.request_response.println();

		if((parent=sk.component_cont.get_component(comp.parent_component_id))==null)
			ci.request_response.println("No parent");
		else {
			ci.request_response.println("parent component_id:			",	parent.component_id);
			ci.request_response.println("parent component_name:		",		parent.component_name);
			ci.request_response.println("parent component_part_name:	",	parent.part_name);
			ci.request_response.println("parent component_directory:	",	parent.component_directory_name);
			ci.request_response.println("parent component_file:		",		parent.component_file_name);
		}
		ci.request_response.println();
		ci.request_response.println();
		ci.request_response.println();
		
		if(comp.children_number()>0) {
			for(int i=0,ni=comp.children_number();i<ni;i++) {
				String str="children	"+i+"	";
				ci.request_response.println(str+"component_id:			",	comp.children[i].component_id);
				ci.request_response.println(str+"component_name:		",	comp.children[i].component_name);
				ci.request_response.println(str+"component_part_name:	",	comp.children[i].part_name);
				ci.request_response.println(str+"component_directory:	",	comp.children[i].component_directory_name);
				ci.request_response.println(str+"component_file:		",	comp.children[i].component_file_name);
				ci.request_response.println();
			}
			
			ci.request_response.println();
			ci.request_response.println();
			ci.request_response.println();
		}
		
		if(comp.driver_number()>0) {
			for(int i=0,ni=comp.driver_number();i<ni;i++)
				if((p=comp.driver_array.get(i).component_part)!=null) {
					String str="part	"+i+"	";
					ci.request_response.println(str+"use_name:		",			p.user_name);
					ci.request_response.println(str+"system_name:		",		p.system_name);
					ci.request_response.println(str+"permanent_part_id:	",		p.permanent_part_id);
					ci.request_response.println(str+"render_id:		",			p.render_id);
					ci.request_response.println(str+"part_id:		",			p.part_id);
					ci.request_response.println(str+"mesh_directory:		",	p.directory_name);
					ci.request_response.println(str+"mesh_file:		",			p.mesh_file_name);
					ci.request_response.println();
				}
			ci.request_response.println();
			ci.request_response.println();
			ci.request_response.println();
		}
	}
}
