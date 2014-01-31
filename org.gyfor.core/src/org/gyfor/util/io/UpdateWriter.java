package org.gyfor.util.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * An writer for updating data in a file. The file modification time of the
 * output file is only changed if the file is new or if the written file
 * contents is different from what was previously there.
 * <ul>
 * <li>If no file is initially present, a new file is created. The file
 * modification time will be the time of last write to the file.</li>
 * <li>If the file is already present and the data written is different to what
 * was previously there. If the file is already in the file, the file
 * modification date will be updated. The file modification time will be the
 * time of last write to the file.</li>
 * <li>If the file is already present and the data written is the same as what
 * was previously in the file, the file modification date will not change.</li>
 * </ul>
 * This writer is particularly useful for Ant and similar jobs that depend on
 * file modification times to indicate a change in file contents. Using this
 * writer ensures that if there is no change in file contents, the file
 * modification time does not change.
 * <p>
 * UpdateWriter is meant for writing streams of characters. For writing streams
 * of raw bytes (such as image data), consider using UpdateOutputStream.
 * 
 * @author Kevin.Holloway
 * 
 */
public class UpdateWriter extends PrintWriter {

	public UpdateWriter(String fileName) throws IOException {
		this(new File(fileName));
	}

  public UpdateWriter(File file) throws IOException {
    this(new UpdateOutputStream(file));
  }
  
  private UpdateWriter(UpdateOutputStream stream) throws IOException {
    super(stream);
  }

}
