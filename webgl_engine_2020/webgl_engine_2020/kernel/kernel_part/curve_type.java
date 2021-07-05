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
		case "hyperbola":
			return 4;
		case "parabola":
			return 5;
		case "segment":
			return 6;
		case "pickup_point_set":
			return 7;
		case "render_point_set":
			return 8;
		}
	}
}
