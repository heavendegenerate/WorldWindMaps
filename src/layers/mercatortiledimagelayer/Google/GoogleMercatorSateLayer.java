/*
 * Copyright (C) 2017 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package layers.mercatortiledimagelayer.Google;



import java.net.MalformedURLException;
import java.net.URL;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.mercator.BasicMercatorTiledImageLayer;
import gov.nasa.worldwind.layers.mercator.MercatorSector;
import gov.nasa.worldwind.util.LevelSet;
import gov.nasa.worldwind.util.Tile;
import gov.nasa.worldwind.util.TileUrlBuilder;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

public class GoogleMercatorSateLayer extends BasicMercatorTiledImageLayer{
    public static final String LAYERNAME = "Google Mercator Sate";
	 
    public GoogleMercatorSateLayer() {
        super(makeLevels());
        this.setName(LAYERNAME);

    }
 
    private static LevelSet makeLevels() {
        // TODO Auto-generated method stub
        AVList params = new AVListImpl();
 
        params.setValue(AVKey.TILE_WIDTH, 256);
        params.setValue(AVKey.TILE_HEIGHT, 256);
        params.setValue(AVKey.DATA_CACHE_NAME, "Google/MercatorSate");
        params.setValue(AVKey.DATASET_NAME, "sate");
        params.setValue(AVKey.FORMAT_SUFFIX, ".png");
        params.setValue(AVKey.NUM_LEVELS, 18);
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle
                .fromDegrees(22.5d), Angle.fromDegrees(45d)));
        params.setValue(AVKey.SECTOR, new MercatorSector(-1.0, 1.0,
                Angle.NEG180, Angle.POS180));
        params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilder());
 
        return new LevelSet(params);
    }
    private static class URLBuilder implements TileUrlBuilder
    {
        public URL getURL(Tile tile, String imageFormat)
                throws MalformedURLException
        {
            //http://www.google.cn/maps/vt?lyrs=s@725&gl=cn&x=432&y=188&z=9
            //"http://www.google.cn/maps/vt?lyrs=s@183&gl=cn&x=%s&y=%s&z=%s"
            String url=String.format(
                    "http://www.google.cn/maps/vt?lyrs=s@725&gl=cn&x=%s&y=%s&z=%s",
                    tile.getColumn(),
                    (1 << (tile.getLevelNumber()) + 3) - 1 - tile.getRow(),
                    (tile.getLevelNumber() + 3) 
                    );
            //System.out.println(url);
            return new URL(url);
        }
    }
 
}
