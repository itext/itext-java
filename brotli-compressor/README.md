# Brotli Compression Strategy for iText

## Overview

This module provides a Brotli compression implementation for PDF streams in iText. Brotli is a modern compression
algorithm that typically provides better compression ratios than traditional Flate/Deflate compression, especially for
text-heavy content.

## Important Notice

**EXPERIMENTAL FEATURE**: Brotli compression for PDF streams is currently experimental and is not part of the official
PDF specification. PDF documents compressed using Brotli may not be compatible with other PDF processors or readers. Use
this feature with caution and only when you control both the generation and consumption of the PDF documents.

## Features

- Implementation of iText's `IStreamCompressionStrategy` interface
- Automatic compression level mapping from iText's 0-9 scale to Brotli's 0-11 scale
- Better compression ratios compared to Flate compression for most text content
- Seamless integration with iText's PDF generation workflow

## Requirements

- iText 9.5.0 or later

## Installation

`brotli-compressor` is experimental so the artifacts are only available on iText's artifactory.

Add the following dependency and config to your Maven project:

```xml
<repositories>
  <repository>
    <id>itext-releases</id>
    <name>iText Repository - releases</name>
    <url>https://repo.itextsupport.com/releases</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.itextpdf</groupId>
  <artifactId>brotli-compressor</artifactId>
  <version>{itext.version.bigger.then.9.5.0}</version>
</dependency>
```



## Usage

To use Brotli compression for PDF streams, register the `BrotliStreamCompressionStrategy` with your PDF document:

```java
import com.itextpdf.BrotliStreamCompressionStrategy;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.IStreamCompressionStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

// Create a PDF document with Brotli compression
DocumentProperties properties = new DocumentProperties();
properties.registerDependency(IStreamCompressionStrategy .class, new BrotliStreamCompressionStrategy());
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream), properties);

// Continue with normal PDF generation
// All streams will be compressed using Brotli
```

## Compression Levels

The Brotli compression strategy automatically maps iText compression levels to Brotli levels:

- iText levels: 0 (no compression) to 9 (best compression)
- Brotli levels: 0 to 11
- Mapping formula: `brotliLevel = round(iTextLevel * (11.0 / 9.0))`
- Default level: 6 (used when the input level is out of range)

You can set the compression level on the PDF writer:

```java

DocumentProperties properties = new DocumentProperties();
properties.registerDependency(IStreamCompressionStrategy .class, new BrotliStreamCompressionStrategy());

PdfWriter writer = new PdfWriter(outputStream);
writer.setCompressionLevel(9); // Maximum compression

PdfDocument pdfDoc = new PdfDocument(writer, properties);

```

## Decompression

Supported out of the box in the iText kernel module

The filter supports:

- Standard Brotli decompression
- Optional Brotli dictionary streams (specified via decode parameters)
- Memory limits awareness through iText's MemoryLimitsAwareHandler

## Compatibility Warning

PDF documents using Brotli compression (`/BrotliDecode` filter) are currently not compliant with the official PDF
specification (ISO 32000). These documents may:

- Fail to open in standard PDF readers (Adobe Acrobat, Preview, etc.)
- Not render correctly in web browsers
- Be rejected by PDF validation tools
- Not be processable by third-party PDF libraries

This compression method should only be used in controlled environments where:

- You generate and consume the PDF documents within your own system
- You have verified compatibility with your specific PDF processing pipeline
- You can handle potential incompatibilities with external tools

## Brotli (brotli4j) Support in GraalVM Native Image

This project uses **brotli4j**, which relies on a native Brotli library. When building a **GraalVM native image**, the
native library is **not included automatically** and must be configured explicitly.

### Native Image Configuration

Add the following options to your `native-image` buildargs:

```bash
#add all the runtimes you are deploying to, iText test env does these so we have added these as a example
-H:IncludeResources=lib/windows-x86_64/brotli.dll 
-H:IncludeResources=lib/linux-aarch64/libbrotli.so

--initialize-at-run-time=com.aayushatharva.brotli4j.Brotli4jLoader
```
