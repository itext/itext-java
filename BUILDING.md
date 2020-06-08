To build **iText 7 Community**, [Maven][1] must be installed.

Running install without a profile will generate the **iText 7 Community** jars:
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

You can use the supplied `Vagrantfile` to get a [Vagrant][4] VM ([Ubuntu][5] 14.04 LTS - Trusty Tahr, with [VirtualBox][6]) with all the required software installed.
```bash
$ vagrant box add ubuntu/trusty64
$ vagrant up
$ vagrant ssh -- \
    'cd /vagrant ; mvn clean install -Dmaven.test.skip=true' \
    > >(tee mvn.log) 2> >(tee mvn-error.log >&2)
```

[1]: http://maven.apache.org/
[2]: http://www.ghostscript.com/
[3]: http://www.imagemagick.org/
[4]: https://www.vagrantup.com/
[5]: http://www.ubuntu.com/
[6]: https://www.virtualbox.org/