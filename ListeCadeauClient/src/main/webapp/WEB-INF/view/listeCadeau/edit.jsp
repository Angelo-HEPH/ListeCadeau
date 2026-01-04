<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="be.couderiannello.models.ListeCadeau" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Modifier la liste</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

<%
    ListeCadeau liste = (ListeCadeau) request.getAttribute("liste");
    String error = (String) request.getAttribute("error");
%>

<% if (error != null && !error.isBlank()) { %>
    <div class="alert alert-danger"><%= error %></div>
<% } %>

<% if (liste == null) { return; } %>

<h2 class="mb-4">Modifier la liste</h2>

<form method="post" action="<%= request.getContextPath() %>/liste/edit">

    <input type="hidden" name="id" value="<%= liste.getId() %>">

    <div class="mb-3">
        <label class="form-label">Titre</label>
        <input type="text" name="title" class="form-control"
               value="<%= liste.getTitle() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Événement</label>
        <input type="text" name="evenement" class="form-control"
               value="<%= liste.getEvenement() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Date d’expiration</label>
        <input type="date" name="expirationDate" class="form-control"
               value="<%= liste.getExpirationDate() %>">
    </div>

    <div class="form-check mb-3">
        <input type="checkbox" class="form-check-input"
               name="statut" <%= liste.isStatut() ? "checked" : "" %>>
        <label class="form-check-label">Liste active</label>
    </div>

    <button type="submit" class="btn btn-success">
        Enregistrer
    </button>

    <a href="<%= request.getContextPath() %>/liste/manage?id=<%= liste.getId() %>"
       class="btn btn-secondary ms-2">
        Annuler
    </a>

</form>

</div>
</body>
</html>
