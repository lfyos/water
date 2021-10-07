package driver_radar;

import kernel_part.part;
import kernel_component.component;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_transformation.point;
import kernel_driver.instance_driver;
import kernel_driver.component_driver;
import kernel_transformation.location;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_network.client_request_response;
import kernel_common_class.debug_information;
import kernel_common_class.gps_caculater;

public class extended_component_driver  extends component_driver
{
	private int sensor_comp_id,target_comp_id,modify_comp_id;
	private double sensor_loca_data[],target_loca_data[]; 
	private boolean caculate_location_modify_flag; 
	private gps_caculater gps;
	
	public void destroy()
	{
		super.destroy();
		gps=null;
	}
	public extended_component_driver(part my_component_part)
	{
		super(my_component_part);
		sensor_comp_id	=-1;
		target_comp_id	=-1;
		modify_comp_id	=-1;
		sensor_loca_data=null;
		target_loca_data=null;
		caculate_location_modify_flag=true;
		gps=null;
	}
	private void save(engine_kernel ek)
	{
		point p0,px,py;
		location loca;
		component my_comp;
		
		file_writer fw=new file_writer(
			component_part.directory_name+component_part.material_file_name,
			component_part.file_charset);
		
		fw.println("/*	caculate_location_modify_flag	*/	",
				caculate_location_modify_flag?"true":"false");
		
		my_comp=ek.component_cont.get_component(sensor_comp_id);
		fw.println("/*	sensor_comp_name				*/	",
				(my_comp==null)?"NO_COMPONENT_EXIST":(my_comp.component_name));
		fw.print  ("/*	mount_component_position		*/");
		
		if(sensor_loca_data==null) 
			fw.println("	0	0	0	1	0	0	0	1	0");
		else{
			loca=new location(sensor_loca_data).normalize();
			p0=loca.multiply(0,0,0);fw.print  ("	",p0.x);	 fw.print  ("	",p0.y);	 fw.print  ("	",p0.z);
			px=loca.multiply(1,0,0);fw.print  ("	",px.x-p0.x);fw.print  ("	",px.y-p0.y);fw.print  ("	",px.z-p0.z);
			py=loca.multiply(0,1,0);fw.print  ("	",py.x-p0.x);fw.print  ("	",py.y-p0.y);fw.println("	",py.z-p0.z);
		}
		my_comp=ek.component_cont.get_component(target_comp_id);
		fw.println("/*	target_component_name			*/	",
				(my_comp==null)?"NO_COMPONENT_EXIST":(my_comp.component_name));
		fw.print  ("/*	target_component_position		*/");
		
		if(target_loca_data==null) 
			fw.println("	0	0	0	1	0	0	0	1	0");
		else{
			loca=new location(target_loca_data).normalize();
			p0=loca.multiply(0,0,0);fw.print  ("	",p0.x);	 fw.print  ("	",p0.y);	 fw.print  ("	",p0.z);
			px=loca.multiply(1,0,0);fw.print  ("	",px.x-p0.x);fw.print  ("	",px.y-p0.y);fw.print  ("	",px.z-p0.z);
			py=loca.multiply(0,1,0);fw.print  ("	",py.x-p0.x);fw.print  ("	",py.y-p0.y);fw.println("	",py.z-p0.z);
		}
		
		my_comp=ek.component_cont.get_component(modify_comp_id);
		fw.println("/*	modify_component_name			*/	",
				(my_comp==null)?"NO_COMPONENT_EXIST":(my_comp.component_name));

		fw.println();
		fw.println("/*	gps data:latitude						*/	",gps.latitude);
		fw.println("/*	gps data:longitude						*/	",gps.longitude);
		fw.println("/*	gps data:altitude						*/	",gps.altitude);
		fw.println("/*	gps data:delta_latitude					*/	",gps.delta_latitude);
		fw.println("/*	gps data:delta_longitude				*/	",gps.delta_longitude);
		fw.println();
		
		fw.close();
	}

	private void set_location(double data[],boolean data_type,
		boolean keep_origin_flag,boolean keep_orientation_flag,engine_kernel ek)
	{
		component sensor_comp,target_comp,modify_comp;
		if((sensor_comp=ek.component_cont.get_component(sensor_comp_id))==null) {
			debug_information.println("Not mount component!");
			return;
		}
		if((target_comp=ek.component_cont.get_component(target_comp_id))==null) {
			debug_information.println("Not target component!");
			return;
		}
		if((modify_comp=ek.component_cont.get_component(modify_comp_id))==null) {
			debug_information.println("Not modify component!");
			return;
		}
		location target_loca,modify_loca,sensor_loca;
		
		if(data_type)
			sensor_loca=new location(data);
		else 
			sensor_loca=location.caculate_location_from_three_point(
				new point(data[0],data[1],data[2]),new point(data[3],data[4],data[5]),
				new point(data[6],data[7],data[8]),caculate_location_modify_flag);
		if(sensor_loca==null) {
			debug_information.println("sensor_loca==null");
			debug_information.println(data[0]+","+data[1]+","+data[2]);
			debug_information.println(data[3]+","+data[4]+","+data[5]);
			debug_information.println(data[6]+","+data[7]+","+data[8]);
			return;
		}
		
		sensor_loca=sensor_comp.absolute_location.multiply(new location(sensor_loca_data)).multiply(sensor_loca);		
		target_loca=target_comp.absolute_location.multiply(new location(target_loca_data));
		modify_loca=modify_comp.absolute_location;
		
		for(component p=sensor_comp;p!=null;p=ek.component_cont.get_component(p.parent_component_id))
			if(p.component_id==modify_comp.component_id) {
				if(keep_origin_flag){
					point difference=sensor_loca.multiply(0,0,0).sub(target_loca.multiply(0,0,0));
					target_loca=location.move_rotate(difference.x,difference.y,difference.z,0,0,0).multiply(target_loca);
				}
				if(keep_orientation_flag) {
					point difference=target_loca.multiply(0,0,0).sub(sensor_loca.multiply(0,0,0));
					target_loca=location.move_rotate(difference.x,difference.y,difference.z,0,0,0).multiply(sensor_loca);
				}
				modify_comp.modify_location(
						modify_comp.move_location
							.multiply(modify_loca.negative())
							.multiply(target_loca)
							.multiply(sensor_loca.negative())
							.multiply(modify_loca).normalize(),
						ek.component_cont);
				sensor_comp.recurse_caculate_location(ek.component_cont);
				target_comp.recurse_caculate_location(ek.component_cont);
				return;
		}
		for(component p=target_comp;p!=null;p=ek.component_cont.get_component(p.parent_component_id))
			if(p.component_id==modify_comp.component_id) {
				if(keep_origin_flag){
					point difference=target_loca.multiply(0,0,0).sub(sensor_loca.multiply(0,0,0));
					sensor_loca=location.move_rotate(difference.x,difference.y,difference.z,0,0,0).multiply(sensor_loca);
				}
				if(keep_orientation_flag) {
					point difference=sensor_loca.multiply(0,0,0).sub(target_loca.multiply(0,0,0));
					sensor_loca=location.move_rotate(difference.x,difference.y,difference.z,0,0,0).multiply(target_loca);
				}
				modify_comp.modify_location(
						modify_comp.move_location
							.multiply(modify_loca.negative())
							.multiply(sensor_loca)
							.multiply(target_loca.negative())
							.multiply(modify_loca).normalize(),
						ek.component_cont);
				sensor_comp.recurse_caculate_location(ek.component_cont);
				target_comp.recurse_caculate_location(ek.component_cont);
				return;
			}
		debug_information.println(
			"Modify component is NOT parent of sensor or target");	
		return;
	}
	
	private void initialize_radar(file_reader f,engine_kernel ek)
	{
		component sensor_comp,target_comp,modify_comp;
		location sensor_loca,target_loca;
		String str;

		sensor_comp_id	=-1;
		target_comp_id	=-1;
		modify_comp_id	=-1;
		sensor_loca_data=null;
		target_loca_data=null;

		caculate_location_modify_flag=f.get_boolean();

		if((sensor_comp=ek.component_cont.search_component(str=f.get_string()))==null) {
			debug_information.println("sensor_comp name is wrong in initialize_radar(file_reader f,engine_kernel ek):	",str);
			return;
		}
		sensor_loca=location.caculate_location_from_three_point(
				new point(f),new point(f),new point(f),caculate_location_modify_flag);
		if(sensor_loca==null) {
			debug_information.println("mount_loca is null in initialize_radar(file_reader f,engine_kernel ek)");
			return;
		}
		if((target_comp=ek.component_cont.search_component(str=f.get_string()))==null){
			debug_information.println("target_comp name is wrong in initialize_radar(file_reader f,engine_kernel ek):	",str);
			return;
		}
		target_loca=location.caculate_location_from_three_point(
				new point(f),new point(f),new point(f),caculate_location_modify_flag);
		if(target_loca==null) {
			debug_information.println("target_loca is null in initialize_radar(file_reader f,engine_kernel ek)");
			return;
		}
		if((modify_comp=ek.component_cont.search_component(str=f.get_string()))==null){
			debug_information.println("modify_comp name is wrong in initialize_radar(file_reader f,engine_kernel ek):	",str);
			return;
		}
		
		gps=new gps_caculater(f.get_double(),f.get_double(),f.get_double(),f.get_double(),f.get_double());
		
		sensor_comp_id	=sensor_comp.component_id;
		target_comp_id	=target_comp.component_id;
		modify_comp_id	=modify_comp.component_id;
		sensor_loca_data=sensor_loca.get_location_data();
		target_loca_data=target_loca.get_location_data();
	}
	public void initialize_radar(String file_name,String file_charset,engine_kernel ek)
	{
		file_reader f=new file_reader(file_name,file_charset);
		initialize_radar(f,ek);
		f.close();
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;

		part p=comp.driver_array[driver_id].component_part;
		initialize_radar(p.directory_name+p.material_file_name,p.file_charset,ek);
	}
	public instance_driver create_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_instance_driver(comp,driver_id,ek);
	}
	public String[] response_event(engine_kernel ek,client_information ci)
	{
		location loca;
		component sensor_comp,target_comp,modify_comp;
		String data_str		=ci.request_response.get_parameter("sensor_data");
		String command_str	=ci.request_response.get_parameter("operation");
		command_str=(command_str==null)?"":command_str.toLowerCase();
		
		debug_information.println(command_str,data_str);
		
		double data[]={1,0,0,0,		0,1,0,0,	0,0,1,0,	0,0,0,1};
		
		if(data_str!=null)
			try{
				for(int index_id,i=0,ni=data.length;i<ni;i++){
					if((index_id=data_str.indexOf(','))<0){
						data[i]=Double.parseDouble(data_str);
						break;
					}
					data[i]=Double.parseDouble(data_str.substring(0,index_id));
					data_str=data_str.substring(index_id+1);
				}
			}catch(Exception e) {
				;
			}
		switch(command_str) {
		case "sensor_component":
			if((sensor_comp=ek.component_cont.search_component())==null)
				break;
			sensor_comp_id=sensor_comp.component_id;
			save(ek);
			break;
		case "sensor_position":
			if((loca=location.caculate_location_from_three_point(
					new point(data[0],data[1],data[2]),new point(data[3],data[4],data[5]),
					new point(data[6],data[7],data[8]),caculate_location_modify_flag))==null)
				break;
			sensor_loca_data=loca.get_location_data();
			save(ek);
			break;
		case "target_component":
			if((target_comp=ek.component_cont.search_component())==null)
				break;
			target_comp_id=target_comp.component_id;
			save(ek);
			break;
		case "target_position":
			if((loca=location.caculate_location_from_three_point(
					new point(data[0],data[1],data[2]),new point(data[3],data[4],data[5]),
					new point(data[6],data[7],data[8]),caculate_location_modify_flag))==null)
				break;
			target_loca_data=loca.get_location_data();
			save(ek);
			break;
		case "modify_component":
			if((modify_comp=ek.component_cont.search_component())==null)
				break;
			modify_comp_id=modify_comp.component_id;
			save(ek);
			break;
			
			
			
		case "set_gps_origin":
			gps=new gps_caculater(data[0],data[1],data[2],gps.delta_latitude,gps.delta_longitude);
			save(ek);
			break;
			
			
			
			
			
		case "set_location_by_location":
			set_location(data,true,false,false,ek);
			break;	
		case "set_location_by_points":
			set_location(data,false,false,false,ek);
			break;
			
			
			
			
			

		case "set_origin_by_gps":
			if(gps==null)
				break;
			gps.caculate(data[0],data[1],data[2]);
			data=location.move_rotate(gps.p.x,gps.p.y,gps.p.z,0,0,0).get_location_data();
			ci.request_response.println("["+gps.p.x+","+gps.p.y+","+gps.p.z+"]");
		case "set_origin_by_location":
			set_location(data,true,false,true,ek);
			break;
		case "set_origin_by_points":
			set_location(data,false,false,true,ek);
			break;
			


			
			
		case "set_orientation_by_angles":
		{
			double alpha=data[0],beta=data[1],gamma=data[2];
			location x_loca=location.move_rotate(0,0,0,beta,0,0);
			location y_loca=location.move_rotate(0,0,0,0,gamma,0);
			location z_loca=location.move_rotate(0,0,0,0,0,alpha);
			loca=new location(new double[] {
					0,	0,	 1,		0,
					1,	0,	 0,		0,
					0,	1,	 0,		0,
					0,	0,	 0,		1
			});
			loca=loca.multiply(z_loca).multiply(x_loca).multiply(y_loca);
			data=loca.get_location_data();
		}
		case "set_orientation_by_location":
			set_location(data,true,true,false,ek);
			break;
		case "set_orientation_by_points":
			set_location(data,false,true,false,ek);
			break;
			
			
			

			
			
		case "touch":
			break;
		default:
			break;
		}
		
		ci.request_response.print("ok");
		
		return null;
	}
}