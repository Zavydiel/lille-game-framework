package gameframework.drawing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * This implementation of {@link SpriteManager} assumes that sprite types are in
 * rows whereas increments of a type are in columns
 * 
 */
public class SpriteManagerDefaultImpl implements SpriteManager {

	private final DrawableImage image;
	private Map<String, Integer> types;
	private final int spriteSize;
	private int spriteNumber = 0;
	private final int maxSpriteNumber;
	private int currentRow;
	private final int renderingSize;

	public SpriteManagerDefaultImpl(DrawableImage image, int renderingSize,
			int maxSpriteNumber) {
		this.renderingSize = renderingSize;
		this.image = image;
		this.maxSpriteNumber = maxSpriteNumber;
		this.spriteSize = image.getWidth() / maxSpriteNumber;
	}

	@Override
	public void setTypes(String... types) {
		int i = 0;
		this.types = new HashMap<String, Integer>(types.length);
		for (String type : types) {
			this.types.put(type, i);
			i++;
		}
	}

	@Override
	public void draw(Graphics g, Point position) {
		// Destination image coordinates
		int dx1 = (int) position.getX();
		int dy1 = (int) position.getY();
		int dx2 = dx1 + renderingSize;
		int dy2 = dy1 + renderingSize;

		// Source image coordinates
		int sx1 = spriteNumber * spriteSize;
		int sy1 = currentRow * spriteSize;
		int sx2 = sx1 + spriteSize;
		int sy2 = sy1 + spriteSize;
		g.drawImage(image.getImage(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
				null);
	}

	@Override
	public void setType(String type) {
		if (!types.containsKey(type)) {
			throw new IllegalArgumentException(type
					+ " is not a valid type for this sprite manager.");
		}
		this.currentRow = types.get(type);
	}

	@Override
	public void increment() {
		spriteNumber = (spriteNumber + 1) % maxSpriteNumber;
	}

	@Override
	public void reset() {
		spriteNumber = 0;
	}

	@Override
	public void setIncrement(int increment) {
		this.spriteNumber = increment;
	}
	
	@Override
	public void horizontalFlip(){
		AffineTransform affineTransform = AffineTransform.getScaleInstance(-1, 1);
		BufferedImage bufferedImage = toBufferedImage(image.getImage());
		affineTransform.translate(-bufferedImage.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		bufferedImage = op.filter(bufferedImage, null);
		image.setImage(bufferedImage);
	}
	
	@Override
	public void verticalFlip(){
		AffineTransform affineTransform = AffineTransform.getScaleInstance(1, -1);
		BufferedImage bufferedImage = toBufferedImage(image.getImage());
		affineTransform.translate(0, -bufferedImage.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		bufferedImage = op.filter(bufferedImage, null);
		image.setImage(bufferedImage);
	}
	
	protected BufferedImage toBufferedImage(Image image){
	    if (image instanceof BufferedImage){
	        return (BufferedImage) image;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D graphics = bufferedImage.createGraphics();
	    graphics.drawImage(image, 0, 0, null);
	    graphics.dispose();

	    return bufferedImage;
	}
}
