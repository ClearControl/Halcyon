package halcyon.view.console;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import halcyon.view.ConsolePanel;
import org.dockfx.DockNode;

/**
 * Standard Output and Error capture console
 */
public class StdOutputCaptureConsole extends DockNode
{
	private final ConsolePanel consolePane;

	/**
	 * Instantiates a new Standard output/error capture console.
	 */
	public StdOutputCaptureConsole()
	{
		super(new ConsolePanel());
		setTitle("Console");

		consolePane = (ConsolePanel ) getContents();

		System.setOut(new PrintStream(new StreamAppender(	"StdOut",
																											consolePane,
																											System.out)));
		System.setErr(new PrintStream(new StreamAppender(	"StdErr",
																											consolePane,
																											System.err)));
	}

	/**
	 * The Stream appender for output stream.
	 */
	public class StreamAppender extends OutputStream
	{
		private StringBuilder buffer;
		private String prefix;
		private TextAppender textAppender;
		private PrintStream old;

		/**
		 * Instantiates a new Stream appender.
		 * @param prefix the prefix
		 * @param consumer the consumer
		 * @param old the old
		 */
		public StreamAppender(String prefix,
													TextAppender consumer,
													PrintStream old)
		{
			this.prefix = prefix;
			buffer = new StringBuilder(128);
			buffer.append("[").append(prefix).append("] ");
			this.old = old;
			this.textAppender = consumer;
		}

		/**
		 * Write b.
		 * @param b the b
		 * @throws IOException the io exception
		 */
		@Override
		public void write(int b) throws IOException
		{
			char c = (char) b;
			String value = Character.toString(c);
			buffer.append(value);
			if (value.equals("\n"))
			{
				textAppender.appendText(buffer.toString());
				buffer.delete(0, buffer.length());
				buffer.append("[").append(prefix).append("] ");
			}
			old.print(c);
		}
	}
}