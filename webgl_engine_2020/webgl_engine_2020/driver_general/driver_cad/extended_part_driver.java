package driver_cad;

import java.io.File;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.part_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_engine.system_parameter;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;
import kernel_part.part;
import kernel_part.part_rude;
import kernel_part.part_container_for_part_search;
import kernel_transformation.box;
import kernel_transformation.location;
import kernel_transformation.point;

public class extended_part_driver extends part_driver
{
	private String assemble_directory_name,assemble_file_charset,light_file_name,light_file_charset;
	private boolean create_normal_flag;
	
	public extended_part_driver(part p,String my_assemble_directory_name,String my_assemble_file_charset,
			String my_light_file_name,String my_light_file_charset,boolean my_create_normal_flag,
			system_parameter system_par,client_request_response request_response)
	{
		super();
		assemble_directory_name=my_assemble_directory_name;
		assemble_file_charset=my_assemble_file_charset;
		light_file_name=my_light_file_name;
		light_file_charset=my_light_file_charset;
		create_normal_flag=my_create_normal_flag;
	}
	public void destroy()
	{	
		super.destroy();
		light_file_name=null;
		light_file_charset=null;
	}
	private void set_component_scale(component my_comp,double my_x_scale,double my_y_scale,double my_z_scale)
	{
		if(my_comp==null)
			return;
		for(int i=0,ni=my_comp.driver_number();i<ni;i++)
			if(my_comp.driver_array[i] instanceof extended_component_driver) {
				extended_component_driver ecd=(extended_component_driver)(my_comp.driver_array[i]);
				ecd.parameter.x_scale=my_x_scale;
				ecd.parameter.y_scale=my_y_scale;
				ecd.parameter.z_scale=my_z_scale;
			}
		for(int i=0,ni=my_comp.children_number();i<ni;i++)
			set_component_scale(my_comp.children[i],my_x_scale,my_y_scale,my_z_scale);
	}
	public void initialize_part_driver(part p,engine_kernel ek,client_request_response request_response)
	{
		if(p.part_id!=0)
			return;
		
		file_reader fr=new file_reader(p.directory_name+"modification.txt",p.file_charset);
		while(!(fr.eof())) {
			String component_name=fr.get_string();
			String component_operation=fr.get_string();
			if((component_name==null)||(component_operation==null))
				continue;
			if((component_name.isEmpty())||(component_operation.isEmpty()))
				continue;
			component my_comp=ek.component_cont.search_component(component_name);
			
			switch(component_operation){
			case "scale":
				if(my_comp==null) {
					fr.get_double();
					fr.get_double();
					fr.get_double();
				}else
					set_component_scale(my_comp,fr.get_double(),fr.get_double(),fr.get_double());
				break;
			case "move":
				if(my_comp==null) {
					fr.get_double();
					fr.get_double();
					fr.get_double();
				}else
					my_comp.relative_location=my_comp.relative_location.multiply(
							location.move_rotate(fr.get_double(),fr.get_double(),fr.get_double(),0,0,0));
				break;
			case "rotate":
				if(my_comp==null) {
					fr.get_double();
					fr.get_double();
					fr.get_double();
				}else
					my_comp.relative_location=my_comp.relative_location.multiply(
							location.move_rotate(0,0,0,fr.get_double(),fr.get_double(),fr.get_double()));
				break;
			}
		}
		fr.close();
		ek.mark_reset_flag();
		return;
	}
	public void response_init_data(part p,engine_kernel ek,client_information ci)
	{
	}
	public part_driver clone(part parent,part p,
			system_parameter system_par,client_request_response request_response)
	{
		return new extended_part_driver(p,assemble_directory_name,assemble_file_charset,
				light_file_name,light_file_charset,create_normal_flag,system_par,request_response);
	}
	public int caculate_material_id(
			part p,String type_str,int body_id,int face_id,int loop_id,int edge_id,
			String material_x,String material_y,String material_z,String material_w)
	{
		return 0;
	}
	public part_rude create_part_mesh_and_buffer_object_head(
			part p,file_writer buffer_object_file_writer,
			part_container_for_part_search pcps,system_parameter system_par)
	{
		buffer_object_file_writer.println("		{");
		buffer_object_file_writer.println(file_reader.get_text(light_file_name,light_file_charset));
		buffer_object_file_writer.println(file_reader.get_text(p.directory_name+p.material_file_name,p.file_charset));
		buffer_object_file_writer.println("		}");
		
		return super.create_part_mesh_and_buffer_object_head(p,buffer_object_file_writer,pcps,system_par);
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
		if(comp!=null)
			for(int i=0,ni=comp.driver_number();i<ni;i++)
				if(comp.driver_array[i] instanceof extended_component_driver) {
					extended_component_driver ecd=(extended_component_driver)(comp.driver_array[i]);
					point my_p0=(p0==null)?null:new point(p0.x/ecd.parameter.x_scale,p0.y/ecd.parameter.y_scale,p0.z/ecd.parameter.z_scale);
					point my_p1=(p1==null)?null:new point(p1.x/ecd.parameter.x_scale,p1.y/ecd.parameter.y_scale,p1.z/ecd.parameter.z_scale);
					box ret_val=super.caculate_part_box(p,comp,driver_id,body_id,face_id,loop_id,edge_id,point_id,my_p0,my_p1);
					if(ret_val!=null){
						ret_val.p[0].x*=ecd.parameter.x_scale;
						ret_val.p[0].y*=ecd.parameter.y_scale;
						ret_val.p[0].z*=ecd.parameter.z_scale;
	
						ret_val.p[1].x*=ecd.parameter.x_scale;
						ret_val.p[1].y*=ecd.parameter.y_scale;
						ret_val.p[1].z*=ecd.parameter.z_scale;
					}
					return ret_val;
				}			
		return super.caculate_part_box(p,comp,driver_id,body_id,face_id,loop_id,edge_id,point_id,p0,p1);
	}
	public String [][]assemble_file_name_and_file_charset(file_reader fr,part p,
			engine_kernel ek,client_request_response request_response)
	{
		String assemble_file_name=assemble_directory_name+fr.get_string();
		if(!(new File(assemble_file_name).exists()))
			return null;
		return new String[][]
			{
				new String[]{assemble_file_name},
				new String[]{assemble_file_charset}
			};
	}
	public String[] response_event(part p,engine_kernel ek,client_information ci)
	{
		return super.response_event(p,ek,ci);
	}
}
