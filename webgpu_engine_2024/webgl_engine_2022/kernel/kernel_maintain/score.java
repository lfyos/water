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
			String number		=f.get_string();
			String id			=f.get_string();
			String name			=f.get_string();
			if(f.eof())
				break;
			String score_str_1	=f.get_string();
			String score_str_2	=f.get_string();

			debug_information.print  (number);
			debug_information.print  ("\t",id);
			debug_information.print  ("\t",name);
			
			int all_score=0,int_score_2=0,score[]=new int[10];
			try {
				double double_score;
				double_score =Double.parseDouble(score_str_1);
				double_score+=Double.parseDouble(score_str_2);
				all_score=(int)(Math.round(0.5*double_score));
				int_score_2=Integer.decode(score_str_2);
			}catch(Exception e) {
				for(int i=0;i<13;i++)
					debug_information.print  ("\t²»¼Æ");
				debug_information.println();
				continue;
			}
			for(;;){
				int step=5;
				if(int_score_2>=90)
					step=3;
				if(int_score_2>=95)
					step=1;
				score[9]=int_score_2*10;
				for(int i=0;i<9;i++) {
					score[i]=(int)(Math.round(int_score_2+2.0*(Math.random()-0.5)*step));
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
			debug_information.print  ("\t",score_str_1);
			debug_information.print  ("\t",score_str_2);
			
			for(int i=0;i<10;i++)
				debug_information.print  ("\t",score[i]);
			debug_information.println();
		}
		f.close();
		debug_information.println("End");
	}
}
