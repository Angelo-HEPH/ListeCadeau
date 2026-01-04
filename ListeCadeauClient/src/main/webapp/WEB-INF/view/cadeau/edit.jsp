<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="be.couderiannello.models.Cadeau" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Modifier un cadeau</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

<%
    Cadeau cadeau = (Cadeau) request.getAttribute("cadeau");
    int listeId = (Integer) request.getAttribute("listeId");
    String error = (String) request.getAttribute("error");
%>

<h2 class="mb-4">Modifier le cadeau</h2>

<% if (error != null) { %>
    <div class="alert alert-danger">
        <%= error %>
    </div>
<% } %>

<form method="post" action="<%= request.getContextPath() %>/cadeau/edit">

    <input type="hidden" name="id" value="<%= cadeau.getId() %>">
    <input type="hidden" name="listeId" value="<%= listeId %>">

    <div class="mb-3">
        <label class="form-label">Nom</label>
        <input type="text" name="name" class="form-control"
               value="<%= cadeau.getName() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Description</label>
        <textarea name="description" class="form-control" required><%= cadeau.getDescription() %></textarea>
    </div>

    <div class="mb-3">
        <label class="form-label">Prix (€)</label>
        <input type="number" step="0.01" name="price" class="form-control"
               value="<%= cadeau.getPrice() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Lien produit</label>
        <input type="url" name="linkSite" class="form-control"
               value="<%= cadeau.getLinkSite() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Photo (URL)</label>
        <input type="url" name="photo" class="form-control"
               value="<%= cadeau.getPhoto() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Priorité</label>
        <select name="priorite" class="form-select" required>
            <option value="GRANDE" <%= cadeau.getPriorite().name().equals("GRANDE") ? "selected" : "" %>>
                GRANDE
            </option>
            <option value="MOYENNE" <%= cadeau.getPriorite().name().equals("MOYENNE") ? "selected" : "" %>>
                MOYENNE
            </option>
            <option value="FAIBLE" <%= cadeau.getPriorite().name().equals("FAIBLE") ? "selected" : "" %>>
                FAIBLE
            </option>
        </select>
    </div>

    <button type="submit" class="btn btn-success">
        Enregistrer
    </button>

    <a href="<%= request.getContextPath() %>/liste/manage?id=<%= listeId %>"
       class="btn btn-secondary ms-2">
        Annuler
    </a>

</form>

</div>
</body>
</html>
