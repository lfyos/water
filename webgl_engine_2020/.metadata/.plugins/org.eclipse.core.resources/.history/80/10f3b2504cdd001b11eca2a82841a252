package format_convert;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kernel_file_manager.file_reader;
import kernel_file_manager.file_writer;
import kernel_common_class.sorter;
import kernel_common_class.debug_information;

import kernel_transformation.location;

class gltf_name_and_value
{
	public String	field_name [];
	public String	field_value[];
	
	public String get_value(String name)
	{
		name=name.toUpperCase();
		for(int i=0,ni=field_name.length;i<ni;i++)
			if(field_name[i].toUpperCase().compareTo(name)==0)
				return field_value[i];
		return null;
	}
	public void insert_default_attribute()
	{
		String default_name[]=new String[] {"POSITION","NORMAL"};
		for(int i=0,ni=default_name.length;i<ni;i++) {
			for(int j=0,nj=field_name.length;j<nj;j++)
				if(default_name[i].compareTo(field_name [j].toUpperCase())==0) {
					default_name[i]=null;
					break;
				}
			if(default_name[i]!=null){
				String	bak_name []=field_name;
				String	bak_value[]=field_value;
				field_name	=new String[field_name.length+1];
				field_value	=new String[field_value.length+1];
				for(int j=0,nj=bak_name.length;j<nj;j++) {
					field_name [j]=bak_name[j];
					field_value[j]=bak_value[j];
				}
				field_name [field_name.length-1]	=default_name[i];
				field_value[field_value.length-1]	=null;
			}
		}
	}
	public void attribute_sort()
	{
		String	bak_name []=field_name;
		String	bak_value[]=field_value;
		String defined_name[]=new String[]
		{
				"POSITION",		"NORMAL",		"TANGENT",
				"TEXCOORD_0",	"TEXCOORD_1",	"TEXCOORD_2",	"TEXCOORD_3",
				"COLOR_0",		"COLOR_1",		"COLOR_2",		"COLOR_3",
				"JOINTS_0",		"JOINTS_1",		"JOINTS_2",		"JOINTS_3",
				"WEIGHTS_0",	"WEIGHTS_1",	"WEIGHTS_2",	"WEIGHTS_3"
		};
		field_name	=new String[field_name.length];
		field_value	=new String[field_value.length];
		int number=0;
		for(int i=0,ni=defined_name.length;i<ni;i++)
			for(int j=0,nj=bak_name.length;j<nj;j++)
				if(bak_name[j]!=null)
					if(defined_name[i].compareTo(bak_name[j].toUpperCase())==0) {
						field_name [number  ]=bak_name [j];
						field_value[number++]=bak_value[j];
						bak_name[j]=null;
					}
		int default_number=number;
		for(int i=0,ni=bak_name.length;i<ni;i++)
			if(bak_name[i]!=null) {
				field_name [number  ]=bak_name [i];
				field_value[number++]=bak_value[i];
			}
		for(int i=default_number,n=field_name.length-1;i<n;i++)
			for(int j=i+1;j<=n;j++)
				if(field_name[i].compareTo(field_name[j])>0){
					String	name  =field_name [i];
					String	value =field_value[i];
					field_name [i]=field_name [j];
					field_value[i]=field_value[j];
					field_name [j]=name;
					field_value[j]=value;
				}
	}
	public gltf_name_and_value(JSONObject myobject)
	{
		try{
			Set<String> keyset=myobject.keySet();
			int number=keyset.size();
			if(number<=0) {
				field_name	=new String[0];
				field_value	=new String[0];
				return;
			}
			field_name=new String[number];
			Iterator<String> keys=keyset.iterator();
			for(int i=0;i<number;i++)
				field_name[i]=keys.next();
		}catch(Exception e) {
			field_name	=new String[0];
			field_value	=new String[0];
			return;
		}
		field_value	=new String[field_name.length];
		for(int i=0,ni=field_value.length;i<ni;i++)
			try{
				field_value[i]=myobject.getString(field_name[i]);
			}catch(Exception e){
				field_value[i]=null;
			}
	}
}

class gltf_array_or_object_union
{
	private JSONArray	union_array;
	private JSONObject	union_object;
	private String		object_id[],search_id;
	
	public JSONObject get(String object_id)
	{
		search_id=object_id;
		if(union_object!=null)
			return union_object.getJSONObject(object_id);
		else if(union_array!=null)
			return union_array.getJSONObject(Integer.decode(object_id));
		else
			return null;
	}
	public JSONObject get(int index_id)
	{
		search_id=object_id[index_id];
		if(union_array!=null)
			return union_array.getJSONObject(index_id);
		else if(union_object!=null)
			return union_object.getJSONObject(search_id);
		else
			return null;
	};
	public double get_value(String object_id,double default_value)
	{
		search_id=object_id;
		if(union_object!=null)
			return union_object.getDoubleValue(object_id);
		else if(union_array!=null)
			return union_array.getDoubleValue(Integer.decode(object_id));
		else
			return default_value;
	}
	public double get_value(int index_id,double default_value)
	{
		if((index_id<0)||(index_id>=object_id.length))
			return default_value;
		
		search_id=object_id[index_id];
		if(union_array!=null)
			return union_array.getDoubleValue(index_id);
		else if(union_object!=null)
			return union_object.getDoubleValue(search_id);
		else
			return default_value;
	};
	
	public String get_string(String object_id)
	{
		search_id=object_id;
		if(union_object!=null)
			return union_object.getString(object_id);
		else if(union_array!=null)
			return union_array.getString(Integer.decode(object_id));
		else
			return null;
	}
	public String get_string(int index_id)
	{
		search_id=object_id[index_id];
		if(union_array!=null)
			return union_array.getString(index_id);
		else if(union_object!=null)
			return union_object.getString(search_id);
		else
			return null;
	}
	
	public int get_number()
	{
		return object_id.length;
	}
	
	public String get_search_id()
	{
		return search_id;
	}
	
	public gltf_array_or_object_union(JSONObject my_jason_object,String my_name)
	{
		union_array=null;
		union_object=null;
		object_id=new String[0];
		search_id="";

		try{
			if((union_array=my_jason_object.getJSONArray(my_name))!=null) {
				object_id=new String[union_array.size()];
				for(int i=0,ni=object_id.length;i<ni;i++)
					object_id[i]=Integer.toString(i);
				return;
			}
		}catch(Exception e) {
			union_array=null;
		}
		try{
			if((union_object=my_jason_object.getJSONObject(my_name))!=null) {
				object_id=(new gltf_name_and_value(union_object)).field_name;
				return;
			}
		}catch(Exception e){
			union_object=null;
			object_id=new String[0];
		}
	}
}

class gltf_sampler
{
	public String name;
	public int mag_filter, min_filter,wrap_s,warp_t;
	public gltf_sampler(JSONObject my_object,String my_name)
	{
		name		=my_name;
		mag_filter	=my_object.getIntValue("magFilter");
		min_filter	=my_object.getIntValue("minFilter");
		wrap_s		=my_object.getIntValue("wrapS");
		warp_t		=my_object.getIntValue("wrapT");
	}	
}

class gltf_sampler_sorter extends sorter<gltf_sampler,String>
{
	public int compare_data(gltf_sampler s,gltf_sampler t)
	{
		return compare_key(s,t.name);
	}
	public int compare_key(gltf_sampler s,String t)
	{
		try{
			double x=Double.valueOf(s.name);
			double y=Double.valueOf(t);
			return ((x)<(y))?-1:(x>y)?1:0;
		}catch(Exception e){
			return s.name.compareTo(t);
		}
	}
	public gltf_sampler_sorter(JSONObject my_jason_object)
	{
		gltf_array_or_object_union gltf_sampler_array=new gltf_array_or_object_union(my_jason_object,"samplers");
		data_array=new gltf_sampler[gltf_sampler_array.get_number()];
		for(int i=0,ni=data_array.length;i<ni;i++)
			data_array[i]=new gltf_sampler(gltf_sampler_array.get(i),gltf_sampler_array.get_search_id());
		do_sort(new gltf_sampler[data_array.length]);
	}
}

class gltf_image
{
	public String name,uri;
	public gltf_image(JSONObject my_object,String my_name)
	{
		name=my_name;
		uri=my_object.getString("uri");
	}
}

class gltf_image_sorter extends sorter<gltf_image,String>
{
	public int compare_data(gltf_image s,gltf_image t)
	{
		return compare_key(s,t.name);
	}
	public int compare_key(gltf_image s,String t)
	{
		try{
			double x=Double.valueOf(s.name);
			double y=Double.valueOf(t);
			return ((x)<(y))?-1:(x>y)?1:0;
		}catch(Exception e){
			return s.name.compareTo(t);
		}
	}
	public gltf_image_sorter(JSONObject my_jason_object)
	{
		gltf_array_or_object_union my_image_array=new gltf_array_or_object_union(my_jason_object,"images");
		data_array=new gltf_image[my_image_array.get_number()];
		for(int i=0,ni=data_array.length;i<ni;i++)
			data_array[i]=new gltf_image(my_image_array.get(i),my_image_array.get_search_id());
		do_sort(new gltf_image[data_array.length]);
	}
}

class gltf_texture
{
	public String name;
	public gltf_image image;
	public gltf_sampler sampler;
	
	public void write_out(file_writer fw)
	{
		fw.println("		{");
		
		if(image==null) {
				fw.println("			\"texture_name\"	:	","\""+name+"\",");
				fw.println("			\"uri\"			:	null,");
		}else {
			if(image.name==null)
				fw.println("			\"texture_name\"	:	null,");
			else
				fw.println("			\"texture_name\"	:	","\""+image.name.trim()+"\",");
			if(image.uri==null)
				fw.println("			\"uri\"			:	null,");
			else
				fw.println("			\"uri\"			:	","\""+image.uri.trim()+"\",");
		}
		if(sampler==null)
			fw.println("			\"sampler\" 		:	null");
		else {
			fw.print  ("			\"sampler\" 		:	[	",sampler.min_filter);
			fw.print  ("	,",sampler.mag_filter);
			fw.print  ("	,",sampler.wrap_s);
			fw.print  ("	,",sampler.warp_t);
			fw.println("]");
		}
		fw.print  ("		}");
	}
	
	public gltf_texture(JSONObject my_object,String my_name,gltf_image_sorter my_image_sorter,gltf_sampler_sorter my_sampler_sorter)
	{
		name=my_name;
		try {
			image=my_image_sorter.data_array[my_image_sorter.search(my_object.getString("source"))];
		}catch(Exception e) {
			image=null;
		}
		try {
			sampler=my_sampler_sorter.data_array[my_sampler_sorter.search(my_object.getString("sampler"))];
		}catch(Exception e) {
			sampler=null;
		}
	}	
}

class gltf_texture_sorter extends sorter<gltf_texture,String>
{
	public int compare_data(gltf_texture s,gltf_texture t)
	{
		return compare_key(s,t.name);
	}
	public int compare_key(gltf_texture s,String t)
	{
		try{
			double x=Double.valueOf(s.name);
			double y=Double.valueOf(t);
			return ((x)<(y))?-1:(x>y)?1:0;
		}catch(Exception e){
			return s.name.compareTo(t);
		}
	}
	
	public void write_out(file_writer fw)
	{
		int texture_number=0;
		if(data_array!=null)
			texture_number=data_array.length;
		
		fw.println("	\"texture\"	:	[");
		
		for(int i=0;i<texture_number;i++) {
			data_array[i].write_out(fw);
			fw.println((i==(texture_number-1))?"":",");
		}
		fw.println("	],");
	}
	public gltf_texture_sorter(JSONObject my_jason_object)
	{
		gltf_image_sorter my_image_sorter				=new gltf_image_sorter(my_jason_object);
		gltf_sampler_sorter my_sampler_sorter			=new gltf_sampler_sorter(my_jason_object);
		gltf_array_or_object_union gltf_texture_array	=new gltf_array_or_object_union(my_jason_object,"textures");
		data_array=new gltf_texture[gltf_texture_array.get_number()];
		for(int i=0,ni=data_array.length;i<ni;i++)
			data_array[i]=new gltf_texture(gltf_texture_array.get(i),
				gltf_texture_array.get_search_id(),my_image_sorter,my_sampler_sorter);
		do_sort(new gltf_texture[data_array.length]);
	}
}

class gltf_material
{
	public String name,material_name;
	public double base_color_factor[],metallic_factor,roughness_factor;
	public double normal_factor[],occlusion_factor[],emissive_factor[];
	
	public int base_color_factor_texture_id,metallic_roughness_factor_texture_id;
	public int normal_texture_id,occlusion_texture_id,emissive_texture_id;
	
	public void write_out(file_writer fw)
	{
		fw.println("		{");
		
		fw.println("			\"base_color_factor\"						:	[	",
				base_color_factor[0]+",	"+base_color_factor[1]+",	"+base_color_factor[2]+",	"+base_color_factor[3]+"	],");
		fw.println("			\"metallic_factor\"						:	",		metallic_factor+",");
		fw.println("			\"roughness_factor\"						:	",	roughness_factor+",");
		
		fw.println("			\"normal_factor\"							:	[	",
				normal_factor[0]+",	"+normal_factor[1]+",	"+normal_factor[2]+",	1.0	],");
		fw.println("			\"occlusion_factor\"						:	[	",
				occlusion_factor[0]+",	"+occlusion_factor[1]+",	"+occlusion_factor[2]+",	1.0	],");
		fw.println("			\"emissive_factor\"						:	[	",
				emissive_factor[0]+",	"+emissive_factor[1]+",	"+emissive_factor[2]+",	1.0	],");
		
		fw.println("			\"base_color_factor_texture_id\"			:	",base_color_factor_texture_id+",");
		fw.println("			\"metallic_roughness_factor_texture_id\"	:	",metallic_roughness_factor_texture_id+",");
		fw.println("			\"normal_texture_id\"						:	",normal_texture_id+",");
		fw.println("			\"occlusion_texture_id\"					:	",occlusion_texture_id+",");
		fw.println("			\"emissive_texture_id\"					:	",emissive_texture_id);
		
		fw.print  ("		}");
	}
	
	public gltf_material(JSONObject my_material_object,String my_name,gltf_texture_sorter my_texture_sorter)
	{	
		name				=my_name;
		material_name		=my_name;
		base_color_factor	=new double[] {1,1,1,1};
		metallic_factor		=1.0;
		roughness_factor	=1.0;
		normal_factor		=new double[] {1,1,1,1};
		occlusion_factor	=new double[] {1,1,1,1};
		emissive_factor		=new double[] {1,1,1,1};
		
		base_color_factor_texture_id		=-1;
		metallic_roughness_factor_texture_id=-1;
		normal_texture_id					=-1;
		occlusion_texture_id				=-1;
		emissive_texture_id					=-1;
	
		material_name=my_material_object.getString("name");
		
		{
			JSONObject my_object=my_material_object.getJSONObject("pbrMetallicRoughness");
			try{
				JSONArray my_base_color_object=my_object.getJSONArray("baseColorFactor");
				base_color_factor[0]=my_base_color_object.getDoubleValue(0);
				base_color_factor[1]=my_base_color_object.getDoubleValue(1);
				base_color_factor[2]=my_base_color_object.getDoubleValue(2);
				base_color_factor[3]=my_base_color_object.getDoubleValue(3);
			}catch(Exception e) {
				;
			}
			try{
				metallic_factor=my_object.getDoubleValue("metallicFactor");
			}catch(Exception e) {
				;
			}
			try{
				roughness_factor=my_object.getDoubleValue("roughnessFactor");
			}catch(Exception e) {
				;
			}
		}
		
		try{
			JSONArray my_normal_object=my_material_object.getJSONArray("normalFactor");
			normal_factor[0]=my_normal_object.getDoubleValue(0);
			normal_factor[1]=my_normal_object.getDoubleValue(1);
			normal_factor[2]=my_normal_object.getDoubleValue(2);
		}catch(Exception e) {
			;
		}
		try{
			JSONArray my_occlusion_object=my_material_object.getJSONArray("occlusionFactor");
			occlusion_factor[0]=my_occlusion_object.getDoubleValue(0);
			occlusion_factor[1]=my_occlusion_object.getDoubleValue(1);
			occlusion_factor[2]=my_occlusion_object.getDoubleValue(2);
		}catch(Exception e) {
			;
		}
		try{
			JSONArray my_emissive_object=my_material_object.getJSONArray("emissiveFactor");
			emissive_factor[0]=my_emissive_object.getDoubleValue(0);
			emissive_factor[1]=my_emissive_object.getDoubleValue(1);
			emissive_factor[2]=my_emissive_object.getDoubleValue(2);
		}catch(Exception e) {
			;
		}
		
		try {
			base_color_factor_texture_id=my_texture_sorter.search(
					my_material_object.getJSONObject("baseColorTexture").getString("index"));
		}catch(Exception e) {
			normal_texture_id=-1;
		}
		try {
			metallic_roughness_factor_texture_id=my_texture_sorter.search(
					my_material_object.getJSONObject("metallicRoughnessTexture").getString("index"));
		}catch(Exception e) {
			normal_texture_id=-1;
		}
		try {
			normal_texture_id=my_texture_sorter.search(
					my_material_object.getJSONObject("normalTexture").getString("index"));
		}catch(Exception e) {
			normal_texture_id=-1;
		}
		try {
			occlusion_texture_id=my_texture_sorter.search(
					my_material_object.getJSONObject("occlusionTexture").getString("index"));
		}catch(Exception e) {
			occlusion_texture_id=-1;
		}
		try {
			emissive_texture_id=my_texture_sorter.search(
					my_material_object.getJSONObject("emissiveTexture").getString("index"));
		}catch(Exception e) {
			emissive_texture_id=-1;
		}
	}
}

class gltf_material_sorter extends sorter<gltf_material,String>
{
	public int compare_data(gltf_material s,gltf_material t)
	{
		return compare_key(s,t.name);
	}
	public int compare_key(gltf_material s,String t)
	{
		try{
			double x=Double.valueOf(s.name);
			double y=Double.valueOf(t);
			return ((x)<(y))?-1:(x>y)?1:0;
		}catch(Exception e){
			return s.name.compareTo(t);
		}
	}
	public void write_out(file_writer fw)
	{
		fw.println("	\"material\"	:	[");
		for(int i=0,ni=data_array.length;i<ni;i++) {
			data_array[i].write_out(fw);
			fw.println((i<(ni-1))?",":"");
		}
		fw.println("	],");
	}
	public gltf_material_sorter(JSONObject my_jason_object,gltf_texture_sorter my_texture_sorter)
	{
		gltf_array_or_object_union my_material_array=new gltf_array_or_object_union(my_jason_object,"materials");
		data_array=new gltf_material[my_material_array.get_number()];
		for(int i=0,ni=data_array.length;i<ni;i++)
			data_array[i]=new gltf_material(my_material_array.get(i),
					my_material_array.get_search_id(),my_texture_sorter);
		do_sort(new gltf_material[data_array.length]);
	}
}

class gltf_glb
{
	public String jason_text;
	public byte binary_data[];
	
	private FileInputStream s_stream;
	private BufferedInputStream	s_buf;
	
	private boolean load(String my_file_name,String my_charset_name)
	{
		ByteBuffer byte_buffer=ByteBuffer.allocate(64);
		byte_buffer.order(ByteOrder.LITTLE_ENDIAN);

		try{
			s_stream=new FileInputStream(new File(my_file_name));
			s_buf=new BufferedInputStream(s_stream);
			byte glb_data[]=new byte[20];
			
			if(s_buf.read(glb_data)<20){
				debug_information.println("Read GLB file head fail: ","\t"+my_charset_name+"\t"+my_file_name);
				return true;
			}

			byte_buffer.clear();
			for(int i=12;i<16;i++)
				byte_buffer.put(glb_data[i]);
			byte_buffer.rewind();
			int jason_length=byte_buffer.getInt();
			if(jason_length<=0) {
				debug_information.println("GLB JASON length wrong: ","\t"+my_charset_name+"\t"+my_file_name);
				return true;
			}
			glb_data=new byte[jason_length];
			for(int my_len,pointer=0;pointer<jason_length;pointer+=my_len)
				if((my_len=s_buf.read(glb_data,pointer,jason_length-pointer))<0) {
					while(pointer<jason_length)
						glb_data[pointer++]=0;
					break;
				}
			jason_text=new String(glb_data,my_charset_name);

			glb_data=new byte[8];
			if(s_buf.read(glb_data)<8){
				debug_information.println("Read GLB binary head fail: ","\t"+my_charset_name+"\t"+my_file_name);
				return true;
			}
			byte_buffer.clear();
			for(int i=0;i<4;i++)
				byte_buffer.put(glb_data[i]);
			byte_buffer.rewind();
			jason_length=byte_buffer.getInt();
			if(jason_length<=0) {
				debug_information.println("GLB binary length wrong: ","\t"+my_charset_name+"\t"+my_file_name);
				return true;
			}
			binary_data=new byte[jason_length];
			for(int my_len,pointer=0;pointer<jason_length;pointer+=my_len)
				if((my_len=s_buf.read(binary_data,pointer,jason_length-pointer))<0) {
					while(pointer<jason_length)
						binary_data[pointer++]=0;
					break;
				}
	        s_buf.close();
			s_stream.close();
			return false;		
		}catch(Exception e) {
			debug_information.println("Read GLB data Exception: ",e.toString());
			debug_information.println("\t"+my_charset_name+"\t", my_file_name);
			return true;
		}
	}
	
	public gltf_glb(String my_file_name,String my_charset_name)
	{
		s_buf=null;
		s_stream=null;
		
		if(load(my_file_name,my_charset_name)) {
			jason_text=null;
			binary_data=null;
		}
		if(s_buf!=null) {
			try{
				s_buf.close();
			}catch(Exception e) {
				debug_information.println("Close GLB buffer fail: ",e.toString());
				debug_information.println("\t", my_file_name);
			}
			s_buf=null;
		}
		if(s_stream!=null) {
			try{
				s_stream.close();
			}catch(Exception e) {
				debug_information.println("Close GLB stream fail: ",e.toString());
				debug_information.println("\t", my_file_name);
			}
			s_stream=null;
		}
	}
}

class gltf_buffer
{
	public String name;
	public byte data[];
	
	public gltf_buffer(String my_name,byte my_data[])
	{
		name=my_name;
		data=my_data;
	}
	public gltf_buffer(String my_name,String my_file_name,int my_length)
	{
		name=my_name;
		try {
			data=new byte[my_length];

			FileInputStream s_stream=new FileInputStream(new File(my_file_name));
			BufferedInputStream	s_buf=new BufferedInputStream(s_stream);

			for(int my_len,pointer=0;pointer<my_length;pointer+=my_len)
				if((my_len=s_buf.read(data,pointer,my_length-pointer))<0) {
					while(pointer<my_length)
						data[pointer++]=0;
					break;
				}
	        s_buf.close();
			s_stream.close();

		}catch(Exception e) {
			data=null;
			debug_information.println("Read buffer data fail: ",e.toString());
			debug_information.println("\t", my_file_name);
		}
	}
}

class gltf_buffer_sorter extends sorter<gltf_buffer,String>
{
	public int compare_data(gltf_buffer s,gltf_buffer t)
	{
		return s.name.compareTo(t.name);
	}
	public int compare_key(gltf_buffer s,String t)
	{
		return s.name.compareTo(t);
	}
	public gltf_buffer_sorter(JSONObject my_jason_object,String my_directory_name,byte glb_binary_data[])
	{
		gltf_array_or_object_union my_buffer_array=new gltf_array_or_object_union(my_jason_object,"buffers");
		data_array=new gltf_buffer[my_buffer_array.get_number()];
		for(int i=0,ni=data_array.length;i<ni;i++) {
			JSONObject p=my_buffer_array.get(i);
			String buffer_name=my_buffer_array.get_search_id();
			int length=p.getInteger("byteLength");
			String file_name;
			try {
				file_name=p.getString("uri");
			}catch(Exception e) {
				file_name=null;
			}
			if(file_name!=null)
				if((file_name=file_name.trim()).length()<=0)
					file_name=null;
			if(file_name==null)
				data_array[i]=new gltf_buffer(buffer_name,glb_binary_data);
			else
				data_array[i]=new gltf_buffer(buffer_name,my_directory_name+file_name,length);
		}
		do_sort(new gltf_buffer[data_array.length]);
	}
}

class gltf_buffer_view
{
	private byte buffer_data[];
	private int buffer_offset,buffer_view_length;
	
	public String name;
	public int byte_stride,target;
	
	public byte []get_data()
	{
		byte ret_val[]=new byte[(buffer_data==null)?0:buffer_view_length];
		for(int i=0,j=buffer_offset,ni=ret_val.length;i<ni;)
			ret_val[i++]=buffer_data[j++];
		return ret_val;
	}
	public gltf_buffer_view(JSONObject my_buffer_view_object,String my_name,gltf_buffer_sorter my_buffer_sorter)
	{
		buffer_data=null;
		buffer_offset=0;
		buffer_view_length=0;
		
		name=my_name;
		
		int buffer_id;
		
		if((buffer_id=my_buffer_sorter.search(my_buffer_view_object.getString("buffer")))<0) {
			byte_stride=0;
			target=0;
			return;
		}
		try {
			buffer_data=my_buffer_sorter.data_array[buffer_id].data;
		}catch(Exception e) {
			buffer_data=null;
		}
		try {
			buffer_offset=my_buffer_view_object.getInteger("byteOffset");
		}catch(Exception e) {
			buffer_offset=0;
		}
		try {
			buffer_view_length=my_buffer_view_object.getInteger("byteLength");
		}catch(Exception e) {
			buffer_view_length=0;
		}
		try{
			target=my_buffer_view_object.getInteger("target");
		}catch(Exception e){
			target=0;
		}
		try{
			byte_stride=my_buffer_view_object.getInteger("byteStride");
		}catch(Exception e) {
			byte_stride=0;
		}	
	}
}
class gltf_buffer_view_sorter extends sorter<gltf_buffer_view,String>
{
	public int compare_data(gltf_buffer_view s,gltf_buffer_view t)
	{
		return s.name.compareTo(t.name);
	}
	public int compare_key(gltf_buffer_view s,String t)
	{
		return s.name.compareTo(t);
	}
	public gltf_buffer_view_sorter(JSONObject my_jason_object,String my_directory_name,byte glb_binary_data[])
	{
		gltf_buffer_sorter my_buffer_sorter				=new gltf_buffer_sorter(my_jason_object,my_directory_name,glb_binary_data);
		gltf_array_or_object_union my_buffer_view_array	=new gltf_array_or_object_union(my_jason_object,"bufferViews");
		
		data_array=new gltf_buffer_view[my_buffer_view_array.get_number()];
		
		for(int i=0,ni=data_array.length;i<ni;i++)
			data_array[i]=new gltf_buffer_view(my_buffer_view_array.get(i),
				my_buffer_view_array.get_search_id(),my_buffer_sorter);
		do_sort(new gltf_buffer_view[data_array.length]);
	}
}

class gltf_accessor
{
	public String accessor_name;
	public String accessor_string[][];
	
	public gltf_accessor(JSONObject my_jason_object,String my_name,gltf_buffer_view_sorter my_buffer_view_sorter)
	{
		accessor_name=my_name;
		
		gltf_buffer_view my_buffer_view;
		{
			int buffer_id=my_buffer_view_sorter.search(my_jason_object.getString("bufferView"));
			if(buffer_id<0){
				accessor_string=new String[][]{};
				return;
			}
			my_buffer_view=my_buffer_view_sorter.data_array[buffer_id];
		}

		int component_type	=my_jason_object.getInteger("componentType");
		String type			=my_jason_object.getString("type").toUpperCase();
		
		int byte_offset;
		try{
			byte_offset=my_jason_object.getInteger("byteOffset");
		}catch(Exception e) {
			byte_offset=0;
		}
		
		
		int accessor_data_length;
		switch(component_type) {
		default:
		case 5120:	accessor_data_length=1;	break;	//BYTE
		case 5121:	accessor_data_length=1;	break;	//UNSIGNED BYTE
		case 5122:	accessor_data_length=2;	break;	//SHORT
		case 5123:	accessor_data_length=2;	break;	//UNSIGNED SHORT
		case 5125:	accessor_data_length=4;	break;	//UNSIGNED INT
		case 5126:	accessor_data_length=4;	break;	//FLOAT
		}
		switch(type) {
		default:
		case "SCALAR":	accessor_data_length*=1;	break;
		case "VEC2":	accessor_data_length*=2;	break;
		case "VEC3":	accessor_data_length*=3;	break;
		case "VEC4":	accessor_data_length*=4;	break;
		case "MAT2":	accessor_data_length*=4;	break;
		case "MAT3":	accessor_data_length*=9;	break;
		case "MAT4":	accessor_data_length*=16;	break;	
		}
		
		int byte_stride=my_buffer_view.byte_stride;
		if(byte_stride<=0)
			byte_stride=accessor_data_length;
		
		byte data[][]=new byte[my_jason_object.getInteger("count")][];
		{
			byte my_buffer_view_data[]=my_buffer_view.get_data();
			for(int i=0,ni=data.length;i<ni;i++){
				data[i]=new byte[accessor_data_length];
				for(int j=0;j<accessor_data_length;j++)
					data[i][j]=my_buffer_view_data[byte_offset+i*byte_stride+j];
			}
			my_buffer_view_data=null;
		}
		
		accessor_string=new String[data.length][];
		
		ByteBuffer byte_buffer=ByteBuffer.allocate(64);
		byte_buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		switch(type){
		default:
		case "SCALAR":	//	1
			switch(component_type){
			default:
			case 5120:// (BYTE)	1
				for(int i=0,ni=data.length;i<ni;i++)
					accessor_string[i]=new String[] {Integer.toString((int)(data[i][0]))};
				break;
			case 5121:// (UNSIGNED_BYTE)	1
				for(int i=0,ni=data.length;i<ni;i++) 
					accessor_string[i]=new String[]{Integer.toString(Byte.toUnsignedInt(data[i][0]))};
				break;
			case 5122://  (SHORT)	2
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {Short.toString(byte_buffer.getShort())};
				}
				break;
			case 5123://  (UNSIGNED_SHORT)	2
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {Short.toString(byte_buffer.getShort())};
				}
				break;
			case 5125://  (UNSIGNED_INT)	4
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {Integer.toString(byte_buffer.getInt())};
				}
				break;
			case 5126://  (FLOAT)	4
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {Float.toString(byte_buffer.getFloat())};	
				}
				break;
			}
			break;
		case "VEC2":	//	2
			switch(component_type){
			default:
			case 5120:// (BYTE)	1
				for(int i=0,ni=data.length;i<ni;i++)
					accessor_string[i]=new String[] {
						Integer.toString((int)(data[i][0])),
						Integer.toString((int)(data[i][1]))
					};
				break;
			case 5121:// (UNSIGNED_BYTE)	1
				for(int i=0,ni=data.length;i<ni;i++)
					accessor_string[i]=new String[] {
						Integer.toString(Byte.toUnsignedInt(data[i][0])),
						Integer.toString(Byte.toUnsignedInt(data[i][1]))
					};
				break;
			case 5122://  (SHORT)	2
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort())
					};
				}
				break;
			case 5123://  (UNSIGNED_SHORT)	2
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort())
					};
				}
				break;
			case 5125://  (UNSIGNED_INT)	4
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Integer.toString(byte_buffer.getInt()),
						Integer.toString(byte_buffer.getInt())
					};
				}
				break;
			case 5126://  (FLOAT)	4
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Float.toString(byte_buffer.getFloat()),
						Float.toString(byte_buffer.getFloat())
					};
				}
				break;
			}
			break;
		case "VEC3":	//	3
			switch(component_type){
			default:
			case 5120:// (BYTE)	1
				for(int i=0,ni=data.length;i<ni;i++)
					accessor_string[i]=new String[] {
						Integer.toString((int)(data[i][0])),
						Integer.toString((int)(data[i][1])),
						Integer.toString((int)(data[i][2]))
					};
				break;
			case 5121:// (UNSIGNED_BYTE)	1
				for(int i=0,ni=data.length;i<ni;i++)
					accessor_string[i]=new String[] {
						Integer.toString(Byte.toUnsignedInt(data[i][0])),
						Integer.toString(Byte.toUnsignedInt(data[i][1])),
						Integer.toString(Byte.toUnsignedInt(data[i][2]))
					};
				break;
			case 5122://  (SHORT)	2
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort())
					};
				}
				break;
			case 5123://  (UNSIGNED_SHORT)	2
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort())
					};
				}
				break;
			case 5125://  (UNSIGNED_INT)	4
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Integer.toString(byte_buffer.getInt()),
						Integer.toString(byte_buffer.getInt()),
						Integer.toString(byte_buffer.getInt())
					};
				}
				break;
			case 5126://  (FLOAT)	4
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Float.toString(byte_buffer.getFloat()),
						Float.toString(byte_buffer.getFloat()),
						Float.toString(byte_buffer.getFloat())
					};
				}
				break;
			}
			break;
		case "VEC4":	//	4
		case "MAT2":	//	4
			switch(component_type){
			default:
			case 5120:// (BYTE)	1
				for(int i=0,ni=data.length;i<ni;i++)
					accessor_string[i]=new String[] {
							Integer.toString((int)(data[i][0])),
							Integer.toString((int)(data[i][1])),
							Integer.toString((int)(data[i][2])),
							Integer.toString((int)(data[i][3]))
					};
				break;
			case 5121:// (UNSIGNED_BYTE)	1
				for(int i=0,ni=data.length;i<ni;i++)
					accessor_string[i]=new String[] {
						Integer.toString(Byte.toUnsignedInt(data[i][0])),
						Integer.toString(Byte.toUnsignedInt(data[i][1])),
						Integer.toString(Byte.toUnsignedInt(data[i][2])),
						Integer.toString(Byte.toUnsignedInt(data[i][3]))
					};
				break;
			case 5122://  (SHORT)	2
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort())
					};
				}
				break;
			case 5123://  (UNSIGNED_SHORT)	2
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort()),
						Short.toString(byte_buffer.getShort())
					};
				}
				break;
			case 5125://  (UNSIGNED_INT)	4
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Integer.toString(byte_buffer.getInt()),
						Integer.toString(byte_buffer.getInt()),
						Integer.toString(byte_buffer.getInt()),
						Integer.toString(byte_buffer.getInt())
					};
				}
				break;
			case 5126://  (FLOAT)	4
				for(int i=0,ni=data.length;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					accessor_string[i]=new String[] {
						Float.toString(byte_buffer.getFloat()),
						Float.toString(byte_buffer.getFloat()),
						Float.toString(byte_buffer.getFloat()),
						Float.toString(byte_buffer.getFloat())
					};
				}
				break;
			}
			break;
		case "MAT3":	//	9
			switch(component_type){
			default:
			case 5120:// (BYTE)	1
				for(int i=0,ni=data.length,j;i<ni;i++)
					for(j=0,accessor_string[i]=new String[9];j<9;j++)
						accessor_string[i][j]=Integer.toString((int)(data[i][j]));
				break;
			case 5121:// (UNSIGNED_BYTE)	1
				for(int i=0,ni=data.length,j;i<ni;i++)
					for(j=0,accessor_string[i]=new String[9];j<9;j++)
						accessor_string[i][j]=Integer.toString(Byte.toUnsignedInt(data[i][j]));
				break;
			case 5122://  (SHORT)	2
				for(int i=0,ni=data.length,j;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					for(j=0,accessor_string[i]=new String[9];j<9;j++)
						accessor_string[i][j]=Short.toString(byte_buffer.getShort());
				}
				break;
			case 5123://  (UNSIGNED_SHORT)	2
				for(int i=0,ni=data.length,j;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					for(j=0,accessor_string[i]=new String[9];j<9;j++)
						accessor_string[i][j]=Short.toString(byte_buffer.getShort());
				}
				break;
			case 5125://  (UNSIGNED_INT)	4
				for(int i=0,ni=data.length,j;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					for(j=0,accessor_string[i]=new String[9];j<9;j++)
						accessor_string[i][j]=Integer.toString(byte_buffer.getInt());
				}
				break;
			case 5126://  (FLOAT)	4
				for(int i=0,ni=data.length,j;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					for(j=0,accessor_string[i]=new String[9];j<9;j++)
						accessor_string[i][j]=Float.toString(byte_buffer.getFloat());
				}
				break;
			}
			break;
		case "MAT4":	//	16
			switch(component_type){
			default:
			case 5120:// (BYTE)	1
				for(int i=0,ni=data.length,j;i<ni;i++)
					for(j=0,accessor_string[i]=new String[16];j<16;j++)
						accessor_string[i][j]=Integer.toString((int)(data[i][j]));
				break;
			case 5121:// (UNSIGNED_BYTE)	1
				for(int i=0,ni=data.length,j;i<ni;i++)
					for(j=0,accessor_string[i]=new String[16];j<16;j++)
						accessor_string[i][j]=Integer.toString(Byte.toUnsignedInt(data[i][j]));
				break;
			case 5122://  (SHORT)	2
				for(int i=0,ni=data.length,j;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					for(j=0,accessor_string[i]=new String[16];j<16;j++)
						accessor_string[i][j]=Short.toString(byte_buffer.getShort());
				}
				break;
			case 5123://  (UNSIGNED_SHORT)	2
				for(int i=0,ni=data.length,j;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					for(j=0,accessor_string[i]=new String[16];j<16;j++)
						accessor_string[i][j]=Short.toString(byte_buffer.getShort());
				}
				break;
			case 5125://  (UNSIGNED_INT)	4
				for(int i=0,ni=data.length,j;i<ni;i++) {
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					for(j=0,accessor_string[i]=new String[16];j<16;j++)
						accessor_string[i][j]=Integer.toString(byte_buffer.getInt());
				}
				break;
			case 5126://  (FLOAT)	4
				for(int i=0,ni=data.length,j;i<ni;i++){
					byte_buffer.clear();
					byte_buffer.put(data[i]);
					byte_buffer.rewind();
					for(j=0,accessor_string[i]=new String[16];j<16;j++)
						accessor_string[i][j]=Float.toString(byte_buffer.getFloat());
				}
				break;
			}
			break;
		}
	}
}

class gltf_accessor_sorter extends sorter<gltf_accessor,String>
{
	public int compare_data(gltf_accessor s,gltf_accessor t)
	{
		return s.accessor_name.compareTo(t.accessor_name);
	}
	public int compare_key(gltf_accessor s,String t)
	{
		return s.accessor_name.compareTo(t);
	}
	public gltf_accessor_sorter(JSONObject my_jason_object,String my_directory_name,byte glb_binary_data[])
	{
		gltf_buffer_view_sorter my_buffer_view_sorter	=new gltf_buffer_view_sorter	(my_jason_object,my_directory_name,glb_binary_data);
		gltf_array_or_object_union my_accessor_array	=new gltf_array_or_object_union	(my_jason_object,"accessors");
		data_array=new gltf_accessor[my_accessor_array.get_number()]; 
		for(int i=0,ni=data_array.length;i<ni;i++)
			data_array[i]=new gltf_accessor(my_accessor_array.get(i),
				my_accessor_array.get_search_id(),my_buffer_view_sorter);
		do_sort(new gltf_accessor[data_array.length]);
	}
}

class gltf_primitive
{
	public String name,indices;
	public int mode,material;
	public gltf_name_and_value attribute[];
	public int vertex_number;
	
	private int []caculate_index(int vertex_index[])
	{
		switch(mode) {
		default:
		case 0x0000://POINTS
		case 0x0001://LINES
		case 0x0004://TRIANGLES 
			return vertex_index;
		case 0x0002:// LINE_LOOP
			return vertex_index;
		case 0x0003:// LINE_STRIP 
			return vertex_index;
		case 0x0005://TRIANGLE_STRIP
			return vertex_index;
		case 0x0006:// TRIANGLE_FAN
			return vertex_index;
		}
	}
	public void write_out(int face_id,file_writer f_mesh,file_writer material_fw,gltf_accessor_sorter my_accessor_sorter)
	{
		int attribute_number=0;
		for(int i=0,ni=attribute.length;i<ni;i++){
			material_fw.println("				{");
			for(int j=0,nj=attribute[i].field_name.length;j<nj;j++){
				material_fw.print  ("					\"",	attribute[i].field_name[j]);
				material_fw.print  ("\"	:			",		attribute_number++);
				if(j==(nj-1))
					material_fw.println();
				else
					material_fw.println(",");
			}
			material_fw.println("				}",(i==(ni-1))?"":",");
		}

		String str[]=new String[]{
				"",
				"/*			face "+face_id+" name	*/  gltf_face_"+name.replace(' ','_').replace('\t','_'),
				"/*			face_type   */  unknown  /*  parameter_number  */  0  /*  parameter */ ",
				"/*			face_attribute_number   */ "+(attribute_number-2),
		};
		for(int i=0,ni=str.length;i<ni;i++)
			f_mesh.println(str[i]);
		
		boolean attribute_flag[]=new boolean[attribute_number];
		for(int attribute_id=0,i=0,ni=attribute.length;i<ni;i++)
			for(int j=0,nj=attribute[i].field_name.length;j<nj;j++){
				String attribute_name=attribute[i].field_name[j];
				String my_accessor_id=attribute[i].get_value(attribute_name);
				
				f_mesh.println();
				if(my_accessor_id==null){
					attribute_flag[attribute_id++]=true;
					
					f_mesh.println("          /* vertex number	*/  1");
					f_mesh.println("          /* vertex  0		*/	0	0	0	1");
				}else{
					attribute_flag[attribute_id++]=false;
					
					String my_attribute_name=attribute_name.replace('*','_').replace(' ','_').replace('\t','_');
					
					String vertex_string[][]=my_accessor_sorter.data_array[my_accessor_sorter.search(my_accessor_id)].accessor_string;
					f_mesh.print  ("          /* ",my_attribute_name);
					f_mesh.println("	vertex number	*/	",vertex_string.length);
					for(int k=0,nk=vertex_string.length;k<nk;k++) {
						f_mesh.print  ("          /*	",my_attribute_name);
						f_mesh.print  (" vertex ",k);
						f_mesh.print  ("	*/");
						int number=0;
						while((number<vertex_string[k].length)&&(number<4))
							f_mesh.print  ("	",vertex_string[k][number++]);
						for(;number<3;number++)
							f_mesh.print  ("	0");
						for(;number<4;number++)
							f_mesh.print  ("	1");
						f_mesh.println();
					}
				}
				f_mesh.println();
		}

		int vertex_index[];
		
		if(indices==null) {
			vertex_index=new int[vertex_number];
			for(int i=0;i<vertex_number;i++)
				vertex_index[i]=i;
		}else{
			String indices_string[][]=my_accessor_sorter.data_array[my_accessor_sorter.search(indices)].accessor_string;
			int vertex_index_number=0;
			for(int i=0,ni=indices_string.length;i<ni;i++)
				vertex_index_number+=indices_string[i].length;
			
			vertex_index=new int[vertex_index_number];
			for(int n=0,i=0,ni=indices_string.length;i<ni;i++)
				for(int j=0,nj=indices_string[i].length;j<nj;j++)
					vertex_index[n++]=Integer.decode(indices_string[i][j]);
		}
		
		vertex_index=caculate_index(vertex_index);
		
		f_mesh.println("          /* face_primitive_number  */  1");
		f_mesh.println("          /* face_primitive  0  material      */    "+0+"	"+0+"	"+0+"	"+face_id);
		
		for(int i=0;i<attribute_number;i++) {
			f_mesh.print("          /* face_primitive_index  ",i+"	*/");
			for(int j=0,nj=vertex_index.length;j<nj;j++) {
				if((j%3)==0) {
					f_mesh.println();
					f_mesh.print  ("			/* face_primitive  ",j/3);
					f_mesh.print  ("        */    ");
				}
				f_mesh.print("	",(attribute_flag[i])?0:vertex_index[j]);
			}
			f_mesh.println();
			f_mesh.println("			/* face_primitive  end        */    	-1");
		}
		
		f_mesh.println();
		f_mesh.println("          /* face_loop_number   */ 0");
		f_mesh.println();
		f_mesh.println();
		
		return;
	}
	
	public gltf_primitive(JSONObject my_primitive_object,String my_name,
			gltf_accessor_sorter my_accessor_sorter,gltf_material_sorter my_material_sorter)
	{
		name=my_name;
		try{
			mode=my_primitive_object.getInteger("mode");
		}catch(Exception e) {
			mode=4;
		}
		try {
			material=my_material_sorter.search(my_primitive_object.getString("material"));
		}catch(Exception e) {
			material=-1;
		}
		try {
			indices=my_primitive_object.getString("indices");
		}catch(Exception e) {
			indices=null;
		}
		gltf_array_or_object_union target_array=new gltf_array_or_object_union(my_primitive_object,"targets");
		attribute=new gltf_name_and_value[target_array.get_number()+1];
		attribute[0]=new gltf_name_and_value(my_primitive_object.getJSONObject("attributes"));
		attribute[0].insert_default_attribute();
		attribute[0].attribute_sort();
		for(int i=1,ni=attribute.length;i<ni;i++) {
			attribute[i]=new gltf_name_and_value(target_array.get(i-1));
			attribute[i].attribute_sort();
		}
		
		vertex_number=-1;
		for(int i=0,ni=attribute.length;i<ni;i++)
			for(int j=0,nj=attribute[i].field_value.length;j<nj;j++) {
				String accessor_id=attribute[i].field_value[j];
				if(accessor_id!=null) {
					int my_vertex_number=my_accessor_sorter.data_array[my_accessor_sorter.search(accessor_id)].accessor_string.length;
					if((vertex_number<0)||(vertex_number>my_vertex_number))
						vertex_number=my_vertex_number;
				}
			}
		if(vertex_number<0)	
			vertex_number=0;

		return;
	}
}

class gltf_mesh extends sorter<gltf_primitive,String>
{
	public String name,id;
	public double weight_array[];
	
	public int compare_data(gltf_primitive s,gltf_primitive t)
	{
		return s.name.compareTo(t.name);
	}
	public int compare_key(gltf_primitive s,String t)
	{
		return s.name.compareTo(t);
	}
	public void write_out(file_writer mesh_fw,file_writer material_fw,gltf_accessor_sorter my_accessor_sorter)
	{
		String str[]={
				"/*	version				*/	2020.01.01",
				"/*	origin material		*/	0  0  0  1",
				"/*	body_number			*/	1",
				"/*		body  0  name 	*/	gltf_body_0	/*		face_number		*/	"+data_array.length,
			};
		for(int i=0,ni=str.length;i<ni;i++)
			mesh_fw.println(str[i]);	
		
		material_fw.println("	\"attribute\"	:	[");
		for(int i=0,ni=data_array.length;i<ni;i++) {
			material_fw.println("		{");
			material_fw.print  ("			\"face\"		:	",i						);
			material_fw.println(",");
			material_fw.print  ("			\"mode\"		:	",data_array[i].mode	);
			material_fw.println(",");
			material_fw.print  ("			\"material\"	:	",data_array[i].material);
			material_fw.println(",");
			material_fw.println("			\"attribute\"	:	[");
			
			data_array[i].write_out(i,mesh_fw,material_fw,my_accessor_sorter);
			
			material_fw.println("			]");
			material_fw.println("		}",(i==(ni-1))?"":",");
		}
		material_fw.println("	],");
		material_fw.println();
	}
	public gltf_mesh(JSONObject my_mesh_object,String my_name,
			gltf_accessor_sorter my_accessor_sorter,gltf_material_sorter my_material_sorter)
	{
		name=my_name;
		id=my_name;

		gltf_array_or_object_union my_weight_array=new gltf_array_or_object_union(my_mesh_object,"weights");
		weight_array=new double[my_weight_array.get_number()+1];
		weight_array[0]=1.0;
		for(int i=1,ni=weight_array.length;i<ni;i++)
			weight_array[i]=my_weight_array.get_value(i-1,0.0);
		
		gltf_array_or_object_union my_primitive_array=new gltf_array_or_object_union(my_mesh_object,"primitives");
		data_array=new gltf_primitive[my_primitive_array.get_number()];
		
		for(int i=0,ni=data_array.length;i<ni;i++)
			data_array[i]=new gltf_primitive(my_primitive_array.get(i),
				my_primitive_array.get_search_id(),my_accessor_sorter,my_material_sorter);
		
		do_sort(new gltf_primitive[data_array.length]);
	}
}

class gltf_mesh_sorter extends sorter<gltf_mesh,String>
{
	public int compare_data(gltf_mesh s,gltf_mesh t)
	{
		return s.name.compareTo(t.name);
	}
	public int compare_key(gltf_mesh s,String t)
	{
		return s.name.compareTo(t);
	}
	public gltf_mesh_sorter(JSONObject my_jason_object,
			gltf_accessor_sorter my_accessor_sorter,gltf_material_sorter my_material_sorter)
	{
		gltf_array_or_object_union my_mesh_array=new gltf_array_or_object_union(my_jason_object,"meshes");
		data_array=new gltf_mesh[my_mesh_array.get_number()];
		for(int i=0,ni=data_array.length;i<ni;i++)
			data_array[i]=new gltf_mesh(my_mesh_array.get(i),
					my_mesh_array.get_search_id(),my_accessor_sorter,my_material_sorter);
		do_sort(new gltf_mesh[data_array.length]);
	}
}

class gltf_mesh_same_tester extends sorter<gltf_mesh,gltf_mesh>
{
	public int compare_key(gltf_mesh s,gltf_mesh t)
	{
		return compare_data(s,t);
	}
	public int compare_data(gltf_mesh s,gltf_mesh t)
	{
		String s_str,t_str;
		int s_result=s.data_array.length,t_result=t.data_array.length;
		if(s_result!=t_result)
			return s_result-t_result;
		
		for(int i=0,ni=s_result;i<ni;i++){
			if((s_result=s.data_array[i].mode)!=(t_result=t.data_array[i].mode))
				return s_result-t_result;
			if((s_result=s.data_array[i].material)!=(t_result=t.data_array[i].material))
				return s_result-t_result;
			if((s_result=s.data_array[i].vertex_number)!=(t_result=t.data_array[i].vertex_number))
				return s_result-t_result;
			if((s_result=s.data_array[i].attribute.length)!=(t_result=t.data_array[i].attribute.length))
				return s_result-t_result;
			if((s_str=s.data_array[i].indices)==null)
				s_str="";
			if((t_str=t.data_array[i].indices)==null)
				t_str="";
			if((s_result=s_str.compareTo(t_str))!=0)
				return s_result;
			
			for(int j=0,nj=s.data_array[i].attribute.length;j<nj;j++) {
				s_result=s.data_array[i].attribute[j].field_name.length;
				t_result=t.data_array[i].attribute[j].field_name.length;
				if(s_result!=t_result)
					return s_result-t_result;
				for(int k=0,nk=s_result;k<nk;k++) {
					s_str=s.data_array[i].attribute[j].field_name[k];
					t_str=t.data_array[i].attribute[j].field_name[k];
					if((s_str=s.data_array[i].indices)==null)
						s_str="";
					if((t_str=t.data_array[i].indices)==null)
						t_str="";
					if((s_result=s_str.compareTo(t_str))!=0)
						return s_result;
					s_str=s.data_array[i].attribute[j].field_value[k];
					t_str=t.data_array[i].attribute[j].field_value[k];
					if((s_str=s.data_array[i].indices)==null)
						s_str="";
					if((t_str=t.data_array[i].indices)==null)
						t_str="";
					if((s_result=s_str.compareTo(t_str))!=0)
						return s_result;
				}
			}
		}
		return 0;
	}
	public gltf_mesh_same_tester(gltf_mesh my_mesh_array[])
	{
		data_array=new gltf_mesh[my_mesh_array.length];
		for(int i=0,n=data_array.length;i<n;i++)
			data_array[i]=my_mesh_array[i];
		
		gltf_mesh buffer[]=new gltf_mesh[data_array.length];
		do_sort(buffer);
		
		int number=0;
		for(int i=0,j,n=data_array.length;i<n;i=j) {
			for(j=i;j<n;j++) {
				if(compare_data(data_array[i],data_array[j])!=0)
					break;
				data_array[j].id=Integer.toString(number);
			}
			buffer[number++]=data_array[i];
		}
		data_array=new gltf_mesh[number];
		for(int i=0;i<number;i++)
			data_array[i]=buffer[i];
	}
}

class gltf_node
{
	static int Undefined_component_id=0;
	
	private void caculate_location(JSONObject my_node_object)
	{
		JSONArray data_array;
		try{
			if((data_array=my_node_object.getJSONArray("matrix"))!=null){
				double location_data[]=new double[16];
				for(int i=0;i<16;i++)
					location_data[i]=data_array.getDoubleValue(i);
				node_location=new location(location_data);
				return;
			}
		}catch(Exception e) {
			;
		}
		
		try{
			if((data_array=my_node_object.getJSONArray("translation"))==null)
				node_location=new location();
			else
				node_location=location.move_rotate(data_array.getDoubleValue(0),
					data_array.getDoubleValue(1),data_array.getDoubleValue(2),0, 0, 0);
		}catch(Exception e){
			node_location=new location();
			debug_information.println("translation error:",e.toString());
		}
		try{
			if((data_array=my_node_object.getJSONArray("rotation"))!=null)
				node_location=node_location.multiply(
						location.quaternion(
								data_array.getDoubleValue(0),data_array.getDoubleValue(1),
								data_array.getDoubleValue(2),data_array.getDoubleValue(3)));
		}catch(Exception e) {
			debug_information.println("rotation error:",e.toString());
		}
		try{
			if((data_array=my_node_object.getJSONArray("scale"))!=null)
				node_location=node_location.multiply(
						location.scale(data_array.getDoubleValue(0),
							data_array.getDoubleValue(1),data_array.getDoubleValue(2)));
		}catch(Exception e) {
			debug_information.println("scale error:",e.toString());
		}
	}
	private void caculate_children(JSONObject my_node_object)
	{
		JSONArray child_array;
		try{
			child_array=my_node_object.getJSONArray("children");
		}catch(Exception e) {
			child_array=null;
		}
		if(child_array==null)
			children=new String[0];
		else{
			children=new String[child_array.size()];
			for(int i=0,ni=children.length;i<ni;i++)
				children[i]=child_array.getString(i);
		}
	}
	private void caculate_meshes(JSONObject my_node_object)
	{
		JSONArray mesh_array;
		try{
			mesh_array=my_node_object.getJSONArray("meshes");
		}catch(Exception e) {
			mesh_array=null;
		}
		if(mesh_array!=null) {
			meshes=new String[mesh_array.size()];
			for(int i=0,ni=meshes.length;i<ni;i++)
				meshes[i]=mesh_array.getString(i);
			return;
		}
		try {
			meshes=new String[]{my_node_object.getString("mesh")};
			if(meshes[0]!=null)
				if(meshes[0].length()>0)
					return;
		}catch(Exception e) {
			;
		}
		meshes=new String[]{};
	}
	
	public String component_name,search_name;
	public location node_location;
	public String children[],meshes[];
	
	public gltf_node(JSONObject my_node_object,String my_search_name)
	{
		try{
			if((component_name=my_node_object.getString("name"))==null)
				component_name=my_search_name.trim();
			else if((component_name=component_name.trim()).length()<=0)
				component_name=my_search_name.trim();
		}catch(Exception e){
			component_name="Undefined_component_name_"+Undefined_component_id++;
		}
		
		component_name=component_name.replace(" ","").replace("\t","").replace("\n","").replace("\r","");
		
		search_name=my_search_name;
		caculate_location(my_node_object);
		caculate_children(my_node_object);
		caculate_meshes(my_node_object);
	}
}

class gltf_node_sorter extends sorter<gltf_node,String>
{
	private double identity_matrix_data[];
	
	public int compare_data(gltf_node s,gltf_node t)
	{
		return s.search_name.compareTo(t.search_name);
	}
	public int compare_key(gltf_node s,String t)
	{
		return s.search_name.compareTo(t);
	}
	public gltf_node_sorter(JSONObject my_jason_object)
	{
		gltf_array_or_object_union my_node_array=new gltf_array_or_object_union(my_jason_object,"nodes");
		data_array=new gltf_node[my_node_array.get_number()];
		for(int i=0,ni=data_array.length;i<ni;i++)
			data_array[i]=new gltf_node(my_node_array.get(i),my_node_array.get_search_id());
		do_sort(new gltf_node[data_array.length]);
		
		identity_matrix_data=(new location()).get_location_data();
	}
	private void write_location_data(double my_node_location_data[],file_writer fw,String space_string)
	{
		fw.print  (space_string,"/*	location		*/");
		for(int i=0,ni=my_node_location_data.length;i<ni;i++)
			fw.print  ("	",my_node_location_data[i]);
		fw.println();	
	}
	private void write_mesh(int node_id,double my_node_location_data[],file_writer fw,
			String space_string,String component_name,gltf_mesh_sorter my_mesh_sorter)
	{
		for(int i=0,search_id,ni=data_array[node_id].meshes.length;i<ni;i++){
			fw.println(space_string+"/*	name			*/	",(component_name+"_"+i));
			fw.print  (space_string,"/*	type			*/	");
			if((search_id=my_mesh_sorter.search(data_array[node_id].meshes[i]))<0)
				fw.println("gltf_unexist_part");
			else
				fw.println("gltf_part_",my_mesh_sorter.data_array[search_id].id);
			write_location_data(my_node_location_data,fw,space_string);
			fw.println(space_string+"/*	child_number	*/	0");
		}
	}
	public void write_out(file_writer fw,String my_node_name,String space_string,gltf_mesh_sorter my_mesh_sorter)
	{
		int node_id=search(my_node_name);
		if(node_id<0) {
			fw.println(space_string,"/*	name			*/	gltf_unfound_component_"+my_node_name);
			fw.println(space_string,"/*	type			*/	gltf_unfound_part");
			fw.println(space_string,"/*	location		*/	1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0");
			fw.println(space_string,"/*	child_number	*/	0");
		}else{
			double my_node_location_data[]=data_array[node_id].node_location.get_location_data();
			fw.println(space_string+"/*	name			*/	",data_array[node_id].component_name);
			fw.println(space_string,"/*	type			*/	gltf_unfound_part");
			write_location_data(my_node_location_data,fw,space_string);
			fw.println(space_string+"/*	child_number	*/	",
					(data_array[node_id].children.length)+(data_array[node_id].meshes.length));
			space_string+="	";
			for(int i=0,ni=data_array[node_id].children.length;i<ni;i++)
				write_out(fw,data_array[node_id].children[i],space_string,my_mesh_sorter);
			
			write_mesh(node_id,identity_matrix_data,fw,space_string,data_array[node_id].component_name,my_mesh_sorter);
		}
	}
}

class gltf_scene
{
	public String scene_name,name,node_array[];
	
	public void write_out(String file_name,String file_charset,
			gltf_node_sorter my_node_sorter,gltf_mesh_sorter my_mesh_sorter)
	{
		String str[]=new String[] {
			"/*	name	*/	gltf_component_root",
			"/*	type	*/	gltf_part_root",
			"/*	location	*/	1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0",
			"/*	child_number	*/	"+node_array.length
		};

		file_writer fw=new file_writer(file_name,file_charset);
		for(int i=0,ni=str.length;i<ni;i++)
			fw.println(str[i]);
		for(int i=0,ni=node_array.length;i<ni;i++)
			my_node_sorter.write_out(fw, node_array[i],"	",my_mesh_sorter);
		fw.close();
	}
	
	public gltf_scene(JSONObject my_scene_object,String my_name)
	{
		name=my_name;
		if((scene_name=my_scene_object.getString("name"))==null)
			scene_name="no_scene_name";

		JSONArray my_array;
		try{
			my_array=my_scene_object.getJSONArray("nodes");
		}catch(Exception e) {
			my_array=null;
		}
		if(my_array==null)
			node_array=new String[0];
		else{
			node_array=new String[my_array.size()];
			for(int i=0,ni=node_array.length;i<ni;i++)
				node_array[i]=my_array.getString(i);
		}
	}
}

class gltf_scene_sorter extends sorter<gltf_scene,String>
{
	public int default_scene_id;
	
	public int compare_data(gltf_scene s,gltf_scene t)
	{
		return s.name.compareTo(t.name);
	}
	public int compare_key(gltf_scene s,String t)
	{
		return s.name.compareTo(t);
	}
	public gltf_scene_sorter(JSONObject my_jason_object)
	{
		gltf_array_or_object_union my_scene_array=new gltf_array_or_object_union(my_jason_object,"scenes");
		data_array=new gltf_scene[my_scene_array.get_number()];
		for(int i=0,ni=data_array.length;i<ni;i++)
			data_array[i]=new gltf_scene(my_scene_array.get(i),my_scene_array.get_search_id());
		do_sort(new gltf_scene[data_array.length]);
		if((default_scene_id=search(my_jason_object.getString("scene")))>=data_array.length)
			default_scene_id=-1;
	}
}

public class gltf_converter 
{
	private String get_file_string(String mesh_file_name,String source_charset)
	{
		File f=new File(mesh_file_name);
		int file_length;
		if((file_length=(int)(f.length()))>0)
			try{
				FileInputStream s_stream=new FileInputStream(f);
				BufferedInputStream	s_buf=new BufferedInputStream(s_stream);
				byte binary_data[]=new byte[file_length];
				for(int my_len,pointer=0;pointer<file_length;pointer+=my_len)
					if((my_len=s_buf.read(binary_data,pointer,file_length-pointer))<0) {
						while(pointer<file_length)
							binary_data[pointer++]=0;
						break;
					}
				s_buf.close();
				s_stream.close();
				return new String(binary_data,source_charset);
			}catch(Exception e) {
				debug_information.println("Read GLTF data Exception: ",e.toString());
				debug_information.println("\t"+source_charset+"\t", mesh_file_name);
			}
		return "";
	}
	
	public gltf_converter(String mesh_file_name,String target_directory_name,
			String source_charset,String target_charset)
	{
		mesh_file_name=file_reader.separator(mesh_file_name);
		if(!(new File(mesh_file_name).exists())) {
			debug_information.println("GLTF converter find gltf file NOT exist:	",mesh_file_name);
			return;
		}
		target_directory_name=file_reader.separator(target_directory_name);
		if(target_directory_name.charAt(target_directory_name.length()-1)!=File.separatorChar)
			target_directory_name+=File.separator;
		source_charset=(source_charset==null)?(Charset.defaultCharset().name()):source_charset;
		target_charset=(target_charset==null)?(Charset.defaultCharset().name()):target_charset;
		
		JSONObject my_jason_object;
		byte glb_binary_data[]=null;
		int index_id;
		
		do{
			if((index_id=mesh_file_name.lastIndexOf("."))>=0)
				if(mesh_file_name.substring(index_id).toLowerCase().compareTo(".gltf")==0){
					my_jason_object=JSON.parseObject(get_file_string(mesh_file_name,source_charset));
					break;
				}
			gltf_glb glb=new gltf_glb(mesh_file_name,source_charset);
			if(glb.jason_text==null)
				return;
			my_jason_object=JSON.parseObject(glb.jason_text);
			glb_binary_data=glb.binary_data;
			break;
		}while(false);
		
		gltf_accessor_sorter my_accessor_sorter	=new gltf_accessor_sorter(my_jason_object,
				((index_id=mesh_file_name.lastIndexOf(File.separator))<0)
				?("."+File.separator):(mesh_file_name.substring(0,index_id+1)),glb_binary_data);
		gltf_texture_sorter	my_texture_sorter	=new gltf_texture_sorter (my_jason_object);
		gltf_material_sorter my_material_sorter	=new gltf_material_sorter(my_jason_object,my_texture_sorter);
		gltf_mesh_sorter		my_mesh_sorter	=new gltf_mesh_sorter	 (my_jason_object,my_accessor_sorter,my_material_sorter);
		gltf_mesh_same_tester	my_same_tester	=new gltf_mesh_same_tester(my_mesh_sorter.data_array);
		
		file_writer fw=new file_writer(target_directory_name+"gltf.list",target_charset);
		for(int i=0,ni=my_same_tester.data_array.length;i<ni;i++) {
			String my_id=my_same_tester.data_array[i].id;
			fw.println("	gltf_part_",my_id);
			fw.println("	gltf_part_",my_id);
			fw.println("	gltf_part_",my_id+".mesh");
			fw.println("	gltf_part_",my_id+".material");
			fw.println("	gltf_part_",my_id+".description");
			fw.println("	gltf_part_",my_id+".mp3");
			fw.println();
			
			file_writer mesh_fw=new file_writer(target_directory_name+"gltf_part_"+my_id+".mesh",target_charset);
			file_writer material_fw=new file_writer(target_directory_name+"gltf_part_"+my_id+".material",target_charset);
			my_same_tester.data_array[i].write_out(mesh_fw,material_fw,my_accessor_sorter);
			mesh_fw.close();
			material_fw.close();
		}
		fw.close();
		
		fw=new file_writer(target_directory_name+"gltf.material",target_charset);
		
		my_material_sorter.write_out(fw);
		my_texture_sorter.write_out(fw);
		
		fw.close();
		
		gltf_node_sorter	my_node_sorter		=new gltf_node_sorter	(my_jason_object);
		gltf_scene_sorter	my_scene_sorter		=new gltf_scene_sorter	(my_jason_object);
		
		if(my_scene_sorter.default_scene_id>=0)
			my_scene_sorter.data_array[my_scene_sorter.default_scene_id].write_out(
					target_directory_name+"gltf.assemble",target_charset,my_node_sorter,my_mesh_sorter);
	}
}