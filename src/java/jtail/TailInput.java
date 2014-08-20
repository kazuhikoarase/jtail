package jtail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * TailInput
 * @author kazuhiko arase
 */
public class TailInput {

	public interface Consumer {
		void accept(byte[] bytes) throws IOException;
	}

	private File file;
	private long waitIntervalInMillis;
	private boolean alive;

	private long start;
	private ByteArrayOutputStream buf;

	public TailInput(File file, long waitIntervalInMillis) {
		this.file = file;
		this.waitIntervalInMillis = waitIntervalInMillis;
		this.alive = true;
	}

	protected void waitForInput() throws IOException {
		while (start == file.length() ) {
			try {
				Thread.sleep(waitIntervalInMillis);
			} catch(InterruptedException e) {
				throw new IOException(e);
			}
			if (!alive) {
				return;
			}
		}
	}

	public void read(Consumer c) throws IOException {

		start = file.length();
		buf = null;

		while (alive) {

			waitForInput();

			if (start > file.length() ) {
				// file changed.
				start = 0;
			}

			RandomAccessFile raf = new RandomAccessFile(file, "r");
			try {
				int b;
				raf.seek(start);
				while (alive && (b = raf.read() ) != -1) {
					start += 1;
					doByte(c, b);
				}
			} finally {
				raf.close();
			}
		}
	}

	protected void doByte(Consumer c, int b) throws IOException {
		if (buf == null) {
			buf = new ByteArrayOutputStream();
		}
		buf.write(b);
		if (b == '\n') {
			buf.close();
			doBytes(c, buf.toByteArray() );
			buf = null;
		}
	}

	protected void doBytes(Consumer c, byte[] bytes) throws IOException {
		c.accept(bytes);
	}

	public void shutdown() {
		alive = false;
	}
}
