package kernel_common_class;

public class jason_string 
{
	public static String change_string(String str)
	{
		if(str==null)
			return "\"\"";
		str=str.replace("\\","\\\\").
				replace("\n","\\n\\r").
				replace("\"","\\\"").
				replace("\r","\\n\\r").
				replace("\t","\\t")
				
				;
		return "\""+str+"\"";
	}
}
