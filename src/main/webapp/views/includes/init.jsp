<%-- Context path for static assets (works on embedded Tomcat and external WAR deploy) --%>
<% pageContext.setAttribute("ctx", request.getContextPath() != null ? request.getContextPath() : ""); %>
