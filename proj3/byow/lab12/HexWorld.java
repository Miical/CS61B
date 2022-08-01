package byow.lab12;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 27;
    private static final int HEIGHT = 30;

    private static final long SEED = 74193;
    private static final Random RANDOM = new Random(SEED);

    private static TETile makeNewTile(int tileType) {
        switch (tileType) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.TREE;
            case 3: return Tileset.GRASS;
            case 4: return Tileset.MOUNTAIN;
            case 5: return Tileset.SAND;
            default: return Tileset.NOTHING;
        }
    }

    private static void fillEmptyTilesWithNothing(TETile tiles[][]) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] == null) {
                    tiles[i][j] = Tileset.NOTHING;
                }
            }
        }
    }

    /**
     * Add a tiles in the given position.
     * @param orgX The lower left corner x coordinate.
     * @param orgY The lower left corner y coordinate.
     * @param x Relative x coordinate
     * @param y Relative y coordinate.
     */
    private static void addSingleTileInRelativePos (TETile[][] tiles, int orgX, int orgY,
                                             int x, int y, int tileType) {
        if (orgX + x < 0 || WIDTH <= orgX + x) {
            return;
        }
        if (orgY + y < 0 || HEIGHT <= orgY + y) {
            return;
        }
        tiles[orgX + x][orgY + y] = makeNewTile(tileType);
    }

    /**
     * Create a new hexagon whose lower left corner coordinate is (x, y) .
     */
    private static void addHexagon(TETile[][] tiles,int x, int y,
                            int size, int tileType) {
        // Draw the bottom half.
        for (int i = 0; i < size; i++) {
            int spacesNumber = size - i - 1;
            int tilesNumber = size + i * 2;
            for (int j = 0; j < tilesNumber; j++) {
                addSingleTileInRelativePos(tiles, x, y,
                        j + spacesNumber, i, tileType);
            }
        }

        // Draw the top half.
        for (int i = size - 1; i >= 0; i--) {
            int spacesNumber = size - i - 1;
            int tilesNumber = size + i * 2;
            for (int j = 0; j < tilesNumber; j++) {
                addSingleTileInRelativePos(tiles, x, y + size,
                        j + spacesNumber, size - i - 1, tileType);
            }
        }
    }

    private static void makeHexagonWorld(TETile tiles[][]) {
        int yDelta = 3, xDelta = 5;

        /* Draw the left half. */
        for (int i = 0; i < 3; i++) {
            int hexagonNumber = i + 3;
            int leftSpacesNumber = xDelta * i;
            for (int j = 0; j < hexagonNumber; j++) {
                int bottomSpacesNumber = yDelta * (2 - i + 2 * j);
                int tileType = RANDOM.nextInt(6);
                if (tileType < 0) tileType = -tileType;
                addHexagon(tiles, leftSpacesNumber, bottomSpacesNumber, 3,
                        tileType);
            }
        }

        /* Draw the right half. */
        for (int i = 0; i < 2; i++) {
            int hexagonNumber = i + 3;
            int leftSpacesNumber = xDelta * (4 - i);
            for (int j = 0; j < hexagonNumber; j++) {
                int bottomSpacesNumber = yDelta * (2 - i + 2 * j);
                int tileType = RANDOM.nextInt(6);
                if (tileType < 0) tileType = -tileType;
                addHexagon(tiles, leftSpacesNumber, bottomSpacesNumber, 3,
                        tileType);
            }
        }

    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        makeHexagonWorld(tiles);

        fillEmptyTilesWithNothing(tiles);
        ter.renderFrame(tiles);
    }

}
