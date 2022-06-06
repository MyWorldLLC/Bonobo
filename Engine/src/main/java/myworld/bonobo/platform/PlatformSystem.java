package myworld.bonobo.platform;

import myworld.bonobo.platform.windowing.Window;
import myworld.bonobo.platform.windowing.WindowManager;


public interface PlatformSystem {

    WindowManager<? extends Window> getWindowManager();

}
