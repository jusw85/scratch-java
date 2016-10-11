package test.camel;

import org.apache.camel.spring.Main;

class SpringTest {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.setFileApplicationContextUri("camel-spring4.xml");
        main.run();
    }
}
