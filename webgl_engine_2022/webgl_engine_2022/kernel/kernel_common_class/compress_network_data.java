package kernel_common_class;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.DeflaterOutputStream;

public class compress_network_data
{
	private static byte[]gzip_compress(byte data_buf[])
	{
		try{  
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			GZIPOutputStream zip	 =new GZIPOutputStream(bos);
			zip.write(data_buf);
			zip.flush();
			zip.finish();
			zip.close();
			byte ret_val[]=bos.toByteArray();
			bos.close();

			return ret_val;
		}catch(Exception e){
			debug_information.println(
					"Do GZIP network compress error:error\t",e.toString());
			e.printStackTrace();
			return null;
		}
	}
	private static byte[]deflate_compress(byte data_buf[])
	{
		try{  
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			DeflaterOutputStream  zip=new DeflaterOutputStream(bos);
			zip.write(data_buf);
			zip.flush();
			zip.finish();
			zip.close();
			byte ret_val[]=bos.toByteArray();
			bos.close();

			return ret_val;
		}catch(Exception e){
			debug_information.println(
					"Do DEFLATE network compress error:error\t",e.toString());
			e.printStackTrace();
			
			return null;
		}
	}
	private static byte[]br_compress(byte data_buf[])
	{
		return null;
	}
	public static byte[]do_compress(byte data_buf[],String compress_response_header)
	{
		switch(compress_response_header) {
		case "br":
			return br_compress(data_buf);
		case "gzip":
			return gzip_compress(data_buf);
		case "deflate":
			return deflate_compress(data_buf);
		default:
			return null;
		}
	}
}
