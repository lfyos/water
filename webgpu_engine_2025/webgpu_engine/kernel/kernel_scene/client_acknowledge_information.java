package kernel_scene;

public class client_acknowledge_information 
{
	public boolean acknowledged_part_load_flag[][];
	public int loaded_file_number;
	public long loaded_data_length;
	public int loading_render_id,loading_part_id;
	
	public void destroy()
	{
		if(acknowledged_part_load_flag!=null) 
			acknowledged_part_load_flag=null;
	}
	public client_acknowledge_information(scene_kernel sk)
	{
		acknowledged_part_load_flag=new boolean[sk.render_cont.renders.size()][];
		for(int i=0,ni=acknowledged_part_load_flag.length;i<ni;i++) {
			acknowledged_part_load_flag[i]=new boolean[sk.render_cont.renders.get(i).parts.size()];
			for(int j=0,nj=acknowledged_part_load_flag[i].length;j<nj;j++)
				acknowledged_part_load_flag[i][j]=false;
		}
		loaded_file_number	=0;
		loaded_data_length	=0;
		loading_render_id	=-1;
		loading_part_id		=-1;
	}
	public void get_client_parameter(scene_kernel sk,client_information ci)
	{
		String str;
		int index_id,render_id,part_id;
		
		if((str=ci.request_response.get_parameter("acknowledge"))==null)
			return;
		if((index_id=str.indexOf("_"))<0)
			return;
		loaded_file_number=Integer.decode(str.substring(0,index_id));
		str=str.substring(index_id+1);
		
		if((index_id=str.indexOf("_"))<0)
			return;
		loaded_data_length=Long.decode(str.substring(0,index_id));
		str=str.substring(index_id+1);
		
		if((index_id=str.indexOf("_"))<0)
			return;
		loading_render_id=Integer.decode(str.substring(0,index_id));
		str=str.substring(index_id+1);
		
		if((index_id=str.indexOf("_"))<0)
			return;
		loading_part_id=Integer.decode(str.substring(0,index_id));
		str=str.substring(index_id+1);

		while(str.length()>0){
			if((index_id=str.indexOf('_'))<0)
				break;
			render_id=Integer.decode(str.substring(0,index_id));
			str=str.substring(index_id+1);		
			if((index_id=str.indexOf('_'))<0) {
				part_id=Integer.decode(str);
				str="";
			}else{
				part_id=Integer.decode(str.substring(0,index_id));
				str=str.substring(index_id+1);
			}
			if((render_id<0)||(part_id<0))
				continue;
			if(render_id>=acknowledged_part_load_flag.length)
				continue;
			if(part_id>=acknowledged_part_load_flag[render_id].length)
				continue;
			acknowledged_part_load_flag[render_id][part_id]=true;
		}	
	}
}
