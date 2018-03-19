package com.itextpdf.svg.renderers.factories;

import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * The default implementation of {@link ISvgNodeRendererFactory} that will be
 * used by default by the entry points defined by this project.
 */
public class DefaultSvgNodeRendererFactory implements ISvgNodeRendererFactory {

    private Map<String, Class<? extends ISvgNodeRenderer>> rendererMap = new HashMap<>();
    private Collection<String> ignoredTags = new HashSet<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSvgNodeRendererFactory.class);

    /**
     * Default constructor which uses the default {@link ISvgNodeRendererMapper}
     * implementation.
     */
    public DefaultSvgNodeRendererFactory() {
        this(new DefaultSvgNodeRendererMapper());
    }

    /**
     * Constructor which allows injecting a custom
     * {@link ISvgNodeRendererMapper} implementation.
     *
     * @param mapper the custom mapper implementation - if null, then we fall
     * back to the {@link DefaultSvgNodeRendererMapper}
     */
    public DefaultSvgNodeRendererFactory(ISvgNodeRendererMapper mapper) {
        if (mapper != null) {
            rendererMap.putAll(mapper.getMapping());
            ignoredTags.addAll(mapper.getIgnoredTags());
        } else {
            ISvgNodeRendererMapper defaultMapper = new DefaultSvgNodeRendererMapper();
            rendererMap.putAll(defaultMapper.getMapping());
            ignoredTags.addAll(defaultMapper.getIgnoredTags());
        }
    }

    @Override
    public ISvgNodeRenderer createSvgNodeRendererForTag(IElementNode tag, ISvgNodeRenderer parent) {
        ISvgNodeRenderer result;
        try {
            if (tag == null) {
                throw new SvgProcessingException(SvgLogMessageConstant.TAGPARAMETERNULL);
            }
            Class<? extends ISvgNodeRenderer> clazz = rendererMap.get(tag.name());
            if (clazz == null) {
                throw new NullPointerException();
            }
            result = (ISvgNodeRenderer) rendererMap.get(tag.name()).newInstance();
        } catch (NullPointerException ex) {
            LOGGER.error(DefaultSvgNodeRendererFactory.class.getName(), ex);
            throw new SvgProcessingException(SvgLogMessageConstant.UNMAPPEDTAG, ex).setMessageParams(tag.name());
        } catch (ReflectiveOperationException ex) {
            LOGGER.error(DefaultSvgNodeRendererFactory.class.getName(), ex);
            throw new SvgProcessingException(SvgLogMessageConstant.COULDNOTINSTANTIATE, ex).setMessageParams(tag.name());
        }
        if (parent != null) {
            result.setParent(parent);
            parent.addChild(result);
        }
        return result;
    }

    @Override
    public boolean isTagIgnored(IElementNode tag){
        return ignoredTags.contains(tag.name());
    }
}
