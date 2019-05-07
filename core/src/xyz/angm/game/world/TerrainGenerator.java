package xyz.angm.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.util.Random;

import static xyz.angm.game.ui.Screen.VIEWPORT_HEIGHT;
import static xyz.angm.game.ui.Screen.VIEWPORT_WIDTH;

/** Generates random terrain using a noise function. */
public class TerrainGenerator {

    private static final Color GRASS_COLOR = new Color(0x2C8324FF);
    private static final Color STONE_COLOR = new Color(0x8B8B86FF);
    private static final Color WATER_COLOR = new Color(0x5DA6EFFF);
    private static final double STONE_CHANCE = 0.3f;
    private static final double WATER_CHANCE = 0.2f;

    /** Multiplicator for the world map. Takes the viewport size as base. */
    public static final int WORLD_SIZE_MULTIPLICATOR = 3;

    private final SimplexNoiseGenerator noiseGenerator;

    /** Create a terrain generator. Seed is used for randomness; same seed = same world
     * @param seed Seed to be used */
    TerrainGenerator(long seed) {
        noiseGenerator = new SimplexNoiseGenerator(seed);
    }

    /** Creates a Texture displaying the ground, using the proper terrain.
     * @return A Texture constructed using a Pixmap.*/
    Texture createWorldMapTexture() {
        Pixmap map = new Pixmap(WORLD_SIZE_MULTIPLICATOR * VIEWPORT_WIDTH, WORLD_SIZE_MULTIPLICATOR * VIEWPORT_HEIGHT, Pixmap.Format.RGB888);
        map.setColor(GRASS_COLOR);
        map.fill(); // Turn all pixels into grass at first to improve draw performance by reducing calls to JNI

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                double noise = noiseGenerator.generateDot(x, y);

                if (noise < WATER_CHANCE) map.setColor(WATER_COLOR);
                else if (noise < (STONE_CHANCE + WATER_CHANCE)) map.setColor(STONE_COLOR);
                else continue; // Grass was already drawn by the fill() call at the top; this draw can be skipped
                map.drawPixel(x, y);
            }
        }

        return new Texture(map);
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

        SimplexNoiseGenerator(long seed) {
            Random random = new Random(seed);
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
