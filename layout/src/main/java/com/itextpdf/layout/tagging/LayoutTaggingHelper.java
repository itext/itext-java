/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.tagutils.WaitingTagsManager;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.ILargeElement;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.AreaBreakRenderer;
import com.itextpdf.layout.renderer.IRenderer;

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

    public static void addTreeHints(LayoutTaggingHelper taggingHelper, IRenderer rootRenderer) {
        List<IRenderer> childRenderers = rootRenderer.getChildRenderers();
        if (childRenderers == null) {
            return;
        }
        taggingHelper.addKidsHint(rootRenderer, childRenderers);
        for (IRenderer childRenderer : childRenderers) {
            addTreeHints(taggingHelper, childRenderer);
        }

    }

    public static TaggingHintKey getHintKey(IPropertyContainer container) {
        return container.<TaggingHintKey>getProperty(Property.TAGGING_HINT_KEY);
    }

    public static TaggingHintKey getOrCreateHintKey(IPropertyContainer container) {
        return getOrCreateHintKey(container, true);
    }

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

    public void addKidsHint(IPropertyContainer parent, Iterable<? extends IPropertyContainer> newKids) {
        addKidsHint(parent, newKids, -1);
    }

    public void addKidsHint(IPropertyContainer parent, Iterable<? extends IPropertyContainer> newKids, int insertIndex) {
        if (parent instanceof AreaBreakRenderer) {
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
            if (kid instanceof AreaBreakRenderer) {
                return;
            }
            newKidsKeys.add(getOrCreateHintKey(kid));
        }
        addKidsHint(parentKey, newKidsKeys, insertIndex);
    }

    public void addKidsHint(TaggingHintKey parentKey, Collection<TaggingHintKey> newKidsKeys) {
        addKidsHint(parentKey, newKidsKeys, -1);
    }

    public void addKidsHint(TaggingHintKey parentKey, Collection<TaggingHintKey> newKidsKeys, int insertIndex) {
        addKidsHint(parentKey, newKidsKeys, insertIndex, false);
    }

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

    public void markArtifactHint(IPropertyContainer hintOwner) {
        TaggingHintKey hintKey = getOrCreateHintKey(hintOwner);
        markArtifactHint(hintKey);
    }

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

    public TagTreePointer useAutoTaggingPointerAndRememberItsPosition(IRenderer renderer) {
        TagTreePointer autoTaggingPointer = context.getAutoTaggingPointer();
        TagTreePointer position = new TagTreePointer(autoTaggingPointer);
        autoTaggingPointerSavedPosition.put(renderer, position);
        return autoTaggingPointer;
    }

    public void restoreAutoTaggingPointerPosition(IRenderer renderer) {
        TagTreePointer autoTaggingPointer = context.getAutoTaggingPointer();
        TagTreePointer position = autoTaggingPointerSavedPosition.remove(renderer);
        if (position != null) {
            autoTaggingPointer.moveToPointer(position);
        }
    }

    public List<TaggingHintKey> getKidsHint(TaggingHintKey parent) {
        List<TaggingHintKey> kidsHint = kidsHints.get(parent);
        if (kidsHint == null) {
            return Collections.<TaggingHintKey>emptyList();
        }
        return Collections.<TaggingHintKey>unmodifiableList(kidsHint);
    }

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

    public TaggingHintKey getParentHint(IPropertyContainer hintOwner) {
        TaggingHintKey hintKey = getHintKey(hintOwner);
        if (hintKey == null) {
            return null;
        }
        return getParentHint(hintKey);
    }

    public TaggingHintKey getParentHint(TaggingHintKey hintKey) {
        return parentHints.get(hintKey);
    }

    public TaggingHintKey getAccessibleParentHint(TaggingHintKey hintKey) {
        do {
            hintKey = getParentHint(hintKey);
        } while (hintKey != null && isNonAccessibleHint(hintKey));
        return hintKey;
    }

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

    public int moveKidHint(TaggingHintKey hintKeyOfKidToMove, TaggingHintKey newParent) {
        return moveKidHint(hintKeyOfKidToMove, newParent, -1);
    }

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

    public PdfDocument getPdfDocument() {
        return document;
    }

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

            tagPointer.addTag(ind, modelElement.getAccessibilityProperties());
            hintKey.setTagPointer(new TagTreePointer(tagPointer));
            if (hintKey.getOverriddenRole() != null) {
                tagPointer.setRole(hintKey.getOverriddenRole());
            }
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
    }

    private void registerSingleRule(String role, ITaggingRule rule) {
        List<ITaggingRule> rules = taggingRules.get(role);
        if (rules == null) {
            rules = new ArrayList<>();
            taggingRules.put(role, rules);
        }
        rules.add(rule);
    }

    private int getNearestNextSiblingIndex(WaitingTagsManager waitingTagsManager, TagTreePointer parentPointer, TaggingHintKey parentKey, TaggingHintKey kidKey) {
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