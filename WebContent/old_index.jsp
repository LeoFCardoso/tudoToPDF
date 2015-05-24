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
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome to tudoToPDF</title>
</head>
<body>
	<p>
		<strong>Please upload a file and set parameters.</strong>
	</p>
	<form action="fileSubmit.pdf" enctype="multipart/form-data"
		id="fileSubmitForm" method="post" name="fileSubmit" target="_self">
		<p>
			&nbsp;&nbsp;<input name="theFile" size="50" type="file" />
		</p>

		<p>
			watermark: <input name="watermark" type="checkbox" checked="checked" />
			type <select name="watermarkType" size="1"><option
					selected="selected" value="diagonal">diagonal</option>
				<option value="horizontal">horizontal</option></select> transparency [1-100]:
			<input maxlength="100" name="transparency" size="3" type="text"
				value="50" /> protected: <input name="protected" type="checkbox"
				checked="checked" />
		</p>

		<p>
			strategy: <select name="strategy" size="1">
				<option value="<%=Docx4JConverter.getCode()%>">Docx4J (<%=StringUtils.join(Docx4JConverter.supportedExtensions(), ',')%>)</option>
				<option value="<%=JODConverter.getCode()%>">JOD (<%=StringUtils.join(JODConverter.supportedExtensions(), ',')%>)</option>
				<option value="<%=XDocReportConverter.getCode()%>">XDocReport (<%=StringUtils.join(XDocReportConverter.supportedExtensions(), ',')%>)</option>
				<option selected="selected" value="<%=OfficeToPDFConverter.getCode()%>">OfficeToPDF (<%=StringUtils.join(OfficeToPDFConverter.supportedExtensions(), ',')%>)</option>
				<option value="<%=AsposeWordsConverter.getCode()%>">Aspose Words Commercial (<%=StringUtils.join(AsposeWordsConverter.supportedExtensions(), ',')%>)</option>
				<option value="<%=PDFBoxConverter.getCode()%>">PDFBox (<%=StringUtils.join(PDFBoxConverter.supportedExtensions(), ',')%>)</option>
				<option value="Dummy">Dummy</option>
			<%-- 	
			    <option value=""></option>
				<option value=""></option>
				<option value=""></option>  
			--%>
				</select>
		</p>

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
		middle: <input maxlength="30" name="middleWmText" size="30"
			type="text" value="MIDDLE" /> - size:&nbsp;<input maxlength="3"
			name="middleWmSize" size="3" type="text" value="200" />
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

		<p>&lt;&lt;TODO - more options&gt;&gt;</p>
		<p>
			<input name="submit" type="submit" value="Get PDF" />
		</p>
	</form>
	<p>&nbsp;</p>
	<P>Powered by tudoToPDF.
</body>
</html>