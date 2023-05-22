package kernel_part;

import kernel_common_class.const_value;
import kernel_common_class.sorter;
import kernel_transformation.box;

public class part_container_for_process_sequence extends sorter<part,part>
{
	public int compare_part(part pi,part pj)
	{
		if(pi.part_par.process_sequence_id!=pj.part_par.process_sequence_id)
			return (pi.part_par.process_sequence_id<pj.part_par.process_sequence_id)?-1:1;
		
		box bi,bj;
		double distance2_i=0,distance2_j=0;
		
		if(pi.driver!=null)
			if((bi=pi.secure_caculate_part_box(null,-1,-1,-1,-1,-1,-1,null,null))!=null)
				distance2_i=bi.distance2();
		if(pj.driver!=null)
			if((bj=pj.secure_caculate_part_box(null,-1,-1,-1,-1,-1,-1,null,null))!=null)
				distance2_j=bj.distance2();
		
		if((distance2_i<=const_value.min_value2)&&(distance2_j>const_value.min_value2))	//parts without box, first be processed
			return -1;
		if((distance2_i>const_value.min_value2)&&(distance2_j<=const_value.min_value2))	
			return 1;
		if((distance2_i>const_value.min_value2)&&(distance2_j>const_value.min_value2)){	//part both with box,compare their distance
			double distance2_max=Math.max(distance2_i,distance2_j);						//big part first be processed
			if(distance2_max>const_value.min_value2)
				if(Math.abs((distance2_i/distance2_max)-(distance2_j/distance2_max))>0.1)//compare only with great difference
					return (distance2_i>distance2_j)?-1:1;
		}
		boolean i_flag,j_flag;
		i_flag=pi.is_normal_part();
		j_flag=pj.is_normal_part();
		if(i_flag^j_flag)		// normal part last
			return i_flag?1:-1;
		if(!i_flag) {			// both is NOT normal part
			i_flag=pi.is_bottom_box_part();
			j_flag=pj.is_bottom_box_part();
			if(i_flag^j_flag)	// top box part first,bottom box part last
				return i_flag?1:-1;
		}
	
		long data_length_i=pi.boftal.buffer_object_total_file_length;
		long data_length_j=pj.boftal.buffer_object_total_file_length;
		long data_length_max=Math.max(data_length_i,data_length_j);
		
		if(data_length_max>0){
			double data_length_pi=((double)data_length_i)/((double)data_length_max);
			double data_length_pj=((double)data_length_j)/((double)data_length_max);
			if(Math.abs(data_length_pi-data_length_pj)>0.1)		//simple part process first
				return (data_length_i<data_length_j)?-1:1;
		}
		if(Math.abs(distance2_i-distance2_j)>=const_value.min_value2)
			return (distance2_i>distance2_j)?-1:1;	//big part process first
		else
			return (data_length_i<data_length_j)?-1:1;//simple part process first
	}
	
	public int compare_data(part s,part t)
	{
		return compare_part(s,t);
	}
	public int compare_key(part s,part t)
	{
		return compare_part(s,t);
	}
	private static part[] clone_array(part my_parts[])
	{
		part ret_val[]=new part[my_parts.length];
		for(int i=0,ni=ret_val.length;i<ni;i++)
			ret_val[i]=my_parts[i];
		return ret_val;
	}
	public part_container_for_process_sequence(part my_parts[])
	{
		super(clone_array(my_parts));
	}
}