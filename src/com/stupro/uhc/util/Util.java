package com.stupro.uhc.util;



//import java.lang.reflect.Modifier;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import javafx.geometry.BoundingBox;
//import javafx.geometry.Bounds;
//import javafx.geometry.Point2D;
//import javafx.scene.Node;
//import javafx.scene.Parent;

public class Util {
	//currently not in use
//	/**
//	 * Change the parent of a node.
//	 *
//	 * <p>
//	 * The node should have a common ancestor with the new parent.
//	 * </p>
//	 *
//	 * @param item
//	 *            The node to move.
//	 * @param newParent
//	 *            The new parent.
//	 */
//	@SuppressWarnings("unchecked")
//	public static void changeParent(Node item, Parent newParent) {
//	    try {
//	        // HAve to use reflection, because the getChildren method is protected in common ancestor of all
//	        // parent nodes.
//
//	        // Checking old parent for public getChildren() method
//	        Parent oldParent = item.getParent();
//	        if ((oldParent.getClass().getMethod("getChildren").getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
//	            throw new IllegalArgumentException("Old parent has no public getChildren method.");
//	        }
//	        // Checking new parent for public getChildren() method
//	        if ((newParent.getClass().getMethod("getChildren").getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
//	            throw new IllegalArgumentException("New parent has no public getChildren method.");
//	        }
//
//	        // Finding common ancestor for the two parents
//	        Parent commonAncestor = findCommonAncestor(oldParent, newParent);
//	        if (commonAncestor == null) {
//	            throw new IllegalArgumentException("Item has no common ancestor with the new parent.");
//	        }
//
//	        // Bounds of the item
//	        Bounds itemBoundsInParent = item.getBoundsInParent();
//
//	        // Mapping coordinates to common ancestor
//	        Bounds boundsInParentBeforeMove = localToParentRecursive(oldParent, commonAncestor, itemBoundsInParent);
//
//	        // Swapping parent
//	        ((Collection<Node>) oldParent.getClass().getMethod("getChildren").invoke(oldParent)).remove(item);
//	        ((Collection<Node>) newParent.getClass().getMethod("getChildren").invoke(newParent)).add(item);
//
//	        
//	        // Mapping coordinates back from common ancestor
//	        Bounds boundsInParentAfterMove = parentToLocalRecursive(newParent, commonAncestor, boundsInParentBeforeMove);
//
//	        // Setting new translation
//	        item.setTranslateX(
//	                        item.getTranslateX() + (boundsInParentAfterMove.getMinX() - itemBoundsInParent.getMinX()));
//	        item.setTranslateY(
//	                        item.getTranslateY() + (boundsInParentAfterMove.getMinY() - itemBoundsInParent.getMinY()));
//	    } catch (Exception e) {
//	        throw new IllegalStateException("Error while switching parent.", e);
//	    }
//	}
//
//	/**
//	 * Finds the topmost common ancestor of two nodes.
//	 *
//	 * @param firstNode
//	 *            The first node to check.
//	 * @param secondNode
//	 *            The second node to check.
//	 * @return The common ancestor or null if the two node is on different
//	 *         parental tree.
//	 */
//	public static Parent findCommonAncestor(Node firstNode, Node secondNode) {
//	    // Builds up the set of all ancestor of the first node.
//	    Set<Node> parentalChain = new HashSet<>();
//	    Node cn = firstNode;
//	    while (cn != null) {
//	        parentalChain.add(cn);
//	        cn = cn.getParent();
//	    }
//
//	    // Iterates down through the second ancestor for common node.
//	    cn = secondNode;
//	    while (cn != null) {
//	        if (parentalChain.contains(cn)) {
//	            return (Parent) cn;
//	        }
//	        cn = cn.getParent();
//	    }
//	    return null;
//	}
//
//	/**
//	 * Transitively converts the coordinates from the node to an ancestor's
//	 * coordinate system.
//	 *
//	 * @param node
//	 *            The node the starting coordinates are local to.
//	 * @param ancestor
//	 *            The ancestor to map the coordinates to.
//	 * @param x
//	 *            The X of the point to be converted.
//	 * @param y
//	 *            The Y of the point to be converted.
//	 * @return The converted coordinates.
//	 */
//	public static Point2D localToParentRecursive(Node node, Parent ancestor, double x, double y) {
//	    Point2D p = new Point2D(x, y);
//	    Node cn = node;
//	    while (cn != null) {
//	        if (cn == ancestor) {
//	            return p;
//	        }
//	        p = cn.localToParent(p);
//	        cn = cn.getParent();
//	    }
//	    throw new IllegalStateException("The node is not a descedent of the parent.");
//	}
//
//	/**
//	 * Transitively converts the coordinates of a bound from the node to an
//	 * ancestor's coordinate system.
//	 *
//	 * @param node
//	 *            The node the starting coordinates are local to.
//	 * @param ancestor
//	 *            The ancestor to map the coordinates to.
//	 * @param bounds
//	 *            The bounds to be converted.
//	 * @return The converted bounds.
//	 */
//	public static Bounds localToParentRecursive(Node node, Parent ancestor, Bounds bounds) {
//	    Point2D p = localToParentRecursive(node, ancestor, bounds.getMinX(), bounds.getMinY());
//	    return new BoundingBox(p.getX(), p.getY(), bounds.getWidth(), bounds.getHeight());
//	}
//
//	/**
//	 * Transitively converts the coordinates from an ancestor's coordinate
//	 * system to the nodes local.
//	 *
//	 * @param node
//	 *            The node the resulting coordinates should be local to.
//	 * @param ancestor
//	 *            The ancestor the starting coordinates are local to.
//	 * @param x
//	 *            The X of the point to be converted.
//	 * @param y
//	 *            The Y of the point to be converted.
//	 * @return The converted coordinates.
//	 */
//	public static Point2D parentToLocalRecursive(Node n, Parent parent, double x, double y) {
//	    List<Node> parentalChain = new ArrayList<>();
//	    Node cn = n;
//	    while (cn != null) {
//	        if (cn == parent) {
//	            break;
//	        }
//	        parentalChain.add(cn);
//	        cn = cn.getParent();
//	    }
//	    if (cn == null) {
//	        throw new IllegalStateException("The node is not a descedent of the parent.");
//	    }
//
//	    Point2D p = new Point2D(x, y);
//	    for (int i = parentalChain.size() - 1; i >= 0; i--) {
//	        p = parentalChain.get(i).parentToLocal(p);
//	    }
//
//	    return p;
//	}
//
//	/**
//	 * Transitively converts the coordinates of the bounds from an ancestor's
//	 * coordinate system to the nodes local.
//	 *
//	 * @param node
//	 *            The node the resulting coordinates should be local to.
//	 * @param ancestor
//	 *            The ancestor the starting coordinates are local to.
//	 * @param bounds
//	 *            The bounds to be converted.
//	 * @return The converted coordinates.
//	 */
//	public static Bounds parentToLocalRecursive(Node n, Parent parent, Bounds bounds) {
//	    Point2D p = parentToLocalRecursive(n, parent, bounds.getMinX(), bounds.getMinY());
//	    return new BoundingBox(p.getX(), p.getY(), bounds.getWidth(), bounds.getHeight());
//	}
	
	
	private static String OS = System.getProperty("os.name").toLowerCase();
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }
    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }
    public static boolean isUnix() {
        return (OS.indexOf("nux") >= 0);
    }
	
}

