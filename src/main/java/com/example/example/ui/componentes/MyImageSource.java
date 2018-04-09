package com.example.example.ui.componentes;

import com.vaadin.server.StreamResource;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Getter
@Setter
public class MyImageSource implements StreamResource.StreamSource {
    private ByteArrayOutputStream imagebuffer = null;
    private BufferedImage image;
    int reloads = 0;

    public MyImageSource(BufferedImage image) {
        this.image = image;
    }

    public InputStream getStream () {
        try {
            imagebuffer = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imagebuffer);
            return new ByteArrayInputStream(
                    imagebuffer.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }
}