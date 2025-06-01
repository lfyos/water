package kernel_common_class;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

public class http_date_string 
{
	private SimpleDateFormat simple_data_format=null;
	
	public http_date_string()
	{
		simple_data_format=new SimpleDateFormat("EEE,dd MMM yyyy HH:mm:ss:SS 'GMT'",Locale.US);
		simple_data_format.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	public String date_string(long t)
	{
		return simple_data_format.format(new Date(t));
	}
	public long parse(String str)
	{
		try {
			return simple_data_format.parse(str).getTime();
		}catch(Exception e){
			return -1;
		}
	}
}
