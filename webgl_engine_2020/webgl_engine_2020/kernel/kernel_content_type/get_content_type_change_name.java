package kernel_content_type;

import kernel_common_class.change_name;
import kernel_common_class.class_file_reader;
import kernel_common_class.common_reader;

public class get_content_type_change_name
{
	public static change_name get_change_name()
	{
		common_reader reader=class_file_reader.get_reader(
				"content_type.txt",get_content_type_change_name.class);
		if(reader==null)
			return null;
		change_name ret_val=new change_name(new common_reader[] {reader},null);
		reader.close();
		return ret_val;
	}
}
