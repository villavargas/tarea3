<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% pageContext.setAttribute("newLineChar", "\n"); %>
<html>
<head>
	<title>Rename Tomcat Log</title>
	<link rel="stylesheet" href="<c:url value="/static/css/table.css"/>" type="text/css" media="screen, projection" />
	<link rel="stylesheet" href="<c:url value="/static/css/customStatistics.css"/>" type="text/css" media="screen, projection" />

	<script type="text/javascript" src="<c:url value="/static/js/renameTomcatLog.js"/>"></script>
</head>
<body>
	<div class="prepend-top span-17 colborder" id="content">
		<button id="toggleSidebarButton">&gt;</button>
		<div class="marginLeft" id="inner">
			<h2>Rename Tomcat Log</h2>
			
			<div id="renameTomcatLogForm" data-url="<c:url value="/examplehac/renameTomcatLog/rename"/>" >
				<button class="buttonExecute">Rename Tomcat Log</button>
			</div>
		</div>
	</div>
	<div id="dialogContainer"></div>
</body>
</html>

