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
            StreamWriter mesh_writer, material default_material,material_container material_cont)
        {
            try
            {
                if (b.MaterialPropertyValues != null)
                    default_material = material_cont.get((double[])(b.MaterialPropertyValues), b.GetTexture(""));
            }catch(Exception )
            {
            }
            string str = "/*    body  "+ my_body_id + "  name   */  "+ cut_space(b.Name)+ "   /*   face_number   */  ";
            object[] face_array;
            try
            {
                face_array = (object[])(b.GetFaces());
            }catch(Exception )
            {
                face_array = null; 
            }
            if (face_array == null)
            {
                mesh_writer.WriteLine(str + "0");
                return;
            }
            str = str + face_array.Length;

            mesh_writer.WriteLine(str);

            for (int i = 0, ni = face_array.Length; i < ni; i++)
            {
                mesh_writer.WriteLine("        /*   No.   " +i+ "  face name  */  " + cut_space(b.Name) + "_" + my_body_id + "_face");
                new face((Face2)(face_array[i]), 
                    ChordTolerance, LengthTolerance, mesh_writer, default_material, material_cont);
            }
        }
    }
}
