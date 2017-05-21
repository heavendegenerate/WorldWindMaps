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
import layers.mercatortiledimagelayer.CustomedMercatorTiledImageLayer;

import java.net.*;

public class TiandituMercatorLayer extends BasicMercatorTiledImageLayer {
//public class TiandituMercatorLayer extends CustomedMercatorTiledImageLayer {
    public static final String TIANDITU_SATE = "Tianditu Mercator Sate";
    public static final String TIANDITU_VECTOR = "Tianditu Mercator Vector";
    public static final String TIANDITU_BOUNDARY = "Tianditu Mercator Boundary";
    public static final String TIANDITU_SATE_LABEL_ZH = "Tianditu Mercator Sate Label ZH";
    public static final String TIANDITU_SATE_LABEL_EN = "Tianditu Mercator Sate Label EN";
    public static final String TIANDITU_VECTOR_LABEL_ZH = "Tianditu Mercator Vector Label ZH";
    public static final String TIANDITU_VECTOR_LABEL_EN = "Tianditu Mercator Vector Label EN";

    public TiandituMercatorLayer(String name) {
        super(makeLevels(name));
        this.setName(name);
    }

    private static LevelSet makeLevels(String name) {
        AVList params = new AVListImpl();

        params.setValue(AVKey.TILE_WIDTH, 256);				//瓦片大小256*256
        params.setValue(AVKey.TILE_HEIGHT, 256);
        params.setValue(AVKey.DISPLAY_NAME, name);
        params.setValue(AVKey.NAME, name);
        params.setValue(AVKey.DATASET_NAME, name);
        params.setValue(AVKey.FORMAT_SUFFIX, ".png");		//瓦片格式
        params.setValue(AVKey.NUM_LEVELS, 16);				//瓦片级数
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);		//开始级数
        //params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(45d), Angle.fromDegrees(45d)));//初始瓦片覆盖经纬度大小
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle
            .fromDegrees(22.5d), Angle.fromDegrees(45d)));
        //params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);	//覆盖范围（全球）
        params.setValue(AVKey.SECTOR, new MercatorSector(-1.0, 1.0, Angle.NEG180, Angle.POS180));
        params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilder());//URL请求

        switch(name) {
            case TIANDITU_SATE:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/SateMercator");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/img_w/wmts");
                params.setValue(AVKey.LAYER_NAME, "img");
                break;
            case TIANDITU_SATE_LABEL_EN:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/SateLabelENMercator");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/eia_w/wmts");
                params.setValue(AVKey.LAYER_NAME, "eia");
                break;
            case TIANDITU_SATE_LABEL_ZH:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/SateLabelZHMercator");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/cia_w/wmts");
                params.setValue(AVKey.LAYER_NAME, "cia");
                break;
            case TIANDITU_VECTOR:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/VectorMercator");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/vec_w/wmts");
                params.setValue(AVKey.LAYER_NAME, "vec");
                break;
            case TIANDITU_VECTOR_LABEL_ZH:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/VectorLabelZHMercator");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/cva_w/wmts");
                params.setValue(AVKey.LAYER_NAME, "cva");
                break;
            case TIANDITU_VECTOR_LABEL_EN:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/VectorLabelENMercator");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/eva_w/wmts");
                params.setValue(AVKey.LAYER_NAME, "eva");
                break;
            case TIANDITU_BOUNDARY:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/BoundaryMercator");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/ibo_w/wmts");
                params.setValue(AVKey.LAYER_NAME, "ibo");
                break;
            default:
                break;

        }

        return new LevelSet(params);
    }


    private static class URLBuilder implements TileUrlBuilder {
        public URL getURL(Tile tile, String imageFormat)
            throws MalformedURLException
        {

            int row =  (1 << (tile.getLevelNumber()) + 3) - 1 - tile.getRow();
            int col = tile.getColumn();
            int level = tile.getLevelNumber()+3;
            String serverURL = tile.getLevel().getService();
            String layername = tile.getLevel().getParams().getValue(AVKey.LAYER_NAME).toString();
            //System.out.println("serverURL:"+serverURL);
            //tile URL
            //String fullurl = "http://t0.tianditu.com/DataServer?T="+ layername+"_w&x="+col+"&y="+row+"&l="+level;
            String fullurl =  serverURL +
                "?service=wmts&request=GetTile&version=1.0.0&LAYER="+ layername+"&tileMatrixSet=w&TileMatrix="+ level + "&TileRow="+ row +"&TileCol="+col+"&style=default&format=tiles";
                //"?request=GetTile&service=wmts&version=1.0.0&serviceMode=kvp&layer="+ layername+"&Style=default&Format=tiles&TileMatrixSet=w&TileMatrix="+level+"&TileRow="+row+"&TileCol=" + col;
            fullurl = fullurl.replaceFirst("0", String.valueOf((int)(Math.random() * 8)));
            System.out.println("fullurl:"+fullurl);
            return new URL(fullurl);
        }
    }
}
