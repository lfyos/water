package kernel_render;

import kernel_part.part;

public class create_assemble_part_driver_selection
{
	private double part_volume[];
	private part p_part[];
	
	public void register(part my_part,double my_part_volume)
	{
		if(my_part==null)
			return;
		if(my_part.part_id<0)
			return;
		if(part_volume==null){
			part_volume=new double[my_part.part_id+1];
			p_part=new part[my_part.part_id+1];
			for(int i=0,ni=part_volume.length;i<ni;i++){
				part_volume[i]=0;
				p_part[i]=null;
			}
		}else if(my_part.part_id>=part_volume.length){
			double bak_part_volume[]=part_volume;
			part bak_part[]=p_part;
			
			part_volume=new double[my_part.part_id+1];
			p_part=new part[my_part.part_id+1];
			for(int i=0,ni=bak_part_volume.length;i<ni;i++){
				part_volume[i]=bak_part_volume[i];
				p_part[i]=bak_part[i];
			}
			for(int i=bak_part_volume.length,ni=part_volume.length;i<ni;i++){
				part_volume[i]=0;
				p_part[i]=null;
			}
		}
		part_volume[my_part.part_id]+=my_part_volume;
		p_part[my_part.part_id]	 =my_part;
	}
	public part get_biggest_part()
	{
		part ret_val=null;
		double max_volume=0;
		
		if(p_part!=null)
			for(int i=0,ni=p_part.length;i<ni;i++)
				if((p_part[i]!=null)&&(part_volume[i]>0.0)){
					if(ret_val!=null)
						if(part_volume[i]<=max_volume)
							continue;
					max_volume=part_volume[i];
					ret_val=p_part[i];
				}
		return ret_val;
	}
	public create_assemble_part_driver_selection()
	{
		part_volume=null;
	}
}
