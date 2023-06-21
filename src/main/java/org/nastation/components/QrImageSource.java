package org.nastation.components;

import org.nastation.common.util.QRCodeUtil;

import javax.imageio.ImageIO;
import javax.xml.transform.stream.StreamSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class QrImageSource extends StreamSource {

    ByteArrayOutputStream byteArrayOutputStream = null;

    private String text;
    private int width;
    private int height;

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }

    public void setByteArrayOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public InputStream getStream() {
        try {
            BufferedImage image = QRCodeUtil.toBufferedImage(text, width, height);

            // Write the image to a buffer
            byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);

            // Return a stream from the buffer
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        } catch (Exception e) {
            return null;
        }

    }
}