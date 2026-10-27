package driver_distance_tag;

import kernel_transformation.point;

public class distance_tag_parameter 
{
	private final static String tag_parameter_array[][]={
		//		name,					extra_distance_tag			tag_component		title	direction		caculate method
			
		/*00*/	{"absolute_distance",	"no_extra_distance_tag",	"root_component",	"",		"0","0","0",	""	},
		/*01*/	{"projection_distance",	"extra_distance_tag",		"root_component",	"line",	"0","0","0",	""	},
		/*02*/	{"projection_angle",	"extra_distance_tag",		"root_component",	"angle","0","0","0",	""	},
						
		/*03*/	{"global_direction_x",	"no_extra_distance_tag",	"root_component",	"X",	"1","0","0",	"plane_direction_distance"},
		/*04*/	{"global_direction_y",	"no_extra_distance_tag",	"root_component",	"Y",	"0","1","0",	"plane_direction_distance"},
		/*05*/	{"global_direction_z",	"no_extra_distance_tag",	"root_component",	"Z",	"0","0","1",	"plane_direction_distance"},
						
		/*06*/	{"local_direction_x",	"no_extra_distance_tag",	"search_component",	"LX",	"1","0","0",	"plane_direction_distance"},
		/*07*/	{"local_direction_y",	"no_extra_distance_tag",	"search_component",	"LY",	"0","1","0",	"plane_direction_distance"},
		/*08*/	{"local_direction_z",	"no_extra_distance_tag",	"search_component",	"LZ",	"0","0","1",	"plane_direction_distance"},
						
		/*09*/	{"view_direction_x",	"no_extra_distance_tag",	"view_component",	"VX",	"1","0","0",	"plane_direction_distance"},
		/*10*/	{"view_direction_y",	"no_extra_distance_tag",	"view_component",	"VY",	"0","1","0",	"plane_direction_distance"},
		/*11*/	{"view_direction_z",	"no_extra_distance_tag",	"view_component",	"VZ",	"0","0","1",	"plane_direction_distance"},
					
		/*12*/	{"global_plane_yz",		"no_extra_distance_tag",	"root_component",	"YZ",	"1","0","0",	"in_plane_distance"},
		/*13*/	{"global_plane_zx",		"no_extra_distance_tag",	"root_component",	"ZX",	"0","1","0",	"in_plane_distance"},
		/*14*/	{"global_plane_xy",		"no_extra_distance_tag",	"root_component",	"XY",	"0","0","1",	"in_plane_distance"},
						
		/*15*/	{"local_plane_yz",		"no_extra_distance_tag",	"search_component",	"LYZ",	"1","0","0",	"in_plane_distance"},
		/*16*/	{"local_plane_zx",		"no_extra_distance_tag",	"search_component",	"LZX",	"0","1","0",	"in_plane_distance"},
		/*17*/	{"local_plane_xy",		"no_extra_distance_tag",	"search_component",	"LXY",	"0","0","1",	"in_plane_distance"},
						
		/*18*/	{"view_plane_yz",		"no_extra_distance_tag",	"view_component",	"VYZ",	"1","0","0",	"in_plane_distance"},
		/*19*/	{"view_plane_zx",		"no_extra_distance_tag",	"view_component",	"VZX",	"0","1","0",	"in_plane_distance"},
		/*20*/	{"view_plane_xy",		"no_extra_distance_tag",	"view_component",	"VXY",	"0","0","1",	"in_plane_distance"},
						
		/*21*/	{"global_angle_x",		"no_extra_distance_tag",	"root_component",	"AX",	"1","0","0",	"global_angle"},
		/*22*/	{"global_angle_y",		"no_extra_distance_tag",	"root_component",	"AY",	"0","0","0",	"global_angle"},
		/*23*/	{"global_angle_z",		"no_extra_distance_tag",	"root_component",	"AZ",	"0","1","1",	"global_angle"},
						
		/*24*/	{"local_angle_x",		"no_extra_distance_tag",	"search_component",	"ALX",	"1","0","0",	"global_angle"},
		/*25*/	{"local_angle_y",		"no_extra_distance_tag",	"search_component",	"ALY",	"0","1","0",	"global_angle"},
		/*26*/	{"local_angle_z",		"no_extra_distance_tag",	"search_component",	"ALZ",	"0","0","1",	"global_angle"},
						
		/*27*/	{"view_angle_x"	,		"no_extra_distance_tag",	"view_component",	"AVX",	"1","0","0",	"global_angle"},
		/*28*/	{"view_angle_y",		"no_extra_distance_tag",	"view_component",	"AVY",	"0","1","0",	"global_angle"},
		/*29*/	{"view_angle_z",		"no_extra_distance_tag",	"view_component",	"AVZ",	"0","0","1",	"global_angle"},
								
		/*30*/	{"global_angle_yz",		"extra_distance_tag",		"root_component",	"AYZ",	"1","0","0",	"plane_angle"},
		/*31*/	{"global_angle_zx",		"extra_distance_tag",		"root_component",	"AZX",	"0","1","0",	"plane_angle"},
		/*32*/	{"global_angle_xy",		"extra_distance_tag",		"root_component",	"AXY",	"0","0","1",	"plane_angle"},
						
		/*33*/	{"local_angle_yz",		"extra_distance_tag",		"search_component",	"ALYZ",	"1","0","0",	"plane_angle"},
		/*34*/	{"local_angle_zx",		"extra_distance_tag",		"search_component",	"ALZX",	"0","1","0",	"plane_angle"},
		/*35*/	{"local_angle_xy",		"extra_distance_tag",		"search_component",	"ALXY",	"0","0","1",	"plane_angle"},
						
		/*36*/	{"view_angle_yz",		"extra_distance_tag",		"view_component",	"AVYZ",	"1","0","0",	"plane_angle"},
		/*37*/	{"view_angle_zx",		"extra_distance_tag",		"view_component",	"AVYZ",	"0","1","0",	"plane_angle"},
		/*38*/	{"view_angle_xy",		"extra_distance_tag",		"view_component",	"AVYZ",	"0","0","1",	"plane_angle"},
	};	
	private static String sorted_tag_parameter_array[][]=null;
	
	public distance_tag_parameter(String tag_parameter_name)
	{
		String my_parameter[]=tag_parameter_array[0];
		
		if(tag_parameter_name!=null) {
			int number=tag_parameter_array.length;
			if(sorted_tag_parameter_array==null) {
				sorted_tag_parameter_array=new String[number][];
				for(int i=0;i<number;i++)
					sorted_tag_parameter_array[i]=tag_parameter_array[i];
				for(int i=0,ni=number-1;i<ni;i++)
					for(int j=i+1,nj=number;j<nj;j++){
						var pi=sorted_tag_parameter_array[i];
						var pj=sorted_tag_parameter_array[j];
						if(pi[0].compareTo(pj[0])<=0)
							continue;
						sorted_tag_parameter_array[i]=pj;
						sorted_tag_parameter_array[j]=pi;
					}
			}
			for(int begin_pointer=0,end_pointer=number-1;begin_pointer<=end_pointer;) {
				int middle_pointer=(begin_pointer+end_pointer)/2;
				String middle_parameter[]=sorted_tag_parameter_array[middle_pointer];
				int compare_result=middle_parameter[0].compareTo(tag_parameter_name);
				if(compare_result<0)
					begin_pointer=middle_pointer+1;
				else if(compare_result>0)
					end_pointer=middle_pointer-1;
				else{
					my_parameter=middle_parameter;
					break;
				}
			}
		}
		type_id				=my_parameter[0];
		extra_distance_tag	=my_parameter[1];
		tag_component		=my_parameter[2];
		title				=my_parameter[3];
		caculate_method		=my_parameter[7];
		direction			=new point(	
				Double.parseDouble(my_parameter[4]),
				Double.parseDouble(my_parameter[5]),
				Double.parseDouble(my_parameter[6]));
	}
	public String type_id,extra_distance_tag,tag_component,title,caculate_method;
	public point direction;
}
