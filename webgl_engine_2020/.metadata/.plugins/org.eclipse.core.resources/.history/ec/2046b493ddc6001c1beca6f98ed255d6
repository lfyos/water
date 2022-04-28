package driver_distance_tag;

import kernel_common_class.format_change;
import kernel_common_class.jason_string;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.point;
import kernel_transformation.plane;
import kernel_transformation.location;
import kernel_component.component;

public class distance_tag_item
{
	public int state;
	public point p0,px,py;
	public String tag_str;
	
	public distance_tag_item(point my_point)
	{
		state=0;
		p0=new point(my_point);
		px=new point(my_point);
		py=new point(my_point);
		tag_str="";
	}
	public void set_tag_str(int operate_component_id,
			int display_precision,engine_kernel ek,client_information ci)
	{
		plane pl;
		point dir[];
		location loca;
		component comp;
		double data[][];
		String str,title[];
		
		int function_id=0;
		if((str=ci.request_response.get_parameter("function"))!=null)
			if((str=str.trim()).length()>0)
				function_id=Integer.parseInt(str);
		
		tag_str=format_change.double_to_decimal_string(px.sub(p0).distance(),display_precision);
		switch(function_id) {
		default:
		case 0:
			function_id=0;
			break;
		case 1://X direction
		case 2://Y direction
		case 3://Z direction
			function_id-=1;
			title=new String[]{"X:","Y:","Z:"};
			data=new double[][] {new double[] {1,0,0},new double[] {0,1,0},new double[] {0,0,1}};
			pl=new plane(data[function_id][0],data[function_id][1],data[function_id][2],0);
			tag_str=title[function_id]+format_change.double_to_decimal_string(
					Math.abs(pl.test(px)-pl.test(p0)),display_precision);
			break;
		case 4://local X direction
		case 5://local Y direction
		case 6://local Z direction
			if((comp=ek.component_cont.get_component(operate_component_id))!=null){
				function_id-=4;
				title=new String[]{"LX:","LY:","LZ:"};
				data=new double[][] {new double[] {1,0,0},new double[] {0,1,0},new double[] {0,0,1}};
				pl=new plane(comp.absolute_location.multiply(0,0,0),
						comp.absolute_location.multiply(data[function_id][0],data[function_id][1],data[function_id][2]));
				tag_str=title[function_id]+format_change.double_to_decimal_string(
						Math.abs(pl.test(px)-pl.test(p0)),display_precision);
			}
			break;
		case 7://left right direction
		case 8://up down direction
		case 9://near far direction
			function_id-=7;
			title=new String[]{"LR:","UD:","NF:"};
			dir=new point[] {
					ci.display_camera_result.right_direct,
					ci.display_camera_result.up_direct,
					ci.display_camera_result.to_me_direct};
			pl=new plane(dir[function_id].x,dir[function_id].y,dir[function_id].z,0);
			tag_str=title[function_id]+format_change.double_to_decimal_string(
					Math.abs(pl.test(px)-pl.test(p0)),display_precision);
			break;
		case 10://XY plane
		case 11://YZ plane
		case 12://ZX plane
			function_id-=10;
			title=new String[]{"XY:","YZ:","ZX:"};
			data=new double[][] {new double[] {0,0,1},new double[] {1,0,0},new double[] {0,1,0}};
			pl=new plane(new point(0,0,0),new point(data[function_id][0],data[function_id][1],data[function_id][2]));
			loca=pl.project_to_plane_location();
			tag_str=title[function_id]+format_change.double_to_decimal_string(
					loca.multiply(px).sub(loca.multiply(p0)).distance(),display_precision);
			break;
		case 13://local XY plane
		case 14://local YZ plane
		case 15://local ZX plane
			if((comp=ek.component_cont.get_component(operate_component_id))!=null){
				function_id-=13;
				title=new String[]{"LXY:","LYZ:","LZX:"};
				data=new double[][] {new double[] {0,0,1},new double[] {1,0,0},new double[] {0,1,0}};
				pl=new plane(comp.absolute_location.multiply(0,0,0),
					comp.absolute_location.multiply(data[function_id][0],data[function_id][1],data[function_id][2]));
				loca=pl.project_to_plane_location();
				tag_str=title[function_id]+format_change.double_to_decimal_string(
						loca.multiply(px).sub(loca.multiply(p0)).distance(),display_precision);
			}
			break;
		case 16://view plane
			pl=new plane(	ci.display_camera_result.to_me_direct.x,
							ci.display_camera_result.to_me_direct.y,
							ci.display_camera_result.to_me_direct.z,0);
			loca=pl.project_to_plane_location();
			tag_str="View:"+format_change.double_to_decimal_string(
					loca.multiply(px).sub(loca.multiply(p0)).distance(),display_precision);
			break;
		}
		tag_str=jason_string.change_string(tag_str);
	}
}