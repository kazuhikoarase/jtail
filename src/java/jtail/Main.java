package jtail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main
 * @author kazuhiko arase
 */
public class Main {
	
	private static List<Filter> filters;
	
	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			return;
		}

		File file = new File(args[0]);
		filters = new ArrayList<Filter>();
		for (int i = 1; i < args.length; i += 1) {
			filters.add(new Filter(args[i]) );
		}
		String encoding = System.getProperty("file.encoding");

		ByteArrayOutputStream buf = new ByteArrayOutputStream();

		final TailInputStream in = new TailInputStream(file);

		try {

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					in.shutdown();
				}
			} ) );

			int b;

			while ( (b = in.read() ) != -1) {
				
				buf.write(b);

				if (b == '\n') {
					buf.close();
					doLine(new String(buf.toByteArray(), encoding).
						replaceAll("\\r?\\n$", "") );
					buf = new ByteArrayOutputStream();
				}
			}
		} finally {
			in.close();
		}
	}

	protected static void doLine(String line) throws Exception {
		for (Filter filter : filters) {
			if (!filter.accept(line) ) {
				return;
			}
		}
		System.out.println(line);
	}
}
