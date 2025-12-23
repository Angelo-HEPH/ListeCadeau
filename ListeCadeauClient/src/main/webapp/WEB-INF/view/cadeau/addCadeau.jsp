<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ajouter un cadeau</title>
</head>
<body>

<%
    int listeId = Integer.parseInt(request.getAttribute("listeId").toString());
%>

<div class="container mt-5">

    <h1 class="mb-4">Ajouter un cadeau</h1>

    <form action="<%= request.getContextPath() %>/cadeau/create" method="post" class="card p-4 shadow-sm">

        <input type="hidden" name="listeId" value="<%= listeId %>">

        <div class="mb-3">
            <label class="form-label">Nom :</label>
            <input type="text" name="name" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Description :</label>
            <textarea name="description" class="form-control" required></textarea>
        </div>

        <div class="mb-3">
            <label class="form-label">Prix :</label>
            <input type="number" name="price" step="0.01" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Photo (URL) :</label>
            <input type="text" name="photo" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Lien du site :</label>
            <input type="text" name="linkSite" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Priorité :</label>
            <select name="priorite" class="form-select" required>
                <option value="GRANDE">GRANDE</option>
                <option value="MOYENNE">MOYENNE</option>
                <option value="FAIBLE">FAIBLE</option>
            </select>
        </div>

        <button type="submit" class="btn btn-primary">Ajouter</button>
    </form>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger mt-3">
            <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <div class="mt-3">
        <a href="<%= request.getContextPath() %>/liste/manage?id=<%= listeId %>" class="btn btn-secondary">
            Retour à la liste
        </a>
    </div>

</div>

</body>
</html>
