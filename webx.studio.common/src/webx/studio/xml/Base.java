package webx.studio.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



/**
 *  @author zhihua.han@alibaba-inc.com
 *
 */
public abstract class Base {

	protected static final String PROP_ID = "id";

	protected static final String PROP_NAME = "name";

	protected static final String PROP_HOST = "host";

	protected static final String PROP_OPEN_BROWSER = "open_browser";

	protected static final String PROP_URI_CHARSET = "uri_charset";

	protected Map<String, Object> map = new HashMap<String, Object>();

	public boolean equals(Object obj) {
		if (!(obj instanceof Base))
			return false;

		Base base = (Base) obj;
		if (getId() == null)
			return false;
		if (!getId().equals(base.getId()))
			return false;
		return true;
	}

	public List<String> getAttribute(String attributeName, List<String> defaultValue) {
		try {
			Object obj = map.get(attributeName);
			if (obj == null)
				return defaultValue;
			return (List<String>) obj;
		} catch (Exception e) {
			// ignore
		}
		return defaultValue;
	}

	public String getAttribute(String attributeName, String defaultValue) {
		try {
			Object obj = map.get(attributeName);
			if (obj == null)
				return defaultValue;
			return (String) obj;
		} catch (Exception e) {
			// ignore
		}
		return defaultValue;
	}

	public boolean getAttribute(String attributeName, boolean defaultValue) {
		try {
			Object obj = map.get(attributeName);
			if (obj == null)
				return defaultValue;
			if(obj instanceof Boolean)
				return ((Boolean)obj).booleanValue();
			return Boolean.valueOf((String) obj).booleanValue();
		} catch (Exception e) {
			// ignore
		}
		return defaultValue;
	}


	public String getId() {
		return getAttribute(PROP_ID, "");
	}
	public String getName() {
		return getAttribute(PROP_NAME, "");
	}

	public String getHost(){
		return  getAttribute(PROP_HOST, "");
	}

	public boolean isOpenBrowser(){
		return getAttribute(PROP_OPEN_BROWSER, true);
	}

	public void setOpenBrowser(boolean openBrowser){
		map.put(PROP_OPEN_BROWSER, openBrowser);
	}

	public boolean isWithoutAutoconfig() {
		return getAttribute("withoutAutoConfig", false);
	}

	public void setWithoutAutoconfig(boolean withoutAutoconfig) {
		map.put("withoutAutoConfig", withoutAutoconfig);
	}

	public boolean isWithoutReloadfunction() {
		return getAttribute("withoutReloadFunction", false);
	}

	public void setWithoutReloadfunction(boolean withoutReloadfunction) {
		map.put("withoutReloadFunction", withoutReloadfunction);
	}

	public void setName(String name) {
		map.put("name", name);
		if (map.get("id") == null)
			map.put("id", name);
	}

	public void setPort(String port) {
		map.put("port", port);
	}

	public void setHost(String host){
		map.put(PROP_HOST, host);
	}

	public String getPort() {
		return getAttribute("port", "");
	}

	public void setUriCharset(String charset){
		map.put(PROP_URI_CHARSET, charset);
	}

	public String getUriCharset(){
		return getAttribute(PROP_URI_CHARSET, "");
	}

	public void loadList(XMLMemento memento) {
		String key = memento.getString("key");
		List<String> list = new ArrayList<String>();
		int i = 0;
		String key2 = memento.getString("value" + (i++));
		while (key2 != null) {
			list.add(key2);
			key2 = memento.getString("value" + (i++));
		}
		map.put(key, list);
	}

	public void loadMap(XMLMemento memento) {
		String key = memento.getString("key");
		Map<String, String> vMap = new HashMap<String, String>();
		Iterator<String> iterator = memento.getNames().iterator();
		while(iterator.hasNext()) {
			String s = iterator.next();
			String v = memento.getString(s);
			vMap.put(s,v);
		}
		map.put(key, vMap);
	}

	public void save(XMLMemento memento) {
		XMLMemento child = memento;
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Object obj = map.get(key);
			if (obj instanceof String)
				child.putString(key, (String) obj);
			else if (obj instanceof Integer) {
				Integer in = (Integer) obj;
				child.putInteger(key, in.intValue());
			} else if (obj instanceof Boolean) {
				Boolean bool = (Boolean) obj;
				child.putBoolean(key, bool.booleanValue());
			} else if (obj instanceof List) {
				List list = (List) obj;
				saveList(child, key, list);
			} else if (obj instanceof Map) {
				Map map2 = (Map) obj;
				saveMap(child, key, map2);

			}
		}
	}

	public void saveList(XMLMemento memento, String key, List list) {
		XMLMemento child = memento.createChild("list");
		child.putString("key", key);
		int i = 0;
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			String s = (String) iterator.next();
			child.putString("value" + (i++), s);
		}
	}

	public void saveMap(XMLMemento memento, String key, Map map2) {
		XMLMemento child = memento.createChild("map");
		child.putString("key", key);
		Iterator iterator = map2.keySet().iterator();
		while (iterator.hasNext()) {
			String s = (String) iterator.next();
			child.putString(s, (String)map2.get(s));
		}
	}
}
