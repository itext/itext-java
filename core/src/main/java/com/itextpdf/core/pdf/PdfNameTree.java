package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

import java.util.ArrayList;
import java.util.List;

public class PdfNameTree {

    private static final int NodeSize = 40;

    private List<PdfNode> parents;
    private PdfNode root;
    private PdfDocument document;

    /**
     * Creates the NameTree of current Document
     *
     * @param catalog  Document catalog
     * @param treeType the type of tree. Dests Tree, AP Tree etc.
     * @throws PdfException
     */
    public PdfNameTree(PdfCatalog catalog, PdfName treeType) {
        parents = new ArrayList<>();
        document = catalog.getDocument();

        PdfDictionary names = catalog.getPdfObject().getAsDictionary(PdfName.Names);
        if (names != null) {
            PdfDictionary nameTree = names.getAsDictionary(treeType);
            if (nameTree != null) {
                root = new PdfNode(nameTree);
            } else {
                root = new PdfNode(document);
            }
            parents.add(root);
        } else {
            root = new PdfNode(document);
            parents.add(root);
        }
    }

    /**
     * This method creates new name in the NameTree.
     *
     * @param key   PdfString or PdfName key
     * @param value PdfArray as value
     * @throws PdfException
     */
    public void addNewName(PdfObject key, PdfObject value) {
        PdfNode node = parents.get(parents.size() - 1);
        int kidsCount = 0;
        if (node.getKids() != null)
            kidsCount = node.getKids().size();
        if (kidsCount == 0 && node.getNames() != null)
            kidsCount = node.getNames().size();
        if (kidsCount >= NodeSize || (node.getKids() != null && node.getKids().size() != 0)) {
            PdfNode parentNode = parents.get(parents.size() - 1);
            if (parentNode.getPdfObject().containsKey(PdfName.Names)) {
                PdfNode newKidNode = new PdfNode(document);
                PdfArray names = parentNode.getNames();
                for (int i = 0; i < names.size(); i++) {
                    newKidNode.addName(names.get(i), names.get(++i));
                }
                parentNode.addKid(newKidNode);
                parentNode.remove(PdfName.Names);
            }
            node = new PdfNode(document);
            parentNode.addKid(node);
        }

        node.addName(key, value);
        if (root == node) {
            node.remove(PdfName.Limits);
        }
    }

    /**
     * This method generates NameTree
     *
     * @return root
     * @throws PdfException
     */
    public PdfObject generateTree() {
        if (root == null) {
            while (parents.size() != 1) {
                List<PdfNode> nextParents = new ArrayList<PdfNode>();
                PdfNode current = null;
                for (int i = 0; i < parents.size(); i++) {
                    PdfNode node = parents.get(i);
                    if (i % NodeSize == 0) {
                        current = new PdfNode(document);
                        nextParents.add(current);
                    }
                    assert current != null;
                    current.addKid(node);
                }
                parents = nextParents;
            }
            root = parents.get(0);
        }

        return root.getPdfObject();
    }

    public PdfNode getRoot() {
        return root;
    }
}
