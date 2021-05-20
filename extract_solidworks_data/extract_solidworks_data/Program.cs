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
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length <= 2) {
                Console.WriteLine("Provide enough parameter to extract_solidworks_data program");
                return;
            }

            string source_file_name      = args[0];
            string target_directory_name = args[1];
            string charset               = args[2];
            int tessellation_quality     = (args.Length >= 4)?(int.Parse(   args[3])):0;
            double ChordTolerance        = (args.Length >= 5)?(double.Parse(args[4])):0;
            double LengthTolerance       = (args.Length >= 6)?(double.Parse(args[5])):0;

            part_collector collector = new part_collector(target_directory_name,charset, ChordTolerance, LengthTolerance);

            new solidworks(source_file_name, target_directory_name,charset,tessellation_quality, ChordTolerance, LengthTolerance, collector);

            collector.close();
        }
    }
}
