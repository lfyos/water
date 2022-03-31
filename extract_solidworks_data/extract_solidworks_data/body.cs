using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


using System.IO;

using SolidWorks.Interop.sldworks;
using SolidWorks.Interop.swconst;

namespace extract_solidworks_data
{
    class body
    {
        public Boolean box_exist_flag;
        public double min_x, min_y, min_z, max_x, max_y, max_z;
        public double default_color_red, default_color_green, default_color_blue, default_color_material;

        private string cut_space(string str)
        {
            str = str.Replace(" ", "");
            str = str.Replace("\t", "");
            str = str.Replace(" ", "");
            str = str.Replace("\r", "");
            str = str.Replace("\n", "");
            return str;
        }
        public body(Body2 b, int my_body_id, double ChordTolerance, double LengthTolerance, 
            StreamWriter mesh_writer, StreamWriter face_writer, StreamWriter edge_writer,
            material default_material,material_container material_cont)
        {
            box_exist_flag=false;

            min_x = 0;
            min_y = 0;
            min_z = 0;
            max_x = 0;
            max_y = 0;
            max_z = 0;

            default_color_red = 0;
            default_color_green = 0;
            default_color_blue = 0;
            default_color_material=0;

            try
            {
                if (b.MaterialPropertyValues != null)
                    default_material = material_cont.get((double[])(b.MaterialPropertyValues), b.GetTexture(""));
            }catch(Exception )
            {
            }
            mesh_writer.Write("     /*  body    " + my_body_id + "  name    */  "+cut_space(b.Name)+"   /*  face_number */  ");
            object[] face_array;
            try
            {
                face_array = (object[])(b.GetFaces());
            }catch(Exception )
            {
                face_array = new object[0]; 
            }
         
            mesh_writer.WriteLine(face_array.Length);

            for (int i = 0, ni = face_array.Length; i < ni; i++)
            {
                mesh_writer.WriteLine("         /*  face " + i + "  name   */  "+cut_space(b.Name)+"_face_" + i);

                face f=new face(my_body_id,i,(Face2)(face_array[i]), ChordTolerance, LengthTolerance,
                        mesh_writer, face_writer, edge_writer,default_material, material_cont);
                if (!(f.box_exist_flag))
                    continue;
                if (box_exist_flag)
                {
                    double my_volume = (max_x - min_x) * (max_y - min_y) * (max_z - min_z);
                    double face_volume = (f.max_x - f.min_x) * (f.max_y - f.min_y) * (f.max_z - f.min_z);
                    if (Math.Abs(my_volume) >= Math.Abs(face_volume))
                            continue;
                }
                   
                box_exist_flag = f.box_exist_flag;

                min_x = f.min_x;
                min_y = f.min_y;
                min_z = f.min_z;

                max_x = f.max_x;
                max_y = f.max_y;
                max_z = f.max_z;

                default_color_red = f.default_color_red;
                default_color_green = f.default_color_green;
                default_color_blue = f.default_color_blue;
                default_color_material = f.default_color_material;
            }
        }
    }
}
