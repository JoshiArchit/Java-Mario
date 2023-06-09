package com.main.mario;
import com.main.mario.entity.Entity;
import com.main.mario.entity.Player;
import com.main.mario.gfx.Sprite;
import com.main.mario.gfx.SpriteSheet;
import com.main.mario.input.KeyInput;
import com.main.mario.tile.Wall;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Game extends Canvas implements Runnable{

    //===========================================//
    // Frame Variables
    //==========================================//
    public static final  int WIDTH = 270;
    public static final int HEIGHT = WIDTH/14*10;
    public static final int SCALE = 4;
    public static final String TITLE = "Super Mario";

    // Game loop threads
    private Thread thread;
    private boolean running = false;
    private BufferedImage image;

    public static Handler handler;
    public static Camera cam;

    //===========================================//
    // SPRITE OBJECTS
    //==========================================//
    public static SpriteSheet sheet;
    public static Sprite grass;
    public static Sprite[] player;
    public static Sprite mushroom;



    public Game() {
        Dimension size = new Dimension( WIDTH*SCALE, HEIGHT*SCALE );
        setPreferredSize( size );
        setMaximumSize( size );
        setMinimumSize( size );
    }


    // TODO: 6/23/2023 : ADD JFRAME Code
    private void jFrame() {
        // Move frame code here to reduce clutter in main()
    }

    private synchronized void start() {
        if(running) return;

        running = true;
        thread = new Thread(this, "Thread");
        thread.start();
    }

    private synchronized void stop() {
        if(!running) return;

        running = false;
        try {
            thread.join(); // Join with main thread and continue main()
        } catch ( InterruptedException e ) {
            throw new RuntimeException( e );
        }
    }

    // TODO: Research run() method
    @Override
    public void run() {
        try {
            init();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        requestFocus();

        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();

        double delta = 0.0;
        double ns = 1000000000.0/60.0;

        int frames = 0;
        int ticks = 0;

        while(running) {
            long now = System.nanoTime();
            delta += (now-lastTime)/ns;
            lastTime = now;
            while(delta >= 1) {
                tick();
                ticks++;
                delta--;
            }
            render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(frames + " fps " + ticks + " updates per second");
                frames = 0;
                ticks = 0;
            }
        }
        stop();
    }

    public void render() {
        //Create buffer strategy
        BufferStrategy bs = getBufferStrategy();
        if(bs == null) {
            createBufferStrategy( 3 );
            return;
        }

        Graphics g = bs.getDrawGraphics();
        // setColor(new Color(R,G,B))
        g.setColor( Color.BLACK);
        g.fillRect( 0, 0, getWidth(), getHeight() );
        g.translate( cam.getX(), cam.getY() ); // Check
        handler.render( g );
        g.dispose();
        bs.show();
    }


    public void tick() {
        handler.tick();

        for(Entity e: handler.entity) {
            if(e.getId() == Id.player) {
                cam.tick( e );
            }
        }
    }

    public static int getFrameWidth() {
        return WIDTH*SCALE;
    }

    public static int getFrameHeight() {
        return HEIGHT*SCALE;
    }

    private void init() throws IOException {
        handler = new Handler();
        sheet = new SpriteSheet( "/Spritesheet.png" );
        cam = new Camera();
        addKeyListener( new KeyInput() );

        //===========================================//
        // Load Sprites
        //==========================================//
        grass = new Sprite( sheet, 1, 1 );         // Grass Sprite
        player = new Sprite[5];                         // Player Sprite array
        mushroom = new Sprite(sheet, 1, 2);
        for(int i = 0; i < player.length; i++) {
            player[i] = new Sprite( sheet, i+1, 16 );
        }

        //===========================================//
        // Level Creation
        //==========================================//
        image = ImageIO.read( getClass().getResource( "/level.png" ) );
//        handler.addEntity( new Player( 200, 400, 64, 64, true, Id.player, handler ) );
//        handler.addTile( new Wall(200, 200, 64, 64, true, Id.wall, handler) );
        handler.createLevel( image );
    }

    public static void main( String[] args ) {
        Game game = new Game();

        // Move to frames method
        JFrame frame = new JFrame( TITLE );
        frame.add( game );
        frame.pack();
        frame.setResizable( false );
        frame.setLocationRelativeTo( null );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );

        game.start();
    }
}

// TODO: Organize code later into sections