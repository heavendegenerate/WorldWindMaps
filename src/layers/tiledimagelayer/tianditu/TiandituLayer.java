/*
 * Copyright (C) 2017 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package layers.tiledimagelayer.tianditu;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

import java.net.*;

public class TiandituLayer extends WMSTiledImageLayer
{
    public static final String TIANDITU_SATE = "Tianditu Sate";
    public static final String TIANDITU_VECTOR = "Tianditu Vector";
    public static final String TIANDITU_BOUNDARY = "Tianditu Boundary";
    public static final String TIANDITU_SATE_LABEL_ZH = "Tianditu Sate Label ZH";
    public static final String TIANDITU_SATE_LABEL_EN = "Tianditu Sate Label EN";
    public static final String TIANDITU_VECTOR_LABEL_ZH = "Tianditu Vector Label ZH";
    public static final String TIANDITU_VECTOR_LABEL_EN = "Tianditu Vector Label EN";


    public TiandituLayer(String name){
        super(makeLevels(name));
        this.setDetailHint(0.4);
        this.setName(name);

    }

    private static AVList makeLevels(String name)
    {
        AVList params = new AVListImpl();

        params.setValue(AVKey.TILE_WIDTH, 256);				//瓦片大小256*256
        params.setValue(AVKey.TILE_HEIGHT, 256);
        params.setValue(AVKey.DATASET_NAME, name);
        params.setValue(AVKey.FORMAT_SUFFIX, ".png");		//瓦片格式
        params.setValue(AVKey.NUM_LEVELS, 16);				//瓦片级数
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);		//开始级数
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(45d), Angle.fromDegrees(45d)));//初始瓦片覆盖经纬度大小
        params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);	//覆盖范围（全球）
        params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilder());//URL请求
        switch (name) {
            case TIANDITU_SATE:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/Sate");
                params.setValue(AVKey.LAYER_NAME, "img");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/img_c/wmts");
                break;
            case TIANDITU_SATE_LABEL_EN:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/SateLabelEN");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/eia_c/wmts");
                params.setValue(AVKey.LAYER_NAME, "eia");
                break;
            case TIANDITU_SATE_LABEL_ZH:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/SateLabelZH");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/cia_c/wmts");
                params.setValue(AVKey.LAYER_NAME, "cia");
                break;
            case TIANDITU_VECTOR:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/Vector");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/vec_c/wmts");
                params.setValue(AVKey.LAYER_NAME, "vec");
                break;
            case TIANDITU_VECTOR_LABEL_ZH:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/VectorLabelZH");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/cva_c/wmts");
                params.setValue(AVKey.LAYER_NAME, "cva");
                break;
            case TIANDITU_VECTOR_LABEL_EN:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/VectorLabelEN");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/eva_c/wmts");
                params.setValue(AVKey.LAYER_NAME, "eva");
                break;
            case TIANDITU_BOUNDARY:
                params.setValue(AVKey.DATA_CACHE_NAME, "Tianditu/Boundary");
                params.setValue(AVKey.SERVICE, "http://t0.tianditu.com/ibo_c/wmts");
                params.setValue(AVKey.LAYER_NAME, "ibo");
                break;
            default:
                break;
        }




        return params;
    }


    private static class URLBuilder implements TileUrlBuilder
    {
        public URL getURL(Tile tile, String imageFormat)
            throws MalformedURLException
        {
            int row = (int) Math.pow(2, (tile.getLevelNumber() + 2)) - 1 - tile.getRow();//计算行列和级数
            int col = tile.getColumn();
            int level = tile.getLevelNumber()+3;

            String serverURL = tile.getLevel().getService().replaceFirst("0", String.valueOf((int)(Math.random() * 8)));//由于服务器端采用了集群技术，http://tile0/同http://tile7/取的是同一图片
            String layername = tile.getLevel().getParams().getValue(AVKey.LAYER_NAME).toString();
            //System.out.println("Layer Name:"+layername);
            //瓦片URL串
            String fullurl = serverURL + "?request=GetTile&service=wmts&version=1.0.0&serviceMode=kvp&layer="+ layername+ "&Style=default&Format=tiles&TileMatrixSet=c&TileMatrix="+level+"&TileRow="+row+"&TileCol=" + col;


            return new URL(fullurl);
        }
    }

}
