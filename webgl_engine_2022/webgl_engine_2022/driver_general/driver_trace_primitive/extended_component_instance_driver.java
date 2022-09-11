package driver_trace_primitive;

import kernel_camera.camera_result;
import kernel_common_class.debug_information;
import kernel_component.component;
import kernel_driver.component_instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_render.render_target;
import kernel_render.target_viewport;
import kernel_transformation.box;
import kernel_part.part;

public class extended_component_instance_driver extends component_instance_driver
{
	private String primitive_file_name;
	private int framebuffer_width,framebuffer_height,target_id,old_target_id;
	public void destroy()
	{
		super.destroy();
		primitive_file_name=null;
	}
	public extended_component_instance_driver(component my_comp,int my_driver_id)
	{
		super(my_comp,my_driver_id);
		
		part p=comp.driver_array[driver_id].component_part;
		file_reader f=new file_reader(p.directory_name+p.material_file_name,p.file_charset);
		primitive_file_name=f.get_string();
		f.close();
		
		framebuffer_width=0;
		framebuffer_height=0;
		target_id=-1;
		old_target_id=-1;
	}
	public void response_init_component_data(engine_kernel ek,client_information ci)
	{
		
	}
	public boolean check(int render_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		if((framebuffer_width<=0)||(framebuffer_height<=0))
			return false;
		render_target main_t,rt;
		if((main_t=ci.display_camera_result.target)==null)
			return false;
		if(main_t.target_id!=cr.target.target_id)
			return false;
		
		double aspect=((double)framebuffer_width)/((double)framebuffer_height);
		rt=new render_target(comp.component_name,main_t.camera_id,
			main_t.parameter_channel_id,main_t.comp,main_t.driver_id,main_t.clip_plane,
			framebuffer_width,framebuffer_height,4,
			new box(-aspect,-1.0,-1.0,aspect,1.0,1.0),
			new target_viewport[]{new target_viewport(-1,-1,2,2,1,new double[] {0,0,0,0})},
			false,false,false,false);
		
		if((target_id=ci.target_container.register_target(rt,1,null))!=old_target_id) {
			update_component_parameter_version(0);
			old_target_id=target_id;
		};
		return false;
	}
	public void create_render_parameter(int render_buffer_id,int data_buffer_id,engine_kernel ek,client_information ci,camera_result cr)
	{
		ci.request_response.print(data_buffer_id);
	}
	public void create_component_parameter(engine_kernel ek,client_information ci)
	{
		ci.request_response.print("[",comp.component_id).print(",",target_id);
		
		int component_number=ek.component_cont.root_component.component_id+1;
		component my_comp=ek.component_cont.get_component(0);
		ci.request_response.print(",[",(my_comp==null)?-1:(my_comp.fix_render_driver_id));
		for(int component_id=1;component_id<component_number;component_id++) {
			my_comp=ek.component_cont.get_component(component_id);
			ci.request_response.print(",",(my_comp==null)?-1:(my_comp.fix_render_driver_id));
		}
		ci.request_response.print("]]");
	}
	
	public String[] response_component_event(engine_kernel ek,client_information ci)
	{
		String str=ci.request_response.get_parameter("operation");
		switch((str==null)?"":str) {
		case "download_trace_primitive":
			if((str=ci.request_response.get_parameter("file_type"))==null)
				return null;
			str="."+str+".txt";
			return new String[] {
				ek.scene_par.scene_proxy_directory_name+primitive_file_name+str,
				ek.system_par.local_data_charset
			};
		case "start_trace_primitive":
			if((str=ci.request_response.get_parameter("width"))!=null)
				framebuffer_width=Integer.decode(str);
			if((str=ci.request_response.get_parameter("height"))!=null)
				framebuffer_height=Integer.decode(str);
			update_component_parameter_version(0);
			break;
		case "collect_trace_primitive":
			framebuffer_width=0;
			framebuffer_height=0;
			
			long trace_primitive_number=0;
			String file_name=ek.scene_par.scene_proxy_directory_name+primitive_file_name;
			try {
				trace_primitive_number=new collect_trace_primitive(file_name,ek,ci).trace_primitive_number;
			}catch(Exception e) {
				debug_information.println("collect_trace_primitive fail:	",e.toString());
				e.printStackTrace();
			}
			ci.request_response.println(trace_primitive_number);
			break;
		}
		return null;
	}
}
