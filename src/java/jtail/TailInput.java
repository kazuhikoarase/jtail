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

	public TailInput(File file, long waitIntervalInMillis) {
		this.file = file;
		this.waitIntervalInMillis = waitIntervalInMillis;
		this.alive = true;
	}

	public void read(Consumer c) throws IOException {

		long start = file.length();

		while (alive) {

			// wait for input
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

			if (start > file.length() ) {
				// file changed.
				start = 0;
			}

			RandomAccessFile raf = new RandomAccessFile(file, "r");
			try {

				ByteArrayOutputStream buf = null;
				int b;

				raf.seek(start);

				while (alive && (b = raf.read() ) != -1) {
					start += 1;
					if (buf == null) {
						buf = new ByteArrayOutputStream();
					}
					buf.write(b);
					if (b == '\n') {
						buf.close();
						c.accept(buf.toByteArray() );
						buf = null;
					}
				}
			} finally {
				raf.close();
			}
		}
	}

	public void shutdown() {
		alive = false;
	}
}
