/*
 * Copyright (C) 2017 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package layers.mercatortiledimagelayer.tianditu;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.mercator.*;
import gov.nasa.worldwind.util.*;

import java.net.*;

public class TiandituMercatorSateLayer extends BasicMercatorTiledImageLayer {
    public static final String LAYERNAME = "Tianditu Mercator Sate";

    public TiandituMercatorSateLayer() {
        super(makeLevels());
        this.setName(LAYERNAME);
    }

    private static LevelSet makeLevels() {
        AVList params = new AVListImpl();

        params.setValue(AVKey.TILE_WIDTH, 256);				//瓦片大小256*256
        params.setValue(AVKey.TILE_HEIGHT, 256);
        params.setValue(AVKey.DISPLAY_NAME, LAYERNAME);
        params.setValue(AVKey.NAME, LAYERNAME);
        params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/SateMercator");
        params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/img_w/wmts");
        params.setValue(AVKey.LAYER_NAME, "img");
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

            int row =  (1 << (tile.getLevelNumber()) + 3) - 1 - tile.getRow();
            int col = tile.getColumn();
            int level = tile.getLevelNumber()+3;
            String serverURL = tile.getLevel().getService().replaceFirst("0", String.valueOf((int)(Math.random() * 8)));
            //System.out.println("serverURL:"+serverURL);
            //tile URL
            String fullurl = "http://t0.tianditu.com/DataServer?T=img_w&x="+col+"&y="+row+"&l="+level;
            //String fullurl =  serverURL +
                //"?service=wmts&request=GetTile&version=1.0.0&LAYER=img&tileMatrixSet=w&TileMatrix="+ level + "&TileRow="+ row +"&TileCol="+col+"&style=default&format=tiles";
                //"?request=GetTile&service=wmts&version=1.0.0&serviceMode=kvp&layer=img&Style=default&Format=tiles&TileMatrixSet=w&TileMatrix="+level+"&TileRow="+row+"&TileCol=" + col;
            System.out.println("fullurl:"+fullurl);
            return new URL(fullurl);
        }
    }
}
