package driver_manipulator;

import kernel_component.component;
import kernel_component.component_array;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;

public class transparent_component
{
	private static void set_transparency(component comp,double transparency_value)
	{
		comp.uniparameter.transparency_value=transparency_value;
		for(int i=0,ni=comp.children_number();i<ni;i++)
			set_transparency(comp.children[i],transparency_value);
	}
	public static void do_transparency(engine_kernel ek,client_information ci)
	{
		String str;
		double transparency_value;
		component my_comp;
		component_array comp_array=new component_array(ek.component_cont.root_component.component_id+1);
		
		try{
			if((str=ci.request_response.get_parameter("transparency"))==null)
				return;
			transparency_value=Double.parseDouble(str);
		
			if((str=ci.request_response.get_parameter("component"))!=null)
				if(str.length()>0){
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
					str=java.net.URLDecoder.decode(str,ek.system_par.network_data_charset);
					if((my_comp=ek.component_cont.search_component(str))!=null)
						comp_array.add_component(my_comp);
				}
			if(comp_array.component_number<=0)
				if((str=ci.request_response.get_parameter("component_id"))!=null)
					if((my_comp=ek.component_cont.get_component(Integer.decode(str)))!=null)
						comp_array.add_component(my_comp);
		}catch(Exception e){
			kernel_common_class.debug_information.println("Parse parameter error in transparent_component",e.toString());
			e.printStackTrace();
			return;
		}
		
		if(comp_array.component_number<=0)
			comp_array.add_selected_component(ek.component_cont.root_component);
		
		if(comp_array.component_number<=0)
			set_transparency(ek.component_cont.root_component,transparency_value);
		else
			for(int i=0,ni=comp_array.component_number;i<ni;i++)
				set_transparency(comp_array.comp[i],transparency_value);
	}
}
