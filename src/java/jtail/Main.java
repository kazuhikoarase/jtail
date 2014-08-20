package jtail;

import java.io.File;
import java.io.IOException;
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
			System.err.println("jtail [target file] ([filter]...)");
			return;
		}

		final File file = new File(args[0]);
		filters = new ArrayList<Filter>();
		for (int i = 1; i < args.length; i += 1) {
			filters.add(new Filter(args[i]) );
		}

		final String encoding = System.getProperty("file.encoding");

		final TailInput in = new TailInput(file, 500);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				in.shutdown();
			}
		} ) );

		in.read(new TailInput.Consumer() {
			@Override
			public void accept(byte[] bytes) throws IOException {
				doLine(new String(bytes, encoding).
						replaceAll("\\r?\\n$", "") );
			}
		} );

	}

	protected static void doLine(String line) throws IOException {
		for (Filter filter : filters) {
			if (!filter.accept(line) ) {
				return;
			}
		}
		System.out.println(line);
	}
}
