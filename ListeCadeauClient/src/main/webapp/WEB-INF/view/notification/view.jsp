<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.time.format.DateTimeFormatter,
                 be.couderiannello.models.Notification" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Mes notifications</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

<%
    List<Notification> notifications =
        (List<Notification>) request.getAttribute("notifications");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    boolean hasUnread = false;
    if (notifications != null) {
        for (Notification n : notifications) {
            if (!n.isRead()) { hasUnread = true; break; }
        }
    }
%>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="mb-0">🔔 Mes notifications</h2>

    <% if (hasUnread) { %>
		<form method="post" action="<%= request.getContextPath() %>/notifications">
		    <button type="submit" class="btn btn-sm btn-primary">
		        Tout marquer comme lu
		    </button>
		</form>
    <% } %>
</div>

<%
    if (notifications == null || notifications.isEmpty()) {
%>
    <div class="alert alert-info">
        Vous n'avez aucune notification.
    </div>
<%
    } else {
%>

<div class="list-group">
<%
        for (Notification n : notifications) {
            String css = n.isRead()
                ? "list-group-item"
                : "list-group-item list-group-item-warning";
%>

    <div class="<%= css %> d-flex justify-content-between align-items-start mb-2">
        <div class="me-auto">
            <div class="fw-bold">
                <%= n.getMessage() %>
            </div>
            <small class="text-muted">
                📅 <%= n.getSendDate().format(formatter) %>
            </small>
        </div>

        <% if (!n.isRead()) { %>
            <span class="badge bg-danger rounded-pill ms-3">Nouveau</span>
        <% } %>
    </div>

<%
        }
%>
</div>

<%
    }
%>

<div class="mt-4">
    <a href="<%= request.getContextPath() %>/home" class="btn btn-secondary">
        Retour
    </a>
</div>

</div>
</body>
</html>
