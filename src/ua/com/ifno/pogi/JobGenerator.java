package ua.com.ifno.pogi;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;

public class JobGenerator extends Thread {

    private final Scaler scaler;
    private final int TILE_SIZE;
    private final Rectangle viewport;
    private final ImageSettings is;
    private BufferedImage bi = null;

    public JobGenerator(Rectangle viewport, BufferedImage bi,
                        ImageSettings imageSettings, Scaler scaler) {
        TILE_SIZE = imageSettings.getTileSize();
        this.scaler = scaler;
        this.viewport = viewport;
        this.is = imageSettings;

        this.bi = bi;
    }

    private static final ArrayBlockingQueue<TileInfo> jobList = new ArrayBlockingQueue<TileInfo>(
            1000);

    public static ArrayBlockingQueue<TileInfo> getJobList() {
        return jobList;
    }

    @Override
    public void run() {
        try {
            jobList.clear();
            int fx, lx; // first&last tile index
            int fy, ly; // first&last tile index

            double tmp = viewport.getX() / TILE_SIZE;
            if (tmp < 0) {
                fx = (int) Math.ceil(tmp);
            } else {
                fx = (int) Math.floor(tmp);
            }

            tmp = viewport.getY() / TILE_SIZE;
            if (tmp < 0) {
                fy = (int) Math.ceil(tmp);
            } else {
                fy = (int) Math.floor(tmp);
            }

            tmp = viewport.getMaxX() / TILE_SIZE;
            if (tmp < 0) {
                lx = (int) Math.ceil(tmp);
            } else {
                lx = (int) Math.floor(tmp);
            }

            tmp = viewport.getMaxY() / TILE_SIZE;
            if (tmp < 0) {
                ly = (int) Math.ceil(tmp);
            } else {
                ly = (int) Math.floor(tmp);
            }

            for (int i = fx - 1; i < lx + 1; i++) {
                for (int j = fy - 1; j < ly + 1; j++) {
                    String fileUrl;
                    fileUrl = String.format(
                            is.getHost() + is.getTilesPath() + "/%d"
                                    + is.getTileName(), scaler.getPointer(), i,
                            j);
                    URL url = new URL(fileUrl);
                    TileInfo ti = new TileInfo(url, i, j);
                    jobList.add(ti);
                }
            }
            Thread.yield();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            RasterThread a = new RasterThread(viewport, bi, is);
            a.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
