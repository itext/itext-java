/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.select;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.jsoup.helper.Validate;
import com.itextpdf.styledxmlparser.jsoup.internal.Normalizer;
import com.itextpdf.styledxmlparser.jsoup.nodes.Comment;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.DocumentType;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.Node;
import com.itextpdf.styledxmlparser.jsoup.nodes.PseudoTextElement;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.jsoup.nodes.XmlDeclaration;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Evaluates that an element matches the selector.
 */
public abstract class Evaluator {
    protected Evaluator() {
    }

    /**
     * Test if the element meets the evaluator's requirements.
     *
     * @param root    Root of the matching subtree
     * @param element tested element
     * @return Returns <tt>true</tt> if the requirements are met or
     * <tt>false</tt> otherwise
     */
    public abstract boolean matches(Element root, Element element);

    /**
     * Evaluator for tag name
     */
    public static final class Tag extends Evaluator {
        private final String tagName;

        public Tag(String tagName) {
            this.tagName = tagName;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return (element.normalName().equals(tagName));
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("{0}", tagName);
        }
    }


    /**
     * Evaluator for tag name that ends with
     */
    public static final class TagEndsWith extends Evaluator {
        private final String tagName;

        public TagEndsWith(String tagName) {
            this.tagName = tagName;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return (element.normalName().endsWith(tagName));
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("{0}", tagName);
        }
    }

    /**
     * Evaluator for element id
     */
    public static final class Id extends Evaluator {
        private final String id;

        public Id(String id) {
            this.id = id;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return (id.equals(element.id()));
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("#{0}", id);
        }

    }

    /**
     * Evaluator for element class
     */
    public static final class Class extends Evaluator {
        private final String className;

        public Class(String className) {
            this.className = className;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return (element.hasClass(className));
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format(".{0}", className);
        }

    }

    /**
     * Evaluator for attribute name matching
     */
    public static final class Attribute extends Evaluator {
        private final String key;

        public Attribute(String key) {
            this.key = key;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key);
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("[{0}]", key);
        }

    }

    /**
     * Evaluator for attribute name prefix matching
     */
    public static final class AttributeStarting extends Evaluator {
        private final String keyPrefix;

        public AttributeStarting(String keyPrefix) {
            Validate.notEmpty(keyPrefix);
            this.keyPrefix = Normalizer.lowerCase(keyPrefix);
        }

        @Override
        public boolean matches(Element root, Element element) {
            List<com.itextpdf.styledxmlparser.jsoup.nodes.Attribute> values = element.attributes().asList();
            for (com.itextpdf.styledxmlparser.jsoup.nodes.Attribute attribute : values) {
                if (Normalizer.lowerCase(attribute.getKey()).startsWith(keyPrefix))
                    return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("[^{0}]", keyPrefix);
        }

    }

    /**
     * Evaluator for attribute name/value matching
     */
    public static final class AttributeWithValue extends AttributeKeyPair {
        public AttributeWithValue(String key, String value) {
            super(key, value);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key) && value.equalsIgnoreCase(element.attr(key).trim());
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("[{0}={1}]", key, value);
        }

    }

    /**
     * Evaluator for attribute name != value matching
     */
    public static final class AttributeWithValueNot extends AttributeKeyPair {
        public AttributeWithValueNot(String key, String value) {
            super(key, value);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return !value.equalsIgnoreCase(element.attr(key));
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("[{0}!={1}]", key, value);
        }

    }

    /**
     * Evaluator for attribute name/value matching (value prefix)
     */
    public static final class AttributeWithValueStarting extends AttributeKeyPair {
        public AttributeWithValueStarting(String key, String value) {
            super(key, value, false);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key) && Normalizer.lowerCase(element.attr(key)).startsWith(value); // value is lower case already
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("[{0}^={1}]", key, value);
        }

    }

    /**
     * Evaluator for attribute name/value matching (value ending)
     */
    public static final class AttributeWithValueEnding extends AttributeKeyPair {
        public AttributeWithValueEnding(String key, String value) {
            super(key, value, false);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key) && Normalizer.lowerCase(element.attr(key)).endsWith(value); // value is lower case
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("[{0}$={1}]", key, value);
        }

    }

    /**
     * Evaluator for attribute name/value matching (value containing)
     */
    public static final class AttributeWithValueContaining extends AttributeKeyPair {
        public AttributeWithValueContaining(String key, String value) {
            super(key, value);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key) && Normalizer.lowerCase(element.attr(key)).contains(value); // value is lower case
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("[{0}*={1}]", key, value);
        }

    }

    /**
     * Evaluator for attribute name/value matching (value regex matching)
     */
    public static final class AttributeWithValueMatching extends Evaluator {
        String key;
        Pattern pattern;

        public AttributeWithValueMatching(String key, Pattern pattern) {
            this.key = Normalizer.normalize(key);
            this.pattern = pattern;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key) && pattern.matcher(element.attr(key)).find();
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("[{0}~={1}]", key, pattern.toString());
        }

    }

    /**
     * Abstract evaluator for attribute name/value matching
     */
    public abstract static class AttributeKeyPair extends Evaluator {
        String key;
        String value;

        public AttributeKeyPair(String key, String value) {
            this(key, value, true);
        }

        public AttributeKeyPair(String key, String value, boolean trimValue) {
            Validate.notEmpty(key);
            Validate.notEmpty(value);

            this.key = Normalizer.normalize(key);
            boolean isStringLiteral = value.startsWith("'") && value.endsWith("'")
                                        || value.startsWith("\"") && value.endsWith("\"");
            if (isStringLiteral) {
                value = value.substring(1, value.length()-1);
            }

            this.value = trimValue ? Normalizer.normalize(value) : Normalizer.normalize(value, isStringLiteral);
        }
    }

    /**
     * Evaluator for any / all element matching
     */
    public static final class AllElements extends Evaluator {

        @Override
        public boolean matches(Element root, Element element) {
            return true;
        }

        @Override
        public String toString() {
            return "*";
        }
    }

    /**
     * Evaluator for matching by sibling index number (e {@literal <} idx)
     */
    public static final class IndexLessThan extends IndexEvaluator {
        public IndexLessThan(int index) {
            super(index);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return root != element && element.elementSiblingIndex() < index;
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format(":lt({0})", index);
        }

    }

    /**
     * Evaluator for matching by sibling index number (e {@literal >} idx)
     */
    public static final class IndexGreaterThan extends IndexEvaluator {
        public IndexGreaterThan(int index) {
            super(index);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.elementSiblingIndex() > index;
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format(":gt({0})", index);
        }

    }

    /**
     * Evaluator for matching by sibling index number (e = idx)
     */
    public static final class IndexEquals extends IndexEvaluator {
        public IndexEquals(int index) {
            super(index);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.elementSiblingIndex() == index;
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format(":eq({0})", index);
        }

    }

    /**
     * Evaluator for matching the last sibling (css :last-child)
     */
    public static final class IsLastChild extends Evaluator {
		@Override
		public boolean matches(Element root, Element element) {
			final Element p = (Element) element.parent();
			return p != null && !(p instanceof Document) && element.elementSiblingIndex() == p.children().size()-1;
		}

		@Override
		public String toString() {
			return ":last-child";
		}
    }

    public static final class IsFirstOfType extends IsNthOfType {
		public IsFirstOfType() {
			super(0,1);
		}
		@Override
		public String toString() {
			return ":first-of-type";
		}
    }

    public static final class IsLastOfType extends IsNthLastOfType {
		public IsLastOfType() {
			super(0,1);
		}
		@Override
		public String toString() {
			return ":last-of-type";
		}
    }


    public static abstract class CssNthEvaluator extends Evaluator {
    	protected final int a, b;

    	public CssNthEvaluator(int a, int b) {
    		this.a = a;
    		this.b = b;
    	}
    	public CssNthEvaluator(int b) {
    		this(0,b);
    	}

    	@Override
    	public boolean matches(Element root, Element element) {
    		final Element p = (Element) element.parent();
    		if (p == null || (p instanceof Document)) return false;

    		final int pos = calculatePosition(root, element);
    		if (a == 0) return pos == b;

    		return (pos-b)*a >= 0 && (pos-b)%a==0;
    	}

		@Override
		public String toString() {
			if (a == 0)
				return MessageFormatUtil.format(":{0}({1})",getPseudoClass(), b);
			if (b == 0)
				return MessageFormatUtil.format(":{0}({1}n)",getPseudoClass(), a);
			return MessageFormatUtil.format(":{0}({1}n{2})", getPseudoClass(),a, b);
		}

		protected abstract String getPseudoClass();
		protected abstract int calculatePosition(Element root, Element element);
    }


    /**
     * css-compatible Evaluator for :eq (css :nth-child)
     *
     * @see IndexEquals
     */
    public static final class IsNthChild extends CssNthEvaluator {

    	public IsNthChild(int a, int b) {
    		super(a,b);
		}

		protected int calculatePosition(Element root, Element element) {
			return element.elementSiblingIndex()+1;
		}


		protected String getPseudoClass() {
			return "nth-child";
		}
    }

    /**
     * css pseudo class :nth-last-child)
     *
     * @see IndexEquals
     */
    public static final class IsNthLastChild extends CssNthEvaluator {
    	public IsNthLastChild(int a, int b) {
    		super(a,b);
    	}

        @Override
        protected int calculatePosition(Element root, Element element) {
            Element parent = (Element) element.parent();
            if (parent == null)
    	        return 0;
        	return parent.children().size() - element.elementSiblingIndex();
        }

		@Override
		protected String getPseudoClass() {
			return "nth-last-child";
		}
    }

    /**
     * css pseudo class nth-of-type
     *
     */
    public static class IsNthOfType extends CssNthEvaluator {
    	public IsNthOfType(int a, int b) {
    		super(a,b);
    	}

		protected int calculatePosition(Element root, Element element) {
			int pos = 0;
            Element parent = (Element) element.parent();
            if (parent == null)
                return 0;
        	Elements family = parent.children();
            for (Element el : family) {
                if (el.tag().equals(element.tag())) pos++;
                if (el == element) break;
            }
			return pos;
		}

		@Override
		protected String getPseudoClass() {
			return "nth-of-type";
		}
    }

    public static class IsNthLastOfType extends CssNthEvaluator {

		public IsNthLastOfType(int a, int b) {
			super(a, b);
		}

		@Override
		protected int calculatePosition(Element root, Element element) {
			int pos = 0;
            Element parent = (Element) element.parent();
            if (parent == null)
                return 0;
        	Elements family = parent.children();
        	for (int i = element.elementSiblingIndex(); i < family.size(); i++) {
        		if (family.get(i).tag().equals(element.tag())) pos++;
        	}
			return pos;
		}

		@Override
		protected String getPseudoClass() {
			return "nth-last-of-type";
		}
    }

    /**
     * Evaluator for matching the first sibling (css :first-child)
     */
    public static final class IsFirstChild extends Evaluator {
    	@Override
    	public boolean matches(Element root, Element element) {
    		final Element p = (Element) element.parent();
    		return p != null && !(p instanceof Document) && element.elementSiblingIndex() == 0;
    	}

    	@Override
    	public String toString() {
    		return ":first-child";
    	}
    }

    /**
     * css3 pseudo-class :root
     * @see <a href="http://www.w3.org/TR/selectors/#root-pseudo">:root selector</a>
     *
     */
    public static final class IsRoot extends Evaluator {
    	@Override
    	public boolean matches(Element root, Element element) {
    		final Element r = root instanceof Document?root.child(0):root;
    		return element == r;
    	}
    	@Override
    	public String toString() {
    		return ":root";
    	}
    }

    public static final class IsOnlyChild extends Evaluator {
		@Override
		public boolean matches(Element root, Element element) {
			final Element p = (Element) element.parent();
			return p!=null && !(p instanceof Document) && element.siblingElements().isEmpty();
		}
    	@Override
    	public String toString() {
    		return ":only-child";
    	}
    }

    public static final class IsOnlyOfType extends Evaluator {
		@Override
		public boolean matches(Element root, Element element) {
			final Element p = (Element) element.parent();
			if (p==null || p instanceof Document) return false;

			int pos = 0;
        	Elements family = p.children();
            for (Element el : family) {
                if (el.tag().equals(element.tag())) pos++;
            }
        	return pos == 1;
		}
    	@Override
    	public String toString() {
    		return ":only-of-type";
    	}
    }

    public static final class IsEmpty extends Evaluator {
		@Override
		public boolean matches(Element root, Element element) {
        	List<Node> family = element.childNodes();
            for (Node n : family) {
                if (!(n instanceof Comment || n instanceof XmlDeclaration || n instanceof DocumentType)) return false;
            }
        	return true;
		}
    	@Override
    	public String toString() {
    		return ":empty";
    	}
    }

    /**
     * Abstract evaluator for sibling index matching
     */
    public abstract static class IndexEvaluator extends Evaluator {
        int index;

        public IndexEvaluator(int index) {
            this.index = index;
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) text
     */
    public static final class ContainsText extends Evaluator {
        private final String searchText;

        public ContainsText(String searchText) {
            this.searchText = Normalizer.lowerCase(searchText);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return Normalizer.lowerCase(element.text()).contains(searchText);
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format(":contains({0})", searchText);
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) data
     */
    public static final class ContainsData extends Evaluator {
        private final String searchText;

        public ContainsData(String searchText) {
            this.searchText = Normalizer.lowerCase(searchText);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return Normalizer.lowerCase(element.data()).contains(searchText);
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format(":containsData({0})", searchText);
        }
    }

    /**
     * Evaluator for matching Element's own text
     */
    public static final class ContainsOwnText extends Evaluator {
        private final String searchText;

        public ContainsOwnText(String searchText) {
            this.searchText = Normalizer.lowerCase(searchText);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return Normalizer.lowerCase(element.ownText()).contains(searchText);
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format(":containsOwn({0})", searchText);
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) text with regex
     */
    public static final class Matches extends Evaluator {
        private final Pattern pattern;

        public Matches(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean matches(Element root, Element element) {
            Matcher m = pattern.matcher(element.text());
            return m.find();
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format(":matches({0})", pattern);
        }
    }

    /**
     * Evaluator for matching Element's own text with regex
     */
    public static final class MatchesOwn extends Evaluator {
        private final Pattern pattern;

        public MatchesOwn(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean matches(Element root, Element element) {
            Matcher m = pattern.matcher(element.ownText());
            return m.find();
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format(":matchesOwn({0})", pattern);
        }
    }

    public static final class MatchText extends Evaluator {

        @Override
        public boolean matches(Element root, Element element) {
            if (element instanceof PseudoTextElement)
                return true;

            List<TextNode> textNodes = element.textNodes();
            for (TextNode textNode : textNodes) {
                PseudoTextElement pel = new PseudoTextElement(
                    com.itextpdf.styledxmlparser.jsoup.parser.Tag.valueOf(element.tagName()), element.baseUri(), element.attributes());
                textNode.replaceWith(pel);
                pel.appendChild(textNode);
            }
            return false;
        }

        @Override
        public String toString() {
            return ":matchText";
        }
    }
}
