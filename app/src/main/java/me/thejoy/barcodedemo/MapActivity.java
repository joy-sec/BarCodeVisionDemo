package me.thejoy.barcodedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.Utils;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private static final String MAP_FILE = "canary_islands.map";

    private MapView mapView;
    private Marker m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        AndroidGraphicFactory.createInstance(getApplication());
        //mapView = new MapView(this);
        mapView=findViewById(R.id.mapView);
        //setContentView(mapView);

        try {

            mapView.setClickable(true);
            mapView.getMapScaleBar().setVisible(true);
            mapView.setBuiltInZoomControls(true);

            TileCache tileCache = AndroidUtil.createTileCache(this, "mapcache",
                    mapView.getModel().displayModel.getTileSize(), 1f,
                    mapView.getModel().frameBufferModel.getOverdrawFactor());

            File mapFile = new File(Environment.getExternalStorageDirectory(), MAP_FILE);
            MapDataStore mapDataStore = new MapFile(mapFile);
            TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                    mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE) {

                @Override
                public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
                    Toast.makeText(MapActivity.this,String.valueOf(tapLatLong),Toast.LENGTH_LONG).show();

                    Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.m));
                    bitmap.incrementRefCount();
                    Marker marker = new Marker(new LatLong(tapLatLong.latitude, tapLatLong.longitude), bitmap, 0, -bitmap.getHeight() / 2) {
                        @Override public boolean onTap(LatLong geoPoint, Point viewPosition, Point tapPoint) {
                            if (contains(viewPosition, tapPoint)) {
                                Toast.makeText(MapActivity.this, "Tapped", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            return false;
                        }
                    };
//                    if (m!=null){
//                        mapView.getLayerManager().getLayers().remove(m);
//                        m=marker;
//                    }else {
//                        m=marker;
//                    }
                    mapView.getLayerManager().getLayers().add(marker);
                    return true;
                }
            };
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);

            mapView.getLayerManager().getLayers().add(tileRendererLayer);

            mapView.setCenter(new LatLong(28.2336984, -16.6086312));
            mapView.setZoomLevel((byte) 12);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
    }
}
