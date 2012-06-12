package $package;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;

public class HelloWorld{

    public void execute(Context context, Navigator navigator) {
        context.put("message", "$message");
    }

}