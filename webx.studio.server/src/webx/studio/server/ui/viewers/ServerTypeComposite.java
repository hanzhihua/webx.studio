package webx.studio.server.ui.viewers;


import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

import webx.studio.server.core.ServerType;


/**
 * @author zhihua.hanzh
 *
 */
public class ServerTypeComposite  extends AbstractTreeComposite {

	protected ServerTypeTreeContentProvider contentProvider;

	public ServerTypeComposite(Composite parent) {
		super(parent);

		contentProvider = new ServerTypeTreeContentProvider();
		treeViewer.setContentProvider(contentProvider);

		ILabelProvider labelProvider = new ServerTypeTreeLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.setInput(AbstractTreeContentProvider.ROOT);
	}

	protected String getTitleLabel() {
		return "Select the &server type:";
	}


	public void refresh() {
		ISelection sel = treeViewer.getSelection();
		treeViewer.setContentProvider(new ServerTypeTreeContentProvider());
		treeViewer.setSelection(sel);
	}

	public ServerType getSelectedServerType(){
		Object object =treeViewer.getSelection();
		if(object instanceof ServerType){
			return(ServerType)object;
		}
		if(object instanceof TreeSelection){
			TreeSelection ts = (TreeSelection)object;
			object = ts.getFirstElement();
			if(object instanceof ServerType){
				return (ServerType)object;
			}
		}
		return null;
	}

	public TreeViewer getTreeViewer(){
		return this.treeViewer;
	}
}