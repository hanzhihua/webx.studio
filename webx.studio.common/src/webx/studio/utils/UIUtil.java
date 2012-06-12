package webx.studio.utils;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import webx.studio.StudioCommonPlugin;

/**
 *  @author zhihua.han@alibaba-inc.com
 *
 */
public abstract class UIUtil {

	public static Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	public static Button createButton(Composite parent, String labelText,
			SelectionListener listener) {
		return createButton(parent, labelText, listener, 0, true);
	}

	public static Button createButton(Composite parent, String labelText,
			SelectionListener listener, int indentation, boolean enabled) {
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		button.setText(labelText);
		button.addSelectionListener(listener);
		button.setEnabled(enabled);

		FontMetrics fontMetrics = getFontMetrics(button);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = Dialog.convertHorizontalDLUsToPixels(fontMetrics,
				IDialogConstants.BUTTON_WIDTH);
		gd.widthHint = Math.max(widthHint,
				button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		gd.horizontalIndent = indentation;
		button.setLayoutData(gd);
		return button;
	}

	public static FontMetrics getFontMetrics(Control control) {
		FontMetrics fontMetrics = null;
		GC gc = new GC(control);
		try {
			gc.setFont(control.getFont());
			fontMetrics = gc.getFontMetrics();
		} finally {
			gc.dispose();
		}
		return fontMetrics;
	}

	public static Button createCheckBox(Composite parent, String labelText) {
		Button button = new Button(parent, SWT.CHECK);
		button.setFont(parent.getFont());
		button.setText(labelText);
		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		return button;
	}

	public static Text createTextField(Composite parent, String labelText) {
		return createTextField(parent, labelText, 0, 0);
	}

	public static Text createTextField(Composite parent, String labelText,
			int indentation) {
		return createTextField(parent, labelText, indentation, 0);
	}

	public static Text createTextField(Composite parent, String labelText,
			int indentation, int widthHint) {
		Composite textArea = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		textArea.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentation;
		textArea.setLayoutData(gd);

		Label label = new Label(textArea, SWT.NONE);
		label.setText(labelText);
		label.setFont(parent.getFont());

		Text text = new Text(textArea, SWT.BORDER | SWT.SINGLE);
		text.setFont(parent.getFont());
		if (widthHint > 0) {
			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.widthHint = widthHint;
		} else {
			gd = new GridData(GridData.FILL_HORIZONTAL);
		}
		text.setLayoutData(gd);
		return text;
	}

	public static IEditorPart getActiveEditor() {
		IWorkbenchWindow window = StudioCommonPlugin.getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				return page.getActiveEditor();
			}
		}
		return null;
	}

	public static ITextEditor getTextEditor(IEditorPart part) {
		if (part instanceof ITextEditor) {
			return (ITextEditor) part;
		} else if (part instanceof IAdaptable) {
			return (ITextEditor) ((IAdaptable) part)
					.getAdapter(ITextEditor.class);
		}
		return null;
	}

	public static IEditorPart openInEditor(IFile file, int line) {
		return openInEditor(file, line, true);
	}

	public static IEditorPart openInEditor(IFile file, int line,
			boolean activate) {
		IEditorPart editor = null;
		IWorkbenchPage page = StudioCommonPlugin.getActiveWorkbenchPage();
		try {
			if (line > 0) {
				editor = IDE.openEditor(page, file, activate);
				ITextEditor textEditor = null;
				if (editor instanceof ITextEditor) {
					textEditor = (ITextEditor) editor;
				} else if (editor instanceof IAdaptable) {
					textEditor = (ITextEditor) ((IAdaptable) editor)
							.getAdapter(ITextEditor.class);
				}
				if (textEditor != null) {
					IDocument document = textEditor.getDocumentProvider()
							.getDocument(editor.getEditorInput());
					try {
						int start = document.getLineOffset(line - 1);
						((ITextEditor) textEditor).selectAndReveal(start, 0);
						page.activate(editor);
					} catch (BadLocationException x) {
						// ignore
					}
				} else {
					IMarker marker = file.createMarker(IMarker.TEXT);
					marker.setAttribute(IMarker.LINE_NUMBER, line);
					editor = IDE.openEditor(page, marker, activate);
					marker.delete();
				}
			} else {
				editor = IDE.openEditor(page, file, activate);
			}
		} catch (CoreException e) {
			openError("Open Editor Problems", e.getMessage(), e);
		}
		return editor;
	}

	public static IEditorPart openInEditor(IEditorInput input, String editorId) {
		return openInEditor(input, editorId, true);
	}

	public static IEditorPart openInEditor(IEditorInput input, String editorId,
			boolean activate) {
		IWorkbenchPage page = StudioCommonPlugin.getActiveWorkbenchPage();
		try {
			return page.openEditor(input, editorId, activate);
		} catch (PartInitException e) {
			openError("Open Editor Problems", e.getMessage(), e);
		}
		return null;
	}

	public static IEditorPart openInEditor(IJavaElement element) {
		try {
			IEditorPart editor = JavaUI.openInEditor(element);
			if (editor != null) {
				JavaUI.revealInEditor(editor, element);
			}
			return editor;
		} catch (PartInitException e) {
			openError("Open Editor Problems", e.getMessage(), e);
		} catch (JavaModelException e) {
			openError("Open Editor Problems", e.getMessage(), e);
		}
		return null;
	}

	public static String getSelectedText(ITextEditor editor) {
		ISelection selection = editor.getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection) {
			return ((ITextSelection) selection).getText().trim();
		}
		return null;
	}

	public static void openError(String title, String message){
		Shell shell = StudioCommonPlugin.getActiveWorkbenchShell();
		MessageDialog.openError(shell, title, message);
	}

	public static void openInformation(String title, String message){
		Shell shell = StudioCommonPlugin.getActiveWorkbenchShell();
		MessageDialog.openInformation(shell, title, message);
	}

	public static void openError(String title, String message,
			CoreException exception) {
		Shell shell = StudioCommonPlugin.getActiveWorkbenchShell();

		Throwable throwable = null;
		IStatus status = exception.getStatus();
		if (status != null) {
			throwable =  status.getException();
		}
		if (throwable != null) {
			ErrorDialog.openError(shell, title, message,
					status);
		} else {
			MessageDialog.openError(shell, title, message);
		}
	}

	public static void openError(String title,
			Exception exception) {
		if(exception instanceof CoreException){
			openError(title,exception.getMessage(),(CoreException)exception);
		}else{
			Shell shell = StudioCommonPlugin.getActiveWorkbenchShell();
			MessageDialog.openError(shell, title, exception.getMessage());
		}
	}

	public static void openError(String plugin,String title,Exception exception){
		Shell shell = StudioCommonPlugin.getActiveWorkbenchShell();
		Status status = new Status(IStatus.ERROR, plugin, 4, exception.getMessage(), exception);
		ErrorDialog.openError(shell, title,  exception.getMessage(),
				status);
	}

	public static void refreshPackageExplorer() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow workbenchWindow = StudioCommonPlugin
						.getDefault().getWorkbench().getActiveWorkbenchWindow();
				IWorkbenchPage page = workbenchWindow.getActivePage();
				IViewPart viewPart = page.findView(JavaUI.ID_PACKAGES);
				if (viewPart instanceof org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart) {
					org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart explorer = (org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart) viewPart;
					explorer.getTreeViewer().refresh();
				}
			}
		});
	}

	private static FontMetrics fontMetrics;

	protected static void initializeDialogUnits(Control testControl) {
		GC gc = new GC(testControl);
		gc.setFont(JFaceResources.getDialogFont());
		fontMetrics = gc.getFontMetrics();
		gc.dispose();
	}

	protected static int getButtonWidthHint(Button button) {
		int widthHint = Dialog.convertHorizontalDLUsToPixels(fontMetrics,
				IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint,
				button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	public static Button createButton(Composite comp, String label) {
		Button b = new Button(comp, SWT.PUSH);
		b.setText(label);
		if (fontMetrics == null)
			initializeDialogUnits(comp);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		data.widthHint = getButtonWidthHint(b);
		b.setLayoutData(data);
		return b;
	}

	public static int convertHorizontalDLUsToPixels(Composite comp, int x) {
		if (fontMetrics == null)
			initializeDialogUnits(comp);
		return Dialog.convertHorizontalDLUsToPixels(fontMetrics, x);
	}

	public static int convertVerticalDLUsToPixels(Composite comp, int y) {
		if (fontMetrics == null)
			initializeDialogUnits(comp);
		return Dialog.convertVerticalDLUsToPixels(fontMetrics, y);
	}

	public static void showView(final String viewId) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();
					if (page != null) {
						IWorkbenchPart part = page.findView(viewId);
						if (part == null) {
							try {
								part = page.showView(viewId);
							} catch (PartInitException e) {
								StudioCommonPlugin.logError(e);
							}
						}
						if (part != null) {
							page.activate(part);
							page.bringToTop(part);

						}
					}
				}
			}
		});
	}

	public static Control createEmptySpace(Composite parent) {
		return createEmptySpace(parent, 1);
	}

	private static Control createEmptySpace(Composite parent, int span) {
		Label label = new Label(parent, SWT.LEFT);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = span;
		gd.horizontalIndent = 0;
		gd.widthHint = 0;
		gd.heightHint = 0;
		label.setLayoutData(gd);
		return label;
	}


}
