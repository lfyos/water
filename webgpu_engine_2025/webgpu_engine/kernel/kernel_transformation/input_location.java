package kernel_transformation;

import kernel_scene.scene_parameter;
import kernel_scene.system_parameter;
import kernel_common_class.const_value;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_directory;
import kernel_common_class.debug_information;

public class input_location 
{
	public static location do_input(file_reader fr,
			system_parameter system_par,scene_parameter scene_par)
	{
		String name,sepa,charset;
		if((name=fr.get_string())==null) {
			debug_information.println("input_location fail:	((command=fr.get_string())==null)");
			debug_information.println("location file:	",fr.directory_name+fr.file_name);
			return new location();
		}
		try {
			switch(name.trim()){
			default:
				fr.push_string(name);
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
				point dx=px.sub(p0);
				point dy=py.sub(p0);
				point dz=dx.cross(dy);
				
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
			case "environment_location":
				name	=fr.get_string();
				sepa	=fr.get_string();
				if((name!=null)&&(sepa!=null))
					if((name=name.trim()).length()>0)
						if((name=scene_par.scene_environment.search_change_name(name,null))!=null)
							if((name=name.trim()).length()>0)
								return new location(name,sepa.trim());
				return new location();
			case "relative_file_location":
				if((name=fr.get_string())!=null)
					if((name=name.trim()).length()>0) {
						name=fr.directory_name+file_directory.replace_special_char(name);
						file_reader f=new file_reader(name,fr.get_charset());
						if(!(f.eof())) {
							location ret_val=new location(f);
							f.close();
							return ret_val;
						}
						f.close();
						debug_information.print  ("input_location error(relative_file_location),");
						debug_information.println("location file:	",name);
					}		
				return new location();
			case "absolute_file_location":
				if((name=fr.get_string())!=null)
					if((name=name.trim()).length()>0) {
						name=file_directory.replace_special_char(name);
						file_reader f=new file_reader(name,fr.get_charset());
						if(!(f.eof())){
							location ret_val=new location(f);
							f.close();
							return ret_val;
						}
						f.close();
						debug_information.print  ("input_location error(absolute_file_location),");
						debug_information.println("location file:	",name);
					}
				return new location();
			case "charset_relative_file_location":
				name	=fr.get_string();
				charset	=fr.get_string();
				if((name!=null)&&(charset!=null))
					if((name=name.trim()).length()>0){
						name=fr.directory_name+file_directory.replace_special_char(name);
						file_reader f=new file_reader(name,charset.trim());
						if(!(f.eof())) {
							location ret_val=new location(f);
							f.close();
							return ret_val;
						}
						f.close();
						debug_information.print  ("input_location error(charset_relative_file_location),");
						debug_information.println("location file:	",name);	
					}	
				return new location();
			case "charset_absolute_file_location":
				name	=fr.get_string();
				charset	=fr.get_string();
				if((name!=null)&&(charset!=null))
					if((name=name.trim()).length()>0){
						name=file_directory.replace_special_char(name);
						file_reader f=new file_reader(name,charset.trim());
						if(!(f.eof())){
							location ret_val=new location(f);
							f.close();
							return ret_val;
						}
						f.close();
						debug_information.print  ("input_location error(charset_absolute_file_location),");
						debug_information.println("location file:	",name);
					}
				return new location();
			case "environment_relative_file_location":
				if((name=fr.get_string())!=null)
					if((name=name.trim()).length()>0) 
						if((name=scene_par.scene_environment.search_change_name(name,null))!=null)
							if((name=name.trim()).length()>0){
								name=fr.directory_name+file_directory.replace_special_char(name);
								file_reader f=new file_reader(name,fr.get_charset());
								if(!(f.eof())) {
									location ret_val=new location(f);
									f.close();
									return ret_val;
								}
								f.close();
								debug_information.print  ("input_location error(environment_relative_file_location),");
								debug_information.println("location file:	",name);
							}		
				return new location();
			case "environment_absolute_file_location":
				if((name=fr.get_string())!=null)
					if((name=name.trim()).length()>0) 
						if((name=scene_par.scene_environment.search_change_name(name,null))!=null)
							if((name=name.trim()).length()>0){
								name=file_directory.replace_special_char(name);
								file_reader f=new file_reader(name,fr.get_charset());
								if(!(f.eof())) {
									location ret_val=new location(f);
									f.close();
									return ret_val;
								}
								f.close();
								debug_information.print  ("input_location error(client_absolute_file_location),");
								debug_information.println("location file:	",name);
							}		
				return new location();
			case "environment_charset_relative_file_location":
				name	=fr.get_string();
				charset	=fr.get_string();
				if((name!=null)&&(charset!=null))
					if((name=name.trim()).length()>0)
						if((name=scene_par.scene_environment.search_change_name(name,null))!=null)
							if((name=name.trim()).length()>0){
								name=fr.directory_name+file_directory.replace_special_char(name);
								file_reader f=new file_reader(name,charset.trim());
								if(!(f.eof())) {
									location ret_val=new location(f);
									f.close();
									return ret_val;
								}
								f.close();
								debug_information.print  ("input_location error(client_charset_relative_file_location),");
								debug_information.println("location file:	",name);	
							}	
				return new location();
			case "environment_charset_absolute_file_location":
				name	=fr.get_string();
				charset	=fr.get_string();
				if((name!=null)&&(charset!=null))
					if((name=name.trim()).length()>0)
						if((name=scene_par.scene_environment.search_change_name(name,null))!=null)
							if((name=name.trim()).length()>0){
								name=file_directory.replace_special_char(name);
								file_reader f=new file_reader(name,charset.trim());
								if(!(f.eof())){
									location ret_val=new location(f);
									f.close();
									return ret_val;
								}
								f.close();
								debug_information.print  ("input_location error(client_charset_absolute_file_location),");
								debug_information.println("location file:	",name);
							}
				return new location();
			}
		}catch(Exception e){
			e.printStackTrace();
			debug_information.println("input_location Exception:	",e.toString());
			debug_information.println("location file:	",fr.directory_name+fr.file_name);
			return new location();
		}
	}
}
