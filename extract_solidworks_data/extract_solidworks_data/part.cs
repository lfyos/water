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
        private Boolean box_exist_flag;
        private double min_x, min_y, min_z, max_x, max_y, max_z;
        private double default_color_red, default_color_green, default_color_blue, default_color_material;
        public part(IModelDoc2 doc,string charset,
            string mesh_file_name,string material_file_name,
            double ChordTolerance,double LengthTolerance)
        {
            box_exist_flag = false;

            min_x = 0;
            min_y = 0;
            min_z = 0;
            max_x = 0;
            max_y = 0;
            max_z = 0;

            default_color_red = 0;
            default_color_green = 0;
            default_color_blue = 0;
            default_color_material = 0;

            IPartDoc part_doc = (IPartDoc)doc;

            material default_material;
            double[] material_value = (double[])(part_doc.MaterialPropertyValues);
            if (material_value == null)
                default_material = new material();
            else if (doc.Extension == null)
                default_material = new material(material_value, null);
            else
                default_material = new material(material_value, doc.Extension.GetTexture(""));

            material_container material_cont = new material_container(default_material);
            
            FileStream mesh_stream      = new FileStream(mesh_file_name, FileMode.Create, FileAccess.Write);
            StreamWriter mesh_writer    = new StreamWriter(mesh_stream, Encoding.GetEncoding(charset));

            FileStream face_stream      = new FileStream(mesh_file_name+".face", FileMode.Create, FileAccess.Write);
            StreamWriter face_writer    = new StreamWriter(face_stream, Encoding.GetEncoding(charset));

            FileStream edge_stream      = new FileStream(mesh_file_name + ".edge", FileMode.Create, FileAccess.Write);
            StreamWriter edge_writer    = new StreamWriter(edge_stream, Encoding.GetEncoding(charset));

            FileStream material_stream  = new FileStream(material_file_name, FileMode.Create, FileAccess.Write);
            StreamWriter material_writer= new StreamWriter(material_stream, Encoding.GetEncoding(charset));

            object[] body_array;
            try
            {
                body_array = (object[])(part_doc.GetBodies2((int)(swBodyType_e.swSolidBody), true));
            }catch(Exception)
            {
                body_array = null;
            }

            if (body_array == null)
                mesh_writer.WriteLine("/*  body_number */	0");
            else
            {
                mesh_writer.WriteLine("/*  body_number */	" + body_array.Length);

                for (int i = 0, ni = body_array.Length; i < ni; i++)
                {
                    body b= new body((Body2)(body_array[i]), i, ChordTolerance, LengthTolerance,
                            mesh_writer, face_writer, edge_writer, default_material, material_cont);

                    if (!(b.box_exist_flag))
                        continue;
                    if (box_exist_flag)
                    {
                        double my_volume = (max_x - min_x) * (max_y - min_y) * (max_z - min_z);
                        double face_volume = (b.max_x - b.min_x) * (b.max_y - b.min_y) * (b.max_z - b.min_z);
                        if (Math.Abs(my_volume) >= Math.Abs(face_volume))
                            continue;
                    }

                    box_exist_flag = b.box_exist_flag;

                    min_x = b.min_x;
                    min_y = b.min_y;
                    min_z = b.min_z;

                    max_x = b.max_x;
                    max_y = b.max_y;
                    max_z = b.max_z;

                    default_color_red = b.default_color_red;
                    default_color_green = b.default_color_green;
                    default_color_blue = b.default_color_blue;
                    default_color_material = b.default_color_material;
                }
            }

            material_cont.save(material_writer);

            mesh_writer.Close();
            mesh_stream.Close();

            face_writer.Close();
            face_stream.Close();

            edge_writer.Close();
            edge_stream.Close();

            material_writer.Close();
            material_stream.Close();

            FileStream new_mesh_stream = new FileStream(mesh_file_name+".new", FileMode.Create, FileAccess.Write);
            StreamWriter new_mesh_writer = new StreamWriter(new_mesh_stream, Encoding.GetEncoding(charset));

            new_mesh_writer.WriteLine("/*	version					*/	2021.07.15");
            new_mesh_writer.WriteLine("/*	origin material			*/	0	0	0	1");
            new_mesh_writer.WriteLine("/*	default material		*/	"
                + default_color_red + "  " + default_color_green + "  " 
                + default_color_blue + "  " + default_color_material);
            new_mesh_writer.WriteLine("/*	origin extra_data		            */	1");
            new_mesh_writer.WriteLine("/*	default vertex_location_extra_data	*/	1");
            new_mesh_writer.WriteLine("/*	default vertex_normal_extra_data	*/	1");
            new_mesh_writer.WriteLine("/*	max_attribute_number	*/  1");
            new_mesh_writer.WriteLine("     /*      attribute_0	    */  0   0   0   1");
            new_mesh_writer.WriteLine();

            mesh_stream = new FileStream(mesh_file_name, FileMode.Open, FileAccess.Read);
            StreamReader mesh_reader = new StreamReader(mesh_stream, Encoding.GetEncoding(charset));

            for (String str; !(mesh_reader.EndOfStream);)
                if ((str = mesh_reader.ReadLine()) != null)
                    new_mesh_writer.WriteLine(str);

            new_mesh_writer.Close();
            new_mesh_stream.Close();
            mesh_reader.Close();
            mesh_stream.Close();

            File.Delete(mesh_file_name);
            File.Move(mesh_file_name + ".new", mesh_file_name);
        }
    }
}
