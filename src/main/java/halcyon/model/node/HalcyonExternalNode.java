package halcyon.model.node;

import javafx.scene.Node;

/**
 * Halcyon External Node
 * 
 */
public class HalcyonExternalNode extends HalcyonNodeBase implements
																												HalcyonNodeInterface
{

	private Runnable mRunnableShow, mRunnableHide, mRunnableClose;

	public HalcyonExternalNode(	String name,
															HalcyonNodeType type,
															Runnable pRunnableShow,
															Runnable pRunnableHide,
															Runnable pRunnableClose)
	{
		super(name, type);
		mRunnableShow = pRunnableShow;
		mRunnableHide = pRunnableHide;
		mRunnableClose = pRunnableClose;
		mRunnableClose = pRunnableClose;
	}

	@Override
	public Node getPanel()
	{
		throw new UnsupportedOperationException("Cannot request panel from an external node (external nodes are non-dockable)");
	}

	public void setVisible(boolean pVisible)
	{
		if (pVisible)
		{
			if (mRunnableShow != null)
				mRunnableShow.run();
		}
		else
		{
			if (mRunnableHide != null)
				mRunnableHide.run();
		}
	}

	public void close()
	{
		if (mRunnableClose != null)
			mRunnableClose.run();
	}

}