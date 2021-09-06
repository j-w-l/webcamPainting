import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Core problem is identifying the "paintbrush" in the webcam image. This program does that via region growing.

 * Created: 1/24/2020. Revised: 6/11/2021.
 * @author: Jonathan Lee
 * Indebted to a scaffold from CBK.
 *
 * DEPENDENCIES: JavaCV: a Java wrapper of OpenCV. https://github.com/bytedeco/javacv
 */

public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar color must be to target to belong to region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		regions = new ArrayList<ArrayList<Point>>();

		// Loop over all the pixels.
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				// If a pixel is unvisited and of the right color:
				// NOTE: "right color" is relative to the colorMatch threshold.
				if (visited.getRGB(x, y) == 0 && colorMatch(new Color(image.getRGB(x, y)), new Color(targetColor.getRGB()))) {

					// Start a new region. Track which pixels need to be visited (initially just that one)
					ArrayList<Point> R = new ArrayList<>();
					ArrayList<Point> toVisit = new ArrayList<>();
					toVisit.add(new Point(x, y));

					// As long as there's some pixel that needs to be visited
					while (!toVisit.isEmpty()) {
						Point pixel = toVisit.remove(toVisit.size()-1);
						R.add(pixel);
						int coorX = (int)pixel.getX(), coorY = (int)pixel.getY();
						visited.setRGB(coorX, coorY, 1);

						// Loop over all of its neighbors: these coordinates are of the PIXEL.
						for (int nx = Math.max(0, coorX-1); nx < Math.min(image.getWidth(), coorX+2); nx++) {
							for (int ny = Math.max(0, coorY-1); ny < Math.min(image.getHeight(), coorY+2); ny++) {

								// If the NEIGHBOR is of the correct color (and is unvisited)
								if (visited.getRGB(nx, ny) == 0 && colorMatch(new Color(image.getRGB(nx, ny)), new Color(targetColor.getRGB()))) { // or
									// Add it to the list of pixels to be visited
									toVisit.add(new Point(nx, ny));
								}
							}
						}
					}
					// If the region is big enough to be worth keeping, do so
					if (R.size() >= minRegion) {
						regions.add(R);
					}
				}
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough."
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		return Math.abs(c1.getRed() - c2.getRed()) <= maxColorDiff &&
				Math.abs(c1.getBlue() - c2.getBlue()) <= maxColorDiff &&
				Math.abs(c1.getGreen() - c2.getGreen()) <= maxColorDiff;
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		if (regions == null || regions.size() < 1) {
			return null;
		}

		ArrayList<Point> best = regions.get(0);

		for (ArrayList<Point> reg : regions) {
			if (reg.size() > best.size()) {
				best = reg;
			}
		}

		return best;
	}

	/**
	 * Sets recoloredImage to be a copy of image, but with each region a uniform random color.
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null),
				image.getColorModel().isAlphaPremultiplied(), null);

		// Now recolor the regions in it
		for (ArrayList<Point> reg : regions) {
			Color regionColor = new Color( (int)(16777216*Math.random()) ); // uniform random color per region
			for (Point pixel : reg) {
				recoloredImage.setRGB((int)pixel.getX(), (int)pixel.getY(), regionColor.getRGB());
			}
		}
	}
}
