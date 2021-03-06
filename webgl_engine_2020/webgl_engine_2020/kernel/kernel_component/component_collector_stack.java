package kernel_component;

import kernel_common_class.common_reader;
import kernel_common_class.common_writer;
import kernel_component.component;
import kernel_component.component_collector;
import kernel_component.component_link_list;
import kernel_file_manager.file_directory;
import kernel_file_manager.file_reader;
import kernel_part.part;
import kernel_render.render;
import kernel_engine.component_container;
import kernel_engine.system_parameter;
import kernel_engine.scene_parameter;

public class component_collector_stack
{
	public String component_collector_stack_file_name,component_collector_stack_file_charset;
	private int parameter_channel_id[];
	private long version;
	public long get_collector_version()
	{
		return version;
	}
	private component_collector collector[];
	private int collector_pointer;
	
	public void destroy()
	{
		if(collector!=null)
			for(int i=0,ni=collector.length;i<ni;i++)
				if(collector[i]!=null) {
					collector[i].destroy();
					collector[i]=null;
				}
		collector=null;
	}
	
	private void set_hide_flag(component comp,boolean hide_flag,component_container component_cont)
	{
		int child_number;
		if((child_number=comp.children_number())>0) {
			comp.modify_display_flag(parameter_channel_id,true,component_cont);
			for(int i=0;i<child_number;i++)
				set_hide_flag(comp.children[i],hide_flag,component_cont);
		}else{
			if(hide_flag)
				if(comp.uniparameter.part_list_flag)
					if(comp.driver_number()>0)
						if(comp.driver_array[0].component_part!=null) {
							comp.modify_display_flag(parameter_channel_id,false,component_cont);
							return;
						}
			comp.modify_display_flag(parameter_channel_id,true,component_cont);
		}
	}
	private void set_hide_flag(component_collector my_collector,
			boolean hide_flag,component_container component_cont)
	{
		if(my_collector!=null)
			for(int i=0;i<(my_collector.component_collector.length);i++)
				if(my_collector.component_collector[i]!=null)
					for(int j=0;j<(my_collector.component_collector[i].length);j++)
						for(component_link_list p=my_collector.component_collector[i][j];p!=null;p=p.next_list_item)
							set_hide_flag(p.comp,hide_flag,component_cont);
	}
	public component_collector push_collector(system_parameter system_par,scene_parameter scene_par,
			component_collector push_collector,component_container component_cont,render []renders)
	{
		part p;
		if(push_collector.part_number==1)
			for(int render_id=0,exit_flag=1;(exit_flag>0)&&(render_id<push_collector.component_collector.length);render_id++)
				if(push_collector.component_collector[render_id]!=null)
					for(int part_id=0;(exit_flag>0)&&(part_id<push_collector.component_collector[render_id].length);part_id++)
						for(component_link_list cll=push_collector.component_collector[render_id][part_id];(exit_flag>0)&&(cll!=null);cll=cll.next_list_item)
							for(int i=0,ni=cll.comp.driver_number();(exit_flag>0)&&(i<ni);i++)
								if((p=cll.comp.driver_array[i].component_part)!=null){
									String root_directory=file_directory.part_file_directory(p,system_par,scene_par);
									
									if(cll.comp.uniparameter.display_part_name_or_component_name_flag)
										push_collector.title=p.user_name;
									else
										push_collector.title=cll.comp.component_name;
									
									push_collector.description=p.user_name;
									if(p.description_file_name!=null)
										if(file_reader.is_exist(p.directory_name+p.description_file_name))
											push_collector.description=file_reader.get_text(
													p.directory_name+p.description_file_name,p.file_charset);

									push_collector.audio_file_name=root_directory+"audio.mp3";
									exit_flag=0;
								}

		set_hide_flag(get_top_collector(),true,component_cont);
		push_collector.list_id=version++;
		collector[collector_pointer]=push_collector;
		collector_pointer=(1+collector_pointer)%(collector.length);
		set_hide_flag(push_collector,false,component_cont);

		return push_collector;
	}
	public component_collector push_component_array(
			boolean part_list_flag_effective_flag,system_parameter system_par,scene_parameter scene_par,
			component_array comp_array,component_container component_cont,render []renders)
	{
		comp_array.make_to_ancestor(component_cont);
		comp_array.remove_not_in_part_list_component(part_list_flag_effective_flag);
		component_collector push_collector=new component_collector(renders);
		push_collector.register_component(comp_array);
		return push_collector(system_par,scene_par,push_collector,component_cont,renders);
	}
	public component_collector pop(component_container component_cont,boolean pop_all_flag)
	{
		component_collector ret_val=get_top_collector();
		set_hide_flag(ret_val,true,component_cont);
		
		if(pop_all_flag){
			for(int i=0,ni=collector.length;i<ni;i++)
				collector[i]=null;
			collector_pointer=0;
		}else{
			collector_pointer=(collector_pointer-1+collector.length)%(collector.length);
			collector[collector_pointer]=null;
			set_hide_flag(get_top_collector(),false,component_cont);
		}
		version++;
		return ret_val;
	}
	public component_collector get_top_collector()
	{
		return collector[(collector.length-1+collector_pointer)%(collector.length)];
	}
	public component_collector get_one_collector(long list_id)
	{
		for(int i=0,ni=collector.length;i<ni;i++)
			if(collector[i]!=null)
				if(collector[i].list_id==list_id)
					return collector[i];
		return null;
	}
	public component_collector[] get_all_collector()
	{
		int collector_number=0;
		for(int i=0;i<collector.length;i++)
			if(collector[i]!=null)
				collector_number++;
		if(collector_number<=0)
			return null;
		component_collector ret_val[]=new component_collector[collector_number];
		for(int i=0,j=collector_pointer-1;i<ret_val.length;j--){
			j=(collector.length+j)%(collector.length);
			if(collector[j]!=null)
				ret_val[i++]=collector[j];
		}
		return ret_val;
	}
	public component_collector delete_collector(
			long list_id,component_container component_cont)
	{
		version++;
		if(list_id<0){
			collector_pointer=0;
			for(int i=0,ni=collector.length;i<ni;i++)
				collector[i]=null;	
			return null;
		}
		for(int i=0,ni=collector.length;i<ni;i++){
			component_collector ret_val;
			if((ret_val=collector[i])!=null)
				if(ret_val.list_id==list_id){
					int top=(collector_pointer-1+ni)%ni;
					if(i==top)
						return pop(component_cont,false);
					for(int j=i,next_j;j!=top;j=next_j)
						collector[j]=collector[next_j=(j+1)%ni];
					collector[top]=null;
					collector_pointer=top;
					return ret_val;
				}
		}
		return null;
	}

	private static final String no_title_string="__no_title__"; 
	
	public void save(common_writer fw,
			component_container component_cont,boolean save_component_name_or_id_flag)
	{
		component_collector p[]=get_all_collector();
		
		if(p==null)
			return;
		
		fw.println("/*	save_component_name_or_id_flag 	*/		",save_component_name_or_id_flag?"true":"false");
		fw.println();
		
		component_array ca=new component_array(component_cont.root_component.component_id+1);
		
		for(int i=p.length-1;i>=0;i--){
			ca.clear_compoment();
			ca.add_collector(p[i]);
			
			ca.make_to_children();
			ca.make_to_ancestor(component_cont);
			
			if(ca.component_number<=0)
				continue;
			if(ca.component_number==1)
				if(ca.comp[0].component_id==component_cont.root_component.component_id)
					continue;
			
			String str=p[i].title;
			if(str==null)
				str=no_title_string;
			else if((str=str.trim()).length()<=0)
				str=no_title_string;

			fw.println(str+"\t\t",ca.component_number);
			
			if(save_component_name_or_id_flag)
				for(int j=0,nj=ca.component_number;j<nj;j++)
					fw.println("\t",ca.comp[j].component_name);
			else
				for(int j=0,nj=ca.component_number;j<nj;j++)
					fw.println("\t",ca.comp[j].component_id);
			
			fw.println();
		}
		fw.println();
		fw.println();
		fw.println();
	}
	public void load(common_reader f,component_container component_cont,
			system_parameter system_par,scene_parameter scene_par,render []renders)
	{
		boolean save_component_name_or_id_flag=f.get_boolean();
		
		component_array ca=new component_array(component_cont.root_component.component_id+1);
		
		for(int i=0,ni=scene_par.max_component_collector_number;i<ni;i++){
			String title=f.get_string();
			int component_number=f.get_int();
			if(f.eof())
				break;
			if(f.error_flag())
				break;
			ca.clear_compoment();
			
			for(int j=0;(j<component_number)&&(!(f.eof()));j++){
				String component_name_or_id;
				if((component_name_or_id=f.get_string())==null)
					continue;
				component comp;
				if(save_component_name_or_id_flag)
					comp=component_cont.search_component(component_name_or_id);
				else
					comp=component_cont.get_component(Integer.decode(component_name_or_id));
				
				if(comp!=null)
					ca.add_component(comp);
			}
			if(ca.component_number>0){
				ca.make_to_children();
				ca.make_to_ancestor(component_cont);
				push_component_array(false,system_par,scene_par,ca,component_cont,renders);
				if(title!=null)
					if(title.compareTo(no_title_string)!=0)
						get_top_collector().title=title;
			}
		}
		
		version++;
	}
	public component_collector_stack(String scene_directory_name,String my_component_collector_stack_file_charset,
			component_container component_cont,system_parameter system_par,scene_parameter scene_par,render []renders)
	{
		version=1;
		parameter_channel_id=scene_par.component_collector_parameter_channel_id;

		collector_pointer=0;
		collector=new component_collector[scene_par.max_component_collector_number];
		for(int i=0,ni=collector.length;i<ni;i++)
			collector[i]=null;
	
		if(component_cont!=null)
			if(component_cont.root_component!=null)
				set_hide_flag(component_cont.root_component,true,component_cont);

		component_collector_stack_file_name=scene_directory_name+scene_par.component_collector_stack_file_name;
		component_collector_stack_file_charset=my_component_collector_stack_file_charset;
		file_reader f=new file_reader(component_collector_stack_file_name,component_collector_stack_file_charset);
		load(f,component_cont,system_par,scene_par,renders);
		f.close();
	}
}
