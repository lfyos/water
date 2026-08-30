package kernel_component;

import java.util.Collection;

import kernel_part.part;
import kernel_part.part_rude;
import kernel_driver.component_driver;
import kernel_interface.client_process_bar;
import kernel_common_class.debug_information;
import kernel_common_class.tree_search_container_tree_node;
import kernel_common_class.tree_string_search_container;

public class component_caculator 
{
	private int same_render_component_driver_id[];
	private int same_part_component_driver_id[][];
	private int not_exist_component_driver_id;
	
	public int component_number;
	public int render_component_id_and_driver_id[][][],part_component_id_and_driver_id[][][][];
	public component component_pointer[];
	public tree_string_search_container<component> search_component_cont;
	public int top_assemble_component_number,part_component_number,exist_part_component_number;
	public long total_face_primitive_number,total_edge_primitive_number,total_point_primitive_number;
	
	private void caculate_component_driver_id(component comp)
	{
		int driver_number	=comp.driver_array.size();
		int children_number	=comp.children.size();
	
		for(int i=0;i<children_number;i++)
			caculate_component_driver_id(comp.children.get(i));
		
		comp.component_id		=component_number++;
		comp.parent_component_id=-1;
		for(int i=0;i<children_number;i++)
			comp.children.get(i).parent_component_id=comp.component_id;
		
		for(int i=0;i<driver_number;i++){
			component_driver c_d=comp.driver_array.get(i);
			c_d.same_render_component_driver_id=not_exist_component_driver_id;
			c_d.same_part_component_driver_id	=not_exist_component_driver_id;
			not_exist_component_driver_id++;
			if(c_d.component_part==null)
				continue;
			int render_id	=c_d.component_part.render_id;
			int part_id		=c_d.component_part.part_id;
			if((render_id<0)||(part_id<0))
				continue;
			
			if(same_part_component_driver_id==null){
				same_render_component_driver_id	=new int[render_id+1];
				same_part_component_driver_id	=new int[render_id+1][];
				for(int j=0,nj=same_part_component_driver_id.length;j<nj;j++){
					same_render_component_driver_id	[j]=0;
					same_part_component_driver_id	[j]=null;
				}
			}else if(render_id>=same_part_component_driver_id.length){
				int render_bak[]	=same_render_component_driver_id;
				int part_bak[][]	=same_part_component_driver_id;
				same_render_component_driver_id	=new int[render_id+1];
				same_part_component_driver_id	=new int[render_id+1][];
				for(int j=0,nj=part_bak.length;j<nj;j++){
					same_render_component_driver_id	[j]	=render_bak	[j];
					same_part_component_driver_id	[j]	=part_bak	[j];
				}
				for(int j=part_bak.length,nj=same_part_component_driver_id.length;j<nj;j++){
					same_render_component_driver_id	[j]	=0;
					same_part_component_driver_id	[j]	=null;
				}
			}
			if(same_part_component_driver_id[render_id]==null){
				same_part_component_driver_id[render_id]=new int[part_id+1];
				for(int j=0,nj=same_part_component_driver_id[render_id].length;j<nj;j++)
					same_part_component_driver_id[render_id][j]=0;
			}else if(part_id>=same_part_component_driver_id[render_id].length){
				int part_bak[]=same_part_component_driver_id[render_id];
				same_part_component_driver_id[render_id]=new int[part_id+1];
				for(int j=0,nj=part_bak.length;j<nj;j++)
					same_part_component_driver_id[render_id][j]=part_bak[j];
				for(int j=part_bak.length,nj=same_part_component_driver_id[render_id].length;j<nj;j++)
					same_part_component_driver_id[render_id][j]=0;
			}
			c_d.same_render_component_driver_id=same_render_component_driver_id[render_id]++;
			c_d.same_part_component_driver_id	=same_part_component_driver_id	[render_id][part_id]++;
			not_exist_component_driver_id--;
		}
	}
	
	private void register_componennt_to_part(component comp)
	{
		component_driver c_d;
		for(int i=0,ni=comp.children.size();i<ni;i++)
			register_componennt_to_part(comp.children.get(i));
		
		for(int i=0,ni=comp.driver_array.size();i<ni;i++)
			if((c_d=comp.driver_array.get(i)).component_part!=null){
				int render_id	=c_d.component_part.render_id;
				int part_id		=c_d.component_part.part_id;
				if((render_id<0)||(part_id<0))
					continue;
				
				if(render_component_id_and_driver_id==null){
					int nj=same_render_component_driver_id.length;
					render_component_id_and_driver_id=new int[nj][][];
					for(int j=0;j<nj;j++)
						render_component_id_and_driver_id[j]=null;
				}
				if(render_component_id_and_driver_id[render_id]==null){
					int nj=same_render_component_driver_id[render_id];
					render_component_id_and_driver_id[render_id]=new int[nj][];
					for(int j=0;j<nj;j++)
						render_component_id_and_driver_id[render_id][j]=null;
				}
				if(part_component_id_and_driver_id==null){
					int nj=same_part_component_driver_id.length;
					part_component_id_and_driver_id=new int[nj][][][];
					for(int j=0;j<nj;j++)
						part_component_id_and_driver_id[j]=null;
				}
				if(part_component_id_and_driver_id[render_id]==null){
					int nj=same_part_component_driver_id[render_id].length;
					part_component_id_and_driver_id[render_id]=new int[nj][][];
					for(int j=0;j<nj;j++)
						part_component_id_and_driver_id[render_id][j]=null;
				}
				if(part_component_id_and_driver_id[render_id][part_id]==null){
					int nj=same_part_component_driver_id[render_id][part_id];
					part_component_id_and_driver_id[render_id][part_id]=new int[nj][];
					for(int j=0;j<nj;j++)
						part_component_id_and_driver_id[render_id][part_id][j]=null;
				}
				int id[]={comp.component_id,i};
				part_component_id_and_driver_id		[render_id][part_id][c_d.same_part_component_driver_id		]=id;
				render_component_id_and_driver_id	[render_id]			[c_d.same_render_component_driver_id	]=id;
			}
	}
	
	private void set_pointer(component comp)
	{
		for(component my_child_comp:comp.children)
			set_pointer(my_child_comp);
		component_pointer[comp.component_id]=comp;
		search_component_cont.add(comp.component_name,comp);
	}
	public component_caculator(component root_component,boolean display_flag,
			client_process_bar process_bar,String process_bar_title)
	{
		component_number					=0;
		render_component_id_and_driver_id	=null;
		part_component_id_and_driver_id		=null;
		same_render_component_driver_id		=null;
		same_part_component_driver_id		=null;
		not_exist_component_driver_id		=0;
		caculate_component_driver_id(root_component);
		register_componennt_to_part(root_component);
		
		component_pointer				=new component[component_number];
		search_component_cont			=new tree_string_search_container<component>(null);
		
		set_pointer(root_component);
		
		for(int i=0,ni=component_pointer.length;i<ni;i++){
			component comp=component_pointer[i];
			if((comp.driver_array.size()<=0)&&(comp.children.size()<=0)){
				if(display_flag)
					debug_information.print  ("Find no driver component:");
				do{
					if(display_flag) {
						debug_information.print  ("\t",comp.component_name);
						debug_information.print  ("\t",comp.part_name);
					}
					if(comp.parent_component_id<0)
						break;
					if(display_flag)
						debug_information.print  ("\t");
					comp=component_pointer[comp.parent_component_id];
				}while(true);
				
				if(display_flag)
					debug_information.println();
			}
		}
		
		{
			Collection<tree_search_container_tree_node<String,component>> sort_component_list;
			sort_component_list=search_component_cont.tree_get_node_collection();
			int display_same_number=0,display_same_component_number=0;
			int i=0,total_same_component_number=sort_component_list.size();
			
			process_bar.set_process_bar(true,process_bar_title,"", 0,total_same_component_number);
			
			for(tree_search_container_tree_node <String,component> my_tree_node:sort_component_list){
				process_bar.set_process_bar(false,process_bar_title,my_tree_node.key,i++,total_same_component_number);
				if(my_tree_node.list.size()<=1)
					continue;
				display_same_number++;
				int j=0,item_number=my_tree_node.list.size();
				for(component my_component:my_tree_node.list){
					j++;
					display_same_component_number++;
					if(display_flag){
						debug_information.print  (display_same_number);
						debug_information.print  ("	Find same name component:",j+"/"+item_number);
						debug_information.print  ("	",my_component.component_name);
						debug_information.print  ("	",my_component.part_name);
						debug_information.print  ("	",my_component.component_directory_name);
						debug_information.println(my_component.component_file_name);
					}
				}
			}
			process_bar.set_process_bar(false,process_bar_title,"", 
					total_same_component_number,total_same_component_number);
			
			if(display_same_component_number>0)
				if(display_flag)
					debug_information.println("	total same name componment number is ",
						Integer.toString(display_same_number)+"/"+Integer.toString(display_same_component_number));
		}

		{
			top_assemble_component_number=0;
			part_component_number=0;
			exist_part_component_number=0;
			total_face_primitive_number=0;
			total_edge_primitive_number=0;
			total_point_primitive_number=0;
		
			for(int i=0,ni=component_pointer.length;i<ni;i++){
				component comp=component_pointer[i];
				for(int j=0,nj=comp.driver_array.size();j<nj;j++){
					part my_part=comp.driver_array.get(j).component_part;
					if(my_part.is_top_box_part()){
						top_assemble_component_number++;
						break;
					}	
				}
				if(comp.children.size()<=0){
					part_component_number++;
					if(comp.driver_array.size()>0){
						exist_part_component_number++;
						part_rude part_mesh;
						if((part_mesh=comp.driver_array.get(0).component_part.part_mesh)!=null) {
							total_face_primitive_number	+=part_mesh.total_face_primitive_number;
							total_edge_primitive_number	+=part_mesh.total_edge_primitive_number;
							total_point_primitive_number+=part_mesh.total_point_primitive_number;
						}
					}
				}	
			}
		}
	}
}
