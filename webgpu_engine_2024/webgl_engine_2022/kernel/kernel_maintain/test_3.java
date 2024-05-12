package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_writer;

public class test_3 
{
	public static void main(String args[])
	{
		debug_information.println("Begin:");
		
		file_writer fw=new file_writer("f:/temp/particle.txt",null);
		
		fw.println("[");
		for(int i=0,ni=10000;i<ni;i++) {
			fw.	print  ("\t");
			for(int j=0;j<3;j++) {
				String str=Math.random()+",";
				while(str.length()<25)
					str+=" ";
				fw.	print(str);
			}
			fw.println(Double.toString(Math.random()),(i<(ni-1))?",":"");	
		}
		fw.println("]");
		
		fw.close();

		debug_information.println("End");
	}
}
