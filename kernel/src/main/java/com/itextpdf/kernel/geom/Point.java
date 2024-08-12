/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  This code was originally part of the Apache Harmony project.
 *  The Apache Harmony project has been discontinued.
 *  That's why we imported the code into iText.
 */
/**
 * @author Denis M. Kishenko
 */
package com.itextpdf.kernel.geom;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.util.HashCode;

/**
 * Class that represent point object with x and y coordinates.
 */
public class Point implements Cloneable {
    private double x;
    private double y;

    /**
     * Instantiates a new {@link Point} instance with 0 x and y.
     */
    public Point() {
        setLocation(0, 0);
    }

    /**
     * Instantiates a new {@link Point} instance based on passed x and y.
     *
     * @param x the x coordinates of the point
     * @param y the y coordinates of the point
     */
    public Point(double x, double y) {
        setLocation(x, y);
    }

    /**
     * Instantiates a new {@link Point} instance based on another point.
     *
     * @param p the point which will be copied
     */
    public Point(Point p) {
        setLocation(p.x, p.y);
    }

    /**
     * Gets x coordinate of the point.
     *
     * @return the x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets y coordinate of the point.
     *
     * @return the y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Gets location of point by creating a new copy.
     *
     * @return the copy of this point
     */
    public Point getLocation() {
        return new Point(x, y);
    }

    /**
     * Sets x and y double coordinates of the point.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setLocation(double x, double y) {
    	this.x = x;
    	this.y = y;
    }

    /**
     * Moves the point by the specified offset.
     *
     * @param dx the x-axis offset
     * @param dy the y-axis offset
     */
    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    /**
     * The distance between this point and the second point which is defined by passed x and y coordinates.
     *
     * @param px the x coordinate of the second point
     * @param py the y coordinate of the second point
     *
     * @return the distance between points
     */
    public double distance(double px, double py) {
        return Math.sqrt(distanceSq(getX(), getY(), px, py));
    }

    /**
     * The distance between this point and the second point.
     *
     * @param p the second point to calculate distance
     *
     * @return the distance between points
     */
    public double distance(Point p) {
        return distance(p.getX(), p.getY());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Point) {
            Point p = (Point)obj;
            return x == p.x && y == p.y;
        }
        return false;
    }

    @Override
    public String toString() {
        return MessageFormatUtil.format("Point: [x={0},y={1}]", x, y);
    }

    @Override
    public int hashCode() {
        HashCode hash = new HashCode();
        hash.append(getX());
        hash.append(getY());
        return hash.hashCode();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Object clone() {
        return new Point(x, y);
    }

    private static double distanceSq(double x1, double y1, double x2, double y2) {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }
}

