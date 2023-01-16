/*
 * Copyright 2022 MyWorld, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package myworld.bonobo.demo;

import myworld.bonobo.core.Application;
import myworld.bonobo.platform.render.GlfwVulkanPlatform;
import myworld.bonobo.platform.windowing.Window;
import myworld.bonobo.platform.windowing.WindowFeatures;

public class HelloWorld extends Application {

    protected Window window;

    public static void main(String[] args){
        HelloWorld hello = new HelloWorld();
        hello.start();
    }

    @Override
    public void initializeApp(){
        systemManager.register(new HelloSystem());
        window = systemManager.getSystem(GlfwVulkanPlatform.class).createWindow(
                new WindowFeatures(
                "Bonobo",
                640, 480,
                new WindowFeatures.DisplayState(false, false))
        );
        window.setVisible(true);
    }

}
