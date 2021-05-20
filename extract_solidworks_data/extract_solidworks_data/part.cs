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
    class part
    {
        public part(IModelDoc2 doc,string charset,
            string mesh_file_name,string material_file_name,
            double ChordTolerance,double LengthTolerance)
        {
            IPartDoc part_doc = (IPartDoc)doc;

            double[] material_value = (double[])(part_doc.MaterialPropertyValues);

            material default_material=(material_value == null)?new material():new material(material_value,(doc.Extension == null)?null:(doc.Extension.GetTexture("")));

            material_container material_cont = new material_container(default_material);
            
            FileStream mesh_stream = new FileStream(mesh_file_name, FileMode.Create, FileAccess.Write);
            StreamWriter mesh_writer = new StreamWriter(mesh_stream, Encoding.GetEncoding(charset));

            FileStream material_stream = new FileStream(material_file_name, FileMode.Create, FileAccess.Write);
            StreamWriter material_writer = new StreamWriter(material_stream, Encoding.GetEncoding(charset));

            mesh_writer.WriteLine("/*	version			*/	2020.02.25");
            mesh_writer.WriteLine("/*	origin material	*/	0	0	0	1");

            object[] body_array;
            try
            {
                body_array = (object[])(part_doc.GetBodies2((int)(swBodyType_e.swSolidBody), true));
            }catch(Exception)
            {
                body_array = null;
            }

            if (body_array == null)
                mesh_writer.WriteLine("/*	body_number		*/	0");
            else
            {
                mesh_writer.WriteLine("/*	body_number		*/	" + body_array.Length);

                for (int i = 0, ni = body_array.Length; i < ni; i++)
                    new body((Body2)(body_array[i]), i,
                        ChordTolerance, LengthTolerance, mesh_writer, default_material, material_cont);
            }

            material_cont.save(material_writer);

            mesh_writer.Close();
            mesh_stream.Close();
            material_writer.Close();
            material_stream.Close();
        }
    }
}
