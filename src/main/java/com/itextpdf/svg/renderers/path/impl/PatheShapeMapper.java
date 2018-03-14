package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer;
import com.itextpdf.svg.renderers.path.IPathShape;
import com.itextpdf.svg.renderers.path.IPathShapeMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of {@link IPathShapeMapper} that will be used by
 * sub classes of {@link PathSvgNodeRenderer To map the path-data
 * instructions(moveto, lineto, corveto ...) to their respective implementations.
 */
public class PatheShapeMapper implements IPathShapeMapper {
    @Override
    public Map<String, IPathShape> getMapping() {
        Map<String, IPathShape> result = new HashMap<>();
        result.put( SvgTagConstants.PATH_DATA_LINE_TO, new LineTo() );
        result.put( SvgTagConstants.PATH_DATA_MOVE_TO, new MoveTo(  ) );
        result.put( SvgTagConstants.PATH_DATA_CURVE_TO, new CurveTo(  ));
        result.put( SvgTagConstants.PATH_DATA_QUARD_CURVE_TO, new QuadraticCurveTo() );
        result.put( SvgTagConstants.PATH_DATA_CLOSE_PATH, new ClosePath() );
        result.put( SvgTagConstants.PATH_DATA_CURVE_TO_S, new SmoothSCurveTo() );
        return result;
    }
}
