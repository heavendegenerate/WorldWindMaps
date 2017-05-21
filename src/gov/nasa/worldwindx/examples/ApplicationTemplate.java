/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwindx.examples;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.layers.Earth.*;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwindx.examples.util.*;
import layers.mercatortiledimagelayer.Google.*;
import layers.mercatortiledimagelayer.baidu.BaiduMercatorMapLayer;
import layers.mercatortiledimagelayer.esri.*;
import layers.mercatortiledimagelayer.tencent.*;
import layers.mercatortiledimagelayer.tianditu.*;
import layers.tiledimagelayer.baidu.BaiduMapLayer;
import layers.tiledimagelayer.tianditu.*;

import javax.swing.*;
import java.awt.*;

/**
 * Provides a base application framework for simple WorldWind examples. Examine other examples in this package to see
 * how it's used.
 *
 * @version $Id: ApplicationTemplate.java 2115 2014-07-01 17:58:16Z tgaskins $
 */
public class ApplicationTemplate
{
    public static class AppPanel extends JPanel
    {
        private static final String BINGIMAGERYLAYERNAME = "Bing Imagery";
        private static final String PLACENAMESLAYERNAME = "Place Names";
        protected WorldWindow wwd;
        protected StatusBar statusBar;
        protected ToolTipController toolTipController;
        protected HighlightController highlightController;

        public AppPanel(Dimension canvasSize, boolean includeStatusBar)
        {
            super(new BorderLayout());

            this.wwd = this.createWorldWindow();
            ((Component) this.wwd).setPreferredSize(canvasSize);

            // Create the default model as described in the current worldwind properties.
            Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
            this.wwd.setModel(m);



            // Setup a select listener for the worldmap click-and-go feature
            this.wwd.addSelectListener(new ClickAndGoSelectListener(this.getWwd(), WorldMapLayer.class));

            //this.printLayers();
            this.addEsriLayers();
            this.addTiandituMercatorLayers();
            this.addTiandituLayers();
            this.addBaiduLayers();
            this.addTencentLayers();
            this.addGoogleLayers();

            this.add((Component) this.wwd, BorderLayout.CENTER);
            if (includeStatusBar)
            {
                this.statusBar = new StatusBar();
                this.add(statusBar, BorderLayout.PAGE_END);
                this.statusBar.setEventSource(wwd);
            }

            // Add controllers to manage highlighting and tool tips.
            this.toolTipController = new ToolTipController(this.getWwd(), AVKey.DISPLAY_NAME, null);
            this.highlightController = new HighlightController(this.getWwd(), SelectEvent.ROLLOVER);
            //this.jumpHome();

        }

        protected WorldWindow createWorldWindow()
        {
            return new WorldWindowGLCanvas();
        }

        public WorldWindow getWwd()
        {
            return wwd;
        }

        public StatusBar getStatusBar()
        {
            return statusBar;
        }

        private  void jumpHome() {
            final double la = 41.7945;
            final double lo = 123.4407;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        AppPanel.this.wwd.getView().goTo(new Position(Angle.fromDegrees(la),Angle.fromDegrees(lo),0), 500);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        protected void printLayers() {
            LayerList layers = wwd.getModel().getLayers();
            System.out.println(layers.toString());
            for(int i = 0; i < layers.size(); i++){
                Layer layer = layers.get(i);
                System.out.println(layer.getName());
                System.out.println(layer.isEnabled());
            }
        }

        protected void addOSMLayers() {
            OSMCycleMapLayer osmCycleMapLayer = new OSMCycleMapLayer();
            osmCycleMapLayer.setEnabled(false);
            this.addLayer(osmCycleMapLayer, BINGIMAGERYLAYERNAME);

            OSMMapnikLayer osmMapnikLayer = new OSMMapnikLayer();
            osmMapnikLayer.setEnabled(false);
            this.addLayer(osmMapnikLayer, BINGIMAGERYLAYERNAME);
        }

        protected void addEsriLayers() {
            EsriWorldStreetMapLayer esriWorldStreetMapLayer = new EsriWorldStreetMapLayer();
            esriWorldStreetMapLayer.setEnabled(false);
            this.addLayer(esriWorldStreetMapLayer, BINGIMAGERYLAYERNAME);

            EsriWorldImageryLayer esriWorldImageryLayer = new EsriWorldImageryLayer();
            esriWorldImageryLayer.setEnabled(false);
            this.addLayer(esriWorldImageryLayer, BINGIMAGERYLAYERNAME);

            EsriWorldTransportationLayer esriWorldTransportationLayer = new EsriWorldTransportationLayer();
            esriWorldTransportationLayer.setEnabled(false);
            this.addLayer(esriWorldTransportationLayer, BINGIMAGERYLAYERNAME);
        }


        protected void addTiandituMercatorLayers() {
            //Tianditu Vector Map (Mercator)
            TiandituMercatorLayer tiandituMercatorVectorLayer = new TiandituMercatorLayer(TiandituMercatorLayer.TIANDITU_VECTOR);
            tiandituMercatorVectorLayer.setEnabled(false);
            this.addLayer(tiandituMercatorVectorLayer, BINGIMAGERYLAYERNAME);


            // Tianditu Satellite Image (Mercator)
            //TencentMercatorMapLayer tiandituMercatorLayer = new TencentMercatorMapLayer();
            TiandituMercatorLayer tiandituMercatorSateLayer = new TiandituMercatorLayer(TiandituMercatorLayer.TIANDITU_SATE);
            tiandituMercatorSateLayer.setEnabled(false);
            this.addLayer(tiandituMercatorSateLayer, BINGIMAGERYLAYERNAME);

            // Tianditu Boundary (Mercator)
            TiandituMercatorLayer tiandituMercatorBoundaryLayer = new TiandituMercatorLayer(TiandituMercatorLayer.TIANDITU_BOUNDARY);
            tiandituMercatorBoundaryLayer.setEnabled(false);
            this.addLayer(tiandituMercatorBoundaryLayer, BINGIMAGERYLAYERNAME);

            //Tianditu Vector Chinese Label (Mercator)
            TiandituMercatorLayer tiandituMercatorVectorLabelZHLayer = new TiandituMercatorLayer(TiandituMercatorLayer.TIANDITU_VECTOR_LABEL_ZH);
            tiandituMercatorVectorLabelZHLayer.setEnabled(false);
            this.addLayer(tiandituMercatorVectorLabelZHLayer, PLACENAMESLAYERNAME);


            //Tianditu Vector English Label (Mercator)
            //TiandituMercatorVectorLabelENLayer tiandituMercatorVectorLabelENLayer = new TiandituMercatorVectorLabelENLayer();
            TiandituMercatorLayer tiandituMercatorVectorLabelENLayer = new TiandituMercatorLayer(TiandituMercatorLayer.TIANDITU_VECTOR_LABEL_EN);
            tiandituMercatorVectorLabelENLayer.setEnabled(false);
            this.addLayer(tiandituMercatorVectorLabelENLayer, PLACENAMESLAYERNAME);


            //Tianditu Satellite Chineses Label (Mercator)
            TiandituMercatorLayer tiandituMercatorSateLabelZHLayer = new TiandituMercatorLayer(TiandituMercatorLayer.TIANDITU_SATE_LABEL_ZH);
            tiandituMercatorSateLabelZHLayer.setEnabled(false);
            this.addLayer(tiandituMercatorSateLabelZHLayer, PLACENAMESLAYERNAME);

            //Tianditu Satellite English Label (Mercator)
            //TiandituMercatorSateLabelENLayer tiandituMercatorSateLabelENLayer = new TiandituMercatorSateLabelENLayer();
            TiandituMercatorLayer tiandituMercatorSateLabelENLayer = new TiandituMercatorLayer(TiandituMercatorLayer.TIANDITU_SATE_LABEL_EN);
            tiandituMercatorSateLabelENLayer.setEnabled(false);
            this.addLayer(tiandituMercatorSateLabelENLayer, PLACENAMESLAYERNAME);


        }

        protected void addTiandituLayers() {

            //Tianditu Vector Map
            //TiandituVectorLayer tiandituVectorLayer = new TiandituVectorLayer();
            TiandituLayer tiandituVectorLayer = new TiandituLayer(TiandituLayer.TIANDITU_VECTOR);
            tiandituVectorLayer.setEnabled(false);
            this.addLayer(tiandituVectorLayer, BINGIMAGERYLAYERNAME);

            //Tianditu Satellite Image
            //TiandituSateLayer tiandituSateLayer = new TiandituSateLayer();
            TiandituLayer tiandituSateLayer = new TiandituLayer(TiandituLayer.TIANDITU_SATE);
            tiandituSateLayer.setEnabled(false);
            this.addLayer(tiandituSateLayer, BINGIMAGERYLAYERNAME);

            //Tianditu Boundary
            //TiandituBoundaryLayer tiandituBoundaryLayer = new TiandituBoundaryLayer();
            TiandituLayer tiandituBoundaryLayer = new TiandituLayer(TiandituLayer.TIANDITU_BOUNDARY);
            tiandituBoundaryLayer.setEnabled(false);
            this.addLayer(tiandituBoundaryLayer, BINGIMAGERYLAYERNAME);

            //Tianditu Vector Chinese Label
            //TiandituVectorLabelZHLayer tiandituVectorLabelZHLayer = new TiandituVectorLabelZHLayer();
            TiandituLayer tiandituVectorLabelZHLayer = new TiandituLayer(TiandituLayer.TIANDITU_VECTOR_LABEL_ZH);
            tiandituVectorLabelZHLayer.setEnabled(false);
            this.addLayer(tiandituVectorLabelZHLayer,PLACENAMESLAYERNAME);

            //Tianditu Vector English label
            //TiandituVectorLabelENLayer tiandituVectorLabelENLayer = new TiandituVectorLabelENLayer();
            TiandituLayer tiandituVectorLabelENLayer = new TiandituLayer(TiandituLayer.TIANDITU_VECTOR_LABEL_EN);
            tiandituVectorLabelENLayer.setEnabled(false);
            this.addLayer(tiandituVectorLabelENLayer, PLACENAMESLAYERNAME);

            //Tianditu Satellite Chinese Label
            //TiandituSateLabelZHLayer tiandituSateLabelZHLayer = new TiandituSateLabelZHLayer();
            TiandituLayer tiandituSateLabelZHLayer = new TiandituLayer(TiandituLayer.TIANDITU_SATE_LABEL_ZH);
            tiandituSateLabelZHLayer.setEnabled(false);
            this.addLayer(tiandituSateLabelZHLayer,PLACENAMESLAYERNAME);

            //Tianditu Satellite English Label
            //TiandituSateLabelENLayer tiandituSateLabelENLayer = new TiandituSateLabelENLayer();
            TiandituLayer tiandituSateLabelENLayer = new TiandituLayer(TiandituLayer.TIANDITU_SATE_LABEL_EN);
            tiandituSateLabelENLayer.setEnabled(false);
            this.addLayer(tiandituSateLabelENLayer, PLACENAMESLAYERNAME);
        }

        protected void addBaiduLayers() {
            BaiduMercatorMapLayer baiduMercatorMapLayer = new BaiduMercatorMapLayer();
            baiduMercatorMapLayer.setEnabled(false);
            this.addLayer(baiduMercatorMapLayer, BINGIMAGERYLAYERNAME);

            BaiduMapLayer baiduMapLayer = new BaiduMapLayer();
            baiduMapLayer.setEnabled(false);
            this.addLayer(baiduMapLayer, BINGIMAGERYLAYERNAME);
        }

        protected void addTencentLayers() {
            TencentMercatorMapLayer tencentMercatorMapLayer = new TencentMercatorMapLayer();
            tencentMercatorMapLayer.setEnabled(false);
            this.addLayer(tencentMercatorMapLayer, BINGIMAGERYLAYERNAME);

            TencentMercatorSateLayer tencentMercatorSateLayer = new TencentMercatorSateLayer();
            tencentMercatorSateLayer.setEnabled(false);
            this.addLayer(tencentMercatorSateLayer,BINGIMAGERYLAYERNAME);

            TencentMercatorLabelLayer tencentMercatorLabelLayer = new TencentMercatorLabelLayer();
            tencentMercatorLabelLayer.setEnabled(false);
            this.addLayer(tencentMercatorLabelLayer, PLACENAMESLAYERNAME);
        }

        protected void addGoogleLayers() {
            GoogleMercatorLayer googleMercatorTerrainLayer = new GoogleMercatorLayer(GoogleMercatorLayer.GOOGLE_TERRAIN);
            googleMercatorTerrainLayer.setEnabled(false);
            this.addLayer(googleMercatorTerrainLayer, BINGIMAGERYLAYERNAME);

            GoogleMercatorLayer googleMercatorTerrainLabelZHLayer = new GoogleMercatorLayer(GoogleMercatorLayer.GOOGLE_TERRAIN_LABEL_ZH);
            googleMercatorTerrainLabelZHLayer.setEnabled(false);
            this.addLayer(googleMercatorTerrainLabelZHLayer, BINGIMAGERYLAYERNAME);

            GoogleMercatorLayer googleMercatorMapLayer = new GoogleMercatorLayer(GoogleMercatorLayer.GOOGLE_MAP);
            googleMercatorMapLayer.setEnabled(false);
            this.addLayer(googleMercatorMapLayer, BINGIMAGERYLAYERNAME);

            //GoogleMercatorSateLayer googleMercatorSateLayer = new GoogleMercatorSateLayer();
            GoogleMercatorLayer googleMercatorSateLayer = new GoogleMercatorLayer(GoogleMercatorLayer.GOOGLE_SATE);
            googleMercatorSateLayer.setEnabled(false);
            this.addLayer(googleMercatorSateLayer, BINGIMAGERYLAYERNAME);

            GoogleMercatorLayer googleMercatorSateLabelZHLayer  = new GoogleMercatorLayer(GoogleMercatorLayer.GOOGLE_SATE_LABEL_ZH);
            googleMercatorSateLabelZHLayer.setEnabled(false);
            this.addLayer(googleMercatorSateLabelZHLayer, BINGIMAGERYLAYERNAME);



            GoogleMercatorLayer googleMercatorLabelLayer = new GoogleMercatorLayer(GoogleMercatorLayer.GOOGLE_LABEL);
            googleMercatorLabelLayer.setEnabled(false);
            this.addLayer(googleMercatorLabelLayer, PLACENAMESLAYERNAME);

        }

        protected void addLayer(Layer layer, String posLayerName) {
            LayerList layers = wwd.getModel().getLayers();
            for(int i = 0; i < layers.size(); i++) {
                if(layers.get(i).getName().equals(posLayerName)) {
                    layers.add(i, layer);
                    break;
                }
            }
        }
    }

    protected static class AppFrame extends JFrame
    {
        private Dimension canvasSize = new Dimension(1000, 800);

        protected AppPanel wwjPanel;
        protected JPanel controlPanel;
        protected LayerPanel layerPanel;
        protected StatisticsPanel statsPanel;

        public AppFrame()
        {
            this.initialize(true, true, false);
        }

        public AppFrame(Dimension size)
        {
            this.canvasSize = size;
            this.initialize(true, true, false);
        }

        public AppFrame(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel)
        {
            this.initialize(includeStatusBar, includeLayerPanel, includeStatsPanel);
        }

        protected void initialize(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel)
        {
            // Create the WorldWindow.
            this.wwjPanel = this.createAppPanel(this.canvasSize, includeStatusBar);
            this.wwjPanel.setPreferredSize(canvasSize);

            // Put the pieces together.
            this.getContentPane().add(wwjPanel, BorderLayout.CENTER);
            if (includeLayerPanel)
            {
                this.controlPanel = new JPanel(new BorderLayout(10, 10));
                this.layerPanel = new LayerPanel(this.getWwd());
                this.controlPanel.add(this.layerPanel, BorderLayout.CENTER);
                this.controlPanel.add(new FlatWorldPanel(this.getWwd()), BorderLayout.NORTH);
                this.getContentPane().add(this.controlPanel, BorderLayout.WEST);
            }

            if (includeStatsPanel || System.getProperty("gov.nasa.worldwind.showStatistics") != null)
            {
                this.statsPanel = new StatisticsPanel(this.wwjPanel.getWwd(), new Dimension(250, canvasSize.height));
                this.getContentPane().add(this.statsPanel, BorderLayout.EAST);
            }

            // Create and install the view controls layer and register a controller for it with the World Window.
            ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
            insertBeforeCompass(getWwd(), viewControlsLayer);
            this.getWwd().addSelectListener(new ViewControlsSelectListener(this.getWwd(), viewControlsLayer));

            // Register a rendering exception listener that's notified when exceptions occur during rendering.
            this.wwjPanel.getWwd().addRenderingExceptionListener(new RenderingExceptionListener()
            {
                public void exceptionThrown(Throwable t)
                {
                    if (t instanceof WWAbsentRequirementException)
                    {
                        String message = "Computer does not meet minimum graphics requirements.\n";
                        message += "Please install up-to-date graphics driver and try again.\n";
                        message += "Reason: " + t.getMessage() + "\n";
                        message += "This program will end when you press OK.";

                        JOptionPane.showMessageDialog(AppFrame.this, message, "Unable to Start Program",
                            JOptionPane.ERROR_MESSAGE);
                        System.exit(-1);
                    }
                }
            });

            // Search the layer list for layers that are also select listeners and register them with the World
            // Window. This enables interactive layers to be included without specific knowledge of them here.
            for (Layer layer : this.wwjPanel.getWwd().getModel().getLayers())
            {
                if (layer instanceof SelectListener)
                {
                    this.getWwd().addSelectListener((SelectListener) layer);
                }
            }

            this.pack();

            // Center the application on the screen.
            WWUtil.alignComponent(null, this, AVKey.CENTER);
            this.setResizable(true);
        }

        protected AppPanel createAppPanel(Dimension canvasSize, boolean includeStatusBar)
        {
            return new AppPanel(canvasSize, includeStatusBar);
        }

        public Dimension getCanvasSize()
        {
            return canvasSize;
        }

        public AppPanel getWwjPanel()
        {
            return wwjPanel;
        }

        public WorldWindow getWwd()
        {
            return this.wwjPanel.getWwd();
        }

        public StatusBar getStatusBar()
        {
            return this.wwjPanel.getStatusBar();
        }

        /**
         * @deprecated Use getControlPanel instead.
         * @return This application's layer panel.
         */
        public LayerPanel getLayerPanel()
        {
            return this.layerPanel;
        }

        public JPanel getControlPanel()
        {
            return this.controlPanel;
        }

        public StatisticsPanel getStatsPanel()
        {
            return statsPanel;
        }

        public void setToolTipController(ToolTipController controller)
        {
            if (this.wwjPanel.toolTipController != null)
                this.wwjPanel.toolTipController.dispose();

            this.wwjPanel.toolTipController = controller;
        }

        public void setHighlightController(HighlightController controller)
        {
            if (this.wwjPanel.highlightController != null)
                this.wwjPanel.highlightController.dispose();

            this.wwjPanel.highlightController = controller;
        }
    }

    public static void insertBeforeCompass(WorldWindow wwd, Layer layer)
    {
        // Insert the layer into the layer list just before the compass.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof CompassLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }

    public static void insertBeforePlacenames(WorldWindow wwd, Layer layer)
    {
        // Insert the layer into the layer list just before the placenames.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof PlaceNameLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }

    public static void insertAfterPlacenames(WorldWindow wwd, Layer layer)
    {
        // Insert the layer into the layer list just after the placenames.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof PlaceNameLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition + 1, layer);
    }

    public static void insertBeforeLayerName(WorldWindow wwd, Layer layer, String targetName)
    {
        // Insert the layer into the layer list just before the target layer.
        int targetPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l.getName().indexOf(targetName) != -1)
            {
                targetPosition = layers.indexOf(l);
                break;
            }
        }
        layers.add(targetPosition, layer);
    }

    static
    {
        System.setProperty("java.net.useSystemProxies", "true");
        if (Configuration.isMacOS())
        {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "World Wind Application");
            System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
            System.setProperty("apple.awt.brushMetalLook", "true");
        }
        else if (Configuration.isWindowsOS())
        {
            System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing
        }
    }

    public static AppFrame start(String appName, Class appFrameClass)
    {
        if (Configuration.isMacOS() && appName != null)
        {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        }

        try
        {
            final AppFrame frame = (AppFrame) appFrameClass.newInstance();
            frame.setTitle(appName);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            java.awt.EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    frame.setVisible(true);
                }
            });

            return frame;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args)
    {
        // Call the static start method like this from the main method of your derived class.
        // Substitute your application's name for the first argument.
        ApplicationTemplate.start("World Wind Application", AppFrame.class);
    }
}
