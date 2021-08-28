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
        private int total_point_primitive_number;
        private Boolean box_exist_flag;
        private double min_x, min_y, min_z, max_x, max_y, max_z;
        private Curve curve_parameter(Edge my_edge, StreamWriter mesh_writer)
        {
            string curve_name;
            double[] curve_data;

            Curve my_curve;

            total_point_primitive_number = 0;

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

                total_point_primitive_number = 1;
            }
            else if (my_curve.IsCircle())
            {
                curve_name = "circle";
                curve_data = (double[])(my_curve.CircleParams);
                total_point_primitive_number = 1;
            }
            else if (my_curve.IsEllipse())
            {
                curve_name = "ellipse";
                curve_data = (double[])(my_curve.GetEllipseParams());
                total_point_primitive_number = 2;
            }
            else
            {
                curve_name = "unknown";
                curve_data = new double[0];
            }
            mesh_writer.WriteLine("             /*  curve type             */  "+ curve_name);
            mesh_writer.Write    ("             /*  parameter number       */  "+ curve_data.Length+ "  /*  parameter  */");

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
                            mesh_writer.Write("             start_effective    "
                                + start_data[0] + "   " + start_data[1] + "   " + start_data[2] + "   1 ");

                            mesh_writer.WriteLine("/*   start_point_material  */"
                                       + "    " + default_material.red + "    " + default_material.green
                                       + "    " + default_material.blue + "    " + default_material.meterial_id);

                            total_point_primitive_number++;
                            break;
                        }
                }catch(Exception )
                {
                    ;
                }
                mesh_writer.WriteLine("             start_not_effective    ");
                start_data = new double[] { 0, 0, 0 };
            } while (false);

            do
            {
                try {
                    Vertex p = (Vertex)(my_edge.IGetEndVertex());
                    if (p != null)
                        if ((end_data = (double[])(p.GetPoint())) != null)
                        {
                            mesh_writer.Write("             end_effective    " 
                                + end_data[0] + "   " + end_data[1] + "   " + end_data[2] + "   1 ");

                            mesh_writer.WriteLine("/*   end_point_material  */"
                                   + "    " + default_material.red + "    " + default_material.green
                                   + "    " + default_material.blue + "    " + default_material.meterial_id);
                            total_point_primitive_number++;
                            break;
                        }
                }catch(Exception )
                {

                }
                mesh_writer.WriteLine("             end_not_effective    ");
                end_data = new double[] { 0, 0, 0 };
            } while (false);

            return new double[][] { start_data, end_data};
        }

        private int tessellation_point(int body_id,int face_id,int loop_id,int edge_id,
            Curve my_curve, double[]start_point,double[] end_point,
            double ChordTolerance, double LengthTolerance,StreamWriter edge_writer, material default_material)
        {
            min_x = 0;
            min_y = 0;
            min_z = 0;
            max_x = 0;
            max_y = 0;
            max_z = 0;

            box_exist_flag = false;


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
                edge_writer.WriteLine(0);
                return 0;
            }

            int edge_primitive_number = data.Length / 3;

            edge_writer.WriteLine();
            edge_writer.WriteLine("/*   body_id:" + body_id
                + ",face_id:" + face_id
                + ",loop_id:" + loop_id
                + ",edge_id:" + edge_id
                + ",edge_primitive_number:"+ edge_primitive_number
                + "     */");

            for (int i = 0; i < edge_primitive_number; i++)
            {
                double x = data[3 * i + 0], y = data[3 * i + 1], z = data[3 * i + 2];
                edge_writer.Write("     /*  NO."+i+"    vertex location  */   " + x + " " + y + " " + z+ "  1   ");
                edge_writer.WriteLine("/*   tessellation_material  */"
                        + "    " + default_material.red + "    " + default_material.green
                        + "    " + default_material.blue + "    " + default_material.meterial_id);

                if (box_exist_flag)
                {
                    min_x = (min_x < x) ? min_x : x;
                    min_y = (min_y < y) ? min_y : y;
                    min_z = (min_z < z) ? min_z : z;
                    max_x = (max_x > x) ? max_x : x;
                    max_y = (max_y > y) ? max_y : y;
                    max_z = (max_z > z) ? max_z : z;
                }
                else
                {
                    min_x = x;
                    min_y = y;
                    min_z = z;
                    max_x = x;
                    max_y = y;
                    max_z = z;

                    box_exist_flag = true;
                }
            }

            return edge_primitive_number;
        }
        public edge(int body_id, int face_id, int loop_id, int edge_id,
            Edge my_edge, double ChordTolerance, double LengthTolerance,
            StreamWriter mesh_writer, StreamWriter edge_writer, material default_material)
        {
            Curve my_curve= curve_parameter(my_edge, mesh_writer);

            double[][] p = start_end(my_edge, mesh_writer, default_material);

            mesh_writer.WriteLine("             /*  parameter_point_extra_data  */      1");
            mesh_writer.WriteLine("             /*  parameter_point_material    */"
                + "    " + default_material.red  + "    " + default_material.green 
                + "    " + default_material.blue + "    " + default_material.meterial_id);

            int total_edge_primitive_number = tessellation_point(body_id, face_id, loop_id, edge_id,
                my_curve,p[0],p[1], ChordTolerance, LengthTolerance, edge_writer, default_material);

            if (box_exist_flag)
                mesh_writer.WriteLine("             /*  box definition  */  "
                    +min_x + " " + min_y + " " + min_z + " " + max_x + " " + max_y + " " + max_z);
            else
                mesh_writer.WriteLine("             /*  box definition  */	 nobox");

            mesh_writer.WriteLine("             /*  total_edge_primitive_number     */  " + total_edge_primitive_number);
            mesh_writer.WriteLine("             /*  total_point_primitive_number    */  " + total_point_primitive_number);
        }
    }
}
