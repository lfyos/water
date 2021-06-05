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
            new solidworks();
            Console.ReadKey();
        }
    }
}
