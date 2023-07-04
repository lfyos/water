package kernel_common_class;

public class sorter <DATA_TYPE,KEY_TYPE>
{
	public DATA_TYPE data_array[];
	
	public void destroy()
	{
		if(data_array!=null)
			for(int i=0,ni=data_array.length;i<ni;i++)
				data_array[i]=null;
		data_array=null;
	}
	public int compare_data(DATA_TYPE s,DATA_TYPE t)
	{
		return 0;
	}
	public int compare_key(DATA_TYPE s,KEY_TYPE t)
	{
		return 0;
	}
	public int search(KEY_TYPE t)
	{
		int search_id=-1;
		if(data_array!=null)
			for(int begin_pointer=0,end_pointer=data_array.length-1;begin_pointer<=end_pointer;){
				search_id=(begin_pointer+end_pointer)/2;
				int result=compare_key(data_array[search_id],t);
				if(result<0)
					begin_pointer=search_id+1;
				else if(result>0)
					end_pointer=search_id-1;
				else
					return search_id;
			}
		return -1;
	}
	public int[] range(KEY_TYPE t)
	{
		int begin_id,end_id,my_search_id;
		if((my_search_id=search(t))<0)
			return null;
		for(begin_id=my_search_id;begin_id>0;begin_id--)
			if(compare_key(data_array[begin_id-1],t)!=0)
				break;
		int end_number=data_array.length-1;
		for(end_id=my_search_id;end_id<end_number;end_id++)
			if(compare_key(data_array[end_id+1],t)!=0)
				break;
		return new int[]{begin_id,end_id};
	}
	public int get_number()
	{
		return (data_array==null)?0:(data_array.length);
	}
	public void do_sort()
	{
		if(data_array==null)
			return;
		if(data_array.length<=1)
			return;
		
		for(int i=1,part_number=data_array.length;i<part_number;i++)
			for(int j=i;j>0;){
				int parent=(j-1)/2;
				if(compare_data(data_array[parent],data_array[j])>=0)
					break;
				DATA_TYPE p=data_array[parent];
				data_array[parent]=data_array[j];	
				data_array[j]=p;
				j=parent;
			}
		for(int part_number=data_array.length,i=part_number-1;i>0;i--){
			DATA_TYPE p=data_array[i];
			data_array[i]=data_array[0];
			data_array[0]=p;
			for(int j=0,select_child,other_child;;){
				if((select_child=j+j+1)>=i)
					break;
				if((other_child=select_child+1)<i)
					if(compare_data(data_array[select_child],data_array[other_child])<=0)
						select_child=other_child;
				if((compare_data(data_array[j],data_array[select_child]))>=0)
					break;
				p=data_array[j];
				data_array[j]=data_array[select_child];
				data_array[select_child]=p;
				j=select_child;
			}
		}
	}
	public sorter(DATA_TYPE my_data_array[])
	{
		data_array=my_data_array;
		do_sort();
	}
	public sorter()
	{
		data_array=null;
	}
}