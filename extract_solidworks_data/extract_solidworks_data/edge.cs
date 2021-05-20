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
    class edge
    {
        private Curve curve(Edge my_edge, StreamWriter mesh_writer)
        {
            string curve_name;
            double[] curve_data;

            Curve my_curve;

            try
            {
                my_curve = (Curve)(my_edge.GetCurve());
            }catch(Exception )
            {
                my_curve = null;
            }
            if (my_curve == null)
            {
                curve_name = "unknown";
                curve_data = new double[0];
            }
            else if (my_curve.IsLine())
            {
                curve_name = "line";
                curve_data = (double[])(my_curve.LineParams);
            }
            else if (my_curve.IsCircle())
            {
                curve_name = "circle";
                curve_data = (double[])(my_curve.CircleParams);
            }
            else if (my_curve.IsEllipse())
            {
                curve_name = "ellipse";
                curve_data = (double[])(my_curve.GetEllipseParams());
            }
            else
            {
                curve_name = "unknown";
                curve_data = new double[0];
            }
            mesh_writer.WriteLine("/*                   curve type          */  "+ curve_name);
            mesh_writer.Write    ("/*                   parameter number    */  "+ curve_data.Length+ "  /*  parameter  */");

            for (int i = 0, ni = curve_data.Length; i < ni; i++)
                mesh_writer.Write("   " + curve_data[i]);
            mesh_writer.WriteLine();

            return my_curve;
        }
        private  double[][] start_end(Edge my_edge, StreamWriter mesh_writer, material default_material)
        {
            double[] start_data, end_data;
            do
            {
                try
                {
                    Vertex p = (Vertex)(my_edge.GetStartVertex());
                    if (p != null)
                        if ((start_data = (double[])(p.GetPoint())) != null)
                        {
                            mesh_writer.Write("                     start_effective    "
                                + start_data[0] + "   " + start_data[1] + "   " + start_data[2] + "   1 ");

                            mesh_writer.WriteLine("/*   start_point_material  */"
                                       + "    " + default_material.red + "    " + default_material.green
                                       + "    " + default_material.blue + "    " + default_material.meterial_id);
                            break;
                        }
                }catch(Exception )
                {
                    ;
                }
                mesh_writer.Write("                     start_not_effective    ");
                start_data = new double[] { 0, 0, 0 };
            } while (false);

            do
            {
                try {
                    Vertex p = (Vertex)(my_edge.IGetEndVertex());
                    if (p != null)
                        if ((end_data = (double[])(p.GetPoint())) != null)
                        {
                            mesh_writer.Write("                     end_effective    " 
                                + end_data[0] + "   " + end_data[1] + "   " + end_data[2] + "   1 ");

                            mesh_writer.WriteLine("/*   end_point_material  */"
                                   + "    " + default_material.red + "    " + default_material.green
                                   + "    " + default_material.blue + "    " + default_material.meterial_id);
                            break;
                        }
                }catch(Exception )
                {

                }
                mesh_writer.Write("                     end_not_effective    ");
                end_data = new double[] { 0, 0, 0 };
            } while (false);

            return new double[][] { start_data, end_data};
        }

        private void tessellation_point(Curve my_curve, double[]start_point,double[] end_point,
            double ChordTolerance, double LengthTolerance,StreamWriter mesh_writer, material default_material)
        {
            mesh_writer.Write("/*                   vertex number  */   ");

            double[] data;
            try
            {
                data = (double[])(my_curve.GetTessPts(ChordTolerance, LengthTolerance, start_point, end_point));
            }catch(Exception )
            {
                data = null;
            }
            if (data == null)
            {
                mesh_writer.WriteLine(0);
                return;
            }

            int vertex_number = data.Length / 3;
            mesh_writer.WriteLine(vertex_number);

            for(int i = 0; i < vertex_number; i++)
            {
                double x = data[3 * i + 0], y = data[3 * i + 1], z = data[3 * i + 2];
                mesh_writer.Write("/*                   NO."+i+"    vertex location  */   " + x + " " + y + " " + z+ "  1   ");
                mesh_writer.WriteLine("/*   tessellation_material  */"
                + "    " + default_material.red + "    " + default_material.green
                + "    " + default_material.blue + "    " + default_material.meterial_id);
            }
        }
        public edge(Edge my_edge, double ChordTolerance, double LengthTolerance, StreamWriter mesh_writer, material default_material)
        {
            Curve my_curve=curve(my_edge, mesh_writer);

            mesh_writer.WriteLine("/*                   parameter_point_material  */"
                + "    " + default_material.red  + "    " + default_material.green 
                + "    " + default_material.blue + "    " + default_material.meterial_id);

            double[][]p=start_end(my_edge, mesh_writer, default_material);

            tessellation_point(my_curve,p[0],p[1], ChordTolerance, LengthTolerance, mesh_writer, default_material);
        }
    }
}
