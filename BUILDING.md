To build **iText Community**, [Maven][1] must be installed.

Running install without a profile will generate the **iText Community** jars:
```bash
$ mvn clean install \
    -Dmaven.test.skip=true \
    > >(tee mvn.log) 2> >(tee mvn-error.log >&2)
```

Please note that tests are not run in default profile. To run tests, [Ghostscript][2] and [Imagemagick][3] must be installed. 
Some of the tests compare generated PDF files with template files that show the correct results, and these tools are used to
visually compare PDF files. Ghostscript is required to render PDF files into images and Imagemagick is used to compare image data. 
There are two options for running tests:
1. Pass Ghostscript and Imagemagick compare commands as ITEXT_GS_EXEC and ITEXT_MAGICK_COMPARE_EXEC environment variables, respectively 
(Ghostscript and Imagemagick folders must be added to PATH environment variable). This option is suitable for both Windows and Unix systems. 
The syntax of commands depends on installed Ghostscript and Imagemagick versions (`gs`, `gswin64c`, `magick compare`).
2. Pass the paths to Ghostscript and Imagemagick compare execution files as ITEXT_GS_EXEC and ITEXT_MAGICK_COMPARE_EXEC 
environment variables. Examples of paths on Windows:
- `C:\Program Files\gs\gs9.26\bin\gswin64c.exe`
- `C:\Program Files\ImageMagick-7.0.9-Q16\compare.exe`

If you have a new version of ImageMagick, then there is no compare.exe utility there, wrap the path to magick.exe in quotes and call compare command:
ITEXT_MAGICK_COMPARE_EXEC=`"C:\Program Files\ImageMagick-7.0.9-Q16\magick.exe" compare`

To run build with tests, activate the `test` profile and pass ITEXT_GS_EXEC and ITEXT_MAGICK_COMPARE_EXEC environment variables:
```bash
$ mvn clean install \
	-P test \
    -Dmaven.test.failure.ignore=false \
    -DITEXT_GS_EXEC="gs command" \
    -DITEXT_MAGICK_COMPARE_EXEC="magick compare command" \
    > >(tee mvn.log) 2> >(tee mvn-error.log >&2)
```

By default tests are run with the non FIPS mode version of Bouncy Castle. To run the tests in FIPS mode the profile bouncy-castle-test must be deactivated and the profile bouncy-castle-fips-test must be activated:
```bash
$ mvn clean install \
	-P test,bouncy-castle-fips-test,!bouncy-castle-test \
    -Dmaven.test.failure.ignore=false \
    -DITEXT_GS_EXEC="gs command" \
    -DITEXT_MAGICK_COMPARE_EXEC="magick compare command" \
    > >(tee mvn.log) 2> >(tee mvn-error.log >&2)
```

Starting from version 8.0.3 iText Core supports native image compilation using [GraalVM][4]. Follow the instructions at
[Getting started](https://www.graalvm.org/latest/getting-started/) to build your first native application out of java sources.

To run tests in native mode [GraalVM][4] for JDK 22 or higher must be installed and native profile must be used as follows
```bash
$ mvn clean install -Pnative -DskipTests=false \
    -Dmaven.test.failure.ignore=false \
    -DITEXT_GS_EXEC="gs command" \
    -DITEXT_MAGICK_COMPARE_EXEC="magick compare command" \
    > >(tee mvn.log) 2> >(tee mvn-error.log >&2)
```

iText is backwards compatible in minor releases. To ensure that code changes conform to this requirement we use japicmp.
Todo verify this execute following commands:

```bash
$ mvn clean install
$ mvn verify --activate-profiles qa \
    -Dcheckstyle.skip=true \
    -Ddependency-check.skip=true \
    -Dpmd.skip=true \
    -Dspotbugs.skip=true \
    -Dmaven.main.skip=true \
    -Dmaven.test.skip=true \
    -Djapicmp.breakBuildOnModifications=true \
    -Djapicmp.breakBuildOnBinaryIncompatibleModifications=true \
    -Djapicmp.breakBuildOnSourceIncompatibleModifications=true 
```

If you add new public methods or classes those should be documented. 
To verify this you can execute the following commands:

```bash
$ mvn clean install
$ mvn javadoc:javadoc | grep -E "(: warning:)|(: error:)"
```



[1]: https://maven.apache.org/

[2]: https://www.ghostscript.com/

[3]: https://www.imagemagick.org/

[4]: https://www.graalvm.org/
