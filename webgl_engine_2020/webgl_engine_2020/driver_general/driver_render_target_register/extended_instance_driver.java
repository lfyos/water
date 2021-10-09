package driver_render_target_register;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_render.render_target;
import kernel_part.part;
import kernel_transformation.box;
import kernel_render.target_viewport;

public class extended_instance_driver extends instance_driver
{
	private int target_id;
	private render_target_parameter	target_parameter[];
	
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		target_id=-1;
		target_parameter=null;
		
		part p=comp.driver_array[driver_id].component_part;
		file_reader f=new file_reader(p.directory_name+p.material_file_name,p.file_charset);
		int ni=f.get_int();
		target_parameter=new render_target_parameter[ni];
		for(int i=0;i<ni;i++)
			target_parameter[i]=new render_target_parameter(f);
		f.close();
	}
	private void write_target_parameter()
	{
		part p=comp.driver_array[driver_id].component_part;
		file_writer f=new file_writer(p.directory_name+p.material_file_name,p.file_charset);
		f.print("/*	target number 	*/		");
		
		if(target_parameter==null)
			f.println(0);
		else{
			int ni=target_parameter.length;
			f.println(ni);
			for(int i=0;i<ni;i++){
				f.println();
				target_parameter[i].write_out(f);
			}
		}
		f.println();
		f.close();
	}
	private void register_target(engine_kernel ek,client_information ci)
	{
		int main_view_part_id=-1;
		double my_x=ci.parameter.x,my_y=ci.parameter.y;
		for(int i=0,ni=target_parameter.length;i<ni;i++)
			for(int j=0,nj=target_parameter[i].viewport.length;j<nj;j++){
				target_viewport tv=target_parameter[i].viewport[j];
				if((tv.x<=my_x)&&(tv.y<=my_y)&&(my_x<=(tv.x+tv.width))&&(my_y<=(tv.y+tv.height))){
					main_view_part_id=i;
					i=ni;
					j=nj;
				}
			}
		render_target main_rt=null;
		for(int i=0,ni=target_parameter.length;i<ni;i++){
			double aspect_value=ci.parameter.aspect;
			aspect_value*=target_parameter[i].viewport[0].width;
			aspect_value/=target_parameter[i].viewport[0].height;
			
			double center_x=target_parameter[i].center_x;
			double center_y=target_parameter[i].center_y;
			
			render_target rt=new render_target(comp.component_name+"/"+Integer.toString(i),
				target_parameter[i].camera_id,target_parameter[i].parameter_channel_id,
				new component[]{ek.component_cont.root_component},null,ci.clip_plane,0,0,1,
				new box(center_x-aspect_value,center_y-1.0,-1.0,center_x+aspect_value,center_y+1.0,1.0),
				target_parameter[i].viewport);
			
			if(main_view_part_id>=0)
				rt.main_display_target_flag=(main_view_part_id==i)?true:false;
			else
				rt.main_display_target_flag=(i==0)?true:false;
			
			ci.target_container.register_target(rt,-1,main_rt);
			if(main_rt==null){
				main_rt=rt;
				target_id=rt.target_id;
			}
		}
	}
	
	public void response_init_instance_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if(ci.display_camera_result.target.target_id!=cr.target.target_id)
			return true;
		register_target(ek,ci);
		return false;
	}
	public void create_render_parameter(
			int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print(target_id);
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		String str;
		int target_id,viewport_id;

		if(ci.display_camera_result.target==null)
			return null;
		if(target_parameter==null)
			return null;
		if((str=ci.request_response.get_parameter("target"))==null)
			return null;
		if((target_id=Integer.decode(str))<0)
			return null;
		if(target_id>=target_parameter.length)
			return null;
		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;

		switch(str.toLowerCase()){
		case "parameter_channel":
			if((str=ci.request_response.get_parameter("parameter_channel"))!=null)
				target_parameter[target_id].parameter_channel_id=Integer.decode(str);
			return null;
		case "camera":
			if((str=ci.request_response.get_parameter("camera"))==null)
				target_parameter[target_id].camera_id++;
			else if((target_parameter[target_id].camera_id=Integer.decode(str))<0)
				target_parameter[target_id].camera_id=0;
			target_parameter[target_id].camera_id%=ek.camera_cont.camera_array.length;
			return null;
		case "set_clear_color":
			if((str=ci.request_response.get_parameter("viewport"))==null)
				return null;
			if((viewport_id=Integer.decode(str))<0)
				return null;
			if(target_parameter[target_id].viewport==null)
				return null;
			if(viewport_id>=target_parameter[target_id].viewport.length)
				return null;

			if(target_parameter[target_id].viewport[viewport_id].clear_color==null)
				target_parameter[target_id].viewport[viewport_id].clear_color=new double[]{0,0,0,1.0};
			if((str=ci.request_response.get_parameter("red"))!=null)
				target_parameter[target_id].viewport[viewport_id].clear_color[0]=Double.parseDouble(str);
			if((str=ci.request_response.get_parameter("green"))!=null)
				target_parameter[target_id].viewport[viewport_id].clear_color[1]=Double.parseDouble(str);
			if((str=ci.request_response.get_parameter("blue"))!=null)
				target_parameter[target_id].viewport[viewport_id].clear_color[2]=Double.parseDouble(str);
			if((str=ci.request_response.get_parameter("alf"))!=null)
				target_parameter[target_id].viewport[viewport_id].clear_color[3]=Double.parseDouble(str);
			
			for(int i=0,ni=target_parameter[target_id].viewport[viewport_id].clear_color.length;i<ni;i++){
				if(target_parameter[target_id].viewport[viewport_id].clear_color[i]<0.0)
					target_parameter[target_id].viewport[viewport_id].clear_color[i]=0.0;
				else if(target_parameter[target_id].viewport[viewport_id].clear_color[i]>1.0)
					target_parameter[target_id].viewport[viewport_id].clear_color[i]=1.0;
			}
			write_target_parameter();			
			break;
		case "get_clear_color":
			if((str=ci.request_response.get_parameter("viewport"))==null)
				return null;
			if((viewport_id=Integer.decode(str))<0)
				return null;
			if(target_parameter[target_id].viewport==null)
				return null;
			if(viewport_id>=target_parameter[target_id].viewport.length)
				return null;
			ci.request_response.print(target_parameter[target_id].viewport[viewport_id].clear_color);
			break;
		}
		return null;
	}
}
