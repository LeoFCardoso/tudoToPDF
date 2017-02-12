# tudoToPDF
#
# version 1.0
FROM leofcardoso/pdf2pdfocr:latest
MAINTAINER Leonardo F. Cardoso <leonardo.f.cardoso@gmail.com>

# Remove previous configurations from pdf2pdfocr
ENTRYPOINT []
CMD []
USER 0
WORKDIR /

USER root

# Update system and install dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
	curl \
	gcc \
	git \
	libapr1 \
	libapr1-dev \
	libreoffice libssl-dev \
	make \
	maven \
	openjdk-8-jdk rsync \
	software-properties-common

# Add multiverse source for corefonts
RUN apt-add-repository multiverse && apt-get update

# EULA / install for corefonts
RUN echo ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true | debconf-set-selections
RUN apt-get install -y --no-install-recommends ttf-mscorefonts-installer
	
# Download & install tomcat7
ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH
RUN mkdir -p "$CATALINA_HOME"
WORKDIR $CATALINA_HOME

ENV TOMCAT_MAJOR 7
ENV TOMCAT_VERSION 7.0.69
ENV TOMCAT_TGZ_URL https://archive.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

RUN set -x \
	\
	&& curl -fSL "$TOMCAT_TGZ_URL" -o tomcat.tar.gz \
	&& tar -xvf tomcat.tar.gz --strip-components=1 \
	&& rm bin/*.bat \
	&& rm tomcat.tar.gz* \
	\
	&& nativeBuildDir="$(mktemp -d)" \
	&& tar -xvf bin/tomcat-native.tar.gz -C "$nativeBuildDir" --strip-components=1 \
	&& ( \
		export CATALINA_HOME="$PWD" \
		&& cd "$nativeBuildDir/jni/native" \
		&& ./configure \
			--libdir=/usr/lib/jni \
			--prefix="$CATALINA_HOME" \
			--with-apr=/usr/bin/apr-1-config \
			--with-java-home="$JAVA_HOME" \
			--with-ssl=yes \
		&& make -j$(nproc) \
		&& make install \
	) \
	&& rm -rf "$nativeBuildDir" \
	&& rm bin/tomcat-native.tar.gz

# verify Tomcat Native is working properly
RUN set -e \
	&& nativeLines="$(catalina.sh configtest 2>&1)" \
	&& nativeLines="$(echo "$nativeLines" | grep 'Apache Tomcat Native')" \
	&& nativeLines="$(echo "$nativeLines" | sort -u)" \
	&& if ! echo "$nativeLines" | grep 'INFO: Loaded APR based Apache Tomcat Native library' >&2; then \
		echo >&2 "$nativeLines"; \
		exit 1; \
	fi
	
# 

# Compile & install jod converter lib in mvn
WORKDIR /root
RUN git clone https://github.com/nuxeo/jodconverter.git 
WORKDIR /root/jodconverter/jodconverter-core
RUN mvn clean && mvn install
RUN mvn install:install-file -Dfile=./target/jodconverter-core-3.0-NX11-SNAPSHOT.jar -DpomFile=./pom.xml 
WORKDIR /root
RUN rm -rf jodconverter/

# Compile & install tudoToPDF
# Option - download from github (TODO)
COPY . /root/tudoToPDF
WORKDIR /root/tudoToPDF
RUN mvn clean && mvn install
RUN mv /root/tudoToPDF/target/tudoToPDF-0.6.3-SNAPSHOT.war /usr/local/tomcat/webapps/tudoToPDF.war

# Copy configuration
RUN mkdir /opt/leo
RUN mv /root/tudoToPDF/Dockercontent/tudoToPDF.properties /opt/leo/tudoToPDF.properties
RUN chmod +r /opt/leo/tudoToPDF.properties
RUN rsync -a /root/tudoToPDF/Dockercontent/tomcat/conf/ /usr/local/tomcat/conf/ \
		&& rm -rf /root/tudoToPDF/Dockercontent/tomcat/conf
RUN cp /root/tudoToPDF/Dockercontent/tomcat/webapps/ROOT/NotFound.jsp /usr/local/tomcat/webapps/ROOT/NotFound.jsp

# Create work directories
RUN mkdir /opt/tudoToPDF
RUN mkdir /opt/tudoToPDFGenerated

# Cleanup
RUN apt-get autoremove -y && apt-get clean -y 
RUN rm -rf /tmp/* /var/tmp/* /var/lib/apt/lists/* /root/.m2/*

# Expose tomcat port
EXPOSE 8080

# Execute tomcat
ENV CATALINA_OPTS "-Xms1024M -Xmx2048M -Djava.security.egd=file:/dev/./urandom"

CMD ["catalina.sh", "run"]
#