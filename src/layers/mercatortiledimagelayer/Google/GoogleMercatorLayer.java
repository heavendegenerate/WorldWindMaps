/*
 * Copyright (C) 2017 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package layers.mercatortiledimagelayer.Google;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.mercator.*;
import gov.nasa.worldwind.util.*;

import java.net.*;

public class GoogleMercatorLayer extends BasicMercatorTiledImageLayer{
    public static final String GOOGLE_SATE = "Google Mercator Sate";
    public static final String GOOGLE_VECOR = "Google Mercator Vector";
    public static final String GOOGLE_TERRAIN = "Google Mercator Terrain";
    public static final String GOOGLE_SATE_LABEL_ZH = "Google Mercator Sate Label ZH";
    public static final String GOOGLE_TERRAIN_LABEL_ZH = "Google Mercator Terrain Label ZH";
    public static final String GOOGLE_MAP = "Google Mercator Map";
    public static final String GOOGLE_LABEL = "Google Mercator Label";

    public GoogleMercatorLayer(String name) {
        super(makeLevels(name));
        this.setName(name);

    }
 
    private static LevelSet makeLevels(String name) {
        // TODO Auto-generated method stub
        AVList params = new AVListImpl();
 
        params.setValue(AVKey.TILE_WIDTH, 256);
        params.setValue(AVKey.TILE_HEIGHT, 256);

        params.setValue(AVKey.DATASET_NAME, "sate");
        params.setValue(AVKey.FORMAT_SUFFIX, ".png");
        params.setValue(AVKey.NUM_LEVELS, 18);
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle
                .fromDegrees(22.5d), Angle.fromDegrees(45d)));
        params.setValue(AVKey.SECTOR, new MercatorSector(-1.0, 1.0,
                Angle.NEG180, Angle.POS180));
        params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilder());
        switch (name) {
            case GOOGLE_TERRAIN:
                params.setValue(AVKey.DATA_CACHE_NAME, "Google/MercatorTerrain");
                params.setValue(AVKey.SERVICE, "http://www.google.cn/maps/vt?lyrs=t");
                params.setValue(AVKey.LAYER_NAME, "terrain");
                break;
            case GOOGLE_TERRAIN_LABEL_ZH:
                params.setValue(AVKey.DATA_CACHE_NAME, "Google/MercatorTerrainLabel");
                params.setValue(AVKey.SERVICE, "http://www.google.cn/maps/vt?lyrs=p");
                params.setValue(AVKey.LAYER_NAME, "terrainlabel");
                break;
            case GOOGLE_SATE:
                params.setValue(AVKey.DATA_CACHE_NAME, "Google/MercatorSate");
                params.setValue(AVKey.SERVICE, "http://www.google.cn/maps/vt?lyrs=s");
                params.setValue(AVKey.LAYER_NAME, "sate");
                break;
            case GOOGLE_SATE_LABEL_ZH:
                params.setValue(AVKey.DATA_CACHE_NAME, "Google/MercatorSateLabel");
                params.setValue(AVKey.SERVICE, "http://www.google.cn/maps/vt?lyrs=y");
                params.setValue(AVKey.LAYER_NAME, "satelabel");
                break;
            case GOOGLE_MAP:
                params.setValue(AVKey.DATA_CACHE_NAME, "Google/MercatorMap");
                params.setValue(AVKey.SERVICE, "http://www.google.cn/maps/vt?lyrs=m");
                params.setValue(AVKey.LAYER_NAME, "road");
                break;
            case GOOGLE_LABEL:
                params.setValue(AVKey.DATA_CACHE_NAME, "Google/MercatorLabel");
                params.setValue(AVKey.SERVICE, "http://www.google.cn/maps/vt?lyrs=h");
                params.setValue(AVKey.LAYER_NAME, "label");
                break;

        }
 
        return new LevelSet(params);
    }
    private static class URLBuilder implements TileUrlBuilder
    {
        public URL getURL(Tile tile, String imageFormat)
                throws MalformedURLException
        {
            //http://www.google.cn/maps/vt?lyrs=s@725&gl=cn&x=432&y=188&z=9
            //"http://www.google.cn/maps/vt?lyrs=s@183&gl=cn&x=%s&y=%s&z=%s"
            String serverUrl = tile.getLevel().getService();
            String url=String.format(
                    serverUrl + "@725&gl=cn&x=%s&y=%s&z=%s",
                    tile.getColumn(),
                    (1 << (tile.getLevelNumber()) + 3) - 1 - tile.getRow(),
                    (tile.getLevelNumber() + 3) 
                    );
            //System.out.println(url);
            return new URL(url);
        }
    }
 
}
