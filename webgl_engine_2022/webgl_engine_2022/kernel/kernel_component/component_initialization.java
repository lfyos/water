package kernel_component;

import java.util.ArrayList;

import kernel_file_manager.file_reader;

public class component_initialization 
{
	public ArrayList<String[]>  program_and_charset;
	
	public component_initialization()
	{
		program_and_charset=new ArrayList<String[]>();
	}
	public void destroy()
	{
		if(program_and_charset!=null){
			program_and_charset.clear();
			program_and_charset=null;
		}
	}
	public void create_initialization(file_reader fr,String initialization_type)
	{
		String my_initialization_program=null;
		String my_initialization_program_charset=null;
		String line_str,terminated_token;
		
		switch(initialization_type){
		default:
			return;
		case "file_program":
			if((my_initialization_program=file_reader.separator(fr.get_string()))!=null)
				if((my_initialization_program=my_initialization_program.trim()).length()>0)
					if((my_initialization_program_charset=fr.get_charset())!=null)
						if((my_initialization_program_charset=my_initialization_program_charset.trim()).length()>0)
							break;
			return;
		case "charset_file_program":
			if((my_initialization_program=file_reader.separator(fr.get_string()))!=null)
				if((my_initialization_program=my_initialization_program.trim()).length()>0)
					if((my_initialization_program_charset=fr.get_string())!=null)
						if((my_initialization_program_charset=my_initialization_program_charset.trim()).length()>0)
							break;
			return;
		case "token_program":
			if((terminated_token=fr.get_line())!=null)
				if((terminated_token=terminated_token.trim()).length()>0) {
					StringBuffer program_buf=new StringBuffer();
					for(int flag=0;!(fr.eof());) {
						if((line_str=fr.get_line())==null)
							continue;
						if(line_str.trim().compareTo(terminated_token)==0)
							break;
						if((flag++)>0)
							program_buf.append("\n");
						program_buf.append(line_str);
					}
					if((my_initialization_program=program_buf.toString())!=null)
						if(my_initialization_program.length()>0)
							break;
				}
			return;
		}
		program_and_charset.add(program_and_charset.size(),
			new String[] {my_initialization_program,my_initialization_program_charset});
	}
}
