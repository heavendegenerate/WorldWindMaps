/*
 * Copyright (C) 2017 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package layers.tiledimagelayer.baidu;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.mercator.*;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

import java.net.*;

public class BaiduMapLayer extends WMSTiledImageLayer {
    public static final String LAYERNAME = "Baidu Map";

    public BaiduMapLayer() {
        super(makeLevels());
        this.setName(LAYERNAME);

    }

    private static AVList makeLevels() {
        AVList params = new AVListImpl();

        params.setValue(AVKey.TILE_WIDTH, 256);				//瓦片大小256*256
        params.setValue(AVKey.TILE_HEIGHT, 256);
        params.setValue(AVKey.DISPLAY_NAME, LAYERNAME);
        params.setValue(AVKey.NAME, LAYERNAME);
        params.setValue(AVKey.DATA_CACHE_NAME, "Baidu/Map");
        params.setValue(AVKey.SERVICE, "http://online0.map.bdimg.com/onlinelabel/");
        params.setValue(AVKey.LAYER_NAME, "onlinelabel");
        params.setValue(AVKey.DATASET_NAME, LAYERNAME);
        params.setValue(AVKey.FORMAT_SUFFIX, ".png");		//瓦片格式
        params.setValue(AVKey.NUM_LEVELS, 16);				//瓦片级数
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);		//开始级数
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(45d), Angle.fromDegrees(45d)));//初始瓦片覆盖经纬度大小
        params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);	//覆盖范围（全球）
        params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilder());//URL请求

        return params;
    }


    private static class URLBuilder implements TileUrlBuilder {
        public URL getURL(Tile tile, String imageFormat)
            throws MalformedURLException
        {

            int row = (int) Math.pow(2, (tile.getLevelNumber() + 2)) - 1 - tile.getRow();//计算行列和级数
            int col = tile.getColumn();
            int level = tile.getLevelNumber()+3;
            //String serverURL = tile.getLevel().getService().replaceFirst("0", String.valueOf((int)(Math.random() * 5)));
            String serverURL = tile.getLevel().getService();
            //System.out.println("serverURL:"+serverURL);
            //tile URL
            //http://online2.map.bdimg.com/pvd/?qt=tile&x=843&y=310&z=12&styles=pl&p=0&cm=1&limit=80&v=088&udt=20170516
            //http://online0.map.bdimg.com/onlinelabel/?qt=tile&x=707&y=217&z=12&styles=pl&udt=20151021&scaler=1&p=1
            String fullurl = serverURL + "?qt=tile&x="+ col +"&y=" + row +"&z="+ level+"&styles=pl&udt=20170516&scaler=1&p=0";
            System.out.println("fullurl:"+fullurl);
            return new URL(fullurl);
        }
    }
}
