package webx.studio.service.server.core;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.internal.core.JavaProject;

public class ServiceServerPropertyTester extends PropertyTester {

	private static final String IS_RUNNABLE_PROPERTY = "isRunnable";
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (IS_RUNNABLE_PROPERTY.equals(property)) {
			if(receiver instanceof List){
				List list = (List)receiver;
				if(list.size() == 1){
					Object obj = list.get(0);
					if(obj instanceof JavaProject){
							return true;
					}
					return false;
				}
			}

		}
		return true;
	}

}
