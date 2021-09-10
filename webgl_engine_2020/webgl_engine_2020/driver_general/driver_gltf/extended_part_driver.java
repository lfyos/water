package driver_gltf;

import kernel_component.component;
import kernel_driver.component_driver;

import kernel_driver.part_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_part.part_container_for_part_search;
import kernel_transformation.box;
import kernel_transformation.point;

public class extended_part_driver extends part_driver
{
	private String light_file_name;
	private int part_group_id;
	
	public extended_part_driver(String my_light_file_name,int my_part_group_id)
	{
		super();
		light_file_name	=new String(my_light_file_name);
		part_group_id	=my_part_group_id;
	}
	public void destroy()
	{	
		super.destroy();
		light_file_name=null;
	}
	public void initialize_part_driver(part p,engine_kernel ek,client_request_response request_response)
	{
	}
	public void response_init_data(part p,engine_kernel ek,client_information ci)
	{
	}
	public part_driver clone(part parent,part p,
			system_parameter system_par,client_request_response request_response)
	{
		return new extended_part_driver(light_file_name,part_group_id);
	}
	public int caculate_material_id(
			part p,String type_str,int body_id,int face_id,int loop_id,int edge_id,
			String material_x,String material_y,String material_z,String material_w)
	{
		return Integer.decode(material_w);
	}
	private void append_file(file_writer fw,String pre_str,String file_name,String file_charset)
	{
		for(file_reader f=new file_reader(file_name,file_charset);;) {
			if(f.eof()) {
				f.close();
				return;
			}
			String str=f.get_line();
			if(str!=null)
				fw.println(pre_str,str);
		}
	}
	public part_rude create_part_mesh_and_buffer_object_head(part p,
			file_writer buffer_object_file_writer,part_container_for_part_search pcps,
			system_parameter system_par,scene_parameter scene_par)
	{
		if(buffer_object_file_writer!=null) {
			String charset=buffer_object_file_writer.get_charset();
			buffer_object_file_writer.println("\t\t{");
			append_file(buffer_object_file_writer,"\t\t",p.directory_name+"gltf.material",		charset);
			append_file(buffer_object_file_writer,"\t\t",p.directory_name+p.material_file_name,	charset);
			append_file(buffer_object_file_writer,"\t\t",light_file_name,						charset);
			buffer_object_file_writer.println("\t\t\t\"part_group\"	:	",part_group_id);
			buffer_object_file_writer.println("\t\t}");
		}
		return super.create_part_mesh_and_buffer_object_head(p,buffer_object_file_writer,pcps,system_par,scene_par);
	}
	public component_driver create_component_driver(file_reader fr,boolean rollback_flag,
			part my_component_part,engine_kernel ek,client_request_response request_response)
	{
		return new extended_component_driver(my_component_part);
	}
	public box caculate_part_box(part p,component comp,int driver_id,
			int body_id,int face_id,int loop_id,int edge_id,int point_id,
			point p0,point p1)
	{
		return super.caculate_part_box(p,comp,driver_id,body_id,face_id,loop_id,edge_id,point_id,p0,p1);
	}
	public String [][]assemble_file_name_and_file_charset(file_reader fr,part p,
			engine_kernel ek,client_request_response request_response)
	{
		return new String[][]
		{
			new String[] {p.directory_name+"gltf.assemble"},
			new String[] {p.file_charset}
		};
	}
	public String[] response_event(part p,engine_kernel ek,client_information ci)
	{			
		return super.response_event(p,ek,ci);
	}
}