<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ajouter un cadeau</title>
</head>
<body class="bg-light">

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container my-5">

<%
    String error = (String) request.getAttribute("error");
    int listeId = Integer.parseInt(request.getAttribute("listeId").toString());

    String name = (String) request.getAttribute("name");
    String description = (String) request.getAttribute("description");
    String price = (String) request.getAttribute("price");
    String photo = (String) request.getAttribute("photo");
    String linkSite = (String) request.getAttribute("linkSite");
    String priorite = (String) request.getAttribute("priorite");
%>

<% if (error != null && !error.trim().isEmpty()) { %>
    <div class="alert alert-danger text-center fw-bold">
        <%= error %>
    </div>
<% } %>

<div class="bg-light shadow-lg rounded-4 p-5 mx-auto" style="max-width:600px;">

    <h2 class="mb-4 fw-bold">➕ Ajouter un cadeau</h2>
    <p class="text-muted mb-4">
        🎁 Remplissez les informations du cadeau que vous souhaitez ajouter
    </p>

    <form action="<%= request.getContextPath() %>/cadeau/create" method="post">

        <input type="hidden" name="listeId" value="<%= listeId %>">

        <div class="mb-3">
            <label class="form-label fw-semibold">📝 Nom du cadeau</label>
            <input type="text" name="name" class="form-control form-control-lg"
                   value="<%= (name != null ? name : "") %>" required>
        </div>

        <div class="mb-3">
            <label class="form-label fw-semibold">🖊 Description</label>
            <textarea name="description" class="form-control form-control-lg" required><%= (description != null ? description : "") %></textarea>
        </div>

        <div class="mb-3">
            <label class="form-label fw-semibold">💶 Prix (€)</label>
            <input type="number" step="0.01" name="price" class="form-control form-control-lg"
                   value="<%= (price != null ? price : "") %>" required>
        </div>

        <div class="mb-3">
            <label class="form-label fw-semibold">🖼 Photo (URL)</label>
            <input type="url" name="photo" class="form-control form-control-lg"
                   value="<%= (photo != null ? photo : "") %>" required>
        </div>

        <div class="mb-3">
            <label class="form-label fw-semibold">🔗 Lien du site</label>
            <input type="url" name="linkSite" class="form-control form-control-lg"
                   value="<%= (linkSite != null ? linkSite : "") %>" required>
        </div>

        <div class="mb-4">
            <label class="form-label fw-semibold">🎯 Priorité</label>
            <select name="priorite" class="form-select form-select-lg" required>
                <option value="GRANDE" <%= "GRANDE".equals(priorite) ? "selected" : "" %>>GRANDE</option>
                <option value="MOYENNE" <%= "MOYENNE".equals(priorite) ? "selected" : "" %>>MOYENNE</option>
                <option value="FAIBLE" <%= "FAIBLE".equals(priorite) ? "selected" : "" %>>FAIBLE</option>
            </select>
        </div>

        <div class="d-flex justify-content-between">
            <button type="submit" class="btn btn-primary fw-bold btn-lg">
                💾 Ajouter
            </button>

            <a href="<%= request.getContextPath() %>/liste/manage?id=<%= listeId %>"
               class="btn btn-secondary fw-bold btn-lg">
                ❌ Annuler
            </a>
        </div>

    </form>
</div>

</div>
</body>
</html>
