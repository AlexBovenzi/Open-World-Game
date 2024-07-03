package byow.Core;
import byow.TileEngine.TETile;

/**
 * @author alexbovenzi
 */
public class TrySeed1234 {
    public static void main(String[] args) {
        Engine engine = new Engine();
        TETile[][] world = engine.interactWithInputString("N1234S");
        System.out.println(TETile.toString(world));
    }
}
