package webx.studio.server.ui.viewers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author zhihua.hanzh
 *
 */
public abstract class AbstractTreeContentProvider implements ITreeContentProvider {

	public static final String ROOT = "root";

	protected Object initialSelection;

	public class TreeElement {
		public String text;
		public List<Object> contents;
	}

	protected Object[] elements;
	protected Map<Object, TreeElement> elementToParentMap = new HashMap<Object, TreeElement>(2);
	protected Map<String, TreeElement> textMap = new HashMap<String, TreeElement>(2);

	/**
	 * AbstractTreeContentProvider constructor comment.
	 */
	public AbstractTreeContentProvider() {
		super();

		fillTree();
	}

	public AbstractTreeContentProvider(boolean init) {
		super();
	}

	protected abstract void fillTree();

	protected void clean() {
		elements = null;
		elementToParentMap = new HashMap<Object, TreeElement>(2);
		textMap = new HashMap<String, TreeElement>(2);

		initialSelection = null;
	}

	protected TreeElement getOrCreate(List<TreeElement> list, String text) {
		try {
			Object obj = textMap.get(text);
			if (obj != null)
				return (TreeElement) obj;
		} catch (Exception e) {
			return null;
		}

		TreeElement element = new TreeElement();
		element.text = text;
		element.contents = new ArrayList<Object>();
		textMap.put(text, element);
		list.add(element);
		return element;
	}

	protected TreeElement getOrCreate(List<TreeElement> list, String id, String text) {
		try {
			Object obj = textMap.get(id);
			if (obj != null)
				return (TreeElement) obj;
		} catch (Exception e) {
			return null;
		}

		TreeElement element = new TreeElement();
		element.text = text;
		element.contents = new ArrayList<Object>();
		textMap.put(id, element);
		list.add(element);
		return element;
	}

	protected TreeElement getByText(String text) {
		try {
			return textMap.get(text);
		} catch (Exception e) {
			return null;
		}
	}

	protected TreeElement getParentImpl(Object obj) {
		try {
			return elementToParentMap.get(obj);
		} catch (Exception e) {
			return null;
		}
	}

	public void dispose() {
		// do nothing
	}

	public Object[] getElements(Object element) {
		return elements;
	}

	public Object[] getChildren(Object element) {
		if (!(element instanceof TreeElement))
			return null;

		TreeElement rte = (TreeElement) element;
		return rte.contents.toArray();
	}

	public Object getParent(Object element) {
		return getParentImpl(element);
	}

	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	private Object[] getAllObjects() {
		List<Object> list = new ArrayList<Object>();
		Object[] obj = getElements(null);
		if (obj != null) {
			int size = obj.length;
			for (int i = 0; i < size; i++) {
				if (!(obj[i] instanceof AbstractTreeContentProvider.TreeElement))
					list.add(obj[i]);
				getAllChildren(list, obj[i]);
			}
		}
		return list.toArray();
	}

	private void getAllChildren(List<Object> list, Object element) {
		Object[] obj = getChildren(element);
		if (obj != null) {
			int size = obj.length;
			for (int i = 0; i < size; i++) {
				if (!(obj[i] instanceof AbstractTreeContentProvider.TreeElement))
					list.add(obj[i]);
				getAllChildren(list, obj[i]);
			}
		}
	}


}