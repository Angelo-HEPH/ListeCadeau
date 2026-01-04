<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="be.couderiannello.models.ListeCadeau" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Modifier la liste</title>
</head>
<body class="bg-light">

    <jsp:include page="/WEB-INF/view/includes/header.jsp" />

    <div class="container my-5">

        <%
            ListeCadeau liste = (ListeCadeau) request.getAttribute("liste");
            String error = (String) request.getAttribute("error");
        %>

        <% if (error != null && !error.isBlank()) { %>
            <div class="alert alert-danger text-center fw-bold">
                <%= error %>
            </div>
        <% } %>

        <% if (liste == null) { return; } %>

        <div class="bg-light shadow-lg rounded-4 p-5 mx-auto" style="max-width:600px;">

            <h2 class="mb-4 fw-bold">✏️ Modifier la liste</h2>
            <p class="text-muted mb-4">
                🎉 <b><%= liste.getEvenement() %></b> | 📅 Date d’expiration : <b><%= liste.getExpirationDate() %></b>
            </p>

            <form method="post" action="<%= request.getContextPath() %>/liste/edit">

                <input type="hidden" name="id" value="<%= liste.getId() %>">

                <div class="mb-3">
                    <label class="form-label fw-semibold">📝 Titre</label>
                    <input type="text" name="title" class="form-control form-control-lg"
                           value="<%= liste.getTitle() %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">🎉 Événement</label>
                    <input type="text" name="evenement" class="form-control form-control-lg"
                           value="<%= liste.getEvenement() %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">📅 Date d’expiration</label>
                    <input type="date" name="expirationDate" class="form-control form-control-lg"
                           value="<%= liste.getExpirationDate() %>">
                </div>

                <div class="form-check mb-4">
                    <input type="checkbox" class="form-check-input" name="statut" <%= liste.isStatut() ? "checked" : "" %>>
                    <label class="form-check-label fw-semibold"> Liste active</label>
                </div>

                <div class="d-flex justify-content-between">
                    <button type="submit" class="btn btn-primary fw-bold btn-lg">
                        💾 Enregistrer
                    </button>

                    <a href="<%= request.getContextPath() %>/liste/manage?id=<%= liste.getId() %>"
                       class="btn btn-secondary fw-bold btn-lg">
                        ❌ Annuler
                    </a>
                </div>

            </form>
        </div>

    </div>
</body>
</html>
