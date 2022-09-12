package driver_movement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import kernel_camera.camera_parameter;
import kernel_camera.locate_camera;
import kernel_common_class.debug_information;
import kernel_common_class.jason_string;
import kernel_common_class.common_reader;
import kernel_common_class.upload_web_page;
import kernel_common_class.change_name;
import kernel_common_class.const_value;
import kernel_component.component;
import kernel_component.component_array;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_transformation.location;
import kernel_component.component_collector;
import kernel_driver.modifier_container;
import driver_location_modifier.extended_component_driver;

public class movement_function_switch
{
	private engine_kernel ek;
	private client_information ci;
	private movement_manager manager;
	
	private location target_direction[];
	private component_array all_components;
	private modifier_container modifier_cont;
	
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
	private long locate_camera()
	{
		String str;
		long current_movement_id;
		
		if(manager.root_movement==null)
			return -1;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		if((current_movement_id=Long.decode(str))<0)
			return -1;
	
		movement_searcher searcher=new movement_searcher(manager.root_movement,current_movement_id);
		if(searcher.search_link_list==null)
			return -1;
		if(searcher.search_link_list.next!=null){
			manager.movement_start(modifier_cont,current_movement_id,ek.component_cont,true,switch_time_length);
			add_component(searcher.search_link_list.tree_node);
			if((all_components.get_box()!=null)&&(ci.display_camera_result.cam.parameter.movement_flag))
				(new locate_camera(ci.display_camera_result.cam)).locate_on_components(	modifier_cont,all_components.get_box(),
						ci.display_camera_result.cam.parameter.direction_flag?location.combine_location(target_direction):null,
						ci.display_camera_result.cam.parameter.scale_value,true,true,false);
		}
		return searcher.search_link_list.tree_node.movement_tree_id;
	}
	private long delete_move()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if((searcher.search_link_list==null)||(searcher.can_delete_list==null))
			return -1;

		add_component(searcher.can_delete_list.tree_node);
		for(int i=0,n=all_components.component_number;i<n;i++)
			all_components.comp[i].modify_display_flag(
					manager.move_channel_id.all_parameter_channel_id,true,ek.component_cont);
		
		int index_id=searcher.can_delete_list.next.tree_node.children.length-1;
		if(searcher.can_delete_list.index_id!=(index_id--))
			index_id=searcher.can_delete_list.index_id+1;
		long current_movement_id=searcher.can_delete_list.next.tree_node.children[index_id].movement_tree_id;
		
		movement_tree tmp[]=new movement_tree[searcher.can_delete_list.next.tree_node.children.length-1];
		for(int i=0,j=0,ni=searcher.can_delete_list.next.tree_node.children.length;i<ni;i++)
			if(searcher.can_delete_list.index_id!=i)
				tmp[j++]=searcher.can_delete_list.next.tree_node.children[i];
		searcher.can_delete_list.next.tree_node.children=tmp;
		manager.movement_start(modifier_cont,current_movement_id,ek.component_cont,true,switch_time_length);
		return current_movement_id;
	}
	private long reverse_move()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return manager.root_movement.movement_tree_id;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)
			return manager.root_movement.movement_tree_id;
		add_component(searcher.search_link_list.tree_node);
		for(int i=0,n=all_components.component_number;i<n;i++)
			all_components.comp[i].modify_display_flag(
					manager.move_channel_id.all_parameter_channel_id,true,ek.component_cont);

		searcher.search_link_list.tree_node.reverse();
		manager.movement_start(modifier_cont,
				searcher.search_link_list.tree_node.movement_tree_id,ek.component_cont,true,switch_time_length);
		
		return searcher.search_link_list.tree_node.movement_tree_id;
	}
	
	private long updown_move(boolean updown_flag)
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return manager.root_movement.movement_tree_id;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)
			return manager.root_movement.movement_tree_id;
		if(searcher.search_link_list.next==null)
			return manager.root_movement.movement_tree_id;
				
		if(updown_flag){
			if(searcher.search_link_list.index_id>0){
				searcher.search_link_list.next.tree_node.children[searcher.search_link_list.index_id  ]=searcher.search_link_list.next.tree_node.children[searcher.search_link_list.index_id-1];
				searcher.search_link_list.next.tree_node.children[searcher.search_link_list.index_id-1]=searcher.search_link_list.tree_node;
				searcher.search_link_list.index_id--;
			}
		}else{
			if(searcher.search_link_list.index_id<(searcher.search_link_list.next.tree_node.children.length-1)){
				searcher.search_link_list.next.tree_node.children[searcher.search_link_list.index_id  ]=searcher.search_link_list.next.tree_node.children[searcher.search_link_list.index_id+1];
				searcher.search_link_list.next.tree_node.children[searcher.search_link_list.index_id+1]=searcher.search_link_list.tree_node;
				searcher.search_link_list.index_id++;
			}
		}
		add_component(searcher.search_link_list.tree_node);
		for(int i=0;i<(all_components.component_number);i++)
			all_components.comp[i].modify_display_flag(
					manager.move_channel_id.all_parameter_channel_id,true,ek.component_cont);
		
		manager.movement_start(modifier_cont,
				searcher.search_link_list.tree_node.movement_tree_id,
				ek.component_cont,true,switch_time_length);
		
		return searcher.search_link_list.next.tree_node.movement_tree_id;
	}
	private long set_sequence_flag(boolean new_sequence_flag)
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return manager.root_movement.movement_tree_id;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)
			return manager.root_movement.movement_tree_id;
		searcher.search_link_list.tree_node.sequence_flag=new_sequence_flag;
		
		manager.movement_start(modifier_cont,
				searcher.search_link_list.tree_node.movement_tree_id,ek.component_cont,true,switch_time_length);
		
		return searcher.search_link_list.tree_node.movement_tree_id;
	}
	private long tobuffer()
	{
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)
			return -1;
		if(searcher.can_delete_list==null)
			return searcher.search_link_list.tree_node.movement_tree_id;
		
		add_component(searcher.can_delete_list.tree_node);
		for(int i=0,ni=all_components.component_number;i<ni;i++)
			all_components.comp[i].modify_display_flag(
					manager.move_channel_id.all_parameter_channel_id,true,ek.component_cont);
			
		int index_id=searcher.can_delete_list.next.tree_node.children.length-1;
		if(searcher.can_delete_list.index_id!=(index_id--))
			index_id=searcher.can_delete_list.index_id+1;
		long long_movement_tree_id=searcher.can_delete_list.next.tree_node.children[index_id].movement_tree_id;
		
		movement_tree tmp[]=new movement_tree[searcher.can_delete_list.next.tree_node.children.length-1];
		for(int i=0,j=0,ni=searcher.can_delete_list.next.tree_node.children.length;i<ni;i++)
			if(searcher.can_delete_list.index_id!=i)
				tmp[j++]=searcher.can_delete_list.next.tree_node.children[i];
		searcher.can_delete_list.next.tree_node.children=tmp;
		
		if(manager.buffer_movement==null){
			manager.buffer_movement=new movement_tree[1];
			manager.buffer_movement[0]=searcher.search_link_list.tree_node;
		}else{
			movement_tree buffer_tmp[]=new movement_tree[manager.buffer_movement.length+1];
			for(int i=0,ni=manager.buffer_movement.length;i<ni;i++)
				buffer_tmp[i]=manager.buffer_movement[i];
			buffer_tmp[manager.buffer_movement.length]=searcher.search_link_list.tree_node;
			manager.buffer_movement=buffer_tmp;
		}
		manager.movement_start(modifier_cont,long_movement_tree_id,ek.component_cont,true,switch_time_length);
		return long_movement_tree_id;
	}
	private long frombuffer()
	{
		if(manager.buffer_movement==null)
			return -1;
		long current_movement_tree_id;
		if(manager.root_movement==null) {
			manager.root_movement=new movement_tree(manager.id_creator);
			manager.root_movement.children=manager.buffer_movement;
			manager.buffer_movement=null;
			current_movement_tree_id=manager.root_movement.movement_tree_id;
		}else{
			String str;
			current_movement_tree_id=manager.root_movement.movement_tree_id;
			if((str=ci.request_response.get_parameter("id"))==null)
				return current_movement_tree_id;
			movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
			if(searcher.search_link_list==null)
				return current_movement_tree_id;
			if(searcher.search_link_list.next==null)
				return current_movement_tree_id;
			movement_tree tmp[]=new movement_tree[searcher.search_link_list.next.tree_node.children.length+1];
			for(int i=0,j=0,n=searcher.search_link_list.next.tree_node.children.length;i<n;i++){
				if(searcher.search_link_list.index_id==i){
					tmp[j]=new movement_tree(manager.id_creator);
					tmp[j].children=manager.buffer_movement;
					manager.buffer_movement=null;
					current_movement_tree_id=tmp[j++].movement_tree_id;
				}
				tmp[j++]=searcher.search_link_list.next.tree_node.children[i];
			}
			searcher.search_link_list.next.tree_node.children=tmp;
			manager.reset(current_movement_tree_id,modifier_cont,ek.component_cont,switch_time_length);
		}
		return current_movement_tree_id;
	}
	private long fromchild()
	{
		if(manager.root_movement==null)
			return manager.root_movement.movement_tree_id;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if((searcher.search_link_list==null)||(searcher.search_link_list.next==null))
			return -1;
		if(searcher.search_link_list.tree_node.children==null)
			return searcher.search_link_list.tree_node.movement_tree_id;
		movement_tree tmp[]=new movement_tree[
		      searcher.search_link_list.next.tree_node.children.length
		     +searcher.search_link_list.tree_node.children.length-1];
		for(int i=0,j=0,ni=searcher.search_link_list.next.tree_node.children.length;i<ni;i++)
			if(searcher.search_link_list.index_id!=i)
		       tmp[j++]=searcher.search_link_list.next.tree_node.children[i];
		    else
		       	for(int k=0,kn=searcher.search_link_list.tree_node.children.length;k<kn;k++)
		             tmp[j++]=searcher.search_link_list.tree_node.children[k];
		searcher.search_link_list.next.tree_node.children=tmp;
		manager.reset(searcher.search_link_list.next.tree_node.movement_tree_id,
		              modifier_cont,ek.component_cont,switch_time_length);
		return searcher.search_link_list.next.tree_node.movement_tree_id;
	}
	private void do_update_children(movement_tree t,
			boolean update_sound_file_name_flag,boolean update_description_flag)
	{
		if(t.children==null)
			return;
		for(int i=0,ni=t.children.length;i<ni;i++){
			if(update_sound_file_name_flag)
				t.children[i].sound_file_name=t.sound_file_name;
			if(update_description_flag)
				t.children[i].description=t.description;
			do_update_children(t.children[i],update_sound_file_name_flag,update_description_flag);
		}
	}
	
	private long do_update()
	{
		String str;
	
		if(manager.root_movement==null)
			return -1;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)
			return -1;
		
		boolean recursive_flag=true;
		if((str=ci.request_response.get_parameter("recursive"))==null)
			recursive_flag=true;
		else
			switch(str.toLowerCase()) {
			default:
			case "false":
			case "no":
				recursive_flag=true;
				break;
			case "true":
			case "yes":
				recursive_flag=true;
				break;
			}
		String request_charset=ci.request_response.implementor.get_request_charset();
		if((str=ci.request_response.get_parameter("node_name"))!=null)
			if((str=str.trim()).length()>0){
				try{
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
					searcher.search_link_list.tree_node.node_name=str;
				}catch(Exception e){
					;
				}
			}
		if((str=ci.request_response.get_parameter("sound_file_name"))!=null)
			if((str=str.trim()).length()>0){
				try{
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
					str=str.replace(" ","").replace("\t","").replace("\n","").
							replace("\r","").replace( "\"","").replace("\\","/");
					searcher.search_link_list.tree_node.sound_file_name=file_reader.separator(str);
					if(recursive_flag)
						do_update_children(searcher.search_link_list.tree_node,true,false);
				}catch(Exception e){
					;
				}
			}
		if((str=ci.request_response.get_parameter("description"))!=null) 
			if((str=str.trim()).length()>0){
				try{
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
					str=str.replace(" ","").replace("\t","").replace("\n","").
							replace("\r","").replace("\"","").replace("\\","/");
					searcher.search_link_list.tree_node.description=str;
					if(recursive_flag)
						do_update_children(searcher.search_link_list.tree_node,false,true);
				}catch(Exception e){
					;
				}
			}
		return searcher.search_link_list.tree_node.movement_tree_id;
	}
	private long do_follow()
	{
		String str;
		
		if((manager.root_movement==null)||(ek.component_cont==null))
			return -1;
		if(ek.component_cont.root_component==null)
			return manager.root_movement.movement_tree_id;
		if((str=ci.request_response.get_parameter("id"))==null)
			return manager.root_movement.movement_tree_id;;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if((searcher.search_link_list==null))
			return manager.root_movement.movement_tree_id;;
		add_component(searcher.search_link_list.tree_node);
		for(int i=0,n=all_components.component_number;i<n;i++)
			all_components.comp[i].modify_display_flag(
					manager.move_channel_id.all_parameter_channel_id,true,ek.component_cont);
		
		component main_comp;
		if((main_comp=ek.component_cont.get_component(searcher.search_link_list.tree_node.move.moved_component_id))!=null){
			component_array comp_array=new component_array(ek.component_cont.root_component.component_id+1);
			comp_array.add_selected_component(ek.component_cont.root_component,false);
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
				searcher.search_link_list.tree_node.move.follow_component_name		=null;
				searcher.search_link_list.tree_node.move.follow_component_id		=null;
				searcher.search_link_list.tree_node.move.follow_component_location	=null;
			}else{
				searcher.search_link_list.tree_node.move.follow_component_name		=new String[comp_array.component_number];
				searcher.search_link_list.tree_node.move.follow_component_id		=new int[comp_array.component_number];
				searcher.search_link_list.tree_node.move.follow_component_location	=new location[comp_array.component_number];
				
				location main_negative_loca=main_comp.caculate_negative_absolute_location();
				for(int i=0,ni=comp_array.component_number;i<ni;i++){
					location loca=main_negative_loca.multiply(comp_array.comp[i].absolute_location);
					searcher.search_link_list.tree_node.move.follow_component_name[i]		=comp_array.comp[i].component_name;
					searcher.search_link_list.tree_node.move.follow_component_id[i]			=comp_array.comp[i].component_id;
					searcher.search_link_list.tree_node.move.follow_component_location[i]	=loca;
				}
			}
		}
		manager.movement_start(modifier_cont,
				searcher.search_link_list.tree_node.movement_tree_id,
				ek.component_cont,true,switch_time_length);
		
		return searcher.search_link_list.tree_node.movement_tree_id;
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
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)
			return "no_search_result";
		t=searcher.search_link_list.tree_node;
		if(test_movement_tree(comp,t))
			return "selected_component_is_not_parent_of_movement_component";

		file_writer f=new file_writer(
				manager.config_parameter.design_file_name+".append",
				manager.config_parameter.movement_file_charset);
		f.println();
		f.println(comp.part_name);
		f.println(comp.component_name);
		searcher.search_link_list.tree_node.flush(f,0,false);
		f.println();
		f.close();
		
		if(new File(manager.config_parameter.design_file_name).exists()) {
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
		}else
			file_writer.file_rename(
					manager.config_parameter.design_file_name+".append",
					manager.config_parameter.design_file_name);
		return "ok";
	}
	private void listdesignbuffer()
	{
		component comp=ci.parameter.comp;
		ci.request_response.println("{");
		ci.request_response.print  ("		\"component_id\"	:	",
				(comp==null)?-1:(comp.component_id)).println(",");
		ci.request_response.println("		\"movement\"		:	[");
		for(boolean comma_flag=false;comp!=null;comp=ek.component_cont.get_component(comp.parent_component_id)){
			file_reader f=new file_reader(
				manager.config_parameter.design_file_name,manager.config_parameter.movement_file_charset);
			for(int i=0;;i++){
				String part_name=f.get_string(),component_name=f.get_string();
				if(f.eof())
					break;
				if(part_name==null)
					part_name="";
				if(component_name==null)
					component_name="";
				movement_tree t=new movement_tree(f,manager.id_creator);
				
				if(comp.part_name.compareTo(part_name)!=0)
					continue;
				
				if(comma_flag)
					ci.request_response.println(",");
				else
					comma_flag=true;
				
				ci.request_response.println("		{");
				ci.request_response.print  ("			\"buffer_id\"	:	",i).println(",");
				ci.request_response.println("			\"node_name\"	:	",jason_string.change_string(t.node_name));
				ci.request_response.print  ("		}");
			}
			f.close();
		}
		ci.request_response.println();
		ci.request_response.println("	]");
		
		ci.request_response.println("}");
	}
	private void deletedesignbuffer(int id)
	{
		file_writer.file_rename(manager.config_parameter.design_file_name,
								manager.config_parameter.design_file_name+".bak");
		file_reader f=new file_reader(
				manager.config_parameter.design_file_name+".bak",
				manager.config_parameter.movement_file_charset);
		file_writer fw=new file_writer(
				manager.config_parameter.design_file_name,
				manager.config_parameter.movement_file_charset);
		
		for(int i=0;;i++){
			String part_name=f.get_string(),component_name=f.get_string();
			if(f.eof()||(component_name==null)||(part_name==null))
				break;
			movement_tree t=new movement_tree(f,manager.id_creator);

			if(i==id)
				continue;
			
			fw.set_pace(0);
			fw.println();
			fw.println(part_name);
			fw.println(component_name);
			t.flush(fw,0,false);
			fw.println();
		}
		f.close();	
		fw.close();	
		file_writer.file_delete(manager.config_parameter.design_file_name+".bak");
	}
	private void clear_follow(movement_tree t)
	{
		if(t.move!=null) {
			t.move.follow_component_name=null;
			t.move.follow_component_location=null;
			t.move.follow_component_id=null;
		}
		if(t.children==null)
			return;
		for(int i=0,ni=t.children.length;i<ni;i++)
			clear_follow(t.children[i]);
	}
	private void fromdesignbuffer()
	{
		String str;
		component comp;
		
		if((str=ci.request_response.get_parameter("component"))==null)
			return;
		if((comp=ek.component_cont.get_component(Integer.decode(str)))==null)
			return;
		
		boolean place_flag;
		switch(((str=ci.request_response.get_parameter("place"))==null)?"true":str) {
		default:
		case "true":
		case "yes":
			place_flag=true;
			break;
		case "false":
		case "no":
			place_flag=false;
			break;	
		}
		int buffer_id;
		if((str=ci.request_response.get_parameter("buffer"))==null)
			return;
		buffer_id=Integer.decode(str);
	
		file_reader f=new file_reader(
				manager.config_parameter.design_file_name,
				manager.config_parameter.movement_file_charset);
		
		for(int i=0;;i++){
			String part_name=f.get_string();
			String component_name=f.get_string();
			if(f.eof()||(component_name==null)||(part_name==null))
				break;
			movement_tree t=new movement_tree(f,manager.id_creator);
			if(i!=buffer_id)
				continue;
			if(comp.part_name.compareTo(part_name)!=0)
				break;
			if(t.modify_component_name(ek.component_cont,component_name,comp.component_name)){
				if(manager.root_movement==null)
					manager.root_movement=new movement_tree(manager.id_creator);
				if(manager.root_movement.children==null){
					manager.root_movement.children=new movement_tree[] {t};
				}else{
					movement_tree tmp[]=new movement_tree[manager.root_movement.children.length+1];
					if(manager.mount_direction_flag^place_flag){
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
				clear_follow(t);
				manager.movement_start(modifier_cont,t.movement_tree_id,ek.component_cont,true,switch_time_length);
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
	private long view_direction()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return manager.root_movement.movement_tree_id;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)
			return manager.root_movement.movement_tree_id;
		if(searcher.search_link_list.tree_node.direction!=null)
			searcher.search_link_list.tree_node.direction=null;
		else{
			clear_view_direction(searcher.search_link_list.tree_node);
			searcher.search_link_list.tree_node.direction=new location(
					ci.display_camera_result.cam.eye_component.absolute_location);
		}
		return searcher.search_link_list.tree_node.movement_tree_id;
	}
	private long view_box()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return manager.root_movement.movement_tree_id;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)	
			return manager.root_movement.movement_tree_id;
		
		switch(searcher.search_link_list.tree_node.scale_type){
		case 1://起点相机比例
			searcher.search_link_list.tree_node.scale_type=2;
			break;
		case 2://终点相机比例   
			searcher.search_link_list.tree_node.scale_type=3;
			break;
		case 3://起点终点相机比例  
			searcher.search_link_list.tree_node.scale_type=0;
			break;		
		case 0://上层相机比例
		default:
			searcher.search_link_list.tree_node.scale_type=1;
			break;
		}
		return searcher.search_link_list.tree_node.movement_tree_id;
	}
	private long push_component_collector()
	{
		if(manager.root_movement==null)
			return -1;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return -1;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)
			return -1;
		
		add_component(searcher.search_link_list.tree_node);
		if(all_components.make_to_children()<=0)
			return -1;
		
		driver_audio.extended_component_driver acd;
		if((acd=manager.config_parameter.get_audio_component_driver(ek))!=null)
			acd.set_audio(null);
		component_collector collector=ek.collector_stack.push_component_array(true,
				ek.system_par,ek.scene_par,all_components,ek.component_cont,ek.render_cont.renders);
		if(collector==null)
			return -1;
		if(collector.component_number<=0)
			return collector.list_id;
		collector.title=searcher.search_link_list.tree_node.node_name;
		collector.description=searcher.search_link_list.tree_node.description;
		collector.audio_file_name=file_reader.separator(
				manager.directory_name+searcher.search_link_list.tree_node.sound_file_name);
		if(acd!=null)
			acd.set_audio(collector.audio_file_name);
		return collector.list_id;
	}
	private void reset_movement_component(movement_tree t)
	{
		if(t==null)
			return;
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
			debug_information.println("Start loading movement from "+(manager.config_parameter.movement_file_name));
			if(manager.root_movement!=null)
				reset_movement_component(manager.root_movement);	
			manager.init(modifier_cont,ek.component_cont,manager.config_parameter.movement_file_name,
					switch_time_length,manager.config_parameter.movement_file_charset,true);
			debug_information.println("Terminate loading movement from "+(manager.config_parameter.movement_file_name));
		}
	}
	private String[] push()
	{
		return manager.push_movement(ek.component_cont,
				switch_time_length,manager.config_parameter.movement_file_charset,modifier_cont);
	}
	private long component_part_selection()
	{
		class component_part_selection_processor
		{
			private void process(movement_tree t,boolean add_or_clear_flag)
			{
				if(t.children==null){
					if(t.match!=null)
						t.match.add_or_delete_component_part_selection(add_or_clear_flag);
				}else {
					if(t.match!=null)
						t.match.reset();
					for(int i=0,ni=t.children.length;i<ni;i++)
						process(t.children[i],add_or_clear_flag);
				}
			}
			public component_part_selection_processor(movement_tree t,boolean add_or_clear_flag)
			{
				if(t!=null)
					process(t,add_or_clear_flag);
			}
		}
		
		String str;
		if(manager.root_movement==null)
			return -1;
		if((str=ci.request_response.get_parameter("id"))==null)
			str=Long.toString(manager.root_movement.movement_tree_id);
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)
			return manager.root_movement.movement_tree_id;
		if((str=ci.request_response.get_parameter("component_part_selection"))==null)
			str="clear";

		switch(str.trim().toLowerCase()){
		case "clear":
			new component_part_selection_processor(searcher.search_link_list.tree_node,false);
			break;
		case "set":
			new component_part_selection_processor(searcher.search_link_list.tree_node,true);
			break;
		}
		return searcher.search_link_list.tree_node.movement_tree_id;
	}
	private long component_face_match()
	{
		String str;
		
		if(manager.root_movement==null)
			return -1;
		if((str=ci.request_response.get_parameter("id"))==null)
			str=Long.toString(manager.root_movement.movement_tree_id);
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)
			return manager.root_movement.movement_tree_id;
		
		if((str=ci.request_response.get_parameter("component_face_match"))==null)
			str="clear";
		switch(str.trim().toLowerCase()) {
		case "clear":
			class clear_component_face_match
			{
				public void process(movement_tree t)
				{
					if(t.children==null) {
						if(t.match!=null)
							t.match.clear_component_face_match();
					}else {
						if(t.match!=null)
							t.match.reset();
						for(int i=0,ni=t.children.length;i<ni;i++)
							process(t.children[i]);
					}
				}
			}
			new clear_component_face_match().process(searcher.search_link_list.tree_node);
			break;
		case "add":
			if(searcher.search_link_list.tree_node.children!=null) {
				if(searcher.search_link_list.tree_node.match!=null)
					searcher.search_link_list.tree_node.match.reset();
				break;
			}
			if((str=ci.request_response.get_parameter("source_component_id"))==null)
				break;
			component comp;
			if((comp=ek.component_cont.get_component(Integer.parseInt(str)))==null)
				break;
			String source_component_name=comp.component_name;
			if((str=ci.request_response.get_parameter("source_body_id"))==null)
				break;
			int source_body_id;
			if((source_body_id=Integer.parseInt(str))<0)
				break;
			if((str=ci.request_response.get_parameter("source_face_id"))==null)
				break;
			int source_face_id;
			if((source_face_id=Integer.parseInt(str))<0)
				break;
			if((str=ci.request_response.get_parameter("destatination_component_id"))==null)
				break;
			if((comp=ek.component_cont.get_component(Integer.parseInt(str)))==null)
				break;
			String destatination_component_name=comp.component_name;
			if((str=ci.request_response.get_parameter("destatination_body_id"))==null)
				break;
			int destatination_body_id;
			if((destatination_body_id=Integer.parseInt(str))<0)
				break;
			if((str=ci.request_response.get_parameter("destatination_face_id"))==null)
				break;
			int destatination_face_id;
			if((destatination_face_id=Integer.parseInt(str))<0)
				break;
			searcher.search_link_list.tree_node.match.add_component_face_match(
					source_component_name,			source_body_id,			source_face_id,
					destatination_component_name,	destatination_body_id,	destatination_face_id);
			break;
		}
		return searcher.search_link_list.tree_node.movement_tree_id;
	}
	private void scale_time_length(movement_tree t,double scale_value)
	{
		if(t.move!=null)
			if(t.move.movement!=null)
				for(int i=0,ni=t.move.movement.length;i<ni;i++)
					t.move.movement[i].time_length*=scale_value;
		if(t.children!=null)
			for(int i=0,ni=t.children.length;i<ni;i++)
				scale_time_length(t.children[i],scale_value);
	}
	private void set_same_time_length(movement_tree t,double new_time_length)
	{
		if(t.move!=null)
			if(t.move.movement!=null)
				for(int i=0,ni=t.move.movement.length;i<ni;i++)
					t.move.movement[i].time_length=new_time_length;
		if(t.children!=null)
			for(int i=0,ni=t.children.length;i<ni;i++)
				set_same_time_length(t.children[i],new_time_length);
	}
	
	private void upload_movement()
	{
		if(manager==null)
			return;
		common_reader reader=new common_reader(
				ci.request_response.implementor.get_content_stream(),
				ek.system_par.network_data_charset);
		reader.get_string();
		String version_str=reader.get_string();
		if(reader.eof()) {
			reader.close();
			return;
		}
		if(version_str.compareTo("2015.10")!=0) {
			reader.close();
			
			debug_information.println();
			debug_information.println("Wrong version:	",version_str);
			return;
		}
		
		debug_information.println();
		debug_information.println("Start upload movement........");
		movement_tree t=new movement_tree(reader,manager.id_creator);
		debug_information.println("upload movement terminated");
		reader.close();
		
		push();
		if(manager.root_movement==null)
			manager.root_movement=t;
		else{
			reset_movement_component(manager.root_movement);
			movement_tree old=manager.root_movement;
			manager.root_movement=new movement_tree(manager.id_creator);
			manager.root_movement.children=new movement_tree[] {old,t};
		}
		manager.root_movement.mount_component(ek.component_cont,"");
		manager.reset(-1,modifier_cont,ek.component_cont,switch_time_length);
		return;
	}
	private void modify_time_length()
	{
		if(manager.root_movement==null)
			return;
		String str;
		if((str=ci.request_response.get_parameter("id"))==null)
			return;
		movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
		if(searcher.search_link_list==null)
			return;
		if((str=ci.request_response.get_parameter("modify_type"))==null)
			return;
		
		push();
		switch(str.toLowerCase()) {
		case "yes":
		case "true":
			double new_time_length;
			if((str=ci.request_response.get_parameter("time_length"))!=null)
				if((new_time_length=Double.parseDouble(str))>const_value.min_value)
					set_same_time_length(searcher.search_link_list.tree_node,new_time_length);
			break;
		case "no":
		case "false":
			double scale_value;
			if((str=ci.request_response.get_parameter("scale"))!=null)
				if((scale_value=Double.parseDouble(str))>const_value.min_value)
					scale_time_length(searcher.search_link_list.tree_node,scale_value);
			break;
		}
		manager.movement_start(modifier_cont,
			searcher.search_link_list.tree_node.movement_tree_id,
			ek.component_cont,true,switch_time_length);
		return;
	}
	private String[] design_request_dispatch(int movement_component_id,int movement_driver_id)
	{
		String str;
		
		if(ek.component_cont.root_component==null)
			return null;
		if((str=ci.request_response.get_parameter("move_method"))==null)
			return null;
		switch(str.trim().toLowerCase()){
		case "clear_all":
			push();
			if(manager.root_movement!=null){
				reset_movement_component(manager.root_movement);
				manager.root_movement=null;
			}
			return null;
		case "modify_time_length":
			modify_time_length();
			new movement_edit_jason(locate_camera(),switch_time_length,ci,manager);
			return null;
		case "download_audio":
		{
			if(manager.root_movement==null)
				return null;
			if((str=ci.request_response.get_parameter("id"))==null)
				str=Long.toString(manager.root_movement.movement_tree_id);
			movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
			if(searcher.search_link_list==null)
				return null;
			String directory_name=manager.directory_name+manager.config_parameter.sound_pre_string;
			String file_name=file_reader.separator(
					directory_name+searcher.search_link_list.tree_node.sound_file_name);
			return new String[] {file_name,null};
		}
		case "upload_audio":
		{
			if(manager.root_movement==null) {
				ci.request_response.print("-1");
				return null;
			}
			if((str=ci.request_response.get_parameter("id"))==null)
				str=Long.toString(manager.root_movement.movement_tree_id);
			movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
			if(searcher.search_link_list==null){
				ci.request_response.print("-1");
				return null;
			}
			String directory_name=manager.directory_name+manager.config_parameter.sound_pre_string;
			String file_name=file_reader.separator(
					directory_name+searcher.search_link_list.tree_node.sound_file_name);
			long write_length=0;
			try{
				InputStream is=ci.request_response.implementor.get_content_stream();
				FileOutputStream os	=new FileOutputStream(new File(file_name)); 
				byte buffer[]=new byte[ek.system_par.response_block_size];
				for(int len;(len=is.read(buffer))>=0;) { 
					write_length+=len;
					os.write(buffer,0,len);
				}
				os.close();
				is.close();
			}catch(Exception e){
				debug_information.println("write audio file fail:	",e.toString());
			}
			debug_information.println("write audio file:	",file_name);
			debug_information.println("write length:		",write_length);
			
			ci.request_response.print(write_length);
			return null;
		}
		case "upload_audio_webpage":
		{
			if(manager.root_movement==null)
				return null;
			if((str=ci.request_response.get_parameter("id"))==null)
				str=Long.toString(manager.root_movement.movement_tree_id);
			movement_searcher searcher=new movement_searcher(manager.root_movement,Long.decode(str));
			if(searcher.search_link_list==null)
				return null;
			String my_upload_url=ci.request_url_header+"&command=component";
			my_upload_url+="&method=event&operation=design&move_method=upload_audio";
			my_upload_url+="&event_component_id="	+movement_component_id;
			my_upload_url+="&event_driver_id="		+movement_driver_id;
			my_upload_url+="&id="					+searcher.search_link_list.tree_node.movement_tree_id;
			
			if((str=ci.request_response.get_parameter("change_name"))==null)
				str="";
			if((str=str.trim()).length()>0){
				String request_charset=ci.request_response.implementor.get_request_charset();
				try{
					str=java.net.URLDecoder.decode(str,request_charset);
					str=java.net.URLDecoder.decode(str,request_charset);
				}catch(Exception e){
					;
				}
			}
			new upload_web_page(ci,new change_name(null,str,null),
					"audio/mpeg",null,my_upload_url).create_web_page();
			return null;
		}
		case "download_movement":
			return push();
		case "upload_movement":
			upload_movement();
			return null;
		case "upload_movement_webpage":
		{
			String my_upload_url=ci.request_url_header+"&command=component";
			my_upload_url+="&method=event&operation=design&move_method=upload_movement";
			my_upload_url+="&event_component_id="	+movement_component_id;
			my_upload_url+="&event_driver_id="		+movement_driver_id;

			new upload_web_page(ci,new change_name(null,"",null),"text/plain",
					ek.system_par.network_data_charset,my_upload_url).create_web_page();
			return null;
		}
		case "component_part_selection":
			if((str=ci.request_response.get_parameter("restore"))!=null)
				if(str.toLowerCase().compareTo("true")==0)
					push();
			new movement_edit_jason(component_part_selection(),switch_time_length,ci,manager);
			return null;
		case "component_face_match":
			if((str=ci.request_response.get_parameter("restore"))!=null)
				if(str.toLowerCase().compareTo("true")==0)
					push();
			new movement_edit_jason(component_face_match(),switch_time_length,ci,manager);
			return null;
		case "edit_jason":
			new movement_edit_jason(-1,switch_time_length,ci,manager);
			return null;
		case "jason":
			movement_jason.create_jason(manager.parameter.current_movement_id,
				manager.root_movement,ci.request_response,switch_time_length);
			return null;
		case "get_location":
			new movement_get_location(ek,ci,manager);
			return null;
		case "set_location":
			new movement_set_location(ek,ci,manager);
			new movement_get_location(ek,ci,manager);
			return null;
		case "add_point":
			new movement_add_point(ek,ci,manager);
			new movement_get_location(ek,ci,manager);
			return null;
		case "delete_point":
			new movement_delete_point(ek,ci,manager);
			new movement_get_location(ek,ci,manager);
			return null;
		case "add_movement":
			new movement_add_movement(switch_time_length,ek,ci,manager);
			new movement_get_location(ek,ci,manager);
			return null;
		case "change_component":
			new movement_edit_jason(
					movement_change_component.change_component(ek,ci,manager,switch_time_length),
					switch_time_length,ci,manager);
			return null;
		case "delete":
			push();
			new movement_edit_jason(delete_move(),switch_time_length,ci,manager);
			return null;
		case "up":
			push();
			new movement_edit_jason(updown_move(manager.mount_direction_flag?true:false),switch_time_length,ci,manager);
			ci.request_response.println();
			return null;
		case "down":
			push();
			new movement_edit_jason(updown_move(manager.mount_direction_flag?false:true),switch_time_length,ci,manager);
			return null;
		case "reverse":
			push();
			new movement_edit_jason(reverse_move(),switch_time_length,ci,manager);
			return null;
		case "fromchild":
			push();
			new movement_edit_jason(fromchild(),switch_time_length,ci,manager);
			return null;
		case "frombuffer":
			push();
			new movement_edit_jason(frombuffer(),switch_time_length,ci,manager);
			return null;
		case "tobuffer":
			push();
			new movement_edit_jason(tobuffer(),switch_time_length,ci,manager);
			return null;
		case "follow":
			push();
			new movement_edit_jason(do_follow(),switch_time_length,ci,manager);
			return null;
		case "view_direction":
			push();
			new movement_edit_jason(view_direction(),switch_time_length,ci,manager);
			return null;
		case "view_box":
			push();
			new movement_edit_jason(view_box(),switch_time_length,ci,manager);
			return null;
		case "locate_camera":
			new movement_edit_jason(locate_camera(),switch_time_length,ci,manager);
			return null;
		case "collector":
			ci.request_response.println(push_component_collector());
			return null;
		case "sequence":
			push();
			new movement_edit_jason(set_sequence_flag(true),switch_time_length,ci,manager);
			return null;
		case "synchronization":
			push();
			new movement_edit_jason(set_sequence_flag(false),switch_time_length,ci,manager);
			return null;
		case "update":
			new movement_edit_jason(do_update(),switch_time_length,ci,manager);
			return null;
		case "todesignbuffer":			
			ci.request_response.println("\"",todesignbuffer()+"\"");
			return null;
		case "listdesignbuffer":
			listdesignbuffer();
			return null;
		case "fromdesignbuffer":
			push();
			fromdesignbuffer();
			return null;
		case "deletedesignbuffer":
			if((str=ci.request_response.get_parameter("buffer"))!=null)
				deletedesignbuffer(Integer.decode(str));
			return null;
		case "save":
			file_writer f=new file_writer(
				manager.config_parameter.movement_file_name,
				manager.config_parameter.movement_file_charset);
			manager.flush(ek.component_cont,f,switch_time_length,modifier_cont);
			f.close();
			return null;
		case "reload":
			push();
			reload();
			return null;
		case "retreat":
			ci.request_response.print(
				manager.pop_movement(	modifier_cont,ek.component_cont,switch_time_length,
						manager.config_parameter.movement_file_charset)?"true":"false");
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
	private void movement_continue(boolean create_render_modifier_flag)
	{
		String str;
		long current_movement_id=manager.parameter.current_movement_id;
		if((str=ci.request_response.get_parameter("id"))!=null)
			try {
				current_movement_id=Long.parseLong(str);
			}catch(Exception e) {
				current_movement_id=manager.parameter.current_movement_id;
			}
		boolean direction_flag=true,single_step_flag=false;
		if((str=ci.request_response.get_parameter("direct"))!=null)
			if(str.compareTo("backward")==0)
				direction_flag=false;
		if((str=ci.request_response.get_parameter("single_step"))!=null)
			if(str.toLowerCase().trim().compareTo("true")==0)
				single_step_flag=true;
		
		movement_tree t;
		if((t=manager.root_movement.search_movement(current_movement_id))==null)
			if((t=manager.root_movement.search_movement(manager.parameter.current_movement_id))==null)
				for(t=manager.root_movement;t.children!=null;t=t.children[0])
					if(t.children.length<=0)
						break;
		manager.parameter.current_movement_id=t.movement_tree_id;
		
		modifier_cont.clear_modifier(ek,ci);
		update_component_location(ek.component_cont.root_component);

		component location_comp;
		if((location_comp=ek.component_cont.get_component(manager.config_parameter.location_component_id))!=null)
			for(int i=0,ni=location_comp.driver_number();i<ni;i++) 
				if(location_comp.driver_array[i] instanceof extended_component_driver)
					((extended_component_driver)(location_comp.driver_array[i])).clear_location_modifier();
		
		driver_audio.extended_component_driver acd;
		if((acd=manager.config_parameter.get_audio_component_driver(ek))!=null)
			acd.set_audio(null);
		
		manager.movement_start(modifier_cont,
				manager.parameter.current_movement_id,ek.component_cont,direction_flag,switch_time_length);

		manager.suspend.reset_suspend_collector(ek);
		manager.suspend.reset_virtual_mount_component(ek);
		manager.suspend.reset_suspend_match();
		
		if(create_render_modifier_flag)
			manager.create_render_modifier(t,single_step_flag,
				manager.config_parameter.audio_component_id,
				manager.config_parameter.location_component_id,
				modifier_cont,ek.component_cont,switch_time_length,
				manager.config_parameter.sound_pre_string);
	}
	private String[]  movement_request_dispatch()
	{
		String str;
				
		if(manager==null)
			return null;
		if(manager.root_movement==null)
			return null;
		if(ek.camera_cont.camera_array==null)
			return null;
		if((str=ci.request_response.get_parameter("move_method"))==null)
			return null;
		switch(str){
		case "stop":
			movement_continue(false);
			break;
		case "continue":
			movement_continue(true);
			break;
		case "search_jason":
			new movement_search_jason(manager.config_parameter.component_id,
					ek.component_cont.search_component(),ek.component_cont,manager.root_movement,ci);
			break;
		default:
			break;
		}
		return null;
	}
	public String[] get_engine_result(int movement_component_id,int movement_driver_id)
	{
		String str;
				
		if((str=ci.request_response.get_parameter("operation"))==null)
			return null;

		switch(str) {
		case "movement":
			return movement_request_dispatch();
		case "design":
			return design_request_dispatch(movement_component_id,movement_driver_id);
		case "virtual_mount":
			manager.suspend.response_suspend_event(ek,ci);
			return null;
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
		modifier_cont=ek.modifier_cont[manager.config_parameter.movement_modifier_container_id];

		camera_parameter cam_par=ci.display_camera_result.cam.parameter;
		switch_time_length=cam_par.movement_flag?cam_par.switch_time_length:0;
	}
}
