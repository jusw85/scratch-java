package camel;

import org.apache.camel.spring.Main;

class SpringTest {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.setFileApplicationContextUri("camel-spring4.xml");
        main.run();
    }
}
