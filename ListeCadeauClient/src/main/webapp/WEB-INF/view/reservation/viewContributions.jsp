<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.text.DecimalFormat,
                 be.couderiannello.models.Reservation,
                 be.couderiannello.models.Cadeau,
                 be.couderiannello.models.ListeCadeau" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mes contributions</title>
</head>
<body class="bg-light">

    <jsp:include page="/WEB-INF/view/includes/header.jsp" />

    <div class="container my-5">

        <%
            DecimalFormat df = new DecimalFormat("0.00");
            List<Reservation> contributions = (List<Reservation>) request.getAttribute("contributions");
            String error = (String) request.getAttribute("error");
        %>

        <h2 class="mb-4 fw-bold">💶 Mes contributions</h2>

        <% if (error != null && !error.isBlank()) { %>
            <div class="alert alert-danger text-center fw-bold mb-4">
                <%= error %>
            </div>
        <% } %>

        <% if (contributions == null || contributions.isEmpty()) { %>
            <div class="alert alert-info text-center">
                Vous n'avez encore fait aucune contribution.
            </div>
        <% } else { %>

            <div class="row g-4">
                <%
                    for (Reservation r : contributions) {
                        Cadeau c = r.getCadeau();
                        String cadeauNom = (c != null && c.getName() != null) ? c.getName() : "Cadeau";
                        String photo = (c != null && c.getPhoto() != null) ? c.getPhoto() : "";
                        double montant = r.getAmount();
                        ListeCadeau liste = (c != null) ? c.getListeCadeau() : null;
                %>

                    <div class="col-md-4">
                        <div class="card h-100 shadow-sm rounded-4">

                            <% if (photo != null && !photo.isBlank()) { %>
                                <img src="<%= photo %>" class="card-img-top rounded-top"
                                     alt="photo cadeau" style="height:180px; object-fit:cover;">
                            <% } %>

                            <div class="card-body d-flex flex-column">

                                <div class="d-flex justify-content-between align-items-start mb-2">
                                    <h5 class="card-title fw-bold mb-0"><%= cadeauNom %></h5>
                                    <span class="badge bg-success fs-6"><%= df.format(montant) %> €</span>
                                </div>

                                <p class="text-muted small mb-2">
                                    <b>Date :</b> <%= r.getDateReservation() %>
                                </p>

                                <% if (c != null && c.getPrice() > 0) { %>
                                    <p class="text-muted small mb-3">
                                        <b>Prix du cadeau :</b> <%= df.format(c.getPrice()) %> €
                                    </p>
                                <% } %>

                                <form method="post"
                                      action="<%= request.getContextPath() %>/reservation/delete"
                                      onsubmit="return confirm('Supprimer cette contribution ?');"
                                      class="mt-auto">
                                    <input type="hidden" name="reservationId" value="<%= r.getId() %>">
                                    <button type="submit" class="btn btn-danger btn-sm w-100 fw-bold">
                                        ❌ Supprimer
                                    </button>
                                </form>

                            </div>
                        </div>
                    </div>

                <%
                    }
                %>
            </div>

        <% } %>

        <div class="mt-5 text-center">
            <a href="<%= request.getContextPath() %>/home" class="btn btn-secondary btn-lg fw-bold">
                ← Retour à l'accueil
            </a>
        </div>

    </div>
</body>
</html>
