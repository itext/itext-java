package com.itextpdf.layout.property;


import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.layout.renderer.IRenderer;

import java.util.ArrayList;
import java.util.List;

public class Transform {
    private List<SingleTransform> multipleTransform;

    public Transform(int length) {
        multipleTransform = new ArrayList<SingleTransform>(length);
    }

    public void addSingleTransform(SingleTransform singleTransform) {
        multipleTransform.add(singleTransform);
    }

    private List<SingleTransform> getMultipleTransform() {
        return multipleTransform;
    }

    public static AffineTransform getAffineTransform(Transform t, float width, float height) {
        List<SingleTransform> multipleTransform = t.getMultipleTransform();
        AffineTransform affineTransform = new AffineTransform();
        for (int k = multipleTransform.size() - 1; k >= 0; k--) {
            SingleTransform transform = multipleTransform.get(k);
            float[] floats = new float[6];
            for (int i = 0; i < 4; i++)
                floats[i] = transform.getFloats()[i];
            for (int i = 4; i < 6; i++)
                floats[i] = transform.getUnitValues()[i - 4].getUnitType() == UnitValue.POINT ?
                        transform.getUnitValues()[i - 4].getValue() : transform.getUnitValues()[i - 4].getValue() / 100 * (i == 4 ? width : height);
            affineTransform.preConcatenate(new AffineTransform(floats));
        }
        return affineTransform;
    }

    public static class SingleTransform {
        private float a, b, c, d;
        private UnitValue tx, ty;

        public SingleTransform() {
            this.a = 1;
            this.b = 0;
            this.c = 0;
            this.d = 1;
            this.tx = new UnitValue(UnitValue.POINT, 0);
            this.ty = new UnitValue(UnitValue.POINT, 0);
        }

        public SingleTransform(float a, float b, float c, float d, UnitValue tx, UnitValue ty) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.tx = tx;
            this.ty = ty;
        }

        private float[] getFloats() {
            return new float[]{a, b, c, d};
        }

        private UnitValue[] getUnitValues() {
            return new UnitValue[]{tx, ty};
        }
    }
}
