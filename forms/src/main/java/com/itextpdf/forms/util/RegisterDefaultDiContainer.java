package com.itextpdf.forms.util;

import com.itextpdf.commons.utils.DIContainer;
import com.itextpdf.forms.fields.merging.MergeFieldsStrategy;
import com.itextpdf.forms.fields.merging.OnDuplicateFormFieldNameStrategy;

public class RegisterDefaultDiContainer {

    public RegisterDefaultDiContainer() {
        // Empty constructor but should be public as we need it for automatic class loading
        // sharp
    }

    static {
        DIContainer.registerDefault(OnDuplicateFormFieldNameStrategy.class, () -> new MergeFieldsStrategy());
    }
}
