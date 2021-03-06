package kernel_component;

import kernel_file_manager.file_reader;

public class component_initialization 
{
	public String  initialization_program[],initialization_program_charset[];
	
	public component_initialization()
	{
		initialization_program			=null;
		initialization_program_charset	=null;
	}
	public void destroy()
	{
		if(initialization_program!=null){
			for(int i=0,ni=initialization_program.length;i<ni;i++)
				initialization_program[i]=null;
			initialization_program=null;
		}
		if(initialization_program_charset!=null) {
			for(int i=0,ni=initialization_program_charset.length;i<ni;i++)
				initialization_program_charset[i]=null;
			initialization_program_charset=null;
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
					while(!(fr.eof())) {
						if((line_str=fr.get_line())!=null){
							if(line_str.trim().compareTo(terminated_token)==0)
								break;
							if(my_initialization_program==null)
								my_initialization_program =line_str;
							else
								my_initialization_program+="\n"+line_str;
						}
					}
					if(my_initialization_program!=null)
						if(my_initialization_program.trim().length()>0)
							break;
				}
			return;
		}
		if(my_initialization_program.replaceAll("[\b\f\n\r\t ]","").trim().length()<=0)
			return;
		if(initialization_program==null){
			initialization_program			=new String[]{my_initialization_program};
			initialization_program_charset	=new String[]{my_initialization_program_charset};
		}else{
			String initialization_program_bak[]			=initialization_program;
			String initialization_program_charset_bak[]	=initialization_program_charset;
			
			initialization_program			=new String[initialization_program.length+1];
			initialization_program_charset	=new String[initialization_program_charset.length+1];
			
			for(int i=0,ni=initialization_program_bak.length;i<ni;i++){
				initialization_program[i]			=initialization_program_bak[i];
				initialization_program_charset[i]	=initialization_program_charset_bak[i];
			}
			initialization_program		 	[initialization_program.length-1]		 =my_initialization_program;
			initialization_program_charset	[initialization_program_charset.length-1]=my_initialization_program_charset;
		}
	}
}
