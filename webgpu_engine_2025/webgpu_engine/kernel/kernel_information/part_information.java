package kernel_information;

import java.util.ArrayList;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_scene.client_information;
import kernel_file_manager.file_directory;
import kernel_part.buffer_object_file_modify_time_and_length_item;

public class part_information extends jason_creator
{
	private part p;
	private system_parameter part_system_par;
	private scene_parameter part_scene_par;

	public void print()
	{
		print("top_box_part_flag",			p.is_top_box_part());
		print("bottom_box_part_flag",		p.is_bottom_box_part());
		print("normal_box_part_flag",		p.is_normal_part());
		print("part_type_id",				p.part_type_id);
		
		print("render_id",					p.render_id);
		print("part_id",					p.part_id);
		print("part_from_id",				p.part_from_id);
		print("permanent_part_id",			p.permanent_part_id);
		print("permanent_part_from_id",		p.permanent_part_from_id);
		
		print("directory_name",				p.directory_name);
		print("system_name",				p.system_name);
		print("mesh_file_name",				p.mesh_file_name);
		print("material_file_name",			p.material_file_name);
		print("user_name",					p.user_name);
		print("description_file_name",		p.description_file_name);
		print("audio_file_name",			p.audio_file_name);
		print("temp_directory_name",		
				file_directory.part_temporary_directory(p, part_system_par, part_scene_par));
		
		print("total_face_primitive_number",	p.part_mesh.total_face_primitive_number);
		print("total_edge_primitive_number",	p.part_mesh.total_edge_primitive_number);
		print("total_point_primitive_number",	p.part_mesh.total_point_primitive_number);
		
		int		total_buffer_object_file_number=1;
		long	total_buffer_object_text_data_length=p.boftal.buffer_object_head_length;
		
		ArrayList<buffer_object_file_modify_time_and_length_item> item_list;
		for(int i=0,ni=p.boftal.boftal_list.size();i<ni;i++)
			for(int j=0,nj=(item_list=p.boftal.boftal_list.get(i)).size();j<nj;j++) {
				buffer_object_file_modify_time_and_length_item item=item_list.get(j);
				if(item.buffer_object_file_in_head_flag)
					continue;
				total_buffer_object_file_number++;
				total_buffer_object_text_data_length+=item.buffer_object_text_file_length;
			}
		print("total_buffer_object_file_number",		total_buffer_object_file_number);
		print("total_buffer_object_text_data_length",	total_buffer_object_text_data_length);
	}
	
	public part_information(part my_p,scene_kernel my_sk,client_information my_ci)
	{
		super(my_ci.request_response);
		p=my_p;
		part_system_par=my_sk.system_par;
		part_scene_par=my_sk.scene_par;
	}
}
