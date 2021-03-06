package kernel_common_class;

public class balance_tree 
{
	private	balance_tree_item item;
	private balance_tree left_child,right_child;
	private int depth;
	
	public void destroy()
	{
		item=null;
		
		if(left_child!=null) {
			left_child.destroy();
			left_child=null;
		}
		
		if(right_child!=null) {
			right_child.destroy();
			right_child=null;
		}
	}
	public balance_tree clone()
	{
		balance_tree ret_val;
		
		ret_val=new balance_tree(item.clone());
		
		if(left_child!=null)
			ret_val.left_child=left_child.clone();
		if(right_child!=null)
			ret_val.right_child=right_child.clone();
		ret_val.depth=depth;
		
		return ret_val;
	}
	public balance_tree(balance_tree_item my_item)
	{
		item=my_item;
		left_child=null;
		right_child=null;
		depth=0;
	}
	private void ll()
	{
		balance_tree_item bak;
		bak				=item;
		item			=left_child.item;
		left_child.item	=bak;
		
		balance_tree left_tree		=left_child;
		balance_tree left_left_tree	=left_child.left_child;
		balance_tree left_right_tree=left_child.right_child;
		balance_tree right_tree		=right_child;
		
		left_child					=left_left_tree;
		right_child					=left_tree;
		right_child.left_child		=left_right_tree;
		right_child.right_child		=right_tree;
		
		right_child.caculate_depth();
		caculate_depth();
	}
	private void rr()
	{
		balance_tree_item bak;
		bak				=item;
		item			=right_child.item;
		right_child.item=bak;
		
		balance_tree right_tree			=right_child;
		balance_tree left_tree			=left_child;
		balance_tree right_left_tree	=right_child.left_child;
		balance_tree right_right_tree	=right_child.right_child;
		
		left_child						=right_tree;
		right_child						=right_right_tree;
		left_child.left_child			=left_tree;
		left_child.right_child			=right_left_tree;
		
		left_child.caculate_depth();
		caculate_depth();
	}
	private void lr()
	{
		balance_tree_item bak;
		bak							=item;
		item						=left_child.right_child.item;
		left_child.right_child.item	=bak;
		
		balance_tree left_tree				=left_child;
		balance_tree left_right_tree		=left_child.right_child;
		balance_tree left_left_tree			=left_child.left_child;
		balance_tree left_right_left_tree	=left_child.right_child.left_child;
		balance_tree left_right_right_tree	=left_child.right_child.right_child;
		balance_tree right_tree				=right_child;
		
		left_child							=left_tree;
		right_child							=left_right_tree;
		left_child.left_child				=left_left_tree;
		left_child.right_child				=left_right_left_tree;
		right_child.left_child				=left_right_right_tree;
		right_child.right_child				=right_tree;
		
		left_child.caculate_depth();
		right_child.caculate_depth();
		caculate_depth();
		
	}
	private void rl()
	{
		balance_tree_item bak;
		bak							=item;
		item						=right_child.left_child.item;
		right_child.left_child.item	=bak;
		
		balance_tree left_tree				=left_child;
		balance_tree right_tree				=right_child;
		balance_tree right_left_tree		=right_child.left_child;
		balance_tree right_left_left_tree	=right_child.left_child.left_child;
		balance_tree right_left_right_tree	=right_child.left_child.right_child;
		balance_tree right_right_tree		=right_child.right_child;
		
		left_child					=right_left_tree;
		right_child					=right_tree;
		left_child.left_child		=left_tree;
		left_child.right_child		=right_left_left_tree;
		right_child.left_child		=right_left_right_tree;
		right_child.right_child		=right_right_tree;

		left_child.caculate_depth();
		right_child.caculate_depth();
		caculate_depth();
	}
	private int  caculate_balance()
	{
		if(left_child==null)
			if(right_child==null)
				return 0;
			else
				return right_child.depth+1;
		else
			if(right_child==null)
				return (-left_child.depth-1);
			else
				return right_child.depth-left_child.depth;
	}
	private void  caculate_depth()
	{
		if(left_child==null){
			if(right_child==null)
				depth=0;
			else
				depth=right_child.depth+1;
		}else{
			if(right_child==null)
				depth=left_child.depth+1;
			else{
				if(left_child.depth<right_child.depth)
					depth=right_child.depth+1;
				else
					depth=left_child.depth+1;
			}
		}
	}
	private balance_tree_item search(balance_tree_item my_search_item,int operation_code,balance_tree parent)
	{
		balance_tree_item ret_val;
		
		int compare_result=my_search_item.compare(item);

		if(compare_result==0){
			ret_val=item;
			if(operation_code>=0)
				return ret_val;
			if(left_child==null){
				if(right_child!=null){
					item		=right_child.item;
					depth		=right_child.depth;
					left_child	=right_child.left_child;
					right_child	=right_child.right_child;
				}else if(parent==null)
					item=null;
				else if(parent.left_child==this)
					parent.left_child=null;
				else
					parent.right_child=null;
				return ret_val;
			}
			if(right_child==null){
				item		=left_child.item;
				depth		=left_child.depth;
				right_child	=left_child.right_child;
				left_child	=left_child.left_child;
				return ret_val;
			}
			for(balance_tree t=left_child;;t=t.right_child)
				if(t.right_child==null){
					balance_tree_item p=t.item;
					t.item=item;
					item=p;
					ret_val=left_child.search(my_search_item,operation_code,this);
					break;
				}
		}else if(compare_result<0){
			if(left_child==null){
				if(operation_code<=0)
					return null;
				ret_val=null;
				left_child=new balance_tree(my_search_item);
			}else{
				int left_depth=left_child.depth;
				ret_val=left_child.search(my_search_item,operation_code,this);
				if(left_child!=null)
					if(left_child.depth==left_depth)
						return ret_val;
			}
		}else{
			if(right_child==null){
				if(operation_code<=0)
					return null;
				ret_val=null;
				right_child=new balance_tree(my_search_item);
			}else{
				int right_depth=right_child.depth;
				ret_val=right_child.search(my_search_item,operation_code,this);
				if(right_child!=null)
					if(right_child.depth==right_depth)
						return ret_val;
			}
		}
		
		caculate_depth();
		
		if((compare_result=caculate_balance())<=(-2)){
			if(left_child.caculate_balance()<0)
				ll();
			else
				lr();
		}else if(compare_result>=2){
			if(right_child.caculate_balance()<0)
				rl();
			else
				rr();
		}
		return ret_val;
	}
	public balance_tree_item search(balance_tree_item my_search_item,int operation_code)
	{
		return search(my_search_item,operation_code,null);
	}
}