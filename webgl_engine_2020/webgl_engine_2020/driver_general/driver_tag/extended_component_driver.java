package driver_tag;

import kernel_component.component;
import kernel_driver.component_driver;
import kernel_driver.instance_driver;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_network.client_request_response;
import kernel_part.part;

import kernel_transformation.point;

public class extended_component_driver extends component_driver
{
	private String text_component_name[];
	
	public int canvas_width,canvas_height;
	public double view_text_height;
	public point line_color,point_color;
	public int text_component_id[];
	
	public tag_instance tag[];

	public void destroy()
	{
		super.destroy();
		text_component_name=null;
		text_component_id=null;
		tag=null;
	}
	public extended_component_driver(file_reader f,part component_part)
	{
		super(component_part);

		canvas_width		=f.get_int();
		canvas_height		=f.get_int();
		view_text_height	=f.get_double();
		
		line_color			=new point(f);
		point_color			=new point(f);
		
		int text_component_name_number=f.get_int();
		text_component_name=new String[text_component_name_number];
		for(int i=0;i<text_component_name_number;i++)
			if((text_component_name[i]=f.get_string())==null)
				text_component_name[i]="";
		text_component_id=null;
		tag=null;
	}
	public void initialize_component_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
//		String component_directory_name=comp.component_directory_name;
//		String scene_directory_name=ek.scene_directory_name;
//		String parameter_directory_name=ek.scene_par.directory_name;
		
		component text_comp;
		int text_component_number=0;
		text_component_id=new int[text_component_name.length];
		for(int i=0,ni=text_component_id.length;i<ni;i++)
			if((text_comp=ek.component_cont.search_component(text_component_name[i]))!=null)
				text_component_id[text_component_number++]=text_comp.component_id;
	
		text_component_name=null;		
		if(text_component_id.length!=text_component_number) {
			int bak[]=text_component_id;
			text_component_id=new int[text_component_number];
			for(int i=0,ni=text_component_id.length;i<ni;i++)
				text_component_id[i]=bak[i];
		}
		tag=new tag_instance[text_component_id.length];
		for(int i=0,ni=tag.length;i<ni;i++)
			tag[i]=new tag_instance();
		return;
	}
	public instance_driver create_instance_driver(component comp,int driver_id,
			engine_kernel ek,client_request_response request_response)
	{
		return new extended_instance_driver(comp,driver_id);
	}
	private void reset_tag_display(engine_kernel ek,client_information ci)
	{
		for(int i=0,ni=tag.length;i<ni;i++){
			if(tag[i]==null)
				continue;
			if(tag[i].is_display()) {
				tag[i].set_text(ek,ci,ci.display_camera_result,
					text_component_id[i],canvas_width,canvas_height,view_text_height);
				continue;
			}
			component text_comp=ek.component_cont.get_component(text_component_id[i]);
			if(text_comp==null)
				continue;
			for(int j=0,nj=text_comp.driver_number();j<nj;j++)
				if(text_comp.driver_array[j] instanceof driver_text.extended_component_driver)
					((driver_text.extended_component_driver)(text_comp.driver_array[j])).set_text(tag[i].t_item);
		}
		update_component_render_version();
	}
	private int get_function_id(client_information ci)
	{
		String function_string;
		int function_id;
		if((function_string=ci.request_response.get_parameter("function"))!=null)
			if((function_id=Integer.decode(function_string))>=0)
				if(function_id<=16)
					return function_id;
		return -1;
	}
	private String[] delete_one_tag(int tag_id,engine_kernel ek,client_information ci)
	{
		for(int i=tag_id,ni=tag.length-1;i<ni;i++)
			tag[i]=tag[i+1];
		tag[tag.length-1]=new tag_instance();
		reset_tag_display(ek,ci);
		
		return null;
	}
	public String[] response_event(component comp,
			int parameter_channel_id,engine_kernel ek,client_information ci)
	{	
		String str;
		int control_function_id;
		
		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;

		switch(str){
		case "reset":
			return null;
		case "mousedown":
			if((control_function_id=get_function_id(ci))<0)
				return null;
			if(ci.parameter!= null)
				if(ci.parameter.comp!=null){
					if(ci.parameter.comp.component_id==comp.component_id){
						if((ci.parameter.body_id>=0)&&(ci.parameter.body_id<tag.length))
							if(tag[ci.parameter.body_id]!=null)
								if(tag[ci.parameter.body_id].state==3){
									boolean flag=true;
									for(int i=0,ni=tag.length;i<ni;i++)
										if(tag[i]!=null)
											if((tag[i].state==1)||(tag[i].state==2)){
												flag=false;
												break;
											}
									if(flag){
										switch(ci.parameter.point_id){
										case 1:
											point dir=tag[ci.parameter.body_id].right_direct;
											tag[ci.parameter.body_id].p0=tag[ci.parameter.body_id].p0.add(dir);
											tag[ci.parameter.body_id].right_direct=dir.reverse();
										case 2:
											tag[ci.parameter.body_id].state=1;
											
											update_component_render_version();
											
											break;
										default:
											break;
										}
										return null;
									}
								}
					}else{
						for(int i=0;i<tag.length;i++)
							if(text_component_id[i]==ci.parameter.comp.component_id)
								if(tag[i]!=null)
									if((tag[i].t_item!=null)&&(tag[i].state==3)){
										boolean flag=true;
										for(int j=0;j<(tag.length);j++)
											if((tag[j].state==1)||(tag[j].state==2)){
												flag=false;
												break;
											}
										if(flag){
											tag[i].state=2;
											
											update_component_render_version();
											return null;
										}
									}
					}
				}
			for(int i=0,ni=tag.length;i<ni;i++)
				if((str=tag[i].set(ek,ci,ci.display_camera_result,text_component_id[i],control_function_id,
					ci.parameter,view_text_height,canvas_width,canvas_height))!=null)
				{
					ci.message_display.set_display_message(str,-1);
					break;
				}
			return null;
		case "mousemove":
			if((control_function_id=get_function_id(ci))<0)
				return null;
			for(int i=0,ni=tag.length;i<ni;i++)
				switch(tag[i].state){
				case 1:
				case 2:
					update_component_render_version();
					tag[i].touch(ek,ci,ci.display_camera_result,text_component_id[i],
						control_function_id,ci.parameter,view_text_height,canvas_width,canvas_height);
					return null;
				}
			return null;
		case "mousewheel":
			if(ci.display_camera_result.target==null)
				return null;
			if((str=ci.request_response.get_parameter("distance"))!=null)
				ci.display_camera_result.cam.parameter.distance=Double.parseDouble(str);
			if((str=ci.request_response.get_parameter("half_fovy_tanl"))!=null)
				ci.display_camera_result.cam.parameter.half_fovy_tanl=Double.parseDouble(str);
			ci.render_buffer.cam_buffer.synchronize_camera_buffer(
				ek.camera_cont.camera_array,ci.display_camera_result.target.camera_id);
			return null;
		case "keydown":
			if((str=ci.request_response.get_parameter("keycode"))==null)
				return null;
			int keycode=Integer.decode(str);
			switch(keycode){
			case 27://ESC
			case 46://delete
			case 8://backspace
				for(int i=tag.length-1;i>=0;i--)
					if(tag[i].test_undone_state())
						return delete_one_tag(i,ek,ci);
				if(keycode==27)//ESC
					break;
				for(int i=0,ni=tag.length;i<ni;i++)
					if(ci.parameter.comp!=null)
						if(ci.parameter.comp.component_id==text_component_id[i])
							return delete_one_tag(i,ek,ci);
				if(keycode==46)//delete
					return delete_one_tag(0,ek,ci);
				else if(tag.length>0)
					return delete_one_tag(tag.length-1,ek,ci);
				else
					return null;
			case 45://insert
				break;
			case 33://pageup
				break;
			case 34://pagedown
				break;
			case 32://spacebar
				reset_tag_display(ek,ci);
				break;
			case 13://Enter
				reset_tag_display(ek,ci);
				break;
			case 37://left arrow
				break;
			case 38://upper arrow
				break;
			case 39://right arrow
				break;
			case 40://down arrow
				break;
			default:
				break;
			}
		}
		return null;
	}
}