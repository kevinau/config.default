package org.gyfor.data;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.gyfor.osgi.OSGi;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;


@Component(service=ImageManager.class, configurationPolicy=ConfigurationPolicy.OPTIONAL, immediate=true)
public class ImageManager {
  
  private DataLocation dataLocation;
  
  private ImageManagerConfiguration config = new ImageManagerConfiguration();
  
  
  @Reference(cardinality=ReferenceCardinality.MANDATORY)
  protected void setDataLocation(DataLocation dataLocation) {
    this.dataLocation = dataLocation;
  }
  
  
  protected void unsetDataLocation(DataLocation dataLocation) {
    this.dataLocation = null;
  }
  
  
  @Activate
  public void activate (Map<String, Object> props) {
    config = OSGi.getConfiguration2(props, ImageManagerConfiguration.class);
  }
  
  
  @Deactivate
  public void deactivate (Map<String, Object> props) {
  }
  
  
  /**
   * Convenience method that returns a scaled instance of the
   * provided {@code BufferedImage}.
   *
   * @param img the original image to be scaled
   * @param targetWidth the desired width of the scaled instance,
   *    in pixels
   * @param targetHeight the desired height of the scaled instance,
   *    in pixels
   * @param hint one of the rendering hints that corresponds to
   *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
   *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
   *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
   *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
   * @param higherQuality if true, this method will use a multi-step
   *    scaling technique that provides higher quality than the usual
   *    one-step technique (only useful in downscaling cases, where
   *    {@code targetWidth} or {@code targetHeight} is
   *    smaller than the original dimensions, and generally only when
   *    the {@code BILINEAR} hint is specified)
   * @return a scaled version of the original {@code BufferedImage}
   */
  public BufferedImage getScaledInstance(BufferedImage img,
                                         int targetWidth,
                                         int targetHeight,
                                         Object hint,
                                         boolean higherQuality) {
      int type = (img.getTransparency() == Transparency.OPAQUE) ?
          BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
      BufferedImage ret = (BufferedImage)img;
      int w, h;
      if (higherQuality) {
          // Use multi-step technique: start with original size, then
          // scale down in multiple passes with drawImage()
          // until the target size is reached
          w = img.getWidth();
          h = img.getHeight();
      } else {
          // Use one-step technique: scale directly from original
          // size to target size with a single drawImage() call
          w = targetWidth;
          h = targetHeight;
      }
      
      do {
          if (higherQuality && w > targetWidth) {
              w /= 2;
              if (w < targetWidth) {
                  w = targetWidth;
              }
          }

          if (higherQuality && h > targetHeight) {
              h /= 2;
              if (h < targetHeight) {
                  h = targetHeight;
              }
          }

          BufferedImage tmp = new BufferedImage(w, h, type);
          Graphics2D g2 = tmp.createGraphics();
          g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
          g2.drawImage(ret, 0, 0, w, h, null);
          g2.dispose();

          ret = tmp;
      } while (w != targetWidth || h != targetHeight);

      return ret;
  }
  
  
  private void buildPDFImage(File file, File imageFile, File thumbFile) throws IOException {
    // load a pdf from a byte buffer
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    FileChannel channel = raf.getChannel();
    ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
    PDFFile pdfFile = new PDFFile(buf);
    raf.close();

    // use the PDF&nbsp;Renderer library on the buf which contains the in memory
    // PDF document
    PDFPage page = pdfFile.getPage(1);

    // get the width and height for the doc at the default zoom
    double width = page.getBBox().getWidth();
    double height = page.getBBox().getHeight();
    Rectangle rect = new Rectangle(0, 0, (int)width, (int)height);
    
    int maxSize = 256;            // Length of longest side, in pixels
    int minSize = 181;
    int iwidth;
    int iheight;
    if (width > height) {
      // Landscape mode
      iwidth = maxSize;
      iheight = minSize;
    } else {
      // Portrait mode
      iheight = maxSize;
      iwidth = minSize;
    }
    int jwidth = iwidth * 8;
    int jheight = iheight * 8;
    
    // generate the image
    BufferedImage image = (BufferedImage)page.getImage(jwidth, jheight,
        rect, // clip rect
        null, // null for the ImageObserver
        true, // fill background with white
        true) // block until drawing is done
    ;
    imageFile.getParentFile().mkdirs();
    ImageIO.write(image, "png", imageFile);

    if (thumbFile != null) {
      // Scale the image and create a thumbnail of the original document
      buildThumb (imageFile, thumbFile);
    }
  }


  private void buildThumb (File imageFile, File thumbFile) throws IOException {
    BufferedImage image = ImageIO.read(imageFile);
    buildThumb(image, thumbFile);
  }
  
  
  private void buildThumb (BufferedImage image, File thumbFile) throws IOException {
//    // Get the image from the image file
//    logger.debug("Resize image file: {}", imageFile);
//    BufferedImage image = ImageIO.read(imageFile);

    // get the width and height for the doc at the default zoom
    int width = image.getWidth();
    int height = image.getHeight();
    
    int maxSize = 256;            // Length of longest side, in pixels
    int iwidth;
    int iheight;
    if (width > height) {
      // Landscape mode
      iwidth = maxSize;
      iheight = (height * iwidth) / width;
    } else {
      // Portrait mode
      iheight = maxSize;
      iwidth = (iheight * width) / height;
    }
    image = getScaledInstance(image, iwidth, iheight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
    
    thumbFile.getParentFile().mkdirs();
    ImageIO.write(image, "png", thumbFile);
  }

  
  private void extractODFThumb (File file, File imageFile) throws IOException {
    final ZipFile zipFile = new ZipFile(file);
    ZipEntry entry = zipFile.getEntry("Thumbnails/thumbnail.png");
    if (entry != null) {
      InputStream input = zipFile.getInputStream(entry);
      // Copy input stream to image file
      OutputStream output = new FileOutputStream(imageFile);
      byte[] buffer = new byte[4096];
      int n = input.read(buffer);
      while (n > 0) {
        output.write(buffer, 0, n);
        n = input.read(buffer);
      }
      input.close();
      output.close();
    } else {
      // Use a generic image
    }
    zipFile.close();
  }  
  
  
  private void copyPNG (File file, File imageFile, File thumbFile) throws IOException {
    InputStream input = new FileInputStream(file);
    OutputStream output = new FileOutputStream(imageFile);
    byte[] buffer = new byte[4096];
    int n = input.read(buffer);
    while (n > 0) {
      output.write(buffer, 0, n);
      n = input.read(buffer);
    }
    input.close();
    output.close();
    
    if (thumbFile != null) {
      buildThumb(imageFile, thumbFile);
    }
  }  
  
  
  private void buildImage (File sourceFile, String extn, File imageFile, File thumbFile) throws IOException {
    //File sourceFile = dataLocation.getDataBasedFile("{data}/source/original", fileName);
    switch (extn) {
    case ".pdf" :
      // Generate an image file from the source document
      buildPDFImage(sourceFile, imageFile, thumbFile);
      break;
    case ".odt" :
    case ".ods" :
    case ".odp" :
    case ".odg" :
    case ".odf" :
      extractODFThumb(sourceFile, thumbFile);
      break;
    case ".png" :
      copyPNG(sourceFile, imageFile, thumbFile);
      break;
    default :
      // Use a generic image
      break;
    }
  }

  
  public File getImageFile (File sourceFile) {
    String fileName = sourceFile.getName();
    String imageName;
    String extn;
    int n = fileName.lastIndexOf('.');
    if (n == -1) {
      imageName = fileName + ".png";
      extn = "";
    } else {
      imageName = fileName.substring(0, n) + ".png";
      extn = fileName.substring(n);
    }

    File imageFile = dataLocation.getDataBasedFile(config.getImageLocation(), imageName);
    if (!imageFile.exists()) {
      // Build only an image
      try {
        buildImage(sourceFile, extn, imageFile, null);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
    return imageFile;
  }
  
  
  public File getThumbFile (File sourceFile) {
    String fileName = sourceFile.getName();
    String imageName;
    String extn;
    int n = fileName.lastIndexOf('.');
    if (n == -1) {
      imageName = fileName + ".png";
      extn = "";
    } else {
      imageName = fileName.substring(0, n) + ".png";
      extn = fileName.substring(n);
    }

    File thumbFile = dataLocation.getDataBasedFile(config.getThumbLocation(), imageName);
    if (!thumbFile.exists()) {
      try {
        File imageFile = dataLocation.getDataBasedFile(config.getImageLocation(), imageName);
        if (!imageFile.exists()) {
          // Build the image, and from that, build a thumbnail image
          buildImage(sourceFile, extn, imageFile, thumbFile);
        } else {
          buildThumb(imageFile, thumbFile);
        }
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }  
    return thumbFile;
  }

}
