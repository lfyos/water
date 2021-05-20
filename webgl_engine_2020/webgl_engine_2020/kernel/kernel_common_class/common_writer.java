package kernel_common_class;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

public class common_writer 
{
	private byte writer_recorder[][];
	private int writer_recorder_number;
	
	public void set_recorder()
	{
		writer_recorder=new byte[1000][];
		writer_recorder_number=0;
	}
	
	public byte[]get_recorder()
	{
		if(writer_recorder==null)
			return null;
		int number=0;
		for(int i=0;i<writer_recorder_number;i++)
			number+=writer_recorder[i].length;
		byte ret_val[]=new byte[number];
		number=0;
		for(int i=0;i<writer_recorder_number;i++)
			for(int j=0,nj=writer_recorder[i].length;j<nj;j++)
				ret_val[number++]=writer_recorder[i][j];
		return ret_val;
	}
	private void append_recorder(byte data[])
	{
		if(writer_recorder.length<=writer_recorder_number) {
			byte bak[][]=writer_recorder;
			writer_recorder=new byte[writer_recorder.length+1000][];
			for(int i=0,ni=bak.length;i<ni;i++)
				writer_recorder[i]=bak[i];
		}
		writer_recorder[writer_recorder_number++]=data;
	}
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
				if(writer_recorder!=null)
					append_recorder(data);
			}
		}
		return this;
	}
	public long output_data_length;
	public String begin_str,separator_str,end_str,newline_str;
	private String charset_name;

	public String get_charset()
	{
		return charset_name;
	}
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
		
		writer_recorder=null;
		writer_recorder_number=0;
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
	public common_writer print_file(String file_name)
	{
		File f=new File(file_name);
		byte[] data_buf=new byte[16*1024];
		try{
			FileInputStream 	s_stream=new FileInputStream(f);
			BufferedInputStream	s_buf	=new BufferedInputStream(s_stream);
			
			for(int one_length;(one_length=s_buf.read(data_buf))>=0;)
				write(data_buf,0,one_length);
	       
	        s_buf.close();
			s_stream.close();
		}catch(Exception e){
			debug_information.println("print_file exception:\t",e.toString());
			debug_information.println(f.getAbsolutePath());
			e.printStackTrace();
		}
		return this;
	}
	public common_writer print_file(String str,String file_name)
	{
		print(str);
		print_file(file_name);
		return this;
	}
	public common_writer println_file(String file_name)
	{
		print_file(file_name);
		println();
		return this;
	}
	public common_writer println_file(String str,String file_name)
	{
		print(str);
		print_file(file_name);
		println();
		return this;
	}
	public common_writer write(byte data[],int offset,int length)
	{
		if(length>0){
			output_data_length+=length;
			write_routine(data,offset,length);
			
			if(writer_recorder!=null) {
				byte append_data[]=new byte[length];
				for(int i=0,j=offset;i<length;i++,j++)
					append_data[i]=data[j];
				append_recorder(append_data);
			}
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
