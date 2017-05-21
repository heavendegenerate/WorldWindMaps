/*
 * Copyright (C) 2017 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package layers.mercatortiledimagelayer.baidu;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.mercator.*;
import gov.nasa.worldwind.util.*;
import layers.tiledimagelayer.baidu.BaiduMapLayer;

import java.net.*;

public class BaiduMercatorMapLayer extends BasicMercatorTiledImageLayer {
    public static final String LAYERNAME = "Baidu Map Mercator";
    private static double[] array1 = { 75, 60, 45, 30, 15, 0 };
    private static double[] array3 = { 12890594.86, 8362377.87, 5591021, 3481989.83, 1678043.12, 0 };
    private static double[][] array2 = {new double[]{-0.0015702102444, 111320.7020616939, 1704480524535203d, -10338987376042340d, 26112667856603880d,-35149669176653700d, 26595700718403920d, -10725012454188240d, 1800819912950474d, 82.5}
        ,new double[]{0.0008277824516172526, 111320.7020463578, 647795574.6671607, -4082003173.641316, 10774905663.51142, -15171875531.51559, 12053065338.62167, -5124939663.577472, 913311935.9512032, 67.5}
        ,new double[]{0.00337398766765, 111320.7020202162, 4481351.045890365, -23393751.19931662, 79682215.47186455, -115964993.2797253, 97236711.15602145, -43661946.33752821, 8477230.501135234, 52.5}
        ,new double[]{0.00220636496208, 111320.7020209128, 51751.86112841131, 3796837.749470245, 992013.7397791013, -1221952.21711287, 1340652.697009075, -620943.6990984312, 144416.9293806241, 37.5}
        ,new double[]{-0.0003441963504368392, 111320.7020576856, 278.2353980772752, 2485758.690035394, 6070.750963243378, 54821.18345352118, 9540.606633304236, -2710.55326746645, 1405.483844121726, 22.5}
        ,new double[]{-0.0003218135878613132, 111320.7020701615, 0.00369383431289, 823725.6402795718, 0.46104986909093, 2351.343141331292, 1.58060784298199, 8.77738589078284, 0.37238884252424, 7.45}};
    private static double[][] array4 = {new double[]{1.410526172116255e-8, 0.00000898305509648872, -1.9939833816331, 200.9824383106796, -187.2403703815547, 91.6087516669843, -23.38765649603339, 2.57121317296198, -0.03801003308653, 17337981.2}
        ,new double[]{-7.435856389565537e-9, 0.000008983055097726239, -0.78625201886289, 96.32687599759846, -1.85204757529826, -59.36935905485877, 47.40033549296737, -16.50741931063887, 2.28786674699375, 10260144.86}
        ,new double[]{-3.030883460898826e-8, 0.00000898305509983578, 0.30071316287616, 59.74293618442277, 7.357984074871, -25.38371002664745, 13.45380521110908, -3.29883767235584, 0.32710905363475, 6856817.37}
        ,new double[]{-1.981981304930552e-8, 0.000008983055099779535, 0.03278182852591, 40.31678527705744, 0.65659298677277, -4.44255534477492, 0.85341911805263, 0.12923347998204, -0.04625736007561, 4482777.06}
        ,new double[]{3.09191371068437e-9, 0.000008983055096812155, 0.00006995724062, 23.10934304144901, -0.00023663490511, -0.6321817810242, -0.00663494467273, 0.03430082397953, -0.00466043876332, 2555164.4}
        ,new double[]{2.890871144776878e-9, 0.000008983055095805407, -3.068298e-8, 7.47137025468032, -0.00000353937994, -0.02145144861037, -0.00001234426596, 0.00010322952773, -0.00000323890364, 826088.5}};


    public BaiduMercatorMapLayer() {
        super(makeLevels());
        this.setName(LAYERNAME);

    }

    private static LevelSet makeLevels() {
        AVList params = new AVListImpl();

        params.setValue(AVKey.TILE_WIDTH, 256);				//瓦片大小256*256
        params.setValue(AVKey.TILE_HEIGHT, 256);
        params.setValue(AVKey.DISPLAY_NAME, LAYERNAME);
        params.setValue(AVKey.NAME, LAYERNAME);
        params.setValue(AVKey.DATA_CACHE_NAME, "Baidu/MapMercator");
        params.setValue(AVKey.SERVICE, "http://online0.map.bdimg.com/onlinelabel/");
        params.setValue(AVKey.LAYER_NAME, "onlinelabel");
        params.setValue(AVKey.DATASET_NAME, LAYERNAME);
        params.setValue(AVKey.FORMAT_SUFFIX, ".png");		//瓦片格式

        params.setValue(AVKey.NUM_LEVELS, 16);				//瓦片级数
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);		//开始级数
        //params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(45d), Angle.fromDegrees(45d)));//初始瓦片覆盖经纬度大小
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle
            .fromDegrees(45d), Angle.fromDegrees(90d)));
        //params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);	//覆盖范围（全球）
        params.setValue(AVKey.SECTOR, new MercatorSector(-1.0, 1.0, Angle.NEG180, Angle.POS180));
        params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilder());//URL请求


        return new LevelSet(params);
    }


    private static class URLBuilder implements TileUrlBuilder {
        public URL getURL(Tile tile, String imageFormat)
            throws MalformedURLException
        {

            LatLon  tileCentorid =  tile.getSector().getCentroid();
            double longitude = tileCentorid.asDegreesArray()[1];
            double latitude = tileCentorid.asDegreesArray()[0];
            System.out.println("longitude;"+ longitude + ", latitude " + latitude );
            longitude = tile.getSector().getMinLongitude().getDegrees();
            latitude = tile.getSector().getMinLatitude().getDegrees();
            System.out.println("longitude;"+ longitude + ", latitude " + latitude );
            if(latitude <= 74 && latitude >= -74) {
                double[] pos = LatLng2Mercator(tileCentorid);
                int level = tile.getLevel().getLevelNumber() + 3;

                int row = (int) (pos[1] / (Math.pow(2, 18 - level) * 256));
                int col = (int) (pos[0] / (Math.pow(2, 18 - level) * 256));
                //String serverURL = tile.getLevel().getService().replaceFirst("0", String.valueOf((int)(Math.random() * 5)));
                String serverURL = tile.getLevel().getService();
                //System.out.println("serverURL:"+serverURL);
                //tile URL
                //http://online2.map.bdimg.com/pvd/?qt=tile&x=843&y=310&z=12&styles=pl&p=0&cm=1&limit=80&v=088&udt=20170516
                //http://online0.map.bdimg.com/onlinelabel/?qt=tile&x=707&y=217&z=12&styles=pl&udt=20151021&scaler=1&p=1
                //http://online2.map.bdimg.com/onlinelabel/?qt=tile&x=211&y=79&z=10&styles=pl&udt=20170516&scaler=1&p=0
                String fullurl = serverURL + "?qt=tile&x=" + col + "&y=" + row + "&z=" + level
                    + "&styles=pl&udt=20170516&scaler=1&p=0";
                System.out.println("fullurl:" + fullurl);
                return new URL(fullurl);
            } else {
                return new URL(null);
            }
        }
    }

    private static double[] LatLng2Mercator(LatLon p)
    {
        double[] arr = null;
        double n_lat = p.latitude.getDegrees() > 74 ? 74 : p.latitude.getDegrees();
        n_lat = n_lat < -74 ? -74 : n_lat;
        for (int i = 0; i < array1.length; i++)
        {
            if (p.latitude.getDegrees() >= array1[i])
            {
                arr = array2[i];
                break;
            }
        }
        if (arr == null)
        {
            for (int i = array1.length - 1; i >= 0; i--)
            {
                if (p.latitude.getDegrees() <= -array1[i])
                {
                    arr = array2[i];
                    break;
                }
            }
        }
        double[] res = Convertor(p.longitude.getDegrees(), p.latitude.getDegrees(), arr);
        return res;
    }

    private static double[] Convertor(double x, double y, double[] param)
    {
        double T = param[0] + param[1] * Math.abs(x);
        double cC = Math.abs(y) / param[9];
        double cF = param[2] + param[3] * cC + param[4] * cC * cC + param[5] * cC * cC * cC + param[6] * cC * cC * cC * cC + param[7] * cC * cC * cC * cC * cC + param[8] * cC * cC * cC * cC * cC * cC;
        T *= (x < 0 ? -1 : 1);
        cF *= (y < 0 ? -1 : 1);
        return new double[] { T, cF };
    }

}
