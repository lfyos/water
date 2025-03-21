package kernel_common_class;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class http_modify_string 
{
	private static  SimpleDateFormat data_format()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("EEE,dd MMM yyyy HH:mm:ss:SS 'GMT'",Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf;
	}
	public static String string(long t)
	{
		return data_format().format(new Date(t));
	}
	public static long parse(String str)
	{
		try {
			return data_format().parse(str).getTime();
		}catch(Exception e){
			return -1;
		}
	}
}