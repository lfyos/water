package driver_menu;

import java.io.File;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.part_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_engine.scene_parameter;
import kernel_engine.system_parameter;
import kernel_file_manager.file_directory;
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
	public extended_part_driver()
	{
		super();
	}
	public void destroy()
	{	
		super.destroy();
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
		return new extended_part_driver();
	}
	public int caculate_material_id(
			part p,String type_str,int body_id,int face_id,int loop_id,int edge_id,
			String material_x,String material_y,String material_z,String material_w)
	{
		return 0;
	}
	public part_rude create_part_mesh_and_buffer_object_head(part p,
			file_writer buffer_object_file_writer,part_container_for_part_search pcps,
			system_parameter system_par,scene_parameter scene_par)
	{
		return super.create_part_mesh_and_buffer_object_head(p,buffer_object_file_writer,pcps,system_par,scene_par);
	}
	public component_driver create_component_driver(file_reader fr,boolean rollback_flag,
			part my_component_part,engine_kernel ek,client_request_response request_response)
	{
		String original_file_name=fr.directory_name+file_reader.separator(fr.get_string());
		String file_name=original_file_name;
		boolean file_type=false;
		
		do {
			File f=new File(my_component_part.directory_name+my_component_part.mesh_file_name);
			String directory_name=file_reader.separator(f.getParent())+File.separatorChar;
			int directory_length=directory_name.length();
			if(original_file_name.length()>=directory_length)
				if(original_file_name.substring(0,directory_length).compareTo(directory_name)==0) {
					file_name=file_directory.part_file_directory(my_component_part,
							ek.system_par,ek.scene_par)+original_file_name.substring(directory_length);
					if(new File(original_file_name).lastModified()>=new File(file_name).lastModified())
						file_writer.file_copy(original_file_name, file_name);
					file_type=true;
					break;
				}
			directory_name=ek.system_par.data_root_directory_name;
			directory_length=directory_name.length();
			if(original_file_name.length()>directory_length)
				if(original_file_name.substring(0,directory_length).compareTo(directory_name)==0){
					file_name=file_directory.part_file_directory(my_component_part,
							ek.system_par,ek.scene_par)+original_file_name.substring(directory_length);
					if(new File(original_file_name).lastModified()>=new File(file_name).lastModified())
						file_writer.file_copy(original_file_name, file_name);
					file_type=true;
					break;
				}
		}while(false);
		
		String material_file_name=my_component_part.directory_name+my_component_part.material_file_name;
		file_reader fm=new file_reader(material_file_name,my_component_part.file_charset);
		double min_x=fm.get_double(),max_x=fm.get_double(),min_y=fm.get_double(),max_y=fm.get_double();
		fm.close();

		return new extended_component_driver(my_component_part,file_name,file_type,
						fr.get_double(),fr.get_double(),min_x,max_x,min_y,max_y);
	}
	public box caculate_part_box(part p,component comp,int driver_id,
			int body_id,int face_id,int loop_id,int edge_id,int point_id,
			point p0,point p1)
	{
		return null;
	}
	public String [][]assemble_file_name_and_file_charset(file_reader fr,part p,
			engine_kernel ek,client_request_response request_response)
	{
		String directory_name=file_reader.separator(
					new File(p.directory_name+p.mesh_file_name).getParent())+File.separatorChar;
		String file_name=directory_name+file_reader.separator(fr.get_string());
		
		return new String[][]
		{
			new String[]{file_name},
			new String[]{p.file_charset}
		};
	}
	public String[] response_event(part p,engine_kernel ek,client_information ci)
	{		
		return super.response_event(p,ek,ci);
	}
}
