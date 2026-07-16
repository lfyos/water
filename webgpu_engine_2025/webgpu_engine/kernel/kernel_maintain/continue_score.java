package kernel_maintain;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;

public class continue_score 
{
	private static void print_score(double score,int low_score,int high_score)
	{
		int int_score	=(int)score;
		if((score-int_score)>=0.5)
			int_score++;
		int_score=(int_score<low_score)?low_score:(int_score>high_score)?high_score:int_score;
		debug_information.print  (int_score+"\t");
	}
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		file_reader f=new file_reader("E:\\tmp\\x.txt","UTF-8");
		for(int counter=1,number=f.get_int();;counter++){
			String name			=f.get_string();
			String id			=f.get_string();
			if(f.eof())
				break;
			debug_information.print  (counter+"\t"+id+"\t"+name+"\t");
			double total_length,my_length,score,total_score=0;
			for(int i=0;i<number;i++){
				total_length=f.get_double();
				my_length	=f.get_double();
				score		=(my_length/total_length)*100;
				total_score+=score/number;
	
//				print_score(score,75,95);
			}
			
			print_score(total_score,75,95);
			
			debug_information.println();
		}
		f.close();
		
		debug_information.println("End");
	}
}
