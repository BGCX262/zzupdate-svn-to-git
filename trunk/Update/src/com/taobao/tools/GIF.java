package com.taobao.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class GIF {

	private GC shellGC;

	private Color shellBackground;

	private ImageLoader loader;

	private ImageData[] imageDataArray;

	private Thread animateThread;

	private Image image;

	private final boolean useGIFBackground = false;

	public void gif(Control parent, final Shell shell, String fileName) {
		
		
		shellGC = new GC(parent);
		shellBackground = parent.getBackground();

		String path = Property.java_path + "/image/" + fileName;
		if (fileName.substring(0, 1).equals("-")) {
			path = Property.java_path + "/command/" + fileName.substring(1);
		}
		if (path != null) {
			loader = new ImageLoader();
			try {
				imageDataArray = loader.load(path);
				if (imageDataArray.length > 1) {
					animateThread = new Thread(fileName) {
						public void run() {
							Image offScreenImage = new Image(shell.getDisplay(), loader.logicalScreenWidth, loader.logicalScreenHeight);
							GC offScreenImageGC = new GC(offScreenImage);
							offScreenImageGC.setBackground(shellBackground);
							offScreenImageGC.fillRectangle(0, 0, loader.logicalScreenWidth, loader.logicalScreenHeight);

							try {
								int imageDataIndex = 0;
								ImageData imageData = imageDataArray[imageDataIndex];
								if (image != null && !image.isDisposed()) {
									image.dispose();
								}
								image = new Image(shell.getDisplay(), imageData);
								offScreenImageGC.drawImage(image, 0, 0, imageData.width, imageData.height, imageData.x, imageData.y, imageData.width, imageData.height);

								int repeatCount = loader.repeatCount;
								while (loader.repeatCount == 0 || repeatCount > 0) {
									switch (imageData.disposalMethod) {
									case SWT.DM_FILL_BACKGROUND:
										Color bgColor = null;
										if (useGIFBackground) {
											bgColor = new Color(shell.getDisplay(), imageData.palette.getRGB(loader.backgroundPixel));
										}
										offScreenImageGC.setBackground(bgColor != null ? bgColor : shellBackground);
										offScreenImageGC.fillRectangle(imageData.x, imageData.y, imageData.width, imageData.height);
										if (bgColor != null)
											bgColor.dispose();
										break;
									case SWT.DM_FILL_PREVIOUS:
										/*
										 * Restore the previous image before
										 * drawing.
										 */
										offScreenImageGC.drawImage(image, 0, 0, imageData.width, imageData.height, imageData.x, imageData.y, imageData.width, imageData.height);
										break;
									}

									imageDataIndex = (imageDataIndex + 1) % imageDataArray.length;
									imageData = imageDataArray[imageDataIndex];
									image.dispose();
									image = new Image(shell.getDisplay(), imageData);
									offScreenImageGC.drawImage(image, 0, 0, imageData.width, imageData.height, imageData.x, imageData.y, imageData.width, imageData.height);

									/* Draw the off-screen image to the shell. */
									shellGC.drawImage(offScreenImage, 0, 0);

									/*
									 * Sleep for the specified delay time
									 * (adding commonly-used slow-down fudge
									 * factors).
									 */
									try {
										int ms = imageData.delayTime * 10;
										if (ms < 20)
											ms += 30;
										if (ms < 30)
											ms += 10;
										Thread.sleep(ms);
									} catch (InterruptedException e) {
									}

									/*
									 * If we have just drawn the last image,
									 * decrement the repeat count and start
									 * again.
									 */
									if (imageDataIndex == imageDataArray.length - 1)
										repeatCount--;
								}
							} catch (SWTException ex) {
								// ex.printStackTrace();
							} finally {
								if (offScreenImage != null && !offScreenImage.isDisposed())
									offScreenImage.dispose();
								if (offScreenImageGC != null && !offScreenImageGC.isDisposed())
									offScreenImageGC.dispose();
								if (image != null && !image.isDisposed())
									image.dispose();
							}
						}
					};
					animateThread.setDaemon(true);
					animateThread.start();
				} else {
				    Image bg = new Image(null, path);
                    if (path.contains("degree")) {
                        int w = bg.getImageData().width;
                        int h = bg.getImageData().height;
                        GridData data = (GridData) parent.getLayoutData();
                        data.heightHint = h;
                        data.widthHint = w;
                        parent.getParent().layout(true);
                    }
                    parent.setBackgroundImage(bg);
				}
			} catch (SWTException ex) {
			}
		}
	}
}
