package com.itextpdf.layout.margincollapse;

class MarginsCollapse {
    private float maxPositiveMargin = 0;
    private float minNegativeMargin = 0;

    void joinMargin(float margin) {
        if (maxPositiveMargin < margin) {
            maxPositiveMargin = margin;
        } else if (minNegativeMargin > margin) {
            minNegativeMargin = margin;
        }
    }

    public void joinMargin(MarginsCollapse marginsCollapse) {
        joinMargin(marginsCollapse.maxPositiveMargin);
        joinMargin(marginsCollapse.minNegativeMargin);
    }

    float getCollapsedMarginsSize() {
        return maxPositiveMargin + minNegativeMargin;
    }

    @Override
    public MarginsCollapse clone() {
        MarginsCollapse collapse = new MarginsCollapse();
        collapse.maxPositiveMargin = this.maxPositiveMargin;
        collapse.minNegativeMargin = this.minNegativeMargin;
        return collapse;
    }
}
