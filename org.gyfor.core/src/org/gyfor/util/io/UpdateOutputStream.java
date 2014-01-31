package org.gyfor.util.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * An output stream for updating data in a file. The file modification time of
 * the output file is only changed if the file is new or if the written file
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
 * This output stream is particularly useful for Ant and similar jobs that
 * depend on file modification times to indicate a change in file contents.
 * Using this output stream ensures that if there is no change in file contents,
 * the file modification time does not change.
 * <p>
 * UpdateOutputStream is meant for writing streams of raw bytes such as image
 * data. For writing streams of characters, consider using UpdateWriter.
 * 
 * @author Kevin.Holloway
 * 
 */
public class UpdateOutputStream extends OutputStream {

	private final RandomAccessFile accessor;
	private boolean isWriting;
	private long posn;

	/**
	 * Constructs a FileWriter object given a file name.
	 * 
	 * @param fileName
	 *            - String The system-dependent filename.
	 * @throws IOException
	 *             - if the named file exists but is a directory rather than a
	 *             regular file, does not exist but cannot be created, or cannot
	 *             be opened for any other reason
	 */
	public UpdateOutputStream(String fileName) throws IOException {
		this(new File(fileName));
	}

	/**
	 * Constructs a FileWriter object given a File object.
	 * 
	 * @param file
	 *            - a File object to write to.
	 * @throws IOException
	 *             - if the named file exists but is a directory rather than a
	 *             regular file, does not exist but cannot be created, or cannot
	 *             be opened for any other reason
	 */
	public UpdateOutputStream(File file) throws IOException {
		this.accessor = new RandomAccessFile(file, "rw");
		isWriting = false;
		posn = 0;
	}

	/**
	 * Closes the stream, flushing it first. Once the stream has been closed,
	 * further write() or flush() invocations will cause an IOException to be
	 * thrown. Closing a previously closed stream has no effect.
	 * <p>
	 * If the file previously existed, and if the new contents of the file are
	 * the same as previously, the modified time of the file is not changed from
	 * what it was previously.
	 * <p>
	 * If the file did not previously exist, or if the new contents of the file
	 * are different to what was there previously, the modified time of the file
	 * will be the time of the last write.
	 */
	@Override
	public void close() throws IOException {
		if (posn != accessor.getFilePointer()) {
			throw new RuntimeException("Inconsistsent state: " + posn + " "
					+ accessor.getFilePointer());
		}
		accessor.setLength(posn);
		accessor.close();
	}

	
	/**
	 * Flushes the stream. If the stream has saved any characters from the
	 * various write() methods in a buffer, write them immediately to their
	 * intended destination. Then, if that destination is another character or
	 * byte stream, flush it. Thus one flush() invocation will flush all the
	 * buffers in a chain of Writers and OutputStreams.
	 * <p>
	 * If the intended destination of this stream is an abstraction provided by
	 * the underlying operating system, for example a file, then flushing the
	 * stream guarantees only that bytes previously written to the stream are
	 * passed to the operating system for writing; it does not guarantee that
	 * they are actually written to a physical device such as a disk drive.
	 */
	@Override
	public void flush() throws IOException {
	}

	/**
	 * Writes a single character. The character to be written is contained in
	 * the 16 low-order bits of the given integer value; the 16 high-order bits
	 * are ignored.
	 * <p>
	 * Subclasses that intend to support efficient single-character output
	 * should override this method.
	 * 
	 * @param c
	 *            - int specifying a character to be written
	 * @throws IOException
	 *             - If an I/O error occurs
	 */
	@Override
	public void write(int c) throws IOException {
		if (!isWriting) {
			int c1 = accessor.read();
			if (c1 == c) {
				posn++;
				return;
			}
			accessor.seek(posn);
			isWriting = true;
		}
		accessor.write(c);
		posn++;
	}

	/**
	 * Writes an array of characters.
	 * 
	 * @param cbuf
	 *            - Array of characters to be written
	 * @throws IOException
	 *             - If an I/O error occurs
	 */
	@Override
	public void write(byte[] cbuf) throws IOException {
		write(cbuf, 0, cbuf.length);
	}

	/**
	 * Writes a portion of an array of characters.
	 * 
	 * @param cbuff
	 *            - Array of characters
	 * @param off
	 *            - Offset from which to start writing characters
	 * @param len
	 *            - Number of characters to write
	 * @throws IOException
	 *             - If an I/O error occurs
	 */
	@Override
	public void write(byte[] cbuff, int off, int len) throws IOException {
		if (!isWriting) {
			byte[] nbuff = new byte[len];
			int n = accessor.read(nbuff);
			if (n == len && contentEqual(cbuff, nbuff, off, len)) {
				// The newly written content is the same as the existing
				// content
				posn += len;
				return;
			}
			accessor.seek(posn);
			isWriting = true;
		}
		accessor.write(cbuff, off, len);
		posn += len;
	}

	private boolean contentEqual(byte[] cbuff, byte[] nbuff, int off, int len) {
		if (off == 0 && len == cbuff.length) {
			return Arrays.equals(cbuff, nbuff);
		} else {
			for (int i = 0; i < len; i++) {
				if (cbuff[off + i] != nbuff[i]) {
					return false;
				}
			}
			return true;
		}
	}
}
