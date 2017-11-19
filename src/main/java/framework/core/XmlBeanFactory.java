package framework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import framework.core.annotations.Required;
import framework.injection.interface_inj.injectors.InjectFinder;
import framework.injection.interface_inj.injectors.InjectFinderFilename;
import framework.injection.interface_inj.injectors.Injector;
import framework.parsers.Bean;
import framework.parsers.Scope;

public class XmlBeanFactory implements BeanFactory {
    
    Map<String, Object> beanTable = new HashMap<>();
    Map<String, Object> interceptorTable = new HashMap<>();
    Map<Class, Object> interfaceInjectors = new HashMap<>();
    Map<String, Bean> beanMap;
    
    XmlBeanFactory(String xmlFilePath, XmlBeanDefinitionReader xbdr) {        
        xbdr.loadBeanDefinitions(xmlFilePath);
        beanMap = xbdr.getBeanMap();
        generateBeans(beanMap);
        setInterfaceInjectors(beanTable);
        injectDependencies(beanTable.values());
        setupInterceptors(xbdr.getInterceptorList());
    }
    
    private void generateBeans(Map<String, Bean> beanMap) {
        for (String name : beanMap.keySet()) {
            Bean b = beanMap.get(name);
            if(b.getScope() == Scope.SINGLETON) {
                beanTable.put(b.getName(), generateBean(b));
            }
        }
    }

    private Object generateBean(Bean b) {
        try {
            final Class<?> clazz = Class.forName(b.getClassName());
            Constructor<?> ctor;
            Object object;

            List<String> ca = b.getConstructorArg();

            if (!ca.isEmpty()) {

                Class<?>[] consClasses = new Class[ca.size() / 2];

                for (int i = 0, j = 0; i < ca.size(); i+=2) {
                    if (ca.get(i) == null || ca.get(i).contentEquals("String")) {
                        consClasses[j] = String.class;
                    } else if (classLibrary.containsKey(ca.get(i))) {
                        consClasses[j] = getPrimitiveClassForName(ca.get(i));
                    } else {
                        consClasses[j] = Class.forName(ca.get(i));
                    }
                    j++;
                }
                ctor = clazz.getConstructor(consClasses);
                Object[] consArgs = new Object[consClasses.length];
                for (int i = 1, j = 0; i < ca.size(); i+=2) {
                    if (consClasses[j].isPrimitive()){
                        consArgs[j] =
                                getWrapperClassValueForPrimitiveType(consClasses[j], ca.get(i));
                    }
                    else {
                        consArgs[j] = consClasses[j].cast(ca.get(i));
                    }
                    j++;
                }
                object = ctor.newInstance(consArgs);
            } else {
                ctor = clazz.getConstructor();
                object = ctor.newInstance();
            }

            List<String> props = b.getProperties();

            if (!props.isEmpty()) {
                for (int i = 0; i < props.size(); i++) {
                    char first = Character.toUpperCase(props.get(i).charAt(0));
                    String methodName = "set" + first + props.get(i).substring(1);
                    Method method = object.getClass().getMethod(methodName,
                            new Class[] { props.get(i+1).getClass() });
                    method.invoke(object, props.get(i+1));
                    i++;
                }
            }
            for(Field field : object.getClass().getDeclaredFields()) {
                if(field.isAnnotationPresent(Required.class)) {
                    boolean isAccessible = field.isAccessible();
                    field.setAccessible(true);
                    if(field.get(object) == null) {
                        throw new BeanInitializationException("Property " + field.getName() + " is not set on " + object);
                    }
                    field.setAccessible(isAccessible);
                }
            }

            return object;
        } catch(Exception ex) {
            throw new BeanInitializationException("Exception occured during initialization " + ex.getLocalizedMessage());
        }
    }
    
    private void setupInterceptors(List<Bean> interceptorList) {
        for (Bean b : interceptorList) {
            try {
                final Class<?> clazz = Class.forName(b.getClassName());                
                Object interceptor = clazz.getConstructor().newInstance();
                interceptorTable.put(b.getName(), interceptor);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void injectDependencies(Collection<Object> beans) {
        for(Object bean : beans) {
            injectDependenciesIntoTheBean(bean);
        }
    }

    private void injectDependenciesIntoTheBean(Object bean) {
        interfaceInjection(bean);
    }

    private void interfaceInjection(Object bean) {
        for(Class inject : interfaceInjectors.keySet()){
            if(inject.isAssignableFrom(bean.getClass())){
                ((Injector)interfaceInjectors.get(inject)).injectInto(bean);
            }
        }

    }

    private void setInterfaceInjectors(Map<String, Object> beanTable) {
        interfaceInjectors.put(InjectFinder.class, beanTable.get("MovieFinder"));
        interfaceInjectors.put(InjectFinderFilename.class, beanTable.get("FilenameFinder"));
    }



    public Object getBean(String name) {
        if(beanMap.get(name).getScope() == Scope.PROTOTYPE) {
            Object bean = generateBean(beanMap.get(name));
            injectDependenciesIntoTheBean(bean);
            return bean;
        } else {
            return beanTable.get(name);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String name, Class<T> type) {
        return (T) getBean(name);
    }
    
    public Object[] getInterceptors() {
        return interceptorTable.values().toArray();
    }

}
