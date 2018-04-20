/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.path.DefaultSvgPathShapeFactory;
import com.itextpdf.svg.renderers.path.IPathShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;path&gt; tag.
 */
public class PathSvgNodeRenderer extends AbstractSvgNodeRenderer {

    private static final String SEPERATOR = "";
    private static final String SPACE_CHAR = " ";

    @Override
    public void doDraw(SvgDrawContext context) {
        PdfCanvas canvas = context.getCurrentCanvas();

        for (IPathShape item : getShapes()) {
            item.draw( canvas );
        }
    }

    private Collection<IPathShape> getShapes() {

        Collection<String> parsedResults = parsePropertiesAndStyles();
        Collection<IPathShape> shapes = new ArrayList<>();

        for (String parsedResult : parsedResults) {

            //split att to {M , 100, 100}
            String[] pathPropertis = parsedResult.split( SPACE_CHAR );
            if (pathPropertis.length > 0 && !pathPropertis[0].equals( SEPERATOR )) {
                if (pathPropertis[0].equalsIgnoreCase( SvgTagConstants.PATH_DATA_CLOSE_PATH )) {

                    //This may be removed as closePathe could be added inside doDraw method
                    shapes.add( DefaultSvgPathShapeFactory.createPathShape( SvgTagConstants.PATH_DATA_CLOSE_PATH ) );
                } else {
                    //Implements (absolute) command value only
                    //TODO implement relative values e. C(absalute), c(relative)
                    IPathShape pathShape = DefaultSvgPathShapeFactory.createPathShape( pathPropertis[0].toUpperCase() );

                    pathShape.setCoordinates( Arrays.copyOfRange( pathPropertis, 1, pathPropertis.length ) );
                    shapes.add( pathShape );
                }
            }
        }
        return shapes;
    }

    private Collection<String> parsePropertiesAndStyles() {
        StringBuilder result = new StringBuilder();
        String attributes = attributesAndStyles.get( SvgTagConstants.D );
        String closePath = attributes.indexOf( 'z' ) > 0 ? attributes.substring( attributes.indexOf( 'z' ) ) : "".trim();

        if (!closePath.equals( SEPERATOR )) {
            attributes = attributes.replace( closePath, SEPERATOR ).trim();
        }

        String SPLIT_REGEX = "(?=[\\p{L}][^,;])";
        String[] coordinates = attributes.split(SPLIT_REGEX);//gets an array attributesAr of {M 100 100, L 300 100, L200, 300, z}

        for (String inst : coordinates) {
            if (!inst.equals( SEPERATOR )) {
                String instruction = inst.charAt( 0 ) + SPACE_CHAR;
                String temp = instruction + inst.replace( inst.charAt( 0 ) + SEPERATOR, SEPERATOR ).replace( ",", SPACE_CHAR ).trim();
                result.append(SPACE_CHAR).append(temp);
            }
        }

        String[] resultArray = result.toString().split(SPLIT_REGEX);
        List<String> resultList = new ArrayList<>( Arrays.asList( resultArray ) );
        resultList.add( closePath );
        return resultList;
    }
}
