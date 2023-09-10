package kernel_engine;

import java.io.File;
import java.util.ArrayList;


import kernel_part.part;
import kernel_render.render;
import kernel_component.component;
import kernel_driver.component_driver;
import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_file_manager.file_directory;
import kernel_common_class.jason_string;
import kernel_common_class.class_file_reader;
import kernel_common_class.common_reader;
import kernel_common_class.debug_information;
import kernel_common_class.compress_file_data;
import kernel_component.component_initialization;
import kernel_interface.client_process_bar;
import kernel_network.client_request_response;
import kernel_program_reader.program_file_reader;
import kernel_file_manager.travel_through_directory;

public class engine_initialization
{
	class output_component_program
	{
		private int print_number;
		private file_writer fw;
		public output_component_program(file_writer my_fw)
		{
			print_number=0;
			fw=my_fw;
		}
		public void output(component comp,String program_text)
		{
			if((print_number++)>0)
				fw.println(",");
			fw.println("\t{");
			
			fw.println("\t\t\"component_id\"			:	",	comp.component_id+",");
			fw.println("\t\t\"component_name\"			:	",	jason_string.change_string(comp.component_name)+",");
			fw.println("\t\t\"initialization_function\"	:	",	program_text);
			
			fw.print  ("\t}");
		}
	}

	class output_all_component_program  extends travel_through_directory
	{
		private component comp;
		private String file_charset;
		private output_component_program ocp;
		
		public void operate_file(String file_name)
		{
			ocp.output(comp,file_reader.get_text(file_name,file_charset));
		}
		public output_all_component_program(component my_comp,
				String my_file_name,String my_file_charset,output_component_program my_ocp)
		{
			comp=my_comp;
			file_charset=my_file_charset;
			ocp=my_ocp;
			do_travel(my_file_name,true);
		}
	}

	class file_last_time extends travel_through_directory
	{
		public long last_time;
		public void operate_file(String file_name)
		{
			long my_last_time;
			last_time=(last_time<(my_last_time=new File(file_name).lastModified()))?my_last_time:last_time;
		}
		public file_last_time(String file_name)
		{
			last_time=0;
			do_travel(file_name,false);
		}
	}

	private void  render_driver_initialize(engine_kernel ek,
			client_request_response request_response,client_process_bar process_bar)
	{
		render r;
		int render_number=ek.render_cont.renders.size();
		process_bar.set_process_bar(true,"render_driver_initialization","", 0, render_number);
		
		for(int render_id=0;render_id<render_number;render_id++){
			process_bar.set_process_bar(false,"render_driver_initialization","", render_id, render_number);
			if((r=ek.render_cont.renders.get(render_id))==null)
				continue;
			if(r.driver==null)
				continue;
			
			try{
				r.driver.initialize_render_driver(render_id,ek,request_response);
			}catch(Exception e){
				debug_information.println("Render driver initialize_part_driver fail:	",e.toString());
				debug_information.println("Render class name:		",	r.driver.getClass().getName());
				debug_information.println("render_id:		",			render_id);
				debug_information.println("render_name:		",			r.render_name);
				e.printStackTrace();
			}
		}
		process_bar.set_process_bar(false,"render_driver_initialization","", render_number, render_number);
		return;
	}
	private void part_driver_initialize(engine_kernel ek,
			client_request_response request_response,client_process_bar process_bar)
	{
		int process_parts_sequence[][]=ek.process_part_sequence.process_parts_sequence;
		int number=process_parts_sequence.length;
		process_bar.set_process_bar(true,"part_driver_initialization","", 0, number);
		
		for(int i=0;i<number;i++){
			int render_id=process_parts_sequence[i][0];
			int part_id  =process_parts_sequence[i][1];
			part my_p=ek.render_cont.renders.get(render_id).parts.get(part_id);
			process_bar.set_process_bar(false,"part_driver_initialization",my_p.user_name,i, number);
			
			if(my_p.driver==null)
				continue;
			try {
				my_p.driver.initialize_part_driver(my_p,ek,request_response);
			}catch(Exception e){
				debug_information.println("Part driver initialize_part_driver fail:	",e.toString());
					
				debug_information.println("Part user name:		",	my_p.user_name);
				debug_information.println("Part system name:	",	my_p.system_name);
				debug_information.println("Part mesh_file_name:",	my_p.directory_name+my_p.mesh_file_name);
				debug_information.println("Part material_file_name:",my_p.directory_name+my_p.material_file_name);
				debug_information.println("part_file_directory:",
						file_directory.part_file_directory(my_p,ek.system_par,ek.scene_par));
				e.printStackTrace();
			}
		}
		process_bar.set_process_bar(false,"part_driver_initialization","", number, number);
	}
	private void initialize_component_driver(component sort_component_array[],engine_kernel ek,
			client_request_response request_response,client_process_bar process_bar)
	{
		int number=sort_component_array.length;
		process_bar.set_process_bar(true,"component_driver_initialization","",0, number);
		for(int i=0;i<number;i++) {
			process_bar.set_process_bar(false,"component_driver_initialization",
					sort_component_array[i].component_name,i, number);
			for(int j=0,driver_number=sort_component_array[i].driver_number();j<driver_number;j++) {
				component_driver cd=sort_component_array[i].driver_array.get(j);
				try{
					cd.initialize_component_driver(sort_component_array[i],j,ek,request_response);
				}catch(Exception e) {
					debug_information.println("Component driver initialize fail:	",e.toString());
					debug_information.println("Component name:",sort_component_array[i].component_name);
					debug_information.println("Component file:",
							sort_component_array[i].component_directory_name+sort_component_array[i].component_file_name);
					debug_information.println("Component driver id:",j);
					e.printStackTrace();
				}
			}
		}
		process_bar.set_process_bar(false,"component_driver_initialization","",number,number);
	}
	private void file_initialize(component sort_component_array[],
			engine_kernel ek,client_request_response request_response,client_process_bar process_bar)
	{
		component_initialization pi;
		String destination_file_name=ek.scene_par.scene_proxy_directory_name+"initialization.gzip_js";
		
		long last_time=program_file_reader.get_system_program_last_time(ek.system_par);
		for(int render_id=0,render_number=ek.render_cont.renders.size();render_id<render_number;render_id++) {
			render r=ek.render_cont.renders.get(render_id);
			if(r==null)
				continue;
			if(last_time<r.program_last_time)
				last_time=r.program_last_time;
			String shader_file_name[][];
			if((shader_file_name=r.driver.shader_file_name_array())==null)
				continue;
			for(int i=0,ni=shader_file_name.length;i<ni;i++)
				for(int j=0,nj=shader_file_name[i].length;j<nj;j++){
				common_reader reader;
				int index_id=shader_file_name[i][j].lastIndexOf('.');
				if(shader_file_name[i][j].substring(index_id,index_id+3).toLowerCase().compareTo(".js")==0)
					reader=class_file_reader.get_reader(
							shader_file_name[i][j],r.driver.getClass(),
							ek.system_par.js_class_charset,
							ek.system_par.js_jar_file_charset);
				else if(shader_file_name[i][j].substring(index_id,index_id+4).toLowerCase().compareTo(".txt")==0)
					reader=class_file_reader.get_reader(
						shader_file_name[i][j],r.driver.getClass(),
						ek.system_par.text_class_charset,
						ek.system_par.text_jar_file_charset);
				else
					reader=class_file_reader.get_reader(
							shader_file_name[i][j],r.driver.getClass(),
							ek.system_par.local_data_charset,
							ek.system_par.local_data_charset);
				
				if(reader!=null) {
					if(!(reader.error_flag()))
						if(last_time<reader.lastModified_time)
							last_time=reader.lastModified_time;
					reader.close();
				}
			}
		}
		
		ArrayList<component> init_comp=new ArrayList<component>();
		for(int i=0,ni=sort_component_array.length;i<ni;i++) {
			if((pi=sort_component_array[i].initialization)==null)
				continue;
			if(pi.program_and_charset!=null)
				if(pi.program_and_charset.size()>0){
					init_comp.add(sort_component_array[i]);
					continue;
				}
			pi.destroy();
			sort_component_array[i].initialization=null;
		}
		int collect_init_comp_number=init_comp.size();
		
		process_bar.set_process_bar(true,"file_initialization_0","",0, collect_init_comp_number);
		for(int i=0;i<collect_init_comp_number;i++){
			component comp=init_comp.get(i);
			process_bar.set_process_bar(false,"file_initialization_0",comp.component_name,i, collect_init_comp_number);
			
			for(int j=0,nj=comp.initialization.program_and_charset.size();j<nj;j++){
				String my_program_and_charset[]=comp.initialization.program_and_charset.get(j);
				if(my_program_and_charset[1]==null){
					if(last_time<comp.uniparameter.file_last_modified_time)
						last_time=comp.uniparameter.file_last_modified_time;
					continue;
				}
				File f;
				String real_file_name;
				real_file_name=comp.component_directory_name+my_program_and_charset[0];
				if(!(f=new File(real_file_name)).exists())
					if(!(f=new File(real_file_name=ek.create_parameter.scene_directory_name+my_program_and_charset[0])).exists())
						if(!(f=new File(real_file_name=ek.scene_par.directory_name+my_program_and_charset[0])).exists())
							if(!(f=new File(real_file_name=ek.scene_par.extra_directory_name+my_program_and_charset[0])).exists())
								if(!(f=new File(real_file_name=ek.system_par.data_root_directory_name+my_program_and_charset[0])).exists()){
									comp.initialization.program_and_charset.set(j,null);
									debug_information.print  (
											"Not exist component init function,component name:	",comp.component_name);
									debug_information.println("	file_name:	",my_program_and_charset[0]);
									continue;
								}
				file_last_time flt=new file_last_time(f.getAbsolutePath());
				if(last_time<flt.last_time)
					last_time=flt.last_time;
				
				comp.initialization.program_and_charset.set(j,new String[] {real_file_name,my_program_and_charset[1]});
			}
		}
		
		process_bar.set_process_bar(false,"file_initialization_0","",collect_init_comp_number,collect_init_comp_number);
		
		if((new File(destination_file_name)).lastModified()>last_time)
			return;
		
		process_bar.set_process_bar(true,"file_initialization_1","",0, sort_component_array.length);
		
		file_writer fw=new file_writer(destination_file_name,ek.system_par.network_data_charset);

		fw.println("export var initialization_data=[");
		
		fw.println().println("[");
		{
			for(int i=0,ni=sort_component_array.length;i<ni;i++){
				process_bar.set_process_bar(false,"file_initialization_1",
						sort_component_array[i].component_name,i, ni);
				
				fw.print("\t[",jason_string.change_string(sort_component_array[i].component_name));
				fw.print(",",sort_component_array[i].component_id);
				fw.print(",[");
				
				for(int j=0,nj=sort_component_array[i].children_number();j<nj;j++)
					fw.print((j<=0)?"":",",sort_component_array[i].children[j].component_id);
				
				fw.println((i!=(ni-1))?"]],":"]]");
			}
			process_bar.set_process_bar(false,"file_initialization_1","",
					sort_component_array.length, sort_component_array.length);
		}
		fw.println("],").println("[");
		{
			int id[][][][]=ek.component_cont.part_component_id_and_driver_id;
			process_bar.set_process_bar(true,"file_initialization_2","",0, id.length);
			for(int render_id=0,render_number=id.length;render_id<render_number;render_id++){
				render r=ek.render_cont.renders.get(render_id);
				process_bar.set_process_bar(false,"file_initialization_2",r.render_name,render_id,render_number);
				fw.println("	[");
				for(int part_id=0,part_number=id[render_id].length;part_id<part_number;part_id++){
					fw.println("		[");
					for(int i=0,ni=id[render_id][part_id].length;i<ni;i++) {
						int component_id=id[render_id][part_id][i][0];
						int driver_id	=id[render_id][part_id][i][1];
						fw.print  ("			[",component_id);
						fw.print  (",",driver_id);
						fw.println("],");
					}
					part p=r.parts.get(part_id);
					fw.	print  ("			",p.permanent_render_id).println(",").
						println("			",p.permanent_part_id).
						println((part_id==(part_number-1))?"		]":"		],");
				}
				fw.println();
				fw.println((render_id==(render_number-1))?"	]":"	],");	
			}
			process_bar.set_process_bar(false,"file_initialization_2","",id.length, id.length);
		}
		fw.println("],").println("[");
		{
			process_bar.set_process_bar(true,"file_initialization_3","",0,collect_init_comp_number);
			output_component_program ocp=new output_component_program(fw);
			for(int i=0,ni=collect_init_comp_number;i<ni;i++){
				component comp=init_comp.get(i);
				process_bar.set_process_bar(false,"file_initialization_3",
						comp.component_name,i,collect_init_comp_number);
				for(int j=0,nj=comp.initialization.program_and_charset.size();j<nj;j++) {
					String my_program_and_charset[];
					if((my_program_and_charset=comp.initialization.program_and_charset.get(j))==null)
						continue;
					if(my_program_and_charset[0]==null)
						continue;
					if(my_program_and_charset[1]==null) 
						ocp.output(comp,my_program_and_charset[0]);
					else
						new output_all_component_program(
								comp,my_program_and_charset[0],my_program_and_charset[1],ocp);
				}
			}
			fw.println();
			process_bar.set_process_bar(false,"file_initialization_3","",collect_init_comp_number,collect_init_comp_number);
		}
		fw.println("],").println("[");
		{
			int render_number=ek.render_cont.renders.size();
			process_bar.set_process_bar(true,"file_initialization_4","",0,render_number);
			for(int render_id=0;render_id<render_number;render_id++) {
				render r=ek.render_cont.renders.get(render_id);
				process_bar.set_process_bar(false,"file_initialization_4",r.render_name,render_id,render_number);
				fw.	println("	[").print("		",jason_string.change_string(r.render_name));

				String shader_file_name[][]=r.driver.shader_file_name_array();
				if(shader_file_name==null)
					shader_file_name=new String[][] {};
				for(int i=0,ni=shader_file_name.length;i<ni;i++) {
					fw.println(",");
					if(i!=0)
						fw.println("		[");
					else{
						fw.println("function(render_id,render_name,");
						fw.println("	init_data,text_array,shader_code,render)");
						fw.println("{");
					}
					for(int j=0,nj=shader_file_name[i].length;j<nj;j++){
						common_reader reader;
						int index_id=shader_file_name[i][j].lastIndexOf('.');
						if(shader_file_name[i][j].substring(index_id,index_id+3).toLowerCase().compareTo(".js")==0)
							reader=class_file_reader.get_reader(
									shader_file_name[i][j],r.driver.getClass(),
									ek.system_par.js_class_charset,
									ek.system_par.js_jar_file_charset);
						else if(shader_file_name[i][j].substring(index_id,index_id+4).toLowerCase().compareTo(".txt")==0)
							reader=class_file_reader.get_reader(
								shader_file_name[i][j],r.driver.getClass(),
								ek.system_par.text_class_charset,
								ek.system_par.text_jar_file_charset);
						else
							reader=class_file_reader.get_reader(
									shader_file_name[i][j],r.driver.getClass(),
									ek.system_par.local_data_charset,
									ek.system_par.local_data_charset);
						
						String str="";
						if(reader!=null) {
							if(!(reader.error_flag()))
								str=reader.get_text();
							reader.close();
						}
						if(i==0)
							fw.println(str);
						else
							fw.print("			",jason_string.change_string(str)).
							println((j==(nj-1))?"":",");
					}	
					
					if(i!=0) 
						fw.print  ("		]");
					else{
						fw.println("	return new new_render_driver(render_id,render_name,");
						fw.println("					init_data,text_array,shader_code,render);");
						fw.print  ("}");
					}
				}
				fw.println().println().println();
				fw.println((render_id<(render_number-1))?"	],":"	]");
			}
			
			process_bar.set_process_bar(false,"file_initialization_4","",render_number,render_number);
		}
		
		fw.println("],");
		{
			String common_shader_str=null;
			common_reader reader=program_file_reader.get_system_program_reader(ek.system_par);
			if(reader!=null) 
				if(!(reader.error_flag())){
					common_shader_str=reader.get_text();
				reader.close();
			}			
			fw.	println("	",
						 ( common_shader_str==null)?"\"\""
						:((common_shader_str=common_shader_str.trim()).length()<=0)?"\"\""
						:jason_string.change_string(common_shader_str+"\n"));
		}
		fw.println().println("];").println();
		
		fw.close();
		
		String tmp_file_name=fw.directory_name+fw.file_name+".tmp";
		file_writer.file_rename(fw.directory_name+fw.file_name,tmp_file_name);
		compress_file_data.do_compress(
			new File(tmp_file_name),new File(fw.directory_name+fw.file_name),
			ek.system_par.response_block_size,"gzip");
		file_writer.file_delete(tmp_file_name);

		return;
	}
	public engine_initialization(boolean not_real_scene_fast_load_flag,
			engine_kernel ek,client_request_response request_response,client_process_bar process_bar)
	{
		component sort_component_array[];
		if((sort_component_array=ek.component_cont.get_sort_component_array())==null)
			sort_component_array=new component[] {};
		
		debug_information.println();
		debug_information.println("Begin initialize_render_driver");
		render_driver_initialize(ek,request_response,process_bar);
		debug_information.println("End initialize_render_driver");		
		
		debug_information.println();
		debug_information.println("Begin initialize_part_driver");
		part_driver_initialize(ek,request_response,process_bar);
		debug_information.println("End initialize_part_driver");
		
		debug_information.println();
		debug_information.println("Begin initialize_component_driver");
		initialize_component_driver(sort_component_array,ek,request_response,process_bar);
		debug_information.println("End initialize_component_driver");
		
		debug_information.println();
		debug_information.println("Begin create initialization file");
		if(not_real_scene_fast_load_flag)
			file_initialize(sort_component_array,ek,request_response,process_bar);
		
		for(int i=0,ni=sort_component_array.length;i<ni;i++)
			if(sort_component_array[i].initialization!=null) {
				sort_component_array[i].initialization.destroy();
				sort_component_array[i].initialization=null;
			}
		debug_information.println("End create initialization file");
	}
}
