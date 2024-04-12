package kernel_part;

import kernel_driver.part_driver;
import kernel_common_class.debug_information;

public class caculate_material_id
{
	public static int caculate(part_driver pd,int max_material_id,
			part p,String type_str,int body_id,int face_id,int loop_id,int edge_id,
			String material_x,String material_y,String material_z,String material_w)
	{
		int material_id=0;
		try {
			material_id=pd.caculate_material_id(
				p,type_str,body_id,face_id,loop_id,edge_id,
				material_x,material_y,material_z,material_w);
		}catch(Exception e) {
			e.printStackTrace();
			
			debug_information.println("Execte part caculate_material_id() fail",e.toString());
			
			debug_information.print ("body_id:",body_id);
			debug_information.print (",face_id:",face_id);
			debug_information.print (",loop_id:",loop_id);
			debug_information.println(",edge_id:",edge_id);
			
			debug_information.println("Part user name:",	p.user_name);
			debug_information.println("Part system name:",	p.system_name);
			debug_information.println("Mesh_file_name:",	p.directory_name+p.mesh_file_name);
			debug_information.println("Material_file_name:",p.directory_name+p.material_file_name);
		}
		return (max_material_id+material_id%max_material_id)%max_material_id;
	}
}
