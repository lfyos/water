package kernel_transformation;

import kernel_common_class.const_value;

public class tetrahedron {
	public point p_a,p_b,p_c,p_d;
	public tetrahedron next;

	public tetrahedron(point my_p_a,point my_p_b,point my_p_c,point my_p_d,tetrahedron my_next)
	{
		p_a=new point(my_p_a);
		p_b=new point(my_p_b);
		p_c=new point(my_p_c);
		p_d=new point(my_p_d);
		
		next=my_next;
	}
	public tetrahedron(location absolute_location,box local_box)
	{
		point a=absolute_location.multiply(new point(local_box.p[0].x,local_box.p[1].y,local_box.p[0].z));
		point b=absolute_location.multiply(new point(local_box.p[0].x,local_box.p[1].y,local_box.p[1].z));
		point c=absolute_location.multiply(new point(local_box.p[1].x,local_box.p[1].y,local_box.p[1].z));
		point d=absolute_location.multiply(new point(local_box.p[1].x,local_box.p[1].y,local_box.p[0].z));
		
		point e=absolute_location.multiply(new point(local_box.p[0].x,local_box.p[0].y,local_box.p[0].z));
		point f=absolute_location.multiply(new point(local_box.p[0].x,local_box.p[0].y,local_box.p[1].z));
		point g=absolute_location.multiply(new point(local_box.p[1].x,local_box.p[0].y,local_box.p[1].z));
		point h=absolute_location.multiply(new point(local_box.p[1].x,local_box.p[0].y,local_box.p[0].z));
		
		next=new tetrahedron(a,d,c,h,null);
		next=new tetrahedron(a,b,c,f,next);
		next=new tetrahedron(a,e,f,h,next);
		next=new tetrahedron(c,g,f,h,next);
		p_a=a;
		p_b=c;
		p_c=f;
		p_d=h;
	}
	private int caculate_outer_pointer_number(plane pl)
	{
		point bak_p_a=p_a;
		point bak_p_b=p_b;
		point bak_p_c=p_c;
		point bak_p_d=p_d;
		
		int switch_value=0;
				
		if(pl.test(p_a)>0)
			switch_value+=1;
		if(pl.test(p_b)>0)
			switch_value+=2;
		if(pl.test(p_c)>0)
			switch_value+=4;
		if(pl.test(p_d)>0)
			switch_value+=8;
		
		switch(switch_value){
		case 0:
			return  0;
		case 1:
			return  1;
		case 2:
			p_a=bak_p_b;
			p_b=bak_p_a;
			return  1;
		case 3:
			return  2;
		case 4:
			p_a=bak_p_c;
			p_c=bak_p_a;
			return  1;
		case 5:
			p_b=bak_p_c;
			p_c=bak_p_b;
			return  2;
		case 6:
			p_a=bak_p_c;
			p_c=bak_p_a;
			return  2;
		case 7:
			return  3;
		case 8:
			p_a=bak_p_d;
			p_d=bak_p_a;
			return  1;
		case 9:
			p_b=bak_p_d;
			p_d=bak_p_b;
			return  2;
		case 10:
			p_a=bak_p_d;
			p_d=bak_p_a;
			return  2;
		case 11:
			p_c=bak_p_d;
			p_d=bak_p_c;
			return  3;
		case 12:
			p_a=bak_p_c;
			p_c=bak_p_a;
			p_b=bak_p_d;
			p_d=bak_p_b;
			return  2;
		case 13:
			p_b=bak_p_d;
			p_d=bak_p_b;
			return  3;
		case 14:
			p_a=bak_p_d;
			p_d=bak_p_a;
			return  3;
		case 15:
			return  4;
		default:
			return  4;
		}	
	}
	private boolean test_point(plane pl,point a,point b,point c,point d)
	{
		if(Math.abs(pl.test(a))>const_value.min_value)
			return true;
		if(Math.abs(pl.test(b))>const_value.min_value)
			return true;
		if(Math.abs(pl.test(c))>const_value.min_value)
			return true;
		if(Math.abs(pl.test(d))>const_value.min_value)
			return true;
		return false;
	}
	private tetrahedron clip_tetrahedron_1(plane pl,tetrahedron head)
	{
		point p_ab,p_ac,p_ad;
		location loca=pl.insect_location(p_a);
		
		if((p_ab=loca.multiply(p_b)).distance2()<const_value.min_value2)
			return test_point(pl,p_a,p_b,p_c,p_d)?new tetrahedron(p_a,p_b,p_c,p_d,head):head;
		if((p_ac=loca.multiply(p_c)).distance2()<const_value.min_value2)
			return test_point(pl,p_a,p_b,p_c,p_d)?new tetrahedron(p_a,p_b,p_c,p_d,head):head;
		if((p_ad=loca.multiply(p_d)).distance2()<const_value.min_value2)
			return test_point(pl,p_a,p_b,p_c,p_d)?new tetrahedron(p_a,p_b,p_c,p_d,head):head;
			
		if(test_point(pl,p_b,p_ab,p_ac,p_ad))				
			head=new tetrahedron(p_b,p_ab,p_ac,p_ad,head);
		if(test_point(pl,p_b,p_c,p_ac,p_ad))
			head=new tetrahedron(p_b,p_c,p_ac,p_ad,head);
		if(test_point(pl,p_b,p_d,p_c,p_ad))
			head=new tetrahedron(p_b,p_d,p_c,p_ad,head);
		
		return head;
	}
	private tetrahedron clip_tetrahedron_2(plane pl,tetrahedron head)
	{
		location loca=pl.insect_location(p_a);
		point p_ac=loca.multiply(p_c),p_ad=loca.multiply(p_d);
		if((p_ac.distance2()<const_value.min_value2)||(p_ad.distance2()<const_value.min_value2)){
			point bak=p_a;	
			p_a=p_b;	
			p_b=bak;
			
			return clip_tetrahedron_1(pl,head);
		}
		
		loca=pl.insect_location(p_b);
		point p_bc=loca.multiply(p_c),p_bd=loca.multiply(p_d);
		if((p_bc.distance2()<const_value.min_value2)||(p_bd.distance2()<const_value.min_value2))
			return clip_tetrahedron_1(pl,head);
		
		if(test_point(pl,p_ac,p_bc,p_bd,p_c))
			head=new tetrahedron(p_ac,p_bc,p_bd,p_c,head);
		if(test_point(pl,p_ac,p_ad,p_bd,p_d))
			head=new tetrahedron(p_ac,p_ad,p_bd,p_d,head);
		if(test_point(pl,p_ac,p_bd,p_c,p_d))
			head=new tetrahedron(p_ac,p_bd,p_c,p_d,head);
		
		return head;
	}
	private tetrahedron clip_tetrahedron_3(plane pl,tetrahedron head)
	{
		point p_ad,p_bd,p_cd;
		location loca=pl.insect_location(p_d);
		if((p_ad=loca.multiply(p_a)).distance2()>const_value.min_value2)
			if((p_bd=loca.multiply(p_b)).distance2()>const_value.min_value2)
				if((p_cd=loca.multiply(p_c)).distance2()>const_value.min_value2)
					return new tetrahedron(p_ad,p_bd,p_cd,p_d,head);
		return head;
	}
	public tetrahedron clip_tetrahedron(plane pl,tetrahedron head)
	{
		switch(caculate_outer_pointer_number(pl)){
		case 0:
			return test_point(pl,p_a,p_b,p_c,p_d)?new tetrahedron(p_a,p_b,p_c,p_d,head):head;
		case 1:
			return clip_tetrahedron_1(pl,head);
		case 2:
			return clip_tetrahedron_2(pl,head);
		case 3:
			return clip_tetrahedron_3(pl,head);
		case 4:
		default:
			return head;
		}
	}
}