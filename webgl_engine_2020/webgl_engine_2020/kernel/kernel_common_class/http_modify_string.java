package kernel_common_class;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class http_modify_string 
{
	private static  SimpleDateFormat data_format()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf;
	}
	public static String string(Date da)
	{
		return data_format().format(da);
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