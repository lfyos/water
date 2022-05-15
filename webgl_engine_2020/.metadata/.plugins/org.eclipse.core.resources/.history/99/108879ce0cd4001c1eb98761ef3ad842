package kernel_component;

import kernel_common_class.change_name;
import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_transformation.location;

public class component_core_2 extends component_core_1
{
	public location relative_location;
	private boolean dynamic_location_flag;
	
	public void destroy()
	{
		super.destroy();
		relative_location=null;
	}
	public boolean get_dynamic_location_flag()
	{
		return dynamic_location_flag;
	}
	private location input_location_from_file(String file_name,String file_charset)
	{
		file_reader f=new file_reader(file_reader.separator(file_name),file_charset);
		location ret_val=f.eof()?new location():new location(f);
		f.close();
		return ret_val;
	}
	private location input_location(file_reader fr,client_request_response request_response)
	{
		String command	=fr.get_string();
		String name		=fr.get_string();
		String separator=fr.get_string();
		
		dynamic_location_flag=true;
		
		if((command==null)||(name==null)||(separator==null))
			return new location();
		if((command=command.trim()).isEmpty())
			return new location();
		if((name=name.trim()).isEmpty())
			return new location();
		if((separator=separator.trim()).isEmpty())
			return new location();

		try {
			switch(command.toLowerCase()) {
			default:
				dynamic_location_flag=false;
				try {
					return new location(
							Double.parseDouble(command),Double.parseDouble(name),	Double.parseDouble(separator),	fr.get_double(),
							fr.get_double(),			fr.get_double(),			fr.get_double(),				fr.get_double(),
							fr.get_double(),			fr.get_double(),			fr.get_double(),				fr.get_double(),
							fr.get_double(),			fr.get_double(),			fr.get_double(),				fr.get_double());
				}catch(Exception e) {
					debug_information.println("Component location error:	",command+"	"+name+"	"+separator);
					debug_information.println("Component location file:		",fr.directory_name+fr.file_name);
					debug_information.println(e.toString());
					e.printStackTrace();
					
					return new location();
				}
			case "client_location":
				if((name=request_response.get_parameter(name))!=null)
					return new location(name,separator);
				return new location();
			case "environment_location":
				if((name=System.getenv(name))!=null)
					return new location(name,separator);
				return new location();
			case "client_environment_location":
				if((name=request_response.get_parameter(name))!=null)
					if((name=System.getenv(name))!=null)
						return new location(name,separator);
				return new location();
			case "relative_file_location":
				fr.insert_string(new String[] {separator});
				if((name=request_response.get_parameter(name))!=null)
					return input_location_from_file(fr.directory_name+name,fr.get_charset());
				return new location();
			case "absolute_file_location":
				fr.insert_string(new String[] {separator});
				if((name=request_response.get_parameter(name))!=null)
					return input_location_from_file(name,fr.get_charset());
				return new location();
			}
		}catch(Exception e){
			debug_information.println("private location input_location fail:	",e.toString());
			e.printStackTrace();
			return new location();
		}
	}
	public component_core_2(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,change_name change_part_name,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,change_part_name,ccp);
		
		relative_location=input_location(fr,ccp.request_response);
		if(uniparameter.normalize_location_flag)
			relative_location=relative_location.normalize();
	}
}