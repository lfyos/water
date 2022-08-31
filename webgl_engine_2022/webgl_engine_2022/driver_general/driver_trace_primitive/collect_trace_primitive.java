package driver_trace_primitive;

import java.io.InputStream;

import kernel_common_class.common_reader;
import kernel_component.component;
import kernel_engine.client_information;
import kernel_engine.engine_kernel;
import kernel_file_manager.file_writer;
import kernel_part.part;
import kernel_part.primitive_interface;
import kernel_transformation.plane;
import kernel_transformation.point;

public class collect_trace_primitive 
{
	public long trace_primitive_number;
	
	public collect_trace_primitive(String file_name,engine_kernel ek,client_information ci)
	{
		if(ek.render_cont.renders==null)
			return;
		
		InputStream stream;	
		if((stream=ci.request_response.implementor.get_content_stream())==null)
			return;
		
		trace_primitive_number=0;
		
		file_writer stl_fw	=new file_writer(file_name+".stl.txt",	ek.system_par.local_data_charset);
		file_writer id_fw	=new file_writer(file_name+".id.txt",	ek.system_par.local_data_charset);
		stl_fw.println("solid	",stl_fw.file_name).println().println();
		
		int last_render_id=-1,last_part_id=-1;
		primitive_interface p_i=null;
		part p_i_p=null,p;
		
		for(common_reader cr=new common_reader(stream,
				ci.request_response.implementor.get_request_charset());;)
		{
			int render_id=cr.get_int(),part_id=cr.get_int();
			int body_id=cr.get_int(),face_id=cr.get_int(),triangle_id=cr.get_int();
			if(cr.eof()) {
				cr.close();
				break;
			}
			int data_buffer_id=cr.get_int();

			if((render_id<0)||(render_id>=ek.render_cont.renders.length))
				continue;
			if(ek.render_cont.renders[render_id]==null)
				continue;
			if(ek.render_cont.renders[render_id].parts==null)
				continue;
			if((part_id<0)||(part_id>=ek.render_cont.renders[render_id].parts.length))
				continue;
			if((p=ek.render_cont.renders[render_id].parts[part_id])==null)
				continue;
			if((body_id<0)||(body_id>=p.part_mesh.body_number()))
				continue;
			if((face_id<0)||(face_id>=p.part_mesh.body_array[body_id].face_number()))
				continue;
			int primitive_number=p.part_mesh.body_array[body_id].face_array[face_id].fa_face.total_face_primitive_number;
			if((triangle_id<0)||(triangle_id>=primitive_number))
				continue;
			int data_buffer_number=ek.component_cont.part_component_id_and_driver_id[render_id][part_id].length;
			if((data_buffer_id<0)||(data_buffer_id>=data_buffer_number))
				continue;
			int component_id=ek.component_cont.part_component_id_and_driver_id[render_id][part_id][data_buffer_id][0];
			component my_comp;
			if((my_comp=ek.component_cont.get_component(component_id))==null)
				continue;
			
			id_fw.	print  ("	",component_id).
			print  ("	",p.permanent_render_id).
			print  ("	",p.permanent_part_id).
			print  ("	",p.render_id).
			print  ("	",p.part_id).
			print  ("	",body_id).
			print  ("	",face_id).
			print  ("	",triangle_id).
			println("	",p.system_name).
			println("	",my_comp.component_name);
	
			double p0[],p1[],p2[],p3[];
			point pp0,pp1,pp2,pp3;
			plane pl;
			
			if((render_id!=last_render_id)||(part_id!=last_part_id)) {
				if(p_i!=null) {
					p.destroy_primitive_interface(p_i,ek.system_par);
					p_i=null;
					p_i_p=null;
				}
			}
			if(p_i==null) {
				p_i=(p_i_p=p).create_primitive_interface(ek.system_par,true);
				last_render_id=render_id;
				last_part_id=part_id;
			}
	
			switch(p_i.get_primitive_vertex_number(body_id,face_id,triangle_id)){
			default:
				break;
			case 3:
				trace_primitive_number++;
				p0=p_i.get_primitive_vertex_location_data(body_id,face_id,triangle_id,0);
				p1=p_i.get_primitive_vertex_location_data(body_id,face_id,triangle_id,1);
				p2=p_i.get_primitive_vertex_location_data(body_id,face_id,triangle_id,2);
				
				pp0=my_comp.absolute_location.multiply(p0[0],p0[1],p0[2]);
				pp1=my_comp.absolute_location.multiply(p1[0],p1[1],p1[2]);
				pp2=my_comp.absolute_location.multiply(p2[0],p2[1],p2[2]);
				pl=new plane(pp0,pp1,pp2);
	
				stl_fw.print  ("facet normal	",pl.A).	print("	",pl.B).	println("	",pl.C);
				stl_fw.println("	outer loop");
				stl_fw.print  ("		vertex	",pp0.x).	print("	",pp0.y).	println("	",pp0.z);
				stl_fw.print  ("		vertex	",pp1.x).	print("	",pp1.y).	println("	",pp1.z);
				stl_fw.print  ("		vertex	",pp2.x).	print("	",pp2.y).	println("	",pp2.z);
				stl_fw.println("	endloop");
				stl_fw.println("endfacet").println();
				break;
			case 4:
				trace_primitive_number++;
				trace_primitive_number++;
				p0=p_i.get_primitive_vertex_location_data(body_id,face_id,triangle_id,0);
				p1=p_i.get_primitive_vertex_location_data(body_id,face_id,triangle_id,1);
				p2=p_i.get_primitive_vertex_location_data(body_id,face_id,triangle_id,2);
				p3=p_i.get_primitive_vertex_location_data(body_id,face_id,triangle_id,3);
					
				pp0=my_comp.absolute_location.multiply(p0[0],p0[1],p0[2]);
				pp1=my_comp.absolute_location.multiply(p1[0],p1[1],p1[2]);
				pp2=my_comp.absolute_location.multiply(p2[0],p2[1],p2[2]);
				pp3=my_comp.absolute_location.multiply(p3[0],p3[1],p3[2]);
				pl=new plane(pp0,pp1,pp2);
				
				stl_fw.print  ("facet normal	",pl.A).	print("	",pl.B).	println("	",pl.C);
				stl_fw.println("	outer loop");
				stl_fw.print  ("		vertex	",pp0.x).	print("	",pp0.y).	println("	",pp0.z);
				stl_fw.print  ("		vertex	",pp1.x).	print("	",pp1.y).	println("	",pp1.z);
				stl_fw.print  ("		vertex	",pp2.x).	print("	",pp2.y).	println("	",pp2.z);
				stl_fw.println("	endloop");
				stl_fw.println("endfacet").println();
				
				stl_fw.print  ("facet normal	",pl.A).	print("	",pl.B).	println("	",pl.C);
				stl_fw.println("	outer loop");
				stl_fw.print  ("		vertex	",pp2.x).	print("	",pp2.y).	println("	",pp2.z);
				stl_fw.print  ("		vertex	",pp3.x).	print("	",pp3.y).	println("	",pp3.z);
				stl_fw.print  ("		vertex	",pp0.x).	print("	",pp0.y).	println("	",pp0.z);
				stl_fw.println("	endloop");
				stl_fw.println("endfacet").println();
				break;
			}
		}
		stl_fw.println("endsolid").println();
		
		if((p_i_p!=null)&&(p_i!=null))
			p_i_p.destroy_primitive_interface(p_i,ek.system_par);
		
		stl_fw.close();
		id_fw.close();
	}
}
