package kernel_component;

public class component_link_list {
		public component comp;
		public int driver_id;
		public component_link_list next_list_item;
		
		public void destroy()
		{
			for(component_link_list p=next_list_item;p!=null;){
				component_link_list pp=p;
				p=p.next_list_item;
				if(pp.comp!=null)
					pp.comp=null;
				pp.next_list_item=null;
			}
		}
		public component_link_list(component my_comp,int my_driver_id,component_link_list my_next_list_item)
		{
			comp=my_comp;
			driver_id=my_driver_id;
			next_list_item=my_next_list_item;
		}
}
