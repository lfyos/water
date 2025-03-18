package driver_manipulator;

import kernel_component.component;
import kernel_component.component_array;
import kernel_scene.client_information;
import kernel_scene.scene_kernel;

public class operate_component_transparent
{
	private static void set_transparency(component comp,double transparency_value)
	{
		comp.uniparameter.transparency_value=transparency_value;
		for(int i=0,ni=comp.children_number();i<ni;i++)
			set_transparency(comp.children[i],transparency_value);
	}
	
	public static void do_transparency(scene_kernel sk,client_information ci)
	{
		String str;
		double transparency_value;
		component my_comp;
		component_array comp_array=new component_array();
		
		do{
			try{
				if((str=ci.request_response.get_parameter("transparency"))==null)
					return;
				transparency_value=Double.parseDouble(str);
				
				if((str=ci.request_response.get_parameter("component_name"))!=null) {
					if(str.length()>0){
						String request_charset=ci.request_response.implementor.get_request_charset();
						str=java.net.URLDecoder.decode(str,request_charset);
						str=java.net.URLDecoder.decode(str,request_charset);
						if((my_comp=sk.component_cont.search_component(str))!=null)
							comp_array.add_component(my_comp);
					}
					break;
				}
				if((str=ci.request_response.get_parameter("component_id"))!=null) {
					if((my_comp=sk.component_cont.get_component(Integer.decode(str)))!=null)
						comp_array.add_component(my_comp);
					break;
				}
				comp_array.add_selected_component(sk.component_cont.root_component,false);
				if(comp_array.comp_list.size()<=0) {
					str=ci.request_response.get_parameter("flag");
					switch((str==null)?"":str) {
					default:
					case "noselection_all":
						comp_array.add_component(sk.component_cont.root_component);
						break;
					case "noselection_none":
						break;
					}
				}
				break;
			}catch(Exception e){
				e.printStackTrace();
				
				kernel_common_class.debug_information.println("Parse parameter error in transparent_component",e.toString());
				
				return;
			}
		}while(false);

		for(int i=0,ni=comp_array.comp_list.size();i<ni;i++)
			set_transparency(comp_array.comp_list.get(i),transparency_value);
	}
}
