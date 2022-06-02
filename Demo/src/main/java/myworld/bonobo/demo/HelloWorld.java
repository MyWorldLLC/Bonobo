package myworld.bonobo.demo;

import myworld.bonobo.core.Application;

public class HelloWorld extends Application {

    public static void main(String[] args){
        HelloWorld hello = new HelloWorld();
        hello.start();
    }

    @Override
    public void initializeApp(){
        systemManager.register(new HelloSystem());
    }

}
