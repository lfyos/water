package kernel_common_class;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

import kernel_file_manager.file_reader;

public class common_writer 
{
	public common_writer flush()
	{
		return this;
	}
	public common_writer write_routine(byte data[],int offset,int length)
	{
		return this;
	}
	public common_writer print_routine(String str)
	{	
		if(str!=null){
			byte data[];
			try{
				data=str.getBytes(charset_name);
			}catch(Exception e){
				data=null;
			}
			if(data!=null){
				write_routine(data,0,data.length);
				output_data_length+=data.length;
			}
		}
		return this;
	}
	public long output_data_length;
	public String begin_str,separator_str,end_str,newline_str;
	private String charset_name;

	public common_writer(String my_charset_name,
			String my_begin_str,String my_separator_str,String my_end_str)
	{
		if((charset_name=my_charset_name)==null)
			charset_name=Charset.defaultCharset().name();
		
		output_data_length=0;
		begin_str=my_begin_str;
		separator_str=my_separator_str;
		end_str=my_end_str;
		newline_str="\n";
	}
	public String get_charset()
	{
		return charset_name;
	}
	public void set_charset(String my_charset_name)
	{
		charset_name=my_charset_name;
	}
	public common_writer set_pace(int new_space_number)
	{
		newline_str="\n";
		for(int i=0;i<new_space_number;i++)
			newline_str+=" ";
		print(newline_str);
		return this;
	}
	public common_writer print(String str)
	{
		if(str!=null)
			print_routine(str);
		return this;
	}
	public common_writer println()
	{
		print(newline_str);
		return this;
	}
	public common_writer print(int x)
	{
		print(Integer.toString(x));
		return this;
	}
	public common_writer print(long x)
	{
		print(Long.toString(x));
		return this;
	}
	public common_writer print(double x)
	{
		print(Double.toString(x));
		return this;
	}
	public common_writer print(String p[])
	{
		if(p!=null)
			for(int i=0,ni=p.length;i<ni;i++)
				print(p[i]);
		return this;
	}
	public common_writer print(int p[])
	{
		print(begin_str);
		for(int i=0,ni=p.length;i<ni;i++)
			if(i<=0)
				print(p[i]);
			else
				print(separator_str,p[i]);
		print(end_str);
		return this;
	}
	public common_writer print(long p[])
	{
		print(begin_str);
		for(int i=0,ni=p.length;i<ni;i++)
			if(i<=0)
				print(p[i]);
			else
				print(separator_str,p[i]);
		print(end_str);
		return this;
	}
	public common_writer print(double p[])
	{
		print(begin_str);
		for(int i=0,ni=p.length;i<ni;i++)
			if(i<=0)
				print(p[i]);
			else
				print(separator_str,p[i]);
		print(end_str);
		return this;
	}
	public common_writer println(String x)
	{
		print(x);println();return this;
	}
	public common_writer println(int x)
	{
		print(x);println();return this;
	}
	public common_writer println(long x)
	{
		print(x);println();return this;
	}
	public common_writer println(double x)
	{
		print(x);println();return this;
	}
	public common_writer println(String p[])
	{
		print(p);println();return this;
	}
	public common_writer println(int x[])
	{
		print(x);println();return this;
	}
	public common_writer println(long x[])
	{
		print(x);println();return this;
	}
	public common_writer println(double x[])
	{
		print(x);println();return this;
	}
	public common_writer print(String str,String x)
	{
		print(str);print(x);return this;
	}
	public common_writer print(String str,int x)
	{
		print(str);print(x);return this;
	}
	public common_writer print(String str,long x)
	{
		print(str);print(x);return this;
	}
	public common_writer print(String str,double x)
	{
		print(str);print(x);return this;
	}
	public common_writer print(String str,int x[])
	{
		print(str);print(x);return this;
	}
	public common_writer print(String str,long x[])
	{
		print(str);print(x);return this;
	}
	public common_writer print(String str,double x[])
	{
		print(str);print(x);return this;
	}
	public common_writer println(String str,String x)
	{
		print(str,x);println();return this;
	}
	public common_writer println(String str,int x)
	{
		print(str,x);println();return this;
	}
	public common_writer println(String str,long x)
	{
		print(str,x);println();return this;
	}
	public common_writer println(String str,double x)
	{
		print(str,x);println();return this;
	}
	public common_writer println(String str,int x[])
	{
		print(str,x);println();return this;
	}
	public common_writer println(String str,long x[])
	{
		print(str,x);println();return this;
	}
	public common_writer println(String str,double x[])
	{
		print(str,x);println();return this;
	}
	public common_writer print_charset_file(String file_name,String file_charset)
	{
		new file_reader(file_name,file_charset).get_text(this).close();
		return this;
	}
	public common_writer print_charset_file(String str,String file_name,String file_charset)
	{
		print(str);
		print_charset_file(file_name,file_charset);
		return this;
	}
	public common_writer print_file(String file_name,int buffer_length)
	{
		File f=new File(file_name);
		byte data_buf[]=new byte[buffer_length];
		try{
			FileInputStream 	s_stream=new FileInputStream(f);
			BufferedInputStream	s_buf	=new BufferedInputStream(s_stream);
			
			for(int one_length;(one_length=s_buf.read(data_buf))>=0;)
				write(data_buf,0,one_length);
	       
	        s_buf.close();
			s_stream.close();
		}catch(Exception e){
			e.printStackTrace();
			
			debug_information.println("print_file exception:\t",e.toString());
			debug_information.println(f.getAbsolutePath());
			
		}
		return this;
	}
	public common_writer print_file(String str,String file_name,int buffer_length)
	{
		print(str);
		print_file(file_name,buffer_length);
		return this;
	}
	public common_writer println_file(String file_name,int buffer_length)
	{
		print_file(file_name,buffer_length);
		println();
		return this;
	}
	public common_writer println_file(String str,String file_name,int buffer_length)
	{
		print(str);
		print_file(file_name,buffer_length);
		println();
		return this;
	}
	public common_writer write(byte data[],int offset,int length)
	{
		if(length>0){
			output_data_length+=length;
			write_routine(data,offset,length);
		}
		return this;
	}
	public common_writer write(byte data[])
	{
		return write(data,0,data.length);
	}
	public void close()
	{
	}
}
