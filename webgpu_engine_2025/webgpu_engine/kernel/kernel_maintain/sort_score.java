package kernel_maintain;

import java.util.ArrayList;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;

public class sort_score 
{
	public static void main(String args[])
	{
		class score_item
		{
			public String id,name,score_1,score_2,score_3;
			public score_item(file_reader f)
			{
				id		=f.get_string();
				name	=f.get_string();
				score_1	=f.get_string();
				score_2	=f.get_string();
				score_3	=f.get_string();
			}
		};
		
		debug_information.println("Begin");
		
		file_reader f=new file_reader("E:\\tmp\\y.txt","UTF-8");
		ArrayList<score_item> list=new ArrayList<score_item>();
		while(f.get_string()!=null) {
			score_item s=new score_item(f);
			list.add(s);
		}
		f.close();
		
		for(int i=1;i<list.size();i++)
			for(int j=0;j<i;j++) {
				score_item pi=list.get(i);
				score_item pj=list.get(j);
				if(pj.id.compareTo(pi.id)>0) {
					list.set(i, pj);
					list.set(j, pi);
				}
			}
		for(int i=0;i<list.size();i++) {
			score_item pi=list.get(i);
			debug_information.print  (i+1);
			debug_information.print  ("\t"+pi.id);
			debug_information.print  ("\t"+pi.name);
			debug_information.print  ("\t"+pi.score_1);
			debug_information.print  ("\t"+pi.score_2);
			debug_information.print  ("\t"+pi.score_3);
			debug_information.println();
		}

		debug_information.println("End");
	}
}
