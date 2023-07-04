package kernel_network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Base64;

import kernel_common_class.debug_information;
import kernel_common_class.exclusive_file_mutex;
import kernel_common_class.nanosecond_timer;
import kernel_engine.engine_call_result;
import kernel_engine.system_parameter;
import kernel_common_class.common_reader;
import kernel_common_class.common_writer;
import kernel_common_class.compress_file_data;
import kernel_common_class.compress_network_data;
import kernel_file_manager.file_writer;

public class client_request_response extends common_writer
{
	public network_implementation implementor;
	public long request_time;
	public String response_content_type;
	public boolean display_content_flag;
	
	private network_parameter parameter[];
	private network_result first_result,last_result;
	
	private ByteArrayOutputStream output_stream;

	public void destroy()
	{
		if(implementor!=null) {
			implementor=null;
		}
		if(parameter!=null) {
			for(int i=0,ni=parameter.length;i<ni;i++)
				if(parameter[i]!=null)
					parameter[i]=null;
			parameter=null;
		}
		while(first_result!=null) {
			network_result p=first_result;
			first_result=first_result.next;
			p.next=null;
			p.result=null;
		}
		while(last_result!=null) {
			network_result p=last_result;
			last_result=last_result.next;
			p.next=null;
			p.result=null;
		}
		if(output_stream!=null) {
			output_stream=null;
		}
	}
	public client_request_response(String charset_name,network_implementation my_implementor)
	{
		super(charset_name,"[",",","]");
		
		implementor			=my_implementor;
		
		parameter			=null;
		first_result		=null;
		last_result			=null;
		
		output_stream		=new ByteArrayOutputStream();
		
		request_time=nanosecond_timer.absolute_nanoseconds();
		
		response_content_type="text/plain";
		
		display_content_flag=false;
	}
	private void inset_result(String result[])
	{
		if(last_result==null){
			last_result=new network_result(result);
			first_result=last_result;
		}else{
			last_result.next=new network_result(result);
			last_result=last_result.next;
		}
	}
	private void parameter_sort(network_parameter parameter[],int begin_pointer,int end_pointer,network_parameter tmp[])
	{
		if(begin_pointer<end_pointer){
			int mid_pointer=(begin_pointer+end_pointer)/2;
			parameter_sort(parameter,	begin_pointer,		mid_pointer,		tmp);
			parameter_sort(parameter,	mid_pointer+1,		end_pointer,		tmp);
		
			for(int i=begin_pointer,j=mid_pointer+1,k=0;;){
				if(i>mid_pointer){
					if(j>end_pointer)
						break;
					else
						tmp[k++]=parameter[j++];
				}else{
					if(j>end_pointer)
						tmp[k++]=parameter[i++];
					else{
						if(parameter[i].name.compareTo(parameter[j].name)<0)
							tmp[k++]=parameter[i++];
						else
							tmp[k++]=parameter[j++];
					}
				}
			}
			for(int i=0,j=begin_pointer;j<=end_pointer;)
				parameter[j++]=tmp[i++];
		}
	}
	public void install_parameter(network_parameter my_parameter[])
	{
		if((parameter=my_parameter)!=null)
			parameter_sort(parameter,0,parameter.length-1,new network_parameter[parameter.length]);	
	}
	public network_result get_network_result()
	{
		network_result ret_val	=first_result;
		parameter				=null;
		first_result			=null;
		last_result				=null;
		return ret_val;
	}
	public String get_parameter(String name)
	{
		if(parameter!=null)
			for(int begin_pointer=0,end_pointer=parameter.length-1;begin_pointer<=end_pointer;){
				int middle_pointer=(begin_pointer+end_pointer)/2;
				int compare_result=parameter[middle_pointer].name.compareTo(name);
				if(compare_result==0)
					return parameter[middle_pointer].value;
				if(compare_result<0)
					begin_pointer=middle_pointer+1;
				else
					end_pointer=middle_pointer-1;
			}
		return implementor.get_parameter(name);
	}
	public boolean get_boolean(String name,boolean default_value)
	{
		String str=get_parameter(name);
		switch((str==null)?"":(str.trim().toLowerCase())) {
		case "yes":
		case "true":
			return true;
		case "no":
		case "false":
			return false;
		default:
			return default_value;
		}
	}
	public int get_int(String name)
	{
		String str=get_parameter(name);
		return (str==null)?0:Integer.parseInt(str);
	}
	public double get_double(String name)
	{
		String str=get_parameter(name);
		return (str==null)?0:Double.parseDouble(str);
	}
	public void response_network_data(String compress_response_header,engine_call_result ecr,system_parameter system_par)
	{
		try{
			output_stream.close();
		}catch(Exception e){
			debug_information.println("Error 1 in response_network_data\t",e.toString());
			e.printStackTrace();
		}
		
		byte data_buf[]=output_stream.toByteArray();
		if(compress_response_header!=null){
			byte compress_data_buf[]=compress_network_data.do_compress(data_buf,compress_response_header);
			if(compress_data_buf==null)
				compress_response_header=null;
			else
				data_buf=compress_data_buf;
		}
		implementor.set_response_http_header(
			"public long[] response_network_data(String compress_response_header,engine_call_result ecr,system_parameter system_par)",
			get_charset(),response_content_type,compress_response_header,ecr.cors_string,ecr.date_string,
			Long.toString(system_par.file_buffer_expire_time_length));
		implementor.response_binary_data("response_network_data error",data_buf,data_buf.length);
		implementor.terminate_response_binary_data("Error 3 in response_network_data");
		
		return ;
	}
	
	private String range_string;
	
	private long[] get_range(long file_length,long max_length)
	{	
		range_string="";
		if(file_length<=0)
			return null;
		if(file_length<=max_length)
			return new long[] {0,file_length};
		
		String str;
		if((str=implementor.get_header("Range"))==null)
			return new long[] {0,file_length};

		range_string=str;
		str=str.toLowerCase();
		
		final String sub_str="bytes=";
		int index_id=str.indexOf(sub_str);
		if(index_id<0)
			return new long[] {0,file_length};
		str=str.substring(sub_str.length()+index_id).trim();
		if((index_id=str.indexOf(','))>=0)
			str=str.substring(0,index_id).trim();
		if((index_id=str.indexOf('-'))<0)
			return new long[] {0,file_length};

		String begin_str=str.substring(0,index_id).trim();
		String end_str=str.substring(index_id+1).trim();

		long begin_pointer,end_pointer;
		
		if(begin_str.length()<=0) {
			if(end_str.length()<=0)
				return new long[] {0,file_length};
			begin_pointer=file_length-Long.decode(end_str);
			end_pointer=file_length;
		}else if(end_str.length()<=0) {
			begin_pointer=Long.decode(begin_str);
			end_pointer=begin_pointer+max_length;
		}else {
			begin_pointer=Long.decode(begin_str);
			end_pointer=Long.decode(end_str)+1;
		}
		
		if(begin_pointer<0)
			begin_pointer=0;
		if(end_pointer>file_length)
			end_pointer=file_length;
		if(begin_pointer>=end_pointer)
			return null;
		if((begin_pointer==0)&&(end_pointer==file_length))
			return new long[] {0,file_length};
		
		str ="bytes "+Long.toString(begin_pointer);
		str+="-"	 +Long.toString(end_pointer-1);
		str+="/"	 +Long.toString(file_length);

		implementor.set_status_code(206);
		implementor.set_header("Content-Range",str);

		range_string+="\n"+str;
		
		return new long[] {begin_pointer,end_pointer};
	}
	
	public void response_file_data(
		String compress_response_header,engine_call_result ecr,system_parameter system_par)
	{
		try{
			output_stream.close();
		}catch(Exception e){
			debug_information.println("Error 1 in response_file_data\t",e.toString());
			e.printStackTrace();
		}
		
		String file_name=ecr.file_name,network_data_charset=ecr.file_charset,error_msg;
		
		error_msg ="\nfile_name is "+file_name;
		error_msg ="\file_charset is "+ecr.file_charset;
		error_msg+="\ncharset_file_name is "+ecr.charset_file_name;
		error_msg+="\ncompress_file_name is "+ecr.compress_file_name;
		error_msg+="\ncharset_name is "+system_par.network_data_charset;

		if((ecr.charset_file_name!=null)&&(ecr.file_charset!=null))
			if(system_par.network_data_charset.compareTo(ecr.file_charset)!=0){
				exclusive_file_mutex efm=exclusive_file_mutex.lock(
					ecr.charset_file_name+".lock","wait for create charset file name:"+ecr.charset_file_name);
				try {
					long s=new File(file_name).lastModified();
					long t=new File(ecr.charset_file_name).lastModified();
					if(s>=t)
						file_writer.charset_copy(file_name,ecr.file_charset,
								ecr.charset_file_name,system_par.network_data_charset);
				}catch(Exception e) {
					debug_information.println("response_file_data exception 1\t",e.toString());
					e.printStackTrace();
				}
				
				efm.unlock();
				
				file_name=ecr.charset_file_name;
				network_data_charset=system_par.network_data_charset;
			}
		File f=new File(file_name);
		
		long file_length=f.length();
		if(file_length<=0) {
			debug_information.println("response_file_data find file length is ZERO:	",file_name);
			return;
		}

		if((ecr.compress_file_name!=null)&&(compress_response_header!=null))
			switch(ecr.compress_file_name){
			case "gzip":
			case "deflate":
			case "br":
				compress_response_header=ecr.compress_file_name;
				break;
			default:
				String compress_file_name=ecr.compress_file_name+"."+compress_response_header;
				exclusive_file_mutex efm=exclusive_file_mutex.lock(
					compress_file_name+".lock","wait for create compress file name:"+compress_file_name);
				try {
					File gf=new File(compress_file_name);
					if(f.lastModified()<gf.lastModified())
						f=gf;
					else{
						if(compress_file_data.do_compress(f,gf,system_par.response_block_size,compress_response_header))
							compress_response_header=null;
						else
							f=new File(compress_file_name);
					}
				}catch(Exception e) {
					debug_information.println("response_file_data exception 2\t",e.toString());
					e.printStackTrace();
				}
				
				efm.unlock();
				break;
			}

		long file_range[];
		if((file_range=get_range(f.length(),system_par.response_block_size))==null)
			return;

		error_msg=range_string+"\n"+error_msg;
		implementor.set_response_http_header(
			error_msg,network_data_charset,response_content_type,
			compress_response_header,ecr.cors_string,ecr.date_string,
			Long.toString(system_par.file_buffer_expire_time_length));

		byte data_buf[]=new byte[system_par.response_block_size];
		FileInputStream 	s_stream=null;
		BufferedInputStream	s_buf=null;
		try{
			s_stream=new FileInputStream(f);
			s_buf	=new BufferedInputStream(s_stream);	
			if(file_range[0]!=0)
				s_buf.skip(file_range[0]);
			
			for(long i=file_range[0],length;i<=file_range[1];i+=length){
				length=file_range[1]-i+1;
				if(data_buf.length>length)
					data_buf=new byte[(int)length];
				if((length=s_buf.read(data_buf))<=0)
					break;
				if(implementor.response_binary_data(error_msg,data_buf,(int)length))
					break;
			}
		}catch(Exception e){
			debug_information.println("Do response error:source\t",file_name);
			debug_information.println("Do response error:target\t",ecr.compress_file_name);
			debug_information.println("Do response error:error\t",e.toString());
			e.printStackTrace();
		}
		if(s_buf!=null)
			try {
				s_buf.close();
			}catch(Exception e) {
				;
			}
		if(s_stream!=null)
			try{
				s_stream.close();
			}catch(Exception e) {
				;
			}
		implementor.terminate_response_binary_data(error_msg);
		return;
	}
	public common_writer print_routine(String str)
	{
		if(parameter!=null)
			inset_result(new String[] {str,get_charset()});
		else
			super.print_routine(str);
		return this;
	}
	public byte[]get_png_image_data()
	{
		InputStream stream;	
		if((stream=implementor.get_content_stream())==null)
			return null;

		String str;
		Base64.Decoder decoder=Base64.getDecoder();
		if((str=(new common_reader(stream,implementor.get_request_charset())).get_text())==null)
			debug_information.println("Base64.Decoder String is null");
		else{
			str=str.replaceFirst("data:image/png;base64,","").replaceAll(" ","+");
			try{
				return decoder.decode(str);
			}catch(Exception e) {
				debug_information.println("Base64.Decoder fail");
				debug_information.println(e.toString());
				e.printStackTrace();
			}
		}
		return null;
	}
	public common_writer flush()
	{
		try {
			output_stream.flush();
		}catch(Exception e) {
			;
		}
		return this;
	}
	public common_writer reset()
	{
		output_stream.reset();
		return this;
	}
	public common_writer write_routine(byte data[],int offset,int length)
	{
		try{
			output_stream.write(data,offset,length);
			if(display_content_flag)
				debug_information.print(new String(data,get_charset()));
		}catch(Exception e){
			String str="write_routine int client_request_response exception:\t";
			debug_information.println(str,e.toString());
			e.printStackTrace();
		}
		return this;
	}
}
