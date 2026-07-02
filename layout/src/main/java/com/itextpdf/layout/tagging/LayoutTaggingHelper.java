/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.tagging;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.tagutils.WaitingTagsManager;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.ILargeElement;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.AreaBreakRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.SectionBreakRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class is a helper which is used to correctly create structure
 * tree for layout element (with keeping right order for tags).
 */
public class LayoutTaggingHelper {
    private final TagStructureContext context;
    private final PdfDocument document;
    private final boolean immediateFlush;

    // kidsHints and parentHints fields represent tree of TaggingHintKey, where parentHints
    // stores a parent for the key, and kidsHints stores kids for key.
    private final Map<TaggingHintKey, List<TaggingHintKey>> kidsHints;
    private final Map<TaggingHintKey, TaggingHintKey> parentHints;

    private final Map<IRenderer, TagTreePointer> autoTaggingPointerSavedPosition;

    private final Map<String, List<ITaggingRule>> taggingRules;

    // dummiesForPreExistingTags is used to process TaggingDummyElement
    private final Map<PdfObject, TaggingDummyElement> dummiesForPreExistingTags;

    private final int RETVAL_NO_PARENT = -1;
    private final int RETVAL_PARENT_AND_KID_FINISHED = -2;

    private int lastId = 0;

    /**
     * Instantiates a new {@link LayoutTaggingHelper} instance for managing layout-level tagging.
     *
     * <p>This helper maintains a tree of tagging hints that represent the logical structure of a PDF document
     * and coordinates tag creation in the PDF structure tree. It automatically registers default tagging rules
     * for standard roles (e.g., TABLE, THEAD, TFOOT, TH) based on the PDF version.
     *
     * @param document the PDF document being created or modified
     * @param immediateFlush if {@code true}, parent tags will be flushed as soon as all their children are flushed;
     *        if {@code false}, tag flushing is deferred until explicitly requested
     *
     * @see #releaseFinishedHints()
     * @see #releaseAllHints()
     */
    public LayoutTaggingHelper(PdfDocument document, boolean immediateFlush) {
        this.document = document;
        this.context = document.getTagStructureContext();
        this.immediateFlush = immediateFlush;

        this.kidsHints = new LinkedHashMap<>();
        this.parentHints = new LinkedHashMap<>();
        this.autoTaggingPointerSavedPosition = new HashMap<>();

        this.taggingRules = new HashMap<>();
        registerRules(context.getTagStructureTargetVersion());

        dummiesForPreExistingTags = new LinkedHashMap<>();
    }

    /**
     * Recursively registers tagging hints from a renderer tree, preserving the logical structure.
     *
     * <p>This utility method traverses the renderer hierarchy and calls {@link #addKidsHint(IPropertyContainer, Iterable)}
     * for each renderer and its children, ensuring all parent-child relationships are captured in the tagging system.
     *
     * @param taggingHelper the helper instance managing tags
     * @param rootRenderer the root renderer of the tree to process recursively
     *
     * @see #addKidsHint(IPropertyContainer, Iterable)
     * @see IRenderer#getChildRenderers()
     */
    public static void addTreeHints(LayoutTaggingHelper taggingHelper, IRenderer rootRenderer) {
        List<IRenderer> childRenderers = rootRenderer.getChildRenderers();
        if (childRenderers == null) {
            return;
        }
        taggingHelper.addKidsHint(rootRenderer, childRenderers);
        for (IRenderer childRenderer : childRenderers) {
            addTreeHints(taggingHelper, childRenderer);
        }
        if (rootRenderer instanceof AbstractRenderer) {
            taggingHelper.addKidsHint(rootRenderer, ((AbstractRenderer) rootRenderer).getPositionenRenderers());
            for (IRenderer childRenderer : ((AbstractRenderer) rootRenderer).getPositionenRenderers()) {
                addTreeHints(taggingHelper, childRenderer);
            }
        }

    }

    /**
     * Retrieves an existing tagging hint key for the given container without creating one.
     *
     * <p>If no hint key has been created for this container, returns {@code null}.
     * Use {@link #getOrCreateHintKey(IPropertyContainer)} if you need to ensure a hint exists.
     *
     * @param container the element or renderer to retrieve the hint for
     * @return the {@link TaggingHintKey} associated with the container, or {@code null} if not yet created
     *
     * @see #getOrCreateHintKey(IPropertyContainer)
     * @see Property#TAGGING_HINT_KEY
     */
    public static TaggingHintKey getHintKey(IPropertyContainer container) {
        return container.<TaggingHintKey>getProperty(Property.TAGGING_HINT_KEY);
    }

    /**
     * Gets or creates a tagging hint key for the given container.
     *
     * <p>If a hint key already exists for this container, returns it. Otherwise, creates a new
     * {@link TaggingHintKey}, stores it in the container's properties, and returns it.
     *
     * <p>For {@link ILargeElement}s that are not yet complete, the hint key is stored on the element itself
     * rather than on the renderer, to preserve the hint across renderer recreation.
     *
     * <p>If the container's role is {@link StandardRoles#ARTIFACT}, the created hint is automatically
     * marked as artifact and finished.
     *
     * @param container the element or renderer to get or create a hint for
     * @return the existing or newly created {@link TaggingHintKey}
     *
     * @see #getHintKey(IPropertyContainer)
     * @see TaggingHintKey
     * @see Property#TAGGING_HINT_KEY
     */
    public static TaggingHintKey getOrCreateHintKey(IPropertyContainer container) {
        return getOrCreateHintKey(container, true);
    }

    /**
     * Registers child hints for a pre-existing PDF tag (mapped via TagTreePointer).
     *
     * <p>This method is used when you have a pre-existing tag structure element (from a PDF that already
     * contains tags) and need to associate new children with it. The helper creates a {@link TaggingDummyElement}
     * wrapper to manage the pre-existing tag and adds the new children under it.
     *
     * <p>This is useful for merging external PDFs or handling documents that were partially tagged
     * before layout processing.
     *
     * @param parentPointer the {@link TagTreePointer} pointing to the pre-existing parent tag
     * @param newKids the children to add under the parent tag
     *
     * @see TaggingDummyElement
     * @see WaitingTagsManager#assignWaitingState(TagTreePointer, Object)
     */
    public void addKidsHint(TagTreePointer parentPointer, Iterable<? extends IPropertyContainer> newKids) {
        PdfDictionary pointerStructElem = context.getPointerStructElem(parentPointer).getPdfObject();
        TaggingDummyElement dummy = dummiesForPreExistingTags.get(pointerStructElem);
        if (dummy == null) {
            dummy = new TaggingDummyElement(parentPointer.getRole());
            dummiesForPreExistingTags.put(pointerStructElem, dummy);
        }
        context.getWaitingTagsManager().assignWaitingState(parentPointer, getOrCreateHintKey(dummy));
        addKidsHint(dummy, newKids);
    }

    /**
     * Registers children hints for a parent element or renderer (append mode).
     *
     * <p>This method declares that the given children should appear as kids of the parent in the PDF structure tree.
     * Children are appended to any existing children. This method creates {@link TaggingHintKey}s for each child
     * if they don't already exist.
     *
     * @param parent the parent element or renderer
     * @param newKids the children to add under the parent (can be elements or renderers)
     *
     * @see #addKidsHint(IPropertyContainer, Iterable, int)
     * @see #finishTaggingHint(IPropertyContainer)
     */
    public void addKidsHint(IPropertyContainer parent, Iterable<? extends IPropertyContainer> newKids) {
        addKidsHint(parent, newKids, -1);
    }

    /**
     * Registers children hints for a parent element or renderer (with insert position).
     *
     * <p>This method declares that the given children should appear as kids of the parent in the PDF structure tree,
     * optionally at a specific position. If {@code insertIndex} is negative, children are appended.
     *
     * <p>If the parent tag has already been created in the PDF structure tree, this method will relocate
     * child tags into the parent.
     *
     * @param parent the parent element or renderer
     * @param newKids the children to add under the parent
     * @param insertIndex the position at which to insert the first child; negative means append at end
     *
     * @see #addKidsHint(IPropertyContainer, Iterable)
     * @see #addKidsHint(TaggingHintKey, Collection, int)
     */
    public void addKidsHint(IPropertyContainer parent, Iterable<? extends IPropertyContainer> newKids, int insertIndex) {
        if (parent instanceof AreaBreakRenderer || parent instanceof SectionBreakRenderer) {
            return;
        }

        TaggingHintKey parentKey = getOrCreateHintKey(parent);

        if (parent instanceof IRenderer &&
                this.getPdfDocument().getDiContainer().isRegistered(ProhibitedTagRelationsResolver.class)) {
            this.getPdfDocument()
                    .getDiContainer()
                    .getInstance(ProhibitedTagRelationsResolver.class)
                    .repairTagStructure(this, (IRenderer) parent);
        }

        List<TaggingHintKey> newKidsKeys = new ArrayList<>();
        for (IPropertyContainer kid : newKids) {
            if (kid instanceof AreaBreakRenderer || kid instanceof SectionBreakRenderer) {
                return;
            }
            TaggingHintKey kidHint = getOrCreateHintKey(kid);
            newKidsKeys.add(kidHint);
        }
        addKidsHint(parentKey, newKidsKeys, insertIndex);
    }

    /**
     * Registers children hints using {@link TaggingHintKey}s directly (append mode).
     *
     * <p>This variant works directly with {@link TaggingHintKey} objects instead of containers,
     * useful when you already have the hint keys or when working with internal hint manipulation.
     *
     * @param parentKey the parent hint key
     * @param newKidsKeys the hint keys of children to add
     *
     * @see #addKidsHint(TaggingHintKey, Collection, int)
     */
    public void addKidsHint(TaggingHintKey parentKey, Collection<TaggingHintKey> newKidsKeys) {
        addKidsHint(parentKey, newKidsKeys, -1);
    }

    /**
     * Registers children hints using {@link TaggingHintKey}s directly (with insert position).
     *
     * <p>This variant works directly with {@link TaggingHintKey} objects and supports specifying
     * an insertion position. This is the core method that other {@code addKidsHint} overloads delegate to.
     *
     * @param parentKey the parent hint key
     * @param newKidsKeys the hint keys of children to add
     * @param insertIndex the position at which to insert the first child; negative means append at end
     *
     * @see #addKidsHint(TaggingHintKey, Collection)
     */
    public void addKidsHint(TaggingHintKey parentKey, Collection<TaggingHintKey> newKidsKeys, int insertIndex) {
        addKidsHint(parentKey, newKidsKeys, insertIndex, false);
    }

    /**
     * Overrides the PDF role for an element's tag in the structure tree.
     *
     * <p>By default, a tag's role is determined from the element's accessibility properties. This method
     * allows you to override that role at runtime. The override is applied when the tag is created.
     *
     * <p><strong>Important:</strong> Apply role overrides <em>before</em>
     * calling {@link #finishTaggingHint(IPropertyContainer)}.
     * Once tagging rules have been applied during finishing, re-applying the same rules for a new role will not occur.
     *
     * @param hintOwner the element or renderer whose tag role should be overridden
     * @param role the new PDF role (e.g., {@link StandardRoles#SPAN}, {@link StandardRoles#STRONG})
     *
     * @see StandardRoles
     * @see #finishTaggingHint(IPropertyContainer)
     */
    public void setRoleHint(IPropertyContainer hintOwner, String role) {
        // It's unclear whether a role of already created tag should be changed
        // in this case. Also concerning rules, they won't be called for the new role
        // if this overriding role is set after some rule applying event. Already applied
        // rules won't be cancelled either.
        // Restricting this call on whether the finished state is set doesn't really
        // solve anything.
        // Probably this also should affect whether the hint is considered non-accessible
        getOrCreateHintKey(hintOwner).setOverriddenRole(role);
    }

    /**
     * Checks whether the given container is marked as an artifact (non-accessible).
     *
     * <p>An artifact is content that should not appear in the accessibility tree, such as decorative
     * elements. Artifacts are not included in the PDF structure tree.
     *
     * <p>This method checks:
     * <ol>
     *   <li>If a hint exists and is marked as artifact, returns {@code true}
     *   <li>If the container's accessibility role is {@link StandardRoles#ARTIFACT}, returns {@code true}
     *   <li>Otherwise returns {@code false}
     * </ol>
     *
     * @param hintOwner the element or renderer to check
     * @return {@code true} if the container is an artifact, {@code false} otherwise
     *
     * @see #markArtifactHint(IPropertyContainer)
     * @see StandardRoles#ARTIFACT
     */
    public boolean isArtifact(IPropertyContainer hintOwner) {
        TaggingHintKey key = getHintKey(hintOwner);
        if (key != null) {
            return key.isArtifact();
        } else {
            IAccessibleElement aElem = null;
            if (hintOwner instanceof IRenderer && ((IRenderer) hintOwner).getModelElement() instanceof IAccessibleElement) {
                aElem = (IAccessibleElement) ((IRenderer) hintOwner).getModelElement();
            } else if (hintOwner instanceof IAccessibleElement) {
                aElem = (IAccessibleElement) hintOwner;
            }
            if (aElem != null) {
                return StandardRoles.ARTIFACT.equals(aElem.getAccessibilityProperties().getRole());
            }
        }
        return false;
    }

    /**
     * Marks an element or renderer as an artifact (non-accessible content).
     *
     * <p>Artifacts are excluded from the PDF accessibility tree and are not exposed to assistive technologies.
     * Use this for decorative elements, borders, backgrounds, or other non-semantic content.
     *
     * <p>This method:
     * <ul>
     *   <li>Marks the hint as artifact and finished
     *   <li>Recursively marks all children as artifacts
     *   <li>Removes the hint from its parent (orphaning it)
     *   <li>Logs an error if the artifact tag was already created in the PDF
     * </ul>
     *
     * @param hintOwner the element or renderer to mark as artifact
     *
     * @see #markArtifactHint(TaggingHintKey)
     * @see #isArtifact(IPropertyContainer)
     */
    public void markArtifactHint(IPropertyContainer hintOwner) {
        TaggingHintKey hintKey = getOrCreateHintKey(hintOwner);
        markArtifactHint(hintKey);
    }

    /**
     * Marks a hint key as an artifact (non-accessible content).
     *
     * <p>This is the core implementation of artifact marking. It:
     * <ul>
     *   <li>Sets the artifact and finished flags on the hint
     *   <li>Recursively marks all children as artifacts
     *   <li>Removes the hint from its parent
     *   <li>Flushes the artifact tag pointer if already created
     * </ul>
     *
     * @param hintKey the hint key to mark as artifact
     *
     * @see #markArtifactHint(IPropertyContainer)
     */
    public void markArtifactHint(TaggingHintKey hintKey) {
        hintKey.setArtifact();
        hintKey.setFinished();
        TagTreePointer existingArtifactTag = new TagTreePointer(document);
        if (context.getWaitingTagsManager().tryMovePointerToWaitingTag(existingArtifactTag, hintKey)) {
            Logger logger = LoggerFactory.getLogger(LayoutTaggingHelper.class);
            logger.error(IoLogMessageConstant.ALREADY_TAGGED_HINT_MARKED_ARTIFACT);

            context.getWaitingTagsManager().removeWaitingState(hintKey);
            if (immediateFlush) {
                existingArtifactTag.flushParentsIfAllKidsFlushed();
            }
        }
        List<TaggingHintKey> kidsHint = getKidsHint(hintKey);
        for (TaggingHintKey kidKey : kidsHint) {
            markArtifactHint(kidKey);
        }
        removeParentHint(hintKey);
    }

    /**
     * Saves the current auto-tagging pointer position and returns it for temporary use.
     *
     * <p>This method is useful when a renderer needs to temporarily modify the auto-tagging pointer
     * for custom tag creation or structure manipulation. The saved position can be restored later
     * using {@link #restoreAutoTaggingPointerPosition(IRenderer)}.
     *
     * <p><strong>Usage pattern (with try-finally):</strong>
     * <pre>{@code
     * TagTreePointer ptr = helper.useAutoTaggingPointerAndRememberItsPosition(renderer);
     * try {
     *     ptr.addTag("CustomRole");
     *     // ... custom operations ...
     * } finally {
     *     helper.restoreAutoTaggingPointerPosition(renderer);
     * }
     * }</pre>
     *
     * @param renderer the renderer whose position should be saved (used as a key for restoration)
     * @return the current auto-tagging pointer (position at the time of call)
     *
     * @see #restoreAutoTaggingPointerPosition(IRenderer)
     */
    public TagTreePointer useAutoTaggingPointerAndRememberItsPosition(IRenderer renderer) {
        TagTreePointer autoTaggingPointer = context.getAutoTaggingPointer();
        TagTreePointer position = new TagTreePointer(autoTaggingPointer);
        autoTaggingPointerSavedPosition.put(renderer, position);
        return autoTaggingPointer;
    }

    /**
     * Restores the auto-tagging pointer to a previously saved position.
     *
     * <p>This method retrieves the pointer position saved by
     * {@link #useAutoTaggingPointerAndRememberItsPosition(IRenderer)} and moves the auto-tagging pointer back
     * to that location. If no saved position exists for the renderer, does nothing.
     *
     * <p><strong>Important:</strong> Always call this in a finally block or error handling path to ensure
     * the pointer is restored even if an exception occurs during custom tagging operations.
     *
     * @param renderer the renderer whose position should be restored
     *
     * @see #useAutoTaggingPointerAndRememberItsPosition(IRenderer)
     */
    public void restoreAutoTaggingPointerPosition(IRenderer renderer) {
        TagTreePointer autoTaggingPointer = context.getAutoTaggingPointer();
        TagTreePointer position = autoTaggingPointerSavedPosition.remove(renderer);
        if (position != null) {
            autoTaggingPointer.moveToPointer(position);
        }
    }

    /**
     * Gets the unmodifiable list of direct children for a parent hint.
     *
     * <p>This method returns all direct children hints, including non-accessible intermediate nodes.
     * For accessible children only, use {@link #getAccessibleKidsHint(TaggingHintKey)}.
     *
     * <p>Returns an empty list if the parent has no children.
     *
     * @param parent the parent hint key
     * @return an unmodifiable list of direct child hint keys
     *
     * @see #getAccessibleKidsHint(TaggingHintKey)
     */
    public List<TaggingHintKey> getKidsHint(TaggingHintKey parent) {
        List<TaggingHintKey> kidsHint = kidsHints.get(parent);
        if (kidsHint == null) {
            return Collections.<TaggingHintKey>emptyList();
        }
        return Collections.<TaggingHintKey>unmodifiableList(kidsHint);
    }

    /**
     * Gets the list of accessible children for a parent hint, flattening non-accessible intermediate nodes.
     *
     * <p>This method returns only accessible children (those with a non-null role). Non-accessible
     * intermediate nodes (grouping nodes) are recursively flattened, and their accessible descendants
     * are included in the returned list.
     *
     * <p>For example, if a parent has a non-accessible child that contains two accessible children,
     * this method returns those two accessible children directly.
     *
     * <p>Returns an empty list if the parent has no accessible children.
     *
     * @param parent the parent hint key
     * @return an unmodifiable list of accessible child hint keys with non-accessible intermediates flattened
     *
     * @see #getKidsHint(TaggingHintKey)
     */
    public List<TaggingHintKey> getAccessibleKidsHint(TaggingHintKey parent) {
        List<TaggingHintKey> kidsHint = kidsHints.get(parent);
        if (kidsHint == null) {
            return Collections.<TaggingHintKey>emptyList();
        }

        List<TaggingHintKey> accessibleKids = new ArrayList<>();

        for (TaggingHintKey kid : kidsHint) {
            if (isNonAccessibleHint(kid)) {
                accessibleKids.addAll(getAccessibleKidsHint(kid));
            } else {
                accessibleKids.add(kid);
            }
        }

        return accessibleKids;
    }

    /**
     * Gets the parent hint of a given element or renderer.
     *
     * <p>This method retrieves the direct parent hint for the given container by first obtaining
     * its hint key and then looking up the parent.
     *
     * @param hintOwner the element or renderer whose parent should be retrieved
     * @return the parent {@link TaggingHintKey}, or {@code null} if this is a root or has no hint
     *
     * @see #getParentHint(TaggingHintKey)
     * @see #getAccessibleParentHint(TaggingHintKey)
     */
    public TaggingHintKey getParentHint(IPropertyContainer hintOwner) {
        TaggingHintKey hintKey = getHintKey(hintOwner);
        if (hintKey == null) {
            return null;
        }
        return getParentHint(hintKey);
    }

    /**
     * Gets the direct parent hint of a hint key.
     *
     * @param hintKey the child hint key
     * @return the parent {@link TaggingHintKey}, or {@code null} if this is a root
     *
     * @see #getParentHint(IPropertyContainer)
     * @see #getAccessibleParentHint(TaggingHintKey)
     */
    public TaggingHintKey getParentHint(TaggingHintKey hintKey) {
        return parentHints.get(hintKey);
    }

    /**
     * Gets the nearest accessible parent hint, skipping non-accessible intermediate nodes.
     *
     * <p>This method traverses up the hint tree, skipping non-accessible hints (grouping nodes),
     * and returns the first accessible parent found. Useful when you need to know the logical
     * parent regardless of grouping structure.
     *
     * @param hintKey the child hint key
     * @return the nearest accessible parent {@link TaggingHintKey}, or {@code null} if no accessible parent exists
     *
     * @see #getParentHint(TaggingHintKey)
     */
    public TaggingHintKey getAccessibleParentHint(TaggingHintKey hintKey) {
        do {
            hintKey = getParentHint(hintKey);
        } while (hintKey != null && isNonAccessibleHint(hintKey));
        return hintKey;
    }

    /**
     * Incrementally finalizes and releases finished hints from the tagging structure.
     *
     * <p>This method scans all hints and releases those that:
     * <ul>
     *   <li>Are marked as finished
     *   <li>Are accessible (not non-accessible grouping nodes)
     *   <li>Have no unfinished parents (up the hierarchy)
     *   <li>Have no unfinished children
     *   <li>Are not followed by unfinished siblings
     * </ul>
     *
     * <p>When a hint is released:
     * <ul>
     *   <li>It is removed from the hint trees
     *   <li>The associated PDF tag is finalized
     *   <li>If {@code immediateFlush} is enabled, parent tags are flushed if all kids are flushed
     * </ul>
     *
     * <p>This is an incremental operation useful for memory management. Call this periodically
     * (e.g., at end of each page or logical boundary) to progressively finalize tags.
     *
     * @see #releaseAllHints()
     * @see #finishTaggingHint(IPropertyContainer)
     */
    public void releaseFinishedHints() {
        Set<TaggingHintKey> allHints = new HashSet<>();
        for (Map.Entry<TaggingHintKey, TaggingHintKey> entry : parentHints.entrySet()) {
            allHints.add(entry.getKey());
            allHints.add(entry.getValue());
        }

        for (TaggingHintKey hint : allHints) {
            if (!hint.isFinished() || isNonAccessibleHint(hint) || hint.getAccessibleElement() instanceof TaggingDummyElement) {
                continue;
            }
            finishDummyKids(getKidsHint(hint));
        }

        Set<TaggingHintKey> hintsToBeHeld = new HashSet<>();
        for (TaggingHintKey hint : allHints) {
            if (!isNonAccessibleHint(hint)) {
                List<TaggingHintKey> siblingsHints = getAccessibleKidsHint(hint);
                boolean holdTheFirstFinishedToBeFound = false;
                for (TaggingHintKey sibling : siblingsHints) {
                    if (!sibling.isFinished()) {
                        holdTheFirstFinishedToBeFound = true;
                    } else if (holdTheFirstFinishedToBeFound) {
                        // here true == sibling.isFinished
                        hintsToBeHeld.add(sibling);
                        holdTheFirstFinishedToBeFound = false;
                    }
                }
            }
        }

        for (TaggingHintKey hint : allHints) {
            if (hint.isFinished()) {
                releaseHint(hint, hintsToBeHeld, true);
            }
        }
    }

    /**
     * Forces finalization and release of all hints, clearing the entire tagging structure.
     *
     * <p>This is a comprehensive cleanup operation that:
     * <ul>
     *   <li>Finishes all dummy elements (pre-existing tags)
     *   <li>Recursively finishes all dummy children
     *   <li>Calls {@link #releaseFinishedHints()} to finalize any now-finished hints
     *   <li>Releases all remaining unfinished hints (orphaned hints)
     *   <li>Clears all internal maps
     * </ul>
     *
     * <p>Call this at the end of document layout or when discarding the layout state entirely.
     * This method should leave all internal structures empty after completion.
     *
     * @see #releaseFinishedHints()
     * @see #finishTaggingHint(IPropertyContainer)
     */
    public void releaseAllHints() {
        for (TaggingDummyElement dummy : dummiesForPreExistingTags.values()) {
            finishTaggingHint(dummy);
            finishDummyKids(getKidsHint(getHintKey(dummy)));
        }
        dummiesForPreExistingTags.clear();

        releaseFinishedHints();

        Set<TaggingHintKey> hangingHints = new HashSet<>();
        for (Map.Entry<TaggingHintKey, TaggingHintKey> entry : parentHints.entrySet()) {
            hangingHints.add(entry.getKey());
            hangingHints.add(entry.getValue());
        }

        for (TaggingHintKey hint : hangingHints) {
            // In some situations we need to remove tagging hints of renderers that are thrown away for reasons like:
            // - fixed height clipping
            // - forced placement
            // - some other cases?
            // if (!hint.isFinished()) {
            //      Logger logger = LoggerFactory.getLogger(LayoutTaggingHelper.class);
            //      logger.warn(LogMessageConstant.TAGGING_HINT_NOT_FINISHED_BEFORE_CLOSE);
            // }
            releaseHint(hint, null, false);
        }

        assert parentHints.isEmpty();
        assert kidsHints.isEmpty();
    }

    /**
     * Creates or retrieves a PDF tag for a renderer, ensuring it exists in the structure tree.
     *
     * <p>This method is typically called by a renderer before it writes marked content to ensure the tag
     * is positioned correctly in the PDF structure tree. If the tag already exists, it is not recreated.
     *
     * <p>For artifacts, returns {@code false} without creating a tag. For non-accessible hints,
     * the pointer is positioned at the nearest accessible parent. For accessible hints, a tag is
     * created with the correct sibling index.
     *
     * @param renderer the renderer whose tag should be created
     * @param tagPointer the tag tree pointer to use for positioning; the pointer may be moved during tag creation
     * @return {@code true} if a tag was created, {@code false} if one already existed or
     *         hint is artifact/non-accessible
     *
     * @see #createTag(TaggingHintKey, TagTreePointer)
     */
    public boolean createTag(IRenderer renderer, TagTreePointer tagPointer) {
        TaggingHintKey hintKey = getHintKey(renderer);

        boolean noHint = hintKey == null;
        if (noHint) {
            hintKey = getOrCreateHintKey(renderer, false);
        }
        boolean created = createTag(hintKey, tagPointer);
        if (noHint) {
            hintKey.setFinished();
            context.getWaitingTagsManager().removeWaitingState(hintKey);
        }
        return created;
    }

    /**
     * Creates a PDF tag for a hint key, ensuring it exists in the structure tree.
     *
     * <p>This is the core tag creation method. It:
     * <ul>
     *   <li>Returns {@code false} if the hint is an artifact
     *   <li>Determines the correct parent tag and sibling index
     *   <li>Creates the tag via {@code tagPointer.addTag(...)}
     *   <li>Stores the pointer on the hint
     *   <li>Assigns waiting state to the tag
     *   <li>Recursively creates tags for dummy children
     * </ul>
     *
     * <p>The pointer may be modified during this method to position it at the correct parent and index.
     * That's why if auto-tagging pointer is to be used, make sure to rely on
     * {@link #useAutoTaggingPointerAndRememberItsPosition} and {@link #restoreAutoTaggingPointerPosition}
     * functionality.
     *
     * @param hintKey the hint key to create a tag for
     * @param tagPointer the tag tree pointer to use for positioning
     * @return {@code true} if a tag was created, {@code false} if artifact or already exists
     *
     * @see #createTag(IRenderer, TagTreePointer)
     */
    public boolean createTag(TaggingHintKey hintKey, TagTreePointer tagPointer) {
        if (hintKey.isArtifact()) {
            return false;
        }

        boolean created = createSingleTag(hintKey, tagPointer);

        if (created) {
            List<TaggingHintKey> kidsHint = getAccessibleKidsHint(hintKey);
            for (TaggingHintKey hint : kidsHint) {
                if (hint.getAccessibleElement() instanceof TaggingDummyElement) {
                    createTag(hint, new TagTreePointer(document));
                }
            }
        }
        return created;
    }

    /**
     * Marks an element or renderer as logically complete and applies tagging rules.
     *
     * <p>Call this method when an element has finished its layout or rendering and will not
     * receive new children. This triggers:
     * <ul>
     *   <li>Lookup of applicable {@link ITaggingRule}s for the element's role
     *   <li>Invocation of each rule's {@link ITaggingRule#onTagFinish(LayoutTaggingHelper, TaggingHintKey)} method
     *   <li>If all rules return {@code true}, the hint is marked as finished
     *   <li>If any rule returns {@code false}, the hint remains unfinished (rules can block finishing)
     * </ul>
     *
     * <p><strong>Important:</strong> A hint cannot receive new children or be relocated after finishing.
     * Always try to finish hints in parent-to-child order (or at least ensure children are finished before parents)
     * if possible.
     *
     * <p>For non-accessible hints, rules are bypassed and the hint is marked finished immediately.
     * For artifacts, this method has no effect.
     *
     *
     * @param hintOwner the element or renderer to finish
     *
     * @see ITaggingRule
     * @see #releaseFinishedHints()
     */
    public void finishTaggingHint(IPropertyContainer hintOwner) {
        TaggingHintKey rendererKey = getHintKey(hintOwner);

        // artifact is always finished
        if (rendererKey == null || rendererKey.isFinished()) {
            return;
        }

        if (rendererKey.isElementBasedFinishingOnly() && !(hintOwner instanceof IElement)) {
            // avoid auto finishing of hints created based on IElements
            return;
        }

        if (!isNonAccessibleHint(rendererKey)) {
            IAccessibleElement modelElement = rendererKey.getAccessibleElement();
            String role = modelElement.getAccessibilityProperties().getRole();
            if (rendererKey.getOverriddenRole() != null) {
                role = rendererKey.getOverriddenRole();
            }
            List<ITaggingRule> rules = taggingRules.get(role);
            boolean ruleResult = true;
            if (rules != null) {
                for (ITaggingRule rule : rules) {
                    ruleResult = ruleResult && rule.onTagFinish(this, rendererKey);
                }
            }
            if (!ruleResult) {
                return;
            }
        }

        rendererKey.setFinished();
    }

    /**
     * Replaces one child hint with multiple new child hints.
     *
     * <p>This method is useful when a single renderer needs to expand into multiple child tags.
     * It removes the old child from its parent and inserts the new children at the same position.
     *
     * <p>Errors are logged and the operation fails if:
     * <ul>
     *   <li>The child hint is already finished
     *   <li>Any new child is already finished and either has no parent or the parent is already finished too.
     * </ul>
     *
     * <p>The method returns the index where the replacement occurred, which can be used for
     * further hint tree manipulation if needed.
     *
     * @param kidHintKey the child hint to be replaced
     * @param newKidsHintKeys the new child hints to insert at the replacement position
     * @return the index where the old child was removed, or {@code -1} if replacement failed
     *
     * @see #moveKidHint(TaggingHintKey, TaggingHintKey)
     */
    public int replaceKidHint(TaggingHintKey kidHintKey, Collection<TaggingHintKey> newKidsHintKeys) {
        TaggingHintKey parentKey = getParentHint(kidHintKey);
        if (parentKey == null) {
            return -1;
        }
        if (kidHintKey.isFinished()) {
            Logger logger = LoggerFactory.getLogger(LayoutTaggingHelper.class);
            logger.error(IoLogMessageConstant.CANNOT_REPLACE_FINISHED_HINT);

            // If kidHintKey is finished you won't be able to add it anywhere after replacing is ended.
            // If kidHintKey might be finished, use moveKidHint instead.
            // replaceKidHint should be used when parent might be finished.
            return -1;
        }

        int kidIndex = removeParentHint(kidHintKey);

        List<TaggingHintKey> kidsToBeAdded = new ArrayList<>();
        for (TaggingHintKey newKidKey : newKidsHintKeys) {
            int i = removeParentHint(newKidKey);
            if (i == RETVAL_PARENT_AND_KID_FINISHED
                    || i == RETVAL_NO_PARENT && newKidKey.isFinished()) {
                Logger logger = LoggerFactory.getLogger(LayoutTaggingHelper.class);
                logger.error(IoLogMessageConstant.CANNOT_MOVE_FINISHED_HINT);
                continue;
            }
            kidsToBeAdded.add(newKidKey);
        }

        addKidsHint(parentKey, kidsToBeAdded, kidIndex, true);

        return kidIndex;
    }

    /**
     * Moves a child hint from its current parent to a new parent (appended).
     *
     * <p>This method removes a child from its current parent and re-parents it to the new parent,
     * appending it to the new parent's children list.
     *
     * <p>For a specific insertion position in the new parent, use {@link #moveKidHint(TaggingHintKey, TaggingHintKey, int)}.
     *
     * @param hintKeyOfKidToMove the child hint to move
     * @param newParent the new parent hint
     * @return the index where the child was removed from the old parent, or {@code -1} if move failed
     *
     * @see #moveKidHint(TaggingHintKey, TaggingHintKey, int)
     * @see #replaceKidHint(TaggingHintKey, Collection)
     */
    public int moveKidHint(TaggingHintKey hintKeyOfKidToMove, TaggingHintKey newParent) {
        return moveKidHint(hintKeyOfKidToMove, newParent, -1);
    }

    /**
     * Moves a child hint from its current parent to a new parent at a specific position.
     *
     * <p>This method is similar to {@link #moveKidHint(TaggingHintKey, TaggingHintKey)} but allows
     * specifying the insertion index in the new parent's children list. Negative index means append.
     *
     * <p>Errors are logged if:
     * <ul>
     *   <li>The new parent is already finished
     *   <li>The child hint is already finished
     * </ul>
     *
     * @param hintKeyOfKidToMove the child hint to move
     * @param newParent the new parent hint
     * @param insertIndex the position at which to insert the child; negative means append
     * @return the index where the child was removed from the old parent, or {@code -1} if move failed
     *
     * @see #moveKidHint(TaggingHintKey, TaggingHintKey)
     */
    public int moveKidHint(TaggingHintKey hintKeyOfKidToMove, TaggingHintKey newParent, int insertIndex) {
        if (newParent.isFinished()) {
            Logger logger = LoggerFactory.getLogger(LayoutTaggingHelper.class);
            logger.error(IoLogMessageConstant.CANNOT_MOVE_HINT_TO_FINISHED_PARENT);
            return -1;
        }

        int removeRes = removeParentHint(hintKeyOfKidToMove);
        if (removeRes == RETVAL_PARENT_AND_KID_FINISHED
                || removeRes == RETVAL_NO_PARENT && hintKeyOfKidToMove.isFinished()) {
            Logger logger = LoggerFactory.getLogger(LayoutTaggingHelper.class);
            logger.error(IoLogMessageConstant.CANNOT_MOVE_FINISHED_HINT);
            return -1;
        }
        addKidsHint(newParent, Collections.<TaggingHintKey>singletonList(hintKeyOfKidToMove), insertIndex, true);

        return removeRes;
    }

    /**
     * Created a unique id for a structureElement.
     *
     * @param prefix a prefix to prepend to the id
     *
     * @return a unique id
     */
    public String createStructureElementId(String prefix) {
        lastId++;
        return prefix + lastId;
    }

    /**
     * Gets the PDF document associated with this helper.
     *
     * @return the {@link PdfDocument} passed to the constructor
     */
    public PdfDocument getPdfDocument() {
        return document;
    }

    /**
     * Internal implementation of hint key creation/retrieval.
     *
     * <p>This method implements the core logic for obtaining or creating hint keys:
     * <ul>
     *   <li>Checks for existing hint on the container
     *   <li>If not found, wraps the container's accessible element
     *   <li>Automatically marks as artifact if role is ARTIFACT
     *   <li>Optionally stores the hint on the container
     * </ul>
     *
     * @param hintOwner the element or renderer
     * @param setProperty if {@code true}, stores the hint in the container's properties
     * @return the existing or newly created hint key
     */
    private static TaggingHintKey getOrCreateHintKey(IPropertyContainer hintOwner, boolean setProperty) {
        TaggingHintKey hintKey = hintOwner.<TaggingHintKey>getProperty(Property.TAGGING_HINT_KEY);
        if (hintKey == null) {
            IAccessibleElement elem = null;
            if (hintOwner instanceof IAccessibleElement) {
                elem = (IAccessibleElement) hintOwner;
            } else if (hintOwner instanceof IRenderer && ((IRenderer) hintOwner).getModelElement() instanceof IAccessibleElement) {
                elem = (IAccessibleElement) ((IRenderer) hintOwner).getModelElement();
            }
            hintKey = new TaggingHintKey(elem, hintOwner instanceof IElement);
            if (elem != null && StandardRoles.ARTIFACT.equals(elem.getAccessibilityProperties().getRole())) {
                hintKey.setArtifact();
                hintKey.setFinished();
            }

            if (setProperty) {
                if (elem instanceof ILargeElement && !((ILargeElement) elem).isComplete()) {
                    ((ILargeElement) elem).setProperty(Property.TAGGING_HINT_KEY, hintKey);
                } else {
                    hintOwner.setProperty(Property.TAGGING_HINT_KEY, hintKey);
                }
            }
        }
        return hintKey;
    }

    private void addKidsHint(TaggingHintKey parentKey, Collection<TaggingHintKey> newKidsKeys, int insertIndex, boolean skipFinishedChecks) {
        if (newKidsKeys.isEmpty()) {
            return;
        }
        if (parentKey.isArtifact()) {
            for (TaggingHintKey kid : newKidsKeys) {
                markArtifactHint(kid);
            }
            return;
        }

        if (!skipFinishedChecks && parentKey.isFinished()) {
            Logger logger = LoggerFactory.getLogger(LayoutTaggingHelper.class);
            logger.error(IoLogMessageConstant.CANNOT_ADD_HINTS_TO_FINISHED_PARENT);
            return;
        }

        List<TaggingHintKey> kidsHint = kidsHints.get(parentKey);
        if (kidsHint == null) {
            kidsHint = new ArrayList<>();
        }

        TaggingHintKey parentTagHint = isNonAccessibleHint(parentKey) ? getAccessibleParentHint(parentKey) : parentKey;
        boolean parentTagAlreadyCreated = parentTagHint != null && isTagAlreadyExistsForHint(parentTagHint);
        for (TaggingHintKey kidKey : newKidsKeys) {
            if (kidKey.isArtifact()) {
                continue;
            }

            TaggingHintKey prevParent = getParentHint(kidKey);
            if (prevParent != null) {
                // Seems to be a legit use case to re-add hints to just ensure that hints are added
                // Logger logger = LoggerFactory.getLogger(LayoutTaggingHelper.class);
                // logger.error(LogMessageConstant.CANNOT_ADD_KID_HINT_WHICH_IS_ALREADY_ADDED_TO_ANOTHER_PARENT);
                continue;
            }
            if (!skipFinishedChecks && kidKey.isFinished()) {
                Logger logger = LoggerFactory.getLogger(LayoutTaggingHelper.class);
                logger.error(IoLogMessageConstant.CANNOT_ADD_FINISHED_HINT_AS_A_NEW_KID_HINT);
                continue;
            }
            if (insertIndex > -1) {
                kidsHint.add(insertIndex++, kidKey);
            } else {
                kidsHint.add(kidKey);
            }
            kidsHints.put(parentKey, kidsHint);
            parentHints.put(kidKey, parentKey);

            if (parentTagAlreadyCreated) {
                if (kidKey.getAccessibleElement() instanceof TaggingDummyElement) {
                    createTag(kidKey, new TagTreePointer(document));
                }
                if (isNonAccessibleHint(kidKey)) {
                    for (TaggingHintKey nestedKid : getAccessibleKidsHint(kidKey)) {
                        if (nestedKid.getAccessibleElement() instanceof TaggingDummyElement) {
                            createTag(nestedKid, new TagTreePointer(document));
                        }
                        moveKidTagIfCreated(parentTagHint, nestedKid);
                    }
                } else {
                    moveKidTagIfCreated(parentTagHint, kidKey);
                }
            }
        }
    }

    private boolean createSingleTag(TaggingHintKey hintKey, TagTreePointer tagPointer) {
        if (hintKey.isFinished()) {
            Logger logger = LoggerFactory.getLogger(LayoutTaggingHelper.class);
            logger.error(IoLogMessageConstant.ATTEMPT_TO_CREATE_A_TAG_FOR_FINISHED_HINT);
            return false;
        }

        if (isNonAccessibleHint(hintKey)) {
            // try move pointer to the nearest accessible parent in case any direct content will be
            // tagged with this tagPointer
            TaggingHintKey parentTagHint = getAccessibleParentHint(hintKey);
            context.getWaitingTagsManager().tryMovePointerToWaitingTag(tagPointer, parentTagHint);
            return false;
        }

        WaitingTagsManager waitingTagsManager = context.getWaitingTagsManager();
        if (!waitingTagsManager.tryMovePointerToWaitingTag(tagPointer, hintKey)) {

            IAccessibleElement modelElement = hintKey.getAccessibleElement();

            TaggingHintKey parentHint = getAccessibleParentHint(hintKey);
            int ind = -1;
            if (parentHint != null) {
                // if parent tag hasn't been created yet - it's ok, kid tags will be moved on it's creation
                if (waitingTagsManager.tryMovePointerToWaitingTag(tagPointer, parentHint)) {
                    ind = getNearestNextSiblingIndex(waitingTagsManager, tagPointer, parentHint, hintKey);
                }
            }

            AccessibilityProperties props = modelElement.getAccessibilityProperties();
            if (hintKey.getOverriddenRole() != null) {
                props = new DefaultAccessibilityProperties(props).setRole(hintKey.getOverriddenRole());
            }

            tagPointer.addTag(ind, props);
            hintKey.setTagPointer(new TagTreePointer(tagPointer));
            waitingTagsManager.assignWaitingState(tagPointer, hintKey);

            List<TaggingHintKey> kidsHint = getAccessibleKidsHint(hintKey);
            for (TaggingHintKey kidKey : kidsHint) {
                moveKidTagIfCreated(hintKey, kidKey);
            }

            return true;
        }

        return false;
    }

    private int removeParentHint(TaggingHintKey hintKey) {
        TaggingHintKey parentHint = parentHints.get(hintKey);

        if (parentHint == null) {
            return RETVAL_NO_PARENT;
        }

        TaggingHintKey accessibleParentHint = getAccessibleParentHint(hintKey);
        if (hintKey.isFinished() && parentHint.isFinished() && (accessibleParentHint == null || accessibleParentHint.isFinished())) {
            return RETVAL_PARENT_AND_KID_FINISHED;
        }

        return removeParentHint(hintKey, parentHint);
    }

    private int removeParentHint(TaggingHintKey hintKey, TaggingHintKey parentHint) {
        parentHints.remove(hintKey);

        List<TaggingHintKey> kidsHint = kidsHints.get(parentHint);
        int i;
        int size = kidsHint.size();
        for (i = 0; i < size; ++i) {
            if (kidsHint.get(i) == hintKey) {
                kidsHint.remove(i);
                break;
            }
        }
        assert i < size;

        if (kidsHint.isEmpty()) {
            kidsHints.remove(parentHint);
        }
        return i;
    }

    private void finishDummyKids(List<TaggingHintKey> taggingHintKeys) {
        for (TaggingHintKey hintKey : taggingHintKeys) {
            boolean isDummy = hintKey.getAccessibleElement() instanceof TaggingDummyElement;
            if (isDummy) {
                finishTaggingHint((IPropertyContainer) hintKey.getAccessibleElement());
            }
            if (isNonAccessibleHint(hintKey) || isDummy) {
                finishDummyKids(getKidsHint(hintKey));
            }
        }
    }

    private void moveKidTagIfCreated(TaggingHintKey parentKey, TaggingHintKey kidKey) {
        // both arguments shall be accessible, non-accessible are not handled inside this method

        TagTreePointer kidPointer = new TagTreePointer(document);
        WaitingTagsManager waitingTagsManager = context.getWaitingTagsManager();
        if (!waitingTagsManager.tryMovePointerToWaitingTag(kidPointer, kidKey)) {
            return;
        }

        TagTreePointer parentPointer = new TagTreePointer(document);
        if (!waitingTagsManager.tryMovePointerToWaitingTag(parentPointer, parentKey)) {
            return;
        }

        int ind = getNearestNextSiblingIndex(waitingTagsManager, parentPointer, parentKey, kidKey);
        parentPointer.setNextNewKidIndex(ind);
        kidPointer.relocate(parentPointer);
    }


    private static boolean isNonAccessibleHint(TaggingHintKey hintKey) {
        return !hintKey.isAccessible();
    }

    private boolean isTagAlreadyExistsForHint(TaggingHintKey tagHint) {
        return context.getWaitingTagsManager().isObjectAssociatedWithWaitingTag(tagHint);
    }

    private void releaseHint(TaggingHintKey hint, Set<TaggingHintKey> hintsToBeHeld, boolean checkContextIsFinished) {
        TaggingHintKey parentHint = parentHints.get(hint);
        List<TaggingHintKey> kidsHint = kidsHints.get(hint);
        if (checkContextIsFinished && parentHint != null) {
            if (isSomeParentNotFinished(parentHint)) {
                return;
            }
        }
        if (checkContextIsFinished && kidsHint != null) {
            if (isSomeKidNotFinished(hint)) {
                return;
            }
        }

        if (checkContextIsFinished && hintsToBeHeld != null) {
            if (hintsToBeHeld.contains(hint)) {
                return;
            }
        }

        if (parentHint != null) {
            removeParentHint(hint, parentHint);
        }
        if (kidsHint != null) {
            for (TaggingHintKey kidHint : kidsHint) {
                parentHints.remove(kidHint);
            }
            kidsHints.remove(hint);
        }

        TagTreePointer tagPointer = new TagTreePointer(document);
        if (context.getWaitingTagsManager().tryMovePointerToWaitingTag(tagPointer, hint)) {
            context.getWaitingTagsManager().removeWaitingState(hint);
            if (immediateFlush) {
                tagPointer.flushParentsIfAllKidsFlushed();
            }
        } else {
            context.getWaitingTagsManager().removeWaitingState(hint);
        }
    }

    private boolean isSomeParentNotFinished(TaggingHintKey parentHint) {
        TaggingHintKey hintKey = parentHint;
        while (true) {
            if (hintKey == null) {
                return false;
            }
            if (!hintKey.isFinished()) {
                return true;
            }
            if (!isNonAccessibleHint(hintKey)) {
                return false;
            }
            hintKey = getParentHint(hintKey);
        }
    }

    private boolean isSomeKidNotFinished(TaggingHintKey hint) {
        for (TaggingHintKey kidHint : getKidsHint(hint)) {
            if (!kidHint.isFinished()) {
                return true;
            }
            if (isNonAccessibleHint(kidHint) && isSomeKidNotFinished(kidHint)) {
                return true;
            }
        }
        return false;
    }

    private void registerRules(PdfVersion pdfVersion) {
        ITaggingRule tableRule = new TableTaggingRule();
        registerSingleRule(StandardRoles.TABLE, tableRule);
        registerSingleRule(StandardRoles.TFOOT, tableRule);
        registerSingleRule(StandardRoles.THEAD, tableRule);
        registerSingleRule(StandardRoles.TH, new THTaggingRule());
        if (pdfVersion.compareTo(PdfVersion.PDF_1_5) < 0) {
            TableTaggingPriorToOneFiveVersionRule priorToOneFiveRule = new TableTaggingPriorToOneFiveVersionRule();
            registerSingleRule(StandardRoles.TABLE, priorToOneFiveRule);
            registerSingleRule(StandardRoles.THEAD, priorToOneFiveRule);
            registerSingleRule(StandardRoles.TFOOT, priorToOneFiveRule);
        }
        FootnoteTaggingRule footnoteRule = new FootnoteTaggingRule();
        registerSingleRule(StandardRoles.LBL, footnoteRule);
        registerSingleRule(StandardRoles.REFERENCE, footnoteRule);
    }

    private void registerSingleRule(String role, ITaggingRule rule) {
        List<ITaggingRule> rules = taggingRules.get(role);
        if (rules == null) {
            rules = new ArrayList<>();
            taggingRules.put(role, rules);
        }
        rules.add(rule);
    }

    private int getNearestNextSiblingIndex(WaitingTagsManager waitingTagsManager, TagTreePointer parentPointer,
            TaggingHintKey parentKey, TaggingHintKey kidKey) {
        ScanContext scanContext = new ScanContext();
        scanContext.waitingTagsManager = waitingTagsManager;
        scanContext.startHintKey = kidKey;
        scanContext.parentPointer = parentPointer;
        scanContext.nextSiblingPointer = new TagTreePointer(document);
        return scanForNearestNextSiblingIndex(scanContext, null, parentKey);
    }

    private int scanForNearestNextSiblingIndex(ScanContext scanContext, TaggingHintKey toCheck, TaggingHintKey parent) {
        if (scanContext.startVerifying) {
            if (scanContext.waitingTagsManager.tryMovePointerToWaitingTag(scanContext.nextSiblingPointer, toCheck)
                    && scanContext.parentPointer.isPointingToSameTag(new TagTreePointer(scanContext.nextSiblingPointer).moveToParent())) {
                return scanContext.nextSiblingPointer.getIndexInParentKidsList();
            }
        }
        if (toCheck != null && !isNonAccessibleHint(toCheck)) {
            return -1;
        }
        List<TaggingHintKey> kidsHintList = kidsHints.get(parent);
        if (kidsHintList == null) {
            return -1;
        }


        int startIndex = -1;
        if (!scanContext.startVerifying) {
            for (int i = kidsHintList.size() - 1; i >= 0; i--) {
                if (scanContext.startHintKey == kidsHintList.get(i)) {
                    scanContext.startVerifying = true;
                    startIndex = i;
                    break;
                }
            }
        }


        for (int j = startIndex + 1; j < kidsHintList.size(); j++) {
            final TaggingHintKey kid = kidsHintList.get(j);
            final int interMediateResult = scanForNearestNextSiblingIndex(scanContext, kid, kid);
            if (interMediateResult != -1) {
                return interMediateResult;
            }
        }

        return -1;
    }

    private static class ScanContext {
        WaitingTagsManager waitingTagsManager;
        TaggingHintKey startHintKey;
        boolean startVerifying;
        TagTreePointer parentPointer;
        TagTreePointer nextSiblingPointer;
    }
}
