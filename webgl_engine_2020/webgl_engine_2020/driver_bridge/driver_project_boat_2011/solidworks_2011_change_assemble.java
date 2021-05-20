package driver_project_boat_2011;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_file_manager.travel_through_directory ;

public class solidworks_2011_change_assemble extends travel_through_directory 
{
	private void skip_space(int space_number,file_writer fw)
	{
		fw.println();
		for(int i=0;i<space_number;i++)
			fw.print("\t");
	}
	private void change_item(file_reader fr,file_writer fw,int space_number)
	{
		int child_number;
		
		skip_space(space_number,fw);		fr.get_string();			fw.print(fr.get_string());
		skip_space(space_number,fw);		fr.get_string();			fw.print(fr.get_string());
		skip_space(space_number,fw);		fr.get_string();			fw.print(fr.get_double());
		for(int i=0;i<15;i++)
			fw.print("\t",fr.get_double());
		skip_space(space_number,fw);		fr.get_string();			fw.println(child_number=fr.get_int());
		
		for(int i=0;i<child_number;i++)
			change_item(fr,fw,space_number+1);
		return;
	}
	public void operate_file(String file_name,String file_system_charset)
	{
		String str;
		
		file_writer fw;
		file_reader fr=new file_reader(file_name,file_system_charset);

		while(true){
			if(fr.eof()||fr.error_flag())
				break;
			if((str=fr.get_line())==null)
				continue;
			if((str=str.trim()).length()==0)
				continue;
			if(str.length()==1)
				break;
			if(str.substring(0,2).compareTo("/*")!=0)
				break;
			fr.close();
			return;
		}
	
		fr.close();
		
		file_writer.file_rename(file_name, file_name+".liufuyan.lfy");
		fr=new file_reader(file_name+".liufuyan.lfy",file_system_charset);
		fw=new file_writer(file_name,file_system_charset);
		fw.println("/*		change from solidworks 2011		*/	");
		
		change_item(fr,fw,0);
		
		fr.close();
		fw.close();
		
		file_writer.file_delete(file_name+".liufuyan.lfy");
	}
	
	public solidworks_2011_change_assemble(String directory_name)
	{
		do_travel(directory_name,false);
	}
}
