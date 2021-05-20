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
		if((distance2_i<=const_value.min_value2)^(distance2_j<=const_value.min_value2))
			return (distance2_i<=const_value.min_value2)?-1:1;
		
		double distance2_max=Math.max(distance2_i,distance2_j);
		if(distance2_max>const_value.min_value2)
			if(Math.abs((distance2_i/distance2_max)-(distance2_j/distance2_max))>0.1)
				return (distance2_i>distance2_j)?-1:1;
		
		if((pi.mesh_file_name==null)^(pj.mesh_file_name==null))
			return (pi.mesh_file_name==null)?-1:1;
		
		if(pi.mesh_file_name==null)
			if((pi.top_box_part_flag)^(pj.top_box_part_flag))
				return (pi.top_box_part_flag)?-1:1;		

		long data_length_i=pi.boftal.buffer_object_total_file_length;
		long data_length_j=pj.boftal.buffer_object_total_file_length;
		long data_length_max=Math.max(data_length_i,data_length_j);
		
		if(data_length_max>0){
			double data_length_pi=((double)data_length_i)/((double)data_length_max);
			double data_length_pj=((double)data_length_j)/((double)data_length_max);
			if(Math.abs(data_length_pi-data_length_pj)>0.1)
				return (data_length_i<data_length_j)?-1:1;
		}
		if(Math.abs(distance2_i-distance2_j)>=const_value.min_value2)
			return (distance2_i>distance2_j)?-1:1;
		else
			return (data_length_i<data_length_j)?-1:1;
	}
	
	public int compare_data(part s,part t)
	{
		return compare_part(s,t);
	}
	public int compare_key(part s,part t)
	{
		return compare_part(s,t);
	}
	public part_container_for_process_sequence(part my_parts[])
	{
		super(part.clone_array(my_parts),-1,new part[my_parts.length]);
	}
}