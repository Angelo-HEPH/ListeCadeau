<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="be.couderiannello.models.Cadeau" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Modifier un cadeau</title>
</head>
<body class="bg-light">

    <jsp:include page="/WEB-INF/view/includes/header.jsp" />

    <div class="container my-5">

        <%
            Cadeau cadeau = (Cadeau) request.getAttribute("cadeau");
            int listeId = (Integer) request.getAttribute("listeId");
            String error = (String) request.getAttribute("error");
        %>

        <% if (error != null && !error.isBlank()) { %>
            <div class="alert alert-danger text-center fw-bold">
                <%= error %>
            </div>
        <% } %>

        <% if (cadeau == null) { return; } %>

        <div class="bg-light shadow-lg rounded-4 p-5 mx-auto" style="max-width:600px;">

            <h2 class="mb-4 fw-bold">🎁 Modifier le cadeau</h2>
            <p class="text-muted mb-4">
                ✨ <b><%= cadeau.getName() %></b> | 💶 Prix : <b><%= cadeau.getPrice() %> €</b>
            </p>

            <form method="post" action="<%= request.getContextPath() %>/cadeau/edit">

                <input type="hidden" name="id" value="<%= cadeau.getId() %>">
                <input type="hidden" name="listeId" value="<%= listeId %>">

                <div class="mb-3">
                    <label class="form-label fw-semibold">📝 Nom du cadeau</label>
                    <input type="text" name="name" class="form-control form-control-lg"
                           value="<%= cadeau.getName() %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">🖊 Description</label>
                    <textarea name="description" class="form-control form-control-lg" required>
                        <%= cadeau.getDescription() %>
                    </textarea>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">💶 Prix (€)</label>
                    <input type="number" step="0.01" name="price" class="form-control form-control-lg"
                           value="<%= cadeau.getPrice() %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">🔗 Lien produit</label>
                    <input type="url" name="linkSite" class="form-control form-control-lg"
                           value="<%= cadeau.getLinkSite() %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">🖼 Photo (URL)</label>
                    <input type="url" name="photo" class="form-control form-control-lg"
                           value="<%= cadeau.getPhoto() %>" required>
                </div>

                <div class="mb-4">
                    <label class="form-label fw-semibold">🎯 Priorité</label>
                    <select name="priorite" class="form-select form-select-lg" required>
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

                <div class="d-flex justify-content-between">
                    <button type="submit" class="btn btn-primary fw-bold btn-lg">
                        💾 Enregistrer
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
