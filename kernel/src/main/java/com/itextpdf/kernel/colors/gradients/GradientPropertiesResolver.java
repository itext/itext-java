package com.itextpdf.kernel.colors.gradients;

/**
 * This resolver is used during the layout process to prevent infinite loops.
 */
public class GradientPropertiesResolver {

    /**
     * Default max color stops.
     */
    public static final int DEFAULT_MAX_COLOR_STOPS = 10_000;

    private final int maxColorStops;

    /**
     * Creates default instance of {@link GradientPropertiesResolver}.
     */
    public GradientPropertiesResolver() {
        maxColorStops = DEFAULT_MAX_COLOR_STOPS;
    }

    /**
     * Creates {@link GradientPropertiesResolver} instance.
     *
     * <p>
     * This resolver is used for gradient creation.
     *
     * @param maxColorStops max color stops for repeat and reflect
     *                      (see {@link GradientPropertiesResolver#getMaxColorStops()}
     */
    public GradientPropertiesResolver(int maxColorStops) {
        this.maxColorStops = maxColorStops;
    }

    /**
     * Gets maximum color stops for repeat and reflect spreading.
     * <p>
     * This property defines the maximum amount of color stops to be created for
     * repeat and reflect spreading of colors in gradients.
     *
     * @return maximum color stops for repeat and reflect
     */
    public int getMaxColorStops() {
        return maxColorStops;
    }
}
