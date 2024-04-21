package kernel_common_class;

import java.util.Comparator;

class tree_string_search_container_comparator implements Comparator<String[]>
{
	public int compare(String obj1[],String obj2[])
	{
		int len_1=obj1.length,len_2=obj2.length,ret_val;
		for(int i=0,ni=(len_1<len_2)?len_1:len_2;i<ni;i++)
			if((ret_val=obj1[i].compareTo(obj2[i]))!=0)
				return ret_val;
		return len_1-len_2;
	}
}
public class tree_string_search_container <VALUE_TYPE> extends tree_search_container<String[],VALUE_TYPE>
{
	public tree_string_search_container()
	{
		super(new tree_string_search_container_comparator());
	}
}
