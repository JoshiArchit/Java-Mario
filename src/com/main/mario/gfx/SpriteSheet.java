package com.main.mario.gfx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteSheet {

    private BufferedImage sheet;

    public SpriteSheet(String path) throws IOException {
        sheet = ImageIO.read(getClass().getResource( path ));
    }

    public BufferedImage getSprite(int x, int y) {
        return sheet.getSubimage( x*32-32, y*32-32, 32, 32 );
    }
}
