package kernel_maintain;


import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;


public class test_4
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		int component_number=10,root_child_number=0;
		String location_str="1	0	0	0	0	1	0	0	0	0	1	0	0	0	0	1";
		
		file_reader fr=new file_reader("E:\\tmp\\virtual_mount.assemble",null);
		file_writer fw=new file_writer("E:\\water_all\\data\\project\\assemble\\bridge\\bridge_old\\assemble\\virtual_mount.assemble",null);
		while(!(fr.eof())) {
			String part_name=fr.get_string();
			if(part_name==null)
				continue;
			if((part_name=part_name.trim()).length()<=0)
				continue;
			root_child_number++;
			
			fw.println("	virtual_mount_component_"+part_name+"_root");
			fw.println("	undefined_part");
			fw.println("	",location_str);
			fw.println("	",component_number);
			for(int i=0;i<component_number;i++) {
				fw.println("		virtual_mount_component_"+part_name+"_",i);
				fw.println("		",part_name);
				fw.println("		",location_str);
				fw.println("		0");
			}
		}
		fw.close();
		fr.close();
		
		String assemble_str=file_reader.get_text(fw.directory_name+fw.file_name,fw.get_charset());
		fw=new file_writer(fw.directory_name+fw.file_name,fw.get_charset());
		
		fw.println("virtual_mount_root_component");
		fw.println("undefined_part");
		fw.println(location_str);
		fw.println(root_child_number);
		fw.println(assemble_str);
		
		fw.close();
		
		debug_information.println("End");
	}
}
