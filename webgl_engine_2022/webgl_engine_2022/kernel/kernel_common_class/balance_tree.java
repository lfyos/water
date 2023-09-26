package kernel_common_class;

public class balance_tree<effective_balance_tree_item extends balance_tree_item> 
{
	private effective_balance_tree_item item;
	private balance_tree<effective_balance_tree_item> left_child,right_child;
	private int balance,depth;
	
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
	public balance_tree(effective_balance_tree_item my_item)
	{
		item=my_item;
		left_child=null;
		right_child=null;
		balance=0;
		depth=1;
	}
	private void caculate_depth_and_balance()
	{
		int left_depth	=(left_child ==null)?0:left_child. depth;
		int right_depth	=(right_child==null)?0:right_child.depth;
		balance=right_depth-left_depth;
		depth=((left_depth<right_depth)?right_depth:left_depth)+1;
	}
	private void ll()
	{
		effective_balance_tree_item bak;
		bak				=item;
		item			=left_child.item;
		left_child.item	=bak;
		
		balance_tree<effective_balance_tree_item> left_tree		=left_child;
		balance_tree<effective_balance_tree_item> left_left_tree	=left_child.left_child;
		balance_tree<effective_balance_tree_item> left_right_tree	=left_child.right_child;
		balance_tree<effective_balance_tree_item> right_tree		=right_child;
		
		left_child					=left_left_tree;
		right_child					=left_tree;
		right_child.left_child		=left_right_tree;
		right_child.right_child		=right_tree;
		
		right_child.caculate_depth_and_balance();
		caculate_depth_and_balance();
	}
	private void rr()
	{
		effective_balance_tree_item bak;
		bak					=item;
		item				=right_child.item;
		right_child.item	=bak;
		
		balance_tree<effective_balance_tree_item> right_tree		=right_child;
		balance_tree<effective_balance_tree_item> left_tree		=left_child;
		balance_tree<effective_balance_tree_item> right_left_tree	=right_child.left_child;
		balance_tree<effective_balance_tree_item> right_right_tree	=right_child.right_child;
		
		left_child						=right_tree;
		right_child						=right_right_tree;
		left_child.left_child			=left_tree;
		left_child.right_child			=right_left_tree;
		
		left_child.caculate_depth_and_balance();
		caculate_depth_and_balance();
	}
	private void lr()
	{
		effective_balance_tree_item bak;
		bak							=item;
		item						=left_child.right_child.item;
		left_child.right_child.item	=bak;
		
		balance_tree<effective_balance_tree_item> left_tree				=left_child;
		balance_tree<effective_balance_tree_item> left_right_tree		=left_child.right_child;
		balance_tree<effective_balance_tree_item> left_left_tree		=left_child.left_child;
		balance_tree<effective_balance_tree_item> left_right_left_tree	=left_child.right_child.left_child;
		balance_tree<effective_balance_tree_item> left_right_right_tree	=left_child.right_child.right_child;
		balance_tree<effective_balance_tree_item> right_tree			=right_child;
		
		left_child					=left_tree;
		right_child					=left_right_tree;
		left_child.left_child		=left_left_tree;
		left_child.right_child		=left_right_left_tree;
		right_child.left_child		=left_right_right_tree;
		right_child.right_child		=right_tree;
		
		left_child.caculate_depth_and_balance();
		right_child.caculate_depth_and_balance();
		caculate_depth_and_balance();
	}
	private void rl()
	{
		effective_balance_tree_item bak;
		bak							=item;
		item						=right_child.left_child.item;
		right_child.left_child.item	=bak;
		
		balance_tree<effective_balance_tree_item> left_tree				=left_child;
		balance_tree<effective_balance_tree_item> right_tree			=right_child;
		balance_tree<effective_balance_tree_item> right_left_tree		=right_child.left_child;
		balance_tree<effective_balance_tree_item> right_left_left_tree	=right_child.left_child.left_child;
		balance_tree<effective_balance_tree_item> right_left_right_tree	=right_child.left_child.right_child;
		balance_tree<effective_balance_tree_item> right_right_tree		=right_child.right_child;
		
		left_child					=right_left_tree;
		right_child					=right_tree;
		left_child.left_child		=left_tree;
		left_child.right_child		=right_left_left_tree;
		right_child.left_child		=right_left_right_tree;
		right_child.right_child		=right_right_tree;

		left_child.caculate_depth_and_balance();
		right_child.caculate_depth_and_balance();
		caculate_depth_and_balance();
	}
	private effective_balance_tree_item search(effective_balance_tree_item my_search_item,
		balance_tree<effective_balance_tree_item> parent,boolean not_do_append_flag,boolean not_do_delete_flag)
	{
		int compare_result;
		effective_balance_tree_item ret_val;

		if((compare_result=my_search_item.compare(item))==0){
			ret_val=item;
			if(not_do_delete_flag)
				return ret_val;
			if(left_child==null){
				if(right_child==null){
					if(parent!=null){
						if(parent.left_child==this)
							parent.left_child=null;
						else
							parent.right_child=null;
					}
					item=null;
					return ret_val;
				}
				item		=right_child.item;
				depth		=right_child.depth;
				balance		=right_child.balance;
				left_child	=right_child.left_child;
				right_child	=right_child.right_child;
				return ret_val;
			}
			if(right_child==null){
				item		=left_child.item;
				depth		=left_child.depth;
				balance		=left_child.balance;
				right_child	=left_child.right_child;
				left_child	=left_child.left_child;
				return ret_val;
			}
			if(left_child.depth>=right_child.depth){
				for(balance_tree<effective_balance_tree_item> tr,t=left_child;;t=tr)
					if((tr=t.right_child)==null){
						effective_balance_tree_item bak=t.item;
						t.item=item;
						item=bak;
						ret_val=left_child.search(my_search_item,this,not_do_append_flag,not_do_delete_flag);
						break;
					}
			}else{
				for(balance_tree<effective_balance_tree_item> tl,t=right_child;;t=tl)
					if((tl=t.left_child)==null){
						effective_balance_tree_item bak=t.item;
						t.item=item;
						item=bak;
						ret_val=right_child.search(my_search_item,this,not_do_append_flag,not_do_delete_flag);
						break;
					}
			}
		}else if(compare_result<0){
			if(left_child==null){
				if(not_do_append_flag)
					return null;
				left_child=new balance_tree<effective_balance_tree_item>(my_search_item);
				ret_val=null;
			}else{
				int old_depth=left_child.depth;
				ret_val=left_child.search(my_search_item,this,not_do_append_flag,not_do_delete_flag);
				if(left_child!=null)
					if(left_child.depth==old_depth)	
						return ret_val;
			}
		}else{
			if(right_child==null){
				if(not_do_append_flag)
					return null;
				right_child=new balance_tree<effective_balance_tree_item>(my_search_item);
				ret_val=null;
			}else{
				int old_depth=right_child.depth;
				ret_val=right_child.search(my_search_item,this,not_do_append_flag,not_do_delete_flag);
				if(right_child!=null)
					if(right_child.depth==old_depth)
						return ret_val;
			}
		}
		caculate_depth_and_balance();
		if(balance<=(-2)){
			if(left_child.balance<0)
				ll();
			else
				lr();
		}else if(balance>=2){
			if(right_child.balance<0)
				rl();
			else
				rr();
		}
		return ret_val;
	}
	public effective_balance_tree_item search(effective_balance_tree_item my_search_item,boolean do_append_flag,boolean do_delete_flag)
	{
		return search(my_search_item,null,do_append_flag?false:true,do_delete_flag?false:true);
	}
	public balance_tree<effective_balance_tree_item> get_left_child()
	{
		return left_child;
	}
	public balance_tree<effective_balance_tree_item> get_right_child()
	{
		return right_child;
	}
	public effective_balance_tree_item get_item()
	{
		return item;
	}
}