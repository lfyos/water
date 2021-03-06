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
	private void bubble_sort()
	{
		for(int i=0,ni=data_array.length-1;i<ni;i++)
			if(compare_data(data_array[i],data_array[i+1])>0){
				DATA_TYPE p=data_array[i];
				data_array[i]=data_array[i+1];
				data_array[i+1]=p;
				if(i>0)
					i-=2;
			}	
	}
	public int one_time_insertion_sort(int last_id)
	{
		DATA_TYPE p=data_array[last_id];
		for(int i=last_id-1;i>=0;data_array[i+1]=data_array[i--])
			if(compare_data(data_array[i],p)<=0){
				data_array[i+1]=p;
				return i+1;
			}
		data_array[0]=p;
		return 0;
	}
	private void shell_sort()
	{
		for(int step=((step=data_array.length/2)<1)?1:step;step>0;step/=2)
			for(int i=step,ni=data_array.length;i<ni;i++){
				DATA_TYPE p=data_array[i];
				for(int j=i-step;j>=0;data_array[j+step]=data_array[j],j-=step)
					if(compare_data(p,data_array[j])>=0){
						data_array[j+step]=p;
						p=null;
						break;
					}
				if(p!=null)
					data_array[i%step]=p;
			}
	}
	private void selection_sort()
	{
		for(int i=0,ni=data_array.length-1;i<ni;i++)
			for(int j=i+1,nj=ni+1;j<nj;j++)
				if(compare_data(data_array[i],data_array[j])>0) {
					DATA_TYPE p=data_array[i];
					data_array[i]=data_array[j];
					data_array[j]=p;
				}
	}
	private int quick_sort_separate(int pointer_begin,int pointer_end)
	{
		DATA_TYPE separator=data_array[pointer_begin];	
		while(pointer_begin<pointer_end){
			for(;pointer_begin<pointer_end;pointer_end--)
				if(compare_data(data_array[pointer_end],separator)<0){
					data_array[pointer_begin++]=data_array[pointer_end];
					break;
				}
			for(;pointer_begin<pointer_end;pointer_begin++)
				if(compare_data(data_array[pointer_begin],separator)>0){
					data_array[pointer_end--]=data_array[pointer_begin];
					break;
				}
		}
		data_array[pointer_begin]=separator;
		return pointer_begin;
	}
	private void quick_sort(int pointer_begin,int pointer_end)
	{
		while(pointer_begin<pointer_end){
			int pointer_middle=quick_sort_separate(pointer_begin,pointer_end);
			if((pointer_middle-pointer_begin)<(pointer_end-pointer_middle)){
				quick_sort(pointer_begin,pointer_middle-1);
				pointer_begin=pointer_middle+1;
			}else{
				quick_sort(pointer_middle+1,pointer_end);
				pointer_end=pointer_middle-1;
			}
		}
	}
	private void merge_sort(int begin_point,int end_point,DATA_TYPE buffer_array[])
	{
		if(begin_point<end_point){
			int collect_pointer=0,middle_point=(begin_point+end_point)/2;
			int first_pointer=begin_point,second_pointer=middle_point+1;
			
			merge_sort(first_pointer,	middle_point,	buffer_array);
			merge_sort(second_pointer,	end_point,		buffer_array);

			while((first_pointer<=middle_point)&&(second_pointer<=end_point))
				if(compare_data(data_array[first_pointer],data_array[second_pointer])<=0)
					buffer_array[collect_pointer++]=data_array[first_pointer++];
				else
					buffer_array[collect_pointer++]=data_array[second_pointer++];	
			while(first_pointer<=middle_point)
				buffer_array[collect_pointer++]=data_array[first_pointer++];
			while(second_pointer<=end_point)
				buffer_array[collect_pointer++]=data_array[second_pointer++];
			for(first_pointer=begin_point,second_pointer=0;first_pointer<=end_point;)
				data_array[first_pointer++]=buffer_array[second_pointer++];
		}
	}
	private void merge_sort(DATA_TYPE buffer_array[])
	{
		merge_sort(0,data_array.length-1,buffer_array);
	}
	private void heap_sort()
	{
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
		if(data_array==null)
			return 0;
		else
			return data_array.length;
	}
	public void do_sort(int sort_type_id,DATA_TYPE buffer_array[])
	{
		if(data_array==null)
			return;
		if(data_array.length<=1)
			return;
		switch(sort_type_id){
		default:
		case 0:
			if(buffer_array!=null)
				if(buffer_array.length>=data_array.length){
					merge_sort(buffer_array);
					break;
				}
			selection_sort();
			break;
		case 1:
			heap_sort();
			break;
		case 2:
			quick_sort(0,data_array.length-1);
			break;
		case 3:
			selection_sort();
			break;
		case 4:
			bubble_sort();
			break;
		case 5:
			for(int i=1,ni=data_array.length;i<ni;i++)
				one_time_insertion_sort(i);
			break;
		case 6:
			shell_sort();
			break;
		}
	}
	public void do_sort(DATA_TYPE buffer_array[])
	{
		do_sort(-1,buffer_array);
	}
	public void do_sort(int sort_type_id)
	{
		do_sort(sort_type_id,null);
	}
	public void do_sort()
	{
		do_sort(-1,null);
	}
	public sorter(DATA_TYPE my_data_array[],int my_sort_type_id,DATA_TYPE my_buffer_array[])
	{
		data_array=my_data_array;
		do_sort(my_sort_type_id,my_buffer_array);
	}
	public sorter(DATA_TYPE my_data_array[],DATA_TYPE my_buffer_array[])
	{
		data_array=my_data_array;
		do_sort(my_buffer_array);
	}
	public sorter(DATA_TYPE my_data_array[],int my_sort_type_id)
	{
		data_array=my_data_array;
		do_sort(my_sort_type_id);
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