package format_convert;

import java.net.URL;
import java.net.HttpURLConnection;
import kernel_common_class.debug_information;

public class web_converter
{
	public static void main(String args[])
	{
		do_convert(
				"http://localhost:8080/webgl_engine_2020/interface.jsp",			
//				"https://dpms-test.sigmacax.com:8443/lfy/interface.jsp",
				"NoName",			//�û���
				"NoPassword",		//�û�����
				"cad",				//��������
				new String[][] {	//��ʼ������
						new String[] {"type_sub_directory",	""},				//��ʾ����type
			 			new String[] {"scene_sub_directory","a.step"},			//��ʾ����scene
			 			new String[] {"coordinate",			"location.xyz.txt"},//����ϵѡ��
			 			new String[] {"change_part",		""},				//part����
			 			new String[] {"change_component",	""},				//component����
			 			new String[] {"part_type",			""},				//part����
			 			new String[] {"max_loading_number",	"10"}				//ͬʱ��������
				}
		);
	}
	public static void do_convert(
			String my_url,String my_user_name,String my_pass_word,
			String my_scene_name,String my_initialization_parameter[][])
	{
		my_url	+="?channel=creation&command=termination&language=chinese";
		my_url	+="&user_name="		+((my_user_name ==null)?"NoName"	:(my_user_name.trim()));
		my_url	+="&pass_word="		+((my_pass_word ==null)?"NoPassword":(my_pass_word.trim()));
		my_url	+="&scene_name="	+((my_scene_name==null)?""			:(my_scene_name.trim()));
		
		if(my_initialization_parameter!=null)
			for(int i=0,ni=my_initialization_parameter.length;i<ni;i++)
					if(my_initialization_parameter[i]!=null)
						if(my_initialization_parameter[i].length>=2){
							String parameter_item[]	=my_initialization_parameter[i];
							String parameter_name	=parameter_item[0].toString().trim();
							String parameter_value	=parameter_item[1].toString().trim();
							my_url+="&"+parameter_name+"="+parameter_value;
						};
		
		debug_information.println("protected_cadex_converter.do_translate start");
		debug_information.println(my_url);
		
		HttpURLConnection connection=null;
		try{
			int response_code;
			connection=(HttpURLConnection)((new URL(my_url)).openConnection());
			connection.setRequestMethod("GET");
			connection.connect();
			if((response_code=connection.getResponseCode())!=HttpURLConnection.HTTP_OK){
				debug_information.println(
					"protected_cadex_converter.do_translate:connection.getResponseCode()!=HttpURLConnection.HTTP_OK	",response_code);
				debug_information.println(my_url);
			}
		}catch(Exception e){
			debug_information.println("protected_cadex_converter.do_translate:Setup connection fail:",e.toString());
			debug_information.println(my_url);
		}
		if(connection!=null){
			connection.disconnect();
			connection=null;
		}
		debug_information.println("protected_cadex_converter.do_translate end");
		debug_information.println(my_url);
	}
}
