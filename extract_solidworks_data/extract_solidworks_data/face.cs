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
    class face
    {
        public int triangle_number;
        public Boolean box_exist_flag;
        public double min_x, min_y, min_z, max_x, max_y, max_z;
        public double default_color_red, default_color_green, default_color_blue, default_color_material;
        private void write_face_parameter(Face2 f, StreamWriter mesh_writer)
        {
            string str;
            double[] p;

            try
            {
                Surface s = (Surface)(f.GetSurface());

                if (s.IsPlane())
                {
                    str = "plane";
                    p = (double[])(s.PlaneParams);
                }
                else if (s.IsCylinder())
                {
                    str = "cylinder";
                    p = (double[])(s.CylinderParams);
                }
                else if (s.IsCone())
                {
                    str = "cone";
                    p = (double[])(s.ConeParams);
                }
                else if (s.IsSphere())
                {
                    str = "sphere";
                    p = (double[])(s.SphereParams);
                }
                else if (s.IsTorus())
                {
                    str = "torus";
                    p = (double[])(s.TorusParams);
                }
                else
                {
                    str = "unknown";
                    p = new double[0];
                }
            }catch(Exception)
            {
                str = "unknown";
                p = new double[0];
            }

            mesh_writer.WriteLine();
            mesh_writer.WriteLine("             /*  face type                   */  " + str);
            mesh_writer.Write    ("             /*  parameter number            */  " + p.Length + "    /*  prameter */ ");

            for (int i = 0; i < p.Length; i++)
                mesh_writer.Write("    " + p[i]);

            mesh_writer.WriteLine();
        }

        private void write_face_tess(
            int body_id,int face_id,Face2 f, StreamWriter face_writer,
            material default_material, material_container material_cont)
        {
            System.Single[] vertex, normal;
            System.Double[] texture;

            try
            {
                vertex = (System.Single[])(f.GetTessTriangles(true));
                if (vertex == null)
                    vertex = new System.Single[] { 0, 0, 0 };
                else if (vertex.Length < 3)
                    vertex = new System.Single[] { 0, 0, 0 };
            }
            catch (Exception)
            {
                vertex = new System.Single[] { 0, 0, 0 };
            }

            try
            {
                normal = (System.Single[])(f.GetTessNorms());
                if (normal == null)
                    normal = new System.Single[] { 0, 0, 0 };
                else if (normal.Length < 3)
                    normal = new System.Single[] { 0, 0, 0 };
            }
            catch (Exception)
            {
                normal = new System.Single[] { 0, 0, 0 };
            }

            try
            {
                texture = (System.Double[])(f.GetTessTextures());
                if (texture == null)
                    texture = new System.Double[] { 0, 0 };
                else if (texture.Length < 2)
                    texture = new System.Double[] { 0, 0 };
            }
            catch (Exception)
            {
                texture = new System.Double[] { 0, 0 };
            }

            int vertex_number = vertex.Length;
            int normal_number = normal.Length;
            int texture_number = texture.Length;
            triangle_number = ((vertex_number < normal_number) ? vertex_number : normal_number) / 9;

            if (triangle_number <= 0)
            {
                min_x = 0;
                min_y = 0;
                min_z = 0;
                max_x = 0;
                max_y = 0;
                max_z = 0;

                box_exist_flag = false;
            }
            else
            {
                min_x = vertex[0];
                min_y = vertex[1];
                min_z = vertex[2];
                max_x = vertex[0];
                max_y = vertex[1];
                max_z = vertex[2];

                box_exist_flag = true;
            }

            try
            {
                if (f.MaterialPropertyValues != null)
                    default_material = material_cont.get((double[])(f.MaterialPropertyValues), f.GetTexture(""));
            }
            catch (Exception)
            {

            }

            default_color_red = default_material.red;
            default_color_green = default_material.green;
            default_color_blue = default_material.blue;
            default_color_material = default_material.meterial_id;

            face_writer.WriteLine();
            face_writer.WriteLine("/*   body_id:"+body_id+",face_id:"+face_id+ ",triangle_number:"+ triangle_number+"   */");

            for (int i = 0; i < triangle_number; i++)
            {
                face_writer.WriteLine();
                face_writer.WriteLine("/*	triangle:"+i+",material	*/  "
                    + default_color_red + "   "
                    + default_color_green + "   "
                    + default_color_blue + "   "
                    + default_color_material);

                face_writer.WriteLine("/*	triangle:" + i + ",vertex number    */	3");

                for (int j = 0; j < 3; j++)
                {
                    double px = vertex[9 * i + 3 * j + 0], py = vertex[9 * i + 3 * j + 1], pz = vertex[9 * i + 3 * j + 2];
                    double nx = normal[9 * i + 3 * j + 0], ny = normal[9 * i + 3 * j + 1], nz = normal[9 * i + 3 * j + 2];
                    double tx = texture[6 * i + 2 * j + 0], ty = texture[6 * i + 2 * j + 1], tz = 0;

                    min_x = (min_x < px) ? min_x : px;
                    min_y = (min_y < py) ? min_y : py;
                    min_z = (min_z < pz) ? min_z : pz;
                    max_x = (max_x > px) ? max_x : px;
                    max_y = (max_y > py) ? max_y : py;
                    max_z = (max_z > pz) ? max_z : pz;

                    face_writer.WriteLine("/*		" + j + ".location		*/  " + px + "   " + py + "    " + pz + " 1");
                    face_writer.WriteLine("/*		" + j + ".normal		*/  " + nx + "   " + ny + "    " + nz + " 1");
                    face_writer.WriteLine("/*		" + j + ".texture		*/  " + tx + "   " + ty + "    " + tz + " 1");
                }
            }
        }
       
        public face(int body_id, int face_id, 
            Face2 f, double ChordTolerance, double LengthTolerance, 
            StreamWriter mesh_writer, StreamWriter face_writer, StreamWriter edge_writer, 
            material default_material, material_container material_cont)
        {
            write_face_parameter(f, mesh_writer);
            write_face_tess(body_id, face_id, f,face_writer,default_material,material_cont);

            mesh_writer.WriteLine("             /*  total_face_primitive_number */  " + triangle_number);
            mesh_writer.WriteLine("             /*  face_attribute_number       */  1");
            mesh_writer.Write    ("             /*  face_face_box               */  ");

            if (box_exist_flag)
                mesh_writer.WriteLine(min_x + " " + min_y + " " + min_z + " " + max_x + " " + max_y + " " + max_z);
            else
                mesh_writer.WriteLine("nobox");

            System.Object[] p_loop;
            
            try{
                p_loop=(System.Object[])(f.GetLoops());
                if (p_loop == null)
                    p_loop = new System.Object[0];
            }
            catch (Exception)
            {
                p_loop = new System.Object[0];
            }

            mesh_writer.WriteLine();
            mesh_writer.WriteLine("         /*  face loop number        */  " + (p_loop.Length));

            for (int i = 0, ni = p_loop.Length; i < ni; i++)
            {
                Loop2 current_loop = (Loop2)(p_loop[i]);
                System.Object[] p_edge;
                
                try{
                    p_edge= (System.Object[])(current_loop.GetEdges());
                    if (p_edge == null)
                        p_edge = new System.Object[0];
                }
                catch (Exception)
                {
                    p_edge = new System.Object[0];
                }

                mesh_writer.WriteLine("         /*  Loop NO." + i + "   edge number */  " + (p_edge.Length));

                for (int j = 0, nj = p_edge.Length; j < nj; j++) {
                    mesh_writer.WriteLine();
                    mesh_writer.WriteLine("             /*  Loop NO." + i + "  Edge NO." + j + "   */");
                    new edge(body_id, face_id, i, j,
                        (Edge)(p_edge[j]), ChordTolerance, LengthTolerance,mesh_writer, edge_writer,default_material);
                }
            }
            mesh_writer.WriteLine();
        }
    }
}
