package kernel_common_class;

public class debug_information 
{
	public static String info="";
	private static String info_buffer="";
	
	public static void print(String x)
	{
		System.out.print(x);
		info_buffer+=x;
	}
	public static void println()
	{
		System.out.println();
		info=info_buffer;
		info_buffer="";
	}
	
	public static void println(String x)
	{
		print(x);println();
	}

	public static void print(int x)
	{
		print(Integer.toString(x));
	}
	public static void print(String str,int x)
	{
		print(str);print(x);
	}
	public static void println(int x)
	{
		println(Integer.toString(x));
	}
	public static void println(String str,int x)
	{
		print(str);println(x);
	}
	
	
	public static void print(long x)
	{
		print(Long.toString(x));
	}
	public static void print(String str,long x)
	{
		print(str);print(x);
	}
	public static void println(long x)
	{
		println(Long.toString(x));
	}
	public static void println(String str,long x)
	{
		print(str);println(x);
	}
	
	
	public static void print(double x)
	{
		print(Double.toString(x));
	}
	public static void print(String str,double x)
	{
		print(str);print(x);
	}
	public static void println(double x)
	{
		println(Double.toString(x));
	}
	public static void println(String str,double x)
	{
		print(str);println(x);
	}
	
	public static void print(String str,String x)
	{
		print(str);print(x);
	}
	public static void println(String str,String x)
	{
		print(str);println(x);
	}
}