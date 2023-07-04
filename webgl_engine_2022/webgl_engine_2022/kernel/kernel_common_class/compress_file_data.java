package kernel_common_class;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;

public class compress_file_data 
{
	private static boolean gzip_compress(File f,File gf,byte data_buf[])
	{
		FileInputStream fis=null;
		BufferedInputStream bis=null;
		FileOutputStream fos=null;
		GZIPOutputStream zos=null;	
		
		boolean ret_val;
		try{
			fis=new FileInputStream(f);
			bis=new BufferedInputStream(fis);
			fos=new FileOutputStream(gf);
			zos=new GZIPOutputStream(fos);			
		
			for(int len;(len=bis.read(data_buf,0,data_buf.length))>=0;)
				zos.write(data_buf,0,len);

			ret_val=false;
		}catch(Exception e){
			debug_information.println("Do GZIP file compress error:source\t",f.getAbsolutePath());
			debug_information.println("Do GZIP file compress error:target\t",gf.getAbsolutePath());
			debug_information.println("Do GZIP file compress error:error\t",e.toString());
			e.printStackTrace();
			ret_val=true;
		}
		
		if(zos!=null)
			try {
				zos.close();
			}catch(Exception e){
			}	
		if(fos!=null)
			try {
				fos.close();
			}catch(Exception e){
			}
		if(bis!=null)
			try {
				bis.close();
			}catch(Exception e){
			}
		if(fis!=null)
			try {
				fis.close();
			}catch(Exception e){
			}
		return ret_val;
	}
	private static boolean gzip_uncompress(File f,File gf,byte data_buf[])
	{
		FileInputStream fis=null;
		BufferedInputStream bis=null;
		GZIPInputStream gis=null;
		FileOutputStream fos=null;
		boolean ret_val;
		try{
			fis=new FileInputStream(gf);
			bis=new BufferedInputStream(fis);
			gis=new GZIPInputStream(bis);
			fos=new FileOutputStream(f);
						
			for(int len;(len=gis.read(data_buf,0,data_buf.length))>=0;)
				fos.write(data_buf,0,len);

			ret_val=false;
		}catch(Exception e){
			debug_information.println("Do GZIP file uncompress error:source\t",gf.getAbsolutePath());
			debug_information.println("Do GZIP file uncompress error:target\t",f.getAbsolutePath());
			debug_information.println("Do GZIP file uncompress error:error\t",e.toString());
			e.printStackTrace();
			ret_val=true;
		}
		if(fos!=null)
			try{
				fos.close();
			}catch(Exception e){
			}
		if(gis!=null)
			try{
				gis.close();
			}catch(Exception e){
			}	
		if(bis!=null)
			try{
				bis.close();
			}catch(Exception e){
			}	
		if(fis!=null)
			try{
				fis.close();
			}catch(Exception e){
			}
		return ret_val;
	}
	private static boolean deflate_compress(File f,File gf,byte data_buf[])
	{
		FileInputStream 		fis=null;
		BufferedInputStream 	bis=null;
		FileOutputStream 		fos=null;
		DeflaterOutputStream 	zos=null;
		
		boolean ret_val;
		try{
			fis=new FileInputStream(f);
			bis=new BufferedInputStream(fis);
			fos=new FileOutputStream(gf);
			zos=new DeflaterOutputStream(fos);
			
			for(int len;(len=bis.read(data_buf,0,data_buf.length))>=0;)
				zos.write(data_buf,0,len);

			ret_val=false;
		}catch(Exception e){
			debug_information.println("Do DEFLATE file compress error:source\t",f.getAbsolutePath());
			debug_information.println("Do DEFLATE file compress error:target\t",gf.getAbsolutePath());
			debug_information.println("Do DEFLATE file compress error:error\t",e.toString());
			e.printStackTrace();
			ret_val=true;
		}
		if(zos!=null)
			try {
				zos.close();
			}catch(Exception e){
			}	
		if(fos!=null)
			try {
				fos.close();
			}catch(Exception e){
			}	
		if(bis!=null)
			try {
				bis.close();
			}catch(Exception e){
			}	
		if(fis!=null)
			try {
				fis.close();
			}catch(Exception e){
			}
		return ret_val;
	} 
	private static boolean deflate_uncompress(File f,File gf,byte data_buf[])
	{
		FileInputStream 	fis=null;
		BufferedInputStream bis=null;
		DeflaterInputStream gis=null;
		FileOutputStream	fos=null;
		
		boolean ret_val;
		try{
			fis=new FileInputStream(gf);
			bis=new BufferedInputStream(fis);
			gis=new DeflaterInputStream(bis);
			fos=new FileOutputStream(f);
			
			for(int len;(len=gis.read(data_buf,0,data_buf.length))>=0;)
				fos.write(data_buf,0,len);

			ret_val=false;
		}catch(Exception e){
			debug_information.println("Do DEFLATE file uncompress error:source\t",gf.getAbsolutePath());
			debug_information.println("Do DEFLATE file uncompress error:target\t",f.getAbsolutePath());
			debug_information.println("Do DEFLATE file uncompress error:error\t",e.toString());
			e.printStackTrace();
			ret_val=true;
		}
		if(fos!=null)
			try {
				fos.close();
			}catch(Exception e){
			}
		if(gis!=null)
			try {
				gis.close();
			}catch(Exception e){
			}
		if(bis!=null)
			try {
				bis.close();
			}catch(Exception e){
			}
		if(fis!=null)
			try {
				fis.close();
			}catch(Exception e){
			}
		return ret_val;
	} 
	private static boolean br_compress(File f,File gf,byte data_buf[])
	{
		return true;
	} 
	private static boolean br_uncompress(File f,File gf,byte data_buf[])
	{
		return true;
	} 
	public static boolean do_compress(File f,File gf,
			int response_block_size,String compress_response_header)
	{
		byte data_buf[]=new byte[response_block_size];
		gf.delete();
		switch(compress_response_header) {
		case "br":
			return br_compress(f,gf,data_buf);
		case "gzip":
			return gzip_compress(f,gf,data_buf);
		case "deflate":
			return deflate_compress(f,gf,data_buf);
		default:
			return true;
		}
	}
	public static boolean do_uncompress(File f,File gf,
		int response_block_size,String compress_response_header)
	{
		byte data_buf[]=new byte[response_block_size];
		f.delete();
		switch(compress_response_header) {
		case "br":
			return br_uncompress(f,gf,data_buf);
		case "gzip":
			return gzip_uncompress(f,gf,data_buf);
		case "deflate":
			return deflate_uncompress(f,gf,data_buf);
		default:
			return true;
		}
	}
}
