package com.adobe.itext;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class FloatExample {

	private static final String IMAGE_SRC = "layout/src/test/resources/com/adobe/itext/FloatExample/%d.png";
	private static final String DEST = "target/results/FloatExample.pdf";

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new FloatExample().createPdf(DEST);
	}

	// defined range is 0..3
	private static final int FIRST_IMAGE = 1;
	private static final int LAST_IMAGE = 2;

	private static final Color IMAGE_BORDER_COLOR = Color.LIGHT_GRAY;
	private static final float BORDER_MARGIN = 5f;
	private static final float IMAGE_BORDER_WIDTH = 5f;
	private static final float DIV_BORDER_WIDTH = 1f;

	private static final UnitValue OUTER_WIDTH = new UnitValue(UnitValue.PERCENT, 60f);
	//private static final UnitValue DIV_IMAGE_WIDTH = new UnitValue(UnitValue.POINT, 150f);
	private static final UnitValue DIV_IMAGE_WIDTH = new UnitValue(UnitValue.PERCENT, 100f);

	private void createPdf(String dest) throws IOException {
		//Initialize PDF writer
		PdfWriter writer = new PdfWriter(dest);

		//Initialize PDF document
		PdfDocument pdf = new PdfDocument(writer);

		// Initialize document
		Document document = new Document(pdf);
		pdf.setTagged();

		// divWidthProperty, divWidth are n/a when not wrapping image in a div

		document.add(new Paragraph("Actual width of image -- no explicit width, no max.\n"));
		addContent(document, false, 0, null, 0, null, ClearPropertyValue.BOTH);
		document.add(new AreaBreak());

		document.add(new Paragraph("Width < actual width.\n"));
		addContent(document, false, Property.WIDTH, new UnitValue(UnitValue.PERCENT, 30f), 0, null, ClearPropertyValue.BOTH);
		document.add(new AreaBreak());

		document.add(new Paragraph("Max width < actual width.\n"));
		addContent(document, false, Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 30f), 0, null, ClearPropertyValue.BOTH);
		document.add(new AreaBreak());

		document.add(new Paragraph("Max width > actual width.\n"));
		addContent(document, false, Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 60f), 0, null, ClearPropertyValue.BOTH);
		document.add(new AreaBreak());

		// Image wrapped in div

		// Width of Paragraph inside Div if width of parent
		document.add(new Paragraph("No explicit width or max: Non-floating text width is parent width.\n"));
		addContent(document, true, 0, null, 0, null, ClearPropertyValue.BOTH);
		document.add(new AreaBreak());

		document.add(new Paragraph("Bug: Non-floating text width is parent width (limited by max).\n"));
		addContent(document, true, Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 100f),
				Property.WIDTH, new UnitValue(UnitValue.PERCENT, 30f), ClearPropertyValue.BOTH);
		document.add(new AreaBreak());

		document.add(new Paragraph("Max width < actual width.\n"));
		addContent(document, true, Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 100f),
				Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 30f), ClearPropertyValue.BOTH);
		document.add(new AreaBreak());

		document.add(new Paragraph("Bug: Non-floating text width is parent width (limited by max).\nMax width > actual width.\n"));
		addContent(document, true, Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 100f),
				Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 60f), ClearPropertyValue.BOTH);

		document.close();
	}

	private void addContent(Document document, boolean wrapImages,
	                        int imageWidthProperty, UnitValue imageWidth,
	                        int divWidthProperty, UnitValue divWidth,
	                        ClearPropertyValue clearValue)
			throws MalformedURLException {

		ImageProperties[] images = new ImageProperties[4];
		images[0] = new ImageProperties(FloatPropertyValue.NONE, clearValue, HorizontalAlignment.CENTER);
		images[1] = new ImageProperties(FloatPropertyValue.NONE, clearValue, HorizontalAlignment.CENTER);
		images[2] = new ImageProperties(FloatPropertyValue.RIGHT, clearValue, HorizontalAlignment.CENTER);
		images[3] = new ImageProperties(FloatPropertyValue.RIGHT, clearValue, HorizontalAlignment.CENTER);
		Paragraph paragraph = new Paragraph()
				.add("Four images followed by two paragraphs.\n");
		if (wrapImages) {
			String s = "Each image is wrapped in a div.\n";
			s += "All divs specify CLEAR = " + clearValue;
			if (divWidthProperty > 0)
				s += ", " + ((divWidthProperty == Property.WIDTH) ? "WIDTH" : "MAX_WIDTH") + "= " + divWidth;
			if (imageWidthProperty > 0)
				s += ".\nAll images specify " + ((imageWidthProperty == Property.WIDTH) ? "WIDTH" : "MAX_WIDTH") + " = " + imageWidth;
			paragraph.add(s + ".\n");
		}
		else {
			String s = "All images specify CLEAR = " + clearValue;
			if (imageWidthProperty > 0)
				s += ", " + ((imageWidthProperty == Property.WIDTH) ? "WIDTH" : "MAX_WIDTH") + "= " + imageWidth;
			paragraph.add(s + ".\n");
		}
		for (int i = FIRST_IMAGE; i <= LAST_IMAGE; i++) {
			paragraph.add((wrapImages ? "Div" : "Image") + " " + (i) + ": " + images[i] + "\n");
		}
		document.add(paragraph);

		for (int i = FIRST_IMAGE; i <= LAST_IMAGE; i++) {
			Image image = new Image(ImageDataFactory.create(String.format(IMAGE_SRC, i + 1)))
					.setBorder(new SolidBorder(IMAGE_BORDER_COLOR, IMAGE_BORDER_WIDTH))
					.setHorizontalAlignment(images[i].horizontalAlignment);
			if (wrapImages) {
				Div div = new Div()
						.setBorder(new SolidBorder(DIV_BORDER_WIDTH))
						.setMargins(BORDER_MARGIN, 0, BORDER_MARGIN, BORDER_MARGIN);
				div.setHorizontalAlignment(images[i].horizontalAlignment);
				div.setProperty(Property.CLEAR, images[i].clearPropertyValue);
				div.setProperty(Property.FLOAT, images[i].floatPropertyValue);
				if (divWidthProperty > 0)
					div.setProperty(divWidthProperty, divWidth);
				if (imageWidthProperty > 0)
					image.setProperty(imageWidthProperty, imageWidth);
				div.add(image);
				div.add(new Paragraph("Figure " + i + ": This is longer text that wraps")
						.setTextAlignment(TextAlignment.CENTER)).setBold();
				document.add(div);
			} else {
				image.setMargins(BORDER_MARGIN, 0, BORDER_MARGIN, BORDER_MARGIN);
				image.setProperty(Property.CLEAR, images[i].clearPropertyValue);
				image.setProperty(Property.FLOAT, images[i].floatPropertyValue);
				if (imageWidthProperty > 0)
					image.setProperty(imageWidthProperty, imageWidth);
				document.add(image);
			}
		}

		document.add(new Paragraph("The following outline is provided as an over-view of and topical guide to Zambia:"));
		document.add(new Paragraph("Zambia – landlocked sovereign country located in Southern Africa.[1] Zambia has been inhabited for thousands of years by hunter-gatherers and migrating tribes. After sporadic visits by European explorers starting in the 18th century, Zambia was gradually claimed and occupied by the British as protectorate of Northern Rhodesia towards the end of the nineteenth century. On 24 October 1964, the protectorate gained independence with the new name of Zambia, derived from the Zam-bezi river which flows through the country. After independence the country moved towards a system of one party rule with Kenneth Kaunda as president. Kaunda dominated Zambian politics until multiparty elections were held in 1991."));
	}

	class ImageProperties {

		FloatPropertyValue floatPropertyValue;
		ClearPropertyValue clearPropertyValue;
		HorizontalAlignment horizontalAlignment;

		ImageProperties(FloatPropertyValue floatPropertyValue, ClearPropertyValue clearPropertyValue,
		                HorizontalAlignment horizontalAlignment) {
			this.floatPropertyValue = floatPropertyValue;
			this.clearPropertyValue = clearPropertyValue;
			this.horizontalAlignment = horizontalAlignment;
		}

		public String toString() {
			return "float="+floatPropertyValue + ", clear="+clearPropertyValue + ", horiz_align="+horizontalAlignment;
		}
	}
}
