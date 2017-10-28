package com.zrlog.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Rendering;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ThumbnailUtil {

    public static byte[] jpeg(byte[] buf, float quality) throws IOException {
        ByteArrayInputStream bain = new ByteArrayInputStream(buf);
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        Thumbnails.of(bain).outputQuality(quality).rendering(Rendering.SPEED).width(660).toOutputStream(baout);
        return baout.toByteArray();
    }
}
