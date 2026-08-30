package kernel_common_class;

import java.util.Comparator;

class tree_string_array_search_container_comparator implements Comparator<String[]>
{
	public int compare(String str1[],String str2[])
	{
		str1=(str1!=null)?str1:new String[]{};
		str2=(str1!=null)?str2:new String[]{};
		
		int len_1=str1.length,len_2=str2.length;
		int ret_val,len=(len_1<=len_2)?len_1:len_2;
		for(int i=0;i<len;i++)
			if((ret_val=((str1[i]!=null)?str1[i]:"").compareTo((str2[i]!=null)?str2[i]:""))!=0)
				return ret_val;
		return len_1-len_2;
	}
}
public class tree_string_array_search_container <VALUE_TYPE> extends tree_search_container<String[],VALUE_TYPE>
{
	public tree_string_array_search_container(Comparator<VALUE_TYPE>my_value_comparator)
	{
		super(new tree_string_array_search_container_comparator(),my_value_comparator);
	}
}
