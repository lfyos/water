package old_convert;

import java.io.File;
import java.nio.charset.Charset;

import cadex.*;

import kernel_common_class.const_value;
import kernel_common_class.debug_information;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_transformation.point;

class repace_string
{
	public static String replace(String str)
	{
		if(str==null)
			return null;
		else
			return str.	replace('\\','/').
						replace(" ","").
						replace("\t","").
						replace("\r","").
						replace("\n","").
						replace("\"","");
	}
}
class cad_license_key 
{
	private static boolean has_not_done_test_flag=true,uneffective_flag=false;
	private static String error_message="";

	public static boolean test_license(String license_file_name,String target_charset)
	{
		if(has_not_done_test_flag) {
			has_not_done_test_flag=false;
			try{
	        	uneffective_flag=false;
	        	System.loadLibrary ("CadExA3DS");
	        	System.loadLibrary ("CadExACIS");
	        	System.loadLibrary ("CadExBRep");
	            System.loadLibrary ("CadExCollada");
	            System.loadLibrary ("CadExCore");
	        	System.loadLibrary ("CadExCreo");
	        	System.loadLibrary ("CadExDXF");
	            System.loadLibrary ("CadExFBX");
	            System.loadLibrary ("CadExGLTF");
	        	System.loadLibrary ("CadExIFC");
	        	System.loadLibrary ("CadExIGES");
	            System.loadLibrary ("CadExJT");
	            System.loadLibrary ("CadExMesh");
	        	System.loadLibrary ("CadExNX");
	        	System.loadLibrary ("CadExOBJ");
	            System.loadLibrary ("CadExPara");
	            System.loadLibrary ("CadExRhino");
	            System.loadLibrary ("CadExSLD");
	            System.loadLibrary ("CadExSTEP");
	            System.loadLibrary ("CadExSTL");
	            System.loadLibrary ("CadExViewD3D");
	            System.loadLibrary ("CadExView");
	            System.loadLibrary ("CadExVRML");
	            System.loadLibrary ("CadExX3D");
	            System.loadLibrary ("CadExX3MF");
	        }catch (UnsatisfiedLinkError e) {
	        	uneffective_flag=true;
	        	error_message="CAD Exchanger SDK does System.loadLibrary error:\t"+e.toString();
	        	debug_information.println(error_message);
	        }
			String license_str="";
			for(file_reader license_f=new file_reader(license_file_name,target_charset);;) {
				String str;
				if(license_f.eof()){
					license_f.close();
					break;
				}
				if((str=license_f.get_line())!=null)
					license_str+=str.trim();
			}
			if(LicenseManager.Activate(license_str))
				uneffective_flag=false;
			else {
				has_not_done_test_flag=true;
				uneffective_flag=true;
				if(error_message.length()>0)
					error_message+="\n";
				error_message+="CAD Exchanger SDK license error,license file name is "+license_file_name;
			}
		}
		if(uneffective_flag)
			debug_information.println(error_message);
		return uneffective_flag;
	}
}

class material
{
	double color[],ambient[],diffuse[],specular[],emission[];
	double shininess;

	public static boolean compare(material s,material t)
	{
		for(int i=0;i<4;i++) {
			if(Math.abs(s.color[i]-t.color[i])>const_value.min_value)
				return false;
			if(Math.abs(s.ambient[i]-t.ambient[i])>const_value.min_value)
				return false;
			if(Math.abs(s.diffuse[i]-t.diffuse[i])>const_value.min_value)
				return false;
			if(Math.abs(s.specular[i]-t.specular[i])>const_value.min_value)
				return false;
			if(Math.abs(s.emission[i]-t.emission[i])>const_value.min_value)
				return false;
		}
		return (Math.abs(s.shininess-t.shininess)>const_value.min_value)?false:true;
	}
	public material(double my_color[],
			double my_ambient[],double my_diffuse[],double my_specular[],
			double my_emission[],double my_shininess)
	{
		color		=new double[]{my_color   [0],	my_color   [1],	my_color   [2],	my_color   [3]};
		ambient		=new double[]{my_ambient [0],	my_ambient [1],	my_ambient [2],	my_ambient [3]};
		diffuse		=new double[]{my_diffuse [0],	my_diffuse [1],	my_diffuse [2],	my_diffuse [3]};
		specular	=new double[]{my_specular[0],	my_specular[1],	my_specular[2],	my_specular[3]};
		emission	=new double[]{my_emission[0],	my_emission[1],	my_emission[2],	my_emission[3]};
		shininess	=my_shininess;
	}
	public material(String file_name,String charset)
	{
		file_reader f=new file_reader(file_name,charset);
		
		color		=new double[]{f.get_double(),f.get_double(),f.get_double(),f.get_double()};
		ambient		=new double[]{f.get_double(),f.get_double(),f.get_double(),f.get_double()};
		diffuse		=new double[]{f.get_double(),f.get_double(),f.get_double(),f.get_double()};
		specular	=new double[]{f.get_double(),f.get_double(),f.get_double(),f.get_double()};
		emission	=new double[]{f.get_double(),f.get_double(),f.get_double(),f.get_double()};
		shininess	=f.get_double();
		
		f.close();
	}
	public material(ModelData_Appearance ap,material ma)
	{
		color		=new double[]{ma.color   [0],	ma.color   [1],	ma.color   [2],	ma.color   [3]};
		ambient		=new double[]{ma.ambient [0],	ma.ambient [1],	ma.ambient [2],	ma.ambient [3]};
		diffuse		=new double[]{ma.diffuse [0],	ma.diffuse [1],	ma.diffuse [2],	ma.diffuse [3]};
		specular	=new double[]{ma.specular[0],	ma.specular[1],	ma.specular[2],	ma.specular[3]};
		emission	=new double[]{ma.emission[0],	ma.emission[1],	ma.emission[2],	ma.emission[3]};
		shininess	=ma.shininess;
		
		if(ap==null)
			return;
		
		ModelData_Color model_color=new ModelData_Color();
		if(ap.ToColor(model_color))
			if(model_color.IsValid())
				color=new double[]{model_color.R(),model_color.G(),model_color.B(),model_color.A()};

		ModelData_Material model_material=new ModelData_Material();
		if(ap.ToMaterial(model_material)){
			if((model_color=model_material.AmbientColor()).IsValid())
				ambient=new double[]{model_color.R(),model_color.G(),model_color.B(),model_color.A()};
			if((model_color=model_material.DiffuseColor()).IsValid())
				diffuse=new double[]{model_color.R(),model_color.G(),model_color.B(),model_color.A()};
			if((model_color=model_material.SpecularColor()).IsValid()) {
				specular=new double[]{model_color.R(),model_color.G(),model_color.B(),model_color.A()};
				shininess=model_material.Shininess();
				
				if((specular[0]*specular[0]+specular[1]*specular[1]+specular[2]*specular[2])<0.01) {
					specular=new double[]{0.5,0.5,0.5,1.0};
					shininess=8;
				}
			}
			if((model_color=model_material.EmissionColor()).IsValid())
				emission=new double[]{model_color.R(),model_color.G(),model_color.B(),model_color.A()};
		}
	}
}

class material_array
{
	private material ma[];
	public double red,green,blue,alfa;
	
	public void write_material(String file_name,String charset_name)
	{
		file_writer fw=new file_writer(file_name,charset_name);
		
		fw.begin_str	="[";
		fw.separator_str=",";
		fw.end_str		="],";
		
		fw.println("			\"material\"	:	[");
		for(int i=0,ni=ma.length;i<ni;i++) {
			
			fw.println("				{");
			fw.println("					\"ambient\"		:	",		ma[i].ambient);
			fw.println("					\"diffuse\"		:	",		ma[i].diffuse);
			fw.println("					\"specular\"		:	",	ma[i].specular);
			fw.println("					\"emission\"		:	",	ma[i].emission);
			fw.println("					\"shininess\"		:	",	ma[i].shininess);
			fw.println("				}",(i==(ni-1))?"":",");
		}
		fw.println("			]");
		
		fw.close();
	}
	private int touch_routine(ModelData_Appearance ap,material default_material)
	{
		material m=new material(ap,default_material);
		for(int i=0,ni=ma.length;i<ni;i++)
			if(material.compare(ma[i],m))
				return i;
		material bak[]=ma;
		ma=new material[ma.length+1];
		for(int i=0,ni=bak.length;i<ni;i++)
			ma[i]=bak[i];
		ma[ma.length-1]=m;
		return ma.length-1;
	}
	public int touch(ModelData_Appearance ap,material default_material)
	{
		int material_id=touch_routine(ap,default_material);
		red		=ma[material_id].color[0];
		green	=ma[material_id].color[1];
		blue	=ma[material_id].color[2];
		alfa	=ma[material_id].color[3];
		return material_id;
	}
	public material_array()
	{
		ma=new material[0];
	}
}

class line_collector
{
	private ModelData_Point start_point_array[];
	private ModelData_Point end_point_array[];
	
	public void write_out(file_writer fw)
	{
		fw.println("/*                Line edge number    */   ",start_point_array.length);
		for(int i=0,ni=start_point_array.length;i<ni;i++) {
			point p0=new point(start_point_array[i].X(),	start_point_array[i].Y(),	start_point_array[i].Z());
			point p1=new point(end_point_array[i].X(),		end_point_array[i].Y(),		end_point_array[i].Z());
			point dr=p1.sub(p0).expand(1);
			fw.println("/*                    NO.    "+i+"    edge   */");
			fw.println("/*                        curve type   */  line");
			fw.print  ("/*                        parameter number   */  6  /*  parameter  */");
			fw.print  ("	",p0.x);
			fw.print  ("	",p0.y);
			fw.print  ("	",p0.z);
			fw.print  ("	",dr.x);
			fw.print  ("	",dr.y);
			fw.println("	",dr.z);
			
			fw.println("/*                        parameter_point_material  */  0    0    0    1");
			
			fw.print  ("                          start_effective");
			fw.print  ("	",p0.x);
			fw.print  ("	",p0.y);
			fw.print  ("	",p0.z);
			fw.println("	1.0		/*  start_point_material   */   0    0    0    1");
			
			fw.print  ("                          end_effective");
			fw.print  ("	",p1.x);
			fw.print  ("	",p1.y);
			fw.print  ("	",p1.z);
			fw.println("	1.0		/*  end_point_material   */   0    0    0    1");
			
			fw.println("/*                        vertex number   */  2");
			fw.print  ("/*                        NO.     0   vertex location  */");
			fw.print  ("	",p0.x);
			fw.print  ("	",p0.y);
			fw.print  ("	",p0.z);
			fw.println("    1   /*  tessellation_material  */  0    0    0    1");
			
			fw.print  ("/*                        NO.     1   vertex location  */");
			fw.print  ("	",p1.x);
			fw.print  ("	",p1.y);
			fw.print  ("	",p1.z);
			fw.println("    1   /*  tessellation_material  */  0    0    0    1");
		}
	}
	public line_collector(edge_collector my_edge_collector)
	{
		ModelData_Curve my_curve;
		
		int curve_number=0,edge_number=my_edge_collector.edge_array.length;
		ModelData_Point start_point_bak[]=new ModelData_Point[edge_number];
		ModelData_Point end_point_bak[]	 =new ModelData_Point[edge_number];
		for(int i=0;i<edge_number;i++) {
			if((my_curve=my_edge_collector.edge_array[i].Curve(new double[]{0},new double[]{1.0}))!=null) 
				if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Line) {
					start_point_bak	[curve_number]=my_edge_collector.edge_array[i].StartVertex().Point();
					end_point_bak	[curve_number]=my_edge_collector.edge_array[i].EndVertex().Point();
					curve_number++;
				}
		}
		start_point_array	=new ModelData_Point[curve_number];
		end_point_array		=new ModelData_Point[curve_number];
		for(int i=0;i<curve_number;i++) {
			start_point_array[i]=start_point_bak[i];
			end_point_array	 [i]=end_point_bak	[i];
		}
	}
}

class circle_collector
{
	private ModelData_Circle circle_array[];
	private ModelData_Point start_point_array[];
	private ModelData_Point end_point_array[];
		
	public void write_out(file_writer fw,file_writer edge_fw,
			double chordal_deflection,double angular_deflection,
			int max_step_number,boolean external_flag)
	{
		fw.println("/*                Circle edge number    */   ",circle_array.length);
		for(int i=0,ni=start_point_array.length;i<ni;i++) {
			point p0=new point(start_point_array[i].X(),	start_point_array[i].Y(),	start_point_array[i].Z());
			point p1=new point(end_point_array[i].X(),		end_point_array[i].Y(),		end_point_array[i].Z());
			
			ModelData_Point center=circle_array[i].Position().Location();
			ModelData_Direction x_dir=circle_array[i].Position().XDirection();
			ModelData_Direction y_dir=circle_array[i].Position().YDirection();
			point dir=(new point(x_dir.X(),x_dir.Y(),x_dir.Z())).cross(new point(y_dir.X(),y_dir.Y(),y_dir.Z())).expand(1.0);

			fw.println("/*                    NO.    "+i+"    edge   */");
			fw.println("/*                        curve type   */  circle");
			fw.print  ("/*                        parameter number   */  7  /*  parameter  */");
			
			fw.print  ("	",center.X());
			fw.print  ("	",center.Y());
			fw.print  ("	",center.Z());
			fw.print  ("	",dir.x);
			fw.print  ("	",dir.y);
			fw.print  ("	",dir.z);
			fw.println("	",circle_array[i].Radius());
			
			fw.println("/*                        parameter_point_material  */  0    0    0    1");
			
			if(p1.sub(p0).distance2()<const_value.min_value2){
				fw.println("                          start_not_effective");
				fw.println("                          end_not_effective");
			}else {
				fw.print  ("                          start_effective");
				fw.print  ("	",p0.x);
				fw.print  ("	",p0.y);
				fw.print  ("	",p0.z);
				fw.println("	1.0		/*  start_point_material   */   0    0    0    1");
				
				fw.print  ("                          end_effective");
				fw.print  ("	",p1.x);
				fw.print  ("	",p1.y);
				fw.print  ("	",p1.z);
				fw.println("	1.0		/*  end_point_material   */   0    0    0    1");
			}

			double alf_step=2.0*Math.acos(1-chordal_deflection/(circle_array[i].Radius()));
			double start_parameter	=circle_array[i].Parameter(start_point_array[i]);
			double end_parameter	=circle_array[i].Parameter(end_point_array[i]);
			if((end_parameter-start_parameter)<const_value.min_value)
				end_parameter+=2.0*Math.PI;
			
			int step_number;
			if((alf_step=(alf_step>angular_deflection)?angular_deflection:alf_step)<const_value.min_value)
				step_number=max_step_number;
			else if((step_number=1+(int)((end_parameter-start_parameter)/alf_step))>max_step_number)
				step_number=max_step_number;

			if(step_number<=0) {
				fw.println("/*                        vertex number   */  0");
				continue;
			}
			
			double min_x=0,min_y=0,min_z=0,max_x=0,max_y=0,max_z=0;
			fw.println("/*                        vertex number   */  "
					+((external_flag)?"external ":""),step_number+1);
			for(int j=0;j<=step_number;j++) {
				double par=((double)j)/((double)step_number);
				ModelData_Point p=circle_array[i].Value(par*start_parameter+(1.0-par)*end_parameter);
				double x=p.X(),y=p.Y(),z=p.Z();
				(external_flag?edge_fw:fw).print  ("/*	NO.	"+j+"   vertex location  */");
				(external_flag?edge_fw:fw).print  ("	",x);
				(external_flag?edge_fw:fw).print  ("	",y);
				(external_flag?edge_fw:fw).print  ("	",z);
				(external_flag?edge_fw:fw).println("    1   /*  tessellation_material  */  0    0    0    1");
				
				if(j==0) {
					min_x=x;
					min_y=y;
					min_z=z;
					max_x=x;
					max_y=y;
					max_z=z;
				}else {
					min_x=(x<min_x)?x:min_x;
					min_y=(y<min_y)?y:min_y;
					min_z=(z<min_z)?z:min_z;
					max_x=(x>max_x)?x:max_x;
					max_y=(y>max_y)?y:max_y;
					max_z=(z>max_z)?z:max_z;
				}
			}
			(external_flag?edge_fw:fw).println();
			
			if(external_flag)
				fw.println("/*                        edge box   */  ",
						min_x+"	"+min_y+"	"+min_z+"	"+max_x+"	"+max_y+"	"+max_z);
		}
	}
	public circle_collector(edge_collector my_edge_collector)
	{
		ModelData_Curve my_curve;
		
		int curve_number=0,edge_number=my_edge_collector.edge_array.length;
		ModelData_Circle circle_bak[]	 =new ModelData_Circle[edge_number];
		ModelData_Point start_point_bak[]=new ModelData_Point[edge_number];
		ModelData_Point end_point_bak[]	 =new ModelData_Point[edge_number];
		for(int i=0;i<edge_number;i++) {
			if((my_curve=my_edge_collector.edge_array[i].Curve(new double[]{0},new double[]{Math.PI*2.0}))!=null) 
				if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Circle) {
					circle_bak		[curve_number]=ModelData_Circle.Cast(my_curve);
					start_point_bak	[curve_number]=my_edge_collector.edge_array[i].StartVertex().Point();
					end_point_bak	[curve_number]=my_edge_collector.edge_array[i].EndVertex().Point();
					curve_number++;
				}
		}				
		circle_array		=new ModelData_Circle[curve_number];
		start_point_array	=new ModelData_Point[curve_number];
		end_point_array		=new ModelData_Point[curve_number];
		for(int i=0;i<curve_number;i++){
			circle_array	 [i]=circle_bak		[i];
			start_point_array[i]=start_point_bak[i];
			end_point_array	 [i]=end_point_bak	[i];
		}
	}
}

class ellipse_collector
{
	private ModelData_Ellipse ellipse_array[];
	private ModelData_Point start_point_array[];
	private ModelData_Point end_point_array[];
	
	public void write_out(file_writer fw,file_writer edge_fw,
			double chordal_deflection,double angular_deflection,
			int max_step_number,boolean external_flag)
	{
		fw.println("/*                ellipse edge number    */   ",ellipse_array.length);
		
		for(int i=0,ni=start_point_array.length;i<ni;i++) {
			point p0=new point(start_point_array[i].X(),	start_point_array[i].Y(),	start_point_array[i].Z());
			point p1=new point(end_point_array[i].X(),		end_point_array[i].Y(),		end_point_array[i].Z());
			
			ModelData_Point center=ellipse_array[i].Position().Location();
			ModelData_Direction x_dir=ellipse_array[i].Position().XDirection();
			ModelData_Direction y_dir=ellipse_array[i].Position().YDirection();
			double max_radius=ellipse_array[i].MajorRadius();
			double min_radius=ellipse_array[i].MinorRadius();
			
			point center_point=new point(center.X(),center.Y(),center.Z());
			point 	   a_point=new point(x_dir.X(),x_dir.Y(),x_dir.Z()).expand(max_radius).add(center_point);
			point 	   b_point=new point(y_dir.X(),y_dir.Y(),y_dir.Z()).expand(min_radius).add(center_point);

			fw.println("/*                    NO.    "+i+"    edge   */");
			fw.println("/*                        curve type   */  ellipse");
			fw.print  ("/*                        parameter number   */  9  /*  parameter  */");
			
			fw.print  ("	",center_point.x);
			fw.print  ("	",center_point.y);
			fw.print  ("	",center_point.z);
			fw.print  ("	",a_point.x);
			fw.print  ("	",a_point.y);
			fw.print  ("	",a_point.z);
			fw.print  ("	",b_point.x);
			fw.print  ("	",b_point.y);
			fw.println("	",b_point.z);
			
			fw.println("/*                        parameter_point_material  */  0    0    0    1");
			
			if(p1.sub(p0).distance2()<const_value.min_value2){
				fw.println("                          start_not_effective");
				fw.println("                          end_not_effective");
			}else {
				fw.print  ("                          start_effective");
				fw.print  ("	",p0.x);
				fw.print  ("	",p0.y);
				fw.print  ("	",p0.z);
				fw.println("	1.0		/*  start_point_material   */   0    0    0    1");
				
				fw.print  ("                          end_effective");
				fw.print  ("	",p1.x);
				fw.print  ("	",p1.y);
				fw.print  ("	",p1.z);
				fw.println("	1.0		/*  end_point_material   */   0    0    0    1");
			}

			double alf_step=2.0*Math.acos(1-chordal_deflection/(max_radius));
			double start_parameter	=ellipse_array[i].Parameter(start_point_array[i]);
			double end_parameter	=ellipse_array[i].Parameter(end_point_array[i]);
			if((end_parameter-start_parameter)<const_value.min_value)
				end_parameter+=2.0*Math.PI;
			
			int step_number;
			if((alf_step=(alf_step>angular_deflection)?angular_deflection:alf_step)<const_value.min_value)
				step_number=max_step_number;
			else if((step_number=1+(int)((end_parameter-start_parameter)/alf_step))>max_step_number)
				step_number=max_step_number;

			if(step_number<=0) {
				fw.println("/*                        vertex number   */  0");
				continue;
			}
			
			double min_x=0,min_y=0,min_z=0,max_x=0,max_y=0,max_z=0;
			fw.println("/*                        vertex number   */  "
					+((external_flag)?"external ":""),step_number+1);
			
			for(int j=0;j<=step_number;j++) {
				double par=((double)j)/((double)step_number);
				ModelData_Point p=ellipse_array[i].Value(par*start_parameter+(1.0-par)*end_parameter);
				double x=p.X(),y=p.Y(),z=p.Z();
				
				(external_flag?edge_fw:fw).print  ("/*	NO.	"+j+"	vertex location	*/");
				(external_flag?edge_fw:fw).print  ("	",x);
				(external_flag?edge_fw:fw).print  ("	",y);
				(external_flag?edge_fw:fw).print  ("	",z);
				(external_flag?edge_fw:fw).println("    1   /*  tessellation_material  */  0    0    0    1");
				
				if(j==0) {
					min_x=x;
					min_y=y;
					min_z=z;
					max_x=x;
					max_y=y;
					max_z=z;
				}else {
					min_x=(x<min_x)?x:min_x;
					min_y=(y<min_y)?y:min_y;
					min_z=(z<min_z)?z:min_z;
					max_x=(x>max_x)?x:max_x;
					max_y=(y>max_y)?y:max_y;
					max_z=(z>max_z)?z:max_z;
				}
			}
			(external_flag?edge_fw:fw).println();
			
			if(external_flag)
				fw.println("/*                        edge box   */  ",
						min_x+"	"+min_y+"	"+min_z+"	"+max_x+"	"+max_y+"	"+max_z);
			
			debug_information.println("ellipse_collector	",i+":"+step_number);
		}
	}
	public ellipse_collector(edge_collector my_edge_collector)
	{
		ModelData_Curve my_curve;
		
		int curve_number=0,edge_number=my_edge_collector.edge_array.length;
		ModelData_Ellipse ellipse_bak[]	 =new ModelData_Ellipse[edge_number];
		ModelData_Point start_point_bak[]=new ModelData_Point[edge_number];
		ModelData_Point end_point_bak[]	 =new ModelData_Point[edge_number];
		for(int i=0;i<edge_number;i++) {
			if((my_curve=my_edge_collector.edge_array[i].Curve(new double[]{0},new double[]{Math.PI*2.0}))!=null) 
				if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Ellipse) {
					ellipse_bak		[curve_number]=ModelData_Ellipse.Cast(my_curve);
					start_point_bak	[curve_number]=my_edge_collector.edge_array[i].StartVertex().Point();
					end_point_bak	[curve_number]=my_edge_collector.edge_array[i].EndVertex().Point();
					curve_number++;
				}
		}
		ellipse_array		=new ModelData_Ellipse[curve_number];
		start_point_array	=new ModelData_Point[curve_number];
		end_point_array		=new ModelData_Point[curve_number];
		for(int i=0;i<curve_number;i++) {
			ellipse_array	 [i]=ellipse_bak	[i];
			start_point_array[i]=start_point_bak[i];
			end_point_array	 [i]=end_point_bak	[i];
		}
	}
}

class hyperbola_collector
{
	private ModelData_Hyperbola hyperbola_array[];
	private ModelData_Point start_point_array[];
	private ModelData_Point end_point_array[];
	
	public void write_out(file_writer fw,file_writer edge_fw,
			double chordal_deflection,double angular_deflection,
			int max_step_number,boolean external_flag)
	{
		fw.println("/*                hyperbola edge number    */   ",hyperbola_array.length);
		
		for(int i=0,ni=start_point_array.length;i<ni;i++) {
			point p0=new point(start_point_array[i].X(),	start_point_array[i].Y(),	start_point_array[i].Z());
			point p1=new point(end_point_array[i].X(),		end_point_array[i].Y(),		end_point_array[i].Z());
			
			ModelData_Point center=hyperbola_array[i].Position().Location();
			ModelData_Direction x_dir=hyperbola_array[i].Position().XDirection();
			ModelData_Direction y_dir=hyperbola_array[i].Position().YDirection();
			double max_radius=hyperbola_array[i].MajorRadius();
			double min_radius=hyperbola_array[i].MinorRadius();
			
			point center_point=new point(center.X(),center.Y(),center.Z());
			point 	   a_point=new point(x_dir.X(),x_dir.Y(),x_dir.Z()).expand(max_radius).add(center_point);
			point 	   b_point=new point(y_dir.X(),y_dir.Y(),y_dir.Z()).expand(min_radius).add(center_point);

			fw.println("/*                    NO.    "+i+"    edge   */");
			fw.println("/*                        curve type   */  hyperbola");
			fw.print  ("/*                        parameter number   */  9  /*  parameter  */");
			
			fw.print  ("	",center_point.x);
			fw.print  ("	",center_point.y);
			fw.print  ("	",center_point.z);
			fw.print  ("	",a_point.x);
			fw.print  ("	",a_point.y);
			fw.print  ("	",a_point.z);
			fw.print  ("	",b_point.x);
			fw.print  ("	",b_point.y);
			fw.println("	",b_point.z);
			
			fw.println("/*                        parameter_point_material  */  0    0    0    1");
			
			if(p1.sub(p0).distance2()<const_value.min_value2){
				fw.println("                          start_not_effective");
				fw.println("                          end_not_effective");
			}else {
				fw.print  ("                          start_effective");
				fw.print  ("	",p0.x);
				fw.print  ("	",p0.y);
				fw.print  ("	",p0.z);
				fw.println("	1.0		/*  start_point_material   */   0    0    0    1");
				
				fw.print  ("                          end_effective");
				fw.print  ("	",p1.x);
				fw.print  ("	",p1.y);
				fw.print  ("	",p1.z);
				fw.println("	1.0		/*  end_point_material   */   0    0    0    1");
			}

			double start_parameter	=hyperbola_array[i].Parameter(start_point_array[i]);
			double end_parameter	=hyperbola_array[i].Parameter(end_point_array[i]);
			
			int step_number;
			if((step_number=1+(int)((end_parameter-start_parameter)/(chordal_deflection/min_radius)))>max_step_number)
				step_number=max_step_number;

			if(step_number<=0) {
				fw.println("/*                        vertex number   */  0");
				continue;
			}
			
			double min_x=0,min_y=0,min_z=0,max_x=0,max_y=0,max_z=0;
			fw.println("/*                        vertex number   */  "
					+((external_flag)?"external ":""),step_number+1);
			
			for(int j=0;j<=step_number;j++) {
				double par=((double)j)/((double)step_number);
				ModelData_Point p=hyperbola_array[i].Value(par*start_parameter+(1.0-par)*end_parameter);
				double x=p.X(),y=p.Y(),z=p.Z();
				
				(external_flag?edge_fw:fw).print  ("/*	NO.     "+j+"   vertex location  */");
				(external_flag?edge_fw:fw).print  ("	",x);
				(external_flag?edge_fw:fw).print  ("	",y);
				(external_flag?edge_fw:fw).print  ("	",z);
				(external_flag?edge_fw:fw).println("    1   /*  tessellation_material  */  0    0    0    1");
				
				if(j==0) {
					min_x=x;
					min_y=y;
					min_z=z;
					max_x=x;
					max_y=y;
					max_z=z;
				}else {
					min_x=(x<min_x)?x:min_x;
					min_y=(y<min_y)?y:min_y;
					min_z=(z<min_z)?z:min_z;
					max_x=(x>max_x)?x:max_x;
					max_y=(y>max_y)?y:max_y;
					max_z=(z>max_z)?z:max_z;
				}
			}
			(external_flag?edge_fw:fw).println();
			if(external_flag)
				fw.println("/*                        edge box   */  ",
						min_x+"	"+min_y+"	"+min_z+"	"+max_x+"	"+max_y+"	"+max_z);
		}
	}
	
	public hyperbola_collector(edge_collector my_edge_collector)
	{
		ModelData_Curve my_curve;
		
		int curve_number=0,edge_number=my_edge_collector.edge_array.length;
		ModelData_Hyperbola hyperbola_bak[]	=new ModelData_Hyperbola[edge_number];
		ModelData_Point start_point_bak[]	=new ModelData_Point[edge_number];
		ModelData_Point end_point_bak[]		=new ModelData_Point[edge_number];
		for(int i=0;i<edge_number;i++) {
			if((my_curve=my_edge_collector.edge_array[i].Curve(new double[]{0},new double[]{Math.PI*2.0}))!=null)
				if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Hyperbola) {
					hyperbola_bak	[curve_number]=ModelData_Hyperbola.Cast(my_curve);
					start_point_bak	[curve_number]=my_edge_collector.edge_array[i].StartVertex().Point();
					end_point_bak	[curve_number]=my_edge_collector.edge_array[i].EndVertex().Point();
					curve_number++;
				}
		}	
		hyperbola_array		=new ModelData_Hyperbola[curve_number];
		start_point_array	=new ModelData_Point[curve_number];
		end_point_array		=new ModelData_Point[curve_number];
		for(int i=0;i<curve_number;i++) {
			hyperbola_array	 [i]=hyperbola_bak	[i];
			start_point_array[i]=start_point_bak[i];
			end_point_array	 [i]=end_point_bak	[i];
		}
	}
}

class parabola_collector
{
	private ModelData_Parabola parabola_array[];
	private ModelData_Point start_point_array[];
	private ModelData_Point end_point_array[];
	
	public void write_out(file_writer fw,file_writer edge_fw,
			double chordal_deflection,double angular_deflection,
			int max_step_number,boolean external_flag)
	{
		fw.println("/*                parabola edge number    */   ",parabola_array.length);
		
		for(int i=0,ni=start_point_array.length;i<ni;i++) {
			point p0=new point(start_point_array[i].X(),	start_point_array[i].Y(),	start_point_array[i].Z());
			point p1=new point(end_point_array[i].X(),		end_point_array[i].Y(),		end_point_array[i].Z());
			
			ModelData_Point center=parabola_array[i].Position().Location();
			ModelData_Direction x_dir=parabola_array[i].Position().XDirection();
			ModelData_Direction y_dir=parabola_array[i].Position().YDirection();
			double focal=parabola_array[i].Focal();
			
			point center_point=new point(center.X(),center.Y(),center.Z());
			point 	   a_point=new point(x_dir.X(),x_dir.Y(),x_dir.Z()).expand(focal).add(center_point);
			point 	   b_point=new point(y_dir.X(),y_dir.Y(),y_dir.Z()).expand(1).add(center_point);

			fw.println("/*                    NO.    "+i+"    edge   */");
			fw.println("/*                        curve type   */  parabola");
			fw.print  ("/*                        parameter number   */  9  /*  parameter  */");
			
			fw.print  ("	",center_point.x);
			fw.print  ("	",center_point.y);
			fw.print  ("	",center_point.z);
			fw.print  ("	",a_point.x);
			fw.print  ("	",a_point.y);
			fw.print  ("	",a_point.z);
			fw.print  ("	",b_point.x);
			fw.print  ("	",b_point.y);
			fw.println("	",b_point.z);
			
			fw.println("/*                        parameter_point_material  */  0    0    0    1");
			
			if(p1.sub(p0).distance2()<const_value.min_value2){
				fw.println("                          start_not_effective");
				fw.println("                          end_not_effective");
			}else{
				fw.print  ("                          start_effective");
				fw.print  ("	",p0.x);
				fw.print  ("	",p0.y);
				fw.print  ("	",p0.z);
				fw.println("	1.0		/*  start_point_material   */   0    0    0    1");
				
				fw.print  ("                          end_effective");
				fw.print  ("	",p1.x);
				fw.print  ("	",p1.y);
				fw.print  ("	",p1.z);
				fw.println("	1.0		/*  end_point_material   */   0    0    0    1");
			}

			double start_parameter	=parabola_array[i].Parameter(start_point_array[i]);
			double end_parameter	=parabola_array[i].Parameter(end_point_array[i]);
			
			int step_number;
			if((step_number=1+(int)((end_parameter-start_parameter)/(chordal_deflection/focal)))>max_step_number)
				step_number=max_step_number;

			if(step_number<=0) {
				fw.println("/*                        vertex number   */  0");
				continue;
			}
			
			double min_x=0,min_y=0,min_z=0,max_x=0,max_y=0,max_z=0;
			fw.println("/*                        vertex number   */  "
					+((external_flag)?"external ":""),step_number+1);
			for(int j=0;j<=step_number;j++) {
				double par=((double)j)/((double)step_number);
				ModelData_Point p=parabola_array[i].Value(par*start_parameter+(1.0-par)*end_parameter);
				double x=p.X(),y=p.Y(),z=p.Z();
				
				(external_flag?edge_fw:fw).print  ("/*	NO.     "+j+"   vertex location  */");
				(external_flag?edge_fw:fw).print  ("	",x);
				(external_flag?edge_fw:fw).print  ("	",y);
				(external_flag?edge_fw:fw).print  ("	",z);
				(external_flag?edge_fw:fw).println("    1   /*  tessellation_material  */  0    0    0    1");
				
				if(j==0) {
					min_x=x;
					min_y=y;
					min_z=z;
					max_x=x;
					max_y=y;
					max_z=z;
				}else {
					min_x=(x<min_x)?x:min_x;
					min_y=(y<min_y)?y:min_y;
					min_z=(z<min_z)?z:min_z;
					max_x=(x>max_x)?x:max_x;
					max_y=(y>max_y)?y:max_y;
					max_z=(z>max_z)?z:max_z;
				}
			}
			(external_flag?edge_fw:fw).println();
			
			if(external_flag)
				fw.println("/*                        edge box   */  ",
						min_x+"	"+min_y+"	"+min_z+"	"+max_x+"	"+max_y+"	"+max_z);
		}
	}
	public parabola_collector(edge_collector my_edge_collector)
	{
		ModelData_Curve my_curve;
		
		int curve_number=0,edge_number=my_edge_collector.edge_array.length;
		ModelData_Parabola parabola_bak[]=new ModelData_Parabola[edge_number];
		ModelData_Point start_point_bak[]=new ModelData_Point[edge_number];
		ModelData_Point end_point_bak[]	 =new ModelData_Point[edge_number];
		for(int i=0;i<edge_number;i++) {
			if((my_curve=my_edge_collector.edge_array[i].Curve(new double[]{0},new double[]{Math.PI*2.0}))!=null) 
				if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Parabola){
					parabola_bak	[curve_number]=ModelData_Parabola.Cast(my_curve);
					start_point_bak	[curve_number]=my_edge_collector.edge_array[i].StartVertex().Point();
					end_point_bak	[curve_number]=my_edge_collector.edge_array[i].EndVertex().Point();
					curve_number++;
				}
		}
		parabola_array		=new ModelData_Parabola[curve_number];
		start_point_array	=new ModelData_Point[curve_number];
		end_point_array		=new ModelData_Point[curve_number];
		for(int i=0;i<curve_number;i++) {
			parabola_array	 [i]=parabola_bak	[i];
			start_point_array[i]=start_point_bak[i];
			end_point_array	 [i]=end_point_bak	[i];
		}
	}
}

class b_spline_collector
{
	private ModelData_BSplineCurve b_spline_array[];
	private ModelData_Point start_point_array[];
	private ModelData_Point end_point_array[];
	
	public void write_out(file_writer fw,file_writer edge_fw,
			double chordal_deflection,double angular_deflection,
			int max_step_number,boolean external_flag)
	{
		fw.println("/*                b_spline edge number    */   ",b_spline_array.length);
		
		for(int i=0,ni=start_point_array.length;i<ni;i++) {
			point p0=new point(start_point_array[i].X(),	start_point_array[i].Y(),	start_point_array[i].Z());
			point p1=new point(end_point_array[i].X(),		end_point_array[i].Y(),		end_point_array[i].Z());

			fw.println("/*                    NO.    "+i+"    b_spline   */");
			fw.println("/*                        curve type   */  b_spline	/*    parameter number   */  0  /*  parameter  */");
			fw.println("/*                        parameter_point_material  */  0    0    0    1");
			
			if(p1.sub(p0).distance2()<const_value.min_value2){
				fw.println("                          start_not_effective");
				fw.println("                          end_not_effective");
			}else{
				fw.print  ("                          start_effective");
				fw.print  ("	",p0.x);
				fw.print  ("	",p0.y);
				fw.print  ("	",p0.z);
				fw.println("	1.0		/*  start_point_material   */   0    0    0    1");
				
				fw.print  ("                          end_effective");
				fw.print  ("	",p1.x);
				fw.print  ("	",p1.y);
				fw.print  ("	",p1.z);
				fw.println("	1.0		/*  end_point_material   */   0    0    0    1");
			}
			
			int knot_number=b_spline_array[i].NumberOfKnots();
			double knot_value[]=new double[knot_number];
			for(int j=0;j<knot_number;j++)
				knot_value[j]=b_spline_array[i].Knot(j+1);
			
			point knot_position[]=new point[knot_number];
			for(int j=0;j<knot_number;j++){
				ModelData_Point p=b_spline_array[i].Value(knot_value[j]);
				knot_position[j]=new point(p.X(),p.Y(),p.Z());
			}
			knot_number=1;
			for(int j=1,nj=knot_position.length;j<nj;j++)
				if(knot_position[j].sub(knot_position[knot_number-1]).distance2()>const_value.min_value2) {
					knot_value[knot_number]=knot_value[j];
					knot_position[knot_number++]=knot_position[j];
				}			
			double double_skip_number[]=new double[knot_number-1],total_double_skip_number=0;
			for(int j=0,nj=double_skip_number.length;j<nj;j++) {
				try{
					double radius=1.0/b_spline_array[i].Curvature((knot_value[j]+knot_value[j+1])/2.0);
					if((!Double.isNaN(radius))&&(!Double.isInfinite(radius)))
						if((chordal_deflection+const_value.min_value)<radius){
							double step_alf=Math.acos((radius-chordal_deflection)/radius);
							double my_length=knot_position[j+1].sub(knot_position[j]).distance();
							double my_alf=Math.asin(0.5*my_length/radius);
							double_skip_number[j]=my_alf/step_alf;
							if((!Double.isNaN(double_skip_number[j]))&&(!Double.isInfinite(double_skip_number[j]))){
								if(double_skip_number[j]<1.0)
									double_skip_number[j]=1.0;
								else if(double_skip_number[j]>max_step_number)
									double_skip_number[j]=max_step_number;
								total_double_skip_number+=double_skip_number[j];
								continue;
							}
						}
				}catch(Exception e) {
					;
				}
				double_skip_number[j]=1;
				total_double_skip_number++;
			}
			if(total_double_skip_number>max_step_number)
				for(int j=0,nj=double_skip_number.length;j<nj;j++)
					double_skip_number[j]*=max_step_number/total_double_skip_number;
			
			int skip_number[]=new int[knot_number-1],total_skip_number=1;
			for(int j=0,nj=skip_number.length;j<nj;j++){
				skip_number[j]=Math.round((float)(double_skip_number[j]));
				total_skip_number+=skip_number[j];
			}
			double min_x=0,min_y=0,min_z=0,max_x=0,max_y=0,max_z=0;
			
			fw.println("/*                        vertex number   */  "
					+(external_flag?"external ":""),total_skip_number);
			total_skip_number=0;
			for(int j=0,nj=skip_number.length;j<nj;j++) 
				for(int k=0,nk=skip_number[j];k<nk;k++){
					ModelData_Point p=b_spline_array[i].Value(knot_value[j]+k*(knot_value[j+1]-knot_value[j])/nk);
					double x=p.X(),y=p.Y(),z=p.Z();
					(external_flag?edge_fw:fw).print  ("/*	NO.     "+total_skip_number+"   vertex location  */");
					(external_flag?edge_fw:fw).print  ("	",x);
					(external_flag?edge_fw:fw).print  ("	",y);
					(external_flag?edge_fw:fw).print  ("	",z);
					(external_flag?edge_fw:fw).println("    1   /*  tessellation_material  */  0    0    0    1");
					
					if((total_skip_number++)==0) {
						min_x=x;
						min_y=y;
						min_z=z;
						max_x=x;
						max_y=y;
						max_z=z;
					}else {
						min_x=(x<min_x)?x:min_x;
						min_y=(y<min_y)?y:min_y;
						min_z=(z<min_z)?z:min_z;
						max_x=(x>max_x)?x:max_x;
						max_y=(y>max_y)?y:max_y;
						max_z=(z>max_z)?z:max_z;
					}
				}
			ModelData_Point p=b_spline_array[i].Value(knot_value[skip_number.length]);
			
			double x=p.X(),y=p.Y(),z=p.Z();
			(external_flag?edge_fw:fw).print  ("/*	NO.     "+total_skip_number+"   vertex location  */");
			(external_flag?edge_fw:fw).print  ("	",x);
			(external_flag?edge_fw:fw).print  ("	",y);
			(external_flag?edge_fw:fw).print  ("	",z);
			(external_flag?edge_fw:fw).println("    1   /*  tessellation_material  */  0    0    0    1");
			
			if((total_skip_number++)==0) {
				min_x=x;
				min_y=y;
				min_z=z;
				max_x=x;
				max_y=y;
				max_z=z;
			}else {
				min_x=(x<min_x)?x:min_x;
				min_y=(y<min_y)?y:min_y;
				min_z=(z<min_z)?z:min_z;
				max_x=(x>max_x)?x:max_x;
				max_y=(y>max_y)?y:max_y;
				max_z=(z>max_z)?z:max_z;
			}
			(external_flag?edge_fw:fw).println();
			
			if(external_flag)
				fw.println("/*                        edge box   */  ",
						min_x+"	"+min_y+"	"+min_z+"	"+max_x+"	"+max_y+"	"+max_z);
		}
	}
	public b_spline_collector(edge_collector my_edge_collector)
	{
		ModelData_Curve my_curve;
		
		int curve_number=0,edge_number=my_edge_collector.edge_array.length;
		ModelData_BSplineCurve b_spline_bak[]	=new ModelData_BSplineCurve[edge_number];
		ModelData_Point start_point_bak[]		=new ModelData_Point[edge_number];
		ModelData_Point end_point_bak[]	 		=new ModelData_Point[edge_number];
		
		for(int i=0;i<edge_number;i++) {
			if((my_curve=my_edge_collector.edge_array[i].Curve(new double[]{0},new double[]{1.0}))!=null) 
				if(my_curve.Type()==ModelData_CurveType.ModelData_CT_BSpline) {
					b_spline_bak	[curve_number]=ModelData_BSplineCurve.Cast(my_curve);
					start_point_bak	[curve_number]=my_edge_collector.edge_array[i].StartVertex().Point();
					end_point_bak	[curve_number]=my_edge_collector.edge_array[i].EndVertex().Point();
					curve_number++;
				}
		}
		b_spline_array		=new ModelData_BSplineCurve[curve_number];
		start_point_array	=new ModelData_Point[curve_number];
		end_point_array		=new ModelData_Point[curve_number];
		for(int i=0;i<curve_number;i++) {
			b_spline_array	 [i]=b_spline_bak	[i];
			start_point_array[i]=start_point_bak[i];
			end_point_array	 [i]=end_point_bak	[i];
		}
	}
}

class edge_collector
{
	public ModelData_Edge edge_array[];

	private int register_edge(ModelData_Shape theShape,int edge_number)
	{
		if(theShape!=null){
			if(theShape.Type()!=ModelData_ShapeType.ModelData_ST_Edge)
				for(ModelData_Shape.Iterator aShapeIt=new ModelData_Shape.Iterator(theShape);aShapeIt.HasNext();)
			           edge_number=register_edge(aShapeIt.Next(),edge_number);
			else{
				if(edge_number>=edge_array.length) {
					ModelData_Edge bak[]=edge_array;
		        	edge_array=new ModelData_Edge[bak.length+100];
		        	for(int i=0,ni=bak.length;i<ni;i++)
		        		edge_array[i]=bak[i];
				}
	        	edge_array[edge_number++]=ModelData_Edge.Cast(theShape);
			}
		}
        return edge_number;
	}
	private void test()
	{
		for(int i=0,ni=edge_array.length;i<ni;i++) {
			ModelData_Curve my_curve=edge_array[i].Curve(new double[]{0},new double[]{Math.PI*2.0});
			if(my_curve==null)
				continue;
			if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Line) {
				continue;
			}
			if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Circle) {
				continue;
			}
			if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Ellipse) {
				continue;
			}
			if(my_curve.Type()==ModelData_CurveType.ModelData_CT_BSpline) {
				continue;
			}
			if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Hyperbola) {
				debug_information.println("ModelData_CT_Hyperbola:",i);
				continue;
			}
			if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Parabola) {
				debug_information.println("ModelData_CT_Parabola:",i);
				continue;
			}
			if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Bezier) {
				debug_information.println("ModelData_CT_Bezier:",i);
				continue;
			}
			if(my_curve.Type()==ModelData_CurveType.ModelData_CT_Undefined) {
				debug_information.println("ModelData_CT_Undefined:",i);
				continue;
			}
			if(my_curve.Type()==ModelData_CurveType.ModelData_CT_UserDefined) {
				debug_information.println("ModelData_CT_UserDefined:",i);
				continue;
			}
			debug_information.println("Unknown:",i);
		}
	}
	public edge_collector(ModelData_Shape theShape)
	{
		edge_array=new ModelData_Edge[100];
		int edge_number=register_edge(theShape,0);
		if(edge_number!=edge_array.length) {
			ModelData_Edge bak[]=edge_array;
	    	edge_array=new ModelData_Edge[edge_number];
	    	for(int i=0;i<edge_number;i++)
	    		edge_array[i]=bak[i];
		}
		test();
	}
}

class face_collector
{
	public ModelData_Appearance appearance_array[];
	public ModelData_Face face_array[];
	
	private int register_face(ModelData_Shape theShape,int face_number,
			ModelData_BRepRepresentation brep_rep,ModelData_Appearance parent_appearance)
	{
		if(theShape!=null){
			if(theShape.Type()==ModelData_ShapeType.ModelData_ST_Face) {
				if(face_number>=face_array.length) {
					ModelData_Appearance appearance_bak[]=appearance_array;
		        	ModelData_Face face_bak[]=face_array;
		        	
		        	appearance_array=new ModelData_Appearance[face_bak.length+100];
		        	face_array=new ModelData_Face[face_bak.length+100];
		        	
		        	for(int i=0,ni=face_bak.length;i<ni;i++) {
		        		appearance_array[i]=appearance_bak[i];
		        		face_array[i]=face_bak[i];
		        	}
				}
				if((appearance_array[face_number]=brep_rep.Appearance(theShape))==null)
					appearance_array[face_number]=parent_appearance;
	        	face_array[face_number++]=ModelData_Face.Cast(theShape);
			}
	        for(ModelData_Shape.Iterator aShapeIt=new ModelData_Shape.Iterator(theShape);aShapeIt.HasNext();) {
	            ModelData_Shape aShape = aShapeIt.Next();
	            face_number=register_face(aShape,face_number,brep_rep,parent_appearance);
	        }
		}
        return face_number;
	}
	public face_collector(ModelData_Shape theShape,
			ModelData_BRepRepresentation brep_rep,ModelData_Appearance parent_appearance)
	{
		appearance_array=new ModelData_Appearance[100];
		face_array=new ModelData_Face[100];
		int face_number=register_face(theShape,0,brep_rep,parent_appearance);
		if(face_number<face_array.length) {
			ModelData_Appearance appearance_bak[]=appearance_array;
        	ModelData_Face face_bak[]=face_array;
        	appearance_array=new ModelData_Appearance[face_number];
        	face_array=new ModelData_Face[face_number];
        	for(int i=0;i<face_number;i++) {
        		appearance_array[i]=appearance_bak[i];
        		face_array[i]=face_bak[i];
        	}
		}
	}
}

class body_collctor
{
	public ModelData_BRepRepresentation brep_rep;
	
	public ModelData_Appearance body_appearance[];
	public face_collector body_array[];
	
	public body_collctor(ModelData_Part thePart)
	{
		body_appearance=new ModelData_Appearance[0];
		body_array=new face_collector[0];
		if((brep_rep=thePart.BRepRepresentation())==null)
        	return;
		ModelData_BodyList body_list;
		if((body_list=brep_rep.Get())==null)
        	return;
		long body_number;
		if((body_number=body_list.Size())<=0)
        	return;
		body_array=new face_collector[(int)body_number];
		body_appearance=new ModelData_Appearance[body_array.length];

		ModelData_Appearance part_appearance=thePart.Appearance();
		for(int i=0,ni=body_array.length;i<ni;i++) {
			ModelData_Body my_body=body_list.Element(i);
			if((body_appearance[i]=brep_rep.Appearance(my_body))==null)
				body_appearance[i]=part_appearance;
			body_array[i]=new face_collector(my_body,brep_rep,body_appearance[i]);
		}
	}
}

class part_exporter
{
	public boolean do_part_export(boolean external_flag,
			ModelData_Part thePart,int part_id,file_writer part_list_fw,
			String target_directory_name,String charset_name,material default_material,
			double chordal_deflection,double angular_deflection,int max_step_number)
	{
		ModelData_PolyRepresentation poly_rep=thePart.PolyRepresentation(
				ModelData_RepresentationMask.ModelData_RM_Poly);
		if (poly_rep==null) {
        	debug_information.println("(poly_rep==null),Part name:",thePart.Name().toString());
        	return false;
        }
		
		material_array material=new material_array();
		body_collctor my_body_collctor=new body_collctor(thePart);
		
        String part_user_name;
        if((part_user_name=repace_string.replace(thePart.Name().toString())).length()<=0)
        	part_user_name="null_part_user_name";
        
        String mesh_file_name		="part_"+part_id+".mesh";
        String material_file_name	="part_"+part_id+".material";
        String decription_file_name	="part_"+part_id+".decription";
        String audio_file_name		="part_"+part_id+".mp3";

        part_list_fw.println("/*	part user name				*/		",part_user_name);
        part_list_fw.println("/*	part system name			*/		","cad_part_"+part_id);
        part_list_fw.println("/*	part mesh file name			*/		",mesh_file_name);
        part_list_fw.println("/*	part material file name		*/		",material_file_name);
        part_list_fw.println("/*	part description file name	*/		",decription_file_name);
        part_list_fw.println("/*	part audio file name		*/		",audio_file_name);
        part_list_fw.println();

        file_writer fw=new file_writer(target_directory_name+mesh_file_name,charset_name);
        file_writer face_fw=new file_writer(
				fw.directory_name+fw.file_name+".face",fw.get_charset());
        file_writer edge_fw=new file_writer(
				fw.directory_name+fw.file_name+".edge",fw.get_charset());
		       
        int triangle_number=0,body_number=my_body_collctor.body_array.length;
        fw.println("/*	version			*/	2020.08.20");
        fw.println("/*	origin material	*/	0	0	0	0");
        fw.println("/*	body_number		*/	",body_number+1);
        
        for(int body_id=0;body_id<body_number;body_id++) {
        	face_collector body=my_body_collctor.body_array[body_id];
        	fw.println("/*	body  "+body_id+"  name   */  body_"+body_id+"   /*   face_number   */  ",body.face_array.length);
        	
        	for(int face_id=0,face_number=body.face_array.length;face_id<face_number;face_id++) {
        		 fw.println("	/* face  name   */  face_",face_id);
        		 fw.println("	/* face_type   */  unknown  /*   parameter_number   */  0 /*   parameter  */ ");
        		 triangle_number+=dump_triangle_set(external_flag,fw,face_fw,
        				 poly_rep.Triangulation(body.face_array[face_id]),
        				 body.appearance_array[face_id],material,default_material);

        		 edge_collector my_edge_collector=new edge_collector(body.face_array[face_id]);
        		 
        		 fw.println("		/* face_loop_number   */ 6");
        		 new line_collector(my_edge_collector).		write_out(fw);
        		 new circle_collector(my_edge_collector).	write_out(fw,edge_fw,
        				 chordal_deflection,angular_deflection,max_step_number,external_flag);
        		 new ellipse_collector(my_edge_collector).	write_out(fw,edge_fw,
        				 chordal_deflection,angular_deflection,max_step_number,external_flag);
        		 new hyperbola_collector(my_edge_collector).write_out(fw,edge_fw,
        				 chordal_deflection,angular_deflection,max_step_number,external_flag);
        		 new parabola_collector(my_edge_collector).	write_out(fw,edge_fw,
        				 chordal_deflection,angular_deflection,max_step_number,external_flag);
        		 new b_spline_collector(my_edge_collector).	write_out(fw,edge_fw,
        				 chordal_deflection,angular_deflection,max_step_number,external_flag);
        	}
        }
        
        fw.println();
        fw.print  ("/*	body  "+body_number+"  name   */  body_"+body_number+"   /*   face_number   */  ");
        if(triangle_number>0)
        	fw.println("0");
        else{
        	ModelData_PolyShapeList mesh_list;
            if((mesh_list=poly_rep.Get())==null) {
            	debug_information.println("((mesh_list=poly_rep.Get())==null),Part name:"+thePart.Name().toString());
            	return false;
            }
            long poly_face_number;
            if((poly_face_number=mesh_list.Size())<=0) {
            	debug_information.println("((face_number=mesh_list.Size())<=0),Part name:"+thePart.Name().toString());
            	return false;
            }
        	fw.println(poly_face_number);
        	for(int index_id,number,i=0;i<poly_face_number;i++){
        		fw.println("	/* face  name   */  my_face_",i);
        		fw.println("	/* face_type   */  unknown  /*   parameter_number   */  0 /*   parameter  */");  
        		ModelData_PolyVertexSet poly_vertex_set=mesh_list.Access(i);

        		ModelData_Appearance parent_appearance=null;
        		if((number=my_body_collctor.body_appearance.length)>0)
    				if((parent_appearance=my_body_collctor.body_appearance[index_id=i%number])==null)
    					for(int j=0,nj=my_body_collctor.body_array[index_id].face_array.length;j<nj;j++)
    						if((parent_appearance=my_body_collctor.body_array[index_id].appearance_array[j])!=null)
    							break;
        		
                if(poly_vertex_set.TypeId()!=ModelData_IndexedTriangleSet.GetTypeId())
                	dump_triangle_set(external_flag,fw,face_fw,
                		null,parent_appearance,material,default_material);
                else
                	dump_triangle_set(external_flag,fw,face_fw,
                		ModelData_IndexedTriangleSet.Cast(poly_vertex_set),
                		parent_appearance,material,default_material);
                fw.println("		/* face_loop_number   */ 0");
            }
        }

        fw.println();
        fw.println();

        fw.close();
        face_fw.close();
        edge_fw.close();
        
        if(!external_flag) {
        	file_writer.file_delete(face_fw.directory_name+face_fw.file_name);
        	file_writer.file_delete(edge_fw.directory_name+edge_fw.file_name);	
        }
        
        material.write_material(target_directory_name+material_file_name,charset_name);
        
        return true;
    }

	private void external_dump_triangle_set(file_writer fw,file_writer face_fw,
			ModelData_IndexedTriangleSet theTS,ModelData_Appearance my_app,
			material_array material,material default_material,
			int material_id,int triangle_number,
			boolean normal_flag,boolean texture_flag,boolean color_flag)
	{
		int vertex_number=0;
		double min_x=0,min_y=0,min_z=0,max_x=0,max_y=0,max_z=0;
		double material_red=0,material_green=0,material_blue=0,material_alf=1;
		
		double box_attribute[]=new double[] {0,0,0,1,0,0,0,1};
		
		for(int i=0;i<triangle_number;i++) {
			
			material_red	=normal_flag?1:-1;
			material_green	=texture_flag?1:-1;
			material_blue	=color_flag?1:-1;
			material_alf	=material_id;
			
        	face_fw.print  ("/*	face_primitive	",i);
        	face_fw.print  ("	material	*/	",	material_red);
        	face_fw.print  ("	",				material_green);
        	face_fw.print  ("	",				material_blue);
        	face_fw.println("	",				material_alf);
        	face_fw.println();
        	
        	for(int j=0;j<3;j++){
        		ModelData_Point vertex=theTS.Coordinate (i, j);
        		double x=vertex.X(),y=vertex.Y(),z=vertex.Z();
        		if((vertex_number++)==0) {
        			min_x=x;	min_y=y;	min_z=z;
        			max_x=x;	max_y=y;	max_z=z;
        		}else {
        			min_x=(x<min_x)?x:min_x;	min_y=(y<min_y)?y:min_y;	min_z=(z<min_z)?z:min_z;
        			max_x=(x>max_x)?x:max_x;	max_y=(y>max_y)?y:max_y;	max_z=(z>max_z)?z:max_z;
        		}
        		
        		String str=i+":"+j+"  */";
        		
        		face_fw.print  ("/*	face_vertex	",str);
        		face_fw.print  ("	",x);
        		face_fw.print  ("	",y);
        		face_fw.print  ("	",z);
        		face_fw.println("	1");

        		ModelData_Vectorf normal=theTS.VertexNormal (i, j);
        		face_fw.print  ("/*	face_normal	",str);
        		face_fw.print  ("	",normal.X());
        		face_fw.print  ("	",normal.Y());
        		face_fw.print  ("	",normal.Z());
        		face_fw.println("	1");
        		
        		face_fw.print  ("/*	face_texture",str);
        		if(texture_flag) {
        			ModelData_Point2d texture = theTS.UVCoordinate (i, j);
            		face_fw.print  ("	",texture.X());
            		face_fw.print  ("	",texture.Y());
            		face_fw.println("	0	1");
            		box_attribute[0]=texture.X();
            		box_attribute[1]=texture.Y();
                }else {
                	face_fw.println("	0	0	0	1");
                	box_attribute[0]=0;
                	box_attribute[1]=0;
                }
        		box_attribute[2]=0;
        		box_attribute[3]=1;
        		
        		face_fw.print  ("/*	face_color	",str);
        		if (color_flag) {
        			ModelData_Color color = theTS.VertexColor (i, j);
            		face_fw.print  ("	",color.R());
            		face_fw.print  ("	",color.G());
            		face_fw.print  ("	",color.B());
            		face_fw.println("	",color.A());
            		
            		box_attribute[4]=color.R();
            		box_attribute[5]=color.G();
            		box_attribute[6]=color.B();
            		box_attribute[7]=color.A();
                }else{
                	face_fw.print  ("	",material.red);
            		face_fw.print  ("	",material.green);
            		face_fw.print  ("	",material.blue);
            		face_fw.println("	",material.alfa);
            		
            		box_attribute[4]=material.red;
            		box_attribute[5]=material.green;
            		box_attribute[6]=material.blue;
            		box_attribute[7]=material.alfa;
                }
        		
        		face_fw.println();
        	}
		}

		fw.println("		/* face_attribute_number	*/	external 2");
		fw.println("		/* face_primitive_number	*/	",triangle_number);
		fw.println("		/* face_box					*/	",
				min_x+"	"+min_y+"	"+min_z+"	"+max_x+"	"+max_y+"	"+max_z);
		fw.println("		/* face_box_material		*/	",
			material_red+"	"+material_green+"	"+material_blue+"	"+material_alf);
		fw.print  ("		/* face_box_attribute 0	*/	",box_attribute[0]);
		fw.print  ("	",box_attribute[1]);
		fw.print  ("	",box_attribute[2]);
		fw.println("	",box_attribute[3]);
		
		fw.print  ("		/* face_box_attribute 1	*/	",box_attribute[4]);
		fw.print  ("	",box_attribute[5]);
		fw.print  ("	",box_attribute[6]);
		fw.println("	",box_attribute[7]);
		
		fw.println();
	}
	
	private void default_dump_triangle_set(file_writer fw,
			ModelData_IndexedTriangleSet theTS,ModelData_Appearance my_app,
			material_array material,material default_material,
			int material_id,int triangle_number,
			boolean normal_flag,boolean texture_flag,boolean color_flag)
	{
        fw.println("		/* face_attribute_number	*/	2");
        fw.println("		/* face_vertex_number		*/  ",3*triangle_number);
        for(int i=0,k=0;i<triangle_number;i++)
        	for(int j=0;j<3;j++){
        		ModelData_Point vertex=theTS.Coordinate (i, j);
        		fw.print  ("			/* face_vertex  ",k++);
        		fw.print  ("  is  */  ",vertex.X());
        		fw.print  ("  ",		vertex.Y());
        		fw.print  ("  ",		vertex.Z());
        		fw.println("  1.0");
        	}
        if (normal_flag) {
        	fw.println("		/* face_normal_number		*/  ",3*triangle_number);
        	for (int i=0,k=0;i<triangle_number;i++)
        		for(int j=0;j<3;j++){
            		ModelData_Vectorf normal=theTS.VertexNormal (i, j);
            		fw.print  ("			/* face_normal  ",k++);
            		fw.print  ("  is  */  ",normal.X());
            		fw.print  ("  ",		normal.Y());
            		fw.print  ("  ",		normal.Z());
            		fw.println("  1.0");
            	}
        }else {
        	fw.println("		/* face_normal_number		*/	1");
        	fw.println("			/* face_normal  0  is  */	0	0	0	1");
        }
        if(texture_flag) {
        	fw.println("		/* face_texture_number		*/  ",3*triangle_number);
        	for (int i=0,k=0;i<triangle_number;i++)
        		for(int j=0;j<3;j++){
            		ModelData_Point2d texture = theTS.UVCoordinate (i, j);
            		fw.print  ("			/* face_texture  ",k++);
            		fw.print  ("  is  */  ",texture.X());
            		fw.print  ("  ",		texture.Y());
            		fw.println("  0	1");
            	}
        }else {
        	fw.println("		/* face_texture_number		*/	1");
        	fw.println("			/* face_texture  0  is  */	0	0	0	1");
        }
        if (color_flag) {
        	fw.println("		/* face_color_number		*/	",3*triangle_number);
        	for (int i=0,k=0;i<triangle_number;i++)
        		for(int j=0;j<3;j++){
            		ModelData_Color color = theTS.VertexColor (i, j);
            		fw.print  ("			/* face_color  ",k++);
            		fw.print  ("  is  */  ",color.R());
            		fw.print  ("  ",		color.G());
            		fw.print  ("  ",		color.B());
            		fw.println("  ",		color.A());
            	}
        }else{
        	fw.println("		/* face_color_number		*/	1");
        	fw.print  ("			/* face_color  0  is  */	",	material.red);
        	fw.print  ("  ",										material.green);
    		fw.print  ("  ",										material.blue);
    		fw.println("  ",										material.alfa);
        }

        fw.println("		/* face_primitive_number  */  ",triangle_number);

        for(int i=0;i<triangle_number;i++) {
        	String str="			/* face_primitive  "+i+"	";

        	fw.print  (str);
        	fw.print  ("material		*/	",	normal_flag	?"1":"-1");
        	fw.print  ("	",					texture_flag?"1":"-1");
        	fw.print  ("	",					color_flag	?"1":"-1");
        	fw.println("	",					material_id);
        	
        	fw.print  (str);	fw.println("vertex_index	*/	",(3*i)+"	"+(3*i+1)+"	"+(3*i+2)+"	-1");
 
        	if (normal_flag)
        		{fw.print  (str);fw.println("normal_index	*/	",(3*i)+"	"+(3*i+1)+"	"+(3*i+2)+"	-1");}
        	else
        		{fw.print  (str);fw.println("normal_index	*/	0	0	0	-1");}
        	
        	if (texture_flag)
        		{fw.print  (str);fw.println("texture_index	*/	",(3*i)+"	"+(3*i+1)+"	"+(3*i+2)+"	-1");}
        	else
        		{fw.print  (str);fw.println("texture_index	*/	0	0	0	-1");}
        	
        	if (color_flag)
        		{fw.print  (str);fw.println("color_index		*/	",(3*i)+"	"+(3*i+1)+"	"+(3*i+2)+"	-1");}
        	else
        		{fw.print  (str);fw.println("color_index		*/	0	0	0	-1");}
        }
        fw.println();
	}
	private int dump_triangle_set (boolean external_flag,file_writer fw,file_writer face_fw,
			ModelData_IndexedTriangleSet theTS,ModelData_Appearance parent_appearance,
			material_array material,material default_material)
    {
		if(theTS==null){
			fw.println("		/*	face_attribute_number	*/	0");
	    	fw.println("		/*	face_vertex_number		*/  0");
	    	fw.println("		/*	face_normal_number		*/	0");
	    	fw.println("		/*	face_primitive_number	*/  0");
	    	fw.println();
	    	
	    	return 0;
		}
		boolean normal_flag	=theTS.HasNormals();
        boolean texture_flag=theTS.HasUVCoordinates();
        boolean color_flag	=theTS.HasColors();
        
		ModelData_Appearance my_app;
		if((my_app=theTS.Appearance())==null)
			my_app=parent_appearance;
		
		int material_id=material.touch(my_app,default_material);
		int triangle_number=theTS.NumberOfFaces();
        
		if(external_flag)
			external_dump_triangle_set(fw,face_fw,theTS,my_app,material,default_material,
					material_id,triangle_number,normal_flag,texture_flag,color_flag);
		else
			default_dump_triangle_set(fw,theTS,my_app,material,default_material,
	    			material_id,triangle_number,normal_flag,texture_flag,color_flag);
       return triangle_number;
    }
}

class part_collector
{
	public ModelData_Part part_array[];
	public int number_array[],current_id;
	
	public part_collector()
	{
		part_array=new ModelData_Part[0];
		number_array=new int[0];
		
		current_id=0;
	}
	public int touch(ModelData_Part my_part)
	{		
		for(int i=0,ni=part_array.length;i<ni;i++)
			if(part_array[i].equals(my_part)) {
				current_id=i;
				return (number_array[i]++);
			}
		
		ModelData_Part bak_part[]=part_array;
		int bak_number[]=number_array;
		part_array=new ModelData_Part[part_array.length+1];
		number_array=new int[number_array.length+1];
		
		for(int i=0,ni=bak_part.length;i<ni;i++) {
			part_array[i]=bak_part[i];
			number_array[i]=bak_number[i];
		}
		part_array[part_array.length-1]=my_part;
		number_array[number_array.length-1]=1;
		current_id=part_array.length-1;
		
		return 0;
	}
}

class name_collector
{
	public String name_array[];
	public int number_array[],current_id;
	
	public name_collector()
	{
		name_array=new String[0];
		number_array=new int[0];
		
		current_id=0;
	}
	
	public int touch(String my_name)
	{		
		for(int i=0,ni=name_array.length;i<ni;i++)
			if(name_array[i].compareTo(my_name)==0) {
				current_id=i;
				return (number_array[i]++);
			}
		
		String bak_name[]=name_array;
		int bak_number[]=number_array;
		name_array=new String[name_array.length+1];
		number_array=new int[number_array.length+1];
		
		for(int i=0,ni=bak_name.length;i<ni;i++) {
			name_array[i]=bak_name[i];
			number_array[i]=bak_number[i];
		}
		name_array[name_array.length-1]=my_name;
		number_array[number_array.length-1]=1;
		current_id=name_array.length-1;
		
		return 0;
	}
}
class assemble_tree_node
{
	public String component_name,part_name;
	public double matrix_data[];
	public assemble_tree_node parent,children[];
	
	public assemble_tree_node(double my_matrix_data[],assemble_tree_node my_parent)
	{
		matrix_data=my_matrix_data;
		component_name=null;
		part_name=null;
		parent=my_parent;
		children=new assemble_tree_node[0];
	}
	public int display(file_writer fw,	file_writer jason_fw,
			name_collector component_name_collector,
			String pre_str,int create_component_number)
	{
		String str;
		
		parent=null;
		
		if((str=component_name)==null)
			str="NULL_component_name";
		else 
			str=repace_string.replace(str);
		if(str.length()<=0)
			str="zero_length_component_name";
		str+="_"+component_name_collector.touch(str);
		fw.println(pre_str,str);
		
		jason_fw.println(pre_str+"	","{");
		jason_fw.println(pre_str+"		\"component_name\":	\"",str+"\",");
		
		if((str=part_name)==null)
			str="NULL_part_name";
		else 
			str=repace_string.replace(str);
		if(str.length()<=0)
			str="zero_length_part_name";
		fw.println(pre_str,str);
		
		jason_fw.println(pre_str+"		\"part_name\"		:	\"",str+"\",");
		jason_fw.print  (pre_str+"		\"location\"		:	");
		for(int i=0;i<16;i++)
			jason_fw.print((i<=0)?"[":",",matrix_data[i]);
		jason_fw.println("],");
		jason_fw.println(pre_str+"		\"children\"		:	[");
		
		fw.print  (pre_str);
		for(int i=0;i<16;i++)
			fw.print(Double.toString(matrix_data[i]),"\t");
		fw.println();
		fw.println(pre_str,children.length);
		
		create_component_number++;
		
		String child_pre_str=pre_str+"	";
		for(int i=0,ni=children.length;i<ni;i++) {
			create_component_number=children[i].display(fw,jason_fw,
				component_name_collector,child_pre_str,create_component_number);
			jason_fw.println((i<(ni-1))?",":"");
		}
		jason_fw.println(pre_str+"		]");
		jason_fw.print  (pre_str,"	}");
		
		return create_component_number;
	}
}

class component_visitor extends ModelData_Model.VoidElementVisitor
{
	public assemble_tree_node t;

	public part_collector collect_part;
	public int id,instance_number;

	private String target_directory_name,charset_name;
	private file_writer part_list_fw;
	private material default_material;
	
	private double chordal_deflection,angular_deflection;
	private int max_step_number;
	private boolean external_flag;
	
	public component_visitor(String my_target_directory_name,
			String my_charset_name,material my_default_material,
			double my_chordal_deflection,double my_angular_deflection,
			int my_max_step_number,boolean my_external_flag)
    {
		t=new assemble_tree_node(
				new double[] {
						1,0,0,0,
						0,1,0,0,
						0,0,1,0,
						0,0,0,1
				},null);
		t.component_name="cad_root_component";
		t.part_name="cad_root_part";
		
		id=0;
		instance_number=0;
		
		collect_part=new part_collector();
		
		target_directory_name=my_target_directory_name;
		charset_name=my_charset_name;
		
		part_list_fw=new file_writer(target_directory_name+"part.list",charset_name);
		
		default_material=my_default_material;
		chordal_deflection=my_chordal_deflection;
		angular_deflection=my_angular_deflection;
		max_step_number=my_max_step_number;
		external_flag=my_external_flag;
    }
	public void Apply (ModelData_Part thePart)
    {
		int touch_result=collect_part.touch(thePart);
		if(touch_result==0){
			debug_information.println(
				"Total part_number:"+collect_part.part_array.length
				+",touch_result:"+touch_result
				+",Part Apply:"+thePart.Name().toString());
		
			if((new part_exporter()).do_part_export(
				external_flag,thePart,collect_part.current_id,
				part_list_fw,target_directory_name,charset_name,default_material,
				chordal_deflection,angular_deflection,max_step_number)?false:true)
			{
					collect_part.number_array[collect_part.current_id]--;
			}
		}
		t.part_name="cad_part_"+(collect_part.current_id);
    }
    public boolean VisitEnter (ModelData_Instance theInstance)
    {
//    	debug_information.println("Total instance number:"+(instance_number++)+",Instance VisitEnter:"+theInstance.Name().toString());
    	
    	double matrix_data[];
    	if(theInstance.HasTransformation()) {
    		ModelData_Transformation p=theInstance.Transformation();
    		matrix_data=new double[] {
					p.Data(0,0),p.Data(1,0),p.Data(2,0),0,
					p.Data(0,1),p.Data(1,1),p.Data(2,1),0,
					p.Data(0,2),p.Data(1,2),p.Data(2,2),0,
					p.Data(0,3),p.Data(1,3),p.Data(2,3),1
			};
    	}else
    		matrix_data=new double[] {
						1,0,0,0,
						0,1,0,0,
						0,0,1,0,
						0,0,0,1
				};
    	assemble_tree_node this_tree_node=new assemble_tree_node(matrix_data,t);
    	
    	assemble_tree_node bak[]=t.children;
    	t.children=new assemble_tree_node[bak.length+1];
    	for(int i=0,ni=bak.length;i<ni;i++)
    		t.children[i]=bak[i];
    	t.children[t.children.length-1]=this_tree_node;
    	t=this_tree_node;
        return true;
    }
    public void VisitLeave (ModelData_Instance theInstance)
    {
 //   	debug_information.println("Instance VisitLeave:"+theInstance.Name().toString());
    	
    	if((t.component_name=theInstance.Name().toString()).length()<=0) {
    		ModelData_SceneGraphElement sge=theInstance.Reference();
    		if((t.component_name=sge.Name().toString()).length()<=0)
    			t.component_name="unknown_component_name"+"-"+(id++);
    	}
    	assemble_tree_node parent_tree_node=t.parent;
    	t.parent=null;
    	t=parent_tree_node;
    }
    public boolean create_file()
    {
    	part_list_fw.close();
    	
    	file_writer fw=new file_writer(target_directory_name+"assemble.assemble",charset_name);
    	file_writer jason_fw=new file_writer(target_directory_name+"assemble.jason",charset_name);
    	
    	jason_fw.println("{");
    	jason_fw.println("	\"component_name\"	:	\"cad_root_component\",");
    	jason_fw.println("	\"part_name\"			:	\"cad_root_part\",");
    	jason_fw.println("	\"location\"			:	[1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1],");
    	jason_fw.println("	\"children\"			:	[");
    	
    	fw.println("/*");
    	
    	int part_component_number=0;
    	for(int i=0,ni=collect_part.part_array.length;i<ni;i++) 
    		part_component_number+=collect_part.number_array[i];

    	fw.println("		part number:",collect_part.part_array.length);
    	fw.println("		part_component_number:",part_component_number);
    	fw.println();

		for(int i=0,ni=collect_part.part_array.length;i<ni;i++) {
			fw.print  ("		",collect_part.number_array[i]);
			fw.println("		",collect_part.part_array[i].Name().toString());
		}

    	fw.println("*/");
    	fw.println();
    	fw.println();
    	
    	int ret_val;
    	if(t.children.length>0)
    		ret_val=t.display(fw,jason_fw,new name_collector(),"",0)+collect_part.part_array.length;
    	else {
    		ret_val=collect_part.part_array.length;
    		
    		fw.println("cad_root_component");
    		fw.println("cad_root_part");
    		fw.println("1.0	0.0	0.0	0.0		0.0	1.0	0.0	0.0		0.0	0.0	1.0	0.0		0.0	0.0	0.0	1.0");
    		fw.println(ret_val);
    		
    		for(int i=0;i<ret_val;i++) {
    			fw.println();
    			fw.println("		cad_component_",i);
    			fw.println("		cad_part_",i);
    			fw.println("		1.0	0.0	0.0	0.0		0.0	1.0	0.0	0.0		0.0	0.0	1.0	0.0		0.0	0.0	0.0	1.0");
    			fw.println("		",0);
    		}
    		fw.println();
    	}
        fw.close();
        
        jason_fw.println();
        jason_fw.println("	]");
        jason_fw.println("}");
        jason_fw.close();
        
        return (ret_val>0);
    }
}
public class cadex_converter
{
	private static Base_Reader get_reader(String type_name)
	{
		switch(type_name.toLowerCase()){
		default:
			return null;
		case "igs":
		case "iges":
			return new cadex.IGES_Reader();
		case "stp":
		case "step":
			STEP_Reader my_reader = new cadex.STEP_Reader();
			STEP_ReaderParameters reader_parameter = my_reader.Parameters();
	        reader_parameter.SetPreferredBRepRepresentationType(cadex.STEP_ReaderParameters.BRepRepresentationType.AdvancedBRep);
	        return my_reader;
		case "sat":
		case "sab":
			return new cadex.ACIS_Reader();
		case "x_t":
		case "x_b":
		case "xmt_txt":
		case "xmt_bin": 
		case "xmp_txt":
		case "xmp_bin":
			return new cadex.Para_Reader();
		case "jt":
			return new cadex.JT_Reader();
		case "sldprt":
		case "sldasm":
			return new cadex.SLD_Reader();
		case "asm":
		case "prt":
			return new cadex.Creo_Reader();
		case "ifc":
			return new cadex.IFC_Reader();
		case "3dm":
			return new cadex.Rhino_Reader();
		case "obj":
			return new cadex.OBJ_Reader();
		case "stl":
			return new cadex.STL_Reader();
		case "wrl":
			return new cadex.VRML_Reader();
//		case "x3d":	
//			return new cadex.X3D_Reader();
		case "fbx":
			return new cadex.FBX_Reader();
		case "dxf":
			return new cadex.DXF_Reader();
		case "dae":
			return new cadex.Collada_Reader();
		case "3ds":
			return new cadex.A3DS_Reader();
		case "brep":
			return new cadex.BRep_Reader();
		}
	}
	private static boolean do_convert(
			String source_directory_name,String source_file_name,
			String target_directory_name,String config_directory_name,
			String target_charset,double chordal_deflection,double angular_deflection,
			int max_step_number,boolean external_flag)
	{
		target_charset=(target_charset!=null)?target_charset:(Charset.defaultCharset().name());

		target_directory_name=file_reader.separator(target_directory_name);
		if(target_directory_name.charAt(target_directory_name.length()-1)!=File.separatorChar)
			target_directory_name+=File.separator;

		String license_file_name=config_directory_name+"license.txt";
		String default_material_file_name=config_directory_name+"material.txt";

		debug_information.println("CAD Exchanger SDK begin convertion",",directory_name is "	+source_directory_name);
		debug_information.println("CAD Exchanger SDK begin convertion",",file_name is "			+source_file_name);
		debug_information.println("CAD Exchanger SDK begin convertion",",license file name is "	+license_file_name);
		
		if(!(new File(license_file_name).exists())) {
			debug_information.println("license file NOT exist:	",license_file_name);
			return true;
		}
		if(cad_license_key.test_license(license_file_name,target_charset)) {
			debug_information.println("CAD Exchanger SDK convertion terminated:","license error. file_name is "+license_file_name);
			return true;
		}
		int index_id;
		if((index_id=(source_directory_name+source_file_name).lastIndexOf('.'))<0) {
			debug_information.println("CAD Exchanger SDK convertion terminated:",
					"unrecognized file type,file name is "+source_file_name);
			return true;
		}
		File f=new File(source_directory_name+source_file_name);
		if(!(f.exists())) {
			debug_information.println("CAD Exchanger SDK convertion terminated:",
					"3D source file NOT exist,file name is "+source_directory_name+source_file_name);
			return true;
		}
		if(!(f.isFile())) {
			debug_information.println("CAD Exchanger SDK convertion terminated:",
					"3D source file NOT regular file,file name is "+source_directory_name+source_file_name);
			return true;
		}
		Base_Reader my_reader;
		if((my_reader=get_reader((source_directory_name+source_file_name).substring(index_id+1)))==null) {
			debug_information.println("CAD Exchanger SDK convertion terminated:",
					"CAD Exchanger SDK creates reader fail,file name is "+source_file_name);
			return true;
		}
		debug_information.println("CAD Exchanger SDK begin reading file,file name is "+source_file_name);
		if (!my_reader.ReadFile(new Base_UTF16String (source_directory_name+source_file_name))) {
        	debug_information.println("CAD Exchanger SDK failed to read file. file name is ",
        			source_directory_name+source_file_name);
        	return true;
        }

		debug_information.println("CAD Exchanger SDK begin transfering,file name is "+source_file_name);
		ModelData_Model my_model=new ModelData_Model();
        if (!my_reader.Transfer (my_model)) {
        	debug_information.println("CAD Exchanger SDK convertion terminated:",
        			"CAD Exchanger SDK failed to transfer,file name is	"+source_file_name);
        	return true;
        }
        debug_information.println("CAD Exchanger SDK begin creating mesh,file name is "+source_file_name);
        
        ModelAlgo_BRepMesherParameters my_parameter=new ModelAlgo_BRepMesherParameters();
        
        my_parameter.SetGenerateFaceMeshAssociations(true);
        
        if((chordal_deflection>0.0)&&(angular_deflection>0.0)){
        	debug_information.println("CAD Exchanger SDK: chordal_deflection="+chordal_deflection+",angular_deflection="+angular_deflection);
        	my_parameter.SetChordalDeflection(Math.abs(chordal_deflection));
        	my_parameter.SetAngularDeflection(Math.abs(angular_deflection));
        }else {
        	switch((int)(Math.round((chordal_deflection<angular_deflection)?chordal_deflection:angular_deflection))){
        	case 0:
        		debug_information.println("CAD Exchanger SDK: fine");
        		my_parameter.SetGranularity(ModelAlgo_BRepMesherParameters.Granularity.Fine);
        		break;
        	case -1:
        		debug_information.println("CAD Exchanger SDK: Medium");
        		my_parameter.SetGranularity(ModelAlgo_BRepMesherParameters.Granularity.Medium);
        		break;
        	case -2:
        	default:
        		debug_information.println("CAD Exchanger SDK: Coarse");
        		my_parameter.SetGranularity(ModelAlgo_BRepMesherParameters.Granularity.Coarse);
        		break;
        	}
        }
        ModelAlgo_BRepMesher my_mesher=new ModelAlgo_BRepMesher(my_parameter);
        
        debug_information.println("CAD Exchanger SDK begin computing mesh.....");
        my_mesher.Compute(my_model,true);

        debug_information.println("CAD Exchanger SDK begin traversing scene tree,file name is "+source_file_name);
        component_visitor cv=new component_visitor(
        		target_directory_name,target_charset,
        		new material(default_material_file_name,target_charset),
        		my_parameter.ChordalDeflection(),my_parameter.AngularDeflection(),
        		max_step_number,external_flag);
        my_model.Accept(cv);
        
        debug_information.println("CAD Exchanger SDK begin creating file,file name is "+source_file_name);
        
        if(cv.create_file()) {
	        (new file_writer(target_directory_name+"token.txt",null)).close();
			debug_information.println("CAD Exchanger SDK convertion success,file name is "+source_file_name);
			return false;
        }else {
        	file_writer.file_delete(target_directory_name+"token.txt");
			debug_information.println("CAD Exchanger SDK convertion fail,file name is "+source_file_name);
			return true;
        }
	}
	public static void main(String args[])
	{
		String source_directory_name		=args[0];
		String source_file_name				=args[1];
		String target_directory_name		=args[2];
		String config_directory_name		=args[3];
		String charset						=args[4];
		double chordal_deflection			=Double.parseDouble(args[5]);
		double angular_deflection			=Double.parseDouble(args[6]);
		int	   max_step_number				=Integer.decode(args[7]);
		boolean external_flag				=(args[8].toLowerCase().compareTo("external")==0);
		
		debug_information.println("cadex_converter source_directory_name:	",	source_directory_name);
		debug_information.println("cadex_converter source_file_name:	",		source_file_name);
		debug_information.println("cadex_converter target_directory_name:	",	target_directory_name);
		debug_information.println("cadex_converter config_directory_name:	",	config_directory_name);
		debug_information.println("cadex_converter charset :		",			charset);
		debug_information.println("cadex_converter chordal_deflection:	",		chordal_deflection);
		debug_information.println("cadex_converter angular_deflection:	",		angular_deflection);
		debug_information.println("cadex_converter max_step_number:	",			max_step_number);
		debug_information.println("cadex_converter external_flag:	",			external_flag?"external":"NOT external");

		do_convert(source_directory_name,source_file_name,target_directory_name,
			config_directory_name,charset,chordal_deflection,angular_deflection,
			max_step_number,external_flag);
	}
}
