package util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <pre>
 * Allows use of an interface with a closed source class, even if that
 * class doesn't implement the interface
 *
 * Example:
 * Closed source library:
 * public class Square implements Shape { void doSomething(); }
 *
 * Code:
 * private interface MyShape { void doSomething(); }
 *
 * Square square;
 * MyShape shapeProxy = (MyShape) RuntimeAdapter.getInstance(square, MyShape.class);
 * </pre>
 */
public class RuntimeAdapter implements InvocationHandler {

    private final Object object;

    private RuntimeAdapter(Object object) {
        this.object = object;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Method delegateMethod = object.getClass().getMethod(method.getName(), method.getParameterTypes());
        return delegateMethod.invoke(object, args);
    }

    public static Object getInstance(Object object, Class<?>... interfaces) {
        return Proxy.newProxyInstance(object.getClass().getClassLoader(),
                interfaces, new RuntimeAdapter(object));
    }

    public Object getObject() {
        return object;
    }

    private static class Demo {

        interface A {
            void run();
        }

        static class AImpl implements A {
            public void run() {
                System.out.println("Running A");
            }
        }

        interface B {
            void run();
        }

        public static void main(String[] args) {
            A a = new AImpl();

//            B b = (B) Proxy.newProxyInstance(
//                    B.class.getClassLoader(),
//                    new Class<?>[]{B.class},
//                    new InvocationHandler() {
//                        @Override
//                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                            Object instance = a;
//                            Method delegateMethod = instance.getClass().getMethod(method.getName(), method.getParameterTypes());
//                            return delegateMethod.invoke(instance, args);
//                        }
//                    });
//            b.run();

            B b = (B) RuntimeAdapter.getInstance(a, B.class);
            b.run();
        }
    }

}
