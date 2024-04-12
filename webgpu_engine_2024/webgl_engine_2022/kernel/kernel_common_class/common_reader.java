package kernel_common_class;

import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.Charset;

public class common_reader
{
	public long lastModified_time;
	
	private String charset_name;
	public String get_charset()
	{
		return charset_name;
	}
	private boolean mark_flag,error_flag;
	private string_link_list first_string,last_string,queue_string;
	
	private InputStream orginal_input_stream,input_stream;
	private BufferedInputStream buffered_input_stream;
	private InputStreamReader input_stream_reader;
	private BufferedReader reader;
	private Scanner scanner;
	
	public static InputStream create_stream(InputStream my_input_stream)
	{
		return my_input_stream;
	}
	public common_reader(InputStream my_input_stream,String my_charset_name)
	{
		if((charset_name=my_charset_name)==null)
			charset_name=Charset.defaultCharset().name();
		
		lastModified_time=0;

		mark_flag=false;
		error_flag=true;
		
		first_string=null;
		last_string=null;
		queue_string=null;
		
		input_stream_reader=null;
		reader=null;
		scanner=null;
		
		orginal_input_stream=my_input_stream;
		if(orginal_input_stream==null){
			buffered_input_stream=null;
			return;
		}
		buffered_input_stream=new BufferedInputStream(orginal_input_stream);
		if((input_stream=create_stream(buffered_input_stream))==null) {
			try {
				buffered_input_stream.close();
				orginal_input_stream.close();
			}catch(Exception e) {
				;
			}
			buffered_input_stream=null;
			orginal_input_stream=null;
			return;
		}

		try{
			input_stream_reader=new InputStreamReader(input_stream,charset_name);
		}catch(Exception e){
			e.printStackTrace();
			
			try {
				input_stream.close();
				buffered_input_stream.close();
				orginal_input_stream.close();
			}catch(Exception e_close) {
				;
			}
			input_stream=null;
			buffered_input_stream=null;
			orginal_input_stream=null;
			
			debug_information.println("file_reader reset exception:\t",e.toString());
		
			return;
		}
		reader=new BufferedReader(input_stream_reader);
		scanner=new Scanner(reader);
		error_flag=false;

		return;
	}
	public void close()
	{
		mark_flag=false;
		first_string=null;
		last_string=null;
		queue_string=null;

		if(scanner!=null){
			scanner.close();
			scanner=null;
		}
		if(reader!=null){
			try{
				reader.close();
			}catch(Exception e){
				e.printStackTrace();
				
				debug_information.println("file_reader close exception:\t",e.toString());
				
			}
			reader=null;
		}
		if(input_stream_reader!=null) {
			try{
				input_stream_reader.close();
			}catch(Exception e) {
				;
			}
			input_stream_reader=null;
		};
		if(input_stream!=null){
			try{
				input_stream.close();
			}catch(Exception e) {
				;
			}
			input_stream=null;
		}
		if(buffered_input_stream!=null){
			try{
				buffered_input_stream.close();
			}catch(Exception e) {
				;
			}
			buffered_input_stream=null;
		}
		if(orginal_input_stream!=null){
			try{
				orginal_input_stream.close();
			}catch(Exception e) {
				;
			}
			orginal_input_stream=null;
		}
	}
	public boolean error_flag()
	{
		return error_flag;
	}
	public void clear_error()
	{
		error_flag=false;
	}
	public boolean eof()
	{
		if(error_flag)
			return true;
		if(queue_string!=null)
			return false;
		try{
			return scanner.hasNext()?false:true;
		}catch(Exception e){
			error_flag=true;
			return true;
		}
	}
	private void put_in_link_list(String str)
	{
		if(mark_flag){
			if(last_string==null){
				last_string=new string_link_list(str,error_flag);
				first_string=last_string;
			}else{
				last_string.next_list=new string_link_list(str,error_flag);
				last_string=last_string.next_list;
			}
		}
	}
	public void push_string_array(String str_array[])
	{
		for(int i=str_array.length-1;i>=0;i--)
			queue_string=new string_link_list(str_array[i],queue_string);
	}
	public void mark_start()
	{
		mark_flag=true;
		first_string=null;
		last_string=null;
	}
	public void mark_terminate(boolean rescroll_flag)
	{
		if(rescroll_flag){
			if(last_string!=null){
				last_string.next_list=queue_string;
				queue_string=first_string;
				
				clear_error();
			}
		}
		mark_flag=false;
		first_string=null;
		last_string=null;
	}
	public byte get_byte()
	{
		try {
			return scanner.nextByte();
		}catch(Exception e){
			return 0;
		}
	}
	public String get_line()
	{
		try{
			return scanner.nextLine();
		}catch(Exception e){
			return null;
		}
	}
	public String get_no_empty_line()
	{
		while(true){
			String ret_val=get_line();
			if(ret_val==null)
				return null;
			if(ret_val.trim().length()>0)
				return ret_val;
		}
	}
	public String get_text()
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;!(eof());) {
			String str=get_line();
			if(str==null)
				continue;
			if((i++)>0)
				buf.append('\n');
			buf.append(str);
		}
		return buf.toString();
	}
	public int get_text(common_writer cw)
	{
		return get_text(cw,"");
	}
	public int get_text(common_writer cw,String pre_str)
	{
		int ret_val=0;
		
		for(String str;!(eof());)
			if((str=get_line())!=null) {
				cw.println(pre_str,str);
				ret_val+=str.length();
			}
		return ret_val;
	}
	public void get_text(String pre_string,common_writer cw)
	{
		for(String str;!(eof());)
			if((str=get_line())!=null)
				cw.println(pre_string,str);
	}
	public String get_string()
	{
		String ret_val;
		for(int state_flag=0,str_len;;){
			if(queue_string!=null){
				ret_val=queue_string.str;
				error_flag=queue_string.error_flag;
				queue_string=queue_string.next_list;
			}else {
				try{
					if((ret_val=scanner.next())==null)
						return null;
				}catch(Exception e){
					error_flag=true;
					return null;
				}
			}
			if((str_len=ret_val.length())<2){
				if(state_flag>0)
					continue;
				else
					break;
			}
			if(ret_val.charAt(0)=='/')
				if(ret_val.charAt(1)=='*')
					state_flag=(state_flag<=0)?1:(state_flag+1);
			if(ret_val.charAt(str_len-2)=='*')
				if(ret_val.charAt(str_len-1)=='/'){
					state_flag=(state_flag<=0)?0:(state_flag-1);
					continue;
				}
			if(state_flag>0)
				continue;
			if(ret_val.charAt(0)=='/')
				if(ret_val.charAt(1)=='/'){
					try{
						scanner.nextLine();
					}catch(Exception e){
						;
					}
					continue;
				}
			break;
		}
		put_in_link_list(ret_val);
		return ret_val;
	}
	public boolean get_boolean()
	{
		String str;
		if((str=get_string())!=null)
			switch(str.toLowerCase()){
			case "true":
			case "yes":
				return true;
			case "false":
			case "no":
				return false;
			default:
				break;
			}
		return false;
	}
	public int get_int()
	{
		try{
			return Integer.decode(get_string());
		}catch(Exception e){
			error_flag=true;
			return 0;
		}
	}
	public long get_long()
	{
		try{
			return Long.decode(get_string());
		}catch(Exception e){
			error_flag=true;
			return 0;
		}
	}
	public double get_double()
	{
		try{
			return Double.parseDouble(get_string());
		}catch(Exception e){
			error_flag=true;
			return 0;
		}
	}
}