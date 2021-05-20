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
        private string create_parameter_surface(Face2 f)
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
            str = "/*   face type   */  "+str+"  /*    parameter number   */    "+p.Length+"    /*  prameter */    ";
            for (int i = 0; i < p.Length; i++)
                str += "    " + p[i];

            return str;
        }
        private int get_tess_triangles(Face2 f, StreamWriter mesh_writer)
        {
            System.Single[] p;

            try
            {
                p = (System.Single[])(f.GetTessTriangles(true));
                if (p == null)
                    p = new System.Single[] { 0, 0, 0 };
                else if (p.Length < 3)
                    p = new System.Single[] { 0, 0, 0 };
            }catch(Exception)
            {
                p = new System.Single[] { 0, 0, 0 };
            }

            int vertex_number = p.Length / 3;

            mesh_writer.WriteLine();
            mesh_writer.WriteLine("/*            face vertex number */  "+vertex_number);
            for(int i = 0; i < vertex_number; i++)
            {
                mesh_writer.Write("/*               No.   "+i);
                mesh_writer.Write("  vertex  */");
                for(int j=3*i,nj=3*i+3;j<nj;j++)
                    mesh_writer.Write("  "+p[j]);
                mesh_writer.WriteLine("  1");
            }
            mesh_writer.WriteLine();
            return vertex_number;
        }

        private int get_tess_normal(Face2 f, StreamWriter mesh_writer)
        {
            System.Single[] p;

            try
            {
                p = (System.Single[])(f.GetTessNorms());
                if (p == null)
                    p = new System.Single[] { 0, 0, 0 };
                else if (p.Length < 3)
                    p = new System.Single[] { 0, 0, 0 };
            }
            catch (Exception)
            {
                p = new System.Single[] { 0, 0, 0 };
            }

            int normal_number = p.Length / 3;

            mesh_writer.WriteLine();
            mesh_writer.WriteLine("/*            face normal number */  " + normal_number);
            for (int i = 0; i < normal_number; i++)
            {
                mesh_writer.Write("/*               No.   " + i);
                mesh_writer.Write("  normal  */");
                for (int j = 3 * i, nj = 3 * i + 3; j < nj; j++)
                    mesh_writer.Write("  " + p[j]);
                mesh_writer.WriteLine("  1");
            }
            mesh_writer.WriteLine();

            return normal_number;
        }

        private int get_tess_texture(Face2 f, StreamWriter mesh_writer)
        {
            System.Double[] p;
            
            try{
                p= (System.Double[])(f.GetTessTextures());
                if (p == null)
                    p = new System.Double[] { 0, 0 };
                else if (p.Length < 2)
                    p = new System.Double[] { 0, 0 };
            }
            catch (Exception)
            {
                p = new System.Double[] { 0, 0 };
            }

            int texture_number = p.Length / 2;

            mesh_writer.WriteLine();
            mesh_writer.WriteLine("/*            face texture number */  " + texture_number);
            for (int i = 0; i < texture_number; i++)
            {
                mesh_writer.Write("/*               No.   " + i);
                mesh_writer.Write("  texture  */");
                for (int j = 2 * i, nj = 2 * i + 2; j < nj; j++)
                    mesh_writer.Write("  " + p[j]);
                mesh_writer.WriteLine("  0  1");
            }
            mesh_writer.WriteLine();

            return texture_number;
        }
        private material create_triangle(
            Face2 f, int vertex_number,int normal_number,int texture_number,
            StreamWriter mesh_writer, material default_material, material_container material_cont)
        {
            try
            {
                if (f.MaterialPropertyValues != null)
                    default_material = material_cont.get((double[])(f.MaterialPropertyValues), f.GetTexture(""));
            }
            catch (Exception)
            {
               
            }

            int triangle_number = vertex_number / 3;

            mesh_writer.WriteLine(); 
            mesh_writer.WriteLine("/*            face triangle number  */  "+ triangle_number);
            for(int i=0,ni= triangle_number; i < ni; i++)
            {
                string str = "/*                NO.    ";
                mesh_writer.WriteLine(str + "  material		*/	" + default_material.red + "  " + default_material.green + "  " + default_material.blue + "  "+ default_material.meterial_id);
                mesh_writer.WriteLine(str + "  vertex		*/	" + ((3 * i + 0) % vertex_number)  + "    " + ((3 * i + 1) % vertex_number)  + "    " + ((3 * i + 2) % vertex_number)  + "    -1");
                mesh_writer.WriteLine(str + "  normal		*/	" + ((3 * i + 0) % normal_number)  + "    " + ((3 * i + 1) % normal_number)  + "    " + ((3 * i + 2) % normal_number)  + "    -1");
                mesh_writer.WriteLine(str + "  texture		*/	" + ((3 * i + 0) % texture_number) + "    " + ((3 * i + 1) % texture_number) + "    " + ((3 * i + 2) % texture_number) + "    -1");
                mesh_writer.WriteLine();
            }

            mesh_writer.WriteLine();
            return default_material;
        }
        public face(Face2 f, double ChordTolerance, double LengthTolerance, 
            StreamWriter mesh_writer, material default_material, material_container material_cont)
        {
            mesh_writer.WriteLine("        "+create_parameter_surface(f));
            mesh_writer.WriteLine("        /*   attribute number   */  1");

            default_material = create_triangle(f,get_tess_triangles(f,mesh_writer),
                                               get_tess_normal(f,mesh_writer),
                                               get_tess_texture(f,mesh_writer),
                                               mesh_writer,
                                               default_material,
                                               material_cont);

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

            mesh_writer.WriteLine("/*            face loop number   */   "+ (p_loop.Length));

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

                mesh_writer.WriteLine("/*               Loop NO." + i + "     edge number    */   " + (p_edge.Length));

                for (int j = 0, nj = p_edge.Length; j < nj; j++) {
                    mesh_writer.WriteLine();
                    mesh_writer.WriteLine("/*                   Loop NO." + i + "  Edge NO." + j + "   */");
                    new edge((Edge)(p_edge[j]), ChordTolerance, LengthTolerance, mesh_writer, default_material);
                }
            }

            mesh_writer.WriteLine();
        }
    }
}
