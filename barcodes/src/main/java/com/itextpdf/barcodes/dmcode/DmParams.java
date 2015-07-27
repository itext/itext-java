package com.itextpdf.barcodes.dmcode;

public  class DmParams {
    public DmParams(int height, int width, int heightSection, int widthSection, int dataSize, int dataBlock, int errorBlock) {
        this.height = height;
        this.width = width;
        this.heightSection = heightSection;
        this.widthSection = widthSection;
        this.dataSize = dataSize;
        this.dataBlock = dataBlock;
        this.errorBlock = errorBlock;
    }

    public int height;
    public int width;
    public int heightSection;
    public int widthSection;
    public int dataSize;
    public int dataBlock;
    public int errorBlock;
}
