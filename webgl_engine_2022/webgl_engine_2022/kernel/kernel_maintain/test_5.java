package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class test_5 
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		file_reader fr=new file_reader("F:\\temp\\data.old.txt",null);
		file_writer fw=new file_writer("F:\\temp\\data.new.txt",null);
		fw.println("[");
		
		for(double p[];;){
			p=new double[] {fr.get_double(),fr.get_double(),fr.get_double(),fr.get_double()};
			if(fr.eof())
				break;
			fw.print("\t",p[0]).print(",\t",	p[1]).print(",\t",p[2]).print(",\t",p[3]).println(",");
			p=new double[] {fr.get_double(),fr.get_double(),fr.get_double(),fr.get_double()};
			fw.print("\t",p[0]).print(",\t",1.0-p[1]).print(",\t",p[2]).print(",\t",p[3]).println(",").println();
		}
		fw.println("]");
		fr.close();
		fw.close();
		debug_information.println("End");
	}
}
