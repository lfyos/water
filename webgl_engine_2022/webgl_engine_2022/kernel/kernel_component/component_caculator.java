package kernel_component;

import kernel_common_class.debug_information;
import kernel_interface.client_process_bar;
import kernel_part.part_rude;
import kernel_part.part;

public class component_caculator 
{
	private int component_id;
	private int same_render_component_driver_id[];
	private int same_part_component_driver_id[][];
	private int not_exist_component_driver_id;
	
	public int render_component_id_and_driver_id[][][],part_component_id_and_driver_id[][][][];
	public component component_pointer[],sort_component_pointer[];
	public int top_assemble_component_number,part_component_number,exist_part_component_number;
	public long total_face_primitive_number,total_edge_primitive_number,total_point_primitive_number;
	
	private void caculate_component_driver_id(component comp)
	{
		int driver_number	=comp.driver_number();
		int children_number	=comp.children_number();
	
		for(int i=0;i<children_number;i++)
			caculate_component_driver_id(comp.children[i]);
		
		comp.component_id		=component_id++;
		comp.parent_component_id=-1;
		for(int i=0;i<children_number;i++)
			comp.children[i].parent_component_id=comp.component_id;
		
		for(int i=0;i<driver_number;i++){
			comp.driver_array[i].same_render_component_driver_id=not_exist_component_driver_id;
			comp.driver_array[i].same_part_component_driver_id	=not_exist_component_driver_id;
			not_exist_component_driver_id++;
			if(comp.driver_array[i].component_part==null)
				continue;
			int render_id	=comp.driver_array[i].component_part.render_id;
			int part_id		=comp.driver_array[i].component_part.part_id;
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
			comp.driver_array[i].same_render_component_driver_id=same_render_component_driver_id[render_id]++;
			comp.driver_array[i].same_part_component_driver_id	=same_part_component_driver_id	[render_id][part_id]++;
			not_exist_component_driver_id--;
		}
	}
	
	private void register_componennt_to_part(component comp)
	{
		for(int i=0,ni=comp.children_number();i<ni;i++)
			register_componennt_to_part(comp.children[i]);
		
		for(int i=0,ni=comp.driver_number();i<ni;i++)
			if(comp.driver_array[i].component_part!=null){
				int render_id	=comp.driver_array[i].component_part.render_id;
				int part_id		=comp.driver_array[i].component_part.part_id;
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
				part_component_id_and_driver_id		[render_id][part_id][comp.driver_array[i].same_part_component_driver_id		]=id;
				render_component_id_and_driver_id	[render_id]			[comp.driver_array[i].same_render_component_driver_id	]=id;
			}
	}
	
	private void set_pointer(component comp)
	{
		component_pointer[comp.component_id]		=comp;
		sort_component_pointer[comp.component_id]	=comp;
		
		for(int i=0,n=comp.children_number();i<n;i++)
			set_pointer(comp.children[i]);
	}
	
	private void sort_component(int begin_id,int end_id,component tmp[])
	{
		if(begin_id<end_id){
			int mid_id=(begin_id+end_id)/2;
			sort_component(begin_id,mid_id,tmp);
			sort_component(mid_id+1,end_id,tmp);
			
			for(int i=begin_id,j=mid_id+1,k=0;;){
				if(i>mid_id){
					if(j>end_id){
						for(i=begin_id,k=0;i<=end_id;i++,k++)
							sort_component_pointer[i]=tmp[k];
						return;
					}
					tmp[k++]=sort_component_pointer[j++];
				}else if(j>end_id)
					tmp[k++]=sort_component_pointer[i++];
				else {
					int compare_result;

					if((compare_result=sort_component_pointer[i].component_name.compareTo(sort_component_pointer[j].component_name))==0)
						compare_result=sort_component_pointer[i].part_name.compareTo(sort_component_pointer[j].part_name);
					
					if(compare_result<0)
						tmp[k++]=sort_component_pointer[i++];
					else
						tmp[k++]=sort_component_pointer[j++];
				}
			}
		}
	}
	public component_caculator(component root_component,boolean display_flag,
			client_process_bar process_bar,String process_bar_title)
	{
		component_id						=0;
		render_component_id_and_driver_id	=null;
		part_component_id_and_driver_id		=null;
		same_render_component_driver_id		=null;
		same_part_component_driver_id		=null;
		not_exist_component_driver_id		=0;
		caculate_component_driver_id(root_component);
		register_componennt_to_part(root_component);
		
		component_pointer		=new component[root_component.component_id+1];
		sort_component_pointer	=new component[root_component.component_id+1];
		set_pointer(root_component);
		sort_component(0,root_component.component_id,new component[root_component.component_id+1]);
		
		for(int i=0,ni=component_pointer.length;i<ni;i++){
			component comp=component_pointer[i];
			if((comp.driver_number()<=0)&&(comp.children_number()<=0)){
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
			process_bar.set_process_bar(true,process_bar_title,"", 0, sort_component_pointer.length);
			int display_same_number=0,display_same_compoennt_number=0;
			for(int i=0,j=0,ni=sort_component_pointer.length;i<ni;i=j){
				process_bar.set_process_bar(false,process_bar_title,sort_component_pointer[i].component_name, i, ni);
				for(j=i+1;j<ni;j++)
					if(sort_component_pointer[i].component_name.compareTo(sort_component_pointer[j].component_name)!=0)
						break;
				if((j-i)>1) {
					for(display_same_number++;(i<j)&&(i<ni);i++,display_same_compoennt_number++) {
						if(display_flag){
							debug_information.print  (display_same_number);
							debug_information.print  ("	Find same name component:",j-i);
							debug_information.print  ("	",sort_component_pointer[i].component_name);
							debug_information.print  ("	",sort_component_pointer[i].part_name);
							debug_information.print  ("	",sort_component_pointer[i].component_directory_name);
							debug_information.println(sort_component_pointer[i].component_file_name);
						}
					}
				}
			}
			process_bar.set_process_bar(false,process_bar_title,"", sort_component_pointer.length, sort_component_pointer.length);
			
			if(display_same_compoennt_number>0)
				if(display_flag)
					debug_information.println("	total same name componment number is ",
						Integer.toString(display_same_number)+"/"+Integer.toString(display_same_compoennt_number));
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
				for(int j=0,nj=comp.driver_number();j<nj;j++){
					part my_part=comp.driver_array[j].component_part;
					if(my_part.is_top_box_part()){
						top_assemble_component_number++;
						break;
					}	
				}
				if(comp.children==null){
					part_component_number++;
					if(comp.driver_number()>0){
						exist_part_component_number++;
						part_rude part_mesh;
						if((part_mesh=comp.driver_array[0].component_part.part_mesh)!=null) {
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
