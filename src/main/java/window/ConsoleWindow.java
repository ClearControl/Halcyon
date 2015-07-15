package window;

import model.JPanelProvider;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;

/**
 * Control type Console Window
 *
 * Capturing stdout / stderr
 */
public class ConsoleWindow extends ControlType implements JPanelProvider
{
	static final int cMaxNumberOfLines = 500_000;

	private final ConsolePane consolePane;

	public ConsoleWindow()
	{
		super( "ConsoleDockable" );

		consolePane = new ConsolePane();

		System.setOut( new PrintStream( new StreamAppender( "StdOut", consolePane, System.out ) ) );
		System.setErr( new PrintStream( new StreamAppender( "StdErr", consolePane, System.err ) ) );

		setTitleText( "Console" );
		setCloseable( false );
		setMinimizable( true );
		setMaximizable( true );

		setLayout( new BorderLayout() );

		add( consolePane, BorderLayout.CENTER );
	}

	public class ConsolePane extends JPanel implements TextAppender
	{

		private JTextArea mTextArea;

		public ConsolePane()
		{
			setLayout( new BorderLayout() );
			mTextArea = new JTextArea();

			((AbstractDocument) mTextArea.getDocument()).setDocumentFilter(
					new LineLimitedDocumentFilter( mTextArea, cMaxNumberOfLines ) );

			add( new JScrollPane( mTextArea ) );
		}

		@Override
		public void appendText( final String text )
		{
			if (EventQueue.isDispatchThread())
			{
				mTextArea.append( text );
				mTextArea.setCaretPosition( mTextArea.getText().length() );
			} else
			{
				EventQueue.invokeLater( () -> appendText( text ) );
			}
		}
	}

	public interface TextAppender
	{
		public void appendText( String text );
	}

	public class StreamAppender extends OutputStream
	{

		private StringBuilder buffer;
		private String prefix;
		private TextAppender textAppender;
		private PrintStream old;

		public StreamAppender( String prefix, TextAppender consumer, PrintStream old )
		{
			this.prefix = prefix;
			buffer = new StringBuilder( 128 );
			buffer.append( "[" ).append( prefix ).append( "] " );
			this.old = old;
			this.textAppender = consumer;
		}

		@Override
		public void write( int b ) throws IOException
		{
			char c = (char) b;
			String value = Character.toString( c );
			buffer.append( value );
			if (value.equals( "\n" ))
			{
				textAppender.appendText( buffer.toString() );
				buffer.delete( 0, buffer.length() );
				buffer.append( "[" ).append( prefix ).append( "] " );
			}
			old.print( c );
		}
	}

	@Override public JPanel getJPanel()
	{
		return consolePane;
	}
}
