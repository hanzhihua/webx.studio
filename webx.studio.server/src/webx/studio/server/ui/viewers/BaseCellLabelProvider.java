package webx.studio.server.ui.viewers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;

/**
 * @author zhihua.hanzh
 *
 */
public  abstract class BaseCellLabelProvider extends ColumnLabelProvider {
	
	public ILabelDecorator decorator;
	
	protected ILabelProviderListener providerListener;

	public BaseCellLabelProvider() {
		super();
	}

	public BaseCellLabelProvider(ILabelDecorator decorator) {
		super();
		this.decorator = getDecorator();
	}

	public Point getToolTipShift(Object object) {
		return new Point(5, 5);
	}

	public int getToolTipDisplayDelayTime(Object object) {
		return 2000;
	}

	public int getToolTipTimeDisplayed(Object object) {
		return 5000;
	}

	public void dispose() {
		if (decorator != null && providerListener != null) {
			decorator.removeListener(providerListener);
		}
		super.dispose();
	}

	public ILabelDecorator getDecorator(){
		if (decorator == null){
			decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
			providerListener = new ILabelProviderListener() {
				@SuppressWarnings("synthetic-access")
				public void labelProviderChanged(LabelProviderChangedEvent event) {
					fireLabelProviderChanged(event);
				}
			};
			decorator.addListener(providerListener);
		}
		return decorator;
	}

}