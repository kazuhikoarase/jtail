package jtail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * TailInputStream
 * @author kazuhiko arase
 */
public class TailInputStream extends InputStream {

	private File file;
	private long waitIntervalInMillis;
	private boolean alive;
	private long start;
	private RandomAccessFile raf;
	
	public TailInputStream(File file) throws IOException {
		this(file, 100);
	}
	
	public TailInputStream(File file, long waitIntervalInMillis) throws IOException {
		this.file = file;
		this.waitIntervalInMillis = waitIntervalInMillis;
		alive = true;
		start = file.length();
		raf = new RandomAccessFile(file, "r");
		raf.seek(start);
	}

	@Override
	public int read() throws IOException {
		int b = raf.read();
		while (b == -1) {
			b = waitForInput();
			if (!alive) {
				return -1;
			}
		}
		start += 1;
		return b;
	}

	@Override
	public void close() throws IOException {
		raf.close();
	}
	
	protected int waitForInput() throws IOException {

		raf.close();

		try {
			do {
				Thread.sleep(waitIntervalInMillis);
				if (!alive) {
					return -1;
				}
			} while (start == file.length() );
		} catch(InterruptedException e) {
			throw new IOException(e);
		}

		if (start > file.length() ) {
			// file changed.
			start = 0;
		}

		raf = new RandomAccessFile(file, "r");
		raf.seek(start);
		return raf.read();
	}

	public void shutdown() {
		alive = false;
	}
}
