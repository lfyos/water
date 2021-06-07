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
    class solidworks
    {
        private string do_extract(string charset,int tessellation_quality,double ChordTolerance,double LengthTolerance)
        {
            Console.WriteLine("Starting Solidworks......");

            string solidworks_name = "SldWorks.Application";

            ISldWorks sw = null;
            try
            {
                sw=System.Runtime.InteropServices.Marshal.GetActiveObject(solidworks_name) as SolidWorks.Interop.sldworks.ISldWorks;
            }
            catch (Exception)
            {
                sw = null;
            }
            if(sw==null)
                try
                {
                    sw = System.Activator.CreateInstance(System.Type.GetTypeFromProgID(solidworks_name)) as SolidWorks.Interop.sldworks.ISldWorks;
                }
                catch (Exception)
                {
                    sw = null;
                }

            if (sw == null)
            {
                Console.WriteLine("Can NOT start or connect to Solidworks");
                return "";
            }

            sw.Visible = true;
            sw.CommandInProgress = true;

            IModelDoc2 doc=(ModelDoc2)(sw.ActiveDoc);
            if (doc== null)
            {
                Console.WriteLine("NO active Solidworks document exists");
                return "";
            }
            string document_path_name=doc.GetPathName();
            if (doc.GetType() == (int)(swDocumentTypes_e.swDocASSEMBLY))
            {
                Console.WriteLine("Starting assemble ResolveAllLightweight: " + document_path_name);
                ((IAssemblyDoc)doc).ResolveAllLightweight();
            }

            if(tessellation_quality>0)
                doc.SetTessellationQuality(tessellation_quality);

            Console.WriteLine("Starting ForceRebuild3: " + document_path_name);
            doc.ForceRebuild3(false);
            doc.ViewZoomtofit2();
            doc.GraphicsRedraw2();
            Console.WriteLine("ForceRebuild3 finished : " + document_path_name);

            string target_directory_name = document_path_name+".lfy_3d\\";

            if (System.IO.Directory.Exists(target_directory_name))
                new DirectoryInfo(target_directory_name).Delete(true);
            new DirectoryInfo(target_directory_name).Create();

            FileStream assemble_stream = new FileStream(target_directory_name + "assemble.assemble", FileMode.Create, FileAccess.Write);
            StreamWriter assemble_writer = new StreamWriter(assemble_stream, Encoding.GetEncoding(charset));

            part_collector collector = new part_collector(target_directory_name, charset, ChordTolerance, LengthTolerance);

            if (doc.GetType() == (int)(swDocumentTypes_e.swDocASSEMBLY))
                new assemble(doc, collector, target_directory_name).root_tree_node.write("", assemble_writer);
            else if (doc.GetType() == (int)(swDocumentTypes_e.swDocPART))
            {
                collector.register(doc,1);
                assemble_writer.WriteLine("solidworks_root_component");
                assemble_writer.WriteLine("solidworks_part_0");
                assemble_writer.WriteLine("1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0	0.0	0.0	0.0	0.0	1.0");
                assemble_writer.WriteLine("0");
            }
            else
                Console.WriteLine("Can NOT get Solidworks document type: " + document_path_name);

            collector.close();
            assemble_writer.Close();
            assemble_stream.Close();

            sw.QuitDoc(doc.GetTitle());
            sw.CommandInProgress = false;

            return document_path_name;
        }
        public solidworks()
        {
            StreamReader config = new StreamReader(
                AppDomain.CurrentDomain.BaseDirectory + "\\config.txt",
                System.Text.Encoding.GetEncoding("gb2312"));
            string charset              =   config.ReadLine().Trim();
            int tessellation_quality    =   int.Parse(config.ReadLine().Trim());
            double ChordTolerance       =   double.Parse(config.ReadLine().Trim());
            double LengthTolerance      =   double.Parse(config.ReadLine().Trim());
            config.Close();

            Console.WriteLine("Extract_solidworks_data: charset:"               + charset);
            Console.WriteLine("Extract_solidworks_data: tessellation_quality:"  + tessellation_quality);
            Console.WriteLine("Extract_solidworks_data: ChordTolerance:"        + ChordTolerance);
            Console.WriteLine("Extract_solidworks_data: LengthTolerance:"       + LengthTolerance);
            Console.WriteLine("End extract_solidworks_data from "               +
                do_extract(charset, tessellation_quality, ChordTolerance,LengthTolerance));
        }
    }
}
 