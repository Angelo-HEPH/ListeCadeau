<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.text.DecimalFormat,
                 be.couderiannello.models.ListeCadeau,
                 be.couderiannello.models.Cadeau,
                 be.couderiannello.models.Personne" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Consultation liste</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

<%
    DecimalFormat df = new DecimalFormat("0.00");

    ListeCadeau liste = (ListeCadeau) request.getAttribute("liste");
    Personne creator = liste.getCreator();
%>

<h2 class="mb-3">Liste : <%= liste.getTitle() %></h2>

<div class="mb-4">
    <p><b>Événement :</b> <%= liste.getEvenement() %></p>
    <p><b>Créée par :</b> ${creatorLabel}</p>
    <p><b>Date de création :</b> <%= liste.getCreationDate() %></p>
    <p><b>Expiration :</b> <%= liste.getExpirationDate() %></p>
</div>

<hr>

<h3 class="mb-3">Cadeaux</h3>

<%
    List<Cadeau> cadeaux = liste.getCadeaux();
    if (cadeaux == null || cadeaux.isEmpty()) {
%>
    <p class="text-muted">Aucun cadeau dans cette liste.</p>
<%
    } else {
%>

<div class="row">
<%
        for (Cadeau c : cadeaux) {

            double prix = c.getPrice();
            double total = c.totalContribue();
            double restant = c.restantAContribuer();

            int percent = 0;
            if (prix > 0) {
                percent = (int) Math.round((total / prix) * 100.0);
                percent = Math.max(0, Math.min(100, percent));
            }
%>
    <div class="col-md-4 mb-4">
        <div class="card shadow-sm h-100">

            <img src="<%= c.getPhoto() %>" class="card-img-top"
                 alt="photo cadeau" style="height:180px; object-fit:cover;">

            <div class="card-body d-flex flex-column">

                <div class="d-flex justify-content-between align-items-start mb-2">
                    <h5 class="card-title mb-0"><%= c.getName() %></h5>
                    <span class="badge bg-dark"><%= df.format(prix) %> €</span>
                </div>

                <p class="text-muted small mb-2">
                    <b>Priorité :</b> <%= c.getPriorite() %>
                </p>

                <!-- Progression -->
                <div class="mb-2">
                    <div class="progress" style="height: 10px;">
                        <div class="progress-bar" role="progressbar"
                             style="width: <%= percent %>%;"
                             aria-valuenow="<%= percent %>"
                             aria-valuemin="0" aria-valuemax="100"></div>
                    </div>

                    <div class="d-flex justify-content-between small mt-1">
                        <span><b>Déjà offert :</b> <%= df.format(total) %> €</span>
                        <span><b><%= percent %>%</b></span>
                    </div>

                    <div class="mt-2">
                        <span class="fw-bold">Reste :</span>
                        <span class="fw-bold fs-5"><%= df.format(restant) %> €</span>
                    </div>
                </div>

                <p class="card-text small text-muted mb-3" style="flex: 1;">
                    <%= c.getDescription() %>
                </p>

                <!-- Actions -->
                <% if (c.estReserve()) { %>
                    <div class="alert alert-secondary py-2 mb-2 text-center">
                        🎁 <b>Déjà offert</b>
                    </div>
                <% } else { %>
                    <div class="border rounded p-2 mb-2">
                        <div class="fw-bold mb-2">Offrir ce cadeau</div>

                        <form method="post"
                              action="<%= request.getContextPath() %>/reservation/contribuer">

                            <input type="hidden" name="cadeauId" value="<%= c.getId() %>">
                            <input type="hidden" name="listeId" value="<%= liste.getId() %>">

                            <div class="input-group input-group-sm">
                                <input type="number"
                                       name="amount"
                                       step="0.01"
                                       min="0.01"
                                       max="<%= restant %>"
                                       class="form-control"
                                       placeholder="Montant (max <%= df.format(restant) %> €)"
                                       required>
                                <span class="input-group-text">€</span>
                            </div>

                            <button type="submit" class="btn btn-success btn-sm w-100 mt-2">
                                Offrir
                            </button>
                        </form>
                    </div>
                <% } %>

                <a href="<%= c.getLinkSite() %>" target="_blank"
                   class="btn btn-outline-primary btn-sm w-100 mt-auto">
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

<div class="mt-4">
    <a href="<%= request.getContextPath() %>/liste/invitations" class="btn btn-secondary">
        Retour aux invitations
    </a>
</div>

</div>
</body>
</html>
