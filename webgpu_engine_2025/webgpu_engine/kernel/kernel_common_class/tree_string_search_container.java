package kernel_common_class;

import java.util.Comparator;

class tree_string_search_container_comparator implements Comparator<String>
{
	public int compare(String str1,String str2)
	{
		return ((str1!=null)?str1:"").compareTo((str1!=null)?str2:"");
	}
};
public class tree_string_search_container <VALUE_TYPE> extends tree_search_container<String,VALUE_TYPE>
{
	public tree_string_search_container()
	{
		super(new tree_string_search_container_comparator());
	}
}
