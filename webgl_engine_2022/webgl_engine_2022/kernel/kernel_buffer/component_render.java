package kernel_buffer;

import kernel_camera.camera_result;
import kernel_component.component;
import kernel_component.component_container;
import kernel_component.component_link_list;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_driver.component_instance_driver;
import kernel_part.part;
import kernel_render.render_component_counter;
import kernel_common_class.debug_information;

//	flag usage:
//	1: in buffer,	   need update
//	2: in buffer,	No need update
//	4: in list,		   need update
//	8: in list,		No need update

//	16:can not delete, so become keep
//	32:delete success, so deleted

//	64:can not append
//	128:append success

public class component_render
{
	private component comp[];
	private int driver_id[],flag[],instance_id[],component_number;
		
	public component_link_list delete_in_cll;
	public component_link_list delete_out_cll;
	public component_link_list refresh_cll;
	public component_link_list keep_cll;
	public component_link_list append_cll;
	
	public long lastest_append_touch_time;
	public long lastest_refresh_touch_time;
	public long lastest_in_delete_touch_time;
	public long lastest_out_delete_touch_time;

	public void destroy()
	{
		if(comp!=null)
			for(int i=0,ni=comp.length;i<ni;i++)
				if(comp[i]!=null)
					comp[i]=null;
		comp=null;
		driver_id=null;
		flag=null;
		instance_id=null;
		
		if(delete_in_cll!=null)
			delete_in_cll.destroy();
		delete_in_cll=null;
		
		if(delete_out_cll!=null)
			delete_out_cll.destroy();
		delete_out_cll=null;
		
		if(refresh_cll!=null)
			refresh_cll.destroy();
		refresh_cll=null;
		
		if(keep_cll!=null)
			keep_cll.destroy();
		keep_cll=null;
		
		if(append_cll!=null)
			append_cll.destroy();
		append_cll=null;
		
	}
	public component_render(int max_part_component_number)
	{
		component_number=0;
		
		comp		=new component[max_part_component_number];
		comp[0]		=null;
		
		driver_id	=new int[max_part_component_number];
		driver_id[0]=-1;
		
		flag		=new int[max_part_component_number];
		instance_id	=new int[max_part_component_number];
	}
	
	public void clear_clip_flag(component_container component_cont)
	{
		for(int i=0;i<component_number;i++)
			for(component p=comp[i];p!=null;p=component_cont.get_component(p.parent_component_id))
				p.clip.has_done_clip_flag=false;
	}
	public void test_clip_flag_of_delete_component(
			camera_result cr,component_container component_cont,int parameter_channel_id)
	{
		for(int i=0;i<component_number;i++)
			cr.clipper_test(comp[i],component_cont,parameter_channel_id);
	}
	
	private void clear_component_link_list()
	{
		delete_in_cll	=null;
		delete_out_cll	=null;
		keep_cll		=null;
		refresh_cll		=null;
		append_cll		=null;
		
		lastest_in_delete_touch_time	=0;
		lastest_out_delete_touch_time	=0;
		lastest_append_touch_time		=0;
		lastest_refresh_touch_time		=0;
	}
	
	public void mark(component_link_list cll,client_information ci,
			camera_result cam_result,int render_buffer_id,render_component_counter rcc)
	{
		clear_component_link_list();
		
// clear component flag in link list
		for(component_link_list p=cll;p!=null;p=p.next_list_item){
			int flag_id=p.comp.driver_array.get(p.driver_id).same_part_component_driver_id;
			flag[flag_id]=0;
			instance_id[flag_id]=-1;
		}
// set component flag in buffer
		for(int i=0;i<component_number;i++){
			component_instance_driver in_dr=ci.component_instance_driver_cont.get_component_instance_driver(comp[i],driver_id[i]);
			long old_component_render_version=in_dr.get_component_render_version(render_buffer_id);
			long new_component_render_version=comp[i].driver_array.get(driver_id[i]).get_component_render_version();
			int flag_id=comp[i].driver_array.get(driver_id[i]).same_part_component_driver_id;
			flag[flag_id]=(old_component_render_version!=new_component_render_version)?1:2;
			instance_id[flag_id]=i;
		}
// append component flag in link list	
		for(component_link_list p=cll;p!=null;p=p.next_list_item){
			int flag_id=p.comp.driver_array.get(p.driver_id).same_part_component_driver_id;
			switch(flag[flag_id]){
			case 0://component not in buffer,but in link list 
				component_instance_driver in_dr=ci.component_instance_driver_cont.get_component_instance_driver(p.comp,p.driver_id);
				long old_component_render_version=in_dr.get_component_render_version(render_buffer_id);
				long new_component_render_version=p.comp.driver_array.get(p.driver_id).get_component_render_version();
				flag[flag_id]|=(old_component_render_version!=new_component_render_version)?4:8;
				
				if(p.comp.uniparameter.touch_time>lastest_append_touch_time)
					lastest_append_touch_time=p.comp.uniparameter.touch_time;
				append_cll=new component_link_list(p.comp,p.driver_id,append_cll);
				break;
			case 1://component in both buffer and link list, modified,should update 
				flag[flag_id]|=4;
				if(p.comp.uniparameter.touch_time>lastest_refresh_touch_time)
					lastest_refresh_touch_time=p.comp.uniparameter.touch_time;
				refresh_cll=new component_link_list(p.comp,p.driver_id,refresh_cll);
				break;
			case 2://component in both buffer and link list, NOT modified, should NOT update 
				flag[flag_id]|=8;
				rcc.component_keep_number++;
				keep_cll=new component_link_list(p.comp,p.driver_id,keep_cll);
				break;
			default://impossible
				flag[flag_id]=0;
				break;
			}
		}

//create delete_cll, refresh_cll, keep_cll link list
		for(int i=0;i<component_number;i++){
			int flag_id=comp[i].driver_array.get(driver_id[i]).same_part_component_driver_id;
			switch(flag[flag_id]){
			case 1://last display(refresh),			this not display,DELETE
			case 2://last display(not refresh),		this not display,DELETE
				if(comp[i].clip.can_be_clipped_flag){
					if(comp[i].uniparameter.touch_time>lastest_out_delete_touch_time)
						lastest_out_delete_touch_time=comp[i].uniparameter.touch_time;
					delete_out_cll=new component_link_list(comp[i],driver_id[i],delete_out_cll);
				}else{
					if(comp[i].uniparameter.touch_time>lastest_in_delete_touch_time)
						lastest_in_delete_touch_time=comp[i].uniparameter.touch_time;
					delete_in_cll=new component_link_list(comp[i],driver_id[i],delete_in_cll);
				}
				break;
			case 1+4://last display(refresh),		this display(refresh),DELETE
				break;
			case 2+8://last display(not refresh),	this display(not refresh),KEEP
				break;	
			default://impossible
				flag[flag_id]=0;
				break;
			}
		}
		return;
	}
	
	public void create_delete_render_parameter(
			response_flag create_flag,
			int render_id,int part_id,int render_buffer_id,
			component_link_list p,long render_current_time,
			engine_kernel ek,client_information ci,render_component_counter rcc)
	{
		for(;p!=null;p=p.next_list_item) {
			int flag_id=p.comp.driver_array.get(p.driver_id).same_part_component_driver_id;
			switch(flag[flag_id]&(1+2+4+8)){
			case 1://last display(refresh),			this not display,DELETE
			case 2://last display(not refresh),		this not display,DELETE
				if((render_current_time-p.comp.uniparameter.touch_time)>ek.scene_par.touch_time_length)
					if(rcc.component_delete_number>=ek.scene_par.most_component_delete_number){
						flag[flag_id]|=16;//become KEEP
						break;
					}
				int my_instance_id=instance_id[flag_id];
				instance_id[flag_id]=-1;
				
				part my_part=p.comp.driver_array.get(p.driver_id).component_part;
				if(create_flag.first_item_flag)
					create_flag.first_item_flag=false;
				else
					ci.request_response.print(",");
				if((create_flag.render_id!=my_part.render_id)||(create_flag.part_id!=my_part.part_id)) {
					create_flag.render_id		=my_part.render_id;
					create_flag.part_id			=my_part.part_id;
					create_flag.render_buffer_id=render_buffer_id;
					
					ci.request_response.print("[",	my_part.render_id).
										print(",",	my_part.part_id).
										print(",",	render_buffer_id).
										print(",",	my_instance_id).
										print("]");
				}else if(create_flag.render_buffer_id!=render_buffer_id) { 
					create_flag.render_buffer_id=render_buffer_id;
					ci.request_response.print("[",render_buffer_id).
										print(",",my_instance_id).
										print("]");
				}else
					ci.request_response.print(my_instance_id);
			
				flag[flag_id]|=32;
				
				component_number--;
				if(my_instance_id<component_number) {
					comp[my_instance_id]=comp[component_number];
					driver_id[my_instance_id]=driver_id[component_number];
					
					flag_id=comp[my_instance_id].driver_array.
							get(driver_id[my_instance_id]).same_part_component_driver_id;
					instance_id[flag_id]=my_instance_id;
				}
				comp[component_number]=null;
				driver_id[component_number]=-1;
				
				rcc.component_delete_number++;
				break;
			case 1+4://last display(refresh),		this display(refresh),		REFRESH
				break;
			case 2+8://last display(not refresh),	this display(not refresh),	KEEP
				break;
			default://impossible
				flag[flag_id]=0;
				break;
			}
		}
		return;
	}
	
	public void create_append_render_parameter(
			boolean append_or_refresh_flag,response_flag create_flag,
			component_link_list cll,long render_current_time,
			engine_kernel ek,client_information ci,
			camera_result cam_result,int render_buffer_id,render_component_counter rcc)
	{
		for(component_link_list p=cll;p!=null;p=p.next_list_item){
			int my_flag;
			int buffer_id=p.comp.driver_array.get(p.driver_id).same_part_component_driver_id;
			switch(my_flag=flag[buffer_id]&(1+2+4+8)){
			default://impossible
				flag[buffer_id]=0;
				break;
			case 1:		//last display(refresh),		this not display,			DELETE,NO APPEND
			case 2:		//last display(not refresh),	this not display,			DELETE,NO APPEND
			case 2+8:	//last display(not refresh),	this display(not refresh),	KEEP,NO APPEND
				break;
			case 1+4:	//last display(refresh),		this display(refresh),		DELETE,		DO ADD
			case 4:		//last not display,				this display(refresh),		NOT EXIST,	DO ADD
			case 8:		//last not display,				this display(not refresh),	NOT EXIST,	DO ADD
				
				component_instance_driver in_dr=ci.component_instance_driver_cont.
									get_component_instance_driver(p.comp,p.driver_id);
				if(in_dr.get_component_parameter_version()<=0){
					// 	if buffer parameter has not transfer to client broswer,
					//	do not transfer render parameter to client broswer
					flag[buffer_id]|=64;
					continue;
				}
				
				if((render_current_time-p.comp.uniparameter.touch_time)>ek.scene_par.touch_time_length)
					if((rcc.component_append_number+rcc.component_refresh_number)>=ek.scene_par.most_component_append_number){
						if(ci.parameter.comp==null){
							flag[buffer_id]|=64;
							continue;
						}
						if(ci.parameter.comp.component_id!=p.comp.component_id){
							flag[buffer_id]|=64;
							continue;
						}
					}	
				int my_instance_id;
				if(my_flag==(1+4))
					my_instance_id=instance_id[buffer_id];
				else{
					my_instance_id=component_number++;
					instance_id	[buffer_id]=my_instance_id;
					
					comp[my_instance_id]		=p.comp;
					driver_id[my_instance_id]	=p.driver_id;
				}
				
				flag[buffer_id]|=128;
				
				part my_part=p.comp.driver_array.get(p.driver_id).component_part;
				
				if(create_flag.first_item_flag) {
					ci.request_response.print("[");
					create_flag.first_item_flag=false;
				}else
					ci.request_response.print(",[");
				
				if((create_flag.render_id!=my_part.render_id)||(create_flag.part_id!=my_part.part_id)) {
					create_flag.render_id		=my_part.render_id;
					create_flag.part_id			=my_part.part_id;
					create_flag.render_buffer_id=render_buffer_id;
					ci.request_response.print(		my_part.render_id).
										print(",",	my_part.part_id).
										print(",",	render_buffer_id).print(",");
				}else if(create_flag.render_buffer_id!=render_buffer_id) { 
					create_flag.render_buffer_id=render_buffer_id;
					ci.request_response.print(render_buffer_id).print(",");
				}
				ci.request_response.print(my_instance_id).print(",");
				
				try{
					in_dr.create_render_parameter(render_buffer_id,
						p.comp.driver_array.get(p.driver_id).same_part_component_driver_id,
						ek,ci,cam_result);
				}catch(Exception e){
					debug_information.println("instance driver create_render_parameter fail:	",e.toString());
					debug_information.println("Component name:	",	cll.comp.component_name);
					debug_information.println("Driver ID:		",	cll.driver_id);
					debug_information.println("Part user name:	",	my_part.user_name);
					debug_information.println("Part system name:",	my_part.system_name);
					debug_information.println("Mesh file name:	",	my_part.directory_name+my_part.mesh_file_name);
					e.printStackTrace();
				}
				ci.request_response.print("]");

				in_dr.update_component_render_version(render_buffer_id,
						p.comp.driver_array.get(p.driver_id).get_component_render_version());
				
				if(append_or_refresh_flag)
					rcc.component_append_number++;
				else
					rcc.component_refresh_number++;
				
				break;
			}
		}
	}
	public void register_location(engine_kernel ek,client_information ci)
	{
		for(int i=0;i<component_number;i++){
			int flag_id=comp[i].driver_array.get(driver_id[i]).same_part_component_driver_id;
			switch(flag[flag_id]&(1+2+4+8)){
			case 4:		//last not display,				this display(refresh),		need position
			case 8:		//last not display,				this display(not refresh),	need position
			case 1+4:	//last display(refresh),		this display(refresh),		need position 	
			case 2+8:	//last display(not refresh),	this display(not refresh),	need position 	
				ci.render_buffer.location_buffer.put_in_list(comp[i],ek);
				break;
			default:
				break;
			}
		}
		clear_component_link_list();
	}
}