<%@page import="br.nom.leonardo.tudotopdf.pdf.DummyPDFConverter"%>
<%@page import="br.nom.leonardo.tudotopdf.pdf.PDF2PDFOCRConverter"%>
<%@page import="br.nom.leonardo.tudotopdf.config.Config"%>
<%@page import="br.nom.leonardo.tudotopdf.pdf.PDFBoxConverter"%>
<%@page import="br.nom.leonardo.tudotopdf.pdf.AsposeWordsConverter"%>
<%@page import="br.nom.leonardo.tudotopdf.pdf.OfficeToPDFConverter"%>
<%@page import="br.nom.leonardo.tudotopdf.pdf.XDocReportConverter"%>
<%@page import="br.nom.leonardo.tudotopdf.pdf.Docx4JConverter"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="br.nom.leonardo.tudotopdf.pdf.JODConverter"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<title>Welcome to tudoToPDF version <%=Config.VERSION%></title>

<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet">

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
	<h3>Please upload a file and set parameters.</h3>
	<form action="fileSubmit.pdf" enctype="multipart/form-data"
		id="fileSubmitForm" method="post" name="fileSubmit" target="_self">
		<p>
			<input name="theFile" size="50" type="file" />
		</p>

		<p>
			watermark: <input name="watermark" type="checkbox" checked="checked" />
			type <select name="watermarkType" size="1"><option
					selected="selected" value="diagonal">diagonal</option>
				<option value="horizontal">horizontal</option></select> transparency [1-100]:
			<input maxlength="100" name="transparency" size="3" type="text"
				value="50" /> color <select name="watermarkColor" size="1">
				<option selected="selected" value="lightGray">lightGray</option>
				<option value="black">black</option>
				<option value="blue">blue</option>
				<option value="cyan">cyan</option>
				<option value="darkGray">darkGray</option>
				<option value="gray">gray</option>
				<option value="green">green</option>
				<option value="magenta">magenta</option>
				<option value="orange">orange</option>
				<option value="pink">pink</option>
				<option value="red">red</option>
				<option value="white">white</option>
				<option value="yellow">yellow</option>
			</select>
		</p>
		<p>
			protected: <input name="protected" type="checkbox" checked="checked" />
		</p>
		<p>
			strategy: <select id="slcStrategy" name="strategy" size="1"
				onchange="handleStrategyChange();">
				<option value="<%=Docx4JConverter.CODE%>">Docx4J (<%=StringUtils.join(Docx4JConverter.supportedExtensions(), ',')%>)
				</option>
				<option value="<%=JODConverter.CODE%>">JOD (<%=StringUtils.join(JODConverter.supportedExtensions(), ',')%>)
				</option>
				<option value="<%=XDocReportConverter.CODE%>">XDocReport (<%=StringUtils.join(XDocReportConverter.supportedExtensions(), ',')%>)
				</option>
				<option selected="selected" value="<%=OfficeToPDFConverter.CODE%>">OfficeToPDF
					(<%=StringUtils.join(OfficeToPDFConverter.supportedExtensions(), ',')%>)
				</option>
				<option value="<%=AsposeWordsConverter.CODE%>">Aspose Words
					Commercial (<%=StringUtils.join(AsposeWordsConverter.supportedExtensions(), ',')%>)
				</option>
				<option value="<%=PDFBoxConverter.CODE%>">PDFBox (<%=StringUtils.join(PDFBoxConverter.supportedExtensions(), ',')%>)
				</option>
				<option value="<%=PDF2PDFOCRConverter.CODE%>">PDF2PDFOCR (<%=StringUtils.join(PDF2PDFOCRConverter.supportedExtensions(), ',')%>)
				</option>
				<option value="<%=DummyPDFConverter.CODE%>">Dummy</option>
				<%-- 	
			    <option value=""></option>
				<option value=""></option>
				<option value=""></option>  
			--%>
			</select>
		</p>

		<!-- Custom options for pdf2pdfocr strategy -->
		<div id="pdf2pdfocrOptions" style="display: none;">
			<p>PDF2PDFOCR Specific Options:</p>
			<p>
				--> Does not process if source already has text: <input
					name="pdf2pdfocr_flag_t" type="checkbox" checked="checked" />
			</p>
			<p>
				--> Does not process if source is protected: <input
					name="pdf2pdfocr_flag_a" type="checkbox" />
			</p>
			<p>
				--> Force PDF rebuild from images (re-encode): <input
					name="pdf2pdfocr_flag_f" type="checkbox" />
			</p>
			<p>
				--> When re-encoding, use this option (<a href="#"
					onclick="handleEncodingOption('fast')">fast</a>, <a href="#"
					onclick="handleEncodingOption('best')">best</a>, <a href="#"
					onclick="handleEncodingOption('grayscale')">grayscale</a>, <a
					href="#" onclick="handleEncodingOption('jpeg')">jpeg</a> or
				custom): <input maxlength="200" id="pdf2pdfocr_flag_g_value"
					name="pdf2pdfocr_flag_g_value" size="50" type="text" value="best" />
			</p>
			<p>
				--> Deskew before OCR: <input name="pdf2pdfocr_flag_d"
					type="checkbox" /> with threshold of <input maxlength="4"
					name="pdf2pdfocr_flag_d_value" size="5" type="text" value="40%" />
			</p>
			<p>
				--> Force PDFTK use: <input name="pdf2pdfocr_flag_p" type="checkbox" />
			</p>
		</div>

		<p>
			header: <input maxlength="100" name="headerWmText" size="30"
				type="text" value="This is a header" /> - size:&nbsp;<input
				maxlength="3" name="headerWmSize" size="3" type="text" value="15" />
		</p>
		<p>
			top: <input maxlength="30" name="topWmText" size="30" type="text"
				value="TOP" /> - size:&nbsp;<input maxlength="3" name="topWmSize"
				size="3" type="text" value="100" />
		</p>
		<p>
			middle: <input maxlength="30" name="middleWmText" size="30"
				type="text" value="MIDDLE" /> - size:&nbsp;<input maxlength="3"
				name="middleWmSize" size="3" type="text" value="200" />
		</p>
		<p>
			bottom: <input maxlength="30" name="bottomWmText" size="30"
				type="text" value="BOTTOM" /> - size:&nbsp;<input maxlength="3"
				name="bottomWmSize" size="3" type="text" value="90" />
		</p>
		<p>
			footer: <input maxlength="100" name="footerWmText" size="30"
				type="text" value="This is a footer" /> - size:&nbsp;<input
				maxlength="3" name="footerWmSize" size="3" type="text" value="15" />
		</p>
		<p>
			async: <input name="async" type="checkbox" checked="checked" />
		</p>
		<p>&lt;&lt;TODO - more options&gt;&gt;</p>
		<p>
			<input name="submit" type="submit" value="Get PDF" />
		</p>
	</form>
	<p>&nbsp;</p>
	<P>
		Powered by tudoToPDF v<%=Config.VERSION%>.

		<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
		<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
		<!-- Include all compiled plugins (below), or include individual files as needed -->
		<script src="js/bootstrap.min.js"></script>
		<script>
		<!-- local javascript options -->
			function handleEncodingOption(value) {
				$("#pdf2pdfocr_flag_g_value").val(value)
			}

			function handleStrategyChange() {
				if ($("#slcStrategy").val() == "PDF2PDFOCR") {
					$("#pdf2pdfocrOptions").show();
				} else {
					$("#pdf2pdfocrOptions").hide();
				}
			}
			$(window).load(handleStrategyChange);
		</script>
</body>
</html>