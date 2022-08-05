package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Vector;
import java.util.Random;

/**
 * The Engine to run game.
 * @author Jason Liu
 */
public class Engine {
    /** Use ter to render GUI. */
    private TERenderer ter = new TERenderer();
    /** Use random to generate world. */
    private Random random;
    /** Game windows width. */
    public static final int WIDTH = 80;
    /** Game windows height. */
    public static final int HEIGHT = 30;


    /**
     * Represent a rectangle area.
     */
    private class RectArea {
        /** The left bottom coordinate. */
        private int orgX, orgY;
        /** Area height and width. */
        private int height, width;

        /**
         * Constructor.
         * @param x orgX
         * @param y orgY
         * @param w width
         * @param h height
         */
        RectArea(int x, int y, int w, int h) {
            orgX = x;
            orgY = y;
            width = w;
            height = h;
        }

        /** Get orgX.
         * @return orgX
         * */
        public int getOrgX() {
            return orgX;
        }

        /** Get orgY.
         * @return orgY
         * */
        public int getOrgY() {
            return orgY;
        }

        /** Get height.
         * @return height
         * */
        public int getHeight() {
            return height;
        }

        /** Get width.
         * @return width
         * */
        public int getWidth() {
            return width;
        }
    }

    /**
     * Method used for exploring a fresh world.
     * This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        InputSource inputSource = new KeyboardInputSource();
    }

    /**
     * Method used for autograding and testing your code. The input
     * string will be a series of characters (for example,
     * "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the
     * engine using interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite
     * save. For example, if we do interactWithInputString("n123sss:q"),
     * we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        InputSource inputSource = new StringInputDevice(input);
        TETile[][] finalWorldFrame = null;

        while (inputSource.possibleNextInput()) {
            char nextKey = inputSource.getNextKey();
            nextKey = Character.toUpperCase(nextKey);

            if (nextKey == 'N') {
                String seed = "";
                while (true) {
                    nextKey = Character.toUpperCase(inputSource.getNextKey());
                    if (nextKey == 'S') {
                        break;
                    }
                    seed += nextKey;
                }
                finalWorldFrame = createNewWorld(Long.parseLong(seed));
            }
        }
        return finalWorldFrame;
    }

    /**
     * Fill rect with tile.
     * @param world the world need to build
     * @param rect the area
     * @param tile tile used to fill
     */
    private void fillWithTiles(TETile[][] world, RectArea rect, TETile tile) {
        for (int i = 0; i < rect.getWidth(); i++) {
            for (int j = 0; j < rect.getHeight(); j++) {
                int x = rect.getOrgX() + i, y = rect.getOrgY() + j;
                world[x][y] = tile;
            }
        }
    }

    /**
     * Fill rect with tile. If a tile is non-empty, do nothing.
     * @param world the world need to build
     * @param rect the area
     * @param tile tile used to fill
     */
    private void fillEmptyWithTiles(TETile[][] world,
                                    RectArea rect, TETile tile) {
        for (int i = 0; i < rect.getWidth(); i++) {
            for (int j = 0; j < rect.getHeight(); j++) {
                int x = rect.getOrgX() + i, y = rect.getOrgY() + j;
                if (world[x][y] == null) {
                    world[x][y] = tile;
                }
            }
        }
    }

    /**
     * Add a random room in given area.
     * @param world the world need to build
     * @param rect the area range
     */
    private void makeRandomRoom(TETile[][] world, RectArea rect) {
        int width = rect.getWidth() - 2, height = rect.getHeight() - 2;
        int x = rect.getOrgX() + RandomUtils.uniform(random,
                (width + 1) / 2) + 1;
        int y = rect.getOrgY() + RandomUtils.uniform(random,
                (height + 1) / 2) + 1;

        width = rect.getWidth() - (x - rect.getOrgX()) - 1;
        height = rect.getHeight() - (y - rect.getOrgY()) - 1;
        width = RandomUtils.uniform(random, (width + 1) / 2)
                + width / 2 + 1;
        height = RandomUtils.uniform(random, (height + 1) / 2)
                + height / 2 + 1;

        RectArea room = new RectArea(x, y, width, height);
        fillWithTiles(world, room, Tileset.FLOOR);
    }

    /**
     * The number of tiles in rect.
     * @param world the world need to build
     * @param rect the area range
     * @return number of tiles
     */
    private int tilesNumber(TETile[][] world, RectArea rect) {
        int number = 0;
        for (int i = 0; i < rect.getWidth(); i++) {
            for (int j = 0; j < rect.getHeight(); j++) {
                int x = rect.getOrgX() + i, y = rect.getOrgY() + j;
                if (world[x][y] != null) {
                    number++;
                }
            }
        }
        return number;
    }

    /**
     * Return the id-th non-empty tile's coordinate.
     * @param world the world need to build
     * @param rect the area range
     * @param id id
     * @return int[2] - (x, y)
     */
    private int[] getIdThTilePos(TETile[][] world, RectArea rect, int id) {
        for (int i = 0; i < rect.getWidth(); i++) {
            for (int j = 0; j < rect.getHeight(); j++) {
                int x = rect.getOrgX() + i, y = rect.getOrgY() + j;
                if (world[x][y] != null) {
                    id--;
                    if (id == 0) {
                        return new int[] {x, y};
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check if (x, y) is a legal coordinate.
     * @param x coordinate x
     * @param y coordinate y
     * @return return true if (x, y) is legal else return false
     */
    private boolean legalCoordinate(int x, int y) {
        return 0 <= x && x < WIDTH && 0 <= y && y < HEIGHT;
    }

    /**
     * Check if (x, y) is a legal coordinate that could put Floor in.
     * @param x coordinate x
     * @param y coordinate y
     * @return return true if (x, y) is legal else return false
     */
    private boolean legalFloorCoordinate(int x, int y) {
        return 1 <= x && x < WIDTH - 1 && 1 <= y && y < HEIGHT - 1;
    }

    /**
     * Connect two tiles with hallway.
     * The width of hallwall is w. w is 1 or 2.
     * If the width is 2, it will try to make the hallway's width be 2.
     * @param world the world need to build
     * @param posA tile A position
     * @param posB tile B position
     * @param w the width of hallway.
     */
    private void connectTwoTiles(TETile[][] world, int[] posA, int[] posB,
                                 int w) {
        if (posA[0] > posB[0]) {
            int[] swapTemp = posB;
            posB = posA;
            posA = swapTemp;
        }
        for (int x = posA[0]; x <= posB[0]; x++) {
            int y = posA[1];
            world[x][y] = Tileset.FLOOR;
            if (w == 2 && legalFloorCoordinate(x, y + 1)) {
                world[x][y + 1] = Tileset.FLOOR;
            }
        }

        int x = posB[0];
        if (posA[1] > posB[1]) {
            int[] swapTemp = posB;
            posB = posA;
            posA = swapTemp;
        }
        for (int y = posA[1]; y <= posB[1]; y++) {
            world[x][y] = Tileset.FLOOR;
            if (w == 2 && legalFloorCoordinate(x + 1, y)) {
                world[x + 1][y] = Tileset.FLOOR;
            }
        }
    }

    /**
     * Connect two area with hallway, if there is null area, do nothing.
     * @param world the world need to build
     * @param areaA area A
     * @param areaB area B
     */
    private void connectArea(TETile[][] world, RectArea areaA, RectArea areaB) {
        int numA = tilesNumber(world, areaA);
        int numB = tilesNumber(world, areaB);
        if (numA == 0 || numB == 0) {
            return;
        }

        int idA = RandomUtils.uniform(random, numA) + 1;
        int idB = RandomUtils.uniform(random, numB) + 1;
        int[] posA = getIdThTilePos(world, areaA, idA);
        int[] posB = getIdThTilePos(world, areaB, idB);

        final double widthProbability = 0.3;
        int width = RandomUtils.bernoulli(random, widthProbability) ? 2 : 1;
        connectTwoTiles(world, posA, posB, width);
    }

    /**
     * Cut the area vertically, and generate world Separately.
     * Make sure the width is larger or equal than 6.
     * @param world The world needed to build
     * @param rect The area range
     */
    private void verticalCut(TETile[][] world, RectArea rect) {
        int range = rect.getWidth() - 5;
        int leftWidth = 3 + RandomUtils.uniform(random, range);

        RectArea left = new RectArea(rect.getOrgX(), rect.getOrgY(),
                leftWidth, rect.getHeight());
        RectArea right = new RectArea(rect.getOrgX() + leftWidth,
                rect.getOrgY(), rect.getWidth() - leftWidth,
                rect.getHeight());
        generateStructure(world, left);
        generateStructure(world, right);
        connectArea(world, left, right);
    }

    /**
     * Cut the area crossly, and generate world Separately.
     * Make sure the height is larger or equal than 6.
     * @param world The world needed to build
     * @param rect The area range
     */
    private void crossCut(TETile[][] world, RectArea rect) {
        int range = rect.getHeight() - 5;
        int bottomHeight = 3 + RandomUtils.uniform(random, range);

        RectArea top = new RectArea(rect.getOrgX(), rect.getOrgY(),
                rect.getWidth(), bottomHeight);
        RectArea bottom = new RectArea(rect.getOrgX(),
                rect.getOrgY() + bottomHeight,
                rect.getWidth(), rect.getHeight() - bottomHeight);
        generateStructure(world, top);
        generateStructure(world, bottom);
        connectArea(world, top, bottom);
    }

    /**
     * Build the basic structure of the world with only floor.
     * @param world the world need to build
     * @param rect the area range
     */
    private void generateStructure(TETile[][] world, RectArea rect) {
        double rate = 1.0 * (rect.getHeight() + rect.getWidth())
                / (HEIGHT + WIDTH);
        if (rect.getHeight() <= HEIGHT / 2 && rect.getWidth() <= WIDTH / 2) {
            if (RandomUtils.uniform(random) <= 1.0 - rate) {
                makeRandomRoom(world, rect);
                return;
            }
        }

        final double verticalCutRate = 1.0 * WIDTH / (WIDTH + HEIGHT) - 0.1;
        if (rect.getWidth() >= 6) {
            if (RandomUtils.uniform(random) <= verticalCutRate) {
                verticalCut(world, rect);
                return;
            }
        }

        final double crossCutRate = (1.0 * HEIGHT / (WIDTH + HEIGHT) - 0.1)
                / (1 - verticalCutRate);
        if (rect.getHeight() >= 6) {
            if (RandomUtils.uniform(random) <= crossCutRate) {
                crossCut(world, rect);
                return;
            }
        }

        if (rect.getHeight() > HEIGHT / 2 || rect.getWidth() > WIDTH / 2) {
            if (rect.getHeight() > HEIGHT / 2 && rect.getWidth() <= WIDTH / 2) {
                crossCut(world, rect);
            } else if (rect.getHeight() <= HEIGHT / 2
                    && rect.getWidth() > WIDTH / 2) {
                verticalCut(world, rect);
            } else {
                boolean opt = RandomUtils.bernoulli(random);
                if (opt) {
                    verticalCut(world, rect);
                } else {
                    crossCut(world, rect);
                }
            }
        }
    }

    /**
     * Wrap floor with wall.
     * @param world the world need to build
     */
    private void wrapWithWall(TETile[][] world) {
        int[] dx = new int[] {0, 0, 1, -1, 1, 1, -1, -1};
        int[] dy = new int[] {1, -1, 0, 0, 1, -1, 1, -1};
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[i][j] != null) {
                    continue;
                }
                boolean flag = false;
                for (int d = 0; d < dx.length; d++) {
                    int x = i + dx[d], y = j + dy[d];
                    if (legalCoordinate(x, y)) {
                        if (world[x][y] == Tileset.FLOOR) {
                            flag = true;
                        }
                    }
                }
                if (flag) {
                    world[i][j] = Tileset.WALL;
                }
            }
        }
    }

    /**
     * Add a locked door.
     * @param world the world need to build
     */
    void addLockedDoor(TETile[][] world) {
        int[] dx = new int[] {0, 0, 1, -1};
        int[] dy = new int[] {1, -1, 0, 0};
        Vector<int[]> pos = new Vector<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[i][j] != Tileset.WALL) {
                    continue;
                }
                boolean flagFloor = false, flagBlank = false;
                for (int d = 0; d < dx.length; d++) {
                    int x = i + dx[d], y = j + dy[d];
                    if (legalCoordinate(x, y)) {
                        if (world[x][y] == Tileset.FLOOR) {
                            flagFloor = true;
                        }
                        if (world[x][y] == null) {
                            flagBlank = true;
                        }
                    }
                }
                if (flagFloor && flagBlank) {
                    pos.add(new int[]{i, j});
                }
            }
        }
        int id = RandomUtils.uniform(random, pos.size());
        int[] p = pos.elementAt(id);
        world[p[0]][p[1]] = Tileset.LOCKED_DOOR;
    }

    /**
     * Create a new world with seed.
     * @param seed The random seed
     * @return random world
     */
    private TETile[][] createNewWorld(long seed) {
        random = new Random(seed);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        generateStructure(world, new RectArea(0, 0, WIDTH, HEIGHT));
        wrapWithWall(world);
        addLockedDoor(world);
        fillEmptyWithTiles(world, new RectArea(0, 0, WIDTH, HEIGHT),
                Tileset.NOTHING);
        return world;
    }

}
