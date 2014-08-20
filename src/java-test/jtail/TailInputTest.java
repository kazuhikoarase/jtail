package jtail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

public class TailInputTest {

	@Test
	public void test1() throws Exception {

		final File file = File.createTempFile("test", ".log", new File(".") );
		file.deleteOnExit();
		
		Thread logThread = new Thread() {
			@Override
			public void run() {
				try {
					OutputStream out = new FileOutputStream(file);
					try {
						for (int i = 0; i < 10; i += 1) {
							Thread.sleep(1000);
							out.write(String.valueOf(i).charAt(0) );
							out.write('\n');
							out.flush();
						}
					} finally {
						out.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};

		Thread tailThread = new Thread() {
			int count = 0;
			@Override
			public void run() {
				try {
					final TailInput in = new TailInput(file, 100);
					in.read(new TailInput.Consumer() {
						@Override
						public void accept(byte[] bytes) throws IOException {
							Assert.assertArrayEquals(
								new byte[]{ (byte)('0' + count), '\n'},
								bytes);
							//System.out.print(new String(bytes, "UTF-8") );
							//System.out.flush();
							count += 1;
							if (count == 10) {
								System.out.println("shutdown");
								in.shutdown();
							}
						}
					});
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		logThread.start();
		tailThread.start();
		System.out.println("wait for shutdown...");
		tailThread.join();
		System.out.println("joined.");
	}
}