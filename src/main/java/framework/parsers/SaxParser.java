package framework.parsers;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import framework.core.annotations.Component;
import framework.core.annotations.Controller;
import framework.core.annotations.Repository;
import framework.core.annotations.Service;
import org.reflections.Reflections;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxParser extends DefaultHandler implements Parser {
    
    Map<String,Bean> beanMap;
    List<Bean> interceptorList;
    String xmlFileName;
    String tmpValue;
    Bean beanTmp;
    
    public Map<String, Bean> getBeanMap() {
        return beanMap;
    }
    
    public List<Bean> getInterceptorList() {
        return interceptorList;
    }
        
    public SaxParser(String xmlFileName) {
        this.xmlFileName = xmlFileName;
        beanMap = new HashMap<>();
        interceptorList = new ArrayList<>();
        parseDocument();
    }
    
    private void parseDocument() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(xmlFileName, this);
        } catch (ParserConfigurationException e) {
            System.out.println("ParserConfig error");
        } catch (SAXException e) {
            System.out.println("SAXException : xml not well formed");
        } catch (IOException e) {
            System.out.println("IO error");
        }
    }
    
    public String toString() {
        String res = "";
        for (Bean tmpB : beanMap.values()) {
            res += tmpB.toString() + "; ";
        }
        
        return res;
    }
    
    @Override
    public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
        
        if (elementName.equalsIgnoreCase("bean") || elementName.equalsIgnoreCase("interceptor")) {
            beanTmp = new Bean();
            beanTmp.setName(attributes.getValue("id"));
            beanTmp.setClassName(attributes.getValue("class"));
            String scope = attributes.getValue("scope");
            if(scope != null) {
                beanTmp.setScope(Scope.valueOf(scope.toUpperCase()));
            } else {
                beanTmp.setScope(Scope.SINGLETON);
            }
        }
        
        if (elementName.equalsIgnoreCase("constructor-arg")) {
            beanTmp.getConstructorArg().add(attributes.getValue("type"));
            beanTmp.getConstructorArg().add(attributes.getValue("value"));
        }
        
        if (elementName.equalsIgnoreCase("property")) {
            beanTmp.getProperties().add(attributes.getValue("name"));
            beanTmp.getProperties().add(attributes.getValue("value"));
        }

        if (elementName.equalsIgnoreCase("context:component-scan")) {
            String packageName = attributes.getValue("base-package");
            Reflections reflections = new Reflections(packageName);
//            reflections.getAllTypes().forEach(className -> {
//                try {
//                    Class clazz = Class.forName(className);
//                    Annotation annotation = null;
//                    Arrays.stream(clazz.getAnnotations()).forEach(annotation -> {
//                        if(annotation.annotationType().equals(Component.class) || Arrays.stream(annotation.annotationType().getAnnotations()).anyMatch(annotation1 -> {
//                            if(annotation1.getClass().equals(Component.class))
//                                annotation = annotation1;
//
//                        }))
//                    });
//                } catch (ClassNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
//
//            });
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Component.class);
            for(Class annotatedClass : annotated) {
                Component annotation = (Component) annotatedClass.getAnnotation(Component.class);
                Bean bean = new Bean();
                bean.setScope(annotation.scope());
                bean.setName(annotation.name().equals("") ? annotatedClass.getCanonicalName() : annotation.name());
                bean.setClassName(annotatedClass.getCanonicalName());
                beanMap.put(bean.getName(), bean);
            }

            Set<Class<?>> annotated1 = reflections.getTypesAnnotatedWith(Service.class);
            for(Class annotatedClass : annotated1) {
                Service annotation = (Service) annotatedClass.getAnnotation(Service.class);
                Bean bean = new Bean();
                bean.setScope(annotation.scope());
                bean.setName(annotation.name().equals("") ? annotatedClass.getCanonicalName() : annotation.name());
                bean.setClassName(annotatedClass.getCanonicalName());
                beanMap.put(bean.getName(), bean);
            }

            Set<Class<?>> annotated2 = reflections.getTypesAnnotatedWith(Controller.class);
            for(Class annotatedClass : annotated2) {
                Controller annotation = (Controller) annotatedClass.getAnnotation(Controller.class);
                Bean bean = new Bean();
                bean.setScope(annotation.scope());
                bean.setName(annotation.name().equals("") ? annotatedClass.getCanonicalName() : annotation.name());
                bean.setClassName(annotatedClass.getCanonicalName());
                beanMap.put(bean.getName(), bean);
            }

            Set<Class<?>> annotated3 = reflections.getTypesAnnotatedWith(Repository.class);
            for(Class annotatedClass : annotated3) {
                Repository annotation = (Repository) annotatedClass.getAnnotation(Repository.class);
                Bean bean = new Bean();
                bean.setScope(annotation.scope());
                bean.setName(annotation.name().equals("") ? annotatedClass.getCanonicalName() : annotation.name());
                bean.setClassName(annotatedClass.getCanonicalName());
                beanMap.put(bean.getName(), bean);
            }
        }
    }
    
    @Override
    public void endElement(String s, String s1, String element) throws SAXException {
        if (element.equals("bean")) {
            beanMap.put(beanTmp.getName() != null ? beanTmp.getName() : beanTmp.getClassName(), beanTmp);
        }
        
        if (element.equals("interceptor")) {
            interceptorList.add(beanTmp);
        }
    }
    
    @Override
    public void characters(char[] ac, int i, int j) throws SAXException {
        tmpValue = new String(ac, i, j);
    }    
}
