package halcyon.view;

import halcyon.model.list.HalcyonNodeRepository;
import halcyon.model.list.HalcyonNodeRepositoryListener;
import halcyon.model.list.ObservableCollection;
import halcyon.model.list.ObservableCollectionListener;
import halcyon.model.node.HalcyonNode;
import halcyon.model.node.HalcyonNodeInterface;
import halcyon.model.node.HalcyonSwingNode;
import halcyon.window.console.ConsoleInterface;
import halcyon.window.console.StdOutputCaptureConsole;
import halcyon.window.control.ControlWindowBase;
import halcyon.window.toolbar.MicroscopeStartStopToolbar;
import halcyon.window.toolbar.ToolbarInterface;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.control.SplitPane;

import org.dockfx.DockNode;
import org.dockfx.DockPane;
import org.dockfx.DockPos;

/**
 * ViewManager class for managing Windows
 */
public class ViewManager
{
	private final List<HalcyonNodeDockable> pages = new LinkedList<>();

	/** a set of {@link HalcyonNode}s */
	private final HalcyonNodeRepository nodes;

	private final DockPane dockPane;

	private final ControlWindowBase controlWindow;

	private final StdOutputCaptureConsole console;

	private final MicroscopeStartStopToolbar toolbar;

	private DockNode deviceTabsDock;

	public ViewManager(	DockPane dockPane,
											ControlWindowBase config,
											HalcyonNodeRepository nodes,
											ObservableCollection<ConsoleInterface> consoles,
											ObservableCollection<ToolbarInterface> toolbars)
	{
		this.dockPane = dockPane;
		this.nodes = nodes;

		this.dockPane.setPrefSize(800, 600);

		controlWindow = config;
		controlWindow.setPrefSize(200, 300);
		controlWindow.dock(this.dockPane, DockPos.LEFT);

		console = new StdOutputCaptureConsole();
		console.setPrefSize(600, 200);
		console.dock(this.dockPane, DockPos.RIGHT, controlWindow);
		consoles.add(console);

		// Image deviceDockImage = new
		// Image(DockFX.class.getResource("docknode.png").toExternalForm());
		// deviceTabsDock = new DockNode(new VBox(), "Device", new
		// ImageView(deviceDockImage));
		// deviceTabsDock.setPrefSize( 600, 400 );
		// deviceTabsDock.dock( this.dockPane, DockPos.TOP, console );

		toolbar = new MicroscopeStartStopToolbar();
		toolbar.setPrefSize(200, 300);
		toolbar.dock(this.dockPane, DockPos.TOP, controlWindow);
		toolbars.add(toolbar);

		SplitPane split = (SplitPane) dockPane.getChildren().get(0);
		split.setDividerPositions(0.3);

		toolbars.addListener(new ObservableCollectionListener<ToolbarInterface>()
		{
			@Override
			public void itemAdded(ToolbarInterface item)
			{
				((ControlWindowBase) item).dock(dockPane,
																				DockPos.CENTER,
																				toolbar);
			}

			@Override
			public void itemRemoved(ToolbarInterface item)
			{

			}
		});

		consoles.addListener(new ObservableCollectionListener<ConsoleInterface>()
		{
			@Override
			public void itemAdded(ConsoleInterface item)
			{
				((ControlWindowBase) item).dock(dockPane,
																				DockPos.CENTER,
																				console);
			}

			@Override
			public void itemRemoved(ConsoleInterface item)
			{

			}
		});

		nodes.addListener(new HalcyonNodeRepositoryListener()
		{
			@Override
			public void nodeAdded(HalcyonNodeInterface node)
			{
				open(node);
			}

			@Override
			public void nodeRemoved(HalcyonNodeInterface node)
			{
				close(node);
			}
		});
	}

	public HalcyonNodeRepository getNodes()
	{
		return nodes;
	}

	public void open(HalcyonNodeInterface node)
	{
		if (node instanceof HalcyonSwingNode)
		{
			HalcyonSwingNode lHalcyonSwingNode = (HalcyonSwingNode) node;
			lHalcyonSwingNode.setVisible(true);
			return;
		}

		for (final HalcyonNodeDockable n : pages)
		{
			if (n.isDocked())
			{
				deviceTabsDock = n;
				break;
			}
		}

		for (final HalcyonNodeDockable n : pages)
		{
			if (n.getNode() == node)
			{

				if (n.isDocked())
					return;
				else
				{
					if (deviceTabsDock.isDocked())
					{
						n.dock(dockPane, DockPos.CENTER, deviceTabsDock);
					}
					else
					{
						deviceTabsDock = n;
						n.dock(this.dockPane, DockPos.TOP, console);
					}
					return;
				}
			}
		}

		final HalcyonNodeDockable page = new HalcyonNodeDockable(node);
		if (pages.size() == 0)
		{
			deviceTabsDock = page;
			page.dock(this.dockPane, DockPos.TOP, console);
		}
		else
		{
			page.dock(dockPane, DockPos.CENTER, deviceTabsDock);
		}
		pages.add(page);
	}

	public void hide(HalcyonNodeInterface node)
	{
		if (node instanceof HalcyonSwingNode)
		{
			HalcyonSwingNode lHalcyonSwingNode = (HalcyonSwingNode) node;
			lHalcyonSwingNode.setVisible(false);
			return;
		}

		for (final HalcyonNodeDockable page : pages.toArray(new HalcyonNodeDockable[pages.size()]))
		{
			if (page.getNode() == node)
			{
				page.setVisible(false);
			}
		}
	}

	public void close(HalcyonNodeInterface node)
	{
		if (node instanceof HalcyonSwingNode)
		{
			HalcyonSwingNode lHalcyonSwingNode = (HalcyonSwingNode) node;
			lHalcyonSwingNode.close();
			return;
		}

		for (final HalcyonNodeDockable page : pages.toArray(new HalcyonNodeDockable[pages.size()]))
		{
			if (page.getNode() == node)
			{
				page.close();
			}
		}
	}

	public boolean isVisible()
	{
		return dockPane.isVisible();
	}
}
