package driver_movement;

import kernel_file_manager.file_reader;

public class movement_load_old_file {
	
	private String num_str(int x)
	{
		String str=Integer.toString(x);
		while(str.length()<5)
			str="0"+str;
		return str;
	}
	
	private movement_tree []move_array;
	private int []main_step_array;
	private int []min_step_array;
	private String []main_tag_array;
	private String []min_tag_array;
	private int max_movement,main_step,min_step;
	private String main_tag,min_tag;
	
	public movement_tree combine_to_tree(movement_tree_id_creator id_creator)
	{
		int i,j,k,m;
		
		movement_tree root_node=new movement_tree(id_creator);
		
		root_node.node_name			="movement";
		root_node.description		="no_description";
		root_node.sound_file_name	="no_sound";
		root_node.sequence_flag=true;
		
		for(i=0;i<max_movement;i=j){
			movement_tree main_node=new movement_tree(id_creator);
			main_node.node_name=num_str(main_step_array[i]);
			if(main_tag_array[i].compareTo("")!=0)
				main_node.node_name+=":"+main_tag_array[i];
			main_node.description=move_array[i].description;
			main_node.sound_file_name="no_sound";
			main_node.sequence_flag=true;
			
			if(root_node.children==null){
				root_node.children=new movement_tree[1];
				root_node.children[0]=main_node;
			}else{
				movement_tree []new_children=new movement_tree[root_node.children.length+1];
				for(j=0;j<(root_node.children.length);j++)
					new_children[j]=root_node.children[j];
				new_children[root_node.children.length]=main_node;
				root_node.children=new_children;
			}
			for(j=i;j<max_movement;j=k){
				if(main_step_array[i]!=main_step_array[j])
					break;
				movement_tree min_node=new movement_tree(id_creator);
				min_node.node_name			=main_node.node_name+"/"+num_str(min_step_array[j]);
				if(min_tag_array[j].compareTo("")!=0)
					min_node.node_name+=":"+min_tag_array[j];
				min_node.description		=move_array[j].description;
				min_node.sound_file_name	=file_reader.separator(move_array[j].sound_file_name);
				min_node.sequence_flag		=false;

				if(main_node.children==null){
					main_node.children=new movement_tree[1];
					main_node.children[0]=min_node;
				}else{
					movement_tree []new_children=new movement_tree[main_node.children.length+1];
					for(k=0;k<(main_node.children.length);k++)
						new_children[k]=main_node.children[k];
					new_children[main_node.children.length]=min_node;
					main_node.children=new_children;
				}
				for(k=j;k<max_movement;k++){
					if(main_step_array[k]!=main_step_array[i])
						break;
					if(min_step_array[k]!=min_step_array[j])
						break;
					move_array[k].node_name=min_node.node_name+"/"+num_str(k-j+1);
					if(min_node.children==null){
						min_node.children=new movement_tree[1];
						min_node.children[0]=move_array[k];
					}else{
						movement_tree []new_children=new movement_tree[min_node.children.length+1];
						for(m=0;m<(min_node.children.length);m++)
							new_children[m]=min_node.children[m];
						new_children[min_node.children.length]=move_array[k];
						min_node.children=new_children;
					}
				}
			}
		}
		return root_node;
	}
	
	private movement_tree load_one_node(file_reader f,movement_tree_id_creator id_creator)
	{
		String my_system_name;
		
		movement_tree ret_val=new movement_tree(id_creator);
		
		main_step=f.get_int();
		min_step=f.get_int();

		if((my_system_name=f.get_string())==null)
			my_system_name="";
		if((main_tag=f.get_string())==null)
			main_tag="";
		if((min_tag=f.get_string())==null)
			min_tag="";
		
		if(main_tag.compareTo("no_tag")==0)
			main_tag="";
		if(min_tag.compareTo("no_tag")==0)
			min_tag="";	
		
		if((ret_val.sound_file_name=f.get_string())==null)
			ret_val.sound_file_name="";
		else
			ret_val.sound_file_name=file_reader.separator(ret_val.sound_file_name);
		
		if((ret_val.description=f.get_string())==null)
			ret_val.description="";
		ret_val.sequence_flag	=false;	
		
		ret_val.move			=new movement_item_container(my_system_name,null,null,f,false);
		f.get_string();
		ret_val.match			=new movement_match_container(f);
		
		if(ret_val.move.movement!=null)
			for(int i=0;i<ret_val.move.movement.length;i++){
				ret_val.move.movement[i].time_length	*=1000*1000;
				ret_val.move.movement[i].start_time		*=1000*1000;
				ret_val.move.movement[i].terminate_time	*=1000*1000;
			}
		return ret_val;
	}
	
	public movement_load_old_file(file_reader f,movement_tree_id_creator id_creator)
	{
		int my_max_movement=50000;
		
		max_movement=my_max_movement;
		move_array=new movement_tree[max_movement];
		main_step_array=new int[max_movement];
		min_step_array=new int[max_movement];
		main_tag_array=new String[max_movement];
		min_tag_array=new String[max_movement];
		
		for(max_movement=0;max_movement<(move_array.length);max_movement++){
			move_array[max_movement]=load_one_node(f,id_creator);
			if(move_array[max_movement].move.movement==null)
				break;
			if(move_array[max_movement].move.movement.length<=0)
				break;
			main_step_array[max_movement]=main_step;
			min_step_array[max_movement]=min_step;
			main_tag_array[max_movement]=main_tag;
			min_tag_array[max_movement]=min_tag;
			for(int j=max_movement-1;j>=0;j--){
				if(main_step_array[j]<main_step_array[j+1])
					break;
				if(main_step_array[j]==main_step_array[j+1])
					if(min_step_array[j]<=min_step_array[j+1])
						break;
				movement_tree p=move_array[j];
				movement_tree q=move_array[j+1];
				move_array[j]	=q;
				move_array[j+1]	=p;
				
				int x=main_step_array[j];
				int y=main_step_array[j+1];
				main_step_array[j]=y;
				main_step_array[j+1]=x;
				
				x=min_step_array[j];
				y=min_step_array[j+1];
				min_step_array[j]=y;
				min_step_array[j+1]=x;	
				
				String xs=main_tag_array[j];
				String ys=main_tag_array[j+1];
				main_tag_array[j]=ys;
				main_tag_array[j+1]=xs;

				xs=min_tag_array[j];
				ys=min_tag_array[j+1];
				min_tag_array[j]=ys;
				min_tag_array[j+1]=xs;				
			}
		}
	}
}
