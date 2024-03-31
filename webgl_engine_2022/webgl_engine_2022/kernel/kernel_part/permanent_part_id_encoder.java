package kernel_part;

import java.util.ArrayList;

public class permanent_part_id_encoder
{
	class encoder_part_id
	{
		public String part_type_string;
		public int permanent_part_id;
		public encoder_part_id(String my_part_type_string)
		{
			part_type_string	=my_part_type_string;
			permanent_part_id	=0;
		}
	}
	
	private ArrayList<encoder_part_id> list;

	public permanent_part_id_encoder()
	{
		list=new ArrayList<encoder_part_id>();
	}
	public int encoder(String part_type_string)
	{
		for(int begin_pointer=0,end_pointer=list.size()-1,result;begin_pointer<=end_pointer;) {
			int middle_pointer=(begin_pointer+end_pointer)/2;
			encoder_part_id list_item=list.get(middle_pointer);
			if((result=list_item.part_type_string.compareTo(part_type_string))<0)
				begin_pointer=middle_pointer+1;
			else if(result>0)
				end_pointer=middle_pointer-1;
			else
				return ++(list_item.permanent_part_id);
		}
		list.add(new encoder_part_id(part_type_string));
		for(int i=list.size()-1,j=i-1;i>0;i--,j--) {
			encoder_part_id this_item=list.get(i),pre_item=list.get(j);
			if(pre_item.part_type_string.compareTo(this_item.part_type_string)<0)
				break;
			list.set(i, pre_item);
			list.set(j, this_item);
		}
		return 0;
	}
}
