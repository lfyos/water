package kernel_maintain;

import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;

public class score 
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		file_reader f=new file_reader("E:\\tmp\\x.txt",null);
		while(true){
			String number	=f.get_string();
			String id		=f.get_string();
			String name		=f.get_string();
			if(f.eof())
				break;
			String score_str_1		=f.get_string();
			String score_str_2		=f.get_string();


			debug_information.print  (number);
			debug_information.print  ("\t",id);
			debug_information.print  ("\t",name);
			debug_information.print  ("\t",
					(int)(Math.round(0.5*(Double.parseDouble(score_str_1)+Double.parseDouble(score_str_2)))));
			debug_information.print  ("\t",score_str_1);
			debug_information.print  ("\t",score_str_2);
			
			int score[]= {0,0,0,0,0,0,0,0,0,0},my_score=Integer.decode(score_str_2);
			
			if(score_str_2.compareTo("不计")==0)
				for(int i=0;i<10;i++)
					debug_information.print  ("\t不计");
			else {
				for(;;){
					int step=5;
					if(my_score>=90)
						step=3;
					if(my_score>=95)
						step=1;
					score[9]=my_score*10;
					for(int i=0;i<9;i++) {
						score[i]=(int)(Math.round(my_score+2.0*(Math.random()-0.5)*step));
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
				
				for(int i=0;i<10;i++)
					debug_information.print  ("\t",score[i]);
			}
			debug_information.println();
		}
		f.close();

		debug_information.println("End");
	}
}
