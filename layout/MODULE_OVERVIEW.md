# Layout module overview

Layout is a basic iText module that performs the operations of transforming abstract elements 
(like Paragraph, Table, List) into low-level PDF syntax on actual document pages.

In terms of the content presentation, PDF format only accepts low-level operations, like "draw a character at a given 
position" or "draw a line from (x1,y1) to (x2, y2)". The layout module mostly consists of the **rendering engine** logic, 
which deals with the placement on the page of various **model elements**: calculating the exact element's position on the page 
and constructing drawing operations in PDF syntax.

Consider the mechanism of rendering elements.

### Rendering Engine

#### Property Containers & Layout Objects
At the beginning we will start from the `IPropertyContainer` interface. This interface defines methods to set, get, and 
delete properties. These methods work with generic types. All data used in the elements is stored as a property.

Properties are stored in maps, and you can access them using a special key, which is the property number. 
Why do we store properties in a map, instead of using regular fields? First of all, it saves memory, because there are 
many different types of properties, but each element only works with some part of them. Also, it allows to maintain a 
simple inheritance mechanism when we look for a property in the parent map if cannot find it in the element map. 
A list of all the properties is in the class `com.itextpdf.layout.property.Property`. Some properties are noted as 
inherited and placed in `Property#INHERITED_PROPERTIES` array. This means that when we try to get the property of the element 
by the method `IPropertyContainer#getProperty`, if such property is not in the properties list of the current renderer, 
it will be searched in parents recursively. With the approach when properties are kept in the map, the inheritance 
mechanism is the same for every property: there's no need to implement it over and over again or to use reflection.

This interface `IPropertyContainer` has two direct sub-interfaces: `IElement` and `IRenderer`. The `IElement` interface is 
implemented by classes such as `Text`, `Paragraph` and `Table`. These are the objects that we'll add to a document, either 
directly or indirectly (for example when we add `String` to `Paragraph`, and under the hood, the `Text` element will 
automatically wrap this string). The `IRenderer` interface is implemented by classes such as `TextRenderer`, 
`ParagraphRenderer` and `TableRenderer`. These renderers are used internally by iText, but we can subclass them if we 
want to tweak the way an object is rendered. Each renderer borrows the properties of the corresponding model element: 
it first checks if the property is available in the renderer, then - in the corresponding model element and then 
performs the same check for the parent renderer (if the property is inheritable). **If during layout it's needed to 
override the model element properties, one should set them to the renderer**, because we don't want to pollute the 
model element properties. It is important to separate element model structure and logic which performs actual element 
placement (rendering logic). At the model level, a tree of models is created - this is an abstraction that represents 
the structure of the elements that will be added to the documents. Each of these elements (Paragraph, Image, etc.) can 
be added several times. And the rendering includes the basic logic that fills the PDF with data, obtained from the model 
elements tree. Different renderers can be created for one model element, and the result of the work of these renderers 
will be different, but each renderer has one model element on which it is based.

Let's consider in more detail the mechanism of renderers.

#### Renderers
Renderers have two main responsibilities

- `Layout()` - Calculating the area & position its object takes up on the canvas. `Layout()` can work with different input 
parameters and properties, and it's explicitly allowed to call layout several times for the same renderer and results 
will differ.
- `Draw()` - Creating the appearance and adding it to the canvas. It can be called only once after layout, `PdfDocument` 
is changed after `Draw()`.

The base class for renderers is `AbstractRenderer`. It contains a basic set of properties and operations that are common 
to all renderers. The next important class is `BlockRenderer` which is a superclass for high-level layout objects 
renderers such as `DivRenderer`, `ListRenderer` etc. At a lower level, a `LeafRenderer` arises, which works with elements 
such as `TextRenderer` and `ImageRenderer`. We also need to mention `LineRenderer`, which is not an independent renderer, 
but is only used inside the `ParagraphRenderer`. And the main entry for the layout mechanism is the abstract class `RootRenderer`. 
In the methods of this class, the mechanism for constructing the hierarchy of renderers is introduced. It does not have 
parental renderers and some root renderers such as `CanvasRenderer` and `DocumentRenderer` are inherited from it. 
These renderers are created from the `Document` and `Canvas` objects. **You need to understand the difference between the 
_Document_ and _PdfDocument_ objects and also between _Canvas_ and _PdfCanvas_**. So `PdfDocument` and `PdfCanvas` work with the PDF 
on a more low level with PDF pages, internal PDF objects such as arrays, dictionaries, etc. And `Document` and `Canvas` with 
their corresponding renderers are connecting links between the layout mechanism and the output PDF file structure. 
So these classes can do the similar operations but on different levels of abstraction. For example, if you need 15 
lines of code to add some text with `PdfDocument`, `Document` will do this in a few lines. `DocumentRenderer` - directs 
writing of the layout objects to page content streams and handles creation of the new pages in the document if needed 
for continuous placement of elements not fitting on one page. `CanvasRenderer` - directs writing of the layout objects 
to a single arbitrary content stream (e.g. `PdfFormXObject`, or also page content stream), so it only writes to a single 
area, which means that not fitting content will not be shown.

The rendering logic is triggered when a certain element is added to the `RootElement`. Elements added to the document are
presented in the form of a tree, where each parent element has a list of children. This tree is formed at the stage of
writing code, when the added elements are declared, as here:

```
Div container = new Div();
Paragraph paragraph = new Paragraph("New paragraph.");
container.add(paragraph);
```

Then, using the `IElement#createRendererSubTree` method, these elements are recursively converted to a depth-first traversal
tree of renderers. Next, the renderer layout algorithm begins to work. It starts by calling the `layout()` method of 
the renderer for the element that was added directly to the root.

The meaning of the `layout()` method is to determine the free space on the page and fill it with elements. And first, 
layout is performed for all children of the element (Depth-first traversal). Occupied space of a parent is determined
by its children + own properties. Data from parent elements to children is transmitted using a `LayoutContext`, which 
stores information about the area, page number, and others.

When filling out the page, two types of areas are used: `LayoutBBox` (represents the available area, that parent gives for
children elements) and `OccupiedArea` (represents the area taken up by all placed elements, includes child renderers 
occupied area). If an element doesn't fit in a given area, it's split into two independent renderers. First renderer is 
usually named **split renderer** and the second - **overflow renderer**. **Split renderer** is a renderer with data that is fitted
to the available page area, it is successfully layouted and ready to be drawn. And the **overflow renderer** contains part 
of the element which is not yet positioned, it is transferred to the next page, and we call `layout()` on it. After all 
actions in `layout()` are finished it return `LayoutResult` with the results placement current element on the page. This 
object contains info about whether the current renderer was placed on the page in full (`LayoutResult#FULL`), partially 
(`LayoutResult#PARTIAL`) or not at all (`LayoutResult#NOTHING`), also the info about the occupied area and the split/overflow 
renderers.

Briefly, the layout mechanism is shown in the figure:

![Layout mechanism](Layout mechanism.png)

The next step is to call the `Draw()` method. It uses layout-result from `Layout()` step and generates PDF syntax, 
written to the PDF document: drawing instructions based on the rendering result.

Specific Renderers contain the following instructions:
- `TextRenderer`: text instructions to `PdfCanvas`
- `ImageRenderer`: creating and adding `XObject`
- `TableRenderer`: borders, etc.
