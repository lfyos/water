package kernel_common_class;

public class format_change
{
	private static String cut_front_zero(String str)
	{
		for(int i=0,ni=str.length();i<ni;i++)
			if(str.charAt(i)!='0')
				return str.substring(i);
		return "0";
	}
	private static String cut_back_zero(String str)
	{
		for(int i=str.length()-1;i>=0;i--)
			if(str.charAt(i)!='0')
				return str.substring(0,i+1);
		return "0";
	}
	private static String format_exp_str(String exp_str)
	{
		String str;
		switch(exp_str.charAt(0)) {
		case '+':
			if((str=cut_front_zero(exp_str.substring(1))).compareTo("0")==0)
				return "";
			else
				return 'E'+str;
		case '-':
			if((str=cut_front_zero(exp_str.substring(1))).compareTo("0")==0)
				return "";
			else
				return "E-"+str;
		default:
			if((str=cut_front_zero(exp_str)).compareTo("0")==0)
				return "";
			else
				return 'E'+str;
		}
	}
	public static String double_to_decimal_string(double x,int precision,String format_string)
	{
		String pre_token=(x>=0)?"":"-";
		String str=String.format(format_string,Math.abs(x)).toUpperCase();
		int dot_index_id,exp_index_id,integer_length;
	
		if((dot_index_id=str.indexOf('.'))<0){
			if((exp_index_id=str.indexOf('E'))<0)
				return pre_token+cut_front_zero(str);
			else
				return pre_token+cut_front_zero(str.substring(0,exp_index_id))
							+format_exp_str(str.substring(exp_index_id+1));
		}else{
			if((exp_index_id=str.indexOf('E'))<0) {
				String integer_str=cut_front_zero(str.substring(0,dot_index_id));
				String fraction_str=cut_back_zero(str.substring(dot_index_id+1));
				if(fraction_str.compareTo("0")==0)
					return pre_token+integer_str;
				if((integer_length=integer_str.length())>=precision)
					return pre_token+integer_str;
				if((fraction_str.length()+integer_length)<=precision)
					return pre_token+integer_str+'.'+fraction_str;
				return pre_token+integer_str
						+'.'+fraction_str.substring(0,precision-integer_length);
			}else {
				String integer_str=cut_front_zero(str.substring(0,dot_index_id));
				String fraction_str=cut_back_zero(str.substring(dot_index_id+1,exp_index_id));
				String exp_str=format_exp_str(str.substring(exp_index_id+1));
				if(fraction_str.compareTo("0")==0)
					return pre_token+integer_str+exp_str;
				if((integer_length=integer_str.length())>=precision)
					return pre_token+integer_str+exp_str;
				if((fraction_str.length()+integer_length)<=precision)
					return pre_token+integer_str+'.'+fraction_str+exp_str;
				
				if(fraction_str.charAt(precision-integer_length)<'5')
					return pre_token+integer_str
							+'.'+fraction_str.substring(0,precision-integer_length)+exp_str;

				char fraction_char_array[]=fraction_str.substring(0,precision-integer_length).toCharArray();
				for(int i=fraction_char_array.length-1;i>=0;i++)
					if((++(fraction_char_array[i]))<='9')
						return pre_token+integer_str+'.'+(new String(fraction_char_array)).substring(0,i+1)+exp_str;

				return pre_token+(Integer.parseInt(integer_str)+1)+exp_str;
			}
		}
	}
	public static String double_to_decimal_string(double x,int precision)
	{
		return double_to_decimal_string(x,precision,"%f");
	}
}
