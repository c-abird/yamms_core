package uni.hamburg.yamms.io;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import uni.hamburg.yamms.math.RealScalarField;
import uni.hamburg.yamms.math.Topology;

/**
 * Service for image file related tasks
 * 
 * @author Claas Abert
 * 
 */
public class ImageService {
	/**
	 * Reads the red channel of an image and converts it to a
	 * <code>RealScalarField</code>, whereas a black pixel will result in 0 as a
	 * value and a white pixel in <code>ms</code>. The values in between are
	 * interpolated.
	 * 
	 * @param path
	 *            the path to the image
	 * @param topology
	 *            the topology of the scalar field
	 * @param ms
	 *            the saturation value
	 * @return the scalar field
	 * @throws RuntimeException
	 */
	static public RealScalarField loadScalarField(String path,
			Topology topology, double ms) throws RuntimeException {

		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(IOConfig.getInstance().getPathFor(path)));
		} catch (IOException e) {
			// TODO print stack trace
		}
		int w = img.getWidth();
		int h = img.getHeight();

		if (topology.getCellCount(0) != w)
			throw new RuntimeException("Image doesn't fit Topology.");
		if (topology.getCellCount(1) != h)
			throw new RuntimeException("Image doesn't fit Topology.");
		if (topology.getCellCount(2) != 1)
			throw new RuntimeException("Image doesn't fit Topology.");

		int[] rgbs = new int[w * h];
		img.getRGB(0, 0, w, h, rgbs, 0, w);

		double[] values = new double[w * h];
		for (int i = 0; i < w * h; i++) {
			Color p = new Color(rgbs[i]);
			values[i] = ms * (p.getRed() / 255.0);
		}

		return new RealScalarField(topology, values);
	}

}
