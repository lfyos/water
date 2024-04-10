package kernel_common_class;

public class cut_string 
{
	static public String do_cut(String my_str)
	{
		if(my_str==null)
			return "";
		
		for(boolean continue_flag=true;continue_flag&&(my_str.length()>0);)
			switch(my_str.charAt(0)) {
			case ' ':
			case '\t':
			case '/':
			case '\\':
			case '\r':
			case '\n':
				my_str=my_str.substring(1);
				break;
			default:
				continue_flag=false;
				break;
			}
		for(boolean continue_flag=true;continue_flag&&(my_str.length()>0);)
			switch(my_str.charAt(my_str.length()-1)) {
			case ' ':
			case '\t':
			case '/':
			case '\\':
			case '\r':
			case '\n':
				my_str=my_str.substring(0,my_str.length()-1);
				break;
			default:
				continue_flag=false;
				break;
			}
		return my_str;
	}
}
