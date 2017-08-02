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

import com.itextpdf.io.util.HashCode;

import java.io.Serializable;
import com.itextpdf.io.util.MessageFormatUtil;

public class Point implements Serializable, Cloneable {

    private static final long serialVersionUID = -5276940640259749850L;

    public double x;
    public double y;

    public Point() {
        setLocation(0, 0);
    }

    public Point(int x, int y) {
        setLocation(x, y);
    }

    public Point(double x, double y) {
        setLocation(x, y);
    }

    public Point(Point p) {
        setLocation(p.x, p.y);
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
        return MessageFormatUtil.format("Point: [x={0},y={1}]", x, y); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public void setLocation(Point p) {
        setLocation(p.x, p.y);
    }

    public void setLocation(int x, int y) {
        setLocation((double)x, (double)y);
    }

    public void setLocation(double x, double y) {
    	this.x = x;
    	this.y = y;
    }

    public void move(double x, double y) {
        setLocation(x, y);
    }

    public void translate(double dx, double dy) {
        x += dx;
        y += dy;
    }


    @Override
    public int hashCode() {
        HashCode hash = new HashCode();
        hash.append(getX());
        hash.append(getY());
        return hash.hashCode();
    }

    public static double distanceSq(double x1, double y1, double x2, double y2) {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }

    public double distanceSq(double px, double py) {
        return distanceSq(getX(), getY(), px, py);
    }

    public double distanceSq(Point p) {
        return distanceSq(getX(), getY(), p.getX(), p.getY());
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(distanceSq(x1, y1, x2, y2));
    }

    public double distance(double px, double py) {
        return Math.sqrt(distanceSq(px, py));
    }

    public double distance(Point p) {
        return Math.sqrt(distanceSq(p));
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Object clone() {
        return new Point(x, y);
    }
}

