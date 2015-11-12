package com.itextpdf.forms.xfa;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Processes the template section in the XFA form.
 */
public  class Xml2SomTemplate extends Xml2Som {
    private boolean dynamicForm;
    private int templateLevel;

    /**
     * Creates a new instance from the datasets node.
     *
     * @param n the template node
     */
    public Xml2SomTemplate(Node n) {
        order = new ArrayList<String>();
        name2Node = new HashMap<String, Node>();
        stack = new Stack2<String>();
        anform = 0;
        templateLevel = 0;
        inverseSearch = new HashMap<String, InverseStore>();
        processTemplate(n, null);
    }

    /**
     * Gets the field type as described in the <CODE>template</CODE> section of the XFA.
     *
     * @param s the exact template name
     * @return the field type or <CODE>null</CODE> if not found
     */
    public String getFieldType(String s) {
        Node n = name2Node.get(s);
        if (n == null)
            return null;
        if ("exclGroup".equals(n.getLocalName()))
            return "exclGroup";
        Node ui = n.getFirstChild();
        while (ui != null) {
            if (ui.getNodeType() == Node.ELEMENT_NODE && "ui".equals(ui.getLocalName())) {
                break;
            }
            ui = ui.getNextSibling();
        }
        if (ui == null)
            return null;
        Node type = ui.getFirstChild();
        while (type != null) {
            if (type.getNodeType() == Node.ELEMENT_NODE && !("extras".equals(type.getLocalName()) && "picture".equals(type.getLocalName()))) {
                return type.getLocalName();
            }
            type = type.getNextSibling();
        }
        return null;
    }

    private void processTemplate(Node n, HashMap<String, Integer> ff) {
        if (ff == null)
            ff = new HashMap<String, Integer>();
        HashMap<String, Integer> ss = new HashMap<String, Integer>();
        Node n2 = n.getFirstChild();
        while (n2 != null) {
            if (n2.getNodeType() == Node.ELEMENT_NODE) {
                String s = n2.getLocalName();
                if ("subform".equals(s)) {
                    Node name = n2.getAttributes().getNamedItem("name");
                    String nn = "#subform";
                    boolean annon = true;
                    if (name != null) {
                        nn = escapeSom(name.getNodeValue());
                        annon = false;
                    }
                    Integer i;
                    if (annon) {
                        i = Integer.valueOf(anform);
                        ++anform;
                    } else {
                        i = ss.get(nn);
                        if (i == null)
                            i = Integer.valueOf(0);
                        else
                            i = Integer.valueOf(i.intValue() + 1);
                        ss.put(nn, i);
                    }
                    stack.push(nn + "[" + i.toString() + "]");
                    ++templateLevel;
                    if (annon)
                        processTemplate(n2, ff);
                    else
                        processTemplate(n2, null);
                    --templateLevel;
                    stack.pop();
                } else if ("field".equals(s) || "exclGroup".equals(s)) {
                    Node name = n2.getAttributes().getNamedItem("name");
                    if (name != null) {
                        String nn = escapeSom(name.getNodeValue());
                        Integer i = ff.get(nn);
                        if (i == null)
                            i = Integer.valueOf(0);
                        else
                            i = Integer.valueOf(i.intValue() + 1);
                        ff.put(nn, i);
                        stack.push(nn + "[" + i.toString() + "]");
                        String unstack = printStack();
                        order.add(unstack);
                        inverseSearchAdd(unstack);
                        name2Node.put(unstack, n2);
                        stack.pop();
                    }
                } else if (!dynamicForm && templateLevel > 0 && "occur".equals(s)) {
                    int initial = 1;
                    int min = 1;
                    int max = 1;
                    Node a = n2.getAttributes().getNamedItem("initial");
                    if (a != null)
                        try {
                            initial = Integer.parseInt(a.getNodeValue().trim());
                        } catch (Exception e) {
                        }
                    a = n2.getAttributes().getNamedItem("min");
                    if (a != null)
                        try {
                            min = Integer.parseInt(a.getNodeValue().trim());
                        } catch (Exception e) {
                        }
                    a = n2.getAttributes().getNamedItem("max");
                    if (a != null)
                        try {
                            max = Integer.parseInt(a.getNodeValue().trim());
                        } catch (Exception e) {
                        }
                    if (initial != min || min != max)
                        dynamicForm = true;
                }
            }
            n2 = n2.getNextSibling();
        }
    }

    /**
     * <CODE>true</CODE> if it's a dynamic form; <CODE>false</CODE>
     * if it's a static form.
     *
     * @return <CODE>true</CODE> if it's a dynamic form; <CODE>false</CODE>
     * if it's a static form
     */
    public boolean isDynamicForm() {
        return dynamicForm;
    }

    /**
     * Sets the dynamic form flag. It doesn't change the template.
     *
     * @param dynamicForm the dynamic form flag
     */
    public void setDynamicForm(boolean dynamicForm) {
        this.dynamicForm = dynamicForm;
    }
}