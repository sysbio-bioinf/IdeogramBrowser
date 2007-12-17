/*
 * File:	RLibWrapperReader.java
 * Created: 10.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.gui;

import ideogram.r.annotations.Analysis;
import ideogram.r.annotations.RBoolParam;
import ideogram.r.annotations.RDsNameParam;
import ideogram.r.annotations.RNumericParam;
import ideogram.r.annotations.RStringParam;
import ideogram.r.rlibwrappers.RLibraryWrapper;

import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class RLibWrapperReader {
    
    private RInterfacePanelBuilder builder;
    private Class<?> rLibraryWrapperClass;
    private MessageDisplay mdp;
    
    public RLibWrapperReader() {
        this(new EmptyInterfaceBuilder(null), null);
    }
    
    public RLibWrapperReader(MessageDisplay mdp) {
        this(new EmptyInterfaceBuilder(mdp), mdp);
    }
    
    public RLibWrapperReader(RInterfacePanelBuilder builder, MessageDisplay mdp) {
        rLibraryWrapperClass = RLibraryWrapper.class;
        this.builder = builder;
        this.mdp = mdp;
    }
    
    public void setBuilder(RInterfacePanelBuilder builder) {
        this.builder = builder;
    }
        
    public Component createInputPanel() {
        Component ret = null;
        try {
            ret = createInputPanel(null);
        } catch (Exception e) {
            // Should never happen!
            e.printStackTrace();
            System.exit(1);
        }
        return ret;
    }
    
    /**
     * Create the input panel. Pass null, if you want an empty input panel.
     *
     * @param wrapperClass
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     */
    public Component createInputPanel(RLibraryWrapper wrapper) 
    throws ClassNotFoundException, IllegalArgumentException {
        Class<?> c;
        ArrayList<Method> analysisMethods;
        ArrayList<Field> annotatedFields;
        Analysis aAnalysis;
        
        /*
         * Create an empty panel if no wrapper class is specified!
         */
        if (wrapper == null) {
            RInterfacePanelBuilder b;
            if (builder instanceof EmptyInterfaceBuilder) b = builder;
            else b = new EmptyInterfaceBuilder(null);
            b.createNewRInterfacePanel(null);
            return b.getRInterfacePanel();
        }
        c = wrapper.getClass();
        
        /*
         * DO NOT THROW ANY EXCEPTIONS BEFORE THIS LINE!
         */
        
//        if (!isPropperWrapper(c)) {
//            throw new IllegalArgumentException(c.toString() + 
//                    "does not implement the interface " + 
//                    rLibraryWrapperClass.toString());
//        }
                
        if((analysisMethods = getAnalysisMethods(c)).isEmpty()) {
            throw new IllegalArgumentException(c.toString() + "" +
            		"must implement at least one method annotated with " +
            		"@Analysis!");
        }
        
            builder.createNewRInterfacePanel(wrapper);
            annotatedFields = getAnnotatedFields(c);

            for(Method m: analysisMethods) {
                aAnalysis = (Analysis)m.getAnnotation(Analysis.class);
                builder.createAnalysisInterface(aAnalysis.value());
                callBuildMethods(annotatedFields, aAnalysis.value(), wrapper);
            }
        
        return builder.getRInterfacePanel();
    }
    
    /*
     * Get all public fields that are annotated with an @Analysis annotation.
     * The list will be sorted according to the name of the R function.
     */
    private ArrayList<Field> getAnnotatedFields(Class<?> c) {
        ArrayList<Field> ret = new ArrayList<Field>();
        Field[] fields = c.getFields();
        
        for (Field f: fields) {
            if (f.getAnnotation(Analysis.class) != null) {
                ret.add(f);
            }
        }
        Collections.sort(ret, new AnnotationComparator<Field>());
        return ret;
    }
    
    /*
     * Call the RInterfacePanelBuilder's build methods.
     */
    private void callBuildMethods(ArrayList<Field> annotatedFields, 
            String funcname, RLibraryWrapper wrapper) 
    throws IllegalArgumentException {
        Analysis aAnalysis;
        RBoolParam aBool;
        RDsNameParam aDsName;
        RNumericParam aNumeric;
        RStringParam aString;
        
        Field f;
        for (Iterator<Field> it = annotatedFields.iterator(); it.hasNext(); ) {
            f = it.next();
            aAnalysis = f.getAnnotation(Analysis.class);
            if (!aAnalysis.value().equals(funcname)) {
                // No more annotated fields for this function left.
                break;
            }
            aBool = f.getAnnotation(RBoolParam.class);
            aDsName = f.getAnnotation(RDsNameParam.class);
            aNumeric = f.getAnnotation(RNumericParam.class);
            aString = f.getAnnotation(RStringParam.class);
            
            if (aBool != null) {
                builder.buildRBoolInput(aBool.name(), f, aBool.mandatory());
            }
            else if (aDsName != null) {
                builder.buildRDsNameInput(aDsName.name(), f, aDsName.mandatory());
            }
            else if (aNumeric != null) {
                builder.buildRNumericInput(aNumeric.name(), f, aNumeric.mandatory());
            }
            else if (aString != null) {
                builder.buildRStringInput(aString.name(), f, aString.mandatory());
            }
            
            // Remove f from annotated fields.
            it.remove();
        }
    }
    
    
    /*
     * Get all methods annotated with @Analysis. Sort the list according to 
     * the function names.
     */
    private ArrayList<Method> getAnalysisMethods(Class c) {
        Method[] allMethods = c.getDeclaredMethods();
        ArrayList<Method> ret = new ArrayList<Method>();
        for (Method m: allMethods) {
            if(m.getAnnotation(Analysis.class) != null) {
                ret.add(m);
            }
        }
        Collections.sort(ret, new AnnotationComparator<Method>());
        return ret;
    }
    
    /*
     * Check whether c or one of its super classes implement the interface
     * RLibraryWrapper.
     */
    private boolean isPropperWrapper(Class<?> c) {
        while (c != null) {
            Type[] intfs = c.getGenericInterfaces();
            if (contains(intfs, rLibraryWrapperClass)) {
                return true;
            }
            c = c.getSuperclass();
        }
        return false;
    }
    
    /*
     * Check whether arr contains t.
     */
    private boolean contains(Type[] arr, Type t) {
        if (arr.length != 0) {
            /* arr is not sorted. Sorting arr would be of 
             * complexity O(n * log n). Performing a binary search then would
             * be of complexity O(log n). As a search for t is only needed 
             * once, it is cheaper to perform a linear search with complexity
             * O(n).
             */
            for (Type t1: arr) {
                if (t1.equals(t)) return true;
            }
        }
        return false;
    }
    
    private class AnnotationComparator<T extends AnnotatedElement> 
    implements Comparator<T> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         * 
         * NOTE: o1 and o2 MUST be annotated with Analysis!!!
         */
        public int compare(T o1, T o2) {
            Analysis a1, a2;
            a1 = o1.getAnnotation(Analysis.class);
            a2 = o2.getAnnotation(Analysis.class);
            return a1.value().compareTo(a2.value());
        }
        
    }
}
