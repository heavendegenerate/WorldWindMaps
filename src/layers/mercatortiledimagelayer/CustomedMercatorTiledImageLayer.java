/*
 * Copyright (C) 2017 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package layers.mercatortiledimagelayer;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.event.BulkRetrievalListener;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.mercator.*;
import gov.nasa.worldwind.retrieve.*;
import gov.nasa.worldwind.util.*;
import util.TransparentImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

public class CustomedMercatorTiledImageLayer extends BasicMercatorTiledImageLayer implements BulkRetrievable
{
    private String strLayerName = "";
    public CustomedMercatorTiledImageLayer(LevelSet levelSet) {
        super(levelSet);
    }


    public CustomedMercatorTiledImageLayer(TileUrlBuilder ub,String labelName)
    {
        super(makeLevels(ub,labelName,labelName));
    }

    public CustomedMercatorTiledImageLayer(TileUrlBuilder ub,String labelName,String datasetName)
    {
        super(makeLevels(ub,labelName,datasetName));
        strLayerName = labelName;
    }

    private static LevelSet makeLevels(TileUrlBuilder ub,String labelName,String datasetName)
    {
        return makeLevels(ub,labelName,datasetName,".png","www.test.com",0,16);
    }

    private static LevelSet makeLevels(TileUrlBuilder ub,String labelName,String datasetName,String suffix, String serverName,int numLevelMin,int numLevelMax)
    {
        return makeLevels(ub,labelName,datasetName,suffix,serverName,numLevelMin,numLevelMax,256,256);
    }

    private static LevelSet makeLevels(TileUrlBuilder ub, String labelName,String datasetName,String suffix,String serverName,int numLevelMin,int numLevelMax,int tileWidth,int tileHeight)
    {
        AVList params = new AVListImpl();

        String strTemp = "";

        params.setValue(AVKey.TILE_WIDTH, tileWidth);        
        params.setValue(AVKey.TILE_HEIGHT, tileHeight);
        params.setValue(AVKey.DATA_CACHE_NAME, "Earth/"+labelName);
        params.setValue(AVKey.SERVICE, serverName);
        params.setValue(AVKey.DATASET_NAME, datasetName);
        params.setValue(AVKey.FORMAT_SUFFIX, suffix);
        params.setValue(AVKey.NUM_LEVELS, numLevelMax);        
        params.setValue(AVKey.NUM_EMPTY_LEVELS, numLevelMin);
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(22.5d), Angle.fromDegrees(45d)));
        params.setValue(AVKey.SECTOR, new MercatorSector(-1.0, 1.0, Angle.NEG180, Angle.POS180));
        params.setValue(AVKey.TILE_URL_BUILDER, ub);

        return new LevelSet(params);
    }

    protected boolean transformAndSave(BufferedImage image, MercatorSector sector,File outFile)
    {
        try {
            image = transform(image, sector);
            String extension = outFile.getName().substring(
                outFile.getName().lastIndexOf('.') + 1);
            synchronized (this.fileLock) // synchronized with read of file in RequestTask.run()
            {
                return ImageIO.write(image, extension, outFile);
                //return TransparentImageUtil.savePngTransparent(image, extension, outFile);
            }
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String toString()
    {
        return strLayerName;
    }



    ////////////////////////////////////////////////////////////////

    protected void retrieveTexture(MercatorTextureTile tile, DownloadPostProcessor postProcessor)
    {
        if (this.getValue(AVKey.RETRIEVER_FACTORY_LOCAL) != null)
            this.retrieveLocalTexture(tile, postProcessor);
        else
            // Assume it's remote, which handles the legacy cases.
            this.retrieveRemoteTexture(tile, postProcessor);
    }

    protected void retrieveLocalTexture(MercatorTextureTile tile, DownloadPostProcessor postProcessor)
    {
        if (!WorldWind.getLocalRetrievalService().isAvailable())
            return;

        RetrieverFactory retrieverFactory = (RetrieverFactory) this.getValue(AVKey.RETRIEVER_FACTORY_LOCAL);
        if (retrieverFactory == null)
            return;

        AVListImpl avList = new AVListImpl();
        avList.setValue(AVKey.SECTOR, tile.getSector());
        avList.setValue(AVKey.WIDTH, tile.getWidth());
        avList.setValue(AVKey.HEIGHT, tile.getHeight());
        avList.setValue(AVKey.FILE_NAME, tile.getPath());

        Retriever retriever = retrieverFactory.createRetriever(avList, postProcessor);

        WorldWind.getLocalRetrievalService().runRetriever(retriever, tile.getPriority());
    }

    protected void retrieveRemoteTexture(MercatorTextureTile tile, DownloadPostProcessor postProcessor)
    {
        if (!this.isNetworkRetrievalEnabled())
        {
            this.getLevels().markResourceAbsent(tile);
            return;
        }

        if (!WorldWind.getRetrievalService().isAvailable())
            return;

        java.net.URL url;
        try
        {
            url = tile.getResourceURL();
            if (url == null)
                return;

            if (WorldWind.getNetworkStatus().isHostUnavailable(url))
            {
                this.getLevels().markResourceAbsent(tile);
                return;
            }
        }
        catch (java.net.MalformedURLException e)
        {
            Logging.logger().log(java.util.logging.Level.SEVERE,
                    Logging.getMessage("layers.TextureLayer.ExceptionCreatingTextureUrl", tile), e);
            return;
        }

        Retriever retriever;

        if (postProcessor == null)
            //postProcessor = this.createDownloadPostProcessor(tile);
            postProcessor = new DownloadPostProcessor(tile,this);
        retriever = URLRetriever.createRetriever(url, postProcessor);
        if (retriever == null)
        {
            Logging.logger().severe(
                    Logging.getMessage("layers.TextureLayer.UnknownRetrievalProtocol", url.toString()));
            return;
        }
        retriever.setValue(URLRetriever.EXTRACT_ZIP_ENTRY, "true"); // supports legacy layers

        // Apply any overridden timeouts.
        Integer cto = AVListImpl.getIntegerValue(this, AVKey.URL_CONNECT_TIMEOUT);
        if (cto != null && cto > 0)
            retriever.setConnectTimeout(cto);
        Integer cro = AVListImpl.getIntegerValue(this, AVKey.URL_READ_TIMEOUT);
        if (cro != null && cro > 0)
            retriever.setReadTimeout(cro);
        Integer srl = AVListImpl.getIntegerValue(this, AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT);
        if (srl != null && srl > 0)
            retriever.setStaleRequestLimit(srl);

        WorldWind.getRetrievalService().runRetriever(retriever, tile.getPriority());
    }

    //////////////////////////////////////////////////////////////


    @Override
    public BulkRetrievalThread makeLocal(Sector sector, double resolution, BulkRetrievalListener listener) {
        return makeLocal(sector, resolution, null, listener);
    }

    @Override
    public long getEstimatedMissingDataSize(Sector sector, double resolution) {
        return 0;
    }

    @Override
    public long getEstimatedMissingDataSize(Sector sector, double resolution, FileStore fileStore) {
        return 0;
    }

    @Override
    public BulkRetrievalThread makeLocal(Sector sector, double resolution, FileStore fileStore, BulkRetrievalListener listener) {
        Sector targetSector = sector != null ? getLevels().getSector().intersection(sector) : null;
        if (targetSector == null)
            return null;

        CustomedMercatorTiledImageLayerBulkDownloader thread = new CustomedMercatorTiledImageLayerBulkDownloader(this, targetSector,
                resolution, fileStore != null ? fileStore : this.getDataFileStore(), listener);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    public int countImagesInSector(Sector sector, int levelNumber)
    {
        MercatorSector s = MercatorSector.fromSector(sector);
        return countImagesInSector(s,levelNumber);
    }

    public int countImagesInSector(MercatorSector sector, int levelNumber)
    {
        ArrayList<Integer> li = GetRect(sector,levelNumber);

        if(li == null)
            return  0;

        int seRow = li.get(2);
        int nwRow = li.get(3);
        int nwCol = li.get(1);
        int seCol = li.get(0);

        int numRows = nwRow - seRow + 1;
        int numCols = seCol - nwCol + 1;

        return numRows * numCols;
    }

    public MercatorTextureTile[][] getTilesInSector(MercatorSector sector,
                                                    int levelNumber)
    {
        if (sector == null)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Level targetLevel = this.levels.getLastLevel();
        if (levelNumber >= 0)
        {
            for (int i = levelNumber; i < this.getLevels().getLastLevel()
                    .getLevelNumber(); i++)
            {
                if (this.levels.isLevelEmpty(i))
                    continue;

                targetLevel = this.levels.getLevel(i);
                break;
            }
        }

        // Collect all the tiles intersecting the input sector.
        ArrayList<Integer> li = GetRect(sector,levelNumber);

        int seRow = li.get(2);
        int nwRow = li.get(3);
        int nwCol = li.get(1);
        int seCol = li.get(0);

        Angle lonOrigin = this.levels.getTileOrigin().getLongitude();
        double dLat = targetLevel.getTileDelta().getLatitude().degrees/90;
        double dLon = targetLevel.getTileDelta().getLongitude().degrees;

        int numRows = nwRow - seRow + 1;
        int numCols = seCol - nwCol + 1;

        MercatorTextureTile[][] sectorTiles = new MercatorTextureTile[numRows][numCols];

        for (int row = nwRow; row >= seRow; row--)
        {
            for (int col = nwCol; col <= seCol; col++)
            {
                MercatorSector mSector = new MercatorSector(
                        -1 + dLat * row,
                        -1 + dLat * row + dLat,
                        lonOrigin.addDegrees( dLon * col ),
                        lonOrigin.addDegrees( dLon * col + dLon));
                sectorTiles[nwRow - row][col - nwCol] = new MercatorTextureTile(
                        mSector, targetLevel, row, col);
            }
        }

        return sectorTiles;
    }

    public ArrayList<Integer> GetRect(Sector sector,int levelNumber)
    {
        if (sector == null)
        {
            return null;
        }

        Level targetLevel = this.levels.getLastLevel();
        if (levelNumber >= 0)
        {
            for (int i = levelNumber; i < this.getLevels().getLastLevel()
                    .getLevelNumber(); i++)
            {
                if (this.levels.isLevelEmpty(i))
                    continue;

                targetLevel = this.levels.getLevel(i);
                break;
            }
        }

        ArrayList<Integer> li = new ArrayList<Integer>();

        LatLon delta = targetLevel.getTileDelta();
        Angle latOrigin = this.levels.getTileOrigin().getLatitude();
        Angle lonOrigin = this.levels.getTileOrigin().getLongitude();

        double dLatMin = MercatorSector.gudermannianInverse(sector.getMinLatitude());
        double dLatMax = MercatorSector.gudermannianInverse(sector.getMaxLatitude());

        double dLat = delta.getLatitude().degrees/90;
        double dLon = delta.getLongitude().degrees;

        int seRow = (int) ((dLatMin + 1) / dLat);
        int nwRow = (int) ((dLatMax + 1) / dLat);
        int nwCol = Tile.computeColumn(delta.getLongitude(), sector
                .getMinLongitude(), lonOrigin);
        int seCol = Tile.computeColumn(delta.getLongitude(), sector
                .getMaxLongitude(), lonOrigin);

        li.add(seCol);
        li.add(nwCol);

        li.add(seRow);
        li.add(nwRow);

        return li;
    }
}