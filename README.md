####CURRENTLY UNMAINTAINED - tudoToPDF - PDF file conversion on server side JavaEE

This software runs on Java web containers and aims to produce PDF files from supported formats mainly, but not limited to, "Office" suites and images.

All PDF conversion is "server side", so clients must only have to be able to http POST a file. You can add PDF protection and watermark to generated files.

#### Converters

tudoToPDF is designed to be easily extended with "Converters" written by users / developers. By now, there are PDF converters based on these great libraries: Docx4J, XDocReport, JODConverter and Aspose.Words (commercial - you'll need a license). The standalone tool "OfficeToPDF.exe" is also supported (with great results) if you have a windows server with MS Office installed.

#### How to configure

Please look for "tudoToPDF.properties" file. It's location depends on path defined on "config.xml" on the root application classpath.

#### How to build

To build this project, you only need maven. Please look for "pom.xml".
Please add some JODConverter compilation to your maven local repository. I use https://github.com/nuxeo/jodconverter and after compiling "mvn install:install-file -Dfile=<path-to-jarfile> -DpomFile=<path-to-pomfile>"

#### Why this name "tudo"ToPDF?

As you already note, I'm not an English speaker. "Tudo" is a Portuguese word that means "all".

#### Please feel free to contribute with this code.
