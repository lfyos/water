package kernel_maintain;

import kernel_file_manager.file_reader;
import kernel_common_class.debug_information;

public class score 
{
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		file_reader f=new file_reader("c:\\temp\\z.txt",null);
		for(int j=1;!(f.eof());j++) {
//			int number=f.get_int();
			int number=j;
			int id=f.get_int();
			String name=f.get_string();
			String school=f.get_string();
			if(f.eof())
				break;
			String str=f.get_string();
			
			debug_information.print  (number);
			debug_information.print  ("\t",id);
			debug_information.print  ("\t",name);
			debug_information.print  ("\t",school);
			
			int score[]= {0,0,0,0,0,0,0,0,0,0},my_score=0,step=5;
			
			if(str.compareTo("不计")!=0)
				for(;;){
					my_score=Integer.decode(str);
					step=5;
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
						if(score[i]<79)
							flag=false;
					}
					if(flag)
						break;
				}
			if(my_score<50)
				debug_information.print  ("\t不计");
			else
				debug_information.print  ("\t",my_score);
			
			for(int i=0;i<10;i++)
				if(score[i]<50)
					debug_information.print  ("\t不计");
				else
					debug_information.print  ("\t",score[i]);
			debug_information.println();
		}
		f.close();

		debug_information.println("End");
	}
}
