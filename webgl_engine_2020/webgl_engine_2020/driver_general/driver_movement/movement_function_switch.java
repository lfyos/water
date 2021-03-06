package driver_movement;

import kernel_camera.camera_parameter;
import kernel_camera.locate_camera;
import kernel_common_class.debug_information;
import kernel_common_class.web_page;
import kernel_component.component;
import kernel_component.component_array;
import kernel_component.component_selection;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_transformation.location;
import kernel_component.component_collector;


public class movement_function_switch
{
	private engine_kernel ek;
	private client_information ci;
	private movement_manager manager;
	
	private location target_direction[];
	private component_array all_components;
	
	private long switch_time_length;

	private void add_direction(location dir,boolean clear_flag)
	{
		if(clear_flag)
			target_direction=null;
		if(dir==null)
			return;
		if(target_direction==null)
			target_direction=new location[1];
		else{
			location bak[]=target_direction;
			target_direction=new location[bak.length+1];
			for(int i=0,n=bak.length;i<n;i++)
				target_direction[i]=bak[i];
		}
		target_direction[target_direction.length-1]=dir;
	}
	private  void add_component(movement_tree t)
	{
		if(t!=null){
			if(t.children!=null)
				for(int i=0,n=t.children.length;i<n;i++)
					add_component(t.children[i]);
			if(t.move!=null) {
				component moved_component;
				if((moved_component=ek.component_cont.get_component(t.move.moved_component_id))!=null){
					all_components.add_component(moved_component);
					if(t.move.follow_component_id!=null)
						for(int i=0,ni=t.move.follow_component_id.length;i<ni;i++)
							if((moved_component=ek.component_cont.get_component(t.move.follow_component_id[i]))!=null)
								all_components.add_component(moved_component);
				}
			}
			add_direction(t.direction,((t.children!=null)&&(t.direction!=null))?true:false);
		}
	}
	private int locate_camera()
	{
		String str;
		int current_movement_id;
		
		if(manager.root_movement==null)
			return -1;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		if((current_movement_id=Integer.decode(str))<0)
			return -1;
	
		movement_searcher searcher=new movement_searcher(manager.root_movement,current_movement_id);
		if(searcher.result_parent==null)
			return -1;
		
		if(searcher.result!=null){
			manager.movement_start(
				ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
				current_movement_id,ek.component_cont,true,switch_time_length);
			add_component(searcher.result);
			if((all_components.get_box()!=null)&&(ci.display_camera_result.cam.parameter.movement_flag))
				(new locate_camera(ci.display_camera_result.cam)).locate_on_components(
						ek.modifier_cont[manager.move_channel_id.movement_modifier_id],all_components.get_box(),
						ci.display_camera_result.cam.parameter.direction_flag?location.combine_location(target_direction):null,
								ci.display_camera_result.cam.parameter.scale_value,
								ci.parameter.aspect,true,true,false);						
		}
		return searcher.result_parent.movement_tree_id;
	}
	private void edit_move()
	{
		if(manager.root_movement==null)
			return;
		String str;	
		int move_id;
		
		if((str=ci.request_response.get_parameter("id"))==null)
			move_id=manager.last_edit_move_id;
		else
			move_id=Integer.decode(str);
		if(move_id<0)
			move_id=manager.root_movement.movement_tree_id;
		manager.last_edit_move_id=move_id;
		new movement_edit(ek,ci,manager,move_id);
	}
	private int delete_move()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if((searcher.result==null)||(searcher.result_parent==null))
			return -1;
		
		add_component(searcher.result);
		for(int i=0,n=all_components.component_number;i<n;i++)
			all_components.comp[i].modify_display_flag(
					manager.move_channel_id.all_parameter_channel_id,true,ek.component_cont);
		
		int current_movement_id=searcher.result_parent.movement_tree_id;
		if(searcher.result_parent.children.length>1){
			movement_tree tmp[]=new movement_tree[searcher.result_parent.children.length-1];
			for(int i=0,j=0;i<(searcher.result_parent.children.length);i++){
				if(searcher.result_id!=i)
						tmp[j++]=searcher.result_parent.children[i];
				else if(i>0)
						current_movement_id=searcher.result_parent.children[i-1].movement_tree_id;
			}
			searcher.result_parent.children=tmp;
		}
		manager.movement_start(
				ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
				current_movement_id,ek.component_cont,true,switch_time_length);
		return searcher.result_parent.movement_tree_id;
	}
	private int reverse_move()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if((searcher.result==null)||(searcher.result_parent==null))
			return -1;
		add_component(searcher.result);
		for(int i=0,n=all_components.component_number;i<n;i++)
			all_components.comp[i].modify_display_flag(
					manager.move_channel_id.all_parameter_channel_id,true,ek.component_cont);
		
		int current_movement_id=searcher.result_parent.movement_tree_id;
		searcher.result.reverse();
		manager.movement_start(
				ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
				current_movement_id,ek.component_cont,true,switch_time_length);

		return searcher.result_parent.movement_tree_id;
	}
	
	private int updown_move(boolean updown_flag)
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if((searcher.result==null)||(searcher.result_parent==null))
			return -1;
				
		if(updown_flag){
			if(searcher.result_id>0){
				searcher.result_parent.children[searcher.result_id  ]=searcher.result_parent.children[searcher.result_id-1];
				searcher.result_parent.children[searcher.result_id-1]=searcher.result;
				searcher.result_id--;
			}
		}else{
			if(searcher.result_id<(searcher.result_parent.children.length-1)){
				searcher.result_parent.children[searcher.result_id  ]=searcher.result_parent.children[searcher.result_id+1];
				searcher.result_parent.children[searcher.result_id+1]=searcher.result;
				searcher.result_id++;
			}
		}
		add_component(searcher.result);
		for(int i=0;i<(all_components.component_number);i++)
			all_components.comp[i].modify_display_flag(
					manager.move_channel_id.all_parameter_channel_id,true,ek.component_cont);
		
		manager.movement_start(
				ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
				searcher.result.movement_tree_id,ek.component_cont,true,switch_time_length);
		
		return searcher.result_parent.movement_tree_id;
	}
	private int set_sequence_flag(boolean new_sequence_flag)
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if(searcher.result==null)
			return -1;
		searcher.result.sequence_flag=new_sequence_flag;
		
		manager.movement_start(
				ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
				searcher.result.movement_tree_id,ek.component_cont,true,switch_time_length);
		
		return ((searcher.result_parent==null)?manager.root_movement:searcher.result_parent).movement_tree_id;
	}
	private int tobuffer()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if((searcher.result==null)||(searcher.result_parent==null))
			return -1;
		
		int current_movement_id=searcher.result_parent.movement_tree_id;
		
		if(searcher.result_parent.children.length>1){
			movement_tree tmp[]=new movement_tree[searcher.result_parent.children.length-1];
			for(int i=0,j=0;i<(searcher.result_parent.children.length);i++){
				if(searcher.result_id!=i)
					tmp[j++]=searcher.result_parent.children[i];
				else
					if(i>0)
						current_movement_id=searcher.result_parent.children[i-1].movement_tree_id;
			}
			searcher.result_parent.children=tmp;
			
			if(manager.buffer_movement==null){
				manager.buffer_movement=new movement_tree[1];
				manager.buffer_movement[0]=searcher.result;
			}else{
				movement_tree buffer_tmp[]=new movement_tree[manager.buffer_movement.length+1];
				for(int i=0;i<(manager.buffer_movement.length);i++)
					buffer_tmp[i]=manager.buffer_movement[i];
				buffer_tmp[manager.buffer_movement.length]=searcher.result;
				manager.buffer_movement=buffer_tmp;
			}
		}
		add_component(searcher.result);
		for(int i=0;i<(all_components.component_number);i++)
			all_components.comp[i].modify_display_flag(
					manager.move_channel_id.all_parameter_channel_id,true,ek.component_cont);
		
		
		manager.movement_start(
				ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
				current_movement_id,ek.component_cont,true,switch_time_length);

		return searcher.result_parent.movement_tree_id;
	}
	private int frombuffer()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if((searcher.result==null)||(searcher.result_parent==null))
			return -1;
		if(manager.buffer_movement!=null){
			movement_tree t=null,tmp[]=new movement_tree[searcher.result_parent.children.length+1];
			for(int i=0,j=0,n=searcher.result_parent.children.length;i<n;i++){
				if(searcher.result_id==i){
					if(manager.buffer_movement!=null){
						t=new movement_tree();
						t.children=manager.buffer_movement;
						manager.buffer_movement=null;
						tmp[j++]=t;
					}
				}
				tmp[j++]=searcher.result_parent.children[i];
			}
			searcher.result_parent.children=tmp;
			manager.reset(ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
					ek.component_cont,switch_time_length);
			manager.movement_start(ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
					((t!=null)?t:searcher.result).movement_tree_id,ek.component_cont,true,switch_time_length);
			manager.last_edit_move_id=-1;
		}
		return searcher.result_parent.movement_tree_id;
	}
	private int fromchild()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if((searcher.result==null)||(searcher.result_parent==null))
			return -1;
		if(searcher.result.children!=null){
			movement_tree tmp[]=new movement_tree[searcher.result_parent.children.length+searcher.result.children.length-1];
			int j=0;
			for(int i=0;i<(searcher.result_parent.children.length);i++)
				if(searcher.result_id!=i)
					tmp[j++]=searcher.result_parent.children[i];
				else
					for(int k=0;k<(searcher.result.children.length);k++)
						tmp[j++]=searcher.result.children[k];
			searcher.result_parent.children=tmp;
			manager.reset(ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
					ek.component_cont,switch_time_length);
			manager.movement_start(ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
					searcher.result.children[0].movement_tree_id,ek.component_cont,true,switch_time_length);
			
		}
		return searcher.result_parent.movement_tree_id;
	}
	private void tag()
	{
		String language,movement_id,windows_str;
		if(manager.root_movement==null)
			return;
		if((movement_id=ci.request_response.get_parameter("id"))==null)
			return;
		if((language=ci.request_response.get_parameter("language"))==null)
			language="english";
		if((windows_str=ci.request_response.get_parameter("windows"))==null)
			windows_str="parent";
		ci.request_response.implementor.redirect_url(
				language.trim()+"/tag.jsp?movement_id="+movement_id.trim()
					+"&component_id="+manager.config_parameter.component_id
					+"&windows="+windows_str,
				ek.scene_par.scene_cors_string);
	}
	private void do_get()
	{
		String str;
		if(manager.root_movement==null)
			return ;
		if((str=ci.request_response.get_parameter("id"))==null)
			return;
		
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if(searcher.result_parent==null)
			manager.last_edit_move_id=searcher.result.movement_tree_id;
		else
			manager.last_edit_move_id=searcher.result_parent.movement_tree_id;
		
		String node_name="no_node_name",sound_file_name="no_sound_file",description="no_description";
		if(searcher.result!=null){
			if(searcher.result.node_name!=null)
				if(searcher.result.node_name.trim().length()>0)
					node_name=searcher.result.node_name.trim().replace('\\','/').replace("\"","");
			if(searcher.result.sound_file_name!=null)
				if(searcher.result.sound_file_name.trim().length()>0)
					sound_file_name=searcher.result.sound_file_name.trim().replace('\\','/').replace("\"","");
			if(searcher.result.description!=null)
				if(searcher.result.description.trim().length()>0)
					description=searcher.result.description.trim().replace('\\','/').replace("\"","");
		}
		
		ci.request_response.println("{");
		ci.request_response.println("	\"node_name\"		:	\"",node_name+"\",");
		ci.request_response.println("	\"sound_file_name\"	:	\"",sound_file_name+"\",");
		ci.request_response.println("	\"description\"		:	\"",description+"\",");
		ci.request_response.println("	\"id\"				:	",searcher.result.movement_tree_id);
		ci.request_response.println("}");

		return;
	}
	private void do_update_children(movement_tree t,
			boolean update_sound_file_name_flag,boolean update_description_flag)
	{
		if(t.children==null)
			return;
		for(int i=0;i<(t.children.length);i++){
			if(update_sound_file_name_flag)
				t.children[i].sound_file_name=t.sound_file_name;
			if(update_description_flag)
				t.children[i].description=t.description;
			do_update_children(t.children[i],update_sound_file_name_flag,update_description_flag);
		}
	}
	private void do_update()
	{
		String str;
	
		if(manager.root_movement==null)
			return ;
		if((str=ci.request_response.get_parameter("id"))==null)
			return;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if(searcher.result==null)
			return;
		if((str=ci.request_response.get_parameter("node_name"))!=null) {
			try{
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				searcher.result.node_name=str;
			}catch(Exception e){
				return;
			}
		}
		if((str=ci.request_response.get_parameter("sound_file_name"))!=null) {
			try{
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				searcher.result.sound_file_name=str;
				do_update_children(searcher.result,true,false);
			}catch(Exception e){
				return;
			}
		}
		if((str=ci.request_response.get_parameter("description"))!=null) {
			try{
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
				searcher.result.description=str;
				do_update_children(searcher.result,false,true);
			}catch(Exception e){
				return;
			}
		}
	}
	
	private int do_follow()
	{
		String str;
		
		if((manager.root_movement==null)||(ek.component_cont==null))
			return -1;
		if(ek.component_cont.root_component==null)
			return -1;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if((searcher.result==null))
			return -1;
		add_component(searcher.result);
		for(int i=0,n=all_components.component_number;i<n;i++)
			all_components.comp[i].modify_display_flag(
					manager.move_channel_id.all_parameter_channel_id,true,ek.component_cont);
		
		component main_comp;
		if((main_comp=ek.component_cont.get_component(searcher.result.move.moved_component_id))!=null){
			component_array comp_array=new component_array(ek.component_cont.root_component.component_id+1);
			comp_array.add_selected_component(ek.component_cont.root_component);
			for(int i=0;i<comp_array.component_number;i++){
				component comp=ek.component_cont.get_component(main_comp.parent_component_id);
				for(;comp!=null;comp=ek.component_cont.get_component(comp.parent_component_id))
					if(comp.component_id==comp_array.comp[i].component_id){
						comp_array.expand(i--);
						break;
					}
			}
			for(int i=0;i<comp_array.component_number;i++) {
				component comp=comp_array.comp[i];
				for(;comp!=null;comp=ek.component_cont.get_component(comp.parent_component_id))
					if(main_comp.component_id==comp.component_id){
						comp_array.delete(i--);
						break;
					}
			}
			if(comp_array.component_number<=0){
				searcher.result.move.follow_component_name		=null;
				searcher.result.move.follow_component_id		=null;
				searcher.result.move.follow_component_location	=null;
			}else{
				searcher.result.move.follow_component_name		=new String[comp_array.component_number];
				searcher.result.move.follow_component_id		=new int[comp_array.component_number];
				searcher.result.move.follow_component_location	=new location[comp_array.component_number];
				
				location main_negative_loca=main_comp.absolute_location.negative();
				for(int i=0,ni=comp_array.component_number;i<ni;i++){
					location loca=main_negative_loca.multiply(comp_array.comp[i].absolute_location);
					searcher.result.move.follow_component_name[i]		=comp_array.comp[i].component_name;
					searcher.result.move.follow_component_id[i]			=comp_array.comp[i].component_id;
					searcher.result.move.follow_component_location[i]	=loca;
				}
			}
		}
		manager.movement_start(ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
				searcher.result.movement_tree_id,ek.component_cont,true,switch_time_length);
		return ((searcher.result_parent==null)?manager.root_movement:searcher.result_parent).movement_tree_id;
	}
	private boolean test_can_not_todesignbuffer(component comp)
	{
		if(comp.children==null)
			return false;
		String str=comp.component_name;
		for(int i=0,ni=comp.children.length,length=str.length();i<ni;i++){
			String cstr=comp.children[i].component_name;
			if(cstr.length()<length)
				return true;
			if(cstr.substring(0,length).compareTo(str)!=0)
				return true;
		}
		for(int i=0,ni=comp.children.length;i<ni;i++)
			if(test_can_not_todesignbuffer(comp.children[i]))
				return true;
		return false;
	}
	private boolean test_movement_tree(component comp,movement_tree t)
	{
		component move_comp;
		
		if(t.children!=null)
			if(t.children.length>0){
				for(int i=0,ni=t.children.length;i<ni;i++)
					if(test_movement_tree(comp,t.children[i]))
						return true;
				return false;
			}
		if(t.move==null)
			return true;
		if((move_comp=ek.component_cont.get_component(t.move.moved_component_id))==null)
			return true;
		int name_length=comp.component_name.length();
		if(move_comp.component_name.length()<name_length)
			return true;
		if(move_comp.component_name.substring(0,name_length).compareTo(comp.component_name)!=0)
			return true;
		for(;move_comp!=null;move_comp=ek.component_cont.get_component(move_comp.parent_component_id))
			if(move_comp.component_id==comp.component_id)
				break;
		if(move_comp==null)
			return true;
		if(t.move.follow_component_id!=null)
			for(int i=0,ni=t.move.follow_component_id.length;i<ni;i++){
				if((move_comp=ek.component_cont.get_component(t.move.follow_component_id[i]))==null)
					return true;
				if(move_comp.component_name.length()<name_length)
					return true;
				if(move_comp.component_name.substring(0,name_length).compareTo(comp.component_name)!=0)
					return true;
				for(;move_comp!=null;move_comp=ek.component_cont.get_component(move_comp.parent_component_id))
					if(move_comp.component_id==comp.component_id)
						break;
				if(move_comp==null)
					return true;
			}
		return false;
	}
	
	private String todesignbuffer()
	{
		String str;
		component comp;
		movement_tree t;
		
		if(ek.component_cont.root_component==null)
			return "no_root_component";
		if((comp=ek.component_cont.search_component())==null)
			return "no_selected_component";
		if(!(comp.uniparameter.selected_flag))
			return "selected_component_not_selected";
		if(ek.component_cont.get_component(comp.parent_component_id)==null)
			return "selected_component_has_no_parent";
		if(test_can_not_todesignbuffer(comp))
			return "selected_component_error";
		
		if(manager.root_movement==null)
			return "no_movement";
		if((str=ci.request_response.get_parameter("id"))==null)
			return "no_id";
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if((t=searcher.result)==null)
			return "no_search_result";
		if(test_movement_tree(comp,t))
			return "selected_component_is_not_parent_of_movement_component";

		file_writer f=new file_writer(
				manager.config_parameter.design_file_name+".append",
				manager.config_parameter.movement_file_charset);
		f.println();
		f.println(t.node_name);
		f.println(comp.part_name);
		f.println(comp.component_name);
		searcher.result.flush(f,0,false,0);
		f.println();
		f.close();

		file_writer.file_rename(
				manager.config_parameter.design_file_name,
				manager.config_parameter.design_file_name+".bak");
		file_writer.merge_file(
				new String[]{
						manager.config_parameter.design_file_name+".append",
						manager.config_parameter.design_file_name+".bak"
				},manager.config_parameter.design_file_name);
		file_writer.file_delete(manager.config_parameter.design_file_name+".bak");
		file_writer.file_delete(manager.config_parameter.design_file_name+".append");
		
		return "ok";
	}
	
	private void deletedesignbuffer(int id)
	{
		int i,m,n;
		
		file_writer.file_rename(manager.config_parameter.design_file_name, manager.config_parameter.design_file_name+".bak");
		file_reader f=new file_reader(
				manager.config_parameter.design_file_name+".bak",
				manager.config_parameter.movement_file_charset);
		file_writer fw=new file_writer(
				manager.config_parameter.design_file_name,
				manager.config_parameter.movement_file_charset);
		
		for(i=0,m=0,n=0;;i++){
			String node_name=f.get_string();
			String part_name=f.get_string();
			String component_name=f.get_string();
			if(f.eof()||(node_name==null)||(part_name==null))
				break;
			movement_tree t=new movement_tree(f);

			if(i==id)
				m++;
			else{
				n++;
				fw.set_pace(0);
				fw.println();
				fw.println(node_name);
				fw.println(part_name);
				fw.println(component_name);
				t.flush(fw,0,false,0);
				fw.println();
			}
		}
		f.close();	
		f=null;
		fw.close();	
		fw=null;
		file_writer.file_delete(manager.config_parameter.design_file_name+".bak");
		
				
		web_page wp=new web_page(ci,manager.config_parameter.language_change_name,"");

		wp.info =wp.language_change("delete");
		wp.info+=" "+Integer.toString(m);
		wp.info+=" "+wp.language_change("records");
		wp.info+=","+wp.language_change("remain");
		wp.info+=" "+Integer.toString(n);
		wp.info+=" "+wp.language_change("records");

		wp.create_web_page();
	}
	private void fromdesignbuffer()
	{
		String str;
		if((str=ci.request_response.get_parameter("component"))==null)
			return;
		component comp=ek.component_cont.get_component(Integer.decode(str));
		if(comp==null)
			return;
		
		int buffer_id;
		if((str=ci.request_response.get_parameter("buffer"))==null)
			return;
		buffer_id=Integer.decode(str);
	
		file_reader f=new file_reader(
				manager.config_parameter.design_file_name,
				manager.config_parameter.movement_file_charset);
		
		for(int i=0;;i++){
			f.get_string();
			String part_name=f.get_string();
			String component_name=f.get_string();
			if(f.eof()||(component_name==null)||(part_name==null))
				break;
			movement_tree t=new movement_tree(f);
			if(i!=buffer_id)
				continue;
			if(comp.part_name.compareTo(part_name)!=0)
				break;
			if(t.modify_component_name(ek.component_cont,component_name,comp.component_name)){
				if(manager.root_movement==null)
					manager.root_movement=new movement_tree();
				if(manager.root_movement.children==null){
					manager.root_movement.children=new movement_tree[1];
					manager.root_movement.children[0]=t;
				}else{
					movement_tree tmp[]=new movement_tree[manager.root_movement.children.length+1];
					if(manager.mount_direction_flag){
						for(int j=0;j<(manager.root_movement.children.length);j++)
							tmp[j+1]=manager.root_movement.children[j];
						tmp[0]=t;
					}else{
						for(int j=0;j<(manager.root_movement.children.length);j++)
							tmp[j]=manager.root_movement.children[j];
						tmp[manager.root_movement.children.length]=t;
					}
					manager.root_movement.children=tmp;
				}
				manager.root_movement.set_tree_node_id(0);
				manager.movement_start(
						ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
						t.movement_tree_id,ek.component_cont,true,switch_time_length);
			}
			break;
		}
		f.close();
		return;
	}
	private void clear_view_direction(movement_tree t)
	{
		t.direction=null;
		if(t.children!=null)
			for(int i=0,n=t.children.length;i<n;i++)
				clear_view_direction(t.children[i]);
	}
	private int view_direction()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if((searcher.result==null)||(searcher.result_parent==null))
			return -1;
		if(searcher.result.direction!=null)
			searcher.result.direction=null;
		else{
			clear_view_direction(searcher.result);
			searcher.result.direction=new location(ci.display_camera_result.cam.eye_component.absolute_location);
		}
		return searcher.result_parent.movement_tree_id;
	}
	private int view_box()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if(searcher.result!=null)		
			switch(searcher.result.scale_type){
			case 1://????????????
				searcher.result.scale_type=2;
				break;
			case 2://????????????   
				searcher.result.scale_type=3;
				break;
			case 3://????????????????  
				searcher.result.scale_type=0;
				break;		
			case 0://????????????
			default:
				searcher.result.scale_type=1;
				break;
			}
		return searcher.result_parent.movement_tree_id;
	}
	private void do_select_component(movement_tree t,component_selection cs)
	{
		component moved_component;
		if(t.children!=null)
			for(int i=0,ni=t.children.length;i<ni;i++)
					do_select_component(t.children[i],cs);
		if(t.move!=null)
			if((moved_component=ek.component_cont.get_component(t.move.moved_component_id))!=null){
				cs.set_selected_flag(moved_component,ek.component_cont);
				if(t.move.follow_component_id!=null)
					for(int i=0,ni=t.move.follow_component_id.length;i<ni;i++)
						if((moved_component=ek.component_cont.get_component(t.move.follow_component_id[i]))!=null)
							cs.set_selected_flag(moved_component,ek.component_cont);
			}
	}
	private int select_component()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Integer.decode(str));
		if(searcher.result!=null){
			component_selection cs=new component_selection(ek);
			do_select_component(searcher.result,cs);
			add_component(searcher.result);
			driver_audio.extended_component_driver acd;
			if((acd=manager.config_parameter.get_audio_component_driver(ek))!=null)
				acd.set_audio(null);
			component_collector collector=ek.collector_stack.push_component_array(true,
					ek.system_par,ek.scene_par,all_components,ek.component_cont,ek.render_cont.renders);
			if(collector!=null){
				collector.title=searcher.result.node_name;
				collector.description=searcher.result.description;
				collector.audio_file_name=manager.directory_name+searcher.result.sound_file_name;
				if(acd!=null)
					acd.set_audio(collector.audio_file_name);
			}
		}
		return ((searcher.result_parent!=null)?searcher.result_parent:manager.root_movement).movement_tree_id;
	}
	private void reset_movement_component(movement_tree t)
	{
		if(t.children!=null){
			for(int i=0;i<(t.children.length);i++)
				reset_movement_component(t.children[i]);
			return;
		}
		if(t.move==null)
			return;
		component moved_component;
		if((moved_component=ek.component_cont.get_component(t.move.moved_component_id))!=null){
			moved_component.modify_location(new location(),ek.component_cont);
			moved_component.modify_display_flag(
					manager.move_channel_id.all_parameter_channel_id,true,ek.component_cont);
		}
		return;
	}
	private void reload()
	{
		if(manager!=null){
			debug_information.println();
			debug_information.println("????????????????        "+(manager.config_parameter.movement_file_name));
			if(manager.root_movement!=null)
				reset_movement_component(manager.root_movement);	
			manager.init(ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
					ek.component_cont,manager.config_parameter.movement_file_name,
					switch_time_length,manager.config_parameter.movement_file_charset);
			debug_information.println("????????????????        "+(manager.config_parameter.movement_file_name));
		}
	}
	private void push()
	{
		manager.push_movement(ek.component_cont,
				ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
				switch_time_length,manager.config_parameter.movement_file_charset);
	}
	
	private String[] design_request_dispatch()
	{
		String str;
		
		if(ek.component_cont.root_component==null)
			return null;
		if((str=ci.request_response.get_parameter("move_method"))==null)
			return null;
		switch(str.trim().toLowerCase()){
		case "edit":
			edit_move();
			return null;
		case "jason":
			movement_jason.create_jason_with_current_movement_tree_id(
				manager.last_edit_move_id,manager.root_movement,ci.request_response);
			return null;
		case "set_location":
			(new movement_desinger(ek,ci,manager)).set_location();
			return null;
		case "get_location":
			(new movement_desinger(ek,ci,manager)).get_location();
			return null;
		case "add_point":
			(new movement_desinger(ek,ci,manager)).add_point();
			return null;
		case "delete_point":
			(new movement_desinger(ek,ci,manager)).delete_point();
			return null;
		case "add_movement":
			(new movement_desinger(ek,ci,manager)).add_movement();
			return null;
		case "delete":
			push();
			ci.request_response.println(delete_move());
			return null;
		case "up":
			push();
			ci.request_response.println(updown_move(manager.mount_direction_flag?true:false));
			return null;
		case "down":
			push();
			ci.request_response.println(updown_move(manager.mount_direction_flag?false:true));
			return null;
		case "reverse":
			push();
			ci.request_response.println(reverse_move());
			return null;
		case "fromchild":
			push();
			ci.request_response.println(fromchild());
			return null;
		case "frombuffer":
			push();
			ci.request_response.println(frombuffer());
			return null;
		case "tobuffer":
			push();
			ci.request_response.println(tobuffer());
			return null;
		case "follow":
			push();
			ci.request_response.println(do_follow());
		case "view_direction":
			push();
			ci.request_response.println(view_direction());	
			return null;	
		case "todesignbuffer":			
			ci.request_response.println("\"",todesignbuffer()+"\"");
			return null;
		case "view_box":
			push();
			ci.request_response.println(view_box());
			return null;
		case "locate_camera":
			ci.request_response.println(locate_camera());
			return null;
		case "select_component":
			ci.request_response.println(select_component());
			return null;
		case "sequence":
			push();
			ci.request_response.println(set_sequence_flag(true));
			return null;
		case "synchronization":
			push();
			ci.request_response.println(set_sequence_flag(false));
			return null;
		case "tag":
			push();
			tag();
			return null;
		case "get":
			do_get();
			return null;
		case "update":
			do_update();
			return null;
		case "listdesignbuffer":
			if(ci.parameter.comp!=null)
				(new movement_buffer_list(ek,ci,manager)).create_web_page();
			return null;
		case "fromdesignbuffer":
			push();
			fromdesignbuffer();
			if(manager.root_movement!=null)
				new movement_edit(ek,ci,manager,manager.root_movement.movement_tree_id);
			return null;
		case "deletedesignbuffer":
			if((str=ci.request_response.get_parameter("buffer"))!=null)
				deletedesignbuffer(Integer.decode(str));
			return null;
		case "save":
			file_writer f=new file_writer(
					manager.config_parameter.movement_file_name,
					manager.config_parameter.movement_file_charset);
			manager.flush(ek.component_cont,
					ek.modifier_cont[manager.move_channel_id.movement_modifier_id],f,switch_time_length);
			f.close();
			return null;
		case "reload":
			push();
			reload();
			return null;
		case "retreat":
			manager.pop_movement(ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
				ek.component_cont,switch_time_length,manager.config_parameter.movement_file_charset);
			return null;
		case "extract":
			(new movement_buffer_list(ek,ci,manager)).create_web_page();
			return null;
		default:
			return null;
		}
	}
	private void update_component_location(component comp)
	{
		comp.uniparameter.do_response_location_flag=true;
		comp.update_location_version();
		for(int i=0,ni=comp.children_number();i<ni;i++)
			update_component_location(comp.children[i]);
	}
	private String[]  movement_request_dispatch()
	{
		String str;
				
		driver_audio.extended_component_driver acd;
		if((acd=manager.config_parameter.get_audio_component_driver(ek))!=null)
			acd.set_audio(null);
		if(manager==null)
			return null;
		if(ek.camera_cont.camera_array==null)
			return null;
		if((str=ci.request_response.get_parameter("move_method"))==null)
			return null;
		switch(str){
		case "stop":
			ek.modifier_cont[manager.move_channel_id.movement_modifier_id].clear_modifier(ek,ci);
			update_component_location(ek.component_cont.root_component);
			break;
		case "continue":
			boolean direction_flag=true;
			if((str=ci.request_response.get_parameter("direct"))!=null)
				if(str.compareTo("backward")==0)
					direction_flag=false;
			ek.modifier_cont[manager.move_channel_id.movement_modifier_id].clear_modifier(ek,ci);
			manager.create_render_modifier(
					manager.move_channel_id.movement_modifier_id,
					manager.config_parameter.audio_component_id,
					manager.config_parameter.location_component_id,
					ek.modifier_cont[manager.move_channel_id.movement_modifier_id],
					ek.component_cont,direction_flag,switch_time_length,
					manager.config_parameter.sound_pre_string);
			break;
		case "search":
			if(ci.request_response==null)
				break;
			movement_search ms=new movement_search(manager.config_parameter.component_id,
				ek.component_cont.search_component(),ek.component_cont,manager.root_movement,
				ci,manager.config_parameter.language_change_name);
			ms.create_web_page();
			break;
		default:
			break;
		}
		return null;
	}
	public String[] get_engine_result()
	{
		String str;
				
		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;
		
		switch(str) {
		case "movement":
			return movement_request_dispatch();
		case "design":
			return design_request_dispatch();
		case "parameter":
			if((str=ci.request_response.get_parameter("parameter"))==null)
				return null;
			switch(str.trim().toLowerCase()){
			case "set_precision":
				if((str=ci.request_response.get_parameter("value"))!=null){
					double precision=Double.parseDouble(str);
					manager.parameter.movement_precision=(precision<1.0)?1.0:precision;
				}
				break;
			}
			return null;
		default:
			return null;
		}
	}
	
	public movement_function_switch(engine_kernel my_ek,client_information my_ci,movement_manager my_manager)
	{
		ek=my_ek;
		ci=my_ci;
		
		manager=my_manager;
		target_direction=null;
		all_components=new  component_array(ek.component_cont.root_component.component_id+1);
	
		camera_parameter cam_par=ci.display_camera_result.cam.parameter;
		switch_time_length=cam_par.movement_flag?cam_par.switch_time_length:0;
	}
}
