import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * Produces the webcam-based drawing.

 * Created: 1/24/2020. Revised: 6/11/2021.
 * @author: Jonathan Lee
 * Indebted to a scaffold from CBK.
 */

public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		if (displayMode == 'w') {
			g.drawImage(image, 0, 0, null);
		}
		else if (displayMode == 'r') {
			g.drawImage(image, 0, 0, null);
		}
		else if (displayMode == 'p') {
			g.drawImage(painting, 0, 0, null);
		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		// if the webcam is on and working
		if (targetColor != null && painting != null) {
			finder.setImage(image);
			finder.findRegions(targetColor); // finds all the regions of the user's color choice

			// if the user is in recolored view
			if (displayMode == 'r') {
				finder.recolorImage();
				image = finder.getRecoloredImage();
			}

			// if the user is in painting view
			else if (displayMode == 'p') {
				// for each point in a given region
				if (finder.largestRegion() != null) {
					for (Point pixel : finder.largestRegion()) {
						painting.setRGB(pixel.x, pixel.y, paintColor.getRGB()); // recolors to the blue set color
					}
				}
			}
		}

	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (image != null) {
			targetColor = new Color(image.getRGB(x, y));
		}
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
