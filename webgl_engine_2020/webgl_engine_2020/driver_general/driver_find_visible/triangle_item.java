package driver_find_visible;

public class triangle_item 
{
	public int body_id,face_id,triangle_id,component_id;
	public triangle_item next;
	public triangle_item(int my_body_id,int my_face_id,int my_triangle_id,int my_component_id,triangle_item my_next)
	{
		body_id=my_body_id;
		face_id=my_face_id;
		triangle_id=my_triangle_id;
		component_id=my_component_id;
		next=my_next;
	}
}
