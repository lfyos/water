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
		if(data_array!=null)
			for(int begin_pointer=0,end_pointer=data_array.length-1;begin_pointer<=end_pointer;){
				int result,search_id=(begin_pointer+end_pointer)/2;
				if((result=compare_key(data_array[search_id],t))<0)
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
		int begin_id,end_id,end_number,search_id;
		if((search_id=search(t))<0)
			return null;
		for(begin_id=search_id;begin_id>0;begin_id--)
			if(compare_key(data_array[begin_id-1],t)!=0)
				break;
		for(end_id=search_id,end_number=data_array.length-1;end_id<end_number;end_id++)
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
		int part_number=data_array.length;
		int id_array[]=new int[part_number];
		for(int i=0;i<part_number;i++)
			id_array[i]=i;

		for(int i=1;i<part_number;i++)
			for(int j=i,parent,result;j>0;j=parent){
				if((result=compare_data(data_array[parent=(j-1)/2],data_array[j]))==0)
					result=id_array[parent]-id_array[j];
				if(result>=0)
					break;
				
				result=id_array[parent];
				id_array[parent]=id_array[j];
				id_array[j]=result;
				
				DATA_TYPE p=data_array[parent];
				data_array[parent]=data_array[j];	
				data_array[j]=p;
			}
		for(int i=part_number-1;i>0;i--){
			int result=id_array[i];
			id_array[i]=id_array[0];
			id_array[0]=result;
			
			DATA_TYPE p=data_array[i];
			data_array[i]=data_array[0];
			data_array[0]=p;
			
			for(int j=0,select_child,other_child;(select_child=j+j+1)<i;j=select_child){
				if((other_child=select_child+1)<i){
					if((result=compare_data(data_array[select_child],data_array[other_child]))==0)
						result=id_array[select_child]-id_array[other_child];
					if(result<0)
						select_child=other_child;
				}
				if((result=compare_data(data_array[j],data_array[select_child]))==0)
					result=id_array[j]-id_array[select_child];
				if(result>=0)
					break;
				
				result=id_array[j];
				id_array[j]=id_array[select_child];
				id_array[select_child]=result;
				
				p=data_array[j];
				data_array[j]=data_array[select_child];
				data_array[select_child]=p;
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