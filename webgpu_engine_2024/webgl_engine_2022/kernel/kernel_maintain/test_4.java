package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;

public class test_4 
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		file_reader f=new file_reader("E:\\tmp\\x.txt",null);
		while(true){
			String number		=f.get_string();
			String id			=f.get_string();
			String name			=f.get_string();
			if(f.eof())
				break;
			int all_score		=f.get_int(),score[]=new int[10];

			debug_information.print  (number);
			debug_information.print  ("\t",id);
			debug_information.print  ("\t",name);

			for(;;){
				int step=5;
				if(all_score>=90)
					step=3;
				if(all_score>=95)
					step=1;
				score[9]=all_score*10;
				for(int i=0;i<9;i++) {
					score[i]=(int)(Math.round(all_score+2.0*(Math.random()-0.5)*step));
					score[9]-=score[i];
				}
				boolean flag=true;
				for(int i=0;i<10;i++) {
					if(score[i]>95)
						flag=false;
					if(score[i]<70)
						flag=false;
				}
				if(flag)
					break;
			}
			debug_information.print  ("\t",all_score);
			
			for(int i=0;i<10;i++)
				debug_information.print  ("\t",score[i]);
			debug_information.println();
		}
		f.close();
		debug_information.println("End");
	}
}
