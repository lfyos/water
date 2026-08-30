package kernel_common_class;

import java.util.Comparator;

public class tree_string_search_container <VALUE_TYPE> extends tree_search_container<String,VALUE_TYPE>
{
	public tree_string_search_container(Comparator<VALUE_TYPE> value_comparator)
	{
		super(null,value_comparator);
	}
}
