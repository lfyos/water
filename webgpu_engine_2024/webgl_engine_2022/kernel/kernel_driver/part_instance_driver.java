package kernel_driver;

import kernel_common_class.debug_information;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_part.part;

public class part_instance_driver 
{
	public part_instance_driver()
	{	
	}
	public void destroy()
	{
	}
	public void response_init_part_data(part p,engine_kernel ek,client_information ci)
	{
	}
	public String[] response_part_event(part p,engine_kernel ek,client_information ci)
	{	
		String file_name;
		if((file_name=ci.request_response.get_parameter("file"))==null)
			file_name="";
		else{
			String request_charset=ci.request_response.implementor.get_request_charset();
			try {
				file_name=java.net.URLDecoder.decode(file_name,request_charset);
				file_name=java.net.URLDecoder.decode(file_name,request_charset);
			}catch(Exception e) {
				;
			}
			file_name=file_reader.separator(file_name);
		};
		file_name=file_directory.part_file_directory(p,ek.system_par,ek.scene_par)+file_name;
		
		if(file_reader.is_exist(file_name))
			return new String[] {file_name,ek.system_par.local_data_charset};
		
		if(p.part_from_id>=0){
			p=ek.render_cont.renders.get(p.render_id).parts.get(p.part_from_id);
			part_instance_driver part_instance=ci.part_instance_driver_cont.get_part_instance_driver(p);
			if(part_instance!=null)
				try{
					return part_instance.response_part_event(p,ek,ci);
				}catch(Exception e){
					e.printStackTrace();
					
					debug_information.println("Execute part response_part_event fail:",e.toString());
					debug_information.println("Part user name:",	p.user_name);
					debug_information.println("Part system name:",	p.system_name);
					debug_information.println("Mesh_file_name:",	p.directory_name+p.mesh_file_name);
					debug_information.println("Material_file_name:",p.directory_name+p.material_file_name);
					
				}
		}
		return null;
	}
}
