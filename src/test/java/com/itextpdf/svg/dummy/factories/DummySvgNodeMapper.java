package com.itextpdf.svg.dummy.factories;

import com.itextpdf.svg.dummy.renderers.impl.DummyArgumentedConstructorSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummyProcessableSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummyProtectedSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummySvgNodeMapper implements ISvgNodeRendererMapper {

    @Override
    public Map<String, Class<? extends ISvgNodeRenderer>> getMapping() {
        Map<String, Class<? extends ISvgNodeRenderer>> result = new HashMap<>();
        result.put("dummy", DummySvgNodeRenderer.class);
        result.put("processable", DummyProcessableSvgNodeRenderer.class);
        result.put("protected", DummyProtectedSvgNodeRenderer.class);
        result.put("argumented", DummyArgumentedConstructorSvgNodeRenderer.class);
        return result;
    }

    @Override
    public List<String> getIgnoredTags() {
        return new ArrayList<>();
    }
}
