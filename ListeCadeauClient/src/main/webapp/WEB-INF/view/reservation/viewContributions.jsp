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
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

<%
    DecimalFormat df = new DecimalFormat("0.00");
    List<Reservation> contributions = (List<Reservation>) request.getAttribute("contributions");
%>

<h2 class="mb-3">💶 Mes contributions</h2>

<%
    if (contributions == null || contributions.isEmpty()) {
%>
    <div class="alert alert-info">
        Vous n'avez encore fait aucune contribution.
    </div>
<%
    } else {
%>

<div class="row">
<%
        for (Reservation r : contributions) {
            Cadeau c = r.getCadeau();
            String cadeauNom = (c != null && c.getName() != null) ? c.getName() : "Cadeau";
            String photo = (c != null && c.getPhoto() != null) ? c.getPhoto() : "";
            double montant = r.getAmount();

            ListeCadeau liste = (c != null) ? c.getListeCadeau() : null;
%>

    <div class="col-md-4 mb-4">
        <div class="card shadow-sm h-100">

            <% if (photo != null && !photo.isBlank()) { %>
                <img src="<%= photo %>" class="card-img-top"
                     alt="photo cadeau" style="height:180px; object-fit:cover;">
            <% } %>

            <div class="card-body d-flex flex-column">

                <div class="d-flex justify-content-between align-items-start mb-2">
                    <h5 class="card-title mb-0"><%= cadeauNom %></h5>
                    <span class="badge bg-success"><%= df.format(montant) %> €</span>
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
				      onsubmit="return confirm('Supprimer cette contribution ?');">
				
				    <input type="hidden" name="reservationId" value="<%= r.getId() %>">
				
				    <button type="submit"
				            class="btn btn-danger btn-sm mt-auto w-100">
				        ❌ Supprimer la contribution
				    </button>
				</form>
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
    <a href="<%= request.getContextPath() %>/home" class="btn btn-secondary">
        Retour
    </a>
</div>

</div>
</body>
</html>
