package webx.studio.server.core;

import java.util.List;


import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.internal.core.JavaProject;

import webx.studio.projectcreation.ui.core.JejuProjectCore;

public class ServerPropertyTester extends PropertyTester {

	private static final String IS_RUNNABLE_PROPERTY = "isRunnable";
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		System.out.println("===================================================");
		System.out.println(receiver.getClass()+":::"+receiver);
		System.out.println("===================================================");
		if (IS_RUNNABLE_PROPERTY.equals(property)) {
			if(receiver instanceof List){
				List list = (List)receiver;
				if(list.size() == 1){
					Object obj = list.get(0);
//					System.out.println("===================================================");
//					System.out.println(obj.getClass()+":::"+obj.toString());
//					System.out.println("===================================================");
					if(obj instanceof JavaProject){
						JavaProject jp = (JavaProject)obj;
						if(JejuProjectCore.isWarProject(jp.getProject().getName()))
							return true;
					}
					return false;
				}
			}

		}
		return true;
	}

}
