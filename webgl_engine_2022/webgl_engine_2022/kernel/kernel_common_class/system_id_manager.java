package kernel_common_class;

import java.util.ArrayList;

public class system_id_manager 
{
	private ArrayList<int[]> list;

	public system_id_manager()
	{
		list=new ArrayList<int[]>();
	}
	public int apply_for_system_id(int id[])
	{
		int ret_val=list.size();
		list.add(ret_val,id);
		return ret_val;
	}
	public int[] get_system_id(int index_id)
	{
		if(index_id<0)
			return null;
		if(index_id>=list.size())
			return null;
		return list.get(index_id);
	}
	public ArrayList<int[]> get_list()
	{
		return list;
	}
}
