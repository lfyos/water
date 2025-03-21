package format_convert;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;

public class stl_converter 
{
	private byte a,b,c,d;
	private double box_data[];
	private int triangle_number;
	
	private int ascii_search_for_header(file_reader f,String head_string)
	{
		int number=0;
		for(;!(f.eof());number++) {
			String str=f.get_string();
			if(str!=null)
				if(str.toLowerCase().compareTo(head_string)==0)
					return number;
		}
		return -1;
	}
	private double []ascii_get_data(file_reader f,String header_string)
	{
		ascii_search_for_header(f,header_string);
		double ret_val[]=new double[] {0,0,0,1};
		for(int i=0;i<3;i++)
			ret_val[i]=f.get_double();
		return ret_val;
	}
	
	private void output_triangle(double normal[],double vertex1[],double vertex2[],double vertex3[],file_writer fw)
	{
		double vertex[][]=new double[][] {vertex1,vertex2,vertex3};
		
		fw.println();
		fw.println("/*	triangle:"+(triangle_number++)+"	*/");
		
		fw.println("	/*	material		*/	0	0	0	0");
		fw.println("	/*	vertex number	*/	3");
		
		for(int i=0,ni=vertex.length;i<ni;i++) {
			fw.println("	/*	"+i+".location		*/	"
					+vertex[i][0]	+"	"+vertex[i][1]	+"	"+vertex[i][2]	+"	1");
			fw.println("	/*	"+i+".normal		*/	"
					+normal [0]		+"	"+normal [1]	+"	"+normal [2]	+"	1");
			
			if(box_data==null)
				box_data=new double[] 
				{
						vertex[i][0],vertex[i][1],vertex[i][2],
						vertex[i][0],vertex[i][1],vertex[i][2]
				};
			else {
				box_data[0]=(box_data[0]<vertex[i][0])?box_data[0]:vertex[i][0];
				box_data[1]=(box_data[1]<vertex[i][1])?box_data[1]:vertex[i][1];
				box_data[2]=(box_data[2]<vertex[i][2])?box_data[2]:vertex[i][2];
				
				box_data[3]=(box_data[3]>vertex[i][0])?box_data[3]:vertex[i][0];
				box_data[4]=(box_data[4]>vertex[i][1])?box_data[4]:vertex[i][1];
				box_data[5]=(box_data[5]>vertex[i][2])?box_data[5]:vertex[i][2];
			}
		}
	}
	private void ascii_processor(String mesh_file_name,String mesh_charset,file_writer fw)
	{	
		file_reader fr=new file_reader(mesh_file_name,mesh_charset);
		
		ascii_search_for_header(fr,"solid");
		while(!(fr.eof()))
			if(ascii_search_for_header(fr,"facet")>=0){
				double normal[]=ascii_get_data(fr,"normal");
				if(ascii_search_for_header(fr,"outer")>=0)
					if(ascii_search_for_header(fr,"loop")>=0){
						double vertex1[]=ascii_get_data(fr,"vertex");
						double vertex2[]=ascii_get_data(fr,"vertex");
						double vertex3[]=ascii_get_data(fr,"vertex");
						if(ascii_search_for_header(fr,"endloop")>=0)
							if(ascii_search_for_header(fr,"endfacet")>=0)
								output_triangle(normal,vertex1,vertex2,vertex3,fw);
					}
			}
		
		fr.close();
	}
	private void binary_switch_four_byte(DataInputStream dr,DataOutputStream dw)
	{
		try{
			a=dr.readByte();	b=dr.readByte();	c=dr.readByte();	d=dr.readByte();
			dw.writeByte(d);	dw.writeByte(c);	dw.writeByte(b);	dw.writeByte(a);
		}catch(Exception e){
			a=0;	b=0;	c=0;	d=0;
		}
	}
	private void binary_change_format(String file_name,String new_file_name)
	{
		FileInputStream 		fr=null;
		BufferedInputStream 	br=null;
		DataInputStream 		dr=null;
		
		FileOutputStream		fw=null;
		BufferedOutputStream	bw=null;
		DataOutputStream		dw=null;
		
		try{
			fr=new FileInputStream(file_name);
			br=new BufferedInputStream(fr);
			dr=new DataInputStream(br);
			
			fw=new FileOutputStream(new_file_name);
			bw=new BufferedOutputStream(fw);
			dw=new DataOutputStream(bw);
			
			dr.skipBytes(80);
			
			binary_switch_four_byte(dr,dw);
			
			for(int i=0,number=((d*256+c)*256+b)*256+a;i<number;i++){
				for(int j=0;j<4;j++)
					for(int k=0;k<3;k++)
						binary_switch_four_byte(dr,dw);
				byte e=dr.readByte();
				byte f=dr.readByte();
				dr.skipBytes(256*f+e);
			}
		}catch(Exception e){
			e.printStackTrace();
			
			debug_information.println(e.toString());
			
		}
		if(dw!=null)
			try{
				dw.close();
			}catch(Exception e){
				;
			}
		if(bw!=null)
			try{
				bw.close();
			}catch(Exception e){
				;
			}
		if(fw!=null)
			try{
				fw.close();
			}catch(Exception e){
				;
			}
		if(dr!=null)
			try{
				dr.close();
			}catch(Exception e){
				;
			}
		if(br!=null)
			try{
				br.close();
			}catch(Exception e){
				;
			}
		if(fr!=null)
			try{
				fr.close();
			}catch(Exception e){
				;
			}
	}
	
	private void binary_processor(String file_name,file_writer fw)
	{
		FileInputStream 		fr=null;
		BufferedInputStream 	br=null;
		DataInputStream 		dr=null;
		
		try{
			fr=new FileInputStream(file_name);
			br=new BufferedInputStream(fr);
			dr=new DataInputStream(br);
			
			int facet_number=dr.readInt();
			for(int face_id=0;face_id<facet_number;face_id++){
				double stl_data[][]=new double[4][];
				for(int j=0;j<4;j++){
					stl_data[j]=new double[4];
					stl_data[j][3]=1.0;
					for(int k=0;k<3;k++)
						stl_data[j][k]=dr.readFloat();
				}
				output_triangle(stl_data[0],stl_data[1],stl_data[2],stl_data[3],fw);
			}
		}catch(Exception e){
			e.printStackTrace();
			
			debug_information.println(e.toString());
			
		}
		if(dr!=null)
			try{
				dr.close();
			}catch(Exception e){
				;
			}
		if(br!=null)
			try{
				br.close();
			}catch(Exception e){
				;
			}
		if(fr!=null)
			try{
				fr.close();
			}catch(Exception e){
				;
			}
	}
	
	private boolean test_format(String mesh_file_name,String mesh_charset)
	{
		file_reader fr=new file_reader(mesh_file_name,mesh_charset);
		if(ascii_search_for_header(fr,"solid")>=0)
			if(ascii_search_for_header(fr,"facet")>=0)
				if(ascii_search_for_header(fr,"normal")==0)
					if(ascii_search_for_header(fr,"outer")>=0)
						if(ascii_search_for_header(fr,"loop")==0)
							if(ascii_search_for_header(fr,"endloop")>=0)
								if(ascii_search_for_header(fr,"endfacet")==0) {
									fr.close();
									return true;
								}
		fr.close();
		return false;
	}
	public stl_converter(
			String mesh_file_name,String mesh_charset,
			String target_file_name,String target_charset)
	{
		file_writer.file_delete(target_file_name+".edge");
		file_writer.file_touch(target_file_name+".edge",true);
		
		file_writer fw=new file_writer(target_file_name+".face",target_charset);
		fw.println("/*	body:0	face:0	*/");
		
		box_data=null;
		triangle_number=0;
		
		if(test_format(mesh_file_name,mesh_charset)) 
			ascii_processor(mesh_file_name,mesh_charset,fw);
		else{
			String binary_file_name=mesh_file_name+".binary";
			binary_change_format(mesh_file_name,binary_file_name);
			binary_processor(binary_file_name,fw);
			file_writer.file_delete(binary_file_name);
		}
		fw.close();
		
		fw=new file_writer(target_file_name,target_charset);
		
		fw.println("/*	version					*/	2021.07.15");
		fw.println("/*	origin material			*/	0	0	0	0");
		fw.println("/*	default material		*/	0	0	0	0");
		fw.println("/*	origin  vertex_location_extra_data	*/	1");
		fw.println("/*	default vertex_location_extra_data	*/	1");
		fw.println("/*	default vertex_normal_extra_data	*/	1");
		fw.println("/*	max_attribute_number	*/	0");
		fw.println();

		fw.println("/*	body_number	*/	1");
		fw.println("	/* body  0  name	*/	body_0	/*	face_number	*/	1");
		fw.println("		/* face  0  name	*/  body_0_face_0");
		fw.println("			/*	face_type   */  unknown		/*	parameter_number	*/	0	/*	parameter	*/");
		fw.println("			/*	total_face_primitive_number	*/	",triangle_number);
		fw.println("			/*	face_attribute_number		*/	0");
		fw.print  ("			/*	face_face_box				*/");
		if(box_data==null)
			fw.print  ("	nobox");
		else
			for(int i=0,ni=box_data.length;i<ni;i++)
				fw.print  ("	",box_data[i]);
		fw.println();
		
		fw.println("			/*	face_loop_number			*/	0");
		
		fw.close();
	}
	
	public static void main(String args[])
	{
		debug_information.println("Begin");
		
		String dir="E:\\water_all\\data\\project\\part\\other_part\\part_stl\\part\\";
		
		new  stl_converter(dir+"part_0.stl",	"GBK",		dir+"part_0.stl.mesh",	"GBK");
		new  stl_converter(dir+"part_1.stl",	"GBK",		dir+"part_1.stl.mesh",	"GBK");
		new  stl_converter(dir+"part_2.stl",	"GBK",		dir+"part_2.stl.mesh",	"GBK");
		
		debug_information.println("End");
	}
}
