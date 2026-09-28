package kernel_create_top_assemble_part;

import java.util.Date;
import java.util.ArrayList;

import kernel_part.part;
import kernel_scene.part_package;
import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_part.permanent_part_id_encoder;
import kernel_common_class.debug_information;
import kernel_scene.scene_load_call_parameter;
import kernel_network.client_request_response;
import kernel_part.part_container_for_part_search;
import kernel_component.component_load_source_container;

public class mount_top_box_assemble_part 
{
	private static void mount_top_box_part(scene_kernel sk,
			component comp,component_load_source_container scene_component_load_source_cont,
			part_container_for_part_search part_search,client_request_response request_response)
	{
		int child_number;
		if((child_number=comp.children.size())<=0)
			return;
		if(comp.driver_array.size()>0)
			return;
		do{
			ArrayList<part> my_part_list;
			if((my_part_list=part_search.search_value_list(comp.part_name))==null)
				break;
			if(my_part_list.size()<=0)
				break;
			part my_part=my_part_list.get(0);
			try{
				component_driver my_driver=my_part.driver.create_component_driver(null,false,
						my_part,scene_component_load_source_cont,sk,request_response);

				if(my_driver!=null){
					comp.driver_array.add(my_driver);
					return;
				}
			}catch(Exception e){
				e.printStackTrace();
				
				debug_information.println(
					"create_component_driver fail in mount_top_box_part():	",e.toString());
				debug_information.println("Part user name:",	my_part.user_name);
				debug_information.println("Part system name:",	my_part.system_name);
				debug_information.println("Mesh_file_name:",	my_part.directory_name+my_part.mesh_file_name);
				debug_information.println("Material_file_name:",my_part.directory_name+my_part.material_file_name);
			}
		}while(false);

		for(int i=0;i<child_number;i++)
			mount_top_box_part(sk,comp.children.get(i),
				scene_component_load_source_cont,part_search,request_response);
	}
	public static void create_and_mount_top_box_part(scene_kernel sk,String fast_load_type,
			long part_type_code,permanent_part_id_encoder part_id_encoder,
			client_request_response request_response,scene_load_call_parameter load_par)
	{
		long start_time=new Date().getTime();
		ArrayList<part>top_box_part_list=null;
		
		if(sk.create_parameter.create_top_part_expand_ratio>1.0)
			if(sk.create_parameter.create_top_part_left_ratio>1.0)
				if(sk.component_cont.root_component!=null)
					top_box_part_list=new create_assemble_part(sk,fast_load_type,
							load_par,request_response,part_id_encoder).top_box_part;
		
		sk.render_cont.load_part(part_type_code,4,sk.system_par,sk.scene_par,
				"load_third_class_part",fast_load_type,load_par);

		sk.render_cont.scene_part_package=new part_package(fast_load_type,
				"create_second_class_package","create_second_boftal_file",
				sk.render_cont,1,sk.system_par,sk.scene_par,load_par);

		if(top_box_part_list!=null)
			if(top_box_part_list.size()>0)
				mount_top_box_part(sk,sk.component_cont.root_component,load_par.component_load_source_cont,
					new part_container_for_part_search(top_box_part_list),request_response);
		
		debug_information.println("Create top assemble time length:	",new Date().getTime()-start_time);
		debug_information.println();
	}
}
