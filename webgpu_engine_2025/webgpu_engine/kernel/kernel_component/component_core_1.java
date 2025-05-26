package kernel_component;

import kernel_common_class.const_value;
import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_transformation.location;
import kernel_transformation.point;

public class component_core_1 extends component_core_0
{	
	public location relative_location;
	
	public void destroy()
	{
		super.destroy();
		relative_location=null;
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
		String command,name,sepa,charset;
		if((command=fr.get_string())==null) {
			debug_information.println("input_location fail:	((command=fr.get_string())==null)");
			debug_information.println("location file:	",fr.directory_name+fr.file_name);
			return new location();
		}
		try {
			switch(command.trim()){
			default:
				fr.push_string(command);
				return new location(fr);
			case "identity":
				return new location();
			case "move":
				return location.move_rotate(fr.get_double(), fr.get_double(), fr.get_double(), 0, 0, 0);
			case "rotate":
				return location.move_rotate(0,0,0,fr.get_double(), fr.get_double(), fr.get_double());	
			case "move_rotate":
				return location.move_rotate(
							fr.get_double(),fr.get_double(),fr.get_double(),
							fr.get_double(),fr.get_double(), fr.get_double());
			case "p0pxpy":
			{
				point p0=new point(fr.get_double(),fr.get_double(),fr.get_double());
				point px=new point(fr.get_double(),fr.get_double(),fr.get_double());
				point py=new point(fr.get_double(),fr.get_double(),fr.get_double());
				point dx=px.sub(p0),dy=py.sub(p0),dz=dx.cross(dy);
				if(dz.distance2()<const_value.min_value)
					return location.move_rotate(p0.x,p0.y,p0.z,0,0,0);
				if((dy=dz.cross(dx)).distance2()<const_value.min_value)
					return location.move_rotate(p0.x,p0.y,p0.z,0,0,0);
				return new location(
								p0,
								p0.add(dx.expand(1.0)),
								p0.add(dy.expand(1.0)),
								p0.add(dz.expand(1.0))
						).multiply(location.standard_negative);
			}
			case "client_location":
				name	=fr.get_string();
				sepa	=fr.get_string();
				if((name!=null)&&(sepa!=null))
					if((name=request_response.get_parameter(name.trim()))!=null)
						if((name=name.trim()).length()>0)
							return new location(name,sepa.trim());
				return new location();
			case "environment_location":
				name	=fr.get_string();
				sepa	=fr.get_string();
				if((name!=null)&&(sepa!=null))
					if((name=System.getenv(name.trim()))!=null)
						return new location(name.trim(),sepa.trim());
				return new location();
			case "client_environment_location":
				name	=fr.get_string();
				sepa	=fr.get_string();
				if((name!=null)&&(sepa!=null))
					if((name=request_response.get_parameter(name.trim()))!=null)
						if((name=name.trim()).length()>0)
							if((name=System.getenv(name))!=null)
								return new location(name.trim(),sepa.trim());
				return new location();
			case "relative_file_location":
				if((name=fr.get_string())!=null)
					if((name=request_response.get_parameter(name.trim()))!=null)
						if((name=name.trim()).length()>0)
							return input_location_from_file(
										fr.directory_name+name,fr.get_charset());
				return new location();
			case "absolute_file_location":
				if((name=fr.get_string())!=null)
					if((name=request_response.get_parameter(name.trim()))!=null)
						if((name=name.trim()).length()>0)
							return input_location_from_file(name,fr.get_charset());
				return new location();
			case "charset_relative_file_location":
				name	=fr.get_string();
				charset	=fr.get_string();
				if((name!=null)&&(charset!=null))
					if((name=request_response.get_parameter(name.trim()))!=null)
						if((name=name.trim()).length()>0)
							return input_location_from_file(
										fr.directory_name+name.trim(),charset.trim());
				return new location();
			case "charset_absolute_file_location":
				name	=fr.get_string();
				charset	=fr.get_string();
				if((name!=null)&&(charset!=null))
					if((name=request_response.get_parameter(name.trim()))!=null)
						if((name=name.trim()).length()>0)
							return input_location_from_file(name,charset.trim());
				return new location();
			}
		}catch(Exception e){
			e.printStackTrace();
			debug_information.println("input_location Exception:	",e.toString());
			debug_information.println("location file:	",fr.directory_name+fr.file_name);
			return new location();
		}
	}
	public component_core_1(String token_string,file_reader fr,boolean part_list_flag,
			boolean normalize_location_flag,component_construction_parameter ccp)
	{
		super(token_string,fr,part_list_flag,normalize_location_flag,ccp);
		
		relative_location=input_location(fr,ccp.request_response);
		if(uniparameter.normalize_location_flag)
			relative_location=relative_location.normalize();
	}
}