package byow.Core;
//import org.junit.Test;
//import static org.junit.Assert.*;

import byow.InputDemo.InputSource;
import byow.InputDemo.StringInputDevice;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import jh61b.junit.In;
import java.lang.reflect.Array;
import java.util.concurrent.ThreadLocalRandom;

import java.util.ArrayList;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class RoomWorld{

    private static final int WIDTH = 80;
    private static final int HEIGHT = 50;

    private static final int ROOM_MAX_SIZE = 8;
    private static final int ROOM_MIN_SIZE = 6;
    private static final int MAX_ROOMS = 30;
    //private static final String SEED = Engine.getSeed(input);
    private static Random random;

    private static final int STRING = 2;

    /**
     * Takes in a seed, initializes the world, and returns the 2D TETile[][]
     * array representation of the world.
     * @param seed
     * @return
     */
    public static TETile[][] worldCreator(int seed){
        random = new Random(seed);
        //TERenderer ter = new TERenderer();
        //ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        initializeTiles(world);
        createWorld(world);
        addwalls(world);
        return world;
    }

    public static void main(String[] args) {
        int inputType = STRING;
        InputSource inputSource;
        if (inputType == STRING) {
            //inputSource = new StringInputDevice();
        }
        int seed = 7;
        random = new Random(seed);
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        initializeTiles(world);
        createWorld(world);
        addwalls(world);
        ter.renderFrame(world);
    }

    /**
     * Creates a rectangular room with the given coordinates and dimensions and
     * assigns the floor tiles a kind.
     * @param xPos
     * @param xDim
     * @param yPos
     * @param yDim
     * @param world
     * @param kind
     */
    public static void createRoom(int xPos, int xDim, int yPos, int yDim, TETile[][] world, TETile kind) {
        for (int x = xPos + 1; x < xDim; x++) {
            for (int y = yPos + 1; y < yDim; y++) {
                world[x][y] = kind;
            }
        }
    }

    /**
     * Creates a hallway with an intersection that begins as either horizontal or vertical.
     * Randomizes the width of the hallway: between 1 or 2.
     * Hallways always connect two rooms at their center coordinates.
     * @param dir
     * @param width
     * @param prevRoom_x
     * @param newRoom_x
     * @param prevRoom_y
     * @param newRoom_y
     * @param world
     */
    public static void createHallway(String dir, int width, int prevRoom_x, int newRoom_x, int prevRoom_y, int newRoom_y, TETile[][] world) {
        if (dir.equals("vertical")) {
            for (int y = Math.min(prevRoom_y, newRoom_y); y < Math.max(prevRoom_y, newRoom_y) +1; y++) {
                world[prevRoom_x][y] = Tileset.GRASS;
                if (width == 2) {
                    world[prevRoom_x + 1][y] = Tileset.GRASS;
                }
            }
            for (int x = Math.min(prevRoom_x, newRoom_x); x < Math.max(prevRoom_x, newRoom_x) +1; x++) {
                world[x][newRoom_y] = Tileset.GRASS;
                if (width == 2) {
                    world[x][newRoom_y + 1] = Tileset.GRASS;
                }
            }
        } else if (dir.equals("horizontal")) {
            for (int x = Math.min(prevRoom_x, newRoom_x); x < Math.max(prevRoom_x, newRoom_x) +1; x++) {
                world[x][newRoom_y] = Tileset.GRASS;
                if (width == 2) {
                    world[x][newRoom_y + 1] = Tileset.GRASS;
                }
            }
            for (int y = Math.min(prevRoom_y, newRoom_y); y < Math.max(prevRoom_y, newRoom_y) +1; y++) {
                world[prevRoom_x][y] = Tileset.GRASS;
                if (width == 2) {
                    world[prevRoom_x + 1][y] = Tileset.GRASS;
                }
            }
        }
    }

    /**
     * Takes in an arraylist of dimensions of the new room and an arraylist of arraylists of dimensions
     * of previously created rooms, and checks to make sure that none intersect with the new room
     * being created.
     * @param currDims
     * @param dimensions
     * @return
     */
    public static boolean doesItIntersect(ArrayList<Integer> currDims, ArrayList<ArrayList> dimensions) {
        for (ArrayList<Integer> dimension: dimensions) {
            if (oneRoomIntersects(currDims, dimension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if one room intersects with another singular room.
     * @source http://www.roguebasin.com/index.php?title=Complete_Roguelike_Tutorial,_using_python%2Blibtcod
     * @param dim1
     * @param dim2
     * @return
     */
    public static boolean oneRoomIntersects(ArrayList<Integer> dim1, ArrayList<Integer> dim2) {
        int myX1 = dim1.get(0);
        int myX2 = dim1.get(1);
        int myY1 = dim1.get(2);
        int myY2 = dim1.get(3);

        int otherX1 = dim2.get(0);
        int otherX2 = dim2.get(1);
        int otherY1 = dim2.get(2);
        int otherY2 = dim2.get(3);

        return myX1 <= otherX2 && myX2 >= otherX1 &&
                myY1 <= otherY2 && myY2 >= otherY1;
    }

    /**
     * Calculates the center coordinates of a room given its dimensions.
     * @param dimensions
     * @return
     */
    public static ArrayList<Integer> calculateCenterCoords(ArrayList<Integer> dimensions) {
        ArrayList<Integer> center = new ArrayList<>();
        int x1 = dimensions.get(0);
        int x2 = dimensions.get(1);
        int y1 = dimensions.get(2);
        int y2 = dimensions.get(3);

        int centerX = (x1 + x2) / 2;
        center.add(centerX);

        int centerY = (y1 + y2) / 2;
        center.add(centerY);

        return center;
    }

    /**
     * Creates the world by randomly generating rooms and hallways.
     * @source http://www.roguebasin.com/index.php?title=Complete_Roguelike_Tutorial,_using_python%2Blibtcod
     * @param world
     */
    public static void createWorld(TETile[][] world) {
        ArrayList<ArrayList> dimensions = new ArrayList<>();
        int num_rooms = 0;
        for (int r = 0; r < MAX_ROOMS; r++) {
            int w_offset = RandomUtils.uniform(random, ROOM_MIN_SIZE, ROOM_MAX_SIZE + 1);
            int h_offset = RandomUtils.uniform(random, ROOM_MIN_SIZE, ROOM_MAX_SIZE + 1);
            int x = RandomUtils.uniform(random, WIDTH - w_offset); //xpos
            int y = RandomUtils.uniform(random, HEIGHT - h_offset); //ypos
            int w = x + w_offset; //xdim
            int h = y + h_offset; //ydim

            ArrayList<Integer> roomDims = new ArrayList<>();
            roomDims.add(x);
            roomDims.add(w);
            roomDims.add(y);
            roomDims.add(h);

            if (!doesItIntersect(roomDims, dimensions)) {
                //randomize tile type
                int randomNum = random.nextInt(10);
                if (randomNum < 3) {
                    createRoom(x, w, y, h, world, Tileset.FLOWER);
                } else if (randomNum < 6) {
                    createRoom(x, w, y, h, world, Tileset.MOUNTAIN);
                } else {
                    createRoom(x, w, y, h, world, Tileset.WATER);
                }
                ArrayList<Integer> newRoomCenter = calculateCenterCoords(roomDims);
                int newRoom_x = newRoomCenter.get(0);
                int newRoom_y = newRoomCenter.get(1);
                if (num_rooms == 0) {
                    //TODO: put the player in this first room
                } else {
                    //make the hallways based on the x, y coordinates of the center of PREVIOUSLY MADE ROOM
                    //ensures all rooms are connected, hallways generated between rooms in order of room generation
                    ArrayList<Integer> previousRoomCenter = calculateCenterCoords(dimensions.get(num_rooms - 1));
                    int prevRoom_x = previousRoomCenter.get(0);
                    int prevRoom_y = previousRoomCenter.get(1);
                    //randomizes what kind of hallway and implements turning/intersections
                    if (RandomUtils.uniform(random,10)<5){ //ThreadLocalRandom.current().nextInt(0, 10) < 5) {
                        String dir = "horizontal";
                        int width;
                        int randNum = RandomUtils.uniform(random, 10);
                        if (randNum > 5) {
                            width = 2;
                        } else {
                            width = 1;
                        }
                        createHallway(dir, width, prevRoom_x, newRoom_x, prevRoom_y, newRoom_y, world);
                    } else {
                        String dir = "vertical";
                        int width;
                        int randNum = RandomUtils.uniform(random, 10);
                        if (randNum < 5) {
                            width = 2;
                        } else {
                            width = 1;
                        }
                        createHallway(dir, width, prevRoom_x, newRoom_x, prevRoom_y, newRoom_y, world);
                    }
                }
                dimensions.add(roomDims);
                num_rooms += 1;
            }
        }
    }

    /**
     * Initializes the empty world before we create it.
     * @param world
     */
    public static void initializeTiles(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }


    /**
     * Loops through every tile on the board and uses neighborIsFloor method
     * to check if a wall needs to be placed on the board in the current (i,j) position
     * @param world
     */
    public static void addwalls(TETile[][] world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[i][j].equals(Tileset.NOTHING) && neighborIsFloor(i,j,world)){
                    world[i][j] = Tileset.WALL;
                }
            }
        }
    }

    /**
     * Checks to see if the neighbor is a floor or terrain in this case. It's a very ugly
     * method, dont bother reading it just trust that it probably works there are a lot
     * of edge cases in order to not go out of bounds on the world map.
     * @param x is the current x coordinate
     * @param y is the current y coordinate
     * @param world the map
     * @returns a boolean depending on the terrain surrounding the current tile
     */
    public static boolean neighborIsFloor(int x, int y, TETile[][] world) {
        if (x == 0){
            if (y == 0){
                return containsTerrain(world[x+1][y+1]);
            } else if (y == HEIGHT-1) {
                return containsTerrain(world[x+1][y-1]);
            } else {
                return containsTerrain(world[x + 1][y]) ||
                        containsTerrain(world[x + 1][y + 1]) ||
                        containsTerrain(world[x + 1][y - 1]);
            }
        } else if (y == 0){
            if (x == WIDTH - 1){
                return containsTerrain(world[x-1][y+1]);
            }
            return containsTerrain(world[x][y + 1]) ||
                    containsTerrain(world[x - 1 ][y + 1]) ||
                    containsTerrain(world[x + 1][y + 1]);
        } else if (y == HEIGHT - 1) {
            if (x == WIDTH - 1) {
                return containsTerrain(world[x - 1][y - 1]);
            }
            return containsTerrain(world[x][y - 1])||
                    containsTerrain(world[x-1][y-1])||
                    containsTerrain(world[x+1][y-1]);
        } else if (x == WIDTH-1) {
            return containsTerrain(world[x - 1][y])||
                    containsTerrain(world[x - 1][y + 1]) ||
                    containsTerrain(world[x - 1 ][y - 1]);
        }

        return containsTerrain(world[x-1][y-1]) ||
                containsTerrain(world[x][y-1]) ||
                containsTerrain(world[x+1][y-1]) ||
                containsTerrain(world[x-1][y]) ||
                containsTerrain(world[x+1][y]) ||
                containsTerrain(world[x-1][y+1]) ||
                containsTerrain(world[x][y+1]) ||
                containsTerrain(world[x+1][y+1]);

    }

    /**
     * Will probably be updated later, but this helps by checking if tile a
     * equals any of the following terrains. Used as a helper in the neighbor is floor method
     * @param a a tile
     * @return boolean depending on if tile a is a grass flower mountain or water(subject to change)
     */
    public static boolean containsTerrain(TETile a){
        return  Tileset.GRASS.equals(a)||
                Tileset.FLOWER.equals(a) ||
                Tileset.MOUNTAIN.equals(a)||
                Tileset.WATER.equals(a);

    }
}

