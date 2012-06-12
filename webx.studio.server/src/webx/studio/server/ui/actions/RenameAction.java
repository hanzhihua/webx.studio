package webx.studio.server.ui.actions;


import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.TextActionHandler;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import webx.studio.server.core.Server;

public class RenameAction extends AbstractServerAction {

	protected TreeEditor treeEditor;

	protected Tree tree;

	protected Text textEditor;

	protected Composite textEditorParent;

	private TextActionHandler textActionHandler;

	protected Server editedServer;

	protected boolean saving = false;

	public RenameAction(Shell shell, TreeViewer viewer, ISelectionProvider selectionProvider) {
		super(shell, selectionProvider, "Re&name");
		setActionDefinitionId(IWorkbenchActionDefinitionIds.RENAME);
		this.tree = viewer.getTree();
		this.treeEditor = new TreeEditor(tree);
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
			// ignore
		}
	}

	public void perform(Server server) {
		runWithInlineEditor(server);
	}

	private void runWithInlineEditor(Server server) {
		queryNewServerNameInline(server);
	}

	private Tree getTree() {
		return tree;
	}

	private Composite createParent() {
		Tree tree2 = getTree();
		Composite result = new Composite(tree2, SWT.NONE);
		TreeItem[] selectedItems = tree2.getSelection();
		treeEditor.horizontalAlignment = SWT.LEFT;
		treeEditor.grabHorizontal = true;
		treeEditor.setEditor(result, selectedItems[0]);
		return result;
	}

	private static int getCellEditorInset(Control c) {
		// special case for MacOS X
		if ("carbon".equals(SWT.getPlatform())) { //$NON-NLS-1$
			if (System
					.getProperty("org.eclipse.swt.internal.carbon.noFocusRing") == null || c.getShell().getParent() != null) { //$NON-NLS-1$
				return -2; // native border
			}
		}
		return 1; // one pixel wide black border
	}

	private void queryNewServerNameInline(final Server server) {
		if (textEditorParent == null) {
			createTextEditor(server);
		}
		textEditor.setText(server.getName());

		textEditorParent.setVisible(true);
		Point textSize = textEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		textSize.x += textSize.y; // Add extra space for new characters
		Point parentSize = textEditorParent.getSize();
		int inset = getCellEditorInset(textEditorParent);
		textEditor.setBounds(2, inset, Math.min(textSize.x, parentSize.x - 4),
				parentSize.y - 2 * inset);
		textEditorParent.redraw();
		textEditor.selectAll();
		textEditor.setFocus();
	}

	private void createTextEditor(final Server server) {
		// Create text editor parent. This draws a nice bounding rect
		textEditorParent = createParent();
		textEditorParent.setVisible(false);
		final int inset = getCellEditorInset(textEditorParent);
		if (inset > 0) {
			textEditorParent.addListener(SWT.Paint, new Listener() {
				public void handleEvent(Event e) {
					Point textSize = textEditor.getSize();
					Point parentSize = textEditorParent.getSize();
					e.gc.drawRectangle(0, 0, Math.min(textSize.x + 4,
							parentSize.x - 1), parentSize.y - 1);
				}
			});
		}
		// Create inner text editor
		textEditor = new Text(textEditorParent, SWT.NONE);
		textEditor.setFont(tree.getFont());
		textEditorParent.setBackground(textEditor.getBackground());
		textEditor.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				Point textSize = textEditor.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
				textSize.x += textSize.y; // Add extra space for new
				// characters.
				Point parentSize = textEditorParent.getSize();
				textEditor.setBounds(2, inset, Math.min(textSize.x,
						parentSize.x - 4), parentSize.y - 2 * inset);
				textEditorParent.redraw();
			}
		});
		textEditor.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {

				// Workaround for Bug 20214 due to extra
				// traverse events
				switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
					// Do nothing in this case
					disposeTextWidget();
					event.doit = true;
					event.detail = SWT.TRAVERSE_NONE;
					break;
				case SWT.TRAVERSE_RETURN:
					saveChangesAndDispose(server);
					event.doit = true;
					event.detail = SWT.TRAVERSE_NONE;
					break;
				}
			}
		});
		textEditor.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent fe) {
				saveChangesAndDispose(server);
			}
		});

		if (textActionHandler != null) {
			textActionHandler.addText(textEditor);
		}
	}

	protected void disposeTextWidget() {
		if (textActionHandler != null)
			textActionHandler.removeText(textEditor);

		if (textEditorParent != null) {
			textEditorParent.dispose();
			textEditorParent = null;
			textEditor = null;
			treeEditor.setEditor(null, null);
		}
	}

	/**
	 * Save the changes and dispose of the text widget.
	 *
	 * @param server the server to rename
	 */
	protected void saveChangesAndDispose(final Server server) {
		if (saving == true)
			return;

		saving = true;
		final String newName = textEditor.getText();
		getTree().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					if (!newName.equals(((Server)server).getName())) {
						((Server)server).setName(newName);
						((Server)server).save();
					}
					editedServer = null;
					disposeTextWidget();
					if (tree != null && !tree.isDisposed()) {
						tree.setFocus();
					}
				} finally {
					saving = false;
				}
			}
		});
	}

}
