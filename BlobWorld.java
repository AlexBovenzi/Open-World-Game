package byow.lab12;

import byow.Core.Engine;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class BlobWorld {    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 28731;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     *
     * @param tiles
     */
    public static void fillWithblank(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Picks a RANDOM tile with a 33% change of being
     * a wall, 33% chance of being a flower, and 33%
     * chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0:
                return Tileset.WALL;
            case 1:
                return Tileset.FLOWER;
            case 2:
                return Tileset.NOTHING;
            default:
                return Tileset.NOTHING;
        }
    }
    public static class Position{
        int xPos, yPos;
        Position(int x, int y){
            x = xPos;
            y = yPos;
        }

    }
    private static boolean addtile(int x, int y, TETile[][] world){
        if (x > 0 && x < WIDTH-1 && y > 0 && y < HEIGHT-1){// && !world[x][y].equals(Tileset.WALL)) {
            world[x][y] = Tileset.WALL;
            return true;
        }
        return false;
    }


    private static void snake(int steps, int startx, int starty, TETile[][] world) {
        int x = startx;
        int y = starty;
        int lefts, ups, rights, downs;
        lefts = ups = rights = downs = 0;
        for (int i = 0; i < steps; i++) {
            int tileNum = RANDOM.nextInt(5);
            switch (tileNum) {
                case 0:
                    if(addtile(x - 1, y, world)) {
                        x -= 1;
                        lefts++;
                        break;
                    }
                case 1:
                    if(addtile(x + 1, y, world)) {
                        x += 1;
                        rights+=1;
                        break;
                    }
                case 2:
                    if(addtile(x, y - 1, world)){
                        y-= 1;
                        downs++;
                        break;
                    }
                case 3:
                    if(addtile(x, y + 1, world)){
                        y += 1;
                        ups++;
                        break;
                    }
                case 4:
                    //addRoomOrHall(x, y,RANDOM.nextInt(7),RANDOM.nextInt(7), world);
            }
        }
    }
    private static void snake1(int steps, int startx, int starty, TETile[][] world) {
        int x = startx;
        int y = starty;
        for (int i = 0; i < steps; i++) {
            int tileNum = RANDOM.nextInt(7);
            int randomHeight = RANDOM.nextInt(15);
            int randomWidth = RANDOM.nextInt(15);
            switch (tileNum) {
                case 0:
                    for (int q = 0; q < randomWidth; q++) {
                        if(addtile(x - 1, y, world)) {
                            x -= 1;
                        }
                    }
                    break;
                case 1:
                    for (int q = 0; q < randomWidth; q++) {
                        if(addtile(x + 1, y, world)) {
                            x += 1;
                        }
                    }
                    break;
                case 2:
                    for (int q = 0; q < randomHeight; q++) {
                        if(addtile(x, y - 1, world)) {
                            y -= 1;
                        }
                    }
                    break;
                case 3:
                    for (int q = 0; q < randomHeight; q++) {
                        if(addtile(x, y + 1, world)) {
                            y += 1;
                        }
                    }
                    break;
                case 4:
                case 5:
                case 6:
                    addRoomOrHall(x, y,RANDOM.nextInt(8),RANDOM.nextInt(8), world);

            }
        }
    }
    public static boolean addRoomOrHallP(int x, int y , int width, int height, TETile[][] world){
        if (x + width >= WIDTH || y + height >= HEIGHT) {
            return false;
        }
        for (int a = 0; a < width; a++) {
            for (int b = 0; b < height; b++) {
                //world[x + a][y + b] = Tileset.FLOOR;
                addtile(x + a, y + b, world);
            }
        }
        return true;
    }
    public static void addRoomOrHall(int x, int y, int width, int height, TETile[][] world){
        for (int a = 0; a < width; a++) {
            for (int b = 0; b < height; b++) {
                //world[x + a][y + b] = Tileset.FLOOR;
                addtile(x + a, y + b, world);
            }
        }
    }
    public static void addwalls(TETile[][] world) {
        for (int i = 0; i<WIDTH; i++) {
            for (int j = 0; j<HEIGHT; j++) {
                if (world[i][j].equals(Tileset.NOTHING) && neighborIsFloor(i,j,world)){
                    world[i][j] = Tileset.FLOWER;
                }
            }
        }
    }

    public static boolean neighborIsFloor(int x, int y, TETile[][] world) {
        if (x == 0){
            if (y == 0){
                return world[x+1][y+1].equals(Tileset.WALL);
            } else if (y == HEIGHT-1) {
                return world[x+1][y-1].equals(Tileset.WALL);
            }
            return world[x + 1][y].equals(Tileset.WALL)||
                    world[x + 1][y + 1].equals(Tileset.WALL) ||
                    world[x + 1][y - 1].equals(Tileset.WALL);
        } else if (y == 0){
            if (x == WIDTH - 1){
                return world[x-1][y+1].equals(Tileset.WALL);
            }
            return world[x][y + 1].equals(Tileset.WALL) ||
                    world[x - 1 ][y + 1].equals(Tileset.WALL) ||
                    world[x + 1][y + 1].equals(Tileset.WALL);
        } else if (y == HEIGHT - 1) {
            if (x == WIDTH - 1) {
                return world[x - 1][y - 1].equals(Tileset.WALL) ||
                        world[x][y - 1].equals(Tileset.WALL) ||
                        world[x + 1 ][y - 1].equals(Tileset.WALL);
            }
            return world[x][y - 1].equals(Tileset.WALL);
        } else if (x == WIDTH-1) {
            return world[x - 1][y].equals(Tileset.WALL)||
                    world[x - 1][y + 1].equals(Tileset.WALL) ||
                    world[x - 1 ][y - 1].equals(Tileset.WALL);
        }
        return world[x-1][y-1].equals(Tileset.WALL) ||
                world[x][y-1].equals(Tileset.WALL) ||
                world[x+1][y-1].equals(Tileset.WALL) ||
                world[x-1][y].equals(Tileset.WALL) ||
                world[x+1][y].equals(Tileset.WALL) ||
                world[x-1][y+1].equals(Tileset.WALL) ||
                world[x][y+1].equals(Tileset.WALL) ||
                world[x+1][y+1].equals(Tileset.WALL);
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];

        fillWithblank(world);
        //Position p = new Position(WIDTH/2,HEIGHT/2 );
        //snake(2500, RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT), world);
        snake1(300,RANDOM.nextInt(WIDTH-1), RANDOM.nextInt(HEIGHT-1), world);
        addwalls(world);
        ter.renderFrame(world);
    }


}
