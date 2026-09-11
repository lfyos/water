package kernel_buffer;

import kernel_part.part;
import kernel_scene.scene_kernel;
import kernel_component.component;
import kernel_camera.camera_result;
import kernel_scene.client_information;
import kernel_component.component_container;
import kernel_component.component_link_list;
import kernel_common_class.debug_information;
import kernel_render.render_component_counter;

public class component_render
{
	private component comp[];
	private int driver_id[],flag[],instance_id[],component_number;
		
	public component_link_list delete_in_cll;
	public component_link_list delete_out_cll;
	public component_link_list refresh_cll;
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
		
		if(append_cll!=null)
			append_cll.destroy();
		append_cll=null;
		
	}
	public component_render(int max_part_component_number)
	{
		component_number=0;
		comp		=new component[max_part_component_number];
		driver_id	=new int[max_part_component_number];
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
	public void mark(component_link_list cll,client_information ci,
			camera_result cam_result,render_component_counter rcc)
	{
		delete_in_cll	=null;
		delete_out_cll	=null;
		refresh_cll		=null;
		append_cll		=null;
		
		lastest_in_delete_touch_time	=0;
		lastest_out_delete_touch_time	=0;
		lastest_append_touch_time		=0;
		lastest_refresh_touch_time		=0;
		
		for(component_link_list p=cll;p!=null;p=p.next_list_item){
			int data_buffer_id=p.comp.driver_array.get(p.driver_id).same_part_component_driver_id;
			flag[data_buffer_id]=0;
			instance_id[data_buffer_id]=-1;
		}
		for(int i=0;i<component_number;i++){
			var in_dr=ci.component_instance_driver_cont.get_component_instance_driver(comp[i],driver_id[i]);
			long old_component_render_version=in_dr.get_component_render_version(cam_result.target.target_id);
			long new_component_render_version=comp[i].driver_array.get(driver_id[i]).get_component_render_version();
			int data_buffer_id=comp[i].driver_array.get(driver_id[i]).same_part_component_driver_id;
			flag[data_buffer_id]=(old_component_render_version!=new_component_render_version)?1:2;
			instance_id[data_buffer_id]=i;
		}
//flag==0:	component only in link list
//flag==1:	component in buffer,maybe in link list or not, need update
//flag==2:	component in buffer,maybe in link list or not, unnecessary update

		for(component_link_list p=cll;p!=null;p=p.next_list_item){
			int data_buffer_id=p.comp.driver_array.get(p.driver_id).same_part_component_driver_id;
			switch(flag[data_buffer_id]){
			case 0:
				var in_dr=ci.component_instance_driver_cont.get_component_instance_driver(p.comp,p.driver_id);
				long old_component_render_version=in_dr.get_component_render_version(cam_result.target.target_id);
				long new_component_render_version=p.comp.driver_array.get(p.driver_id).get_component_render_version();
				flag[data_buffer_id]|=(old_component_render_version!=new_component_render_version)?4:8;

				if(p.comp.uniparameter.touch_time>lastest_append_touch_time)
					lastest_append_touch_time=p.comp.uniparameter.touch_time;
				append_cll=new component_link_list(p.comp,p.driver_id,append_cll);
				break;
			case 1:
				flag[data_buffer_id]|=4;
				if(p.comp.uniparameter.touch_time>lastest_refresh_touch_time)
					lastest_refresh_touch_time=p.comp.uniparameter.touch_time;
				refresh_cll=new component_link_list(p.comp,p.driver_id,refresh_cll);
				break;
			case 2:			
				flag[data_buffer_id]|=8;
				rcc.component_keep_number++;
				break;
			}
		}
		
//flag==0:	impossible,has change to 0|4 or 0|8
//	flag==0|4:	in link list, not in buffer, need update, 		 has add to append_cll
//	flag==0|8:	in link list, not in buffer, unnecessary update, has add to append_cll

//flag==1:	component in buffer,NOT in link list, need update,		  should do delete
//flag==2:	component in buffer,NOT in link list, unnecessary update, should do delete
		
//flag==1|4: both in link list and buffer, need update,			has add to refresh_cll
//flag==2|8: both in link list and buffer, unnecessary update,	should do keep

//create delete link list
		for(int i=0;i<component_number;i++){
			int data_buffer_id=comp[i].driver_array.get(driver_id[i]).same_part_component_driver_id;
			switch(flag[data_buffer_id]){
			case 1:
			case 2:
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
			}
		}
//flag==0:	impossible,has change to 0|4 or 0|8
//		flag==0|4:	in link list, not in buffer, need update, 		 has add to append_cll
//		flag==0|8:	in link list, not in buffer, unnecessary update, has add to append_cll

//flag==1:	component in buffer,NOT in link list, need update,		 has add to delete_in_cll or delete_out_cll
//flag==2:	component in buffer,NOT in link list, unnecessary update,has add to delete_in_cll or delete_out_cll
			
//flag==1|4: both in link list and buffer,		  need update,		  has add to refresh_cll
//flag==2|8: both in link list and buffer,		  unnecessary update, should keep

		return;
	}
	public void create_append_render_parameter(
			response_flag create_flag,component_link_list cll,long render_current_time,
			scene_kernel sk,client_information ci,camera_result cam_result,render_component_counter rcc)
	{
		for(component_link_list p=cll;p!=null;p=p.next_list_item){
			int data_buffer_id=p.comp.driver_array.get(p.driver_id).same_part_component_driver_id;
			switch(flag[data_buffer_id]){
			case 0|4://update append
			case 0|8://no update append
			case 1|4://refresh
				var instance_driver=ci.component_instance_driver_cont.
						get_component_instance_driver(p.comp,p.driver_id);
				if(instance_driver.get_component_parameter_version()<=0){
					// 	if buffer parameter has not transfer to client broswer,
					//	do not transfer render parameter to client broswer
					flag[data_buffer_id]|=16;			//abandon transfer append or refresh data
					break;
				}
				if((rcc.component_append_number+rcc.component_refresh_number)>=sk.scene_par.most_component_append_number)
					if((render_current_time-p.comp.uniparameter.touch_time)>sk.scene_par.touch_time_length){
						if(ci.parameter.comp==null){
							flag[data_buffer_id]|=16;	//abandon transfer append or refresh data
							break;
						}
						if(ci.parameter.comp.component_id!=p.comp.component_id){
							flag[data_buffer_id]|=16;	//abandon transfer append or refresh data
							break;
						}
					}
				int my_instance_id;
				if(flag[data_buffer_id]==(1|4)){//refresh component, put in original buffer
					rcc.component_refresh_number++;
					my_instance_id=instance_id[data_buffer_id];
				}else if(delete_in_cll!=null){//put in delete vissible buffer
					rcc.component_refresh_number++;
					int delete_buffer_id=delete_in_cll.comp.driver_array.get(
							delete_in_cll.driver_id).same_part_component_driver_id;
					my_instance_id=instance_id[delete_buffer_id];
					delete_in_cll=delete_in_cll.next_list_item;
				}else if(delete_out_cll!=null){//put in delete unvissible buffer
					rcc.component_refresh_number++;
					int delete_buffer_id=delete_out_cll.comp.driver_array.get(
							delete_out_cll.driver_id).same_part_component_driver_id;
					my_instance_id=instance_id[delete_buffer_id];
					delete_out_cll=delete_out_cll.next_list_item;
				}else{//append to buffer end
					rcc.component_append_number++;
					my_instance_id=component_number++;
				}
				comp[my_instance_id]		=p.comp;
				driver_id[my_instance_id]	=p.driver_id;
				instance_id[data_buffer_id]	=my_instance_id;
				
				flag[data_buffer_id]|=32;	//DO transfer append or refresh data

				part my_part=p.comp.driver_array.get(p.driver_id).component_part;
				
				if(create_flag.first_item_flag) {
					ci.request_response.print("[");
					create_flag.first_item_flag=false;
				}else
					ci.request_response.print(",[");
				
				if((create_flag.render_id!=my_part.render_id)||(create_flag.part_id!=my_part.part_id)) {
					create_flag.render_id		=my_part.render_id;
					create_flag.part_id			=my_part.part_id;
					create_flag.target_id		=cam_result.target.target_id;
					ci.request_response.print(		my_part.render_id).
										print(",",	my_part.part_id).
										print(",",	cam_result.target.target_id).
										print(",");
				}else if(create_flag.target_id!=cam_result.target.target_id) { 
					create_flag.target_id=cam_result.target.target_id;
					ci.request_response.print(cam_result.target.target_id).
										print(",");			
				}
				ci.request_response.print(data_buffer_id).
									print(",",my_instance_id).
									print(",");
				try{
					instance_driver.create_render_parameter(sk,ci,cam_result);
				}catch(Exception e){
					e.printStackTrace();
					
					debug_information.println("instance driver create_render_parameter fail:	",e.toString());
					debug_information.println("Component name:	",	cll.comp.component_name);
					debug_information.println("Driver ID:		",	cll.driver_id);
					debug_information.println("Part user name:	",	my_part.user_name);
					debug_information.println("Part system name:",	my_part.system_name);
					debug_information.println("Mesh file name:	",	my_part.directory_name+my_part.mesh_file_name);
					
				}
				ci.request_response.print("]");

				instance_driver.update_component_render_version(cam_result.target.target_id,
						p.comp.driver_array.get(p.driver_id).get_component_render_version());
				break;
			}
		}
	}
//flag==0:	impossible,has change to 0|4 or 0|8
//	flag==0|4:		impossible,has change to 0|4|16 or 0|4|32
//	flag==0|8:		impossible,has change to 0|4|16 or 0|4|32
	
//	flag==0|4|16:	in link list, not in buffer, need update, 		 has add to append_cll,abandon transfer data
//	flag==0|8|16:	in link list, not in buffer, unnecessary update, has add to append_cll,abandon transfer data
	
//	flag==0|4|32:	in link list, not in buffer, need update, 		 has add to append_cll,DO transfer data
//	flag==0|8|32:	in link list, not in buffer, unnecessary update, has add to append_cll,DO transfer data	
	

//flag==1:	component in buffer,NOT in link list, need update,		 has add to delete_in_cll or delete_out_cll
//flag==2:	component in buffer,NOT in link list, unnecessary update,has add to delete_in_cll or delete_out_cll
		
//flag==1|4: 		impossible,has change to 1|4|16 or 1|4|32
	
//	flag==1|4|16:	both in link list and buffer,need update,has add to refresh_cll,abandon transfer data
//	flag==1|4|32:	both in link list and buffer,need update,has add to refresh_cll,DO transfer data

//flag==2|8: both in link list and buffer,		  unnecessary update, should keep	
	
	
	public void create_delete_render_parameter(response_flag create_flag,
			int render_id,int part_id,component_link_list cll,long render_current_time,
			scene_kernel sk,client_information ci,int my_target_id,render_component_counter rcc)
	{
		for(;cll!=null;cll=cll.next_list_item) {
			int data_buffer_id=cll.comp.driver_array.get(cll.driver_id).same_part_component_driver_id;
			switch(flag[data_buffer_id]){
			case 1:
			case 2:
				if(rcc.component_delete_number>=sk.scene_par.most_component_delete_number)
					if((render_current_time-cll.comp.uniparameter.touch_time)>sk.scene_par.touch_time_length){
						flag[data_buffer_id]|=16;//abandon delete
						break;
					}
				int my_instance_id=instance_id[data_buffer_id];
				instance_id[data_buffer_id]=-1;
				
				part my_part=cll.comp.driver_array.get(cll.driver_id).component_part;
				if(create_flag.first_item_flag)
					create_flag.first_item_flag=false;
				else
					ci.request_response.print(",");
				if((create_flag.render_id!=my_part.render_id)||(create_flag.part_id!=my_part.part_id)) {
					create_flag.render_id		=my_part.render_id;
					create_flag.part_id			=my_part.part_id;
					create_flag.target_id		=my_target_id;
					
					ci.request_response.print("[",	my_part.render_id).
										print(",",	my_part.part_id).
										print(",",	my_target_id).
										print(",",	my_instance_id).
										print("]");
				}else if(create_flag.target_id!=my_target_id) { 
					create_flag.target_id=my_target_id;
					ci.request_response.print("[",my_target_id).
										print(",",my_instance_id).
										print("]");
				}else
					ci.request_response.print(my_instance_id);

				flag[data_buffer_id]|=32;	//has done delete
				
				component_number--;
				if(my_instance_id<component_number) {
					comp[my_instance_id]=comp[component_number];
					driver_id[my_instance_id]=driver_id[component_number];
					
					data_buffer_id=comp[my_instance_id].driver_array.
							get(driver_id[my_instance_id]).same_part_component_driver_id;
					instance_id[data_buffer_id]=my_instance_id;
				}
				comp[component_number]=null;
				driver_id[component_number]=-1;

				rcc.component_delete_number++;
				break;
			}
		}
		return;
	}
	
//flag==0:	impossible,has change to 0|4 or 0|8
//	flag==0|4:		impossible,has change to 0|4|16 or 0|4|32
//	flag==0|8:		impossible,has change to 0|4|16 or 0|4|32
	
//	flag==0|4|16:	in link list, not in buffer, need update, 		 has add to append_cll,abandon transfer data
//	flag==0|8|16:	in link list, not in buffer, unnecessary update, has add to append_cll,abandon transfer data
	
//	flag==0|4|32:	in link list, not in buffer, need update, 		 has add to append_cll,DO transfer data
//	flag==0|8|32:	in link list, not in buffer, unnecessary update, has add to append_cll,DO transfer data	

//flag==1:	component NOT in buffer,NOT in link list, need update,		 has add to delete_in_cll or delete_out_cll,be replaced 
//flag==2:	component NOT in buffer,NOT in link list, unnecessary update,has add to delete_in_cll or delete_out_cll,be replaced 

//flag==1|16:	component in buffer,NOT in link list, need update,		 has add to delete_in_cll or delete_out_cll,abandon delete
//flag==2|16:	component in buffer,NOT in link list, unnecessary update,has add to delete_in_cll or delete_out_cll,abandon delete
//flag==1|32:	component in buffer,NOT in link list, need update,		 has add to delete_in_cll or delete_out_cll,DO delete
//flag==2|32:	component in buffer,NOT in link list, unnecessary update,has add to delete_in_cll or delete_out_cll,DO delete	

//flag==1|4: 		impossible,has change to 1|4|16 or 1|4|32
	
//	flag==1|4|16:	both in link list and buffer,need update,has add to refresh_cll,abandon transfer data
//	flag==1|4|32:	both in link list and buffer,need update,has add to refresh_cll,DO transfer data

//flag==2|8: both in link list and buffer,		  unnecessary update, should keep	
	
	public void register_location(scene_kernel sk,client_information ci)
	{
		for(int i=0;i<component_number;i++){
			int data_buffer_id=comp[i].driver_array.get(driver_id[i]).same_part_component_driver_id;
			switch(flag[data_buffer_id]){
			case 1|16:
			case 2|16:
				break;
			default:
				ci.render_buffer.location_buffer.put_in_list(comp[i],sk);
				break;
			}
		}
	}
}