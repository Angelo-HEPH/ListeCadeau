<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, be.couderiannello.models.ListeCadeau, be.couderiannello.models.Cadeau, be.couderiannello.models.Personne" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Consultation liste</title>
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

        <% if (liste == null) { %>
            <div class="alert alert-danger text-center fw-bold mt-4">
                <%= request.getAttribute("error") %>
            </div>
        <% } else { %>

            <div class="bg-white shadow-lg rounded-4 p-4 mb-4">
                <h2 class="fw-bold mb-2">📋 Liste : <%= liste.getTitle() %></h2>
                <p class="text-muted mb-0">
                    🎉 Événement : <b><%= liste.getEvenement() %></b><br>
                    📅 Créée le : <%= liste.getCreationDate() %> | Expiration : <%= liste.getExpirationDate() %>
                </p>
            </div>

            <h3 class="mb-3 fw-bold">🎁 Cadeaux</h3>

            <%
                List<Cadeau> cadeaux = liste.getCadeaux();
                if (cadeaux == null || cadeaux.isEmpty()) {
            %>
                <div class="alert alert-info text-center">
                    Aucun cadeau dans cette liste.
                </div>
            <%
                } else {
            %>

                <div class="row g-4">
                    <%
                        for (Cadeau c : cadeaux) {
                            double prix = c.getPrice();
                            double total = c.getTotalContributed();
                            double restant = c.getRemainingAmount();

                            int percent = 0;
                            if (prix > 0) {
                                percent = (int) Math.round((total / prix) * 100.0);
                                percent = Math.min(100, Math.max(0, percent));
                            }
                    %>
                        <div class="col-md-4">
                            <div class="card shadow-sm rounded-4 h-100">

                                <img src="<%= c.getPhoto() %>" class="card-img-top" alt="photo cadeau"
                                     style="height:180px; object-fit:cover;">

                                <div class="card-body d-flex flex-column">

                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <h5 class="card-title mb-0 fw-bold"><%= c.getName() %></h5>
                                        <span class="badge bg-dark"><%= prix %> €</span>
                                    </div>

                                    <p class="text-muted small mb-2">
                                        <b>Priorité :</b> <%= c.getPriorite() %>
                                    </p>

                                    <div class="mb-2">
                                        <div class="progress" style="height: 12px;">
                                            <div class="progress-bar bg-success" style="width: <%= percent %>%;">
                                            </div>
                                        </div>
                                        <div class="d-flex justify-content-between small mt-1">
                                            <span>Déjà offert : <%= total %> €</span>
                                            <span><%= percent %>%</span>
                                        </div>
                                        <div class="mt-1 fw-bold">Reste : <span class="fs-5"><%= restant %> €</span></div>
                                    </div>

                                    <p class="card-text small text-muted mb-3" style="flex:1;">
                                        <%= c.getDescription() %>
                                    </p>

                                    <% if (c.isFullyReserved() || restant <= 0) { %>
                                        <div class="alert alert-secondary py-2 text-center mb-2">
                                            🎁 <b>Déjà offert</b>
                                        </div>
                                    <% } else { %>
                                        <div class="border rounded p-2 mb-2">
                                            <div class="fw-bold mb-2">Offrir ce cadeau</div>

                                            <form method="post" action="<%= request.getContextPath() %>/reservation/contribuer">
                                                <input type="hidden" name="cadeauId" value="<%= c.getId() %>">
                                                <input type="hidden" name="listeId" value="<%= liste.getId() %>">

                                                <div class="input-group input-group-sm">
                                                    <input type="number" name="amount" step="0.01" min="0.01"
                                                           max="<%= restant %>" class="form-control"
                                                           placeholder="Montant (max <%= restant %> €)" required>
                                                    <span class="input-group-text">€</span>
                                                </div>

                                                <button type="submit" class="btn btn-success btn-sm w-100 mt-2 fw-bold">
                                                    Offrir
                                                </button>
                                            </form>
                                        </div>
                                    <% } %>

                                    <a href="<%= c.getLinkSite() %>" target="_blank"
                                       class="btn btn-outline-primary btn-sm w-100 mt-auto fw-bold">
                                        Voir le produit
                                    </a>

                                </div>
                            </div>
                        </div>
                    <%
                        }
                    %>
                </div>

            <%
                }
            %>

            <div class="mt-4 text-center">
                <a class="btn btn-secondary btn-lg fw-bold" href="<%= request.getContextPath() %>/home">
                    ← Retour à l’accueil
                </a>
            </div>

        <% } %>

    </div>
</body>
</html>
