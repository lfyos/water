package kernel_part;

public class curve_type
{
	public static int curve_type_id(String curve_type_name)
	{
		switch(curve_type_name) {
		default:
			return 0;
		case "line":
			return 1;
		case "circle":
			return 2;
		case "ellipse":
			return 3;
		case "point_set":
			return 4;
		case "segment":
			return 5;
		}
	}
}
