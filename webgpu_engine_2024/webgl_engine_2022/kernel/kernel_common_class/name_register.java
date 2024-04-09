package kernel_common_class;

public class name_register 
{
	private String name[];
	private int id[];
	
	public name_register()
	{
		name=null;
		id=null;
	}
	public String[] get_name_sorted_name()
	{
		return name;
	}
	public String[] get_id_sorted_name()
	{
		if(name==null)
			return name;
		
		String ret_val[]=new String[name.length];
		for(int i=0,n=name.length;i<n;i++)
			for(int j=0;j<n;j++)
				if(id[j]==i){
					ret_val[i]=name[j];
					break;
				}
		return ret_val;
	}
	
	public int register(String my_name)
	{
		if(name==null){
			name=new String[]{my_name};
			id=new int[]{0};
			return id[0];
		}
		for(int begin_pointer=0,end_pointer=name.length-1;begin_pointer<=end_pointer;){
			int middle_pointer=(begin_pointer+end_pointer)/2;
			int compare_result=name[middle_pointer].compareTo(my_name);
			
			if(compare_result<0)
				begin_pointer=middle_pointer+1;
			else if(compare_result>0)
				end_pointer=middle_pointer-1;
			else
				return id[middle_pointer];
		}
		
		String bak_name[]=name;
		int bak_id[]=id;
		
		name=new String[bak_name.length+1];
		id	=new int[bak_id.length+1];
		for(int i=0,ni=bak_name.length;i<ni;i++){
			name[i]	=bak_name[i];
			id[i]	=bak_id[i];
		}
		name[name.length-1]	=my_name;
		id[id.length-1]		=id.length-1;
		
		for(int i=name.length-2;i>=0;i--){
			String str_0=name[i+0],str_1=name[i+1];
			int    id_0 =id[i+0],  id_1 =id[i+1];

			if((str_0).compareTo(str_1)<=0)
				return id_1;
			
			id  [i+0]=id_1;	id  [i+1]=id_0;
			name[i+0]=str_1;name[i+1]=str_0;
		}
		return id[0];
	}
}
