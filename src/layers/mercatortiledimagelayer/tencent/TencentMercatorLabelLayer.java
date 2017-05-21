/*
 * Copyright (C) 2017 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package layers.mercatortiledimagelayer.tencent;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.mercator.*;
import gov.nasa.worldwind.util.*;

import java.net.*;

import static gov.nasa.worldwind.layers.mercator.MercatorSector.gudermannian;

public class TencentMercatorLabelLayer extends BasicMercatorTiledImageLayer {
    public static final String LAYERNAME = "Tencent Label Mercator";

    public TencentMercatorLabelLayer() {
        super(makeLevels());
        this.setName(LAYERNAME);

    }

    private static LevelSet makeLevels() {
        AVList params = new AVListImpl();

        params.setValue(AVKey.TILE_WIDTH, 256);				//瓦片大小256*256
        params.setValue(AVKey.TILE_HEIGHT, 256);
        params.setValue(AVKey.DISPLAY_NAME, LAYERNAME);
        params.setValue(AVKey.NAME, LAYERNAME);
        params.setValue(AVKey.DATA_CACHE_NAME, "Tencent/MercatorLabel");
        params.setValue(AVKey.SERVICE, "http://rt0.map.gtimg.com/tile");
        params.setValue(AVKey.LAYER_NAME, "label");
        params.setValue(AVKey.DATASET_NAME, LAYERNAME);
        params.setValue(AVKey.FORMAT_SUFFIX, ".png");		//瓦片格式
        params.setValue(AVKey.NUM_LEVELS, 16);				//瓦片级数
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);		//开始级数
        //params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(45d), Angle.fromDegrees(45d)));//初始瓦片覆盖经纬度大小
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle
            .fromDegrees(22.5d), Angle.fromDegrees(45d)));
        //params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);	//覆盖范围（全球）
        params.setValue(AVKey.SECTOR, new MercatorSector(-1.0, 1.0, Angle.NEG180, Angle.POS180));
        params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilder());//URL请求

        return new LevelSet(params);
    }




    private static class URLBuilder implements TileUrlBuilder {
        public URL getURL(Tile tile, String imageFormat)
            throws MalformedURLException
        {

            //int row =  (1 << (tile.getLevelNumber()) + 3) - 1 - tile.getRow();
            int row =  tile.getRow();
            int col = tile.getColumn();
            int level = tile.getLevelNumber()+3;

            String serverURL = tile.getLevel().getService().replaceFirst("0", String.valueOf((int)(Math.random() * 4)));;
            //System.out.println("serverURL:"+serverURL);
            //tile URL
            //http://rt{s}.map.gtimg.com/realtimerender?z={z}&x={x}&y={y}&type=vector&style=0&v=1.1.2"
            //String fullurl =  serverURL + "?z=" + level +"&x=" + col + "&y="+ row+"&type=vector&style=0&v=1.1.2";
            String fullurl =  serverURL + "?z=" + level +"&x=" + col + "&y="+ row+"&styleid=2&version=227";
            System.out.println("fullurl:"+fullurl);
            return new URL(fullurl);
        }
    }
}
