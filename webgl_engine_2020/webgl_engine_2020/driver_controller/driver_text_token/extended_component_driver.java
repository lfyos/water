package driver_text_token;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_network.client_request_response;
import kernel_part.part;

import kernel_transformation.point;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class extended_component_driver  extends component_driver
{
	private String directory_component_name,file_name,file_charset;
	
	private String text_component_name[];
	public int text_component_id[];
	
	public int token_component_id[];
	public point token_point[];
	public String token_text[];
	private boolean token_flag[],pickup_text_token_flag;
	
	public void destroy()
	{
		super.destroy();
		
		directory_component_name=null;
		file_name=null;
		file_charset=null;
		
		text_component_name=null;
		text_component_id=null;
		
		token_component_id=null;
		token_point=null;
		token_text=null;
		token_flag=null;
	}
	public extended_component_driver(part my_component_part,
			String my_directory_component_name,String my_file_name,
			String my_file_charset,String my_text_component_name[],
			boolean my_pickup_text_token_flag)
	{
		super(my_component_part);
		
		directory_component_name=my_directory_component_name;
		file_name=my_file_name;
		file_charset=my_file_charset;
		
		text_component_name=my_text_component_name;
		text_component_id=null;
		
		token_component_id=null;
		token_point=null;
		token_text=null;
		token_flag=null;
		
		pickup_text_token_flag=my_pickup_text_token_flag;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;
		
		component text_comp;
		int text_component_number=0;
		text_component_id=new int[text_component_name.length];
		for(int i=0,ni=text_component_name.length;i<ni;i++)
			if((text_comp=ek.component_cont.search_component(text_component_name[i]))!=null)
				text_component_id[text_component_number++]=text_comp.component_id;
		text_component_name=null;
		int bak[]=text_component_id;
		
		text_component_id=new int[text_component_number];
		
		token_component_id=new int[text_component_number];
		token_point=new point[text_component_number];
		token_text=new String[text_component_number];
		token_flag=new boolean[text_component_number];
		for(int i=0;i<text_component_number;i++) {
			text_component_id[i]=bak[i];
			token_component_id[i]=-1;
			token_point[i]=null;
			token_text[i]=null;
			token_flag[i]=false;
		}
		
		component directory_comp;
		if((directory_comp=ek.component_cont.search_component(directory_component_name))==null) {
			kernel_common_class.debug_information.println(
					"directory_component_name can NOT find a component	:	",	directory_component_name);
			kernel_common_class.debug_information.println("Component file name:	",
					comp.component_directory_name+comp.component_file_name);
			kernel_common_class.debug_information.println("Component name:	",	comp.component_name);
			kernel_common_class.debug_information.println("Part name:	",		comp.part_name);
			return;
		}
		if(pickup_text_token_flag)
			return;
		for(file_reader fr=new file_reader(directory_comp.component_directory_name+file_name,file_charset);;) {
			double x=fr.get_double(),y=fr.get_double(),z=fr.get_double();
			if(fr.eof()) {
				fr.close();
				break;
			}
			String component_name,check_text;
			if((component_name=fr.get_string())==null)
				break;
			if((check_text=fr.get_line())==null)
				break;
			if((check_text=check_text.trim()).length()<=0)
				break;
			component my_comp;
			if((my_comp=ek.component_cont.search_component(component_name))!=null)
				for(int i=0,ni=token_point.length;i<ni;i++)
					if(token_point[i]==null) {
						token_point[i]=new point(x,y,z);
						token_component_id[i]=my_comp.component_id;
						token_text[i]=check_text;
						token_flag[i]=false;
						break;
					}
		}
		return;
	}
	public instance_driver create_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_instance_driver(comp,driver_id);
	}
	private void write_out(component comp,engine_kernel ek)
	{
		if(pickup_text_token_flag)
			return;
		component directory_comp;
		if((directory_comp=ek.component_cont.search_component(directory_component_name))==null) {
			kernel_common_class.debug_information.println(
				"directory_component_name can NOT find a component	:	",directory_component_name);
			return;
		}
		file_writer fw=new file_writer(directory_comp.component_directory_name+file_name,file_charset);
		for(int i=0,ni=token_point.length;i<ni;i++)
			if((token_point[i]!=null)&&(!(token_flag[i]))){
				component my_comp;
				if((my_comp=ek.component_cont.get_component(token_component_id[i]))==null)
					continue;
				fw.	print  (		token_point[i].x).
					print  ("	",	token_point[i].y).
					print  ("	",	token_point[i].z).
					print  ("	",	my_comp.component_name).
					println("	",	token_text[i]);
			}
		fw.close();
	}
	public void test_pickup_token(engine_kernel ek,client_information ci)
	{
		if(!pickup_text_token_flag)
			return;
		
		int slot_id=-1;
		for(int i=0,ni=token_point.length;i<ni;i++)
			if(token_flag[i]) {
				slot_id=i;
				break;
			}
		if(slot_id<0)
			for(int i=0,ni=token_point.length;i<ni;i++)
				if(token_point[i]==null) {
					slot_id=i;
					break;
				}
		if(slot_id<0)
			return;
		component text_comp;
		if(ci.parameter.comp!=null)
			if(ci.parameter.comp.uniparameter.part_list_flag){
				point operated_point=new point(0,0,ci.parameter.depth);			
				operated_point=ci.selection_camera_result.negative_matrix.multiply(operated_point);
				operated_point=ci.parameter.comp.absolute_location.negative().multiply(operated_point);
				
				token_point[slot_id]		=operated_point;
				
				token_text[slot_id]			=ci.parameter.comp.component_name;
				if(ci.parameter.comp.uniparameter.display_part_name_or_component_name_flag)
					if(ci.parameter.comp.driver_number()>0)
						if(ci.parameter.comp.driver_array[0].component_part!=null)
							token_text[slot_id]=ci.parameter.comp.driver_array[0].component_part.user_name;
				
				token_component_id[slot_id]	=ci.parameter.comp.component_id;
				token_flag[slot_id]			=true;
				update_component_parameter_version();
				return;
			}
		token_component_id[slot_id]	=-1;
		token_point[slot_id]		=null;
		token_flag[slot_id]			=true;
		
		if((text_comp=ek.component_cont.get_component(text_component_id[slot_id]))!=null)
			if(text_comp.driver_number()>0)
				if(text_comp.driver_array[0] instanceof driver_text.extended_component_driver)
					((driver_text.extended_component_driver)(text_comp.driver_array[0])).clear_text();
		update_component_parameter_version();
	}
	public String[] response_event(component comp,
			int parameter_channel_id,engine_kernel ek,client_information ci)
	{	
		String str=ci.request_response.get_parameter("operation");
		switch((str==null)?"":str) {
		default:
			break;
		case "delete":
			if(pickup_text_token_flag)
				return null;
			if(ci.parameter.comp==null)
				return null;
			for(int i=0,ni=text_component_id.length;i<ni;i++)
				if(ci.parameter.comp.component_id==text_component_id[i]) {
					token_component_id[i]=-1;
					token_point[i]=null;

					component text_comp;
					if((text_comp=ek.component_cont.get_component(text_component_id[i]))!=null)
						if(text_comp.driver_number()>0)
							if(text_comp.driver_array[0] instanceof driver_text.extended_component_driver)
								((driver_text.extended_component_driver)(text_comp.driver_array[0])).clear_text();
					
					write_out(comp,ek);
					update_component_parameter_version();
					break;
				}
			break;
		case "append":
			if(pickup_text_token_flag)
				return null;
			String token_string;
			if((token_string=ci.request_response.get_parameter("value"))==null)
				break;
			try{
				token_string=java.net.URLDecoder.decode(token_string,ek.system_par.network_data_charset);
				token_string=java.net.URLDecoder.decode(token_string,ek.system_par.network_data_charset);
			}catch(Exception e){
				break;
			}
			
			point operated_point=new point(0,0,0);
			if((str=ci.request_response.get_parameter("x"))!=null)
				operated_point.x=Double.parseDouble(str);
			if((str=ci.request_response.get_parameter("y"))!=null)
				operated_point.y=Double.parseDouble(str);
			if((str=ci.request_response.get_parameter("z"))!=null)
				operated_point.z=Double.parseDouble(str);

			component my_comp;
			if((str=ci.request_response.get_parameter("component"))!=null){
				try{
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				}catch(Exception e){
					break;
				}
				my_comp=ek.component_cont.search_component(str);
			}else if((str=ci.request_response.get_parameter("component_id"))!=null){
				try{
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				}catch(Exception e){
					break;
				}
				my_comp=ek.component_cont.get_component(Integer.decode(str));
			}else if((my_comp=ci.parameter.comp)!=null){
				operated_point=new point(0,0,ci.parameter.depth);			
				operated_point=ci.selection_camera_result.negative_matrix.multiply(operated_point);
				operated_point=ci.parameter.comp.absolute_location.negative().multiply(operated_point);
			}
			
			if(my_comp==null)
				return null;
			
			for(int i=0,ni=token_point.length;i<ni;i++)
				if(token_point[i]==null){
					token_point[i]			=operated_point;
					token_text[i]			=token_string;
					token_component_id[i]	=my_comp.component_id;
					token_flag[i]			=false;
					write_out(comp,ek);
					update_component_parameter_version();
					
					break;
				}
			break;
		}
		return null;
	}
}