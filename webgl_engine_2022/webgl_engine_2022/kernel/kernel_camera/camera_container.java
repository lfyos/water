package kernel_camera;

import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_component.component_container;
import kernel_file_manager.file_reader;

public class camera_container 
{
	public camera camera_array[];
	
	public void destroy()
	{
		if(camera_array!=null)
			for(int i=0,ni=camera_array.length;i<ni;i++)
				if(camera_array[i]!=null){
					camera_array[i].destroy();
					camera_array[i]=null;
				}
		camera_array=null;
	}
	
	public  camera_container(file_reader f,component_container component_cont,int max_camera_stack_number)
	{
		for(camera_array=new camera[0];;){
			long switch_time_length				=f.get_long();
			double distance						=f.get_double();
			double half_fovy_tanl				=Math.tan(f.get_double()*(Math.PI)/360.0);
			double near_value_ratio				=f.get_double();
			double far_value_ratio				=f.get_double();
			double low_precision_scale			=f.get_double();
			double high_precision_scale			=f.get_double();
			
			boolean projection_type_flag		=f.get_boolean();
			boolean movement_flag				=f.get_boolean();
			boolean direction_flag				=f.get_boolean();
			boolean change_type_flag			=f.get_boolean();
			double scale_value					=f.get_double();
			boolean synchronize_location_flag	=f.get_boolean();
			boolean light_camera_flag			=f.get_boolean();
			String component_name				=f.get_string();

			if(component_name==null)
				break;
			if(component_name.compareTo("")==0)
				break ;
			component eye_component;
			if(component_cont.root_component==null)
				break;
			else if((eye_component=component_cont.search_component(component_name))==null)
				debug_information.println("创建相机时,发现相机存在的组件 "+component_name+"不存在");
			else{
				camera bak[]=camera_array;
				camera_array=new camera[bak.length+1];
				for(int i=0,ni=bak.length;i<ni;i++)
					camera_array[i]=bak[i];
				camera_parameter cam_par=new camera_parameter(
						movement_flag,direction_flag,change_type_flag,
						scale_value,switch_time_length,distance,
						half_fovy_tanl,half_fovy_tanl,near_value_ratio,far_value_ratio,
						projection_type_flag,low_precision_scale,high_precision_scale,
						synchronize_location_flag,light_camera_flag);
				camera_array[camera_array.length-1]=new camera(
						eye_component,cam_par,max_camera_stack_number);
			}
		}
		f.close();
	}
}
