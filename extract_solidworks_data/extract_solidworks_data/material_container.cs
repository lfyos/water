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
    class material_container
    {
        private material []ma;

        public material_container(material default_material)
        {
            ma = new material[] { default_material};
        }

        public void save(StreamWriter material_writer)
        {
            material_writer.WriteLine("         \"material\"	:	[");
            for (int i = 0, ni = ma.Length; i < ni; i++)
                ma[i].save(material_writer,i==(ni-1));
            material_writer.WriteLine("         ]");
        }
        public material get(double[] color_parameter, Texture my_texture)
        {
            material my_m = new material(color_parameter, my_texture);
            for (int i = 0, ni = ma.Length; i < ni; i++)
                if (my_m.compare(ma[i]))
                    return ma[i];

            material[] bak=ma;
            ma = new material[ma.Length+1];
            for (int i = 0, ni = bak.Length; i < ni; i++)
                ma[i] = bak[i];

            ma[ma.Length - 1] = my_m;
            ma[ma.Length - 1].meterial_id = ma.Length - 1;

            return ma[ma.Length - 1];
        }

    }
}
