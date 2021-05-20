package driver_find_visible;

import java.io.InputStream;

import kernel_common_class.common_reader;
import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_render.render_target;
import kernel_render.target_viewport;
import kernel_transformation.box;

public class extended_instance_driver extends instance_driver
{
	private String triangle_file_name;
	private int framebuffer_width,framebuffer_height,target_id;
	public void destroy()
	{
		super.destroy();
	}
	public extended_instance_driver(component my_comp,int my_driver_id,String my_triangle_file_name)
	{
		super(my_comp,my_driver_id);
		triangle_file_name=my_triangle_file_name;
		framebuffer_width=0;
		framebuffer_height=0;
		target_id=-1;
	}
	public void response_init_data(engine_kernel ek,client_information ci)
	{
	}
	public boolean check(int render_buffer_id,int parameter_channel_id,int data_buffer_id,
			engine_kernel ek,client_information ci,camera_result cr,component_collector collector)
	{
		if((framebuffer_width<=0)||(framebuffer_height<=0))
			return true;
		render_target main_t,rt;
		if((main_t=ci.display_camera_result.target)==null)
			return true;
		if(main_t.target_id!=cr.target.target_id)
			return true;
		rt=new render_target(comp.component_name,main_t.camera_id,
			main_t.parameter_channel_id,main_t.comp,main_t.driver_id,main_t.clip_plane,
			(int)(Math.round(framebuffer_height*ci.parameter.aspect)),framebuffer_height,4,
			new box(0.0-ci.parameter.aspect,-1.0,-1.0,ci.parameter.aspect,1.0,1.0),
			new target_viewport[]{new target_viewport(-1,-1,2,2,1,0,new double[] {0,0,0,0})});
		target_id=ci.target_container.register_target(rt,1,null);
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
		ci.request_response.
			print("[",comp.component_id).
			print(",",target_id).
			print("]");
	}
	public String[] response_event(int parameter_channel_id,engine_kernel ek,client_information ci)
	{
		String str=ci.request_response.get_parameter("operation");
		switch((str==null)?"":str) {
		case "file":
			return new String[] {
				ek.scene_par.scene_proxy_directory_name+triangle_file_name+".stl",
				ek.system_par.local_data_charset
			};
		case "start":
			if((str=ci.request_response.get_parameter("width"))==null)
				break;
			framebuffer_width=Integer.decode(str);
			if((str=ci.request_response.get_parameter("height"))==null)
				break;
			framebuffer_height=Integer.decode(str);
			break;
		case "end":
			framebuffer_width=0;
			framebuffer_height=0;
			InputStream stream;	
			if((stream=ci.request_response.implementor.get_content_stream())==null)
				break;
			triangle_collector tc=new triangle_collector(ek.render_cont.renders);
			str=ci.request_response.implementor.get_request_charset();
			for(common_reader cr=new common_reader(stream,str);;){
				int component_id=cr.get_int();
				int body_id=cr.get_int();
				int face_id=cr.get_int();
				if(cr.eof()) {
					cr.close();
					break;
				}
				int triangle_id=cr.get_int();
				component my_comp=ek.component_cont.get_component(component_id);
				if(my_comp!=null)
					tc.register(my_comp,body_id,face_id,triangle_id);
			}
			tc.write_out(triangle_file_name,ek);
			break;
		}
		return null;
	}
}
