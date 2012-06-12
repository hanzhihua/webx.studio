package webx.studio.server.ui.viewers;

import java.util.ArrayList;
import java.util.List;

import webx.studio.server.ServerPlugin;
import webx.studio.server.core.ServerCore;
import webx.studio.server.core.ServerType;



/**
 * @author zhihua.hanzh
 *
 */
public class ServerTypeTreeContentProvider extends AbstractTreeContentProvider {

	public ServerTypeTreeContentProvider() {
		super(false);
		fillTree();
	}

	public void fillTree() {
		clean();

		List<TreeElement> list = new ArrayList<TreeElement>();
		ServerType[] serverTypes = ServerCore.getServerTypes();
		if (serverTypes != null) {
			int size = serverTypes.length;
			for (int i = 0; i < size; i++) {
				ServerType serverType = serverTypes[i];
				try {
					TreeElement ele = getOrCreate(list, serverType.getVendor());
					ele.contents.add(serverType);
					elementToParentMap.put(serverType, ele);
				} catch (Exception e) {
					ServerPlugin.logError(e);
				}

			}
		}
		elements = list.toArray();
	}

}