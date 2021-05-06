package xyz.angm.game.world;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import xyz.angm.game.Defactory;
import xyz.angm.game.ui.screens.Screen;

import java.util.Random;

/** Generates random terrain using a noise function. */
public class TerrainGenerator {

    /** Chance for stone to generate. */
    private static final double STONE_CHANCE = 0.02f;
    /** Chance for stoneTile to generate. */
    private static final double STONE_TILE_CHANCE = 0.30f;
    /** Chance for enhanced-grass-direction to generate. */
    private static final double GRASS_CHANCE = 0.20f;
    /** The amount of lines to be rendered per continueLoading call. */
    private static final int LINES_PER_STEP = 30;
    /** Multiplicator for the world map. Takes the viewport size as base. */
    public static final int WORLD_SIZE_MULTIPLICATOR = 3;

    /** Seed used for generation. */
    final long seed;
    private final SimplexNoiseGenerator noiseGenerator;
    private final Random random;
    private final Pixmap[] grass = new Pixmap[4];
    private final Pixmap stone;
    private final Pixmap stoneTile;
    private final Pixmap map = new Pixmap(
            WORLD_SIZE_MULTIPLICATOR * Screen.getViewportWidth(), WORLD_SIZE_MULTIPLICATOR * Screen.getViewportHeight(), Pixmap.Format.RGB888);
    private int index = 0;
    private int grassDirection = 1;

    /** Create a terrain generator. Seed is used for randomness; same seed = same world
     * @param seed Seed to be used */
    public TerrainGenerator(long seed) {
        this.seed = seed;
        random = new Random(seed);
        noiseGenerator = new SimplexNoiseGenerator();

        TextureData[] grassTD = new TextureData[4];
        for (int i = 0; i < grassTD.length; i++) {
            grassTD[i] = Defactory.assets.get("textures/map/grass" + i + ".png", Texture.class).getTextureData();
            grassTD[i].prepare();
            grass[i] = grassTD[i].consumePixmap();
        }

        TextureData stoneTD = Defactory.assets.get("textures/map/stone.png", Texture.class).getTextureData();
        TextureData stoneTileTD = Defactory.assets.get("textures/map/stoneTile.png", Texture.class).getTextureData();
        stoneTD.prepare();
        stoneTileTD.prepare();
        stone = stoneTD.consumePixmap();
        stoneTile = stoneTileTD.consumePixmap();
    }

    /** Creates a Texture displaying the ground, using the proper terrain.
     * @return A Texture constructed using the created pixmap. */
    Texture getTexture() {
        return new Texture(map);
    }

    /** Continue loading. Call every frame.
     * @return Loading progress. */
    public float continueLoading() {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = index; y < (index + LINES_PER_STEP); y++) {
                double noise = noiseGenerator.generateDot(x, y);

                Pixmap copyFrom;
                if (noise < STONE_CHANCE) copyFrom = stone;
                else if (noise < (STONE_TILE_CHANCE + STONE_CHANCE)) copyFrom = stoneTile;
                else if (noise > (1 - GRASS_CHANCE)) {
                    copyFrom = grass[grassDirection];
                    grassDirection++;
                    if (grassDirection > 3) grassDirection = 0;
                } else copyFrom = grass[random.nextInt(3)];

                map.setColor(copyFrom.getPixel((x % copyFrom.getWidth()), (y % copyFrom.getWidth())));
                map.drawPixel(x, y);
            }
        }
        index += LINES_PER_STEP;
        return (float) index / map.getHeight();
    }

    private class SimplexNoiseGenerator {

        // These values heavily influence terrain/noise generation.
        private static final int OCTAVES = 3;
        private static final double ROUGHNESS = 0.1;
        private static final double SCALE = 0.001;

        private final int[][] grad3 = {{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1},
                {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1},
                {0, 1, -1}, {0, -1, -1}};
        private final int[] perm = new int[512];

        SimplexNoiseGenerator() {
            int[] p = new int[256];
            for (int i = 0; i < p.length; i++) p[i] = random.nextInt(255);
            for (int i = 0; i < perm.length; i++) perm[i] = p[i & 255];
        }

        /** Calculate the terrain to be used at the specified coordinates.
         * @return The terrain to be used; double in range 0.0-1.0 */
        double generateDot(int x, int y) {
            double noiseTotal = 0.0;
            double layerScale = SCALE;
            double layerWeight = 1.0;

            for (int o = 0; o < OCTAVES; o++) {
                // Calculate single layer/octave of simplex noise, then add it to total noise
                noiseTotal += noise(x * layerScale, y * layerScale) * layerWeight;

                // Increase variables with each incrementing octave
                layerScale *= 2.0;
                layerWeight *= ROUGHNESS;
            }

            // noiseTotal is in range -1.0 < noiseTotal < 1.0; has to be corrected to be in 0.0 < noiseTotal < 1.0 range
            noiseTotal += 1.0;
            noiseTotal /= 2.0;

            return noiseTotal;
        }

        /*
            Everything below in this class is adapted and compressed from Stefan Gustavsons paper on simplex noise
            See his paper for a commented version: http://staffwww.itn.liu.se/~stegu/simplexnoise/simplexnoise.pdf
        */

        private int fastFloor(double x) {
            return x > 0 ? (int) x : (int) x - 1;
        }

        private double dot(int[] g, double x, double y) {
            return g[0] * x + g[1] * y;
        }

        private double noise(double xin, double yin) {
            double n0;
            double n1;
            double n2;

            final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
            double s = (xin + yin) * F2;
            int i = fastFloor(xin + s);
            int j = fastFloor(yin + s);
            final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
            double t = (i + j) * G2;
            double x0 = xin - (i - t);
            double y0 = yin - (j - t);
            int i1;
            int j1;
            if (x0 > y0) {
                i1 = 1;
                j1 = 0;
            } else {
                i1 = 0;
                j1 = 1;
            }
            double x1 = x0 - i1 + G2;
            double y1 = y0 - j1 + G2;
            double x2 = x0 - 1.0 + 2.0 * G2;
            double y2 = y0 - 1.0 + 2.0 * G2;
            int ii = i & 255;
            int jj = j & 255;
            int gi0 = perm[ii + perm[jj]] % 12;
            int gi1 = perm[ii + i1 + perm[jj + j1]] % 12;
            int gi2 = perm[ii + 1 + perm[jj + 1]] % 12;
            double t0 = 0.5 - x0 * x0 - y0 * y0;
            if (t0 < 0) n0 = 0.0;
            else {
                t0 *= t0;
                n0 = t0 * t0 * dot(grad3[gi0], x0, y0);
            }
            double t1 = 0.5 - x1 * x1 - y1 * y1;
            if (t1 < 0) n1 = 0.0;
            else {
                t1 *= t1;
                n1 = t1 * t1 * dot(grad3[gi1], x1, y1);
            }
            double t2 = 0.5 - x2 * x2 - y2 * y2;
            if (t2 < 0) n2 = 0.0;
            else {
                t2 *= t2;
                n2 = t2 * t2 * dot(grad3[gi2], x2, y2);
            }
            return 70.0 * (n0 + n1 + n2);
        }
    }
}
